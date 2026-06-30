package com.payment_service.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.util.List;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class StandardResponse<T> {
    private String timestamp;
    private int status;
    private boolean success;
    private String message;
    private T data;
    private List<String> errors;
}
