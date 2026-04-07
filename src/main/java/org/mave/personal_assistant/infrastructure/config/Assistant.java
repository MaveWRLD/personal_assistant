package org.mave.personal_assistant.infrastructure.config;

import dev.langchain4j.service.MemoryId;
import dev.langchain4j.service.SystemMessage;
import dev.langchain4j.service.UserMessage;
import dev.langchain4j.service.spring.AiService;

import java.util.UUID;

@AiService
public interface Assistant {

    @SystemMessage("""
        You are a smart personal assistant.

        Your responsibilities:
        - Understand the user's intent.
        - Decide whether to respond directly or use an available tool.
        - Prefer using tools when an action is required.
        - Extract necessary parameters for tool usage.
        - Ask for clarification if required information is missing.
        - Be concise and helpful.
        Also the user might misspell words so handle typos.
        Do not assume a specific domain unless the user's request indicates it.
    """)
    String chat(@MemoryId String memoryId, @UserMessage String message);
}
