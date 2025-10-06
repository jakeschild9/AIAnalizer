package edu.missouristate.aianalyzer.view;

import edu.missouristate.aianalyzer.UiLauncher;
import javafx.collections.FXCollections;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;

/**
 * Component representing the Settings page.
 * Allows the user to configure application preferences, such as the theme.
 */
public class SettingsView extends VBox {

    private final UiLauncher launcher;

    /**
     * Constructor requires the UiLauncher instance to allow access to the theme switching method.
     */
    public SettingsView(UiLauncher launcher) {
        this.launcher = launcher;
        this.getStyleClass().add("settings-view");
        this.setAlignment(Pos.TOP_LEFT);
        this.setPadding(new Insets(20));

        Label header = new Label("Application Settings");
        header.getStyleClass().add("header-label");

        // --- Theme Selector Setting ---
        Label themeLabel = new Label("Application Theme:");
        themeLabel.setStyle("-fx-font-weight: bold; -fx-text-fill: -fx-text-color;");

        // Dropdown options
        ComboBox<String> themeSelector = new ComboBox<>(FXCollections.observableArrayList(
                "System Default",
                "Light",
                "Dark"
        ));

        // Set initial selection based on the default theme in UiLauncher
        themeSelector.getSelectionModel().select("Light");

        // Add listener to switch theme when a new value is selected
        themeSelector.setOnAction(e -> {
            String selected = themeSelector.getSelectionModel().getSelectedItem();

            // Map selection to the theme switcher method
            if (selected.equals("Dark")) {
                launcher.applyTheme("dark");
            } else {
                // Treats "System Default" and "Light" as the light theme for simplicity
                launcher.applyTheme("light");
            }
        });

        HBox themeRow = new HBox(10);
        themeRow.setAlignment(Pos.CENTER_LEFT);
        themeRow.setPadding(new Insets(10, 0, 10, 0));
        themeRow.getChildren().addAll(themeLabel, themeSelector);

        // --- Layout assembly ---
        this.getChildren().addAll(
                header,
                new Label("\nUser Interface"), // Section break
                themeRow
        );
    }
}
