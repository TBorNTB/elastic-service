package com.sejong.elasticservice.internal.controller;

import com.sejong.elasticservice.common.constants.TechCategory;
import com.sejong.elasticservice.internal.service.InterestContentService;
import com.sejong.elasticservice.internal.service.PopularContentService;
import com.sejong.elasticservice.internal.dto.ContentResponse;
import io.swagger.v3.oas.annotations.Operation;
import java.util.List;
import java.util.Locale.Category;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/internal")
public class InternalController {

    private final PopularContentService popularContentService;
    private final InterestContentService interestContentService;

    @GetMapping("/weekly-popular")
    @Operation(summary = "일주일 내 인기 컨텐츠 조회 - 가장 인기있는 콘텐츠 1개 (좋아요*2 + 조회수)")
    public ResponseEntity<ContentResponse> getWeeklyPopularContent() {
        ContentResponse content = popularContentService.getMostPopularContent();
        if (content == null) {
            return ResponseEntity.noContent().build();
        }
        return ResponseEntity.ok(content);
    }

    @GetMapping("/follow-interest")
    @Operation(summary = "뉴스레터 구독자의 관심사 컨텐츠 조회")
    public ResponseEntity<List<ContentResponse>> getInterstingContent(List<TechCategory> categories) {
        List<ContentResponse> contents = interestContentService.getRandomContentsOf(categories);
        return ResponseEntity.ok(contents);
    }
}