package com.ridematch.driver.excpetion;

import com.ridematch.driver.dto.ApiResponse;
import com.ridematch.driver.dto.ErrorResponse;
import com.ridematch.driver.enums.ErrorCode;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.ConstraintViolationException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.support.DefaultMessageSourceResolvable;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@ControllerAdvice
public class ExceptionsHandler extends ResponseEntityExceptionHandler {

    @ExceptionHandler(Exception.class)
    protected final ResponseEntity<Object> handleGenericException(Exception exception) {
        log.error("Internal server error: {}", exception.getMessage(), exception);
        return ApiResponse.getFailureResponse(
                Collections.singletonList(ErrorCode.INTERNAL_SERVER.toResponse()),
                ErrorCode.INTERNAL_SERVER.getHttpStatus());
    }

    @ExceptionHandler(ConstraintViolationException.class)
    protected final ResponseEntity<Object> handleConstraintViolation(
            ConstraintViolationException exception) {
        List<ErrorResponse> errors =
                exception.getConstraintViolations().stream()
                        .map(this::toErrorResponseFromViolation)
                        .toList();

        log.error("handleConstraintViolation: {}", exception.getMessage(), exception);
        return ApiResponse.getFailureResponse(errors, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(UploadException.class)
    protected final ResponseEntity<Object> handleUploadException(UploadException exception) {
        return ApiResponse.getFailureResponse(
                Collections.singletonList(ErrorCode.INTERNAL_SERVER.toResponse()),
                ErrorCode.INTERNAL_SERVER.getHttpStatus());
    }

    @Override
    protected ResponseEntity<Object> handleMethodArgumentNotValid(
            MethodArgumentNotValidException exception,
            HttpHeaders headers,
            HttpStatusCode status,
            WebRequest request) {

        log.error("handleMethodArgumentNotValid: {}", exception.getMessage(), exception);

        List<ErrorResponse> errors =
                exception.getBindingResult().getAllErrors().stream()
                        .map(DefaultMessageSourceResolvable::getDefaultMessage)
                        .map(this::toErrorResponseFromKey)
                        .distinct()
                        .collect(Collectors.toList());

        return ApiResponse.getFailureResponse(errors, HttpStatus.BAD_REQUEST);
    }

    private ErrorResponse toErrorResponseFromViolation(ConstraintViolation<?> v) {
        return extractErrorCode(v).toResponse();
    }

    private ErrorResponse toErrorResponseFromKey(String key) {
        try {
            return ErrorCode.valueOf(key).toResponse();
        } catch (IllegalArgumentException ex) {
            return ErrorCode.VALIDATION_FAILED.toResponse();
        }
    }

    private ErrorCode extractErrorCode(ConstraintViolation<?> v) {
        Object attr = v.getConstraintDescriptor().getAttributes().get("error");
        if (attr instanceof ErrorCode ec) {
            return ec;
        }
        try {
            return ErrorCode.valueOf(v.getMessage());
        } catch (Exception ignored) {
            return ErrorCode.VALIDATION_FAILED;
        }
    }
}
