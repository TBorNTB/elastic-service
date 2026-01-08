package com.sejong.elasticservice.domain.project.domain;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class ProjectEvent {

    private String id;

    private String title;
    private String description;

    private ProjectStatus projectStatus;

    private String createdAt;
    private String updatedAt;

    private String thumbnailUrl;

    @Builder.Default
    private List<String> projectCategories = new ArrayList<>();

    @Builder.Default
    private List<String> projectTechStacks = new ArrayList<>();

    @Builder.Default
    private List<String> collaborators = new ArrayList<>();

    @Builder.Default
    private long likeCount = 0L;

    @Builder.Default
    private long viewCount = 0L;
}