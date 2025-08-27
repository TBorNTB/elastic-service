package com.sejong.elasticservice.project;


import com.sejong.elasticservice.TopicNames;
import lombok.RequiredArgsConstructor;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class ProjectIndexConsumer {
    private final ProjectElasticRepository repo;

    @KafkaListener(
            topics = TopicNames.PROJECT_EVENTS,
            groupId = "project-group"
    )
    public void consume(String message) {

        ProjectIndexEvent event = ProjectIndexEvent.fromJson(message);

        if (event.getType()== Type.CREATED||event.getType()==Type.UPDATED) repo.save(event.getProjectDocument());
        if (event.getType()==Type.DELETED) repo.deleteById(event.getAggregatedId());
    }
}
