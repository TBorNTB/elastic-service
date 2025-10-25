package com.sejong.elasticservice.rag.controller;

import com.sejong.elasticservice.rag.common.ApiResponseDto;
import com.sejong.elasticservice.rag.controller.response.DocumentUploadResultResponse;
import com.sejong.elasticservice.rag.service.RagService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.constraints.NotBlank;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;

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
}
