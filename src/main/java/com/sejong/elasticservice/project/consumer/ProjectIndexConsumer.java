package com.sejong.elasticservice.project.consumer;


import com.sejong.elasticservice.common.name.GroupNames;
import com.sejong.elasticservice.common.name.TopicNames;
import com.sejong.elasticservice.project.ProjectIndexEvent;
import com.sejong.elasticservice.project.Type;
import com.sejong.elasticservice.project.repository.ProjectElasticRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class ProjectIndexConsumer {
    private final ProjectElasticRepository repo;

    @KafkaListener(
            topics = TopicNames.PROJECT,
            groupId = GroupNames.PROJECT
    )
    public void consume(String message) {

        ProjectIndexEvent event = ProjectIndexEvent.fromJson(message);

        if (event.getType()== Type.CREATED||event.getType()==Type.UPDATED) repo.save(event.getProjectEvent());
        if (event.getType()==Type.DELETED) repo.deleteById(event.getAggregatedId());
    }
}
