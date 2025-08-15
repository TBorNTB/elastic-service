package com.sejong.elasticservice.project.consumer;


import com.sejong.elasticservice.project.ProjectElasticRepository;
import com.sejong.elasticservice.project.ProjectIndexEvent;
import com.sejong.elasticservice.project.Type;
import lombok.RequiredArgsConstructor;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class ProjectIndexConsumer {
    private final ProjectElasticRepository repo;

    @KafkaListener(
            topics = "${app.kafka.topic:project-events}",
            groupId = "project-group"
    )
    public void consume(String message) {

        ProjectIndexEvent event = ProjectIndexEvent.fromJson(message);

        if (event.getType()== Type.CREATED||event.getType()==Type.UPDATED) repo.save(event.getProjectDocument());
        if (event.getType()==Type.DELETED) repo.deleteById(event.getAggregatedId());
    }
}
