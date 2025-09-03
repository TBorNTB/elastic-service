package com.sejong.elasticservice.document;

import com.sejong.elasticservice.common.name.GroupNames;
import com.sejong.elasticservice.common.name.TopicNames;
import com.sejong.elasticservice.project.Type;
import lombok.RequiredArgsConstructor;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class DocumentIndexConsumer {
    private final DocumentElasticRepository repository;

    @KafkaListener(
            topics = TopicNames.DOCUMENT,
            groupId = GroupNames.DOCUMENT
    )
    public void consume(String message) {

        DocumentIndexEvent event = DocumentIndexEvent.fromJson(message);

        if (event.getType()== Type.CREATED||event.getType()==Type.UPDATED) repository.save(event.getDocumentEvent());
        if (event.getType()==Type.DELETED) repository.deleteById(event.getAggregatedId());
    }
}
