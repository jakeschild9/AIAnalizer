package edu.missouristate.aianalizer.config;

import com.google.genai.Client;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * Spring configuration class responsible for creating and providing the Gemini AI client
 * as a managed bean in the application context.
 */
@Configuration
public class AiClient {
    /**
     * Creates and configures the Google Gemini AI client bean.
     * This bean can then be injected into other services that need to interact with the AI.
     * @return A configured instance of the {@link Client}.
     * @throws Exception if an error occurs during the client build process.
     */
    @Bean
    public Client googleGenAiClient() throws Exception {
        return Client.builder().build();
    }
}
