/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Main.java to edit this template
 */
package fgms;

 import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import sql.DBConnection;

public class DashboardPanel extends JPanel {
    private GymManagementSystem parent;
    private Color MAROON;
    private Color WHITE;
    private Color BLACK;
    private Color RED;
    private Color GREEN;
    
    public DashboardPanel(GymManagementSystem parent) {
        this.parent = parent;
        this.MAROON = parent.MAROON;
        this.WHITE = parent.WHITE;
        this.BLACK = parent.BLACK;
        this.RED = parent.RED;
        this.GREEN = parent.GREEN;
        
        setLayout(new BorderLayout());
        setBackground(WHITE);
        
        // Header panel
        JPanel headerPanel = new JPanel();
        headerPanel.setBackground(MAROON);
        headerPanel.setPreferredSize(new Dimension(0, 80));
        headerPanel.setLayout(new BorderLayout());
        
        JLabel titleLabel = new JLabel("Fitness Gym Management System", SwingConstants.CENTER);
        titleLabel.setFont(new Font("Arial", Font.BOLD, 24));
        titleLabel.setForeground(WHITE);
        headerPanel.add(titleLabel, BorderLayout.CENTER);
        
        add(headerPanel, BorderLayout.NORTH);
        
        // Content panel
        JPanel contentPanel = new JPanel(new GridLayout(2, 2, 20, 20));
        contentPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        contentPanel.setBackground(WHITE);
        
        // Dashboard cards
        JPanel membersCard = createDashboardCard("Members", "Manage gym members", "members.png", GREEN);
        JPanel trainersCard = createDashboardCard("Trainers", "Manage gym trainers", "trainers.png", MAROON);
        JPanel paymentsCard = createDashboardCard("Payments", "Manage payments and finances", "payments.png", RED);
        JPanel statsCard = createDashboardCard("Statistics", "View gym statistics", "stats.png", BLACK);
        
        contentPanel.add(membersCard);
        contentPanel.add(trainersCard);
        contentPanel.add(paymentsCard);
        contentPanel.add(statsCard);
        
        add(contentPanel, BorderLayout.CENTER);
        
        // Footer panel
        JPanel footerPanel = new JPanel();
        footerPanel.setBackground(MAROON);
        footerPanel.setPreferredSize(new Dimension(0, 50));
        add(footerPanel, BorderLayout.SOUTH);
    }
    
    private JPanel createDashboardCard(String title, String description, String iconPath, Color color) {
        JPanel card = new JPanel(new BorderLayout());
        card.setBackground(WHITE);
        card.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(color, 2),
            BorderFactory.createEmptyBorder(10, 10, 10, 10)
        ));
        
        // Icon placeholder
        JLabel iconLabel = new JLabel(new ImageIcon(iconPath));
        iconLabel.setHorizontalAlignment(SwingConstants.CENTER);
        card.add(iconLabel, BorderLayout.NORTH);
        
        // Title
        JLabel titleLabel = new JLabel(title, SwingConstants.CENTER);
        titleLabel.setFont(new Font("Arial", Font.BOLD, 18));
        titleLabel.setForeground(color);
        card.add(titleLabel, BorderLayout.CENTER);
        
        // Description
        JLabel descLabel = new JLabel(description, SwingConstants.CENTER);
        descLabel.setFont(new Font("Arial", Font.PLAIN, 12));
        card.add(descLabel, BorderLayout.SOUTH);
        
        // Add click listener
        card.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                switch (title) {
                    case "Members":
                        parent.showMembersPanel();
                        break;
                    case "Trainers":
                        parent.showTrainersPanel();
                        break;
                    case "Payments":
                        parent.showPaymentsPanel();
                        break;
                    case "Statistics":
                        // Show statistics dialog
                        showStatisticsDialog();
                        break;
                }
            }
        });
        
        return card;
    }
    
    private void showStatisticsDialog() {
        try (Connection conn = DBConnection.getConnection()) {
            // Get member count
            String memberCountQuery = "SELECT COUNT(*) FROM members";
            PreparedStatement memberStmt = conn.prepareStatement(memberCountQuery);
            ResultSet memberRs = memberStmt.executeQuery();
            memberRs.next();
            int memberCount = memberRs.getInt(1);
            
            // Get trainer count
            String trainerCountQuery = "SELECT COUNT(*) FROM trainers";
            PreparedStatement trainerStmt = conn.prepareStatement(trainerCountQuery);
            ResultSet trainerRs = trainerStmt.executeQuery();
            trainerRs.next();
            int trainerCount = trainerRs.getInt(1);
            
            // Get payment stats
            String paymentStatsQuery = "SELECT SUM(amount), AVG(amount), MAX(amount), MIN(amount) FROM payments";
            PreparedStatement paymentStmt = conn.prepareStatement(paymentStatsQuery);
            ResultSet paymentRs = paymentStmt.executeQuery();
            paymentRs.next();
            double totalPayments = paymentRs.getDouble(1);
            double avgPayment = paymentRs.getDouble(2);
            double maxPayment = paymentRs.getDouble(3);
            double minPayment = paymentRs.getDouble(4);
            
            // Create and show dialog
            JDialog statsDialog = new JDialog();
            statsDialog.setTitle("Gym Statistics");
            statsDialog.setSize(400, 300);
            statsDialog.setLocationRelativeTo(this);
            
            JPanel statsPanel = new JPanel(new GridLayout(0, 1, 10, 10));
            statsPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
            
            statsPanel.add(createStatLabel("Total Members: " + memberCount, MAROON));
            statsPanel.add(createStatLabel("Total Trainers: " + trainerCount, GREEN));
            statsPanel.add(createStatLabel("Total Payments: $" + String.format("%.2f", totalPayments), BLACK));
            statsPanel.add(createStatLabel("Average Payment: $" + String.format("%.2f", avgPayment), BLACK));
            statsPanel.add(createStatLabel("Maximum Payment: $" + String.format("%.2f", maxPayment), BLACK));
            statsPanel.add(createStatLabel("Minimum Payment: $" + String.format("%.2f", minPayment), BLACK));
            
            statsDialog.add(statsPanel);
            statsDialog.setVisible(true);
            
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, "Error retrieving statistics: " + e.getMessage(), 
                "Error", JOptionPane.ERROR_MESSAGE);
        }
    }
    
    private JLabel createStatLabel(String text, Color color) {
        JLabel label = new JLabel(text, SwingConstants.CENTER);
        label.setFont(new Font("Arial", Font.BOLD, 16));
        label.setForeground(color);
        return label;
    }
}