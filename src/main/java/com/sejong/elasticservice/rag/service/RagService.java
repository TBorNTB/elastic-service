package com.sejong.elasticservice.rag.service;

import com.sejong.elasticservice.rag.controller.response.DocumentSearchResultDto;
import com.sejong.elasticservice.rag.repository.InMemoryDocumentVectorStore;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.ai.chat.messages.AbstractMessage;
import org.springframework.ai.chat.model.ChatResponse;
import org.springframework.ai.chat.model.Generation;
import org.springframework.stereotype.Service;

import java.io.File;
import java.util.*;
import java.util.stream.Collectors;

@Service
@Slf4j
@RequiredArgsConstructor
public class RagService {

    private final InMemoryDocumentVectorStore vectorStore;
    private final ChatService chatService;


    public String uploadPdfFile(File file, String originalFilename) {
        String documentId = UUID.randomUUID().toString();
        log.info("PDF 문서 업로드 시작. 파일: {}, ID: {}", originalFilename, documentId);

        Map<String,Object> docMetadata = new HashMap();
        docMetadata.put("originalFilename", originalFilename != null ? originalFilename : "");
        docMetadata.put("uploadTime", System.currentTimeMillis());

        vectorStore.addDocumentFile(documentId,file,docMetadata);
        log.info("PDF 문서 업로드 완료. ID: {}", documentId);
        return documentId;
    }

    public List<DocumentSearchResultDto> retrieve(String question, int maxResults) {
        log.debug("검색 시작: '{}', 최대 결과 수: {}", question, maxResults);
        return vectorStore.similaritySearch(question, maxResults);
    }

    /** 기본 모델로 RAG 응답 생성 (기본값: gpt-3.5-turbo) */
    public String generateAnswerWithContexts(String question, List<DocumentSearchResultDto> relevantDocs) {
        return generateAnswerWithContexts(question, relevantDocs, "gpt-3.5-turbo");
    }

    public String generateAnswerWithContexts(String question,
                                             List<DocumentSearchResultDto> relevantDocs,
                                             String model) {
        log.debug("RAG 응답 생성 시작: '{}', 모델: {}", question, model);

        if (relevantDocs == null || relevantDocs.isEmpty()) {
            log.info("관련 정보를 찾을 수 없음: '{}'", question);
            return "관련 정보를 찾을 수 없습니다. 다른 질문을 시도하거나 관련 문서를 업로드해 주세요.";
        }

        // 문서 번호 부여 (응답에서 출처 표시를 위해)
        List<String> numberedDocs = new ArrayList<>(relevantDocs.size());
        for (int i = 0; i < relevantDocs.size(); i++) {
            DocumentSearchResultDto doc = relevantDocs.get(i);
            numberedDocs.add("[" + (i + 1) + "] " + (doc.getContent() != null ? doc.getContent() : ""));
        }

        // 컨텍스트 결합
        String context = String.join("\n\n", numberedDocs);
        log.debug("컨텍스트 크기: {} 문자", context.length());

        // 시스템 프롬프트
        String systemPromptText = (
                "당신은 지식 기반 Q&A 시스템입니다.\n" +
                        "사용자의 질문에 대한 답변을 다음 정보를 바탕으로 생성해주세요.\n" +
                        "주어진 정보에 답이 없다면 모른다고 솔직히 말해주세요.\n" +
                        "답변 마지막에 사용한 정보의 출처 번호 [1], [2] 등을 반드시 포함해주세요.\n\n" +
                        "정보:\n" + context
        );

        try {
            ChatResponse response = chatService.openAiChat(question, systemPromptText);
            log.debug("AI 응답 생성: {}", response);

            // Spring AI ChatResponse → text
            String aiAnswer = Optional.ofNullable(response)
                    .map(ChatResponse::getResult)
                    .map(Generation::getOutput)
                    .map(AbstractMessage::getText)
                    .filter(s -> !s.isBlank())
                    .orElse("응답을 생성할 수 없습니다.");

            // 참고 문서 정보
            String sourceInfo = buildSources(relevantDocs);
            return aiAnswer + sourceInfo;

        } catch (Exception e) {
            log.error("AI 모델 호출 중 오류 발생: {}", e.getMessage(), e);
            String fallback = relevantDocs.stream()
                    .map(DocumentSearchResultDto::getContent)
                    .collect(Collectors.joining("\n\n"));
            return "AI 모델 호출 중 오류가 발생했습니다. 검색 결과만 제공합니다:\n\n" + fallback;
        }
    }

    private String buildSources(List<DocumentSearchResultDto> relevantDocs) {
        StringBuilder sb = new StringBuilder();
        sb.append("\n\n참고 문서:\n");
        for (int i = 0; i < relevantDocs.size(); i++) {
            DocumentSearchResultDto doc = relevantDocs.get(i);
            String original = Optional.ofNullable(doc.getMetadata())
                    .map(m -> m.get("originalFilename"))
                    .map(Object::toString)
                    .orElse("Unknown file");
            sb.append('[').append(i + 1).append("] ").append(original).append('\n');
        }
        return sb.toString();
    }

}
