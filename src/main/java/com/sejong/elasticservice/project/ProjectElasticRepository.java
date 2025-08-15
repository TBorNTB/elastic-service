package com.sejong.elasticservice.project;

import java.util.List;

public interface ProjectElasticRepository {
    String save(ProjectDocument savedProject);

    void deleteById(String projectId);

    List<String> getSuggestions(String query);

    List<ProjectDocument> searchProjects(String query, ProjectStatus projectStatus, List<String> categories, List<String> techStacks, int size, int page);

}
