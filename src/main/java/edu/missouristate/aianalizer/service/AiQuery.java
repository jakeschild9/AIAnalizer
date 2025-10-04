package edu.missouristate.aianalizer.service;

import com.google.genai.Client;
import com.google.genai.types.GenerateContentResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AiQuery {
    private final Client client;

    public String passiveResponseFromFile(String file) {
        GenerateContentResponse response =
                client.models.generateContent(
                        "gemini-2.5-flash",
                        "Indicate if this file is potentially Malicious, Suspicious, or Safe." +
                                "If Malicious or Suspicious, explain the reasons in a sentence. Put the " +
                                "classification before the reasons with a '%' separating them. If safe only" +
                                "output the word 'Safe':" + file,
                        null);
        return response.text();
    }

    public String activeResponseFromFile(String file) {
        GenerateContentResponse response =
                client.models.generateContent(
                        "gemini-2.5-flash",
                        "Provide a single, up to 40-word sentence summarizing the main point of this document. " +
                                "Indicate if it is potentially Malicious, Suspicious, or Safe." +
                                "Place the classification before the sentence with a single '%' in between them:" + file,
                        null);
        return response.text();
    }

    public String responseForLargeFileChunks(String chunk) {
        GenerateContentResponse response =
                client.models.generateContent(
                        "gemini-2.5-flash",
                        "Indicate if this file is potentially Malicious, Suspicious, or Safe." +
                                "Respond with one word from the previous classifications:" + chunk,
                        null);
        return response.text();
    }

    public String respondWithFileDescription(String file) {
        GenerateContentResponse response =
                client.models.generateContent(
                        "gemini-2.5-flash",
                        "Provide a single, up to 40-word sentence summarizing the main point of this document." +
                                file,
                        null);
        return response.text();
    }
}
