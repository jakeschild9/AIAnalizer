package edu.missouristate.aianalyzer.ui;

import edu.missouristate.aianalyzer.AiAnalyzerApplication;
import edu.missouristate.aianalyzer.ui.event.StageReadyEvent;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.stage.Stage;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.context.ConfigurableApplicationContext;

// This is the main entry point for the JavaFX part of the app.
// It's what connects the UI to the Spring backend.
public class JavaFxApplication extends Application {
    private ConfigurableApplicationContext applicationContext;

    // 1. JavaFX calls this `init()` method first.
    // I'm using it to boot up the whole Spring application in the background.
    @Override
    public void init() {
        applicationContext = new SpringApplicationBuilder(AiAnalyzerApplication.class)
                .headless(false) // This is important, it tells Spring we have a UI.
                .run();
    }

    // 2. After `init()` finishes, JavaFX gives us the main window (the 'Stage').
    // Instead of building the UI here, I just fire an event to let a Spring class
    // (the StageInitializer) know that the UI is ready to be built.
    @Override
    public void start(Stage stage) {
        System.out.println("JavaFxApplication.start(): Firing StageReadyEvent...");
        applicationContext.publishEvent(new StageReadyEvent(stage));
    }

    // 3. This gets called when the user closes the window.
    // It's just to make sure Spring and JavaFX shut down cleanly.
    @Override
    public void stop() {
        applicationContext.close();
        Platform.exit();
    }
}
