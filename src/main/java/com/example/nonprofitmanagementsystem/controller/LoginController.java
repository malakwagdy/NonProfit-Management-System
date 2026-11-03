package com.example.nonprofitmanagementsystem.controller;

import com.example.nonprofitmanagementsystem.config.AppContext;
import com.example.nonprofitmanagementsystem.config.ConfigurationManager;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;

public class LoginController {
    @FXML private Label titleLabel;
    @FXML private TextField emailField;
    @FXML private PasswordField passwordField;
    @FXML private Label messageLabel;

    @FXML
    private void initialize() {
        titleLabel.setText(ConfigurationManager.getInstance().getOrganizationName());
    }

    @FXML
    private void onLogin() {
        try {
            AppContext.getInstance().login(emailField.getText(), passwordField.getText());
            Navigator.showScene("main.fxml", "Dashboard");
        } catch (RuntimeException e) {
            messageLabel.setText(e.getMessage());
        }
    }

    @FXML
    private void onGoToSignUp() {
        Navigator.showScene("signup.fxml", "Sign Up");
    }
}
