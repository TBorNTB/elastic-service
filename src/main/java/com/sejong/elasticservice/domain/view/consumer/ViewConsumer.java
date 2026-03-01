package com.sejong.elasticservice.domain.view.consumer;

import com.sejong.elasticservice.common.constants.GroupNames;
import com.sejong.elasticservice.common.constants.TopicNames;
import com.sejong.elasticservice.domain.postlike.domain.PostType;
import com.sejong.elasticservice.domain.project.repository.ProjectRepository;
import com.sejong.elasticservice.domain.csknowledge.repository.CsKnowledgeRepository;
import com.sejong.elasticservice.domain.news.repository.NewsRepository;
import com.sejong.elasticservice.domain.document.repository.DocumentRepository;
import com.sejong.elasticservice.domain.view.domain.ViewEvent;
import lombok.RequiredArgsConstructor;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class ViewConsumer {
    private final ProjectRepository projectElasticRepository;
    private final NewsRepository newsRepository;
    private final CsKnowledgeRepository csKnowledgeRepository;
    private final DocumentRepository documentRepository;

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
        else if (event.getPostType()==PostType.ARTICLE){
            csKnowledgeRepository.updateViewCount(event.getPostId(), event.getViewCount());
        }
    }
}
