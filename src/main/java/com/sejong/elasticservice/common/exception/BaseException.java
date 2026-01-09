package com.sejong.elasticservice.common.exception;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public class BaseException extends RuntimeException {

    private final ExceptionType exceptionType;

    public ExceptionType exceptionType() {
        return exceptionType;
    }
    
    @Override
    public String getMessage() {
        return exceptionType.description();
    }
}