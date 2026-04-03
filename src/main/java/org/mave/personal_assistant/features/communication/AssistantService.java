package org.mave.personal_assistant.features.communication;

import lombok.RequiredArgsConstructor;
import org.mave.personal_assistant.infrastructure.config.Assistant;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AssistantService {

    private static final Logger log = LoggerFactory.getLogger(AssistantService.class);
    private final Assistant personalAssistant;

    public String processUserMessage(String userMessage) {
        log.debug("Processing user message: {}", userMessage);
        try {
            String response = personalAssistant.chat(userMessage);
            log.debug("Assistant response generated successfully");
            return response;
        } catch (Exception e) {
            log.error("Error processing user message: {}", userMessage, e);
            throw e;
        }
    }
}
