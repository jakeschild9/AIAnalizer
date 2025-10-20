package edu.missouristate.aianalyzer.service.ai;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;

import static edu.missouristate.aianalyzer.service.ai.ReadImage.convertImageToBytes;

@Service
@RequiredArgsConstructor
public class ProcessImage {
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

    public String processImageAiResoponse(Path filePath, String fileType) throws IOException {
        if (!Files.exists(filePath)) {
            return "File does not exist: " + filePath;
        }
        fileSize = filePath.toFile().length();
        try {
            if (fileSize <= maxFileSize) {
                return processSmallImageAIResponse(filePath, fileType);
            } else {
                return null;
            }
        } catch (IOException e) {
            return "Error processing file: " + e.getMessage();
        }
    }

    private String processSmallImageAIResponse(Path filePath, String fileType) throws IOException {
        byte[] imageBytes = convertImageToBytes(filePath, fileType);

        String image = new String(imageBytes, StandardCharsets.UTF_8);

        return AiQuery.respondWithImageCategory(image);
    }
}
