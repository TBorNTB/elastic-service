package com.sejong.elasticservice.domain.postlike.consumer;

import com.sejong.elasticservice.common.constants.GroupNames;
import com.sejong.elasticservice.common.constants.TopicNames;
import com.sejong.elasticservice.domain.postlike.domain.PostLikeEvent;
import com.sejong.elasticservice.domain.postlike.domain.PostType;
import com.sejong.elasticservice.domain.project.repository.ProjectRepository;
import com.sejong.elasticservice.domain.csknowledge.repository.CsKnowledgeRepository;
import com.sejong.elasticservice.domain.news.repository.NewsRepository;
import com.sejong.elasticservice.domain.document.repository.DocumentRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class PostlikeConsumer {
    private final ProjectRepository projectElasticRepository;
    private final NewsRepository newsRepository;
    private final CsKnowledgeRepository csKnowledgeRepository;
    private final DocumentRepository documentRepository;

    @KafkaListener(
            topics = TopicNames.POSTLIKE,
            groupId = GroupNames.LIKE
    )
    public void consume(String message) {

        PostLikeEvent event = PostLikeEvent.fromJson(message);

        if(event.getPostType()== PostType.PROJECT){
            projectElasticRepository.updateLikeCount(event.getPostId(), event.getLikeCount());
        }
        else if (event.getPostType()==PostType.DOCUMENT){

        }
        else if (event.getPostType()==PostType.NEWS){
            newsRepository.updateLikeCount(event.getPostId(), event.getLikeCount());
        }
        else if (event.getPostType()==PostType.ARTICLE){
            csKnowledgeRepository.updateLikeCount(event.getPostId(), event.getLikeCount());
        }
    }
}
