package com.product_service.event;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
@AllArgsConstructor
public class InventoryFailedEvent {
    private Long orderId;
    private String reason;
}
