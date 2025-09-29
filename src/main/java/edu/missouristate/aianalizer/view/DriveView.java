package edu.missouristate.aianalizer.view;

import edu.missouristate.aianalizer.UiLauncher;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Label;
import javafx.scene.control.TreeItem;
import javafx.scene.control.TreeView;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;
import javafx.scene.control.SplitPane;
import javafx.scene.layout.TilePane;
import javafx.scene.Node;

import java.io.File;
import java.util.Map;

/**
 * Component representing the Home/Scan/Category page.
 * Displays a dual-pane view: system drives on the left, and AI generated categories on the right.
 */
public class DriveView extends SplitPane {

    // Define colors for the AI category cards to ensure they look good in both light and dark mode.
    // The theme CSS will handle text color contrast.
    private static final Map<String, String> CATEGORY_COLORS = Map.of(
            "Photos", "#4A90E2", // Blue
            "Videos", "#2ecc71", // Emerald Green
            "Archives", "#f1c40f", // Yellow
            "Documents", "#9b59b6", // Amethyst
            "Duplicated", "#e74c3c", // Red
            "Unused", "#34495e", // Dark Blue/Gray
            "System", "#7f8c8d", // Gray
            "Games", "#e67e22", // Orange
            "Temp/Cache", "#1abc9c", // Turquoise
            "Unclassified", "#bdc3c7" // Light Gray
    );

    public DriveView(UiLauncher uiLauncher) {
        this.getStyleClass().add("page-container");
        this.setPadding(Insets.EMPTY); // Padding handled by the VBox containers

        // A. Create the Left Panel (Drive Tree)
        VBox driveTreePanel = createDriveTreePanel();

        // B. Create the Right Panel (AI Categories)
        VBox categoryPanel = createCategoryPanel();

        // C. Assemble the SplitPane
        this.getItems().addAll(driveTreePanel, categoryPanel);
        this.setDividerPositions(0.40); // 40% for the tree, 60% for categories

        // D. Apply a style class to the DriveView itself
        this.getStyleClass().add("drive-split-view");
        HBox.setHgrow(this, Priority.ALWAYS);
        VBox.setVgrow(this, Priority.ALWAYS);
    }

    /**
     * Creates the left panel containing the expandable system drive
     */
    private VBox createDriveTreePanel() {
        VBox panel = new VBox(10);
        panel.setAlignment(Pos.TOP_LEFT);
        panel.setPadding(new Insets(15));

        // This panel gets a slight shadow/border to distinguish it as a separate section
        panel.setStyle("-fx-background-color: -fx-view-bg; -fx-padding: 15;");

        Label header = new Label("System Drives");
        header.getStyleClass().add("header-label");

        TreeView<String> treeView = new TreeView<>();
        treeView.getStyleClass().add("tree-view");

        TreeItem<String> rootItem = new TreeItem<>("My Computer");
        rootItem.setExpanded(true);
        treeView.setRoot(rootItem);
        treeView.setShowRoot(true);
        VBox.setVgrow(treeView, Priority.ALWAYS);

        // Dynamically add actual system drives
        for (File drive : File.listRoots()) {
            String driveName = drive.getAbsolutePath();
            long totalSpace = drive.getTotalSpace();
            long usableSpace = drive.getUsableSpace();

            String labelText;
            if (totalSpace > 0) {
                labelText = String.format("%s (%.1f GB Free / %.1f GB Total)",
                        driveName,
                        usableSpace / (1024.0 * 1024 * 1024),
                        totalSpace / (1024.0 * 1024 * 1024));
            } else {
                labelText = driveName + " (Unavailable or No Media)";
            }

            TreeItem<String> driveItem = new TreeItem<>(labelText);
            rootItem.getChildren().add(driveItem);

            // Placeholder for sub-folders/scan status
            if (totalSpace > 0) {
                driveItem.getChildren().add(new TreeItem<>("more folders..."));
            }
        }

        panel.getChildren().addAll(header, treeView);
        return panel;
    }

    /**
     * Creates the right panel containing AI suggested file categories as visual cards.
     */
    private VBox createCategoryPanel() {
        VBox panel = new VBox(10);
        panel.setAlignment(Pos.TOP_LEFT);
        panel.setPadding(new Insets(15));

        Label header = new Label("File Categories");
        header.getStyleClass().add("header-label");

        // Use TilePane for a flowing, wrap-around layout of the cards
        TilePane categoryGrid = new TilePane();
        categoryGrid.setHgap(10);
        categoryGrid.setVgap(10);
        categoryGrid.setPadding(new Insets(10, 0, 0, 0));
        VBox.setVgrow(categoryGrid, Priority.ALWAYS);

        // Placeholder AI Category Cards (Title, Subtitle, Color)
        categoryGrid.getChildren().addAll(
                createCategoryCard("Photos", "24,561 files", CATEGORY_COLORS.get("Photos")),
                createCategoryCard("Videos", "283 GM", CATEGORY_COLORS.get("Videos")),
                createCategoryCard("Archived Docs", "1.2 GB of ZIP/RAR", CATEGORY_COLORS.get("Archives")),
                createCategoryCard("PDFs/Books", "345 documents", CATEGORY_COLORS.get("Documents")),
                createCategoryCard("Duplicated", "4.1 GB waste", CATEGORY_COLORS.get("Duplicated")),
                createCategoryCard("Unused (5y+)", "Oldest file: 2012", CATEGORY_COLORS.get("Unused")),
                createCategoryCard("System Logs", "Ready for cleanup", CATEGORY_COLORS.get("System")),
                createCategoryCard("Game", "25 games found", CATEGORY_COLORS.get("Games")),
                createCategoryCard("Temp Data", "Clearable Cache", CATEGORY_COLORS.get("Temp/Cache")),
                createCategoryCard("Unclassified", "Needs AI review", CATEGORY_COLORS.get("Unclassified"))
        );

        panel.getChildren().addAll(header, categoryGrid);
        return panel;
    }

    /**
     * Helper method to create a stylized category card with a theme-aware design.
     */
    private Node createCategoryCard(String title, String subtitle, String color) {
        VBox cardContent = new VBox(5);
        cardContent.setAlignment(Pos.TOP_LEFT);
        cardContent.setPadding(new Insets(15));
        VBox.setVgrow(cardContent, Priority.ALWAYS);

        // Title (Main Category Name)
        Label titleLabel = new Label(title);
        titleLabel.setStyle("-fx-font-weight: 800; -fx-font-size: 16px; -fx-text-fill: white;");

        // Subtitle (Metric/Actionable Info)
        Label subtitleLabel = new Label(subtitle);
        subtitleLabel.setStyle("-fx-font-size: 12px; -fx-text-fill: white; -fx-opacity: 0.8;");
        VBox.setVgrow(subtitleLabel, Priority.ALWAYS);

        cardContent.getChildren().addAll(titleLabel, subtitleLabel);

        VBox card = new VBox(cardContent);
        card.setPrefSize(180, 100); // Standard card size
        card.getStyleClass().add("category-card"); // Add class for generic styling

        // Apply unique background color and styling
        card.setStyle(String.format(
                "-fx-background-color: %s; " +
                        "-fx-background-radius: 10px; " +
                        "-fx-border-radius: 10px; " +
                        "-fx-cursor: hand;", color));

        return card;
    }
}
