package com.example.nonprofitmanagementsystem;

import com.example.nonprofitmanagementsystem.controller.Navigator;
import javafx.application.Application;
import javafx.stage.Stage;

public class MainApplication extends Application {
    @Override
    public void start(Stage stage) {
        Navigator.init(stage);
        Navigator.showScene("login.fxml", "Login");
        stage.show();
    }
}
