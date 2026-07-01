package com.order_service.exception;

import feign.Response;
import feign.codec.ErrorDecoder;
import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;

public class CustomFeignErrorDecoder implements ErrorDecoder {

    @Override
    public Exception decode(String methodKey, Response response) {
        if (response.status() == HttpStatus.NOT_FOUND.value()) {
            return new ResponseStatusException(HttpStatus.NOT_FOUND, "Product not found.");
        }
        return new ResponseStatusException(
            HttpStatus.valueOf(response.status()), 
            "Product Service communication failed"
        );
    }
}