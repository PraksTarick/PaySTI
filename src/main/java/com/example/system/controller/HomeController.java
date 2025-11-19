package com.example.system.controller;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.MenuItem;
import javafx.stage.Stage;

public class HomeController {

    @FXML
    public void initialize() {
        System.out.println("HomeController loaded.");
    }

    @FXML
    private void handleSignOut(ActionEvent event) {
        try {
            // Load Login.fxml
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/FXML/Login.fxml"));
            Parent root = loader.load();

            // Get the current stage from the MenuItem that triggered the event
            Stage stage = (Stage) ((MenuItem) event.getSource()).getParentPopup().getOwnerWindow();

            stage.setScene(new Scene(root));
            stage.setTitle("Login");
            stage.show();

            System.out.println("User signed out.");

        } catch (Exception e) {
            e.printStackTrace();
            Alert alert = new Alert(Alert.AlertType.ERROR, "Failed to sign out.");
            alert.show();
        }
    }
}
