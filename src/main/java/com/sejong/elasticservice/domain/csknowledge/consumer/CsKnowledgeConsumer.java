package com.sejong.elasticservice.domain.csknowledge.consumer;

import com.sejong.elasticservice.client.response.UserNameInfo;
import com.sejong.elasticservice.client.service.UserExternalService;
import com.sejong.elasticservice.common.constants.GroupNames;
import com.sejong.elasticservice.common.constants.TopicNames;
import com.sejong.elasticservice.common.constants.Type;
import com.sejong.elasticservice.common.embedded.Names;
import com.sejong.elasticservice.domain.UserNameInfoService;
import com.sejong.elasticservice.domain.csknowledge.domain.CsKnowledgeDocument;
import com.sejong.elasticservice.domain.csknowledge.domain.CsKnowledgeEvent;
import com.sejong.elasticservice.domain.csknowledge.domain.CsKnowledgeIndexEvent;
import com.sejong.elasticservice.domain.csknowledge.repository.CsKnowledgeRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;

@Slf4j
@Component
@RequiredArgsConstructor
public class CsKnowledgeConsumer {

    private final CsKnowledgeRepository repository;
    private final UserNameInfoService userNameInfoService;

    @KafkaListener(
            topics = TopicNames.CSKNOWLEDGE,
            groupId = GroupNames.CSKNOWLEDGE
    )
    public void consume(String message) {
        CsKnowledgeIndexEvent meta = CsKnowledgeIndexEvent.fromJson(message);

        if (meta.getType() == Type.CREATED || meta.getType() == Type.UPDATED) {
            Names writer = getWriterNames(meta.getCsKnowledgeEvent());
            CsKnowledgeDocument document = CsKnowledgeDocument.from(meta.getCsKnowledgeEvent(), writer);
            repository.save(document);
        } else if (meta.getType() == Type.DELETED) {
            repository.deleteById(meta.getAggregatedId());
        }
    }

    private Names getWriterNames(CsKnowledgeEvent event) {
        String writerUsername = event.getWriterId();
        Map<String, UserNameInfo> infos = userNameInfoService.getUserNameInfos(List.of(writerUsername));
        return userNameInfoService.toNames(writerUsername, infos);
    }
}