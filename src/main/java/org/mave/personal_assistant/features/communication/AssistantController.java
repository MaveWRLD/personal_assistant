package org.mave.personal_assistant.features.communication;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.mave.personal_assistant.features.communication.dto.ChatRequest;
import org.mave.personal_assistant.features.communication.dto.ChatResponse;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RestController
@RequestMapping("/api/assistant")
@RequiredArgsConstructor
public class AssistantController {

    private final AssistantService assistantService;

    @PostMapping("/chat")
    public ChatResponse chat(@RequestBody ChatRequest request) {
        log.info("Received chat request for conversationId: {}, message length: {}", 
                request.getConversationId(), 
                request.getMessage() != null ? request.getMessage().length() : 0);
        
        try {
            ChatResponse response = assistantService.processUserMessage(
                    request.getConversationId(),
                    request.getMessage()
            );
            
            log.info("Successfully processed chat request for conversationId: {}, response length: {}", 
                    response.getConversationId(), 
                    response.getResponse() != null ? response.getResponse().length() : 0);
            
            return response;
        } catch (Exception e) {
            log.error("Error processing chat request for conversationId: {}: {}", 
                    request.getConversationId(), e.getMessage(), e);
            throw e;
        }
    }
}
