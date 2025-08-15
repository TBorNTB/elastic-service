package com.sejong.elasticservice.project.doc;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.elasticsearch.annotations.Document;
import org.springframework.data.elasticsearch.annotations.Field;
import org.springframework.data.elasticsearch.annotations.FieldType;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
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

    private List<String> projectCategories = new ArrayList<>();

    private List<String> projectTechStacks = new ArrayList<>();

    private List<String> collaborators = new ArrayList<>();
}
