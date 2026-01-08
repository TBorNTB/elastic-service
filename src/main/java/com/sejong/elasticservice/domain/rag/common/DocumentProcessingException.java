package com.sejong.elasticservice.domain.rag.common;

import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;

public class DocumentProcessingException extends ResponseStatusException {

    private static final long serialVersionUID = 1L;

    public DocumentProcessingException(String message) {
        super(HttpStatus.INTERNAL_SERVER_ERROR, message);
    }

    public DocumentProcessingException(String message, Throwable cause) {
        super(HttpStatus.INTERNAL_SERVER_ERROR, message, cause);
    }
}