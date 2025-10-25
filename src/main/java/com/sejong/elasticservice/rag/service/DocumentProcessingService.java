package com.sejong.elasticservice.rag.service;

import com.sejong.elasticservice.rag.common.DocumentProcessingException;
import lombok.extern.slf4j.Slf4j;
import org.apache.pdfbox.Loader;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.text.PDFTextStripper;
import org.springframework.stereotype.Service;

import java.io.File;
import java.io.IOException;

@Service
@Slf4j
public class DocumentProcessingService {
    public String extractTextFromPdf(File pdfFile) {
        log.debug("PDF 텍스트 추출 시작: {}", pdfFile.getName());
        try(PDDocument document = Loader.loadPDF(pdfFile)){
            log.debug("PDF 문서 로드 성공: {} 페이지", document.getNumberOfPages());
            String text = new PDFTextStripper().getText(document);
            log.debug("PDF 텍스트 추출 완료: {} 문자", text.length());
            return text;
        }catch(IOException e){
            log.error("PDF 텍스트 추출 실패", e);
            throw new DocumentProcessingException("PDF에서 텍스트 추출 실패: " + e.getMessage(), e);
        }
    }
}
