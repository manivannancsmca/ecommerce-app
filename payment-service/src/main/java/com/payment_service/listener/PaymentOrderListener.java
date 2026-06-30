package com.payment_service.listener;


import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

import com.payment_service.entity.Payment;
import com.payment_service.event.OrderPlacedEvent;
import com.payment_service.event.PaymentFailedEvent;
import com.payment_service.event.PaymentProcessedEvent;
import com.payment_service.repository.PaymentRepository;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

@Component
@Slf4j
@RequiredArgsConstructor
public class PaymentOrderListener {

    private final PaymentRepository paymentRepository;
    private final KafkaTemplate<String, Object> kafkaTemplate;

    @KafkaListener(topics = "order-placed-topic", groupId = "payment-group")
    public void handleOrderPlacedEvent(OrderPlacedEvent event) {
        log.info("Processing Payment for Order ID: {}", event.getOrderId());
        
        // Simulating a Business Rule Failure for Saga validation (Amounts > $5000 fail)
        if (event.getTotalAmount().compareTo(BigDecimal.valueOf(5000)) > 0) {
            log.warn("Payment rejected: Credit limit exceeded for Order ID: {}", event.getOrderId());
            
            PaymentFailedEvent failedEvent = PaymentFailedEvent.builder()
                    .orderId(event.getOrderId())
                    .reason("CREDIT_LIMIT_EXCEEDED")
                    .build();
            
            kafkaTemplate.send("payment-failed-topic", String.valueOf(event.getOrderId()), failedEvent);
            return;
        }

        Payment payment = Payment.builder()
                .orderId(event.getOrderId())
                .amount(event.getTotalAmount())
                .paymentMode("AUTOMATED_WALLET")
                .paymentStatus("SUCCESS")
                .transactionId(UUID.randomUUID().toString())
                .paymentDate(LocalDateTime.now())
                .build();

        paymentRepository.save(payment);

        PaymentProcessedEvent processedEvent = PaymentProcessedEvent.builder()
                .orderId(event.getOrderId())
                .productId(event.getProductId())
                .quantity(event.getQuantity())
                .amount(event.getTotalAmount())
                .build();

        kafkaTemplate.send("payment-processed-topic", String.valueOf(event.getOrderId()), processedEvent);
        log.info("Payment SUCCESS event sent downstream for Order ID: {}", event.getOrderId());
    
    }
}
