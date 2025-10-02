package com.sejong.elasticservice.document.consumer;

import com.sejong.elasticservice.common.constants.GroupNames;
import com.sejong.elasticservice.common.constants.TopicNames;
import com.sejong.elasticservice.document.domain.DocumentIndexEvent;
import com.sejong.elasticservice.document.repository.DocumentRepository;
import com.sejong.elasticservice.common.constants.Type;
import lombok.RequiredArgsConstructor;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class DocumentIndexConsumer {
    private final DocumentRepository repository;

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
