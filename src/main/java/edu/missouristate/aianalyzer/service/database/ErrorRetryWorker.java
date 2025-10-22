package edu.missouristate.aianalyzer.service.database;

import edu.missouristate.aianalyzer.model.database.ErrorLog;
import edu.missouristate.aianalyzer.model.FileInterpretation;
import edu.missouristate.aianalyzer.service.ai.ProcessFile;
import edu.missouristate.aianalyzer.service.database.ErrorLogService;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Locale;

@Component
public class ErrorRetryWorker {
    private final ErrorLogService errorLogService;
    private final ProcessFile processFile;

    public ErrorRetryWorker(ErrorLogService errorLogService, ProcessFile processFile) {
        this.errorLogService = errorLogService;
        this.processFile = processFile;
    }

    @Scheduled(fixedDelay = 60_000)
    public void retryHighPriority() {
        for (ErrorLog e : errorLogService.pendingHighPriority(50)) {
            final String pathStr = e.getFilePath();
            if (pathStr == null || pathStr.isBlank()) continue;

            errorLogService.markRetrying(e.getId());
            boolean ok = attempt(pathStr);
            if (ok) {
                errorLogService.markResolved(e.getId());
            }
        }
    }

    private boolean attempt(String pathStr) {
        try {
            Path p = Path.of(pathStr);

            // Derive the required fileType
            String fileType = Files.probeContentType(p);
            if (fileType == null) fileType = ""; // ProcessFile handles routing

            // Use ACTIVE for retries
            String result = processFile.processFileAIResponse(
                    p,
                    fileType,
                    FileInterpretation.SearchType.ACTIVE
            );

            if (result == null) return false;

            String lower = result.toLowerCase(Locale.ROOT);
            if (lower.startsWith("error processing file") || lower.startsWith("file does not exist")) {
                return false;
            }
            return true;
        } catch (Exception ex) {
            return false;
        }
    }
}

