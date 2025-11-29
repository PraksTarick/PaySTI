package com.example.system;

import com.example.system.models.StudentBalance;
import com.example.system.models.User;
import com.example.system.repositories.StudentBalanceRepository;
import com.example.system.repositories.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

@Component
public class DataLoader implements CommandLineRunner {
    
    @Autowired
    private UserRepository userRepository;
    
    @Autowired
    private StudentBalanceRepository studentBalanceRepository;
    
    @Autowired
    private PasswordEncoder passwordEncoder;
    
    @Override
    public void run(String... args) throws Exception {
        // Update all existing student balances to ₱38,000
        updateAllStudentBalances();
        
        // Create test users if database is empty
        if (userRepository.count() == 0) {
            
            // Test Student User 1
            User student1 = new User();
            student1.setUsername("student");
            student1.setPassword(passwordEncoder.encode("password123"));
            student1.setFullName("John Doe");
            student1.setEmail("john.doe@sti.edu");
            student1.setStudentId("2021-000001");
            student1.setRole("STUDENT");
            student1.setEnabled(true);
            student1 = userRepository.save(student1);
            
            // Create tuition balance for student1
            StudentBalance balance1 = new StudentBalance();
            balance1.setStudentId("2021-000001");
            balance1.setTotalTuitionFee(38000.00);
            balance1.setAmountPaid(0.00);
            balance1.setRemainingBalance(38000.00);
            balance1.setSemester("1st Semester");
            balance1.setSchoolYear("2024-2025");
            balance1.setUser(student1);
            studentBalanceRepository.save(balance1);
            
            // Test Student User 2
            User student2 = new User();
            student2.setUsername("maria");
            student2.setPassword(passwordEncoder.encode("maria123"));
            student2.setFullName("Maria Santos");
            student2.setEmail("maria.santos@sti.edu");
            student2.setStudentId("2021-000002");
            student2.setRole("STUDENT");
            student2.setEnabled(true);
            student2 = userRepository.save(student2);
            
            // Create tuition balance for student2
            StudentBalance balance2 = new StudentBalance();
            balance2.setStudentId("2021-000002");
            balance2.setTotalTuitionFee(38000.00);
            balance2.setAmountPaid(38000.00);
            balance2.setRemainingBalance(0.00);
            balance2.setSemester("1st Semester");
            balance2.setSchoolYear("2024-2025");
            balance2.setUser(student2);
            studentBalanceRepository.save(balance2);
            
            // Test Student User 3
            User student3 = new User();
            student3.setUsername("pedro");
            student3.setPassword(passwordEncoder.encode("pedro123"));
            student3.setFullName("Pedro Cruz");
            student3.setEmail("pedro.cruz@sti.edu");
            student3.setStudentId("2021-000003");
            student3.setRole("STUDENT");
            student3.setEnabled(true);
            student3 = userRepository.save(student3);
            
            // Create tuition balance for student3 (Prefinals scenario: Prelims + Midterms paid)
            StudentBalance balance3 = new StudentBalance();
            balance3.setStudentId("2021-000003");
            balance3.setTotalTuitionFee(38000.00);
            balance3.setAmountPaid(19000.00); // Prelims (9,500) + Midterms (9,500) = 19,000
            balance3.setRemainingBalance(19000.00); // Prefinals (9,500) + Finals (9,500) = 19,000
            balance3.setSemester("1st Semester");
            balance3.setSchoolYear("2024-2025");
            balance3.setUser(student3);
            studentBalanceRepository.save(balance3);
            
            // Test Admin User
            User admin = new User();
            admin.setUsername("admin");
            admin.setPassword(passwordEncoder.encode("admin123"));
            admin.setFullName("Admin User");
            admin.setEmail("admin@sti.edu");
            admin.setRole("ADMIN");
            admin.setEnabled(true);
            userRepository.save(admin);
            
            System.out.println("==============================================");
            System.out.println("Test accounts created:");
            System.out.println("1. Username: student | Password: password123 | Total: ₱38,000 | Paid: ₱0 | All terms unpaid");
            System.out.println("2. Username: maria   | Password: maria123   | Total: ₱38,000 | Paid: ₱38,000 | All terms paid");
            System.out.println("3. Username: pedro   | Password: pedro123   | Total: ₱38,000 | Paid: ₱19,000 | Prelims+Midterms paid, Prefinals+Finals unpaid");
            System.out.println("4. Username: admin   | Password: admin123   | Role: Admin");
            System.out.println("==============================================");
        }
    }
    
    private void updateAllStudentBalances() {
        // Get all student balances
        java.util.List<StudentBalance> allBalances = studentBalanceRepository.findAll();
        int updatedCount = 0;
        double termAmount = 9500.00;
        double totalTuition = 38000.00;
        
        for (StudentBalance balance : allBalances) {
            boolean needsUpdate = false;
            double oldTotal = balance.getTotalTuitionFee() != null ? balance.getTotalTuitionFee() : 0.0;
            double oldPaid = balance.getAmountPaid() != null ? balance.getAmountPaid() : 0.0;
            
            // Update total tuition fee if not 38,000
            if (oldTotal != totalTuition) {
                balance.setTotalTuitionFee(totalTuition);
                needsUpdate = true;
            }
            
            // Round amount paid to nearest valid multiple of 9,500 (term amount)
            // Valid amounts: 0, 9,500, 19,000, 28,500, or 38,000
            double roundedPaid = Math.round(oldPaid / termAmount) * termAmount;
            
            // Ensure it doesn't exceed total tuition
            if (roundedPaid > totalTuition) {
                roundedPaid = totalTuition;
            }
            
            // If amount paid changed or needs recalculation
            if (oldPaid != roundedPaid || needsUpdate) {
                balance.setAmountPaid(roundedPaid);
                balance.setRemainingBalance(totalTuition - roundedPaid);
                needsUpdate = true;
            }
            
            if (needsUpdate) {
                studentBalanceRepository.save(balance);
                updatedCount++;
                
                System.out.println("Updated balance for student: " + balance.getStudentId() + 
                                 " | Old Total: ₱" + oldTotal + " | Old Paid: ₱" + oldPaid +
                                 " | New Total: ₱38,000 | New Paid: ₱" + roundedPaid);
            }
        }
        
        if (updatedCount > 0) {
            System.out.println("==============================================");
            System.out.println("Updated " + updatedCount + " student balance(s)");
            System.out.println("All amounts are now in multiples of ₱9,500");
            System.out.println("==============================================");
        }
    }
}