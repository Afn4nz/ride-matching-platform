package com.ridematch.driver.dto;

import com.fasterxml.jackson.annotation.JsonInclude;

import lombok.Data;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.io.Serializable;
import java.util.Collections;
import java.util.List;

@Data
@JsonInclude(JsonInclude.Include.NON_EMPTY)
public class ApiResponse<T> implements Serializable {
    boolean success;
    T data;
    List<ErrorResponse> errors;
    String message;

    public static <T> ResponseEntity<ApiResponse<T>> getSuccessResponse(String message) {
        return getResponse(true, null, Collections.emptyList(), HttpStatus.OK, message);
    }

    public static <T> ResponseEntity<ApiResponse<T>> getSuccessResponse(T data) {
        return getResponse(true, data, Collections.emptyList(), HttpStatus.OK, null);
    }

    public static <T> ResponseEntity<Object> getFailureResponse(
            List<ErrorResponse> errors, HttpStatus httpStatus) {
        ApiResponse<Object> response = new ApiResponse<>();
        response.setSuccess(false);
        response.setErrors(errors);

        return new ResponseEntity<>(response, httpStatus);
    }

    private static <T> ResponseEntity<ApiResponse<T>> getResponse(
            boolean success,
            T data,
            List<ErrorResponse> errors,
            HttpStatus httpStatus,
            String message) {
        ApiResponse<T> response = new ApiResponse<>();
        response.setSuccess(success);
        response.setData(data);
        response.setErrors(errors);
        response.setMessage(message);

        return new ResponseEntity<>(response, httpStatus);
    }
}
