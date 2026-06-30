package com.order_service.listener;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import com.order_service.event.InventoryAllocatedEvent;
import com.order_service.event.InventoryFailedEvent;
import com.order_service.event.PaymentFailedEvent;
import com.order_service.repository.OrderRepository;

@Component
@Slf4j
@RequiredArgsConstructor
public class OrderSagaListener {

    private final OrderRepository orderRepository;

    @KafkaListener(topics = "inventory-allocated-topic", groupId = "order-group")
    @Transactional
    public void handleSagaComplete(InventoryAllocatedEvent event) {
        log.info("Saga Success. Confirming Order ID: {}", event.getOrderId());
        orderRepository.findById(event.getOrderId()).ifPresent(order -> {
            order.setOrderStatus("CONFIRMED");
            orderRepository.save(order);
        });
    }

    @KafkaListener(topics = {"payment-failed-topic", "inventory-failed-topic"}, groupId = "order-group")
    @Transactional
    public void handleSagaRollback(String messagePayload) {
        // Handle fallback parsing generically based on tracking identifiers
        log.warn("Saga Compensation Triggered. Rolling back Order lifecycle states...");
    }
    
    @KafkaListener(topics = "payment-failed-topic", groupId = "order-group")
    @Transactional
    public void handlePaymentFailed(PaymentFailedEvent event) {
        log.warn("Saga Rollback: Payment failed for Order ID: {}. Cancelling order.", event.getOrderId());
        orderRepository.findById(event.getOrderId()).ifPresent(order -> {
            order.setOrderStatus("CANCELLED_PAYMENT_REJECTED");
            orderRepository.save(order);
        });
    }

    @KafkaListener(topics = "inventory-failed-topic", groupId = "order-group")
    @Transactional
    public void handleInventoryFailed(InventoryFailedEvent event) {
        log.warn("Saga Rollback: Inventory stock short for Order ID: {}. Cancelling order.", event.getOrderId());
        orderRepository.findById(event.getOrderId()).ifPresent(order -> {
            order.setOrderStatus("CANCELLED_OUT_OF_STOCK");
            orderRepository.save(order);
            // In a complete workflow, a separate event would also reverse payment here
        });
    }
}
