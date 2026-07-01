package com.order_service.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import com.order_service.client.ProductClient;
import com.order_service.dto.OrderRequest;
import com.order_service.dto.OrderResponse;
import com.order_service.dto.ProductResponse;
import com.order_service.dto.StandardResponse;
import com.order_service.entity.Order;
import com.order_service.event.OrderPlacedEvent;
import com.order_service.exception.ResourceNotFoundException;
import com.order_service.repository.OrderRepository;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
@Slf4j
public class OrderService {

    private final OrderRepository orderRepository;
    private final KafkaTemplate<String, Object> kafkaTemplate;
    private final ProductClient productClient; // Injecting Feign Client

    @Transactional
    public OrderResponse createOrder(OrderRequest request) {
        log.info("Validating product existence for ID: {}", request.getProductId());
        
        // 1. Fetch details via Feign Client
        ResponseEntity<StandardResponse<ProductResponse>> feignResponse = 
                productClient.getProductById(request.getProductId());
        
        // 2. Validate Response Structure
        if (feignResponse.getBody() == null || !feignResponse.getBody().isSuccess() || feignResponse.getBody().getData() == null) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Product details are invalid or missing.");
        }
        
        ProductResponse productDetails = feignResponse.getBody().getData();
        log.info("productDetails : {} ", productDetails);

        // 3. Calculate dynamic pricing using the actual product price
        BigDecimal totalAmount = productDetails.getPrice().multiply(BigDecimal.valueOf(request.getQuantity()));

        // 4. Save Order
        Order order = Order.builder()
                .productId(request.getProductId())
                .quantity(request.getQuantity())
                .totalAmount(totalAmount)
                .orderStatus("PENDING")
                .createdAt(LocalDateTime.now())
                .build();

        Order savedOrder = orderRepository.save(order);
        log.info("Order successfully created with ID: {}", savedOrder.getId());

        // 5. Emit asynchronous event to Kafka broker
        OrderPlacedEvent event = OrderPlacedEvent.builder()
                .orderId(savedOrder.getId())
                .productId(savedOrder.getProductId())
                .quantity(savedOrder.getQuantity())
                .totalAmount(savedOrder.getTotalAmount())
                .build();
        log.info("OrderPlacedEvent : {} ", event);
        //kafkaTemplate.send("order-placed-topic", String.valueOf(savedOrder.getId()), event);
        
        return mapToResponse(savedOrder);
    }

    @Transactional(readOnly = true)
    public OrderResponse getOrderById(Long id) {
        Order order = orderRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Order records not located with tracking ID: " + id));
        return mapToResponse(order);
    }

    private OrderResponse mapToResponse(Order order) {
        return OrderResponse.builder()
                .id(order.getId())
                .productId(order.getProductId())
                .quantity(order.getQuantity())
                .totalAmount(order.getTotalAmount())
                .orderStatus(order.getOrderStatus())
                .createdAt(order.getCreatedAt())
                .build();
    }
}