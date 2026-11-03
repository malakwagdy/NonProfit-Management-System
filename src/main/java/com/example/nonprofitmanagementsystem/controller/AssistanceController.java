package com.example.nonprofitmanagementsystem.controller;

import com.example.nonprofitmanagementsystem.beneficiary.BeneficiaryManager;
import com.example.nonprofitmanagementsystem.beneficiary.Resource;
import com.example.nonprofitmanagementsystem.beneficiary.ResourceDistribution;
import com.example.nonprofitmanagementsystem.config.AppContext;
import com.example.nonprofitmanagementsystem.user.Beneficiary;
import javafx.beans.property.SimpleStringProperty;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;

import java.time.format.DateTimeFormatter;

public class AssistanceController {
    private static final DateTimeFormatter DATE_FORMAT = DateTimeFormatter.ofPattern("dd MMM yyyy, HH:mm");

    @FXML private Label eligibilityLabel;
    @FXML private TableView<Resource> resourcesTable;
    @FXML private TableColumn<Resource, String> resourceNameColumn;
    @FXML private TableColumn<Resource, String> resourceCategoryColumn;
    @FXML private TableColumn<Resource, String> resourceTypeColumn;
    @FXML private TableColumn<Resource, String> resourceAvailableColumn;
    @FXML private TextField quantityField;
    @FXML private Label messageLabel;

    @FXML private TableView<ResourceDistribution> requestsTable;
    @FXML private TableColumn<ResourceDistribution, String> requestIdColumn;
    @FXML private TableColumn<ResourceDistribution, String> requestResourceColumn;
    @FXML private TableColumn<ResourceDistribution, String> requestQuantityColumn;
    @FXML private TableColumn<ResourceDistribution, String> requestDateColumn;
    @FXML private TableColumn<ResourceDistribution, String> requestStatusColumn;
    @FXML private TableColumn<ResourceDistribution, String> requestReasonColumn;

    private final BeneficiaryManager beneficiaryManager = AppContext.getInstance().getBeneficiaryManager();
    private final Beneficiary beneficiary = (Beneficiary) AppContext.getInstance().getCurrentUser();

    @FXML
    private void initialize() {
        resourceNameColumn.setCellValueFactory(c -> new SimpleStringProperty(c.getValue().getResourceName()));
        resourceCategoryColumn.setCellValueFactory(c -> new SimpleStringProperty(c.getValue().getCategory().getName()));
        resourceTypeColumn.setCellValueFactory(c -> new SimpleStringProperty(c.getValue().getAssistanceType().toString()));
        resourceAvailableColumn.setCellValueFactory(c -> new SimpleStringProperty(
                c.getValue().getAvailableQuantity() + " " + c.getValue().getUnit()));

        requestIdColumn.setCellValueFactory(c -> new SimpleStringProperty(String.valueOf(c.getValue().getDistributionId())));
        requestResourceColumn.setCellValueFactory(c -> new SimpleStringProperty(c.getValue().getResource().getResourceName()));
        requestQuantityColumn.setCellValueFactory(c -> new SimpleStringProperty(String.valueOf(c.getValue().getQuantity())));
        requestDateColumn.setCellValueFactory(c -> new SimpleStringProperty(c.getValue().getRequestDate().format(DATE_FORMAT)));
        requestStatusColumn.setCellValueFactory(c -> new SimpleStringProperty(c.getValue().getStatus().toString()));
        requestReasonColumn.setCellValueFactory(c -> new SimpleStringProperty(
                c.getValue().getDecisionReason() == null ? "" : c.getValue().getDecisionReason()));

        eligibilityLabel.setText("Eligibility status: " + beneficiary.getEligibilityStatus()
                + "   |   Family size: " + beneficiary.getFamilySize());
        refresh();
    }

    private void refresh() {
        resourcesTable.getItems().setAll(beneficiaryManager.getAvailableResources());
        requestsTable.getItems().setAll(beneficiaryManager.getDistributionHistory(beneficiary.getUserId()));
    }

    @FXML
    private void onRequest() {
        Resource resource = resourcesTable.getSelectionModel().getSelectedItem();
        if (resource == null) {
            showError("Select a resource first.");
            return;
        }
        int quantity;
        try {
            quantity = Integer.parseInt(quantityField.getText().trim());
        } catch (NumberFormatException e) {
            showError("Quantity must be a whole number");
            return;
        }
        try {
            beneficiaryManager.requestDistribution(beneficiary, resource, quantity);
            messageLabel.setStyle("-fx-text-fill: green;");
            messageLabel.setText("Request for " + quantity + " " + resource.getUnit() + "(s) of "
                    + resource.getResourceName() + " submitted.");
        } catch (RuntimeException e) {
            showError(e.getMessage());
        }
        refresh();
    }

    private void showError(String message) {
        messageLabel.setStyle("-fx-text-fill: red;");
        messageLabel.setText(message);
    }
}
