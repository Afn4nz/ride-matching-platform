package com.ridematch.driver.excpetion;

import com.ridematch.driver.enums.ErrorCode;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class UploadException extends RuntimeException {
    private ErrorCode code;
}
