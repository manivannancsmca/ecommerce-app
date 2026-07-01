package com.api_gateway.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.api_gateway.dto.StandardResponse;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/fallback")
public class FallbackController {

    private ResponseEntity<Map<String, Object>> createFallbackResponse(String message) {
        Map<String, Object> response = new HashMap<>();
        response.put("timestamp", LocalDateTime.now().toString());
        response.put("status", HttpStatus.SERVICE_UNAVAILABLE.value());
        response.put("success", false);
        response.put("message", message);
        response.put("data", null);
        response.put("errors", Collections.singletonList("Service is temporarily unreachable. Please try again later."));
        return ResponseEntity.status(HttpStatus.SERVICE_UNAVAILABLE).body(response);
    }

    @RequestMapping("/product-fallback")
    public ResponseEntity<StandardResponse<Object>> productFallback() {
        StandardResponse<Object> response = StandardResponse.builder()
                .timestamp(LocalDateTime.now())
                .status(HttpStatus.SERVICE_UNAVAILABLE.value())
                .success(false)
                .message("Product service is currently unavailable or timed out. Please try again later.")
                .build();
        return ResponseEntity.status(HttpStatus.SERVICE_UNAVAILABLE).body(response);
    }

    @RequestMapping("/order-fallback")
    public ResponseEntity<StandardResponse<Object>> orderFallback() {
        StandardResponse<Object> response = StandardResponse.builder()
                .timestamp(LocalDateTime.now())
                .status(HttpStatus.SERVICE_UNAVAILABLE.value())
                .success(false)
                .message("Order service is currently unavailable or timed out. Please try again later.")
                .build();
        return ResponseEntity.status(HttpStatus.SERVICE_UNAVAILABLE).body(response);
    }

    @GetMapping("/payment-fallback")
    public ResponseEntity<Map<String, Object>> paymentServiceFallback() {
        return createFallbackResponse("Payment Service is taking longer than expected to respond.");
    }

    @GetMapping("/product-docs-fallback")
    public String productDocsFallback() {
        return "{\"error\": \"Product service unavailable\"}";
    }
}
