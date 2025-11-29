package com.example.system.controller;

import java.text.NumberFormat;
import java.util.Locale;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Component;

import com.example.system.dtos.LoginResponse;
import com.example.system.models.StudentBalance;
import com.example.system.models.User;
import com.example.system.repositories.UserRepository;
import com.example.system.session.SessionManager;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Label;
import javafx.stage.Stage;

@Component
public class TuitionBalanceController {
    
    @FXML
    private Label studentNameLabel;
    
    @FXML
    private Label studentIdLabel;
    
    @FXML
    private Label emailLabel;
    
    @FXML
    private Label totalFeeLabel;
    
    @FXML
    private Label amountPaidLabel;
    
    @FXML
    private Label remainingBalanceLabel;
    
    @FXML
    private Label semesterLabel;
    
    @FXML
    private Label schoolYearLabel;
    
    @Autowired
    private UserRepository userRepository;
    
    @Autowired
    private SessionManager sessionManager;
    
    @Autowired
    private ApplicationContext springContext;
    
    @FXML
    public void initialize() {
        System.out.println("TuitionBalanceController loaded");
        loadStudentData();
    }
    
    private void loadStudentData() {
        LoginResponse currentUser = sessionManager.getCurrentUser();
        
        if (currentUser != null) {
            User user = userRepository.findByUsername(currentUser.getUsername()).orElse(null);
            
            if (user != null) {
                // Set student info
                studentNameLabel.setText(user.getFullName());
                studentIdLabel.setText(user.getStudentId() != null ? user.getStudentId() : "N/A");
                emailLabel.setText(user.getEmail());
                
                // Load balance info
                if (user.getStudentBalance() != null) {
                    StudentBalance balance = user.getStudentBalance();
                    NumberFormat formatter = NumberFormat.getCurrencyInstance(new Locale("en", "PH"));
                    
                    totalFeeLabel.setText(formatter.format(balance.getTotalTuitionFee()));
                    amountPaidLabel.setText(formatter.format(balance.getAmountPaid()));
                    remainingBalanceLabel.setText(formatter.format(balance.getRemainingBalance()));
                    semesterLabel.setText(balance.getSemester());
                    schoolYearLabel.setText(balance.getSchoolYear());
                    
                    System.out.println("Loaded balance for: " + user.getFullName());
                }
            }
        }
    }
    
    @FXML
    public void handleMakePayment() {
        try {
            System.out.println("Make Payment button clicked - Navigating to Payment Method");
            
            String projectRoot = System.getProperty("user.dir");
            java.io.File fxmlFile = new java.io.File(projectRoot + "/src/main/resources/fxml/PaymentMethod.fxml");
            
            FXMLLoader loader = new FXMLLoader(fxmlFile.toURI().toURL());
            loader.setControllerFactory(springContext::getBean);
            Parent root = loader.load();
            
            Stage stage = (Stage) studentNameLabel.getScene().getWindow();
            
            // FIX: Preserve window maximized state
            boolean wasMaximized = stage.isMaximized();
            Scene currentScene = stage.getScene();
            currentScene.setRoot(root); // Replace root instead of creating new scene
            
            stage.setTitle("PaySTI - Select Payment Method");
            
            // Restore maximized state if it was maximized
            if (wasMaximized) {
                stage.setMaximized(true);
            }
            
            System.out.println("Navigated to Payment Method page (preserved window state)");

        } catch (Exception e) {
            System.err.println("Error navigating to payment method: " + e.getMessage());
            e.printStackTrace();
            
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Navigation Error");
            alert.setHeaderText(null);
            alert.setContentText("Failed to load payment method page: " + e.getMessage());
            alert.show();
        }
    }
    
    @FXML
    public void handleBack() {
        try {
            String projectRoot = System.getProperty("user.dir");
            java.io.File fxmlFile = new java.io.File(projectRoot + "/src/main/resources/fxml/Homepage.fxml");
            
            FXMLLoader loader = new FXMLLoader(fxmlFile.toURI().toURL());
            loader.setControllerFactory(springContext::getBean);
            Parent root = loader.load();
            
            Stage stage = (Stage) studentNameLabel.getScene().getWindow();
            
            // FIX: Preserve window maximized state
            boolean wasMaximized = stage.isMaximized();
            Scene currentScene = stage.getScene();
            currentScene.setRoot(root); // Replace root instead of creating new scene
            
            stage.setTitle("PaySTI - Home");
            
            // Restore maximized state if it was maximized
            if (wasMaximized) {
                stage.setMaximized(true);
            }
            
            System.out.println("Navigated back to Homepage (preserved window state)");
        } catch (Exception e) {
            System.err.println("Could not load homepage");
            e.printStackTrace();
        }
    }
}