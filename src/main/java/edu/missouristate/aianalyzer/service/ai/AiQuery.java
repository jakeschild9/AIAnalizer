package edu.missouristate.aianalizer.service;

import com.google.genai.Client;
import com.google.genai.types.GenerateContentResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

/**
 * Service responsible for making API calls to the Google Gemini AI model.
 * It provides various methods to get different types of analysis on file content.
 */
@Service
@RequiredArgsConstructor
public class AiQuery {
    private final Client client;

    /**
     * Sends the entire file content to the AI for a "PASSIVE" analysis.
     * The AI will classify the file as Malicious, Suspicious, or Safe and provide a reason if not safe.
     * @param file The complete content of the file as a string.
     * @return The AI's classification and optional reasoning, separated by a '|' to split data.
     */
    public String passiveResponseFromFile(String file) {
        GenerateContentResponse response =
                client.models.generateContent(
                        "gemini-2.5-flash",
                        "Indicate if this file is potentially Malicious, Suspicious, or Safe by if it" +
                                "contains actual code that can exploit a vulnerability in the system." +
                                "If Malicious or Suspicious, explain the reasons in a sentence. Put the " +
                                "classification before the reasons with a '|' separating them. If safe only" +
                                "output the word 'Safe':" + file,
                        null);
        return response.text();
    }

    /**
     * Sends the entire file content to the AI for an "ACTIVE" analysis.
     * The AI will provide a summary of the document and a security classification.
     * @param file The complete content of the file as a string.
     * @return The AI's classification and summary, separated by a '|' to split data.
     */
    public String activeResponseFromFile(String file) {
        GenerateContentResponse response =
                client.models.generateContent(
                        "gemini-2.5-flash",
                        "Provide a single, up to 40-word sentence summarizing the main point or summary of the following " +
                                "file content. " +
                                "Indicate if it is potentially Malicious, Suspicious, or Safe if it contains actual" +
                                "code that can exploit a vulnerability in the system." +
                                "Place the classification before the sentence with a single '|' in between them:" + file,
                        null);
        return response.text();
    }

    /**
     * Sends a chunk of a large file to the AI for a quick security classification.
     * This is optimized for speed, requesting only a one-word response.
     * @param chunk A string representing a portion of a large file.
     * @return A one-word classification: "Malicious", "Suspicious", or "Safe".
     */
    public String responseForLargeFileChunks(String chunk) {
        GenerateContentResponse response =
                client.models.generateContent(
                        "gemini-2.5-flash",
                        "Indicate if this file is potentially Malicious, Suspicious, or Safe if it contains" +
                                "actual code that can exploit a vulnerability in the system." +
                                "Respond with one word from the previous classifications:" + chunk,
                        null);
        return response.text();
    }

    /**
     * Sends file content to the AI to get a descriptive summary.
     * This is used for generating descriptions for both active searches and for non-safe files.
     * @param file A string representing the content or a chunk of a file.
     * @return A single sentence summarizing the file's main point.
     */
    public String respondWithFileDescription(String file) {
        GenerateContentResponse response =
                client.models.generateContent(
                        "gemini-2.5-flash",
                        "Provide a single, up to 40-word sentence summarizing the main point or contents of this file:" +
                                file,
                        null);
        return response.text();
    }
}
