package com.sejong.elasticservice.csknowledge.controller;

import com.sejong.elasticservice.csknowledge.dto.CsKnowledgeSearchDto;
import com.sejong.elasticservice.csknowledge.service.CsKnowledgeService;
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
@RequestMapping("/api/elastic/csknowledge")
public class CsKnowledgeController {

    private final CsKnowledgeService csKnowledgeService;

    @GetMapping("/search")
    @Operation(summary = "CS 지식 검색 (키워드 + 카테고리 필터)")
    public ResponseEntity<List<CsKnowledgeSearchDto>> searchCsKnowledge(
            @RequestParam String keyword,
            @RequestParam(required = false) String category,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size
    ) {
        List<CsKnowledgeSearchDto> response = csKnowledgeService.searchCsKnowledge(keyword, category, page, size);
        return ResponseEntity.ok(response);
    }
}