package edu.missouristate.aianalizer.config;

import com.google.genai.Client;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class AiClient {

    @Bean
    public Client googleGenAiClient() throws Exception {
        return Client.builder().build();
    }
}
