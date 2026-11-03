package com.example.nonprofitmanagementsystem.controller;

import com.example.nonprofitmanagementsystem.config.AppContext;
import com.example.nonprofitmanagementsystem.config.ConfigurationManager;
import com.example.nonprofitmanagementsystem.donation.DonatedItem;
import com.example.nonprofitmanagementsystem.donation.Donation;
import com.example.nonprofitmanagementsystem.donation.DonationManager;
import com.example.nonprofitmanagementsystem.donation.DonationRequest;
import com.example.nonprofitmanagementsystem.donation.DonationType;
import com.example.nonprofitmanagementsystem.donation.ItemCategory;
import com.example.nonprofitmanagementsystem.donation.ItemCondition;
import com.example.nonprofitmanagementsystem.user.Donor;
import javafx.beans.property.SimpleStringProperty;
import javafx.fxml.FXML;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.layout.GridPane;

import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

public class DonationsController {
    private static final DateTimeFormatter DATE_FORMAT = DateTimeFormatter.ofPattern("dd MMM yyyy, HH:mm");

    @FXML private Label totalLabel;
    @FXML private TableView<Donation> donationsTable;
    @FXML private TableColumn<Donation, String> idColumn;
    @FXML private TableColumn<Donation, String> typeColumn;
    @FXML private TableColumn<Donation, String> amountColumn;
    @FXML private TableColumn<Donation, String> statusColumn;
    @FXML private TableColumn<Donation, String> dateColumn;
    @FXML private TableColumn<Donation, String> descriptionColumn;

    @FXML private ComboBox<DonationType> typeBox;
    @FXML private TextField descriptionField;
    @FXML private Label amountLabel;
    @FXML private TextField amountField;

    @FXML private GridPane onlineBox;
    @FXML private TextField platformField;
    @FXML private TextField transactionField;

    @FXML private GridPane checkBox;
    @FXML private TextField checkNumberField;
    @FXML private TextField bankField;

    @FXML private GridPane inKindBox;
    @FXML private TextField itemNameField;
    @FXML private ComboBox<ItemCategory> categoryBox;
    @FXML private ComboBox<ItemCondition> conditionBox;
    @FXML private TextField quantityField;
    @FXML private TextField itemValueField;

    @FXML private Label messageLabel;

    private final DonationManager donationManager = AppContext.getInstance().getDonationManager();
    private final Donor donor = (Donor) AppContext.getInstance().getCurrentUser();
    private final String currency = ConfigurationManager.getInstance().getDefaultCurrency();

    @FXML
    private void initialize() {
        idColumn.setCellValueFactory(c -> new SimpleStringProperty(String.valueOf(c.getValue().getDonationId())));
        typeColumn.setCellValueFactory(c -> new SimpleStringProperty(c.getValue().getDonationType().toString()));
        amountColumn.setCellValueFactory(c -> new SimpleStringProperty(String.format("%.2f", c.getValue().getAmount())));
        statusColumn.setCellValueFactory(c -> new SimpleStringProperty(c.getValue().getStatus().toString()));
        dateColumn.setCellValueFactory(c -> new SimpleStringProperty(c.getValue().getDonationDate().format(DATE_FORMAT)));
        descriptionColumn.setCellValueFactory(c -> new SimpleStringProperty(c.getValue().getDescription()));

        typeBox.getItems().addAll(DonationType.values());
        typeBox.setValue(DonationType.ONLINE);
        typeBox.setOnAction(e -> updateFormForType());
        categoryBox.getItems().addAll(ItemCategory.values());
        conditionBox.getItems().addAll(ItemCondition.values());
        amountLabel.setText("Amount (" + currency + ")");

        updateFormForType();
        refresh();
    }

    private void updateFormForType() {
        DonationType type = typeBox.getValue();
        setShown(onlineBox, type == DonationType.ONLINE);
        setShown(checkBox, type == DonationType.CHECK);
        setShown(inKindBox, type == DonationType.IN_KIND);
        // In-kind donations are valued from their items, so there is no amount to enter
        amountLabel.setVisible(type != DonationType.IN_KIND);
        amountField.setVisible(type != DonationType.IN_KIND);
    }

    private void setShown(GridPane box, boolean shown) {
        box.setVisible(shown);
        box.setManaged(shown);
    }

    private void refresh() {
        donationsTable.getItems().setAll(donationManager.getDonationsByDonor(donor.getUserId()));
        totalLabel.setText("Total donated: " + String.format("%.2f", donor.getTotalDonated()) + " " + currency);
    }

    @FXML
    private void onDonate() {
        DonationType type = typeBox.getValue();
        try {
            double amount = type == DonationType.IN_KIND ? 0 : parseNumber(amountField, "Amount");
            DonationRequest request = new DonationRequest(type, donor, amount, descriptionField.getText());

            if (type == DonationType.ONLINE) {
                request.setOnlineDetails(platformField.getText().trim(), transactionField.getText().trim());
            } else if (type == DonationType.CHECK) {
                request.setCheckDetails(checkNumberField.getText().trim(), bankField.getText().trim());
            } else {
                List<DonatedItem> items = new ArrayList<>();
                items.add(new DonatedItem(itemNameField.getText().trim(), categoryBox.getValue(), conditionBox.getValue(),
                        (int) parseNumber(quantityField, "Quantity"), parseNumber(itemValueField, "Estimated value")));
                request.setInKindDetails(items);
            }

            // Factory Method creates the right donation type; the Strategy for that type processes it
            Donation donation = donationManager.createDonation(request);
            if (donationManager.processDonation(donation)) {
                showSuccess("Thank you! Your donation of " + String.format("%.2f", donation.getAmount())
                        + " " + currency + " was processed.");
            } else {
                showError("The donation could not be processed (status: " + donation.getStatus()
                        + "). Please check the details you entered.");
            }
        } catch (RuntimeException e) {
            showError(e.getMessage());
        }
        refresh();
    }

    private double parseNumber(TextField field, String name) {
        try {
            return Double.parseDouble(field.getText().trim());
        } catch (NumberFormatException e) {
            throw new IllegalArgumentException(name + " must be a number");
        }
    }

    private void showSuccess(String message) {
        messageLabel.setStyle("-fx-text-fill: green;");
        messageLabel.setText(message);
    }

    private void showError(String message) {
        messageLabel.setStyle("-fx-text-fill: red;");
        messageLabel.setText(message);
    }
}
