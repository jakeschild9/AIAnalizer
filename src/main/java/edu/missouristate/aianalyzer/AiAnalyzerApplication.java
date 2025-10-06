package edu.missouristate.aianalyzer;

import edu.missouristate.aianalyzer.service.database.ActiveScanService;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.annotation.EnableScheduling;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;

@SpringBootApplication
@EnableScheduling // Enables @Scheduled
@EnableAsync      // Enables @Async
public class AiAnalyzerApplication {
	public static void main(String[] args) {
        SpringApplication.run(AiAnalyzerApplication.class, args);
	}

    /**
     * This bean is our startup script. It runs once when the application launches.
     * It will kick off an ACTIVE scan on the user's Downloads folder.
     */
    @Bean
    CommandLineRunner startBackgroundIndexer(ActiveScanService indexingService) {
        return args -> {
            // For testing, let's just index the user's Test folder. (just create a new \Test folder so this doesn't run forever and crash)
            Path downloadsFolder = Paths.get(System.getProperty("user.home"), "Test");

            System.out.println("=========================================================");
            System.out.println("STARTING ACTIVE FILE SCAN OF: " + downloadsFolder);
            System.out.println("=========================================================");

            indexingService.performActiveScan(List.of(downloadsFolder));
        };
    }
}
