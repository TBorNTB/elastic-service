package com.sejong.elasticservice.domain.project.dto;

import com.sejong.elasticservice.common.dto.UserInfo;
import com.sejong.elasticservice.domain.project.domain.ProjectDocument;
import com.sejong.elasticservice.domain.project.domain.ProjectStatus;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ProjectSearchDto {
    private Long id;
    private String title;
    private String description;
    private String thumbnailUrl;
    private ProjectStatus projectStatus;
    private List<String> projectCategories;
    private List<String> projectTechStacks;
    private String createdAt;
    private String updatedAt;
    private String startedAt;
    private String endedAt;
    private long likeCount;
    private long viewCount;
    
    private UserInfo owner;
    private List<UserInfo> collaborators;

    public static ProjectSearchDto from(ProjectDocument document) {
        return ProjectSearchDto.builder()
                .id(Long.parseLong(document.getId()))
                .title(document.getTitle())
                .description(document.getDescription())
                .thumbnailUrl(document.getThumbnailUrl())
                .projectStatus(document.getProjectStatus())
                .projectCategories(document.getProjectCategories())
                .projectTechStacks(document.getProjectTechStacks())
                .createdAt(document.getCreatedAt())
                .updatedAt(document.getUpdatedAt())
                .startedAt(document.getStartedAt())
                .endedAt(document.getEndedAt())
                .likeCount(document.getLikeCount())
                .viewCount(document.getViewCount())
                .owner(UserInfo.from(document.getOwner()))
                .collaborators(document.getCollaborators() != null 
                        ? document.getCollaborators().stream().map(UserInfo::from).toList() 
                        : List.of())
                .build();
    }
}