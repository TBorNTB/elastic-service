package com.sejong.elasticservice.domain.project.consumer;


import com.sejong.elasticservice.client.response.UserNameInfo;
import com.sejong.elasticservice.client.service.UserExternalService;
import com.sejong.elasticservice.common.constants.GroupNames;
import com.sejong.elasticservice.common.constants.TopicNames;
import com.sejong.elasticservice.common.embedded.Names;
import com.sejong.elasticservice.domain.project.domain.ProjectDocument;
import com.sejong.elasticservice.domain.project.domain.ProjectEventMeta;
import com.sejong.elasticservice.common.constants.Type;
import com.sejong.elasticservice.domain.project.repository.ProjectRepository;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

@Slf4j
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

        if (meta.getType() == Type.CREATED || meta.getType() == Type.UPDATED) {
            Result result = getResult(meta);
            ProjectDocument projectDocument = ProjectDocument.from(
                    meta.getProjectEvent(), result.ownerNames(), result.collaboratorNames());
            repo.save(projectDocument);
        } else if (meta.getType() == Type.DELETED) {
            repo.deleteById(meta.getAggregatedId());
        }
    }

    private Result getResult(ProjectEventMeta meta) {
        String ownerUsername = meta.getProjectEvent().getUsername();
        List<String> collaborators = meta.getProjectEvent().getCollaborators();

        List<String> usernames = new ArrayList<>();
        usernames.add(ownerUsername);
        usernames.addAll(collaborators);

        Map<String, UserNameInfo> infos = userExternalService.getUserNameInfos(usernames);

        Names ownerNames = toNames(ownerUsername, infos);

        List<Names> collaboratorNames = collaborators.stream()
                .map(collaborator -> toNames(collaborator, infos))
                .toList();

        return new Result(ownerNames, collaboratorNames);
    }

    private Names toNames(String username, Map<String, UserNameInfo> infos) {
        UserNameInfo info = infos.get(username);
        if (info == null) {
            log.warn("User not found: {}", username);
            return new Names(username, null, null);
        }
        return new Names(username, info.nickname(), info.realName());
    }

    private record Result(Names ownerNames, List<Names> collaboratorNames) {
    }
}
