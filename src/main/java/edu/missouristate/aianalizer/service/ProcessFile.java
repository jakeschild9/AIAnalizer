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

@Service
@RequiredArgsConstructor
public class ProcessFile {
    private final AiQuery AiQuery;

    static final long maxFileSize = 75 * 1024 * 1024;
    static final long chunkSize = 75 * 1024 * 1024;

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

    private String processSmallFileAIResponse(Path filePath, FileInterpretation.SearchType searchType) throws IOException {
        String fileContent = readFileToString(filePath);

        if (searchType == FileInterpretation.SearchType.ACTIVE) {
            return AiQuery.activeResponseFromFile(fileContent);
        } else {
            return AiQuery.passiveResponseFromFile(fileContent);
        }
    }

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

    static String readFileToString(Path filePath) throws IOException {
        return Files.readString(filePath);
    }

    static String normalizeResponse(String description, String classification) {
        return classification + "%" + description;
    }
}
