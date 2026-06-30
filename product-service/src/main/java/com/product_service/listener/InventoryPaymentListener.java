package com.product_service.listener;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import com.product_service.entity.Product;
import com.product_service.event.InventoryAllocatedEvent;
import com.product_service.event.InventoryFailedEvent;
import com.product_service.event.PaymentProcessedEvent;
import com.product_service.repository.ProductRepository;

@Component
@Slf4j
@RequiredArgsConstructor
public class InventoryPaymentListener {

    private final ProductRepository productRepository;
    private final KafkaTemplate<String, Object> kafkaTemplate;



    @KafkaListener(topics = "payment-processed-topic", groupId = "product-group")
    @Transactional
    public void handlePaymentProcessed(PaymentProcessedEvent event) {
        log.info("Reserving stock for Product ID: {} (Qty: {})", event.getProductId(), event.getQuantity());

        Product product = productRepository.findById(event.getProductId()).orElse(null);

        if (product == null || product.getStock() < event.getQuantity()) {
            log.error("Stock allocation failure for Order ID: {}", event.getOrderId());
            
            InventoryFailedEvent failedEvent = InventoryFailedEvent.builder()
                    .orderId(event.getOrderId())
                    .reason("STOCK_OUT_OR_NOT_FOUND")
                    .build();
            
            kafkaTemplate.send("inventory-failed-topic", String.valueOf(event.getOrderId()), failedEvent);
            return;
        }

        // Deduct inventory
        product.setStock(product.getStock() - event.getQuantity());
        productRepository.save(product);

        InventoryAllocatedEvent successEvent = InventoryAllocatedEvent.builder()
                .orderId(event.getOrderId())
                .build();

        kafkaTemplate.send("inventory-allocated-topic", String.valueOf(event.getOrderId()), successEvent);
        log.info("Inventory successfully reserved for Order ID: {}", event.getOrderId());
    }
}
