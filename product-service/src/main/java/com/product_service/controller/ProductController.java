package com.product_service.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j; // 1. Import Slf4j
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.product_service.dto.ProductRequest;
import com.product_service.dto.ProductResponse;
import com.product_service.dto.StandardResponse;
import com.product_service.service.ProductService;

import java.time.LocalDateTime;

@RestController
@RequestMapping("/api/v1/products")
@RequiredArgsConstructor
@Slf4j // 2. Add Lombok's Logging Annotation
public class ProductController {

    private final ProductService productService;

    @PostMapping
    public ResponseEntity<StandardResponse<ProductResponse>> createProduct(@Valid @RequestBody ProductRequest request) {
        log.info("Received request to create a product: {}", request.getName()); // Explicit Log
        
        ProductResponse data = productService.createProduct(request);
        
        StandardResponse<ProductResponse> response = StandardResponse.<ProductResponse>builder()
                .timestamp(LocalDateTime.now())
                .status(HttpStatus.CREATED.value())
                .success(true)
                .message("Product created successfully")
                .data(data)
                .build();
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @GetMapping("/{id}")
    public ResponseEntity<StandardResponse<ProductResponse>> getProductById(@PathVariable Long id) {
        log.info("Received request to fetch product with ID: {}", id); // Explicit Log
        
        ProductResponse data = productService.getProductById(id);
        
        StandardResponse<ProductResponse> response = StandardResponse.<ProductResponse>builder()
                .timestamp(LocalDateTime.now())
                .status(HttpStatus.OK.value())
                .success(true)
                .message("Product retrieved successfully")
                .data(data)
                .build();
        return ResponseEntity.ok(response);
    }

    @GetMapping
    public String testProduct() {
        log.info("Test endpoint called"); // Explicit Log
        return "test product";
    }
}