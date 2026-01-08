package com.sejong.elasticservice.common.exception;

import lombok.AllArgsConstructor;

@AllArgsConstructor
public enum ExceptionType implements ExceptionTypeIfs {

    // 500 Internal Server Error
    EXTERNAL_SERVICE_ERROR(503, "외부 서비스 연결 실패"),
    ;

    private final Integer httpStatus;
    private final String description;

    @Override
    public Integer httpStatus() {
        return httpStatus;
    }

    @Override
    public String description() {
        return description;
    }
}