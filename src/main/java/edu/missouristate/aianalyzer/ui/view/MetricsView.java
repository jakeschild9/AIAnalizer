package edu.missouristate.aianalyzer.ui.view;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.control.Label;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import org.springframework.stereotype.Component;

/**
 * Component representing the Metrics page.
 * Displays a 3-column flexible card layout for data visualization templates
 */
@Component
public class MetricsView extends VBox {

    // Define the base size for one column, including HGap (15px)
    private static final double CARD_WIDTH = 280;
    private static final double CARD_HEIGHT = 160;
    private static final double GAP = 15;

    public MetricsView() {
        this.getStyleClass().add("page-container");
        this.setAlignment(Pos.TOP_LEFT);
        this.setPadding(new Insets(20));

        Label header = new Label("Application Metrics & Insights");
        header.getStyleClass().add("header-label");

        // A) Setup the GridPane for a flexible 3-column layout
        GridPane gridPane = new GridPane();
        gridPane.setPadding(new Insets(10, 0, 10, 0));
        gridPane.setHgap(GAP);
        gridPane.setVgap(GAP);

        // --- ROW 1: One card 3-col
        Node trendsCard = createCard(3 * CARD_WIDTH + 2 * GAP, CARD_HEIGHT, "#4A90E2", "Overall Health Summary", "AI System Status: Operational, 3.2 GB Recoverable");
        GridPane.setConstraints(trendsCard, 0, 0, 3, 1); // Col 0, Row 0, ColSpan 3, RowSpan 1
        gridPane.getChildren().add(trendsCard);

        // --- ROW 2: Two cards (1-col and 2-col)
        Node totalFilesCard = createCard(CARD_WIDTH, CARD_HEIGHT, "#F0E6E6", "Total Files Scanned", "45,892");
        GridPane.setConstraints(totalFilesCard, 0, 1); // Col 0, Row 1
        gridPane.getChildren().add(totalFilesCard);

        Node usageBreakdownCard = createCard(2 * CARD_WIDTH + GAP, CARD_HEIGHT, "#E6F0E6", "Disk Usage Breakdown", "Pie Chart Template");
        GridPane.setConstraints(usageBreakdownCard, 1, 1, 2, 1); // Col 1, Row 1, ColSpan 2
        gridPane.getChildren().add(usageBreakdownCard);

        // --- ROW 3: Three cards (1-col, 1-col, 1-col)
        Node unusedFilesCard = createCard(CARD_WIDTH, CARD_HEIGHT, "#E6F0F0", "Files Unused > 5 Years", "12.3 GB");
        GridPane.setConstraints(unusedFilesCard, 0, 2); // Col 0, Row 2
        gridPane.getChildren().add(unusedFilesCard);

        Node actionsCard = createCard(CARD_WIDTH, CARD_HEIGHT, "#E6E6F0", "AI Recommended Actions", "72 Items to Review");
        GridPane.setConstraints(actionsCard, 1, 2); // Col 1, Row 2
        gridPane.getChildren().add(actionsCard);

        Node duplicatesCard = createCard(CARD_WIDTH, CARD_HEIGHT, "#F0E6F0", "Duplicate Files Found", "2.1 GB");
        GridPane.setConstraints(duplicatesCard, 2, 2); // Col 2, Row 2
        gridPane.getChildren().add(duplicatesCard);


        this.getChildren().addAll(header, gridPane);
    }

    /**
     * Helper method for generating placeholder card
     */
    private Node createCard(double width, double height, String color, String title, String content) {
        VBox cardContent = new VBox(5);
        cardContent.setAlignment(Pos.TOP_LEFT);
        cardContent.setPadding(new Insets(10));

        Label titleLabel = new Label(title);
        titleLabel.setStyle("-fx-font-weight: bold; -fx-font-size: 14px; -fx-text-fill: #333;");

        Label contentLabel = new Label(content);
        // Use white text for the blue header card for better contrast
        if (color.equals("#4A90E2")) {
            contentLabel.setStyle("-fx-font-size: 20px; -fx-text-fill: white;");
            titleLabel.setStyle("-fx-font-weight: bold; -fx-font-size: 14px; -fx-text-fill: #ffffff;");
        } else {
            contentLabel.setStyle("-fx-font-size: 20px; -fx-text-fill: #555;");
        }


        cardContent.getChildren().addAll(titleLabel, contentLabel);

        StackPane card = new StackPane(cardContent);
        card.setPrefSize(width, height);
        card.setMinSize(width, height);

        // Styling for border/background
        card.setStyle(String.format(
                "-fx-background-color: %s; " +
                        "-fx-border-color: #d0d0d0; " +
                        "-fx-border-width: 1px; " +
                        "-fx-border-radius: 8px; " +
                        "-fx-background-radius: 8px;", color));

        return card;
    }
}
