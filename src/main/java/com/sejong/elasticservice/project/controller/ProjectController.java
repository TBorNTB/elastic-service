package com.sejong.elasticservice.project.controller;

import com.sejong.elasticservice.common.pagenation.PageResponse;
import com.sejong.elasticservice.project.domain.ProjectSortType;
import com.sejong.elasticservice.project.dto.ProjectSearchDto;
import com.sejong.elasticservice.project.service.ProjectService;
import com.sejong.elasticservice.project.domain.ProjectStatus;
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
@RequestMapping("/api/elastic/project")
public class ProjectController {

    private final ProjectService projectService;

    @GetMapping("/suggestion")
    @Operation(summary = "검색어 자동 완성 기능")
    public ResponseEntity<List<String>> getSuggestion(
            @RequestParam String query
    ) {
        List<String> suggestions = projectService.getSuggestions(query);
        return ResponseEntity.ok(suggestions);
    }

    //todo 정렬 및 desc asc 지원되게 해야됨
    @GetMapping("/search")
    @Operation(summary = "elastic 내용물 전체 조회 => 현재 정렬 방식은 지원 안함")
    public ResponseEntity<PageResponse<ProjectSearchDto>> searchProjects(
            @RequestParam String query,
            @RequestParam ProjectStatus projectStatus,
            @RequestParam(defaultValue = "") List<String> categories,
            @RequestParam(defaultValue = "") List<String> techStacks,
            @RequestParam(defaultValue = "LATEST") ProjectSortType projectSortType, // 최신순, 인기순, 이름순
            @RequestParam(defaultValue = "5") int size,
            @RequestParam(defaultValue = "0") int page
    ) {

        PageResponse<ProjectSearchDto> response = projectService.searchProjects(
                query, projectStatus, categories, techStacks, projectSortType, size, page
        );
        return ResponseEntity.ok(response);
    }

    @GetMapping("/search/latest")
    @Operation(summary = "최근 파일 조회")
    public ResponseEntity<List<ProjectSearchDto>> searchLatestProjects(
            @RequestParam(defaultValue = "5") int size,
            @RequestParam(defaultValue = "0") int page
    ) {
        List<ProjectSearchDto> response = projectService.searchProjects(size, page);
        return ResponseEntity.ok(response);
    }
}
