package com.sejong.elasticservice.rag.repository;

import com.sejong.elasticservice.rag.common.DocumentProcessingException;
import com.sejong.elasticservice.rag.service.DocumentProcessingService;
import com.sejong.elasticservice.rag.service.EmbeddingService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.ai.document.Document;
import org.springframework.ai.transformer.splitter.TokenTextSplitter;
import org.springframework.ai.vectorstore.SimpleVectorStore;
import org.springframework.ai.vectorstore.VectorStore;
import org.springframework.stereotype.Repository;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

@Repository
@Slf4j
public class InMemoryDocumentVectorStore {

    private final EmbeddingService embeddingService;
    private final DocumentProcessingService documentProcessingService;
    private final VectorStore vectorStore;

    public InMemoryDocumentVectorStore(EmbeddingService embeddingService,
                                       DocumentProcessingService documentProcessingService) {
        this.embeddingService = embeddingService;
        this.documentProcessingService = documentProcessingService;
        this.vectorStore = SimpleVectorStore.builder(embeddingService.getEmbeddingModel()).build();
    }

    public void addDocument(String id, String fileText, Map<String,Object> metadata){

        try {
            // Spring AI Document 객체 생성 (id를 메타데이터에 함께 저장)
            Map<String, Object> merged = new HashMap<>();
            if (metadata != null) merged.putAll(metadata);
            merged.put("id", id);

            Document document = new Document(fileText, merged);

            TokenTextSplitter textSplitter = TokenTextSplitter.builder()
                    .withChunkSize(512)            // 원하는 청크 크기
                    .withMinChunkSizeChars(350)    // 최소 청크 크기
                    .withMinChunkLengthToEmbed(5)  // 임베딩할 최소 청크 길이
                    .withMaxNumChunks(10000)       // 최대 청크 수
                    .withKeepSeparator(true)       // 구분자 유지 여부
                    .build();

            List<Document> chunks = textSplitter.split(document);

            // 벡터 스토어에 문서 청크 추가 (내부적으로 임베딩 변환 수행)
            vectorStore.add(chunks);

            log.info("문서 추가 완료 - ID: {}", id);
        } catch (Exception e) {
            log.error("문서 추가 실패 - ID: {}", id, e);
            throw new DocumentProcessingException("문서 임베딩 및 저장 실패: " + e.getMessage(), e);
        }
    }

    public void addDocumentFile(String id, File file, Map<String, Object> metadata) {
        log.debug("파일 문서 추가 시작 - ID: {}, 파일: {}", id, file.getName());

        try {
            String name = file.getName();
            String ext = "";
            int dot = name.lastIndexOf('.');
            if (dot >= 0) ext = name.substring(dot + 1).toLowerCase(Locale.ROOT);

            // 텍스트 추출
            String fileText = "pdf".equals(ext)
                    ? documentProcessingService.extractTextFromPdf(file)
                    : Files.readString(file.toPath());

            log.debug("파일 텍스트 추출 완료 - 길이: {}", fileText.length());
            addDocument(id, fileText, metadata);
        } catch (IOException e) {
            log.error("파일 읽기 실패 - ID: {}, 파일: {}", id, file.getName(), e);
            throw new DocumentProcessingException("파일 처리 실패: " + e.getMessage(), e);
        } catch (Exception e) {
            log.error("파일 처리 실패 - ID: {}, 파일: {}", id, file.getName(), e);
            throw new DocumentProcessingException("파일 처리 실패: " + e.getMessage(), e);
        }
    }
}
