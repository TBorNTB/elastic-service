package com.sejong.elasticservice.rag.service;

import com.sejong.elasticservice.rag.repository.InMemoryDocumentVectorStore;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.io.File;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

@Service
@Slf4j
@RequiredArgsConstructor
public class RagService {

    private final InMemoryDocumentVectorStore vectorStore;


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
}
