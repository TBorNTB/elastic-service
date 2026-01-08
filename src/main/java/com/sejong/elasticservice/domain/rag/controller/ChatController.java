package com.sejong.elasticservice.domain.rag.controller;

import com.sejong.elasticservice.domain.rag.common.ApiResponseDto;
import com.sejong.elasticservice.domain.rag.controller.request.ChatRequest;
import com.sejong.elasticservice.domain.rag.service.ChatService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.ai.chat.model.ChatResponse;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
@RequestMapping("/api/v1/chat")
@RequiredArgsConstructor
@Slf4j
@Tag(name = "Chat API", description = "OpenAi API를 통한 채팅 기능")
public class ChatController {

    private final ChatService chatService;

    @Operation(
            summary = "LLM 채팅 메시지 전송",
            description = "사용자의 메시지를 받아 OpenAI API를 통해 응답을 생성합니다."
    )
    @PostMapping("/query")
    public ResponseEntity<ApiResponseDto<Map<String, Object>>> sendMessage(
            @Valid @RequestBody ChatRequest request
    ) {

        String systemMessage = """
                당신은 사용자가 업로드한 문서와 관련된 질문에 정확하고 근거 있는 답변을 제공하는 전문 AI 어시스턴트입니다.
                모든 답변은 문서의 내용을 근거로 작성하며, 불확실한 내용은 추측하지 않습니다.
                답변은 간결하고 논리적으로 구성하며, 필요한 경우 관련 근거를 함께 제시합니다.
                """;

        ChatResponse response = chatService.openAiChat(
                request.getQuery(),
                systemMessage
        );

        return ResponseEntity.ok(new ApiResponseDto<>(
                true,
                Map.of("answer", response.getResult().getOutput().getText()),
                null));
    }
}
