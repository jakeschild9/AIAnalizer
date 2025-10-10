package edu.missouristate.aianalyzer.service.ai;


import edu.missouristate.aianalyzer.model.FileInterpretation;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.stereotype.Service;

import java.io.*;
import java.nio.MappedByteBuffer;
import java.nio.channels.FileChannel;
import java.nio.charset.StandardCharsets;
import java.nio.file.*;
import java.util.*;

/**
 * This service is responsible for processing files and interacting with an AI service for analysis.
 * It differentiates between small and large files, processing them in a memory-efficient manner
 * by reading large files in chunks to avoid loading the entire file into memory.
 */
@Service
@RequiredArgsConstructor
public class ProcessFile {
    //AI query service
    private final AiQuery AiQuery;
    //File reading service
    private final ReadFile ReadFile;
    //Size of file
    static long fileSize;
    //Max file size before entering into chunk analysis (5MB)
    static final long maxFileSize = 5 * 1024 * 1024;
    //Size of each chunk for large files (1MB)
    static final long chunkSize = 1024 * 1024;
//    private final InitializingBean classPathFileSystemWatcher;

    /**
     * Determines whether to process the file as small or large based on its size and gets the AI response.
     * @param filePath The path to the file to be processed.
     * @param fileType The type of file being processed.
     * @param searchType The type of AI analysis to perform (ACTIVE or PASSIVE).
     * @return The AI's response as a String, or an error message.
     * @throws IOException If an error occurs during file processing.
     */
    public String processFileAIResponse(Path filePath, String fileType, FileInterpretation.SearchType searchType) throws IOException  {
        if (!Files.exists(filePath)) {
            return "File does not exist: " + filePath;
        }
        fileSize = filePath.toFile().length();
        try {
            if (fileSize <= maxFileSize) {
                return processSmallFileAIResponse(filePath, fileType, searchType);
            } else {
                return processLargeFileAIResponse(filePath, searchType);
            }
        } catch (IOException e) {
            return "Error processing file: " + e.getMessage();
        }
    }

    /**
     * Processes files smaller than or equal to the maxFileSize by reading the entire content into memory.
     * @param filePath The path to the small file.
     * @param fileType The type of file being processed.
     * @param searchType The type of AI analysis to perform.
     * @return The AI's response as a String.
     * @throws IOException If an error occurs while reading the file.
     */
    private String processSmallFileAIResponse(Path filePath, String fileType, FileInterpretation.SearchType searchType) throws IOException {
        try {
            String fileContent = ReadFile.readFileAsString(filePath, fileType);

            if (searchType == FileInterpretation.SearchType.ACTIVE) {
                return AiQuery.activeResponseFromFile(fileContent);
            } else {
                return AiQuery.passiveResponseFromFile(fileContent);
            }
        } catch (IOException e) {
            return "Error processing file: " + e.getMessage();
        }
    }

    /**
     * Processes files larger than maxFileSize by reading them in sequential, memory-mapped chunks.
     * It analyzes each chunk and can short-circuit if a non-"Safe" classification is found.
     * @param filePath The path to the large file.
     * @param searchType The type of AI analysis to perform.
     * @return The normalized AI response, combining classification and description.
     * @throws IOException If an I/O error occurs.
     */
    private String processLargeFileAIResponse(Path filePath, FileInterpretation.SearchType searchType) throws IOException {
        String fileDescription = "";
        String classification = "";
        String chunk;
        long fileSize = filePath.toFile().length();
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
     * A static helper method to format the AI's classification and description into a single, delimited string.
     * @param description The textual description from the AI.
     * @param classification The classification category from the AI.
     * @return A single string in the format "classification|description".
     */
    static String normalizeResponse(String description, String classification) {
        return classification + "|" + description;
    }
}
