package com.ridematch.driver.enums;

import com.ridematch.driver.dto.ErrorResponse;
import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
@AllArgsConstructor
public enum ErrorCode {
    GENERIC_INVALID_ID_INPUT(2001, "Please enter a valid ID.", HttpStatus.BAD_REQUEST),
    INVALID_PHONE_FORMAT(2002, "The phone number should start with 0 and be 10 digits long.", HttpStatus.BAD_REQUEST),
    VALIDATION_FAILED(2003, "Validation failed. Please check your input.", HttpStatus.BAD_REQUEST),
    INTERNAL_SERVER(5000, "An unexpected error occurred. Please try again later.", HttpStatus.INTERNAL_SERVER_ERROR),

    LICENSE_MISSING(3000, "Driver license document is required.", HttpStatus.BAD_REQUEST),
    LICENSE_TOO_LARGE(3001, "Driver license document exceeds the maximum allowed size.", HttpStatus.PAYLOAD_TOO_LARGE),

    REGISTRATION_MISSING(3002, "Vehicle registration document is required.", HttpStatus.BAD_REQUEST),
    REGISTRATION_TOO_LARGE(3003, "Vehicle registration document exceeds the maximum allowed size.", HttpStatus.PAYLOAD_TOO_LARGE),

    VEHICLE_IMAGE_MISSING(3004, "At least one vehicle image is required.", HttpStatus.BAD_REQUEST),
    IMAGE_INVALID(3005, "Invalid or unreadable image file.", HttpStatus.UNPROCESSABLE_ENTITY),
    DIMENSIONS_TOO_LARGE(3006, "Image dimensions exceed the allowed size.", HttpStatus.UNPROCESSABLE_ENTITY),

    SIZE_LIMIT_EXCEEDED(3007, "File size exceeds the maximum allowed limit.", HttpStatus.PAYLOAD_TOO_LARGE),
    FILE_TYPE_BLOCKED(3008, "File type is not supported. Only PDF, JPEG, and PNG are allowed.", HttpStatus.UNSUPPORTED_MEDIA_TYPE),

    PDF_ENCRYPTED(3009, "PDF file is encrypted or password-protected.", HttpStatus.UNPROCESSABLE_ENTITY),
    PDF_EMPTY(3010, "PDF file has no pages.", HttpStatus.UNPROCESSABLE_ENTITY),

    FILE_UPLOAD_ERROR(3011, "Something went wrong", HttpStatus.UNPROCESSABLE_ENTITY);

    private final int code;
    private final String defaultMessage;
    private final HttpStatus httpStatus;

    public ErrorResponse toResponse() {
        return new ErrorResponse(String.valueOf(code), defaultMessage);
    }
}
