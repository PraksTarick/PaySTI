package com.example.system.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.example.system.dtos.LoginRequest;
import com.example.system.dtos.LoginResponse;
import com.example.system.services.AuthService;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

@Component
public class LoginController {
    
    @FXML
    private TextField usernameField;
    
    @FXML
    private PasswordField passwordField;
    
    @FXML
    private Button loginButton;
    
    @Autowired
    private AuthService authService;
    
    private static LoginResponse currentUser;
    
    @FXML
    public void initialize() {
        // This method is called automatically after FXML is loaded
        System.out.println("Login Controller initialized");
    }
    
    @FXML
    public void handleLogin() {
        String username = usernameField.getText();
        String password = passwordField.getText();
        
        // Validate input
        if (username.isEmpty() || password.isEmpty()) {
            showAlert(Alert.AlertType.ERROR, "Login Error", "Please enter both username and password");
            return;
        }
        
        try {
            // Create login request
            LoginRequest loginRequest = new LoginRequest(username, password);
            
            // Call the backend authentication service
            LoginResponse response = authService.login(loginRequest);
            
            // Save current user info
            currentUser = response;
            
            // Show success message
            showAlert(Alert.AlertType.INFORMATION, "Login Successful", 
                    "Welcome, " + response.getFullName() + "!");
            
            // Load homepage
            loadHomepage();
            
        } catch (RuntimeException e) {
            // Show error message
            showAlert(Alert.AlertType.ERROR, "Login Failed", e.getMessage());
        } catch (Exception e) {
            showAlert(Alert.AlertType.ERROR, "Error", "An unexpected error occurred");
            e.printStackTrace();
        }
    }
    
    private void loadHomepage() {
        try {
            // Get current stage
            Stage stage = (Stage) loginButton.getScene().getWindow();
            
            // Load homepage FXML - try different paths
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/FXML/Homepage.fxml"));
            
            // Debug: Print the URL to see if file is found
            System.out.println("Looking for: " + getClass().getResource("/FXML/Homepage.fxml"));
            
            Parent root = loader.load();
            
            // Set the scene
            Scene scene = new Scene(root);
            stage.setScene(scene);
            stage.setTitle("Tuition Management System - Home");
            stage.show();
            
        } catch (Exception e) {
            showAlert(Alert.AlertType.ERROR, "Error", "Could not load homepage");
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
    
    // Static method to get current logged-in user
    public static LoginResponse getCurrentUser() {
        return currentUser;
    }
}