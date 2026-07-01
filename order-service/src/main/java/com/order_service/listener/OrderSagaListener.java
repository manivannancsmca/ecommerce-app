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

    // FIX: Changed String to Object to safely absorb deserialized event DTOs without casting errors
    @KafkaListener(topics = {"payment-failed-topic", "inventory-failed-topic"}, groupId = "order-group")
    @Transactional
    public void handleSagaRollback(Object messagePayload) {
        log.warn("Saga Compensation Triggered via general tracker: {}", messagePayload.getClass().getSimpleName());
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
        });
    }
}