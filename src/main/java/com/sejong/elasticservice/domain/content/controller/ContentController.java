package com.sejong.elasticservice.domain.content.controller;

import com.sejong.elasticservice.common.pagenation.PageResponse;
import com.sejong.elasticservice.domain.content.service.PopularContentService;
import com.sejong.elasticservice.domain.internal.dto.ContentResponse;
import io.swagger.v3.oas.annotations.Operation;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
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

    @GetMapping("/user/{username}/latest")
    @Operation(summary = "특정 유저가 작성한 최신글 조회 (Project, News, CS Knowledge 통합, 최신순 정렬)")
    public ResponseEntity<PageResponse<ContentResponse>> getUserLatestContents(
            @PathVariable String username,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size
    ) {
        PageResponse<ContentResponse> response = popularContentService.getUserLatestContents(username, page, size);
        return ResponseEntity.ok(response);
    }
}
