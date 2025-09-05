package com.sejong.elasticservice.project.repository;

import com.sejong.elasticservice.project.ProjectEvent;
import com.sejong.elasticservice.project.ProjectStatus;
import java.util.List;

public interface ProjectElasticRepository {
    String save(ProjectEvent savedProject);

    void deleteById(String projectId);

    List<String> getSuggestions(String query);

    List<ProjectEvent> searchProjects(String query, ProjectStatus projectStatus, List<String> categories, List<String> techStacks, int size, int page);

    void updateLikeCount(Long postId, Long likeCount);

    void updateViewCount(Long postId, Long viewCount);
}
