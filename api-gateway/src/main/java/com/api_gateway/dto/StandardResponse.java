package com.api_gateway.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class StandardResponse<T> {
    //private String timestamp;
    private LocalDateTime timestamp;
    private int status;
    private boolean success;
    private String message;
    private T data;
    private List<String> errors;
}
