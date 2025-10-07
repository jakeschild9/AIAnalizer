package edu.missouristate.aianalyzer.view;

import edu.missouristate.aianalyzer.UiLauncher;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Label;
import javafx.scene.control.TreeItem;
import javafx.scene.control.TreeView;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;
import javafx.scene.control.SplitPane;
import javafx.scene.Node;
import java.io.File;
import javafx.scene.layout.TilePane;

/**
 * The main "Drives" page that shows system drives on the left
 * and categorized file summaries on the right.
 */
public class DriveView extends SplitPane {

    public DriveView(UiLauncher uiLauncher) {
        // --- Create the left (drive list) and right (categories) panels ---
        VBox driveTreePanel = createDriveTreePanel();
        VBox categoryPanel = createCategoryPanel();

        // --- Add both panels into the SplitPane layout ---
        this.getItems().addAll(driveTreePanel, categoryPanel);
        this.setDividerPositions(0.40);
        this.getStyleClass().add("drive-split-view");

        // Make sure it expands to fill available space.
        HBox.setHgrow(this, Priority.ALWAYS);
        VBox.setVgrow(this, Priority.ALWAYS);
    }

    /**
     * Builds the left panel that lists all system drives.
     */
    private VBox createDriveTreePanel() {
        VBox panel = new VBox(10);
        panel.setAlignment(Pos.TOP_LEFT);
        panel.getStyleClass().add("drive-tree-panel");

        Label header = new Label("System Drives");
        header.getStyleClass().add("header-label");

        // --- Tree view setup ---
        TreeView<String> treeView = new TreeView<>();
        treeView.getStyleClass().add("tree-view");

        TreeItem<String> rootItem = new TreeItem<>("My Computer");
        rootItem.setExpanded(true);
        treeView.setRoot(rootItem);
        treeView.setShowRoot(true);
        VBox.setVgrow(treeView, Priority.ALWAYS);

        // --- Add each drive to the tree ---
        for (File drive : File.listRoots()) {
            String driveName = drive.getAbsolutePath();
            long totalSpace = drive.getTotalSpace();
            long usableSpace = drive.getUsableSpace();

            String labelText = totalSpace > 0
                    ? String.format("%s (%.1f GB Free / %.1f GB Total)",
                    driveName, usableSpace / 1e9, totalSpace / 1e9)
                    : driveName + " (Unavailable or No Media)";

            TreeItem<String> driveItem = new TreeItem<>(labelText);
            rootItem.getChildren().add(driveItem);

            // Add a dummy child so it can be expanded later (if supported)
            if (totalSpace > 0) {
                driveItem.getChildren().add(new TreeItem<>("..."));
            }
        }

        // --- Put it all together ---
        panel.getChildren().addAll(header, treeView);
        return panel;
    }

    /**
     * Builds the right panel that shows file categories (Photos, Videos, etc.).
     */
    private VBox createCategoryPanel() {
        VBox panel = new VBox(10);
        panel.setAlignment(Pos.TOP_LEFT);
        panel.setPadding(new Insets(15));
        // This panel uses the main theme background (no custom color).

        Label header = new Label("File Categories");
        header.getStyleClass().add("header-label");

        // --- Create a grid layout for the category cards ---
        TilePane categoryGrid = new TilePane(10, 10);
        categoryGrid.setPadding(new Insets(10, 0, 0, 0));
        VBox.setVgrow(categoryGrid, Priority.ALWAYS);

        // --- Add sample category cards ---
        categoryGrid.getChildren().addAll(
                createCategoryCard("Photos", "24,561 files"),
                createCategoryCard("Videos", "283 GB"),
                createCategoryCard("Archives", "1.2 GB of ZIP/RAR"),
                createCategoryCard("Documents", "345 documents"),
                createCategoryCard("Duplicated", "4.1 GB waste"),
                createCategoryCard("Unused", "Oldest file: 2012"),
                createCategoryCard("System", "Ready for cleanup"),
                createCategoryCard("Games", "25 games found"),
                createCategoryCard("Temp/Cache", "Clearable Cache"),
                createCategoryCard("Unclassified", "Needs AI review")
        );

        // --- Put everything together ---
        panel.getChildren().addAll(header, categoryGrid);
        return panel;
    }

    /**
     * Creates a single category card with a title and short description.
     */
    private Node createCategoryCard(String title, String subtitle) {
        VBox cardContent = new VBox(5);
        cardContent.setAlignment(Pos.TOP_LEFT);
        cardContent.setPadding(new Insets(15));
        VBox.setVgrow(cardContent, Priority.ALWAYS);

        Label titleLabel = new Label(title);
        titleLabel.getStyleClass().add("card-title");

        Label subtitleLabel = new Label(subtitle);
        subtitleLabel.getStyleClass().add("card-subtitle");
        VBox.setVgrow(subtitleLabel, Priority.ALWAYS);

        cardContent.getChildren().addAll(titleLabel, subtitleLabel);

        // --- Wrap content into a card container ---
        VBox card = new VBox(cardContent);
        card.setPrefSize(180, 100);
        card.getStyleClass().add("category-card");

        // Convert title into a safe CSS class (e.g., "Temp/Cache" → "category-temp-cache")
        String styleClassName = "category-" + title.toLowerCase()
                .replace(" ", "-")
                .replace("/", "-");
        card.getStyleClass().add(styleClassName);

        return card;
    }
}
