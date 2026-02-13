package com.sejong.elasticservice.domain.content.controller;

import com.sejong.elasticservice.common.pagenation.PageResponse;
import com.sejong.elasticservice.domain.content.dto.PostRequest;
import com.sejong.elasticservice.domain.content.dto.PostSummaryDto;
import com.sejong.elasticservice.domain.content.service.BFFService;
import com.sejong.elasticservice.domain.internal_newsletter.dto.ContentResponse;
import io.swagger.v3.oas.annotations.Operation;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/elastic/contents")
public class BFFController {

    private final BFFService BFFService;

    @GetMapping("/popular") // TODO:  현재 Admin 페이지에 존재한다. 메인페이지로 프론트 화면 보여지는 곳 옮기기
    @Operation(summary = "인기 게시글 조회 (Project, News, CS Knowledge 각각의 포스트들을 인기도순으로 정렬한다)")
    public ResponseEntity<PageResponse<ContentResponse>> getPopularContents(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size
    ) {
        PageResponse<ContentResponse> response = BFFService.
            getPopularContents(page, size);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/user/{username}/latest") // TODO: API 연결 필요
    @Operation(summary = "특정 유저가 작성한 최신글 조회 (Project, News, CS Knowledge 통합, 최신순 정렬)")
    public ResponseEntity<PageResponse<ContentResponse>> getUserLatestContents(
            @PathVariable String username,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size
    ) {
        PageResponse<ContentResponse> response = BFFService.getUserLatestContents(username, page, size);
        return ResponseEntity.ok(response);
    }

    @PostMapping("/posts") // mypage - 좋아요 누른글들 조회
    @Operation(summary = "postType과 postId List로 여러 게시글 조회")
    public ResponseEntity<List<PostSummaryDto>> getPostsByTypeAndIds(
            @RequestBody PostRequest request
    ) {
        List<PostSummaryDto> response = BFFService.
            getPostsByTypeAndIds(request);
        return ResponseEntity.ok(response);
    }
}
