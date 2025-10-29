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
        try(PDDocument document = Loader.loadPDF(pdfFile)){
            PDFTextStripper stripper = new PDFTextStripper();
            
            // PDF 텍스트 추출 설정 개선
            stripper.setSortByPosition(true);  // 위치별로 정렬
            stripper.setStartPage(1);          // 시작 페이지
            stripper.setEndPage(document.getNumberOfPages()); // 끝 페이지
            
            String text = stripper.getText(document);
            
            // 텍스트 정리
            text = text.replaceAll("\\s+", " ").trim(); // 연속된 공백을 하나로
            text = text.replaceAll("[\\r\\n]+", " ");   // 줄바꿈을 공백으로
            
            log.info("📄 PDF 텍스트 추출 완료 - 파일: {}, 페이지 수: {}, 텍스트 길이: {}", 
                pdfFile.getName(), document.getNumberOfPages(), text.length());
            log.debug("📄 추출된 텍스트 미리보기: {}", text.substring(0, Math.min(200, text.length())));
            
            return text;
        }catch(IOException e){
            log.error("PDF 텍스트 추출 실패 - 파일: {}", pdfFile.getName(), e);
            throw new DocumentProcessingException("PDF에서 텍스트 추출 실패: " + e.getMessage(), e);
        }
    }
}
