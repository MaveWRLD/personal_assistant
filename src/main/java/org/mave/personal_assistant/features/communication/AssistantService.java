package org.mave.personal_assistant.features.communication;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.mave.personal_assistant.features.communication.dto.ChatResponse;
import org.mave.personal_assistant.infrastructure.config.Assistant;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
public class AssistantService {

    private final Assistant personalAssistant;

    public ChatResponse processUserMessage(String conversationId, String userMessage) {
        log.debug("Processing user message for conversationId: {}, message length: {}", 
                conversationId, userMessage != null ? userMessage.length() : 0);
        
        if (conversationId == null || conversationId.isBlank()) {
            conversationId = UUID.randomUUID().toString();
            log.info("Generated new conversation ID: {}", conversationId);
        } else {
            log.debug("Using existing conversation ID: {}", conversationId);
        }

        log.info("Processing message for conversation {}: {}", 
                conversationId, userMessage.length() > 100 ? 
                        userMessage.substring(0, 100) + "..." : userMessage);
        
        try {
            String response = personalAssistant.chat(conversationId, userMessage);
            log.info("Generated response for conversation {}: {}", 
                    conversationId, response.length() > 100 ? 
                            response.substring(0, 100) + "..." : response);
            
            ChatResponse chatResponse = new ChatResponse(response, conversationId);
            log.debug("Successfully created chat response for conversation {}", conversationId);
            return chatResponse;
        } catch (Exception e) {
            log.error("Error processing message for conversation {}: {}", conversationId, e.getMessage(), e);
            throw e;
        }
    }
}
