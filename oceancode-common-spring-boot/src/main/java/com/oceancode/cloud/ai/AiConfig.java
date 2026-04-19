package com.oceancode.cloud.ai;

import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.prompt.PromptTemplate;
import org.springframework.ai.openai.OpenAiChatModel;
import org.springframework.ai.tool.ToolCallback;
import org.springframework.ai.tool.function.FunctionToolCallback;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;

import java.util.List;

@Configuration
@ConditionalOnClass(PromptTemplate.class)
public class AiConfig {

    @Bean
    @Primary
    public ChatClient mainChatClient(OpenAiChatModel chatModel, List<ToolCallback> callbacks) {
        // https://blog.csdn.net/qq_39805994/article/details/160009661
        return ChatClient
                .builder(chatModel)
                .defaultToolCallbacks(callbacks)
                .build();
    }
}
