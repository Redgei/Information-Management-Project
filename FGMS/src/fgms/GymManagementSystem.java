/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Main.java to edit this template
 */
package fgms;

/**
 *
 * @author Administrator
 */
import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.Connection;

public class GymManagementSystem extends JFrame {
    private JPanel mainPanel;
    private CardLayout cardLayout;
    
    // Color palette
    final Color MAROON = new Color(128, 0, 0);
    final Color WHITE = Color.WHITE;
    final Color BLACK = Color.BLACK;
    final Color RED = Color.RED;
    final Color GREEN = new Color(0, 100, 0);
    
    public GymManagementSystem() {
        setTitle("Fitness Gym Management System");
        setSize(1000, 600);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        
        // Main panel with CardLayout
        cardLayout = new CardLayout();
        mainPanel = new JPanel(cardLayout);
        mainPanel.setBackground(WHITE);
        
        // Create different panels
        DashboardPanel dashboardPanel = new DashboardPanel(this);
        MembersPanel membersPanel = new MembersPanel(this);
        TrainersPanel trainersPanel = new TrainersPanel(this);
        PaymentsPanel paymentsPanel = new PaymentsPanel(this);
        
        // Footer panel - MODIFY ONLY THIS SECTION
// Footer panel - MODIFIED WITH CONFIRMATION DIALOG
JPanel footerPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
footerPanel.setBackground(MAROON);
footerPanel.setPreferredSize(new Dimension(0, 50));

// Add logout button with confirmation
JButton logoutButton = new JButton("Logout");
logoutButton.setBackground(WHITE);
logoutButton.setSize(25,25);
logoutButton.setForeground(BLACK);
logoutButton.setFocusPainted(false);
logoutButton.addActionListener(e -> {
    // Show confirmation dialog
    int confirm = JOptionPane.showConfirmDialog(
        this,
        "Are you sure you want to logout?",
        "Confirm Logout",
        JOptionPane.YES_NO_OPTION,
        JOptionPane.QUESTION_MESSAGE
    );
    
    // Only proceed if user clicks YES
    if (confirm == JOptionPane.YES_OPTION) {
        this.dispose(); // Close the main window
        new LoginForm().setVisible(true); // Show login form again
    }
});

footerPanel.add(logoutButton);
add(footerPanel, BorderLayout.SOUTH);
        // Add panels to main panel
        mainPanel.add(dashboardPanel, "Dashboard");
        mainPanel.add(membersPanel, "Members");
        mainPanel.add(trainersPanel, "Trainers");
        mainPanel.add(paymentsPanel, "Payments");
        
        
        add(mainPanel);
        
        // Show dashboard first
        showDashboard();
    }
    
    public void showDashboard() {
        cardLayout.show(mainPanel, "Dashboard");
    }
    
    public void showMembersPanel() {
        cardLayout.show(mainPanel, "Members");
    }
    
    public void showTrainersPanel() {
        cardLayout.show(mainPanel, "Trainers");
    }
    
    public void showPaymentsPanel() {
        cardLayout.show(mainPanel, "Payments");
    }
    
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            try {
                UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
                GymManagementSystem system = new GymManagementSystem();
                system.setVisible(true);
            } catch (Exception e) {
                e.printStackTrace();
            }
        });
    }
}