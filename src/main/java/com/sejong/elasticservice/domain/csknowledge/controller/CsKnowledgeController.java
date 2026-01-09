package com.sejong.elasticservice.domain.csknowledge.controller;

import com.sejong.elasticservice.common.pagenation.PageResponse;
import com.sejong.elasticservice.domain.csknowledge.dto.CsKnowledgeSearchDto;
import com.sejong.elasticservice.domain.csknowledge.service.CsKnowledgeService;
import com.sejong.elasticservice.domain.project.domain.PostSortType;
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

    @GetMapping("/suggestion")
    @Operation(summary = "검색어 자동 완성 기능")
    public ResponseEntity<List<String>> getSuggestion(
            @RequestParam String query
    ) {
        List<String> suggestions = csKnowledgeService.getSuggestions(query);
        return ResponseEntity.ok(suggestions);
    }

    @GetMapping("/search")
    @Operation(summary = "CS 지식 검색 (키워드 + 카테고리 필터)")
    public ResponseEntity<PageResponse<CsKnowledgeSearchDto>> searchCsKnowledge(
            @RequestParam(required = false) String keyword,
            @RequestParam(required = false) String category,
            @RequestParam(defaultValue = "LATEST") PostSortType sortType,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size
    ) {
        PageResponse<CsKnowledgeSearchDto> response = csKnowledgeService.searchCsKnowledge(keyword, category, sortType, page, size);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/search/member")
    @Operation(summary = "nickname 또는 realname으로 CS 지식 검색", description = "writer의 nickname/realname으로 CS 지식을 검색합니다.")
    public ResponseEntity<PageResponse<CsKnowledgeSearchDto>> searchByMemberName(
            @RequestParam String name,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "0") int page
    ) {
        PageResponse<CsKnowledgeSearchDto> response = csKnowledgeService.searchByMemberName(name, size, page);
        return ResponseEntity.ok(response);
    }
}