package com.sejong.elasticservice.project.controller;

import com.sejong.elasticservice.project.ProjectEvent;
import com.sejong.elasticservice.project.ProjectService;
import com.sejong.elasticservice.project.ProjectStatus;
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
    public ResponseEntity<List<ProjectEvent>> searchProjects(
            @RequestParam String query,
            @RequestParam ProjectStatus projectStatus,
            @RequestParam(defaultValue ="") List<String> categories,
            @RequestParam(defaultValue ="") List<String> techStacks,
            @RequestParam(defaultValue ="5") int size,
            @RequestParam(defaultValue = "0") int page

    ) {

        List<ProjectEvent> response = projectService.searchProjects(
                query, projectStatus, categories, techStacks, size,page
        );
        return ResponseEntity.ok(response);
    }

}
