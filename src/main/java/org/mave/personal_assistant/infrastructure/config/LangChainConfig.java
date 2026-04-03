package org.mave.personal_assistant.infrastructure.config;

import org.mave.personal_assistant.features.task_management.TaskManagementTool;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import dev.langchain4j.memory.chat.MessageWindowChatMemory;
import dev.langchain4j.model.chat.ChatModel;
import dev.langchain4j.model.openai.OpenAiChatModel;
import dev.langchain4j.service.AiServices;

@Configuration
public class LangChainConfig {

    @Value("${openAI.apiKey}")
    private String apiKey;

    @Bean
    public ChatModel openAiChatModel(){
        return OpenAiChatModel.builder()
                .apiKey(apiKey)
                .modelName("gpt-4o-mini")
                .build();
    }

    @Bean
    public Assistant personalAssistant(
            ChatModel chatModel,
            TaskManagementTool taskTool) {

        return AiServices.builder(Assistant.class)
                .chatModel(chatModel)
                .tools(taskTool)
                .chatMemory(MessageWindowChatMemory.withMaxMessages(20))
                .build();
    }
}
