package edu.missouristate.aianalyzer.ui;

import edu.missouristate.aianalyzer.ui.event.StageReadyEvent;
import edu.missouristate.aianalyzer.ui.service.ThemeService;
import edu.missouristate.aianalyzer.ui.view.DriveView;
import edu.missouristate.aianalyzer.ui.view.MetricsView;
import edu.missouristate.aianalyzer.ui.view.SettingsView;
import edu.missouristate.aianalyzer.ui.view.SuggestionsView;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.stage.Stage;
import org.springframework.context.ApplicationListener;
import org.springframework.stereotype.Component;


// This class is the main hub for building our UI.
// It waits for the `StageReadyEvent` signal, then pieces together all the different views.
@Component
public class StageInitializer implements ApplicationListener<StageReadyEvent> {

    // All the main pages (views) of our application.
    Scene mainScene;
    private final DriveView driveView;
    private final MetricsView metricsView;
    private final SettingsView settingsView;
    private final SuggestionsView suggestionsView;
    private final ThemeService themeService;

    // Spring sees this constructor and automatically provides us with all the beans we need.
    public StageInitializer(DriveView driveView, MetricsView metricsView, SettingsView settingsView, SuggestionsView suggestionsView, ThemeService themeService) {
        this.driveView = driveView;
        this.metricsView = metricsView;
        this.settingsView = settingsView;
        this.suggestionsView = suggestionsView;
        this.themeService  = themeService ;
    }

    // This is where the magic happens. Once `JavaFxApplication` fires the `StageReadyEvent`,
    // this method runs on the main JavaFX thread.@Override
    public void onApplicationEvent(StageReadyEvent event) {
// Get the stage from the event that was published in JavaFxApplication.java
        Stage primaryStage = event.getStage();

        // The BorderPane is our main layout: a top nav bar and a center content area.
        BorderPane root = new BorderPane();
        mainScene = new Scene(root, 1280, 720);

        // Hand the scene over to the theme service so it knows what to apply styles to.
        themeService.setScene(mainScene);

        // --- Navigation Buttons ---
        Button drivesButton = new Button("Drives");
        Button metricsButton = new Button("Metrics");
        Button suggestionsButton = new Button("Suggestions");
        Button settingsButton = new Button("Settings");

        // --- Button Actions ---
        drivesButton.setOnAction(e -> root.setCenter(driveView));
        metricsButton.setOnAction(e -> root.setCenter(metricsView));
        suggestionsButton.setOnAction(e -> root.setCenter(suggestionsView));
        settingsButton.setOnAction(e -> root.setCenter(settingsView));

        // --- Assemble the Nav Bar ---
        HBox navigationBar = new HBox(10, drivesButton, metricsButton, suggestionsButton, settingsButton);
        navigationBar.setAlignment(Pos.CENTER_LEFT);
        navigationBar.setPadding(new Insets(10));
        navigationBar.setStyle("-fx-background-color: -fx-secondary-bg;");

        // --- Put It All Together ---
        root.setTop(navigationBar);
        root.setCenter(driveView);

        themeService.applyTheme("msu-maroon");

        primaryStage.setTitle("AI Analyzer");
        primaryStage.setScene(mainScene);
        primaryStage.show();
    }

}