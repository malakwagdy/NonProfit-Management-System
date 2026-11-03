package com.example.nonprofitmanagementsystem.controller;

import com.example.nonprofitmanagementsystem.config.AppContext;
import com.example.nonprofitmanagementsystem.user.Volunteer;
import com.example.nonprofitmanagementsystem.volunteer.VolunteerAssignment;
import javafx.beans.property.SimpleStringProperty;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;

import java.time.format.DateTimeFormatter;

public class AssignmentsController {
    private static final DateTimeFormatter DATE_FORMAT = DateTimeFormatter.ofPattern("dd MMM yyyy, HH:mm");

    @FXML private Label totalHoursLabel;
    @FXML private TableView<VolunteerAssignment> assignmentsTable;
    @FXML private TableColumn<VolunteerAssignment, String> eventColumn;
    @FXML private TableColumn<VolunteerAssignment, String> dateColumn;
    @FXML private TableColumn<VolunteerAssignment, String> taskColumn;
    @FXML private TableColumn<VolunteerAssignment, String> hoursColumn;
    @FXML private TableColumn<VolunteerAssignment, String> statusColumn;

    @FXML
    private void initialize() {
        AppContext context = AppContext.getInstance();
        Volunteer volunteer = (Volunteer) context.getCurrentUser();

        eventColumn.setCellValueFactory(c -> new SimpleStringProperty(c.getValue().getEvent().getEventName()));
        dateColumn.setCellValueFactory(c -> new SimpleStringProperty(c.getValue().getEvent().getEventDate().format(DATE_FORMAT)));
        taskColumn.setCellValueFactory(c -> new SimpleStringProperty(c.getValue().getTaskDescription()));
        hoursColumn.setCellValueFactory(c -> new SimpleStringProperty(String.valueOf(c.getValue().getHoursCompleted())));
        statusColumn.setCellValueFactory(c -> new SimpleStringProperty(c.getValue().getStatus().toString()));

        totalHoursLabel.setText("Total hours volunteered: " + volunteer.getTotalHoursVolunteered());
        assignmentsTable.getItems().setAll(context.getVolunteerManager().getAssignmentsForVolunteer(volunteer));
    }
}
