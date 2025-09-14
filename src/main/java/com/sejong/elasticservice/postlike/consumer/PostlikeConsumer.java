package com.sejong.elasticservice.postlike.consumer;

import com.sejong.elasticservice.common.constants.GroupNames;
import com.sejong.elasticservice.common.constants.TopicNames;
import com.sejong.elasticservice.postlike.domain.PostLikeEvent;
import com.sejong.elasticservice.postlike.PostType;
import com.sejong.elasticservice.project.repository.ProjectElasticRepository;
import com.sejong.elasticservice.csknowledge.repository.CsKnowledgeRepository;
import com.sejong.elasticservice.news.repository.NewsRepository;
import com.sejong.elasticservice.document.repository.DocumentElasticRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class PostlikeConsumer {
    private final ProjectElasticRepository projectElasticRepository;
    private final NewsRepository newsRepository;
    private final CsKnowledgeRepository csKnowledgeRepository;
    private final DocumentElasticRepository documentElasticRepository;

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
        else if (event.getPostType()==PostType.CSKNOWLEDGE){
            csKnowledgeRepository.updateLikeCount(event.getPostId(), event.getLikeCount());
        }
    }
}
