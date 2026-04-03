package org.mave.personal_assistant.features.communication;

import lombok.RequiredArgsConstructor;
import org.mave.personal_assistant.features.communication.AssistantService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/assistant")
@RequiredArgsConstructor
public class AssistantController {

    private static final Logger log = LoggerFactory.getLogger(AssistantController.class);
    private final AssistantService assistantService;

    @PostMapping("/chat")
    public String chat(@RequestBody String message) {
        log.info("Received chat message: {}", message);
        try {
            String response = assistantService.processUserMessage(message);
            log.info("Assistant response generated successfully");
            return response;
        } catch (Exception e) {
            log.error("Error processing chat message: {}", message, e);
            throw e;
        }
    }
}
