package com.order_service.event;

import lombok.Data;

@Data
public class InventoryAllocatedEvent {
    private Long orderId;
}
