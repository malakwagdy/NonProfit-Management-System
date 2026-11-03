package com.example.nonprofitmanagementsystem.controller;

import com.example.nonprofitmanagementsystem.config.AppContext;
import com.example.nonprofitmanagementsystem.user.User;
import com.example.nonprofitmanagementsystem.user.UserRole;
import javafx.fxml.FXML;
import javafx.geometry.Insets;
import javafx.scene.Parent;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.VBox;

// The user control panel: a menu that depends on the user's role, and the selected page in the center
public class MainController {
    @FXML private BorderPane root;
    @FXML private Label welcomeLabel;
    @FXML private VBox menu;

    @FXML
    private void initialize() {
        User user = AppContext.getInstance().getCurrentUser();
        welcomeLabel.setText("Welcome, " + user.getFullName() + " (" + user.getRole() + ")");

        addMenuButton("My Profile", "profile.fxml");
        UserRole role = user.getRole();
        if (role == UserRole.ADMINISTRATOR) {
            addMenuButton("Admin Panel", "admin-home.fxml");
        } else {
            addMenuButton("Events", "events.fxml");
        }
        if (role == UserRole.DONOR) addMenuButton("My Donations", "donations.fxml");
        if (role == UserRole.VOLUNTEER) addMenuButton("My Assignments", "assignments.fxml");
        if (role == UserRole.BENEFICIARY) addMenuButton("Assistance", "assistance.fxml");

        showPage(role == UserRole.ADMINISTRATOR ? "admin-home.fxml" : "events.fxml");
    }

    private void addMenuButton(String text, String fxml) {
        Button button = new Button(text);
        button.setMaxWidth(Double.MAX_VALUE);
        button.setOnAction(e -> showPage(fxml));
        menu.getChildren().add(button);
    }

    private void showPage(String fxml) {
        Parent page = Navigator.loadView(fxml);
        BorderPane.setMargin(page, new Insets(15));
        root.setCenter(page);
    }

    @FXML
    private void onLogout() {
        AppContext.getInstance().logout();
        Navigator.showScene("login.fxml", "Login");
    }
}
