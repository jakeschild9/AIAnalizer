package edu.missouristate.aianalyzer;

import java.nio.file.Path;

import edu.missouristate.aianalyzer.model.FileInterpretation;
import edu.missouristate.aianalyzer.service.ai.ProcessFile;
import edu.missouristate.aianalyzer.service.database.PassiveScanService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;

@SpringBootTest
class AiAnalyzerApplicationTests {
    @Autowired
    private ProcessFile processFile;

    // Add these lines to replace the real services with fakes (prevent hanging of startMonitoring(), even though it's async it has a watcher that silently hangs)
    @MockitoBean
    private PassiveScanService passiveScanService;


    Path testFilePath = Path.of("D:\\Test\\testfile.txt");

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
}
