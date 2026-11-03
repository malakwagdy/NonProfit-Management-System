package com.example.nonprofitmanagementsystem.controller;

import com.example.nonprofitmanagementsystem.config.AppContext;
import com.example.nonprofitmanagementsystem.event.Event;
import com.example.nonprofitmanagementsystem.event.EventManager;
import com.example.nonprofitmanagementsystem.event.EventRegistration;
import com.example.nonprofitmanagementsystem.user.Donor;
import com.example.nonprofitmanagementsystem.user.User;
import javafx.beans.property.SimpleStringProperty;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;

import java.time.format.DateTimeFormatter;

public class EventsController {
    private static final DateTimeFormatter DATE_FORMAT = DateTimeFormatter.ofPattern("dd MMM yyyy, HH:mm");

    @FXML private TableView<Event> eventsTable;
    @FXML private TableColumn<Event, String> nameColumn;
    @FXML private TableColumn<Event, String> typeColumn;
    @FXML private TableColumn<Event, String> dateColumn;
    @FXML private TableColumn<Event, String> locationColumn;
    @FXML private TableColumn<Event, String> spotsColumn;
    @FXML private TableColumn<Event, String> statusColumn;
    @FXML private TableColumn<Event, String> myStatusColumn;
    @FXML private Label descriptionLabel;
    @FXML private Button followButton;
    @FXML private Button unfollowButton;
    @FXML private Label messageLabel;

    private final EventManager eventManager = AppContext.getInstance().getEventManager();
    private final User user = AppContext.getInstance().getCurrentUser();

    @FXML
    private void initialize() {
        nameColumn.setCellValueFactory(c -> new SimpleStringProperty(c.getValue().getEventName()));
        typeColumn.setCellValueFactory(c -> new SimpleStringProperty(c.getValue().getEventType().toString()));
        dateColumn.setCellValueFactory(c -> new SimpleStringProperty(c.getValue().getEventDate().format(DATE_FORMAT)));
        locationColumn.setCellValueFactory(c -> new SimpleStringProperty(c.getValue().getLocation()));
        spotsColumn.setCellValueFactory(c -> new SimpleStringProperty(
                c.getValue().getRegisteredCount() + " / " + c.getValue().getCapacity()));
        statusColumn.setCellValueFactory(c -> new SimpleStringProperty(c.getValue().getEventStatus().toString()));
        myStatusColumn.setCellValueFactory(c -> new SimpleStringProperty(myStatus(c.getValue())));

        eventsTable.getSelectionModel().selectedItemProperty().addListener((obs, old, event) ->
                descriptionLabel.setText(event == null ? "" : event.getDescription()));

        // Only donors can follow fundraisers
        boolean donor = user instanceof Donor;
        followButton.setVisible(donor);
        unfollowButton.setVisible(donor);

        refresh();
    }

    private String myStatus(Event event) {
        String status = "";
        EventRegistration registration = eventManager.findActiveRegistration(event.getEventId(), user);
        if (registration != null) status = "Registered (" + registration.getTicketNumber() + ")";
        if (user instanceof Donor && eventManager.isDonorSubscribed(event.getEventId(), (Donor) user)) {
            status = status.isEmpty() ? "Following" : status + ", following";
        }
        return status;
    }

    private void refresh() {
        Event selected = eventsTable.getSelectionModel().getSelectedItem();
        eventsTable.getItems().setAll(eventManager.getUpcomingEvents());
        eventsTable.getSelectionModel().select(selected);
    }

    private Event getSelectedEvent() {
        Event event = eventsTable.getSelectionModel().getSelectedItem();
        if (event == null) showError("Select an event first.");
        return event;
    }

    @FXML
    private void onRegister() {
        Event event = getSelectedEvent();
        if (event == null) return;
        try {
            EventRegistration registration = eventManager.registerParticipant(event.getEventId(), user);
            showSuccess("Registered for " + event.getEventName() + ". Your ticket: " + registration.getTicketNumber());
        } catch (RuntimeException e) {
            showError(e.getMessage());
        }
        refresh();
    }

    @FXML
    private void onCancelRegistration() {
        Event event = getSelectedEvent();
        if (event == null) return;
        EventRegistration registration = eventManager.findActiveRegistration(event.getEventId(), user);
        if (registration == null) {
            showError("You are not registered for this event.");
            return;
        }
        eventManager.cancelRegistration(event.getEventId(), registration.getRegistrationId());
        showSuccess("Registration cancelled.");
        refresh();
    }

    @FXML
    private void onFollow() {
        Event event = getSelectedEvent();
        if (event == null) return;
        try {
            eventManager.subscribeDonor(event.getEventId(), (Donor) user);
            showSuccess("You will now receive updates about " + event.getEventName() + ".");
        } catch (RuntimeException e) {
            showError(e.getMessage());
        }
        refresh();
    }

    @FXML
    private void onUnfollow() {
        Event event = getSelectedEvent();
        if (event == null) return;
        if (!eventManager.isDonorSubscribed(event.getEventId(), (Donor) user)) {
            showError("You are not following this event.");
            return;
        }
        eventManager.unsubscribeDonor(event.getEventId(), (Donor) user);
        showSuccess("You will no longer receive updates about " + event.getEventName() + ".");
        refresh();
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
