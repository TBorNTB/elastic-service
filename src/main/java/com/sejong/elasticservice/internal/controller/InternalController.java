package com.sejong.elasticservice.internal.controller;

import com.sejong.elasticservice.internal.service.PopularContentService;
import com.sejong.elasticservice.internal.dto.PopularContentResponse;
import io.swagger.v3.oas.annotations.Operation;
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

    @GetMapping("/weekly-popular")
    @Operation(summary = "일주일 내 인기 컨텐츠 조회 - 가장 인기있는 콘텐츠 1개 (좋아요*2 + 조회수)")
    public ResponseEntity<PopularContentResponse> getWeeklyPopularContent() {
        PopularContentResponse popularContent = popularContentService.getMostPopularContent();
        if (popularContent == null) {
            return ResponseEntity.noContent().build();
        }
        return ResponseEntity.ok(popularContent);
    }
}