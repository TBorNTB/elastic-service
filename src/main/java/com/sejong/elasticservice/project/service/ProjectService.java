package com.sejong.elasticservice.project.service;

import com.sejong.elasticservice.project.domain.ProjectDocument;
import com.sejong.elasticservice.project.domain.ProjectStatus;
import com.sejong.elasticservice.project.dto.ProjectSearchDto;
import com.sejong.elasticservice.project.repository.ProjectRepository;
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

    public List<ProjectSearchDto> searchProjects(
            String query,
            ProjectStatus projectStatus,
            List<String> categories,
            List<String> techStacks,
            int size,
            int page
    ) {
        List<ProjectDocument> projectDocuments = projectElasticRepository.searchProjects(query, projectStatus, categories, techStacks, size, page);
        return projectDocuments.stream().map(ProjectSearchDto::toProjectSearchDto).toList();
    }

    public List<ProjectSearchDto> searchProjects(int size, int page) {
        List<ProjectDocument> projectDocuments = projectElasticRepository.searchProjects(size, page);
        return projectDocuments.stream().map(ProjectSearchDto::toProjectSearchDto).toList();
    }
}
