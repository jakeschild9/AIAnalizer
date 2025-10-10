package edu.missouristate.aianalyzer.ui.view;

import javafx.geometry.Pos;
import javafx.scene.control.Label;
import javafx.scene.layout.VBox;
import org.springframework.stereotype.Component;

/**
 * Component representing the AI Suggestions page.
 */
@Component
public class SuggestionsView extends VBox {

    public SuggestionsView() {
        this.getStyleClass().add("page-container");
        this.setAlignment(Pos.CENTER);
        this.setPrefSize(700, 600);

        Label header = new Label("AI Suggestions");
        header.getStyleClass().add("header-label");

        Label content = new Label("This is the AI Suggestions page. Recommended actions can display here.");
        content.getStyleClass().add("placeholder-label");

        this.getChildren().addAll(header, content);
    }
}
