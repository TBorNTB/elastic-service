package com.sejong.elasticservice.rag.repository;

import com.sejong.elasticservice.rag.common.DocumentProcessingException;
import com.sejong.elasticservice.rag.controller.response.DocumentSearchResultDto;
import com.sejong.elasticservice.rag.service.DocumentProcessingService;
import com.sejong.elasticservice.rag.service.EmbeddingService;
import lombok.extern.slf4j.Slf4j;
import org.elasticsearch.client.RestClient;
import org.springframework.ai.document.Document;
import org.springframework.ai.transformer.splitter.TokenTextSplitter;
import org.springframework.ai.vectorstore.SearchRequest;
import org.springframework.ai.vectorstore.VectorStore;
import org.springframework.ai.vectorstore.elasticsearch.ElasticsearchVectorStore;
import org.springframework.stereotype.Repository;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.*;

@Repository
@Slf4j
public class ElasticDocumentVectorStore {

    private final EmbeddingService embeddingService;
    private final DocumentProcessingService documentProcessingService;
    private final VectorStore vectorStore;

    public ElasticDocumentVectorStore(
            EmbeddingService embeddingService,
            DocumentProcessingService documentProcessingService,
            RestClient restClient
    ) {
        this.embeddingService = embeddingService;
        this.documentProcessingService = documentProcessingService;

        this.vectorStore = ElasticsearchVectorStore.builder(restClient, embeddingService.getEmbeddingModel())
                .build();

        log.info("✅ Elasticsearch VectorStore initialized with index 'spring-ai-document-index'");
    }

    public void addDocument(String id, String fileText, Map<String,Object> metadata) {
        try {
            Map<String, Object> merged = new HashMap<>();
            if (metadata != null) merged.putAll(metadata);
            merged.put("id", id);

            log.info("📄 원본 텍스트 길이: {} 문자", fileText.length());
            log.debug("📄 원본 텍스트 미리보기: {}", fileText.substring(0, Math.min(200, fileText.length())));

            Document document = new Document(fileText, merged);

            TokenTextSplitter textSplitter = TokenTextSplitter.builder()
                    .withChunkSize(512)              // 청크 크기 증가
                    .withMinChunkSizeChars(50)       // 최소 청크 크기 감소
                    .withMinChunkLengthToEmbed(10)   // 임베딩할 최소 길이 증가
                    .withMaxNumChunks(1000)          // 최대 청크 수 제한
                    .withKeepSeparator(true)
                    .build();

            List<Document> chunks = textSplitter.split(document);
            log.info("🔪 청킹 결과: {} 개의 청크 생성", chunks.size());
            
            for (int i = 0; i < Math.min(3, chunks.size()); i++) {
                log.debug("📝 청크 {}: {} 문자 - {}", i+1, chunks.get(i).getText().length(), 
                    chunks.get(i).getText().substring(0, Math.min(100, chunks.get(i).getText().length())));
            }

            vectorStore.add(chunks);

            log.info("📘 Elastic VectorStore 문서 추가 완료 - ID: {}, 청크 수: {}", id, chunks.size());
        } catch (Exception e) {
            log.error("❌ Elasticsearch 저장 실패 - ID: {}", id, e);
            throw new DocumentProcessingException("Elasticsearch 저장 실패: " + e.getMessage(), e);
        }
    }

    public void addDocumentFile(String id, File file, Map<String, Object> metadata) {
        try {
            String name = file.getName();
            String ext = "";
            int dot = name.lastIndexOf('.');
            if (dot >= 0) ext = name.substring(dot + 1).toLowerCase(Locale.ROOT);

            String fileText = "pdf".equals(ext)
                    ? documentProcessingService.extractTextFromPdf(file)
                    : Files.readString(file.toPath());

            addDocument(id, fileText, metadata);
        } catch (IOException e) {
            throw new DocumentProcessingException("파일 처리 실패: " + e.getMessage(), e);
        }
    }

    public List<DocumentSearchResultDto> similaritySearch(String query, int maxResults) {
        try {
            SearchRequest request = SearchRequest.builder()
                    .query(query)
                    .topK(maxResults)
                    .similarityThreshold(0.0) // 유사도 임계값 설정
                    .build();

            List<Document> results = Optional.ofNullable(vectorStore.similaritySearch(request))
                    .orElse(Collections.emptyList());

            log.info("🔍 검색 결과: {} 개의 문서 발견", results.size());

            List<DocumentSearchResultDto> mapped = new ArrayList<>(results.size());
            for (int i = 0; i < results.size(); i++) {
                Document doc = results.get(i);
                Map<String, Object> meta = doc.getMetadata();
                String id = String.valueOf(meta.getOrDefault("id", "unknown"));
                
                // 점수 계산 - 여러 필드에서 점수 정보 추출 시도
                double score = 0.0;
                if (meta.containsKey("score")) {
                    score = meta.get("score") instanceof Number
                            ? ((Number) meta.get("score")).doubleValue() : 0.0;
                } else if (meta.containsKey("distance")) {
                    // distance를 score로 변환 (distance는 낮을수록 유사, score는 높을수록 유사)
                    double distance = meta.get("distance") instanceof Number
                            ? ((Number) meta.get("distance")).doubleValue() : 1.0;
                    score = Math.max(0.0, 1.0 - distance); // distance를 score로 변환
                } else {
                    // 순위 기반 점수 계산 (첫 번째 결과가 가장 높은 점수)
                    score = Math.max(0.0, 1.0 - (i * 0.1));
                }
                
                log.debug("📊 문서 {}: ID={}, 점수={}, 메타데이터={}", i+1, id, score, meta.keySet());
                
                mapped.add(new DocumentSearchResultDto(id, doc.getText(), meta, score));
            }
            return mapped;
        } catch (Exception e) {
            log.error("❌ 유사도 검색 중 오류", e);
            throw new DocumentProcessingException("유사도 검색 중 오류: " + e.getMessage(), e);
        }
    }

    /**
     * 개선된 점수 계산을 사용한 유사도 검색
     */
    public List<DocumentSearchResultDto> similaritySearchWithImprovedScoring(String query, int maxResults) {
        try {
            SearchRequest request = SearchRequest.builder()
                    .query(query)
                    .topK(maxResults)
                    .similarityThreshold(0.0)
                    .build();

            List<Document> results = Optional.ofNullable(vectorStore.similaritySearch(request))
                    .orElse(Collections.emptyList());

            log.info("🔍 개선된 점수 계산 검색 결과: {} 개의 문서 발견", results.size());

            List<DocumentSearchResultDto> mapped = new ArrayList<>(results.size());
            for (int i = 0; i < results.size(); i++) {
                Document doc = results.get(i);
                Map<String, Object> meta = doc.getMetadata();
                String id = String.valueOf(meta.getOrDefault("id", "unknown"));
                
                // 개선된 점수 계산
                double score = calculateImprovedScore(meta, i, results.size());
                
                log.debug("📊 개선된 점수 문서 {}: ID={}, 점수={}, 메타데이터={}", i+1, id, score, meta.keySet());
                
                mapped.add(new DocumentSearchResultDto(id, doc.getText(), meta, score));
            }
            return mapped;
        } catch (Exception e) {
            log.error("❌ 개선된 점수 계산 검색 중 오류", e);
            // 오류 발생 시 기존 방식으로 fallback
            return similaritySearch(query, maxResults);
        }
    }

    /**
     * 개선된 점수 계산 로직
     */
    private double calculateImprovedScore(Map<String, Object> meta, int index, int totalResults) {
        // 1. score 필드가 있으면 사용
        if (meta.containsKey("score") && meta.get("score") instanceof Number) {
            double score = ((Number) meta.get("score")).doubleValue();
            return Math.max(0.0, Math.min(1.0, score)); // 0-1 범위로 정규화
        }
        
        // 2. distance 필드가 있으면 score로 변환
        if (meta.containsKey("distance") && meta.get("distance") instanceof Number) {
            double distance = ((Number) meta.get("distance")).doubleValue();
            // distance는 0에 가까울수록 유사, score는 1에 가까울수록 유사
            return Math.max(0.0, Math.min(1.0, 1.0 - distance));
        }
        
        // 3. similarity 필드가 있으면 사용
        if (meta.containsKey("similarity") && meta.get("similarity") instanceof Number) {
            double similarity = ((Number) meta.get("similarity")).doubleValue();
            return Math.max(0.0, Math.min(1.0, similarity));
        }
        
        // 4. 순위 기반 점수 계산 (첫 번째가 가장 높은 점수)
        double rankBasedScore = Math.max(0.1, 1.0 - (index * 0.15));
        return Math.min(1.0, rankBasedScore);
    }
}