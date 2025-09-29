package edu.missouristate.aianalizer;

import edu.missouristate.aianalizer.view.DriveView;
import edu.missouristate.aianalizer.view.MetricsView;
import edu.missouristate.aianalizer.view.SuggestionsView;
import edu.missouristate.aianalizer.view.SettingsView;
import javafx.application.Application;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.Color;
import javafx.stage.Stage;

import java.net.URI;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;

/**
 * Main app class for AIAnalizer.
 * Sets up UI with nav bar and handles view switching and theme management.
 */
public class UiLauncher extends Application {

    private StackPane contentArea;
    private Scene mainScene;
    private final Map<String, Button> navButtons = new HashMap<>();
    private final Map<String, StackPane> views = new HashMap<>();
    private String currentTheme = "light"; // Default theme

    // --- Theme Definitions ---
    private static final String LIGHT_THEME_CSS = getLightCss();
    private static final String DARK_THEME_CSS = getDarkCss();
    private static final String LIGHT_THEME_URI = getCSSAsURI(LIGHT_THEME_CSS);
    private static final String DARK_THEME_URI = getCSSAsURI(DARK_THEME_CSS);
    // -------------------------

    @Override
    public void start(Stage stage) {
        // A. Initialize Views (instantiate the views and wrap them in a StackPane)
        initializeViews();

        // B. Main Layout: BorderPane for North (Nav) and Center (Content)
        BorderPane root = new BorderPane();

        // C. Create the Navigation Bar
        HBox navBar = createNavBar();
        root.setTop(navBar);

        // D. Create the Content Area (StackPane for easy page switching)
        contentArea = new StackPane();
        contentArea.setPadding(new Insets(20));

        // E. Set initial content (Drive View)
        root.setCenter(contentArea);
        showView("DriveView");

        // F. Create Scene and apply initial theme
        mainScene = new Scene(root, 1000, 700, Color.WHITE);
        applyTheme(currentTheme);

        stage.setTitle("AIAnalizer");
        stage.setScene(mainScene);
        stage.show();
    }

    /**
     * Initializes all view components and adds them to the views map.
     */
    private void initializeViews() {
        // DRIVE VIEW (Home)
        StackPane drivePane = new StackPane(new DriveView(this));
        views.put("DriveView", drivePane);

        // METRICS VIEW
        StackPane metricsPane = new StackPane(new MetricsView());
        views.put("MetricsView", metricsPane);

        // SUGGESTIONS VIEW
        StackPane suggestionsPane = new StackPane(new SuggestionsView());
        views.put("SuggestionsView", suggestionsPane);

        // SETTINGS VIEW
        // Pass the UiLauncher instance to the SettingsView so it can call the theme switcher
        StackPane settingsPane = new StackPane(new SettingsView(this));
        views.put("SettingsView", settingsPane);
    }

    /**
     * Creates the modern horizontal navigation bar and sets up button actions.
     */
    private HBox createNavBar() {
        HBox navBar = new HBox();
        navBar.getStyleClass().add("nav-bar");

        Label appTitle = new Label("AIAnalizer");
        appTitle.getStyleClass().add("title-label");

        // Nav Buttons
        Button homeButton = createNavButton("Home (Scan)", "DriveView");
        Button metricsButton = createNavButton("Metrics", "MetricsView");
        Button suggestionsButton = createNavButton("Suggestions", "SuggestionsView");
        Button settingsButton = createNavButton("Settings", "SettingsView");

        navButtons.put("DriveView", homeButton);
        navButtons.put("MetricsView", metricsButton);
        navButtons.put("SuggestionsView", suggestionsButton);
        navButtons.put("SettingsView", settingsButton);

        // Spacer to push buttons to the right
        HBox spacer = new HBox();
        HBox.setHgrow(spacer, Priority.ALWAYS);

        navBar.getChildren().addAll(appTitle, spacer, homeButton, suggestionsButton, metricsButton, settingsButton);
        return navBar;
    }

    /**
     * Helper method to create a navigation button and set its action.
     */
    private Button createNavButton(String text, String viewKey) {
        Button button = new Button(text);
        button.getStyleClass().add("nav-button");

        button.setOnAction(e -> showView(viewKey));
        return button;
    }

    /**
     * Switches the content area to the specified view.
     */
    public void showView(String viewKey) {
        if (!views.containsKey(viewKey)) {
            System.err.println("Error: View key not found: " + viewKey);
            return;
        }

        // 1. Update Content Area
        contentArea.getChildren().clear();
        contentArea.getChildren().add(views.get(viewKey));

        // 2. Update Nav Button Styling
        navButtons.values().forEach(btn -> btn.getStyleClass().remove("selected"));
        Button currentButton = navButtons.get(viewKey);
        if (currentButton != null) {
            currentButton.getStyleClass().add("selected");
        }
    }

    /**
     * Public method to switch the application's theme.
     */
    public void applyTheme(String theme) {
        if (mainScene == null) return;

        // Remove previous theme stylesheet
        mainScene.getStylesheets().remove(LIGHT_THEME_URI);
        mainScene.getStylesheets().remove(DARK_THEME_URI);

        // Apply the new theme stylesheet
        if ("dark".equalsIgnoreCase(theme)) {
            mainScene.getStylesheets().add(DARK_THEME_URI);
            currentTheme = "dark";
        } else {
            // Defaults to light, includes "light" and "system" (if system is light)
            mainScene.getStylesheets().add(LIGHT_THEME_URI);
            currentTheme = "light";
        }

        // TODO: check OS preference here.
        System.out.println("Theme switched to: " + currentTheme);
    }

    /**
     * Helper function to define the Light Mode CSS.
     */
    private static String getLightCss() {
        return """
            .root {
                -fx-font-family: 'Inter', sans-serif;
                -fx-background-color: #f7f9fa; /* Light background */
            }
            .nav-bar {
                -fx-background-color: #ffffff;
                -fx-padding: 10 20;
                -fx-spacing: 15;
                -fx-alignment: CENTER_LEFT;
                -fx-border-color: #e0e0e0;
                -fx-border-width: 0 0 1 0;
            }
            .nav-button {
                -fx-background-color: transparent;
                -fx-text-fill: #333333;
                -fx-font-size: 14px;
                -fx-font-weight: bold;
                -fx-padding: 8 15;
                -fx-cursor: hand;
                -fx-border-radius: 6px;
                -fx-background-radius: 6px;
                -fx-transition: all 0.2s ease-in-out;
            }
            .nav-button:hover {
                -fx-background-color: #e5e5e5;
            }
            .nav-button.selected {
                -fx-background-color: #4A90E2; /* Blue accent for active page */
                -fx-text-fill: white;
            }
            .title-label {
                -fx-font-size: 24px;
                -fx-font-weight: 800;
                -fx-text-fill: #1a1a1a;
                -fx-padding: 0 40 0 0; 
            }
            .drive-view, .settings-view, .page-container {
                -fx-spacing: 10;
                -fx-padding: 15;
                -fx-background-color: white; /* Card/Panel background */
                -fx-border-radius: 10px;
                -fx-background-radius: 10px;
                -fx-effect: dropshadow(three-pass-box, rgba(0,0,0,0.08), 10, 0, 0, 5);
                -fx-max-width: 960px;
            }
            .header-label {
                -fx-font-size: 18px;
                -fx-font-weight: bold;
                -fx-text-fill: #4A90E2;
            }
            .primary-card {
                -fx-background-color: #4A90E2; /* Blue primary card background */
                -fx-border-color: #387bd6;
                -fx-text-fill: white;
            }
            .secondary-card {
                 -fx-background-color: #f0f4f7; /* Off-white card background */
                 -fx-text-fill: #333;
            }
            .card-title {
                -fx-font-weight: bold;
                -fx-font-size: 14px;
                -fx-text-fill: #333;
            }
            .card-content-label {
                -fx-font-size: 20px;
                -fx-text-fill: #555;
            }
        """;
    }

    /**
     * Helper function to define the Dark Mode CSS.
     */
    private static String getDarkCss() {
        return """
            .root {
                -fx-font-family: 'Inter', sans-serif;
                -fx-background-color: #1a1a1a; /* Dark background */
            }
            .nav-bar {
                -fx-background-color: #2c2c2c; /* Dark nav bar */
                -fx-padding: 10 20;
                -fx-spacing: 15;
                -fx-alignment: CENTER_LEFT;
                -fx-border-color: #3d3d3d;
                -fx-border-width: 0 0 1 0;
            }
            .nav-button {
                -fx-background-color: transparent;
                -fx-text-fill: #ffffff; /* White text */
                -fx-font-size: 14px;
                -fx-font-weight: bold;
                -fx-padding: 8 15;
                -fx-cursor: hand;
                -fx-border-radius: 6px;
                -fx-background-radius: 6px;
                -fx-transition: all 0.2s ease-in-out;
            }
            .nav-button:hover {
                -fx-background-color: #4a4a4a; /* Darker hover */
            }
            .nav-button.selected {
                -fx-background-color: #5d9cec; /* Blue accent for active page */
                -fx-text-fill: white;
            }
            .title-label {
                -fx-font-size: 24px;
                -fx-font-weight: 800;
                -fx-text-fill: #e0e0e0; /* Light title text */
                -fx-padding: 0 40 0 0; 
            }
            .drive-view, .settings-view, .page-container {
                -fx-spacing: 10;
                -fx-padding: 15;
                -fx-background-color: #2c2c2c; /* Dark card/panel background */
                -fx-border-radius: 10px;
                -fx-background-radius: 10px;
                -fx-effect: dropshadow(three-pass-box, rgba(0,0,0,0.4), 10, 0, 0, 5);
                -fx-max-width: 960px;
            }
            .header-label {
                -fx-font-size: 18px;
                -fx-font-weight: bold;
                -fx-text-fill: #5d9cec; /* Lighter blue header */
            }
            .primary-card {
                -fx-background-color: #5d9cec; /* Light blue primary card background */
                -fx-border-color: #387bd6;
                -fx-text-fill: black;
            }
            .secondary-card {
                 -fx-background-color: #3c3c3c; /* Dark secondary card background */
                 -fx-text-fill: #e0e0e0;
            }
            .card-title {
                -fx-font-weight: bold;
                -fx-font-size: 14px;
                -fx-text-fill: #e0e0e0;
            }
            .card-content-label {
                -fx-font-size: 20px;
                -fx-text-fill: #f0f0f0;
            }
        """;
    }

    /**
     * Helper function to embed CSS directly into the application and encode it as a data URI.
     */
    private static String getCSSAsURI(String css) {
        try {
            String encodedCss = URLEncoder.encode(css, StandardCharsets.UTF_8.toString())
                    .replaceAll("\\+", "%20");
            return "data:text/css;charset=utf-8," + encodedCss;
        } catch (Exception e) {
            System.err.println("Error encoding CSS: " + e.getMessage());
            return null; // Should not happen with UTF-8
        }
    }

    public static void main(String[] args) {
        launch(args);
    }
}
