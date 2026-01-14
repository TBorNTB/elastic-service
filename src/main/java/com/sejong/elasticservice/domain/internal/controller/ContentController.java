package com.sejong.elasticservice.domain.internal.controller;

import com.sejong.elasticservice.common.pagenation.PageResponse;
import com.sejong.elasticservice.domain.internal.dto.ContentResponse;
import com.sejong.elasticservice.domain.internal.service.PopularContentService;
import io.swagger.v3.oas.annotations.Operation;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/elastic/contents")
public class ContentController {

    private final PopularContentService popularContentService;

    @GetMapping("/popular")
    @Operation(summary = "인기 게시글 조회 (Project, News, CS Knowledge 통합, 인기도순 정렬)")
    public ResponseEntity<PageResponse<ContentResponse>> getPopularContents(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size
    ) {
        PageResponse<ContentResponse> response = popularContentService.getPopularContents(page, size);
        return ResponseEntity.ok(response);
    }
}
