package com.example.system.controller;

import com.example.system.dtos.LoginResponse;
import com.example.system.models.StudentBalance;
import com.example.system.models.User;
import com.example.system.repositories.UserRepository;
import com.example.system.session.SessionManager;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.stage.Stage;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Component;

import java.text.NumberFormat;
import java.util.Locale;

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
    
    @FXML
    private Label prelimsLabel;
    
    @FXML
    private Label midtermsLabel;
    
    @FXML
    private Label preFinalsLabel;
    
    @FXML
    private Label finalsLabel;
    
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
                    
                    // Calculate term payments: Each term is exactly ₱9,500
                    double termAmount = 9500.00;
                    double amountPaid = balance.getAmountPaid() != null ? balance.getAmountPaid() : 0.0;
                    
                    // Calculate how many terms have been fully paid
                    int termsPaid = (int) Math.floor(amountPaid / termAmount);
                    
                    // Display each term: 0.00 if paid, 9,500 if not paid
                    prelimsLabel.setText(termsPaid >= 1 ? formatter.format(0.00) : formatter.format(termAmount));
                    midtermsLabel.setText(termsPaid >= 2 ? formatter.format(0.00) : formatter.format(termAmount));
                    preFinalsLabel.setText(termsPaid >= 3 ? formatter.format(0.00) : formatter.format(termAmount));
                    finalsLabel.setText(termsPaid >= 4 ? formatter.format(0.00) : formatter.format(termAmount));
                    
                    System.out.println("Loaded balance for: " + user.getFullName());
                    System.out.println("Amount paid: " + formatter.format(amountPaid));
                    System.out.println("Terms paid: " + termsPaid + " out of 4");
                }
            }
        }
    }
    
    @FXML
    public void handleBack() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/FXML/Homepage.fxml"));
            loader.setControllerFactory(springContext::getBean);
            Parent root = loader.load();
            
            Stage stage = (Stage) studentNameLabel.getScene().getWindow();
            stage.setScene(new Scene(root));
            stage.setTitle("PaySTI - Home");
            stage.show();
            
            System.out.println("Navigated back to Homepage");
        } catch (Exception e) {
            System.err.println("Could not load homepage");
            e.printStackTrace();
        }
    }
}