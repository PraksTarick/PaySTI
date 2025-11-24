package com.example.system.controller;

import java.io.File;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Component;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import javafx.stage.Window;

@Component
public class PaymentMethodController {

    @Autowired
    private ApplicationContext springContext;

    @FXML
    public void handleBack() {
        try {
            String projectRoot = System.getProperty("user.dir");
            String fxmlPath = projectRoot + "/src/main/resources/FXML/TuitionBalance.fxml";
            
            File fxmlFile = new File(fxmlPath);
            if (!fxmlFile.exists()) {
                System.err.println("TuitionBalance.fxml not found at: " + fxmlPath);
                return;
            }
            
            FXMLLoader loader = new FXMLLoader(fxmlFile.toURI().toURL());
            loader.setControllerFactory(springContext::getBean);
            Parent root = loader.load();
            
            // Get the current stage properly
            Stage stage = getCurrentStage();
            stage.setScene(new Scene(root));
            stage.setTitle("Tuition Balance");
            stage.show();
            
            System.out.println("Navigated back to Tuition Balance");
        } catch (Exception e) {
            System.err.println("Could not load tuition balance screen");
            e.printStackTrace();
        }
    }

    @FXML
    public void handleGCash() {
        loadPaymentScreen("GCashPayment.fxml", "GCash Payment");
    }

    @FXML
    public void handleGoTyme() {
        loadPaymentScreen("GoTymePayment.fxml", "GoTyme Payment");
    }

    private void loadPaymentScreen(String fxmlFile, String title) {
        try {
            String projectRoot = System.getProperty("user.dir");
            String fxmlPath = projectRoot + "/src/main/resources/FXML/" + fxmlFile;
            
            File fxmlFileObj = new File(fxmlPath);
            if (!fxmlFileObj.exists()) {
                System.err.println(fxmlFile + " not found at: " + fxmlPath);
                // Create a simple placeholder screen if the specific payment screen doesn't exist
                createPlaceholderPaymentScreen(title);
                return;
            }
            
            FXMLLoader loader = new FXMLLoader(fxmlFileObj.toURI().toURL());
            loader.setControllerFactory(springContext::getBean);
            Parent root = loader.load();
            
            Stage stage = getCurrentStage();
            stage.setScene(new Scene(root));
            stage.setTitle(title);
            stage.show();
            
            System.out.println("Navigated to " + title);
        } catch (Exception e) {
            System.err.println("Could not load " + fxmlFile);
            e.printStackTrace();
        }
    }

    private void createPlaceholderPaymentScreen(String paymentMethod) {
        try {
            // Create a simple placeholder screen
            VBox root = new VBox(20);
            root.setPadding(new javafx.geometry.Insets(40));
            root.setAlignment(javafx.geometry.Pos.CENTER);
            root.setStyle("-fx-background-color: #ecf0f1;");
            
            Label titleLabel = new Label(paymentMethod + " - Coming Soon");
            titleLabel.setStyle("-fx-font-size: 24px; -fx-font-weight: bold; -fx-text-fill: #2c3e50;");
            
            Label messageLabel = new Label("The " + paymentMethod + " integration is currently under development.\n\nYou will be able to complete your payment securely through this method soon.");
            messageLabel.setStyle("-fx-font-size: 16px; -fx-text-fill: #666; -fx-text-alignment: center;");
            messageLabel.setTextAlignment(javafx.scene.text.TextAlignment.CENTER);
            
            Button backButton = new Button("← Back to Payment Methods");
            backButton.setStyle("-fx-background-color: #1e88e5; -fx-text-fill: white; -fx-font-size: 14; -fx-padding: 10 20; -fx-cursor: hand;");
            backButton.setOnAction(e -> handleBack());
            
            root.getChildren().addAll(titleLabel, messageLabel, backButton);
            
            Stage stage = getCurrentStage();
            stage.setScene(new Scene(root, 900, 600));
            stage.setTitle(paymentMethod);
            stage.show();
            
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    // Helper method to get the current stage
    private Stage getCurrentStage() {
        // This is a placeholder - in a real FXML controller, you'd get the stage from a node
        // For now, we'll return the primary stage
        return (Stage) javafx.stage.Stage.getWindows().stream()
            .filter(Window::isShowing)
            .findFirst()
            .orElse(null);
    }
}