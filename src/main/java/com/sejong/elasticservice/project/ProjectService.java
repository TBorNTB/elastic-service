package com.sejong.elasticservice.project;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class ProjectService {

    private final ProjectElasticRepository projectElasticRepository;

    public List<String> getSuggestions(String query) {
        return projectElasticRepository.getSuggestions(query);
    }

    public List<ProjectDocument> searchProjects(
            String query,
            ProjectStatus projectStatus,
            List<String> categories,
            List<String> techStacks,
            int size,
            int page
    ) {
        List<ProjectDocument> projectDocuments = projectElasticRepository.searchProjects(
                query, projectStatus, categories, techStacks, size,page
        );

        return projectDocuments;
    }
}
