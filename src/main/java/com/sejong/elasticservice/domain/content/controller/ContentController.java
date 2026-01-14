package com.sejong.elasticservice.domain.content.controller;

import com.sejong.elasticservice.common.pagenation.PageResponse;
import com.sejong.elasticservice.domain.content.dto.PostRequest;
import com.sejong.elasticservice.domain.content.dto.PostSummaryDto;
import com.sejong.elasticservice.domain.content.service.ContentService;
import com.sejong.elasticservice.domain.content.service.PopularContentService;
import com.sejong.elasticservice.domain.internal.dto.ContentResponse;
import io.swagger.v3.oas.annotations.Operation;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/elastic/contents")
public class ContentController {

    private final PopularContentService popularContentService;
    private final ContentService contentService;

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

    @PostMapping("/posts")
    @Operation(summary = "postType과 postId List로 여러 게시글 조회")
    public ResponseEntity<List<PostSummaryDto>> getPostsByTypeAndIds(
            @RequestBody PostRequest request
    ) {
        List<PostSummaryDto> response = contentService.getPostsByTypeAndIds(request);
        return ResponseEntity.ok(response);
    }
}
