package com.sejong.elasticservice.domain.project.service;

import com.sejong.elasticservice.common.pagenation.PageResponse;
import com.sejong.elasticservice.domain.project.domain.ProjectDocument;
import com.sejong.elasticservice.domain.project.domain.PostSortType;
import com.sejong.elasticservice.domain.project.domain.ProjectStatus;
import com.sejong.elasticservice.domain.project.dto.ProjectSearchDto;
import com.sejong.elasticservice.domain.project.repository.ProjectRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class ProjectService {

    private final ProjectRepository projectElasticRepository;

    public List<String> getSuggestions(String query) {
        return projectElasticRepository.getSuggestions(query);
    }

    public PageResponse<ProjectSearchDto> searchProjects(
            String query,
            ProjectStatus projectStatus,
            List<String> categories,
            List<String> techStacks,
            PostSortType postSortType,
            int size,
            int page
    ) {
        PageResponse<ProjectDocument> result =
                projectElasticRepository.searchProjects(
                        query,
                        projectStatus,
                        categories,
                        techStacks,
                        postSortType,
                        size,
                        page
                );

        List<ProjectSearchDto> dtoList = result.content()
                .stream()
                .map(ProjectSearchDto::from)
                .toList();

        return new PageResponse<>(
                dtoList,
                result.page(),
                result.size(),
                result.totalElements(),
                result.totalPages()
        );
    }
    public List<ProjectSearchDto> searchProjects(int size, int page) {
        List<ProjectDocument> projectDocuments = projectElasticRepository.searchProjects(size, page);
        return projectDocuments.stream().map(ProjectSearchDto::from).toList();
    }

    public PageResponse<ProjectSearchDto> searchByMemberName(String name, int size, int page) {
        PageResponse<ProjectDocument> result = projectElasticRepository.searchByMemberName(name, size, page);

        List<ProjectSearchDto> dtoList = result.content()
                .stream()
                .map(ProjectSearchDto::from)
                .toList();

        return new PageResponse<>(
                dtoList,
                result.page(),
                result.size(),
                result.totalElements(),
                result.totalPages()
        );
    }

    public PageResponse<ProjectSearchDto> searchByUsername(String username, int size, int page) {
        PageResponse<ProjectDocument> result = projectElasticRepository.searchByUsername(username, size, page);

        List<ProjectSearchDto> dtoList = result.content()
                .stream()
                .map(ProjectSearchDto::from)
                .toList();

        return new PageResponse<>(
                dtoList,
                result.page(),
                result.size(),
                result.totalElements(),
                result.totalPages()
        );
    }
}
