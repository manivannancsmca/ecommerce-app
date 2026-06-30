package com.product_service.event;

import lombok.Data;
import java.math.BigDecimal;

@Data
public class PaymentProcessedEvent {
    private Long orderId;
    private Long productId;
    private Integer quantity;
    private BigDecimal amount;
}
