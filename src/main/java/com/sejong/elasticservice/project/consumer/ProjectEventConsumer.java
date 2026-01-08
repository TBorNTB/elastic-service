package com.sejong.elasticservice.project.consumer;


import com.sejong.elasticservice.client.response.UserNameInfo;
import com.sejong.elasticservice.client.service.UserExternalService;
import com.sejong.elasticservice.common.constants.GroupNames;
import com.sejong.elasticservice.common.constants.TopicNames;
import com.sejong.elasticservice.project.domain.ProjectDocument;
import com.sejong.elasticservice.project.domain.ProjectEventMeta;
import com.sejong.elasticservice.common.constants.Type;
import com.sejong.elasticservice.project.repository.ProjectRepository;
import java.util.List;
import java.util.Map;
import lombok.RequiredArgsConstructor;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class ProjectEventConsumer {

    private final ProjectRepository repo;
    private final UserExternalService userExternalService;

    @KafkaListener(
            topics = TopicNames.PROJECT,
            groupId = GroupNames.PROJECT
    )
    public void consume(String message) {

        ProjectEventMeta meta = ProjectEventMeta.fromJson(message);

        if (meta.getType()== Type.CREATED || meta.getType()==Type.UPDATED) {
            List<String> usernames = meta.getProjectEvent().getCollaborators();
            Map<String, UserNameInfo> userNameInfos = userExternalService.getUserNameInfos(usernames);
            ProjectDocument projectDocument = ProjectDocument.from(meta.getProjectEvent(), userNameInfos);
            repo.save(projectDocument);
        }
        if (meta.getType()==Type.DELETED) {
            repo.deleteById(meta.getAggregatedId());
        }
    }
}
