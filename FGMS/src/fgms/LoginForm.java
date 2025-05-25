/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package fgms;
import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.*;
import sql.DBConnection;

public class LoginForm extends JFrame {
    private JTextField usernameField;
    private JPasswordField passwordField;
    private Color MAROON = new Color(128, 0, 0);
    private Color WHITE = Color.WHITE;
    private Color BLACK = Color.BLACK;
    public LoginForm() {
        setTitle("Gym Management System - Login");
        setSize(500, 400);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setResizable(false);
        
        JPanel mainPanel = new JPanel(new BorderLayout());
        mainPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        mainPanel.setBackground(MAROON);
        
        // Header panel
        JPanel headerPanel = new JPanel();
        headerPanel.setBackground(WHITE);
        headerPanel.setPreferredSize(new Dimension(0, 60));
        
        JLabel titleLabel = new JLabel("GYM MANAGEMENT SYSTEM", SwingConstants.CENTER);
        titleLabel.setFont(new Font("Arial", Font.BOLD, 18));
        titleLabel.setForeground(BLACK);
        headerPanel.add(titleLabel);
        
        // Form panel
        JPanel formPanel = new JPanel(new GridLayout(0, 1, 10, 10));
        formPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        formPanel.setBackground(WHITE);
        
        JLabel usernameLabel = new JLabel("Username:");
        usernameLabel.setFont(new Font("Arial", Font.BOLD, 13));
        usernameField = new JTextField();
       usernameField = new JTextField(20); 


        JLabel passwordLabel = new JLabel("Password:");
        passwordLabel.setFont(new Font("Arial", Font.BOLD, 13));
        passwordField = new JPasswordField();
      passwordField = new JPasswordField(25);


        formPanel.add(usernameLabel);
        formPanel.add(usernameField);
        formPanel.add(passwordLabel);
        formPanel.add(passwordField);
  
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 20, 10));
        buttonPanel.setBackground(WHITE);
        
        JButton loginButton = new JButton("Login");
        loginButton.setBackground(WHITE);
        loginButton.setForeground(BLACK);
        loginButton.setPreferredSize(new Dimension(120, 35));
        loginButton.addActionListener(new LoginButtonListener());
        
        buttonPanel.add(loginButton);
        
        mainPanel.add(headerPanel, BorderLayout.NORTH);
        mainPanel.add(formPanel, BorderLayout.CENTER);
        mainPanel.add(buttonPanel, BorderLayout.SOUTH);
        
        add(mainPanel);
    }
    
    private class LoginButtonListener implements ActionListener {
        @Override
        public void actionPerformed(ActionEvent e) {
            String username = usernameField.getText().trim();
            String password = new String(passwordField.getPassword());
            
            if (username.isEmpty() || password.isEmpty()) {
                JOptionPane.showMessageDialog(LoginForm.this, 
                    "Please enter both username and password", 
                    "Login Error", JOptionPane.ERROR_MESSAGE);
                return;
            }
            
            try (Connection conn = DBConnection.getConnection()) {
                String query = "SELECT * FROM users WHERE username = ? AND password = ?";
                PreparedStatement stmt = conn.prepareStatement(query);
                stmt.setString(1, username);
                stmt.setString(2, password); // 
                
                ResultSet rs = stmt.executeQuery();
                
                if (rs.next()) {
                  
                    dispose(); 
                    new GymManagementSystem().setVisible(true); 
                } else {
                    JOptionPane.showMessageDialog(LoginForm.this, 
                        "Invalid username or password", 
                        "Login Failed", JOptionPane.ERROR_MESSAGE);
                }
            } catch (SQLException ex) {
                JOptionPane.showMessageDialog(LoginForm.this, 
                    "Database error: " + ex.getMessage(), 
                    "Error", JOptionPane.ERROR_MESSAGE);
            }
        }
    }
    
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            try {
                UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
                
       
                createUsersTable();
                
             
                LoginForm loginForm = new LoginForm();
                loginForm.setVisible(true);
            } catch (Exception e) {
                e.printStackTrace();
            }
        });
    }
    
    private static void createUsersTable() {
        try (Connection conn = DBConnection.getConnection()) {
           
            DatabaseMetaData dbm = conn.getMetaData();
            ResultSet tables = dbm.getTables(null, null, "users", null);
            
            if (!tables.next()) {
              
                String createTableSQL = "CREATE TABLE users (" +
                                       "user_id INT AUTO_INCREMENT PRIMARY KEY, " +
                                       "username VARCHAR(50) NOT NULL UNIQUE, " +
                                       "password VARCHAR(100) NOT NULL)";
                
                try (Statement stmt = conn.createStatement()) {
                    stmt.execute(createTableSQL);
                    
                  
                    String insertAdminSQL = "INSERT INTO users (username, password) " +
                                          "VALUES ('admin', 'admin123')";
                    stmt.execute(insertAdminSQL);
                }
            }
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(null, 
                "Error initializing database: " + e.getMessage(), 
                "Database Error", JOptionPane.ERROR_MESSAGE);
            System.exit(1);
        }
    }
}