package edu.missouristate.aianalyzer.ui;

import edu.missouristate.aianalyzer.AiAnalyzerApplication;
import edu.missouristate.aianalyzer.ui.event.StageReadyEvent;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.stage.Stage;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.context.ConfigurableApplicationContext;

// This class acts as the bridge between the JavaFX UI world and the Spring backend world.
// The main() method in AiAnalyzerApplication kicks this off.
public class JavaFxApplication extends Application {
    private ConfigurableApplicationContext applicationContext;

    // 1. First, JavaFX calls this init() method.
    // We use it to start up the entire Spring application in the background.
    @Override
    public void init() {
        // Start Spring.
        // - triggered by the Application.launch() call in AiAnalyzerApplication.
        applicationContext = new SpringApplicationBuilder(AiAnalyzerApplication.class)
                .headless(false) // This tells Spring that a UI will be present.
                .run();
    }

    // 2. After init() is done, JavaFX calls start(), giving us the main window (Stage).
    // We don't build the UI here directly. Instead, we fire an event to let a Spring
    // bean (StageInitializer) know that it's time to take over and build the scene.
    @Override
    public void start(Stage stage) {
        System.out.println("JavaFxApplication.start(): Firing StageReadyEvent...");
        applicationContext.publishEvent(new StageReadyEvent(stage));
    }

    // 3. When the user closes the window, this method is called.
    // We make sure to shut down Spring and the JavaFX platform gracefully.
    @Override
    public void stop() {
        applicationContext.close();
        Platform.exit();
    }
}