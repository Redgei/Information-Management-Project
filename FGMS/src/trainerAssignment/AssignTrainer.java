/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package trainerAssignment;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.*;
import java.util.HashMap;
import java.util.Map;
import sql.DBConnection;

public class AssignTrainer extends JFrame {
    private JComboBox<String> memberCombo;
    private JComboBox<String> trainerCombo;
    private Map<String, Integer> memberMap = new HashMap<>();
    private Map<String, Integer> trainerMap = new HashMap<>();

    public AssignTrainer() {
        setTitle("Assign Trainer to Member");
        setSize(400, 200);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        
        JPanel mainPanel = new JPanel(new GridLayout(3, 2, 10, 10));
        mainPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        
        // Member selection
        mainPanel.add(new JLabel("Select Member:"));
        memberCombo = new JComboBox<>();
        mainPanel.add(memberCombo);
        
        // Trainer selection
        mainPanel.add(new JLabel("Select Trainer:"));
        trainerCombo = new JComboBox<>();
        mainPanel.add(trainerCombo);
        
        // Buttons
        JButton assignButton = new JButton("Assign Trainer");
        assignButton.addActionListener(e -> assignTrainer());
        mainPanel.add(assignButton);
        
        JButton cancelButton = new JButton("Cancel");
        cancelButton.addActionListener(e -> dispose());
        mainPanel.add(cancelButton);
        
        add(mainPanel);
        
        loadMembers();
        loadTrainers();
    }
    
    private void loadMembers() {
        memberCombo.removeAllItems();
        memberMap.clear();
        
        try (Connection conn = DBConnection.getConnection()) {
            String query = "SELECT member_id, first_name, last_name FROM members ORDER BY last_name";
            PreparedStatement stmt = conn.prepareStatement(query);
            ResultSet rs = stmt.executeQuery();
            
            while (rs.next()) {
                String displayText = rs.getString("first_name") + " " + rs.getString("last_name");
                memberCombo.addItem(displayText);
                memberMap.put(displayText, rs.getInt("member_id"));
            }
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, "Error loading members: " + e.getMessage(), 
                "Error", JOptionPane.ERROR_MESSAGE);
        }
    }
    
    private void loadTrainers() {
        trainerCombo.removeAllItems();
        trainerMap.clear();
        
        try (Connection conn = DBConnection.getConnection()) {
            String query = "SELECT trainer_id, first_name, last_name FROM trainers WHERE status = 'Active' ORDER BY last_name";
            PreparedStatement stmt = conn.prepareStatement(query);
            ResultSet rs = stmt.executeQuery();
            
            while (rs.next()) {
                String displayText = rs.getString("first_name") + " " + rs.getString("last_name");
                trainerCombo.addItem(displayText);
                trainerMap.put(displayText, rs.getInt("trainer_id"));
            }
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, "Error loading trainers: " + e.getMessage(), 
                "Error", JOptionPane.ERROR_MESSAGE);
        }
    }
    
    private void assignTrainer() {
        String selectedMember = (String) memberCombo.getSelectedItem();
        String selectedTrainer = (String) trainerCombo.getSelectedItem();
        
        if (selectedMember == null || selectedTrainer == null) {
            JOptionPane.showMessageDialog(this, "Please select both a member and a trainer", 
                "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }
        
        int memberId = memberMap.get(selectedMember);
        int trainerId = trainerMap.get(selectedTrainer);
        
        try (Connection conn = DBConnection.getConnection()) {
            String query = "UPDATE members SET trainer_id = ? WHERE member_id = ?";
            PreparedStatement stmt = conn.prepareStatement(query);
            stmt.setInt(1, trainerId);
            stmt.setInt(2, memberId);
            
            int rowsAffected = stmt.executeUpdate();
            if (rowsAffected > 0) {
                JOptionPane.showMessageDialog(this, "Trainer assigned successfully!", 
                    "Success", JOptionPane.INFORMATION_MESSAGE);
                dispose();
            } else {
                JOptionPane.showMessageDialog(this, "Failed to assign trainer", 
                    "Error", JOptionPane.ERROR_MESSAGE);
            }
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, "Error assigning trainer: " + e.getMessage(), 
                "Error", JOptionPane.ERROR_MESSAGE);
        }
    }
}