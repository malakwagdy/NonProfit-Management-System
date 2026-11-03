package com.example.nonprofitmanagementsystem.controller;

import com.example.nonprofitmanagementsystem.beneficiary.NeedCategory;
import com.example.nonprofitmanagementsystem.config.AppContext;
import com.example.nonprofitmanagementsystem.config.ConfigurationManager;
import com.example.nonprofitmanagementsystem.user.Beneficiary;
import com.example.nonprofitmanagementsystem.user.Donor;
import com.example.nonprofitmanagementsystem.user.User;
import com.example.nonprofitmanagementsystem.user.Volunteer;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.layout.VBox;

import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Arrays;

public class ProfileController {
    @FXML private TextField firstNameField;
    @FXML private TextField lastNameField;
    @FXML private TextField emailField;
    @FXML private TextField phoneField;
    @FXML private TextField addressField;
    @FXML private Label registeredLabel;
    @FXML private Label detailsMessage;

    @FXML private PasswordField oldPasswordField;
    @FXML private PasswordField newPasswordField;
    @FXML private PasswordField confirmPasswordField;
    @FXML private Label passwordMessage;

    @FXML private VBox donorBox;
    @FXML private Label totalDonatedLabel;

    @FXML private VBox volunteerBox;
    @FXML private Label totalHoursLabel;
    @FXML private TextField availabilityField;
    @FXML private TextField skillsField;
    @FXML private Label volunteerMessage;

    @FXML private VBox beneficiaryBox;
    @FXML private Label familySizeLabel;
    @FXML private Label eligibilityLabel;
    @FXML private Label needsLabel;

    private final AppContext context = AppContext.getInstance();
    private User user;

    @FXML
    private void initialize() {
        user = context.getCurrentUser();
        firstNameField.setText(user.getFirstName());
        lastNameField.setText(user.getLastName());
        emailField.setText(user.getEmail());
        phoneField.setText(user.getPhoneNumber());
        addressField.setText(user.getAddress());
        registeredLabel.setText(user.getRegistrationDate().format(DateTimeFormatter.ofPattern("dd MMM yyyy")));

        if (user instanceof Donor) {
            Donor donor = (Donor) user;
            show(donorBox);
            totalDonatedLabel.setText("Total donated: " + String.format("%.2f", donor.getTotalDonated())
                    + " " + ConfigurationManager.getInstance().getDefaultCurrency());
        } else if (user instanceof Volunteer) {
            Volunteer volunteer = (Volunteer) user;
            show(volunteerBox);
            totalHoursLabel.setText("Total hours volunteered: " + volunteer.getTotalHoursVolunteered());
            availabilityField.setText(volunteer.getAvailability());
            skillsField.setText(String.join(", ", volunteer.getSkills()));
        } else if (user instanceof Beneficiary) {
            Beneficiary beneficiary = (Beneficiary) user;
            show(beneficiaryBox);
            familySizeLabel.setText("Family size: " + beneficiary.getFamilySize());
            eligibilityLabel.setText("Eligibility status: " + beneficiary.getEligibilityStatus());
            ArrayList<String> needs = new ArrayList<>();
            for (NeedCategory need : beneficiary.getNeeds()) needs.add(need.getName());
            needsLabel.setText("Needs: " + (needs.isEmpty() ? "none recorded" : String.join(", ", needs)));
        }
    }

    private void show(VBox box) {
        box.setVisible(true);
        box.setManaged(true);
    }

    @FXML
    private void onSaveDetails() {
        try {
            context.getUserManager().updateProfile(user, firstNameField.getText(), lastNameField.getText(),
                    emailField.getText(), phoneField.getText(), addressField.getText());
            showSuccess(detailsMessage, "Details saved.");
        } catch (RuntimeException e) {
            showError(detailsMessage, e.getMessage());
        }
    }

    @FXML
    private void onChangePassword() {
        if (!newPasswordField.getText().equals(confirmPasswordField.getText())) {
            showError(passwordMessage, "New passwords do not match");
            return;
        }
        try {
            context.getUserManager().changePassword(user, oldPasswordField.getText(), newPasswordField.getText());
            oldPasswordField.clear();
            newPasswordField.clear();
            confirmPasswordField.clear();
            showSuccess(passwordMessage, "Password changed.");
        } catch (RuntimeException e) {
            showError(passwordMessage, e.getMessage());
        }
    }

    @FXML
    private void onSaveVolunteerDetails() {
        try {
            context.getVolunteerManager().updateVolunteerDetails((Volunteer) user, availabilityField.getText(),
                    Arrays.asList(skillsField.getText().split(",")));
            showSuccess(volunteerMessage, "Volunteer details saved.");
        } catch (RuntimeException e) {
            showError(volunteerMessage, e.getMessage());
        }
    }

    private void showSuccess(Label label, String message) {
        label.setStyle("-fx-text-fill: green;");
        label.setText(message);
    }

    private void showError(Label label, String message) {
        label.setStyle("-fx-text-fill: red;");
        label.setText(message);
    }
}
