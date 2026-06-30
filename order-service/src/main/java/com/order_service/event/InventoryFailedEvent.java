package com.order_service.event;

import lombok.Data;

@Data
public class InventoryFailedEvent {
    private Long orderId;
    private String reason;
}
