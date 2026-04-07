package org.mave.personal_assistant.infrastructure.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import dev.langchain4j.memory.ChatMemory;
import dev.langchain4j.memory.chat.ChatMemoryProvider;
import org.mave.personal_assistant.features.note_management.NoteManagementTool;
import org.mave.personal_assistant.features.task_management.TaskManagementTool;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import dev.langchain4j.memory.chat.MessageWindowChatMemory;
import dev.langchain4j.model.chat.ChatModel;
import dev.langchain4j.model.openai.OpenAiChatModel;
import dev.langchain4j.service.AiServices;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

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
            TaskManagementTool taskTool, NoteManagementTool noteTool) {

        return AiServices.builder(Assistant.class)
                .chatModel(chatModel)
                .tools(taskTool, noteTool)
                .chatMemoryProvider(chatMemoryProvider())
                .build();
    }


    @Bean
    public ChatMemoryProvider chatMemoryProvider() {

        Map<String, ChatMemory> memories = new ConcurrentHashMap<>();

        return memoryId ->
                memories.computeIfAbsent(
                        (String) memoryId,
                        id -> MessageWindowChatMemory.withMaxMessages(20)
                );
    }



    @Bean
    public ObjectMapper langChain4jObjectMapper() {
        ObjectMapper mapper = new ObjectMapper();
        mapper.registerModule(new JavaTimeModule());
        return mapper;
    }

}
