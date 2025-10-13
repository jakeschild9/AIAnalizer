package edu.missouristate.aianalyzer;

import ch.qos.logback.classic.Level;
import ch.qos.logback.classic.Logger;
import ch.qos.logback.classic.spi.ILoggingEvent;
import ch.qos.logback.core.read.ListAppender;
import edu.missouristate.aianalyzer.config.ServiceLoggingAspect;
import edu.missouristate.aianalyzer.model.FileInterpretation;
import edu.missouristate.aianalyzer.service.ai.AiQuery;
import edu.missouristate.aianalyzer.service.ai.ProcessFile;
import edu.missouristate.aianalyzer.service.database.PassiveScanService;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.bean.override.mockito.MockitoBean;

import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.anyString;
import static org.slf4j.LoggerFactory.getLogger;
@SpringBootTest
@ActiveProfiles("test")
class AiAnalyzerApplicationTests {

    @Autowired
    private ProcessFile processFile;

    // Prevent watcher
    @MockitoBean
    private PassiveScanService passiveScanService;

    // Mock AI calls
    @MockitoBean
    private AiQuery aiQuery;

    private ListAppender<ILoggingEvent> appender;

    private void attachAspectListAppender() {
        Logger aspectLogger = (Logger) getLogger(ServiceLoggingAspect.class);
        aspectLogger.setLevel(Level.DEBUG); // ensure DEBUG visible
        appender = new ListAppender<>();
        appender.start();
        aspectLogger.addAppender(appender);
    }

    @AfterEach
    void tearDown() {
        Logger aspectLogger = (Logger) getLogger(ServiceLoggingAspect.class);
        aspectLogger.detachAndStopAllAppenders();
    }

    @Test
    void logsTimingOnSuccess() throws Exception {
        attachAspectListAppender();

        Path tmp = Files.createTempFile("aianalyzer-", ".txt");
        Files.writeString(tmp, "hello world");
        tmp.toFile().deleteOnExit();
        Mockito.when(aiQuery.respondWithFileDescription(anyString())).thenReturn("summary");
        Mockito.when(aiQuery.responseForLargeFileChunks(anyString())).thenReturn("Safe");
        Mockito.when(aiQuery.activeResponseFromFile(anyString())).thenReturn("Safe|summary");

        String result = processFile.processFileAIResponse(
                tmp, "txt", FileInterpretation.SearchType.ACTIVE
        );

        assertThat(result).isNotBlank();

        List<ILoggingEvent> events = appender.list;
        boolean hasTiming = events.stream()
                .anyMatch(e -> e.getLevel() == Level.DEBUG
                        && e.getFormattedMessage().contains("Service")
                        && e.getFormattedMessage().contains("took")
                        && e.getFormattedMessage().contains("ms"));
        assertThat(hasTiming).as("aspect should log time at DEBUG").isTrue();
    }

    @Test
    void logsErrorWhenAiThrows() throws Exception {
        attachAspectListAppender();

        Path tmp = Files.createTempFile("aianalyzer-", ".txt");
        Files.writeString(tmp, "boom");
        tmp.toFile().deleteOnExit();
        Mockito.when(aiQuery.activeResponseFromFile(anyString()))
                .thenThrow(new IllegalStateException("AI offline"));

        assertThatThrownBy(() -> processFile.processFileAIResponse(
                tmp, "txt", FileInterpretation.SearchType.ACTIVE
        )).isInstanceOf(IllegalStateException.class);

        List<ILoggingEvent> events = appender.list;
        ILoggingEvent error = events.stream()
                .filter(e -> e.getLevel() == Level.ERROR)
                .findFirst()
                .orElse(null);

        assertThat(error).isNotNull();
        assertThat(error.getFormattedMessage()).contains("Service error in");
        assertThat(error.getThrowableProxy()).isNotNull();
        assertThat(error.getThrowableProxy().getMessage()).contains("AI offline");
    }
}

//    @Test
//    void testAiProcessing() throws Exception {
//        String result = processFile.processFileAIResponse(
//                testFilePath,
//                "txt",
//                8,
//                FileInterpretation.SearchType.ACTIVE
//                );
//        System.out.println(result);
//    }
