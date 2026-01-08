package com.sejong.elasticservice.domain.csknowledge.consumer;

import com.sejong.elasticservice.common.constants.GroupNames;
import com.sejong.elasticservice.common.constants.TopicNames;
import com.sejong.elasticservice.common.constants.Type;
import com.sejong.elasticservice.domain.csknowledge.domain.CsKnowledgeIndexEvent;
import com.sejong.elasticservice.domain.csknowledge.repository.CsKnowledgeRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class CsKnowledgeConsumer {
    private final CsKnowledgeRepository repository;

    @KafkaListener(
            topics = TopicNames.CSKNOWLEDGE,
            groupId = GroupNames.CSKNOWLEDGE
    )
    public void consume(String message) {
        CsKnowledgeIndexEvent event = CsKnowledgeIndexEvent.fromJson(message);
        
        if (event.getType() == Type.CREATED || event.getType() == Type.UPDATED) {
            repository.save(event.getCsKnowledgeEvent());
        }
        if (event.getType() == Type.DELETED) {
            repository.deleteById(event.getAggregatedId());
        }
    }
}