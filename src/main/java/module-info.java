module com.example.nonprofitmanagementsystem {
    requires javafx.controls;
    requires javafx.fxml;
    requires java.sql;
    requires io.github.cdimascio.dotenv.java;
    requires org.postgresql.jdbc;


    opens com.example.nonprofitmanagementsystem to javafx.fxml;
    opens com.example.nonprofitmanagementsystem.controller to javafx.fxml;
    opens com.example.nonprofitmanagementsystem.user to javafx.base;
    opens com.example.nonprofitmanagementsystem.donation to javafx.base;
    opens com.example.nonprofitmanagementsystem.event to javafx.base;
    opens com.example.nonprofitmanagementsystem.volunteer to javafx.base;
    opens com.example.nonprofitmanagementsystem.beneficiary to javafx.base;
    opens com.example.nonprofitmanagementsystem.documents to javafx.base;
    opens com.example.nonprofitmanagementsystem.communication to javafx.base;

    exports com.example.nonprofitmanagementsystem;
}