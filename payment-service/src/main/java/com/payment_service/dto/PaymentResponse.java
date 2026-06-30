package com.payment_service.dto;

import lombok.Builder;
import lombok.Data;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@Builder
public class PaymentResponse {
    private Long id;
    private Long orderId;
    private BigDecimal amount;
    private String paymentMode;
    private String paymentStatus;
    private String transactionId;
    private LocalDateTime paymentDate;
}
