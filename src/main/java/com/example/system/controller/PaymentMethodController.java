package com.example.system.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Component;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import javafx.stage.Window;

@Component
public class PaymentMethodController {
    
    @Autowired
    private ApplicationContext springContext;
    
    @FXML
    public void initialize() {
        System.out.println("PaymentMethodController loaded");
    }
    
    @FXML
    public void handleBack() {
        navigateToTuitionBalance();
    }
    
    @FXML
    public void handleGCashPayment() {
        try {
            String projectRoot = System.getProperty("user.dir");
            java.io.File fxmlFile = new java.io.File(projectRoot + "/src/main/resources/fxml/GCashPayment.fxml");
            
            FXMLLoader loader = new FXMLLoader(fxmlFile.toURI().toURL());
            loader.setControllerFactory(springContext::getBean);
            Parent root = loader.load();
            
            Stage stage = getStage();
            if (stage != null) {
                // FIX: Preserve window maximized state
                boolean wasMaximized = stage.isMaximized();
                Scene currentScene = stage.getScene();
                currentScene.setRoot(root); // Replace root instead of creating new scene
                
                stage.setTitle("GCash Payment");
                
                // Restore maximized state if it was maximized
                if (wasMaximized) {
                    stage.setMaximized(true);
                }
                
                System.out.println("Navigated to GCash payment (preserved window state)");
            }
        } catch (Exception e) {
            System.err.println("Could not load GCash payment page");
            e.printStackTrace();
        }
    }
    
    @FXML
    public void handleGoTymePayment() {
        try {
            String projectRoot = System.getProperty("user.dir");
            java.io.File fxmlFile = new java.io.File(projectRoot + "/src/main/resources/fxml/GoTymePayment.fxml");
            
            FXMLLoader loader = new FXMLLoader(fxmlFile.toURI().toURL());
            loader.setControllerFactory(springContext::getBean);
            Parent root = loader.load();
            
            Stage stage = getStage();
            if (stage != null) {
                // FIX: Preserve window maximized state
                boolean wasMaximized = stage.isMaximized();
                Scene currentScene = stage.getScene();
                currentScene.setRoot(root); // Replace root instead of creating new scene
                
                stage.setTitle("goTyme Payment");
                
                // Restore maximized state if it was maximized
                if (wasMaximized) {
                    stage.setMaximized(true);
                }
                
                System.out.println("Navigated to goTyme payment (preserved window state)");
            }
        } catch (Exception e) {
            System.err.println("Could not load goTyme payment page");
            e.printStackTrace();
        }
    }
    
    @FXML
    public void handleGCashHover(MouseEvent event) {
        VBox vbox = (VBox) event.getSource();
        vbox.setStyle("-fx-background-color: #f0f8ff; -fx-background-radius: 15; -fx-padding: 40; -fx-cursor: hand;");
    }
    
    @FXML
    public void handleGCashExit(MouseEvent event) {
        VBox vbox = (VBox) event.getSource();
        vbox.setStyle("-fx-background-color: white; -fx-background-radius: 15; -fx-padding: 40; -fx-cursor: hand;");
    }
    
    @FXML
    public void handleGoTymeHover(MouseEvent event) {
        VBox vbox = (VBox) event.getSource();
        vbox.setStyle("-fx-background-color: #f0fff4; -fx-background-radius: 15; -fx-padding: 40; -fx-cursor: hand;");
    }
    
    @FXML
    public void handleGoTymeExit(MouseEvent event) {
        VBox vbox = (VBox) event.getSource();
        vbox.setStyle("-fx-background-color: white; -fx-background-radius: 15; -fx-padding: 40; -fx-cursor: hand;");
    }
    
    private void navigateToTuitionBalance() {
        try {
            String projectRoot = System.getProperty("user.dir");
            java.io.File fxmlFile = new java.io.File(projectRoot + "/src/main/resources/fxml/TuitionBalance.fxml");
            
            FXMLLoader loader = new FXMLLoader(fxmlFile.toURI().toURL());
            loader.setControllerFactory(springContext::getBean);
            Parent root = loader.load();
            
            Stage stage = getStage();
            if (stage != null) {
                // FIX: Preserve window maximized state
                boolean wasMaximized = stage.isMaximized();
                Scene currentScene = stage.getScene();
                currentScene.setRoot(root); // Replace root instead of creating new scene
                
                stage.setTitle("Tuition Balance");
                
                // Restore maximized state if it was maximized
                if (wasMaximized) {
                    stage.setMaximized(true);
                }
                
                System.out.println("Navigated to Tuition Balance (preserved window state)");
            }
        } catch (Exception e) {
            System.err.println("Could not load tuition balance page");
            e.printStackTrace();
        }
    }
    
    private Stage getStage() {
        for (Window window : Window.getWindows()) {
            if (window instanceof Stage && window.isShowing()) {
                return (Stage) window;
            }
        }
        return null;
    }
}