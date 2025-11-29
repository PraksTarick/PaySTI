package com.example.system.controller;

import java.text.NumberFormat;
import java.util.Locale;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Component;

import com.example.system.dtos.LoginResponse;
import com.example.system.models.StudentBalance;
import com.example.system.models.User;
import com.example.system.repositories.StudentBalanceRepository;
import com.example.system.repositories.UserRepository;
import com.example.system.session.SessionManager;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

@Component
public class GCashPaymentController {
    
    @FXML
    private Label amountLabel;
    
    @FXML
    private TextField mobileNumberField;
    
    @FXML
    private TextField amountField;
    
    @Autowired
    private SessionManager sessionManager;
    
    @Autowired
    private UserRepository userRepository;
    
    @Autowired
    private StudentBalanceRepository studentBalanceRepository;
    
    @Autowired
    private ApplicationContext springContext;
    
    private double remainingBalance = 0.0;
    
    @FXML
    public void initialize() {
        System.out.println("GCashPaymentController loaded");
        loadStudentBalance();
        
        // Add input validation
        amountField.textProperty().addListener((observable, oldValue, newValue) -> {
            if (!newValue.matches("\\d*(\\.\\d*)?")) {
                amountField.setText(oldValue);
            }
        });
    }
    
    private void loadStudentBalance() {
        LoginResponse currentUser = sessionManager.getCurrentUser();
        
        if (currentUser != null) {
            User user = userRepository.findByUsername(currentUser.getUsername()).orElse(null);
            
            if (user != null && user.getStudentBalance() != null) {
                StudentBalance balance = user.getStudentBalance();
                remainingBalance = balance.getRemainingBalance();
                
                NumberFormat formatter = NumberFormat.getCurrencyInstance(new Locale("en", "PH"));
                amountLabel.setText(formatter.format(remainingBalance));
                amountField.setText(String.valueOf(remainingBalance));
                
                System.out.println("Loaded balance: " + formatter.format(remainingBalance));
            }
        }
    }
    
    @FXML
    public void handleBack() {
        navigateToPaymentMethod();
    }
    
    @FXML
    public void handleProceedPayment() {
        String mobileNumber = mobileNumberField.getText().trim();
        String amountText = amountField.getText().trim();
        
        // Validate inputs
        if (mobileNumber.isEmpty()) {
            showAlert(Alert.AlertType.ERROR, "Validation Error", "Please enter your GCash mobile number");
            return;
        }
        
        if (!mobileNumber.matches("^09\\d{9}$")) {
            showAlert(Alert.AlertType.ERROR, "Validation Error", "Please enter a valid mobile number (09XXXXXXXXX)");
            return;
        }
        
        if (amountText.isEmpty()) {
            showAlert(Alert.AlertType.ERROR, "Validation Error", "Please enter the amount to pay");
            return;
        }
        
        double paymentAmount;
        try {
            paymentAmount = Double.parseDouble(amountText);
        } catch (NumberFormatException e) {
            showAlert(Alert.AlertType.ERROR, "Validation Error", "Please enter a valid amount");
            return;
        }
        
        if (paymentAmount <= 0) {
            showAlert(Alert.AlertType.ERROR, "Validation Error", "Amount must be greater than zero");
            return;
        }
        
        if (paymentAmount > remainingBalance) {
            showAlert(Alert.AlertType.ERROR, "Validation Error", "Payment amount exceeds remaining balance");
            return;
        }
        
        // Process payment
        processPayment(paymentAmount);
    }
    
    private void processPayment(double amount) {
        try {
            LoginResponse currentUser = sessionManager.getCurrentUser();
            
            if (currentUser != null) {
                User user = userRepository.findByUsername(currentUser.getUsername()).orElse(null);
                
                if (user != null && user.getStudentBalance() != null) {
                    StudentBalance balance = user.getStudentBalance();
                    
                    // Update balance
                    double newAmountPaid = balance.getAmountPaid() + amount;
                    double newRemainingBalance = balance.getTotalTuitionFee() - newAmountPaid;
                    
                    balance.setAmountPaid(newAmountPaid);
                    balance.setRemainingBalance(newRemainingBalance);
                    
                    // Save to database
                    studentBalanceRepository.save(balance);
                    
                    NumberFormat formatter = NumberFormat.getCurrencyInstance(new Locale("en", "PH"));
                    
                    System.out.println("Payment processed successfully");
                    System.out.println("Amount paid: " + formatter.format(amount));
                    System.out.println("New remaining balance: " + formatter.format(newRemainingBalance));
                    
                    // Show success message
                    showAlert(Alert.AlertType.INFORMATION, "Payment Successful", 
                            "Your payment of " + formatter.format(amount) + " has been processed successfully!\n\n" +
                            "Transaction ID: GCASH_" + System.currentTimeMillis() + "\n" +
                            "Mobile Number: " + mobileNumberField.getText() + "\n" +
                            "New remaining balance: " + formatter.format(newRemainingBalance));
                    
                    // Navigate back to tuition balance page
                    navigateToTuitionBalance();
                }
            }
        } catch (Exception e) {
            System.err.println("Error processing payment: " + e.getMessage());
            e.printStackTrace();
            showAlert(Alert.AlertType.ERROR, "Payment Error", "Failed to process payment. Please try again.");
        }
    }
    
    private void navigateToPaymentMethod() {
        try {
            String projectRoot = System.getProperty("user.dir");
            java.io.File fxmlFile = new java.io.File(projectRoot + "/src/main/resources/fxml/PaymentMethod.fxml");
            
            FXMLLoader loader = new FXMLLoader(fxmlFile.toURI().toURL());
            loader.setControllerFactory(springContext::getBean);
            Parent root = loader.load();
            
            Stage stage = (Stage) mobileNumberField.getScene().getWindow();
            
            // FIX: Preserve window maximized state
            boolean wasMaximized = stage.isMaximized();
            Scene currentScene = stage.getScene();
            currentScene.setRoot(root); // Replace root instead of creating new scene
            
            stage.setTitle("Select Payment Method");
            
            // Restore maximized state if it was maximized
            if (wasMaximized) {
                stage.setMaximized(true);
            }
            
            System.out.println("Navigated back to Payment Method (preserved window state)");
        } catch (Exception e) {
            System.err.println("Could not load payment method page");
            e.printStackTrace();
        }
    }
    
    private void navigateToTuitionBalance() {
        try {
            String projectRoot = System.getProperty("user.dir");
            java.io.File fxmlFile = new java.io.File(projectRoot + "/src/main/resources/fxml/TuitionBalance.fxml");
            
            FXMLLoader loader = new FXMLLoader(fxmlFile.toURI().toURL());
            loader.setControllerFactory(springContext::getBean);
            Parent root = loader.load();
            
            Stage stage = (Stage) mobileNumberField.getScene().getWindow();
            
            // FIX: Preserve window maximized state
            boolean wasMaximized = stage.isMaximized();
            Scene currentScene = stage.getScene();
            currentScene.setRoot(root); // Replace root instead of creating new scene
            
            stage.setTitle("PaySTI - Tuition Balance");
            
            // Restore maximized state if it was maximized
            if (wasMaximized) {
                stage.setMaximized(true);
            }
            
            System.out.println("Navigated back to Tuition Balance (preserved window state)");
        } catch (Exception e) {
            System.err.println("Could not load tuition balance page");
            e.printStackTrace();
        }
    }
    
    private void showAlert(Alert.AlertType type, String title, String content) {
        Alert alert = new Alert(type);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(content);
        alert.showAndWait();
    }
}