package edu.missouristate.aianalyzer.ui.event;

import javafx.stage.Stage;
import org.springframework.context.ApplicationEvent;

// A simple event we fire off once the main JavaFX window (the Stage) is created.
// Spring beans can listen for this to know when it's safe to start doing UI work.
public class StageReadyEvent extends ApplicationEvent {
    public StageReadyEvent(Stage stage) {
        super(stage);
    }

    public Stage getStage() {
        return ((Stage) getSource());
    }
}