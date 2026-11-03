package com.example.nonprofitmanagementsystem.controller;

import com.example.nonprofitmanagementsystem.config.AppContext;
import com.example.nonprofitmanagementsystem.user.SignUpRequest;
import com.example.nonprofitmanagementsystem.user.UserRole;
import javafx.fxml.FXML;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;

public class SignUpController {
    @FXML private ComboBox<UserRole> roleBox;
    @FXML private TextField firstNameField;
    @FXML private TextField lastNameField;
    @FXML private TextField emailField;
    @FXML private TextField phoneField;
    @FXML private TextField addressField;
    @FXML private PasswordField passwordField;
    @FXML private PasswordField confirmPasswordField;
    @FXML private Label familySizeLabel;
    @FXML private TextField familySizeField;
    @FXML private Label messageLabel;

    @FXML
    private void initialize() {
        // Administrators can't sign up themselves, so they are not offered here
        roleBox.getItems().addAll(UserRole.DONOR, UserRole.VOLUNTEER, UserRole.BENEFICIARY);
        roleBox.setValue(UserRole.DONOR);
        roleBox.setOnAction(e -> updateFamilySizeVisibility());
        updateFamilySizeVisibility();
    }

    private void updateFamilySizeVisibility() {
        boolean beneficiary = roleBox.getValue() == UserRole.BENEFICIARY;
        familySizeLabel.setVisible(beneficiary);
        familySizeLabel.setManaged(beneficiary);
        familySizeField.setVisible(beneficiary);
        familySizeField.setManaged(beneficiary);
    }

    @FXML
    private void onSignUp() {
        if (!passwordField.getText().equals(confirmPasswordField.getText())) {
            showError("Passwords do not match");
            return;
        }

        SignUpRequest request = new SignUpRequest(roleBox.getValue(), firstNameField.getText(),
                lastNameField.getText(), emailField.getText(), phoneField.getText(),
                passwordField.getText(), addressField.getText());

        if (roleBox.getValue() == UserRole.BENEFICIARY) {
            try {
                request.setFamilySize(Integer.parseInt(familySizeField.getText().trim()));
            } catch (NumberFormatException e) {
                showError("Family size must be a whole number");
                return;
            }
        }

        try {
            AppContext.getInstance().getUserManager().signUp(request);
            messageLabel.setStyle("-fx-text-fill: green;");
            messageLabel.setText("Account created. You can now log in.");
            AppContext.getInstance().login(request.getEmail(), request.getPassword());
            Navigator.showScene("main.fxml", "Dashboard");
        } catch (RuntimeException e) {
            showError(e.getMessage());
        }
    }

    private void showError(String message) {
        messageLabel.setStyle("-fx-text-fill: red;");
        messageLabel.setText(message);
    }

    @FXML
    private void onGoToLogin() {
        Navigator.showScene("login.fxml", "Login");
    }
}
