package com.sejong.elasticservice.view.consumer;

import com.sejong.elasticservice.common.constants.GroupNames;
import com.sejong.elasticservice.common.constants.TopicNames;
import com.sejong.elasticservice.postlike.PostType;
import com.sejong.elasticservice.project.repository.ProjectRepository;
import com.sejong.elasticservice.csknowledge.repository.CsKnowledgeRepository;
import com.sejong.elasticservice.news.repository.NewsRepository;
import com.sejong.elasticservice.document.repository.DocumentElasticRepository;
import com.sejong.elasticservice.view.domain.ViewEvent;
import lombok.RequiredArgsConstructor;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class ViewConsumer {
    private final ProjectRepository projectElasticRepository;
    private final NewsRepository newsRepository;
    private final CsKnowledgeRepository csKnowledgeRepository;
    private final DocumentElasticRepository documentElasticRepository;

    @KafkaListener(
            topics = TopicNames.VIEW,
            groupId = GroupNames.VIEW
    )
    public void consume(String message) {

        ViewEvent event = ViewEvent.fromJson(message);

        if(event.getPostType()== PostType.PROJECT){
            projectElasticRepository.updateViewCount(event.getPostId(), event.getViewCount());
        }
        else if (event.getPostType()==PostType.DOCUMENT){

        }
        else if (event.getPostType()==PostType.NEWS){
            newsRepository.updateViewCount(event.getPostId(), event.getViewCount());
        }
        else if (event.getPostType()==PostType.CSKNOWLEDGE){
            csKnowledgeRepository.updateViewCount(event.getPostId(), event.getViewCount());
        }
    }
}
