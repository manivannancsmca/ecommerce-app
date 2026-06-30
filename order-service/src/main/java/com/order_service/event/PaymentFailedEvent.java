package com.order_service.event;

import lombok.Data;

@Data
public class PaymentFailedEvent {
    private Long orderId;
    private String reason;
}