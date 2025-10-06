package edu.missouristate.aianalyzer.view;

import edu.missouristate.aianalyzer.UiLauncher;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.control.Label;
import javafx.scene.layout.HBox;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.TilePane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;

import java.util.List;
import java.util.Map;

/**
 * The settings page for the app. Right now it just handles changing the theme.
 */
public class SettingsView extends VBox {

    private final UiLauncher launcher;

    // Helper to link a theme's name (like "MSU Maroon") to its file (like "msu-maroon.css").
    private record Theme(String displayName, String cssFileName) {}

    // Stores the main colors for each theme so we can make the little preview cards.
    // Order: Primary BG, Secondary BG, Primary Text, Accent
    private static final Map<String, List<String>> THEME_PREVIEW_COLORS = Map.ofEntries(
            Map.entry("msu-maroon", List.of("#5E0009", "#8A000D", "#FFFFFF", "#B3B4B2")),
            Map.entry("monolith", List.of("#1A1A1A", "#333333", "#E0E0E0", "#8C7AE6")),
            Map.entry("clay-slate", List.of("#1C2B32", "#5D737E", "#E7F7F7", "#FF9F43")),
            Map.entry("forest-edge", List.of("#162723", "#4CAF50", "#D4D9D8", "#FFCC00")),
            Map.entry("oceans-depth", List.of("#0E1C2A", "#15263B", "#E0E0E0", "#00BCD4")),
            Map.entry("cloud-atlas", List.of("#F3F7FA", "#FFFFFF", "#2C3E50", "#3498DB")),
            Map.entry("quantum-ether", List.of("#14171A", "#1C2025", "#E5E7EB", "#A293F9")),
            Map.entry("canyon-dusk", List.of("#2C2B29", "#4A4540", "#E9E5E2", "#B85C4F")),
            Map.entry("obsidian", List.of("#000000", "#0F0F0F", "#FFFFFF", "#607D8B")),
            Map.entry("velocity-red", List.of("#1F1112", "#3b2224", "#FAFAFA", "#FF5252")),
            Map.entry("light", List.of("#F4F4F4", "#FFFFFF", "#212121", "#007AFF")),
            Map.entry("dark", List.of("#2B2B2B", "#3C3F41", "#BBBBBB", "#007AFF"))
    );

    public SettingsView(UiLauncher launcher) {
        this.launcher = launcher;
        this.getStyleClass().add("settings-view");
        this.setAlignment(Pos.TOP_LEFT);
        this.setPadding(new Insets(20));

        Label header = new Label("Application Settings");
        header.getStyleClass().add("header-label");

        Label themeHeader = new Label("Application Theme:");
        themeHeader.getStyleClass().add("settings-label");

        // List of all the themes we want to show.
        List<Theme> themes = List.of(
                new Theme("MSU Maroon", "msu-maroon"),
                new Theme("Monolith", "monolith"),
                new Theme("Clay & Slate", "clay-slate"),
                new Theme("Forest Edge", "forest-edge"),
                new Theme("Ocean's Depth", "oceans-depth"),
                new Theme("Cloud Atlas", "cloud-atlas"),
                new Theme("Quantum Ether", "quantum-ether"),
                new Theme("Canyon Dusk", "canyon-dusk"),
                new Theme("Obsidian", "obsidian"),
                new Theme("Velocity Red", "velocity-red"),
                new Theme("Light", "light"),
                new Theme("Dark", "dark")
        );

        // Use a TilePane to create a nice grid for the theme cards.
        TilePane themeGrid = new TilePane();
        themeGrid.setHgap(15);
        themeGrid.setVgap(15);
        themeGrid.setPadding(new Insets(10, 0, 10, 0));

        // Go through each theme and create a preview card for it.
        for (Theme theme : themes) {
            Node themeCard = createThemeCard(theme);
            themeGrid.getChildren().add(themeCard);
        }

        // Add everything to the main VBox for this view.
        this.getChildren().addAll(
                header,
                new Label("\nUser Interface"),
                themeHeader,
                themeGrid
        );
    }

    /**
     * Builds a single theme preview card that the user can click.
     */
    private Node createThemeCard(Theme theme) {
        // Grab the four main colors for this theme to build the preview.
        List<String> previewColors = THEME_PREVIEW_COLORS.get(theme.cssFileName());
        Color primaryBg = Color.web(previewColors.get(0));
        Color secondaryBg = Color.web(previewColors.get(1));
        Color primaryText = Color.web(previewColors.get(2));
        Color accent = Color.web(previewColors.get(3));

        // The main background of the little preview window.
        Rectangle cardBg = new Rectangle(160, 80);
        cardBg.setFill(primaryBg);
        cardBg.setArcWidth(10);
        cardBg.setArcHeight(10);

        // A little header bar for the preview.
        Rectangle headerBar = new Rectangle(160, 20);
        headerBar.setFill(secondaryBg);

        // Fake "text lines" to make it look like a real window.
        Rectangle textLine1 = new Rectangle(80, 6);
        textLine1.setFill(primaryText);
        Rectangle textLine2 = new Rectangle(120, 6);
        textLine2.setFill(primaryText);

        // A fake "button" to show the accent color.
        Rectangle button = new Rectangle(50, 15);
        button.setFill(accent);
        button.setArcWidth(5);
        button.setArcHeight(5);

        // Arrange the fake text and button inside a content pane.
        VBox cardContent = new VBox(5);
        cardContent.setPadding(new Insets(28, 15, 10, 15));
        cardContent.getChildren().addAll(textLine1, textLine2, button);

        // Use a StackPane to layer all the preview parts on top of each other.
        StackPane preview = new StackPane();
        preview.getChildren().addAll(cardBg, headerBar, cardContent);
        StackPane.setAlignment(headerBar, Pos.TOP_CENTER);

        // The label that goes under the preview card.
        Label themeNameLabel = new Label(theme.displayName());
        themeNameLabel.getStyleClass().add("theme-card-label");

        // The final clickable card, which holds the preview and the label.
        VBox finalCard = new VBox(10);
        finalCard.setPrefWidth(180);
        finalCard.setAlignment(Pos.CENTER);
        finalCard.getStyleClass().add("theme-card");
        finalCard.getChildren().addAll(preview, themeNameLabel);

        // When the card is clicked, apply the new theme.
        finalCard.setOnMouseClicked(e -> launcher.applyTheme(theme.cssFileName()));

        return finalCard;
    }
}