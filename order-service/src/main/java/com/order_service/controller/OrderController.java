package com.order_service.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.order_service.dto.OrderRequest;
import com.order_service.dto.OrderResponse;
import com.order_service.dto.StandardResponse;
import com.order_service.service.OrderService;

import java.time.LocalDateTime;

@RestController
@RequestMapping("/api/v1/orders")
@RequiredArgsConstructor
public class OrderController {

    private final OrderService orderService;

    @PostMapping
    public ResponseEntity<StandardResponse<OrderResponse>> createOrder(@Valid @RequestBody OrderRequest request) {
        OrderResponse data = orderService.createOrder(request);
        StandardResponse<OrderResponse> response = StandardResponse.<OrderResponse>builder()
                .timestamp(LocalDateTime.now().toString())
                .status(HttpStatus.CREATED.value())
                .success(true)
                .message("Order created successfully")
                .data(data)
                .build();
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @GetMapping("/{id}")
    public ResponseEntity<StandardResponse<OrderResponse>> getOrderById(@PathVariable Long id) {
        OrderResponse data = orderService.getOrderById(id);
        StandardResponse<OrderResponse> response = StandardResponse.<OrderResponse>builder()
                .timestamp(LocalDateTime.now().toString())
                .status(HttpStatus.OK.value())
                .success(true)
                .message("Order data retrieved successfully")
                .data(data)
                .build();
        return ResponseEntity.ok(response);
    }
}