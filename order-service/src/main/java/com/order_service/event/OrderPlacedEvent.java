package com.order_service.event;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.math.BigDecimal;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class OrderPlacedEvent implements Serializable {
    private static final long serialVersionUID = 1L;
    private Long orderId;
    private Long productId;
    private Integer quantity;
    private BigDecimal totalAmount;
}
