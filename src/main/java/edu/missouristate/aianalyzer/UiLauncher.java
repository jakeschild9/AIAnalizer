package edu.missouristate.aianalyzer;

import edu.missouristate.aianalyzer.view.DriveView;
import edu.missouristate.aianalyzer.view.MetricsView;
import edu.missouristate.aianalyzer.view.SettingsView;
import edu.missouristate.aianalyzer.view.SuggestionsView;
import javafx.application.Application;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.stage.Stage;

import java.net.URL;

public class UiLauncher extends Application {

    private Scene mainScene;
    private BorderPane root;

    // Make one variable for each of our main UI pages.
    private Node driveView;
    private Node metricsView;
    private Node settingsView;
    private Node suggestionsView;

    @Override
    public void start(Stage primaryStage) throws Exception {
        root = new BorderPane();
        mainScene = new Scene(root, 1280, 720);

        // --- Set up all the different screens for the app ---
        // We pass 'this' (the UiLauncher itself) to the views so they can call methods here, like applyTheme().
        driveView = new DriveView(this);
        metricsView = new MetricsView();
        suggestionsView = new SuggestionsView();
        settingsView = new SettingsView(this);

        // --- Make the buttons for the top navigation bar ---
        Button drivesButton = new Button("Drives");
        Button metricsButton = new Button("Metrics");
        Button suggestionsButton = new Button("Suggestions");
        Button settingsButton = new Button("Settings");

        // --- Tell the buttons what to do when they are clicked ---
        // When a button is clicked, it just swaps out the center part of our BorderPane.
        drivesButton.setOnAction(e -> root.setCenter(driveView));
        metricsButton.setOnAction(e -> root.setCenter(metricsView));
        suggestionsButton.setOnAction(e -> root.setCenter(suggestionsView));
        settingsButton.setOnAction(e -> root.setCenter(settingsView));

        // Put all the nav buttons into a horizontal box (HBox).
        HBox navigationBar = new HBox(10, drivesButton, metricsButton, suggestionsButton, settingsButton);
        navigationBar.setAlignment(Pos.CENTER_LEFT);
        navigationBar.setPadding(new Insets(10));
        navigationBar.setStyle("-fx-background-color: -fx-secondary-bg;");

        // --- Put the pieces together in the main window layout ---
        root.setTop(navigationBar);
        root.setCenter(driveView); // The DriveView is the first thing the user sees.

        // Load our default theme when the app starts.
        applyTheme("msu-maroon");

        primaryStage.setTitle("AI Analyzer");
        primaryStage.setScene(mainScene);
        primaryStage.show();
    }

    /**
     * Changes the app's look and feel by loading a different CSS file.
     * It first clears any old styles, then loads a common style file,
     * and finally loads the new theme on top.
     *
     * @param themeName The name of the theme, like "msu-maroon" (don't include ".css").
     */
    public void applyTheme(String themeName) {
        if (mainScene == null) return; // Can't do anything if the scene isn't ready.
        mainScene.getStylesheets().clear();

        // 1. First, load common.css, which has styles that apply to all themes.
        String commonCssPath = "/styles/common.css";
        URL commonUrl = getClass().getResource(commonCssPath);
        if (commonUrl != null) {
            mainScene.getStylesheets().add(commonUrl.toExternalForm());
        } else {
            System.err.println("CRITICAL ERROR: Could not find common.css");
        }

        // 2. Now, add the specific theme CSS file on top of the common styles.
        String themeCssPath = "/styles/" + themeName + ".css";
        URL themeUrl = getClass().getResource(themeCssPath);

        if (themeUrl != null) {
            mainScene.getStylesheets().add(themeUrl.toExternalForm());
            System.out.println("Theme switched to: " + themeName);
        } else {
            System.err.println("Error: Could not find theme stylesheet: " + themeCssPath);
        }
    }

    public static void main(String[] args) {
        launch(args);
    }
}