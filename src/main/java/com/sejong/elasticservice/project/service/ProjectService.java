package com.sejong.elasticservice.project.service;

import com.sejong.elasticservice.project.domain.ProjectEvent;
import com.sejong.elasticservice.project.domain.ProjectStatus;
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

    public List<ProjectEvent> searchProjects(
            String query,
            ProjectStatus projectStatus,
            List<String> categories,
            List<String> techStacks,
            int size,
            int page
    ) {
        List<ProjectEvent> projectEvents = projectElasticRepository.searchProjects(
                query, projectStatus, categories, techStacks, size,page
        );

        return projectEvents;
    }
}
