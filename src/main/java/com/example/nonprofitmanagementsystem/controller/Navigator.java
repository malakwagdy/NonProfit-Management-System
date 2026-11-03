package com.example.nonprofitmanagementsystem.controller;

import com.example.nonprofitmanagementsystem.config.ConfigurationManager;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.IOException;
import java.io.UncheckedIOException;

// Loads FXML views and switches what the window shows
public class Navigator {
    private static final String VIEW_PATH = "/com/example/nonprofitmanagementsystem/view/";
    private static Stage stage;

    public static void init(Stage primaryStage) {
        stage = primaryStage;
    }

    // Replaces the whole window content (login, sign up, main dashboard)
    public static void showScene(String fxml, String title) {
        stage.setScene(new Scene(loadView(fxml), 1100, 660));
        stage.setTitle(ConfigurationManager.getInstance().getOrganizationName() + " - " + title);
    }

    // Loads a view so it can be placed inside another one (the dashboard pages)
    public static Parent loadView(String fxml) {
        try {
            return FXMLLoader.load(Navigator.class.getResource(VIEW_PATH + fxml));
        } catch (IOException e) {
            throw new UncheckedIOException("Could not load view: " + fxml, e);
        }
    }
}
