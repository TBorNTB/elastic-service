package com.sejong.elasticservice.rag.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.ai.chat.messages.SystemMessage;
import org.springframework.ai.chat.messages.UserMessage;
import org.springframework.ai.chat.model.ChatResponse;
import org.springframework.ai.chat.prompt.Prompt;
import org.springframework.ai.openai.OpenAiChatModel;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class ChatService {
    private final OpenAiChatModel openAiChatModel;

    public ChatResponse openAiChat(String userInput, String systemMessage) {
        try{
            Prompt prompt = new Prompt(List.of(
                    new SystemMessage(systemMessage),
                    new UserMessage(userInput)
            ));

            ChatResponse response = openAiChatModel.call(prompt);
            return response;
        }catch(Exception e){
            log.error("OpenAI 챗 호출 중 오류 발생: {}",e.getMessage(),e);
            return null;
        }
    }
}
