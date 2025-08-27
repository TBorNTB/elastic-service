package com.sejong.elasticservice.project;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class ProjectDocument {

    private static final DateTimeFormatter FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss.SSS");

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