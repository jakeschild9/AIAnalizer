package edu.missouristate.aianalizer.service;

import edu.missouristate.aianalizer.model.FileInterpretation;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.io.RandomAccessFile;
import java.nio.MappedByteBuffer;
import java.nio.channels.FileChannel;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Objects;

/**
 * This service is responsible for processing files and interacting with an AI service for analysis.
 * It differentiates between small and large files, processing them in a memory-efficient manner
 * by reading large files in chunks to avoid loading the entire file into memory.
 */
@Service
@RequiredArgsConstructor
public class ProcessFile {
    // Dependency for making queries to the AI service.
    private final AiQuery AiQuery;

    // The maximum file size threshold (75 MB) to be processed in a single read.
    static final long maxFileSize = 75 * 1024 * 1024;

    // The size of chunks (75 MB) for reading large files.
    static final long chunkSize = 75 * 1024 * 1024;

    /**
     * Determines whether to process the file as small or large based on its size and gets the AI response.
     * @param filePath The path to the file to be processed.
     * @param fileSize The size of the file in bytes.
     * @param searchType The type of AI analysis to perform (ACTIVE or PASSIVE).
     * @return The AI's response as a String, or an error message.
     * @throws IOException If an error occurs during file processing.
     */
    public String processFileAIResponse(Path filePath, long fileSize, FileInterpretation.SearchType searchType) throws IOException  {
        if (!Files.exists(filePath)) {
            return "File does not exist: " + filePath;
        }

        try {
            if (fileSize <= maxFileSize) {
                return processSmallFileAIResponse(filePath, searchType);
            } else {
                return processLargeFileAIResponse(filePath, fileSize, searchType);
            }
        } catch (IOException e) {
            return "Error processing file: " + e.getMessage();
        }
    }

    /**
     * Processes files smaller than or equal to the maxFileSize by reading the entire content into memory.
     * @param filePath The path to the small file.
     * @param searchType The type of AI analysis to perform.
     * @return The AI's response as a String.
     * @throws IOException If an error occurs while reading the file.
     */
    private String processSmallFileAIResponse(Path filePath, FileInterpretation.SearchType searchType) throws IOException {
        String fileContent = readFileToString(filePath);

        if (searchType == FileInterpretation.SearchType.ACTIVE) {
            return AiQuery.activeResponseFromFile(fileContent);
        } else {
            return AiQuery.passiveResponseFromFile(fileContent);
        }
    }

    /**
     * Processes files larger than maxFileSize by reading them in sequential, memory-mapped chunks.
     * It analyzes each chunk and can short-circuit if a non-"Safe" classification is found.
     * @param filePath The path to the large file.
     * @param fileSize The size of the file in bytes.
     * @param searchType The type of AI analysis to perform.
     * @return The normalized AI response, combining classification and description.
     * @throws IOException If an I/O error occurs.
     */
    private String processLargeFileAIResponse(Path filePath, long fileSize, FileInterpretation.SearchType searchType) throws IOException {
        String fileDescription = "";
        String classification = "";
        String chunk;
        MappedByteBuffer mappedByteBuffer;

        try (RandomAccessFile file = new RandomAccessFile(filePath.toString(), "r");
             FileChannel channel = file.getChannel()) {

            for (long position = 0; position < fileSize; position += chunkSize) {
                long size = Math.min(chunkSize, fileSize - position);

                mappedByteBuffer = channel.map(FileChannel.MapMode.READ_ONLY, position, size);

                byte[] data = new byte[(int) size];
                mappedByteBuffer.get(data);

                chunk = new String(data, StandardCharsets.UTF_8);

                classification = AiQuery.responseForLargeFileChunks(chunk);

                if (position == 0 && searchType == FileInterpretation.SearchType.ACTIVE) {
                    fileDescription = AiQuery.respondWithFileDescription(chunk);
                } else if (!Objects.equals(classification, "Safe")) {
                    fileDescription = AiQuery.respondWithFileDescription(chunk);
                    return normalizeResponse(fileDescription, classification);
                }
            }
            return normalizeResponse(fileDescription, classification);
        }
    }

    /**
     * A static helper method to read the entire content of a file into a string.
     * @param filePath The path to the file to read.
            * @return The content of the file as a String.
            * @throws IOException If an I/O error occurs reading from the file.
     */
    static String readFileToString(Path filePath) throws IOException {
        return Files.readString(filePath);
    }

    /**
     * A static helper method to format the AI's classification and description into a single, delimited string.
     * @param description The textual description from the AI.
     * @param classification The classification category from the AI.
     * @return A single string in the format "classification%description".
     */
    static String normalizeResponse(String description, String classification) {
        return classification + "%" + description;
    }
}
