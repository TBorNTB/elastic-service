package com.sejong.elasticservice.document.controller;

import com.sejong.elasticservice.document.DocumentEvent;
import com.sejong.elasticservice.document.DocumentService;
import io.swagger.v3.oas.annotations.Operation;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/elastic/document")
public class DocumentController {

    private final DocumentService documentService;

    @GetMapping("/suggestion")
    @Operation(summary = "elastic 검색 조회 == 쿠팡 검색 추천처럼 ")
    public ResponseEntity<List<String>> getSuggestion(
            @RequestParam String query
    ) {
        List<String> suggestions = documentService.getSuggestions(query);
        return ResponseEntity.ok(suggestions);
    }

    @GetMapping("/search")
    @Operation(summary = "Document관련 elastic 내용물 전체 조회 => 현재 정렬 방식은 지원 안함")
    public ResponseEntity<List<DocumentEvent>> searchDocuments(
            @RequestParam String query,
            @RequestParam(defaultValue ="5") int size,
            @RequestParam(defaultValue = "0") int page

    ) {

        List<DocumentEvent> response = documentService.searchDocuments(
                query, size,page
        );
        return ResponseEntity.ok(response);
    }
}
