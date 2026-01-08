package com.sejong.elasticservice.domain.rag.controller;

import com.sejong.elasticservice.domain.rag.common.ApiResponseDto;
import com.sejong.elasticservice.domain.rag.controller.request.QueryRequestDto;
import com.sejong.elasticservice.domain.rag.controller.response.DocumentResponseDto;
import com.sejong.elasticservice.domain.rag.controller.response.DocumentSearchResultDto;
import com.sejong.elasticservice.domain.rag.controller.response.DocumentUploadResultResponse;
import com.sejong.elasticservice.domain.rag.controller.response.QueryResponseDto;
import com.sejong.elasticservice.domain.rag.service.RagService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.jetbrains.annotations.NotNull;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.util.List;

@RestController
@RequestMapping("/api/v1/rag")
@Tag(name = "RAG API", description = "Retrieval-Augmented Generation 기능을 위한 API")
@RequiredArgsConstructor
@Slf4j
public class RagController {
    private final RagService ragService;

    @Operation(
            summary = "PDF 문서 업로드",
            description = "PDF 파일을 업로드하여 벡터 스토어에 저장합니다. 추후 질의에 활용됩니다."
    )
    @PostMapping(value = "/documents", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<ApiResponseDto<DocumentUploadResultResponse>> uploadDocument(
            @Parameter(description = "업로드할 PDF 파일", required = true)
            @RequestParam("file") MultipartFile file
    ){

        log.info("문서 업로드 요청 받음: {}", file.getOriginalFilename());
        if (file.isEmpty()) {
            log.warn("빈 파일이 업로드됨");
            return ResponseEntity.badRequest()
                    .body(new ApiResponseDto<>(false, null, "파일이 비어있습니다."));
        }

        String originalName = file.getOriginalFilename();
        if(originalName == null || !originalName.toLowerCase().endsWith(".pdf")){
            return ResponseEntity.badRequest()
                    .body(new ApiResponseDto<>(false, null, "PDF 파일만 업로드 가능합니다."));
        }

        File tempFile = null;
        try {
            // 임시 파일 생성 및 복사
            tempFile = File.createTempFile(originalName, ".pdf");
            log.debug("임시 파일 생성됨: {}", tempFile.getAbsolutePath());
            file.transferTo(tempFile); //file 은 MultiType으로 되어있어 단순히 스트림 형태다. 이걸 임시 저장소에 file로 넣어주는과정

            // 문서 처리
            String documentId = ragService.uploadPdfFile(tempFile,originalName);
            log.info("문서 업로드 성공: {}", documentId);

            DocumentUploadResultResponse response = DocumentUploadResultResponse.of(documentId,
                    "문서가 성공적으로 업로드되었습니다.");
            return ResponseEntity.ok(ApiResponseDto.ok(response));

        } catch (IOException e) {
            log.error("임시 파일 생성/전달 실패", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new ApiResponseDto<>(false, null, "파일 처리 중 오류가 발생했습니다."));
        } catch (Exception e) {
            log.error("문서 처리 중 오류 발생", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new ApiResponseDto<>(false, null, "문서 처리 중 오류가 발생했습니다: " + e.getMessage()));
        } finally {
            if (tempFile != null && tempFile.exists()) {
                boolean deleted = tempFile.delete();
                log.debug("임시 파일 삭제됨({}): {}", deleted, tempFile.getAbsolutePath());
            }
        }

    }

    @Operation(
            summary = "RAG 질의 수행",
            description = "사용자 질문에 대해 관련 문서를 검색하고 RAG 기반 응답을 생성합니다."
    )
    @PostMapping("/query")
    public ResponseEntity<ApiResponseDto<QueryResponseDto>> queryWithRag(
            @Parameter(description = "질의 요청 객체", required = true)
            @RequestBody QueryRequestDto request
    ) {
        log.info("RAG 질의 요청 받음: {}", request.getQuery());

        try {
            // 관련 문서 검색
            // 타입은 프로젝트에 맞게 변경하세요 (예: List<RetrievedDocument>)
            List<DocumentSearchResultDto> relevantDocs = ragService.retrieve(request.getQuery(), request.getMaxResults());

            // RAG 기반 응답 생성
            String answer = ragService.generateAnswerWithContexts(
                    request.getQuery(),
                    relevantDocs,
                    request.getModel()
            );

            List<DocumentResponseDto> relevantDocumentDtos = toDocumentResponseDto(relevantDocs);

            QueryResponseDto payload = QueryResponseDto.of(request.getQuery(), answer, relevantDocumentDtos);

            return ResponseEntity.ok(ApiResponseDto.ok(payload));

        } catch (Exception e) {
            log.error("RAG 질의 처리 중 오류 발생", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new ApiResponseDto<>(false, null, "질의 처리 중 오류가 발생했습니다: " + e.getMessage()));
        }
    }

    @NotNull
    private static List<DocumentResponseDto> toDocumentResponseDto(List<DocumentSearchResultDto> relevantDocs) {
        List<DocumentResponseDto> relevantDocumentDtos = relevantDocs.stream()
                .map(d -> DocumentResponseDto.builder()
                        .id(d.getId())
                        .content(d.getContent())
                        .metadata(d.getMetadata()) // Map<String, Object>
                        .score(d.getScore())
                        .build())
                .collect(java.util.stream.Collectors.toList());
        return relevantDocumentDtos;
    }
}
