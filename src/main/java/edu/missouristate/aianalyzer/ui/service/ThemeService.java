package edu.missouristate.aianalyzer.ui.service;

import javafx.scene.Scene;
import org.springframework.stereotype.Service;

import java.net.URL;

@Service
public class ThemeService {

    private Scene mainScene;

    /**
     * Stores the main scene so we can apply stylesheets to it later.
     */
    public void setScene(Scene scene) {
        this.mainScene = scene;
    }

    /**
     * This is the exact same logic you had in StageInitializer, now moved here.
     */
    public void applyTheme(String themeName) {
        if (mainScene == null) return;
        mainScene.getStylesheets().clear();

        String commonCssPath = "/styles/common.css";
        URL commonUrl = getClass().getResource(commonCssPath);
        if (commonUrl != null) {
            mainScene.getStylesheets().add(commonUrl.toExternalForm());
        } else {
            System.err.println("CRITICAL ERROR: Could not find common.css");
        }

        String themeCssPath = "/styles/themes/" + themeName + ".css";
        URL themeUrl = getClass().getResource(themeCssPath);
        if (themeUrl != null) {
            mainScene.getStylesheets().add(themeUrl.toExternalForm());
            System.out.println("Theme switched to: " + themeName);
        } else {
            System.err.println("Error: Could not find theme stylesheet: " + themeCssPath);
        }
    }
}