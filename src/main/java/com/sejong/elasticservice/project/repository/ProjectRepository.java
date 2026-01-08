package com.sejong.elasticservice.project.repository;

import com.sejong.elasticservice.common.pagenation.PageResponse;
import com.sejong.elasticservice.project.domain.ProjectDocument;
import com.sejong.elasticservice.project.domain.ProjectEvent;
import com.sejong.elasticservice.project.domain.PostSortType;
import com.sejong.elasticservice.project.domain.ProjectStatus;

import java.util.List;

public interface ProjectRepository {
    String save(ProjectDocument projectDocument);

    void deleteById(String projectId);

    List<String> getSuggestions(String query);

    PageResponse<ProjectDocument> searchProjects(String query, ProjectStatus projectStatus, List<String> categories, List<String> techStacks, PostSortType postSortType, int size, int page);

    List<ProjectDocument> searchProjects(int size, int page);

    void updateLikeCount(Long postId, Long likeCount);

    void updateViewCount(Long postId, Long viewCount);


}
