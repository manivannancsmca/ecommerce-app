package com.payment_service.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.payment_service.dto.PaymentRequest;
import com.payment_service.dto.PaymentResponse;
import com.payment_service.dto.StandardResponse;
import com.payment_service.service.PaymentService;

import java.time.LocalDateTime;

@RestController
@RequestMapping("/api/v1/payments")
@RequiredArgsConstructor
public class PaymentController {

    private final PaymentService paymentService;

    @PostMapping
    public ResponseEntity<StandardResponse<PaymentResponse>> processPayment(
            @Valid @RequestBody PaymentRequest request) {
        PaymentResponse data = paymentService.processPayment(request);
        StandardResponse<PaymentResponse> response = StandardResponse.<PaymentResponse>builder()
                .timestamp(LocalDateTime.now().toString())
                .status(HttpStatus.CREATED.value())
                .success(true)
                .message("Payment completed successfully")
                .data(data)
                .build();
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @GetMapping("/order/{orderId}")
    public ResponseEntity<StandardResponse<PaymentResponse>> getPaymentByOrderId(@PathVariable Long orderId) {
        PaymentResponse data = paymentService.getPaymentByOrderId(orderId);
        StandardResponse<PaymentResponse> response = StandardResponse.<PaymentResponse>builder()
                .timestamp(LocalDateTime.now().toString())
                .status(HttpStatus.OK.value())
                .success(true)
                .message("Payment records retrieved safely")
                .data(data)
                .build();
        return ResponseEntity.ok(response);
    }
}
