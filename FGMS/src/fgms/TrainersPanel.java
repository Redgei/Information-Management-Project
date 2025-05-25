    /*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Main.java to edit this template
 */
package fgms;
import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.*;
import java.util.Vector;
import sql.DBConnection;

public class TrainersPanel extends JPanel {
    private GymManagementSystem parent;
    private Color MAROON;
    private Color WHITE;
    private Color BLACK;
    private Color RED;
    private Color GREEN;
    
    private JTable trainersTable;
    private DefaultTableModel tableModel;
    
    public TrainersPanel(GymManagementSystem parent) {
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
        
        JLabel titleLabel = new JLabel("Trainers Management", SwingConstants.CENTER);
        titleLabel.setFont(new Font("Arial", Font.BOLD, 24));
        titleLabel.setForeground(WHITE);
        headerPanel.add(titleLabel, BorderLayout.CENTER);
        
        // Back button
        JButton backButton = new JButton("Back to Dashboard");
        backButton.setBackground(WHITE);
        backButton.setForeground(MAROON);
        backButton.addActionListener(e -> parent.showDashboard());
        headerPanel.add(backButton, BorderLayout.WEST);
        
        add(headerPanel, BorderLayout.NORTH);
        
        // Content panel
        JPanel contentPanel = new JPanel(new BorderLayout());
        contentPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        contentPanel.setBackground(WHITE);
        
        // Table setup
        String[] columnNames = {"ID", "First Name", "Last Name", "Specialization", "Phone", "Email", "Hire Date", "Status"};
        tableModel = new DefaultTableModel(columnNames, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        
        trainersTable = new JTable(tableModel);
        trainersTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        JScrollPane scrollPane = new JScrollPane(trainersTable);
        
        // Button panel
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 10));
        buttonPanel.setBackground(BLACK);
        
        JButton addButton = createButton("Add Trainer", WHITE);
        addButton.setForeground(BLACK);
        JButton editButton = createButton("Edit Trainer", WHITE);
        editButton.setForeground(BLACK);
        JButton deleteButton = createButton("Delete Trainer", WHITE);
        deleteButton.setForeground(BLACK);
        JButton refreshButton = createButton("Refresh", WHITE);
        refreshButton.setForeground(BLACK);
        
        addButton.addActionListener(e -> showAddEditTrainerDialog(null));
        editButton.addActionListener(e -> {
            int selectedRow = trainersTable.getSelectedRow();
            if (selectedRow >= 0) {
                int trainerId = (int) tableModel.getValueAt(selectedRow, 0);
                showAddEditTrainerDialog(trainerId);
            } else {
                JOptionPane.showMessageDialog(this, "Please select a trainer to edit", 
                    "No Selection", JOptionPane.WARNING_MESSAGE);
            }
        });
        
        deleteButton.addActionListener(e -> {
            int selectedRow = trainersTable.getSelectedRow();
            if (selectedRow >= 0) {
                int trainerId = (int) tableModel.getValueAt(selectedRow, 0);
                deleteTrainer(trainerId);
            } else {
                JOptionPane.showMessageDialog(this, "Please select a trainer to delete", 
                    "No Selection", JOptionPane.WARNING_MESSAGE);
            }
        });
        
        refreshButton.addActionListener(e -> loadTrainers());
        
        buttonPanel.add(addButton);
        buttonPanel.add(editButton);
        buttonPanel.add(deleteButton);
        buttonPanel.add(refreshButton);
        
        contentPanel.add(scrollPane, BorderLayout.CENTER);
        contentPanel.add(buttonPanel, BorderLayout.SOUTH);
        
        add(contentPanel, BorderLayout.CENTER);
        
        // Load trainers data
        loadTrainers();
    }
    
    private JButton createButton(String text, Color bgColor) {
        JButton button = new JButton(text);
        button.setBackground(bgColor);
        button.setForeground(WHITE);
        button.setFocusPainted(false);
        return button;
    }
    
   private void loadTrainers() {
    tableModel.setRowCount(0); // Clear existing data

    try (Connection conn = DBConnection.getConnection()) {
        String query = "SELECT Trainer_id, first_name, last_name, specialization, phone, email, hire_date, status " +
                      "FROM trainers ORDER BY last_name";
        PreparedStatement stmt = conn.prepareStatement(query);
        ResultSet rs = stmt.executeQuery();
        
        while (rs.next()) {
            Vector<Object> row = new Vector<>();
            row.add(rs.getInt("Trainer_id"));
            row.add(rs.getString("first_name"));
            row.add(rs.getString("last_name"));
            row.add(rs.getString("specialization"));
            row.add(rs.getString("phone"));
            row.add(rs.getString("email"));
            row.add(rs.getDate("hire_date"));
            row.add(rs.getString("status"));
            tableModel.addRow(row);
        }
    } catch (SQLException e) {
        JOptionPane.showMessageDialog(this, "Error loading trainers: " + e.getMessage(), 
            "Error", JOptionPane.ERROR_MESSAGE);
    }
}
    private void showAddEditTrainerDialog(Integer trainerId) {
        JDialog dialog = new JDialog();
        dialog.setTitle(trainerId == null ? "Add New Trainer" : "Edit Trainer");
        dialog.setSize(500, 400);
        dialog.setLocationRelativeTo(this);
        dialog.setModal(true);
        
        JPanel panel = new JPanel(new GridLayout(0, 2, 10, 10));
        panel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        
        // Form fields
        JTextField firstNameField = new JTextField();
        JTextField lastNameField = new JTextField();
        JTextField specializationField = new JTextField();
        JTextField phoneField = new JTextField();
        JTextField emailField = new JTextField();
        JTextField hireDateField = new JTextField();
        JComboBox<String> statusCombo = new JComboBox<>(new String[]{"Active", "Inactive"});
        
        // If editing, load existing data
        if (trainerId != null) {
            try (Connection conn = DBConnection.getConnection()) {
                String query = "SELECT * FROM trainers WHERE trainer_id = ?";
                PreparedStatement stmt = conn.prepareStatement(query);
                stmt.setInt(1, trainerId);
                ResultSet rs = stmt.executeQuery();
                
                if (rs.next()) {
                    firstNameField.setText(rs.getString("first_name"));
                    lastNameField.setText(rs.getString("last_name"));
                    specializationField.setText(rs.getString("specialization"));
                    phoneField.setText(rs.getString("phone"));
                    emailField.setText(rs.getString("email"));
                    hireDateField.setText(rs.getDate("hire_date").toString());
                    statusCombo.setSelectedItem(rs.getString("status"));
                }
            } catch (SQLException e) {
                JOptionPane.showMessageDialog(this, "Error loading trainer data: " + e.getMessage(), 
                    "Error", JOptionPane.ERROR_MESSAGE);
            }
        }
        
        // Add fields to panel
        panel.add(new JLabel("First Name:"));
        panel.add(firstNameField);
        panel.add(new JLabel("Last Name:"));
        panel.add(lastNameField);
        panel.add(new JLabel("Specialization:"));
        panel.add(specializationField);
        panel.add(new JLabel("Phone:"));
        panel.add(phoneField);
        panel.add(new JLabel("Email:"));
        panel.add(emailField);
        panel.add(new JLabel("Hire Date (YYYY-MM-DD):"));
        panel.add(hireDateField);
        panel.add(new JLabel("Status:"));
        panel.add(statusCombo);
        
        // Buttons
        JButton saveButton = new JButton("Save");
        saveButton.setBackground(WHITE);
        saveButton.setForeground(BLACK);
        saveButton.addActionListener(e -> {
            try {
                if (trainerId == null) {
                    // Add new trainer
                    addTrainer(
                        firstNameField.getText(),
                        lastNameField.getText(),
                        specializationField.getText(),
                        phoneField.getText(),
                        emailField.getText(),
                        hireDateField.getText(),
                        (String) statusCombo.getSelectedItem()
                    );
                } else {
                    // Update existing trainer
                    updateTrainer(
                        trainerId,
                        firstNameField.getText(),
                        lastNameField.getText(),
                        specializationField.getText(),
                        phoneField.getText(),
                        emailField.getText(),
                        hireDateField.getText(),
                        (String) statusCombo.getSelectedItem()
                    );
                }
                dialog.dispose();
                loadTrainers();
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(dialog, "Error saving trainer: " + ex.getMessage(), 
                    "Error", JOptionPane.ERROR_MESSAGE);
            }
        });
        
        JButton cancelButton = new JButton("Cancel");
        cancelButton.setBackground(WHITE);
        cancelButton.setForeground(BLACK);
        cancelButton.addActionListener(e -> dialog.dispose());
        
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 10));
        buttonPanel.add(saveButton);
        buttonPanel.add(cancelButton);
        
        dialog.add(panel, BorderLayout.CENTER);
        dialog.add(buttonPanel, BorderLayout.SOUTH);
        dialog.setVisible(true);
    }
    
   private void addTrainer(String firstName, String lastName, String specialization, 
                      String phone, String email, String hireDate, String status) throws SQLException {
    String query = "INSERT INTO trainers (first_name, last_name, specialization, phone, " +
                  "email, hire_date, status) VALUES (?, ?, ?, ?, ?, ?, ?)";
    
    try (Connection conn = DBConnection.getConnection();
         PreparedStatement stmt = conn.prepareStatement(query, Statement.RETURN_GENERATED_KEYS)) {
        
        // Validate required fields
        if (firstName.isEmpty() || lastName.isEmpty() || specialization.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Please fill in all required fields", 
                "Validation Error", JOptionPane.WARNING_MESSAGE);
            return;
        }

        stmt.setString(1, firstName);
        stmt.setString(2, lastName);
        stmt.setString(3, specialization);
        stmt.setString(4, phone);
        stmt.setString(5, email);
        
        try {
            stmt.setDate(6, Date.valueOf(hireDate));
        } catch (IllegalArgumentException e) {
            JOptionPane.showMessageDialog(this, "Invalid date format. Please use YYYY-MM-DD", 
                "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }
        
        stmt.setString(7, status);
        
        int affectedRows = stmt.executeUpdate();
        
        if (affectedRows == 0) {
            throw new SQLException("Creating trainer failed, no rows affected.");
        }
        
        // Try to get the generated ID
        try (ResultSet generatedKeys = stmt.getGeneratedKeys()) {
            if (generatedKeys.next()) {
                JOptionPane.showMessageDialog(this, "Trainer added successfully with ID: " + generatedKeys.getInt(1), 
                    "Success", JOptionPane.INFORMATION_MESSAGE);
            } else {
                JOptionPane.showMessageDialog(this, "Trainer added successfully (could not retrieve ID)", 
                    "Success", JOptionPane.INFORMATION_MESSAGE);
            }
        }
    } catch (SQLException e) {
        JOptionPane.showMessageDialog(this, "Error adding trainer: " + e.getMessage(), 
            "Error", JOptionPane.ERROR_MESSAGE);
        throw e; // Re-throw the exception after showing the message
    }
}
    
    private void updateTrainer(int trainerId, String firstName, String lastName, String specialization, 
                             String phone, String email, String hireDate, String status) throws SQLException {
        String query = "UPDATE trainers SET first_name = ?, last_name = ?, specialization = ?, " +
                      "phone = ?, email = ?, hire_date = ?, status = ? WHERE trainer_id = ?";
        
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {
            
            stmt.setString(1, firstName);
            stmt.setString(2, lastName);
            stmt.setString(3, specialization);
            stmt.setString(4, phone);
            stmt.setString(5, email);
            stmt.setDate(6, Date.valueOf(hireDate));
            stmt.setString(7, status);
            stmt.setInt(8, trainerId);
            
            int rowsAffected = stmt.executeUpdate();
            if (rowsAffected > 0) {
                JOptionPane.showMessageDialog(this, "Trainer updated successfully", 
                    "Success", JOptionPane.INFORMATION_MESSAGE);
            }
        }
    }
    
    private void deleteTrainer(int trainerId) {
        int confirm = JOptionPane.showConfirmDialog(this, 
            "Are you sure you want to delete this trainer?", 
            "Confirm Deletion", JOptionPane.YES_NO_OPTION);
        
        if (confirm == JOptionPane.YES_OPTION) {
            try (Connection conn = DBConnection.getConnection()) {
                String query = "DELETE FROM trainers WHERE trainer_id = ?";
                PreparedStatement stmt = conn.prepareStatement(query);
                stmt.setInt(1, trainerId);
                
                int rowsAffected = stmt.executeUpdate();
                if (rowsAffected > 0) {
                    JOptionPane.showMessageDialog(this, "Trainer deleted successfully", 
                        "Success", JOptionPane.INFORMATION_MESSAGE);
                    loadTrainers();
                }
            } catch (SQLException e) {
                JOptionPane.showMessageDialog(this, "Error deleting trainer: " + e.getMessage(), 
                    "Error", JOptionPane.ERROR_MESSAGE);
            }
        }
    }
}