package com.sejong.elasticservice.news.controller;

import com.sejong.elasticservice.news.dto.NewsSearchDto;
import com.sejong.elasticservice.news.service.NewsService;
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
@RequestMapping("/api/elastic/news")
public class NewsController {

    private final NewsService newsService;

    @GetMapping("/search")
    @Operation(summary = "뉴스 검색 (키워드 + 카테고리 필터)")
    public ResponseEntity<List<NewsSearchDto>> searchNews(
            @RequestParam String keyword,
            @RequestParam(required = false) String category,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size
    ) {
        List<NewsSearchDto> response = newsService.searchNews(keyword, category, page, size);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/suggestion")
    @Operation(summary = "검색어 자동 완성 기능")
    public ResponseEntity<List<String>> getSuggestion(
            @RequestParam String query
    ) {
        List<String> suggestions = newsService.getSuggestions(query);
        return ResponseEntity.ok(suggestions);
    }

    @GetMapping("/search/latest")
    @Operation(summary = "뉴스 최근 데이터 조회 - 아티클")
    public ResponseEntity<List<NewsSearchDto>> searchNewsLastest(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size
    ) {
        List<NewsSearchDto> response = newsService.searchNews(page, size);
        return ResponseEntity.ok(response);

    }

    @GetMapping("/search/tags")
    @Operation(summary = "뉴스 태그로 검색")
    public ResponseEntity<List<NewsSearchDto>> searchByTags(
            @RequestParam List<String> tags,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size
    ) {
        List<NewsSearchDto> response = newsService.searchByTags(tags, page, size);
        return ResponseEntity.ok(response);
    }

}
