package com.sejong.elasticservice.common.exception;

import feign.FeignException;
import io.swagger.v3.oas.annotations.Hidden;
import jakarta.servlet.http.HttpServletRequest;
import java.nio.file.AccessDeniedException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@Slf4j
@RestControllerAdvice
@Hidden
public class GlobalExceptionHandler {

    @ExceptionHandler(BaseException.class)
    public ResponseEntity<ExceptionResponse> handleException(HttpServletRequest request, BaseException e) {
        ExceptionType type = e.exceptionType();
        log.info("잘못된 요청이 들어왔습니다. URI: {}, 내용: {}", request.getRequestURI(), type.description());
        return ResponseEntity.status(type.httpStatus())
                .body(new ExceptionResponse(type.description()));
    }

    @ExceptionHandler(AccessDeniedException.class)
    public ResponseEntity<ExceptionResponse> handleAccessDeniedException(HttpServletRequest request, AccessDeniedException e) {
        log.warn("권한이 없는 요청입니다. URI: {}, 메시지: {}", request.getRequestURI(), e.getMessage());
        return ResponseEntity.status(HttpStatus.FORBIDDEN)
                .body(new ExceptionResponse("접근 권한이 없습니다. 관리자에게 문의하세요."));
    }

    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<ExceptionResponse> handleIllegalArgumentException(HttpServletRequest request, IllegalArgumentException e) {
        log.error("잘못된 인자 예외 발생. URI: {}, 메시지: {}", request.getRequestURI(), e.getMessage());
        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body(new ExceptionResponse(e.getMessage()));
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ExceptionResponse> handleValidationException(HttpServletRequest request, MethodArgumentNotValidException e) {
        StringBuilder message = new StringBuilder("입력값 검증 실패: ");
        for (FieldError error : e.getBindingResult().getFieldErrors()) {
            message.append(error.getField()).append(" - ").append(error.getDefaultMessage()).append("; ");
        }
        log.error("검증 실패. URI: {}, 메시지: {}", request.getRequestURI(), message);
        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body(new ExceptionResponse(message.toString()));
    }

    @ExceptionHandler(FeignException.class)
    public ResponseEntity<ExceptionResponse> handleFeignException(HttpServletRequest request, FeignException e) {
        log.error("외부 서비스 호출 실패. URI: {}, 메시지: {}", request.getRequestURI(), e.getMessage());
        
        HttpStatus status = HttpStatus.SERVICE_UNAVAILABLE;
        String message = "외부 서비스 연결 실패";
        
        if (e.status() == 404) {
            status = HttpStatus.NOT_FOUND;
            message = "요청한 리소스를 외부 서비스에서 찾을 수 없습니다";
        } else if (e.status() >= 400 && e.status() < 500) {
            status = HttpStatus.BAD_REQUEST;
            message = "외부 서비스 요청 실패";
        }
        
        return ResponseEntity.status(status)
                .body(new ExceptionResponse(message));
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<String> handleAllUncaughtException(HttpServletRequest request, Exception ex) {
        log.error("예상치 못한 오류 발생. URI: {}, 메시지: {}", request.getRequestURI(), ex.getMessage(), ex);
        return new ResponseEntity<>("Internal Server Error: " + ex.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
    }
}