package edu.missouristate.aianalyzer.ui.view.Home;

import edu.missouristate.aianalyzer.ui.service.FileSystemService;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.io.File;

@Component
public class DriveView extends SplitPane {

    private final FileSystemService fileSystemService;

    @Autowired
    public DriveView(FileSystemService fileSystemService) {
        this.fileSystemService = fileSystemService;

        // Set up the two main panels: the drive list on the left, categories on the right.
        VBox driveTreePanel = createDriveTreePanel();
        VBox categoryPanel = createCategoryPanel();

        this.getItems().addAll(driveTreePanel, categoryPanel);
        this.setDividerPositions(0.40);
        this.getStyleClass().add("drive-split-view");

        HBox.setHgrow(this, Priority.ALWAYS);
        VBox.setVgrow(this, Priority.ALWAYS);
    }

    private VBox createDriveTreePanel() {
        VBox panel = new VBox(10);
        panel.setAlignment(Pos.TOP_LEFT);
        panel.getStyleClass().add("drive-tree-panel");

        Label header = new Label("System Drives");
        header.getStyleClass().add("header-label");

        // Set up the TreeView. It's now generic and works directly with <File> objects.
        TreeView<File> treeView = new TreeView<>();
        treeView.getStyleClass().add("tree-view");
        VBox.setVgrow(treeView, Priority.ALWAYS);

        // We need a root for the tree that isn't a real drive, so I'll make a fake 'My Computer' item.
        File virtualRootFile = new File("My Computer");
        TreeItem<File> rootItem = new TreeItem<>(virtualRootFile);
        rootItem.setExpanded(true);
        treeView.setRoot(rootItem);
        treeView.setShowRoot(true);

        // Loop through all the system drives and add them under 'My Computer'.
        // I'm using my custom FileTreeItem so it can load subfolders on its own.
        for (File drive : File.listRoots()) {
            FileTreeItem driveItem = new FileTreeItem(drive, fileSystemService);
            rootItem.getChildren().add(driveItem);
        }

        // This is the important part. By default, the TreeView doesn't know how to display a `File` object.
        // We have to tell it exactly what text to show for each item in the tree.
        treeView.setCellFactory(tv -> new TreeCell<>() {
            @Override
            protected void updateItem(File file, boolean empty) {
                super.updateItem(file, empty);
                if (empty || file == null) {
                    setText(null);
                } else {
                    // Handle my fake root and the 'Loading...' placeholder text.
                    if (file.getPath().equals("My Computer") || file.getPath().equals("Loading...")) {
                        setText(file.getPath());
                    } else {
                        // For actual drives and folders, show a clean path and, if it's a drive, the free space.
                        long totalSpace = file.getTotalSpace();

                        // For root drives, getName() is empty, so instead getPath() (e.g., "C:\").
                        // For all other folders/files, getName() (e.g., "GamingRoot").
                        String displayName = file.getName().isEmpty() ? file.getPath() : file.getName();

                        String textToShow = displayName;

                        // Only show free space if the file is a root drive (which has no parent).
                        if (file.getParentFile() == null) {
                            textToShow = String.format("%s (%.1f GB Free)",
                                    displayName, file.getUsableSpace() / 1_000_000_000.0);
                        }

                        setText(textToShow);
                    }
                }
            }
        });

        panel.getChildren().addAll(header, treeView);
        return panel;
    }


    // RIGHT panel above
    // LEFT panel below

    // Builds the panel on the right that shows all the file category cards.
    private VBox createCategoryPanel() {
        VBox panel = new VBox(10);
        panel.setAlignment(Pos.TOP_LEFT);
        panel.setPadding(new Insets(15));

        Label header = new Label("File Categories");
        header.getStyleClass().add("header-label");

        // A TilePane is perfect for the category cards, it'll wrap them nicely as the window resizes.
        TilePane categoryGrid = new TilePane(10, 10);
        categoryGrid.setPadding(new Insets(10, 0, 0, 0));
        VBox.setVgrow(categoryGrid, Priority.ALWAYS);

        // For now, just add some placeholder cards to see how it looks.
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

        panel.getChildren().addAll(header, categoryGrid);
        return panel;
    }

    // Helper function to make one of those category cards.
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

        // The VBox `card` is the main container with all the styling.
        VBox card = new VBox(cardContent);
        card.setPrefSize(180, 100);
        card.getStyleClass().add("category-card");

        // This lets me use the card's title for CSS. I'm converting a title like "Temp/Cache"
        // into a safe class name like "category-temp-cache" so I can style each card
        // type differently in the CSS file.
        String styleClassName = "category-" + title.toLowerCase()
                .replace(" ", "-")
                .replace("/", "-");
        card.getStyleClass().add(styleClassName);

        return card;
    }
}
