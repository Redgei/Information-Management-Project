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

public class PaymentsPanel extends JPanel {
    private GymManagementSystem parent;
    private Color MAROON;
    private Color WHITE;
    private Color BLACK;
    private Color RED;
    private Color GREEN;
    
    private JTable paymentsTable;
    private DefaultTableModel tableModel;

    
    public PaymentsPanel(GymManagementSystem parent) {
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
        
        JLabel titleLabel = new JLabel("Payments Management", SwingConstants.CENTER);
        titleLabel.setFont(new Font("Arial", Font.BOLD, 24));
        titleLabel.setForeground(WHITE);
        headerPanel.add(titleLabel, BorderLayout.CENTER);
        
        // Back button
        JButton backButton = new JButton("Back to Dashboard");
        backButton.setBackground(WHITE);
        backButton.setForeground(RED);
        backButton.addActionListener(e -> parent.showDashboard());
        headerPanel.add(backButton, BorderLayout.WEST);
        
        add(headerPanel, BorderLayout.NORTH);
        
        // Content panel
        JPanel contentPanel = new JPanel(new BorderLayout());
        contentPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        contentPanel.setBackground(WHITE);
        
        // Filter panel
//        JPanel filterPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 10));
//        filterPanel.setBackground(WHITE);
//        
//        
//        memberCombo = new JComboBox<>();
//        memberCombo.addItem("All Members");
//        loadMembersToCombo();
//        
//        JButton filterButton = createButton("Filter", WHITE);
//        filterButton.setForeground(BLACK); 
//        filterButton.addActionListener(e -> loadPayments());
//        
//        filterPanel.add(new JLabel("Filter by Member:"));
//        filterPanel.add(memberCombo);
//        filterPanel.add(filterButton);
        
// In the constructor, replace the search panel with this:
JPanel searchPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 10));
searchPanel.setBackground(WHITE);

JTextField searchField = new JTextField(15);
JButton searchButton = createButton("Search by ID", WHITE);
searchButton.setForeground(BLACK);

searchButton.addActionListener(e -> {
    String searchText = searchField.getText().trim();
    if (searchText.isEmpty()) {
        loadPayments(); // Load all if search is empty
    } else {
        try {
            int paymentId = Integer.parseInt(searchText);
            searchPaymentById(paymentId);
        } catch (NumberFormatException ex) {
            JOptionPane.showMessageDialog(this, "Please enter a valid Payment ID number", 
                "Invalid Input", JOptionPane.WARNING_MESSAGE);
        }
    }
});

searchPanel.add(new JLabel("Search by Payment ID:"));
searchPanel.add(searchField);
searchPanel.add(searchButton);

contentPanel.add(searchPanel, BorderLayout.NORTH);

        // Table setup
        String[] columnNames = {"Payment_id", "Member Name", "Payment Date", "Amount", "Method", "Payment For"};
        tableModel = new DefaultTableModel(columnNames, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        
        paymentsTable = new JTable(tableModel);
        paymentsTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        JScrollPane scrollPane = new JScrollPane(paymentsTable);
        
        // Button panel
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 10));
        buttonPanel.setBackground(BLACK);
        
        JButton addButton = createButton("Add Payment", WHITE);
        addButton.setForeground(BLACK);
        JButton editButton = createButton("Edit Payment", WHITE);
        editButton.setForeground(BLACK);
        JButton deleteButton = createButton("Delete Payment", WHITE);
        deleteButton.setForeground(BLACK);
        JButton refreshButton = createButton("Refresh", WHITE);
        refreshButton.setForeground(BLACK);
        JButton statsButton = createButton("Payment Stats", WHITE);
        statsButton.setForeground(BLACK);
        
        addButton.addActionListener(e -> showAddEditPaymentDialog(null));
        editButton.addActionListener(e -> {
            int selectedRow = paymentsTable.getSelectedRow();
            if (selectedRow >= 0) {
                int paymentId = (int) tableModel.getValueAt(selectedRow, 0);
                showAddEditPaymentDialog(paymentId);
            } else {
                JOptionPane.showMessageDialog(this, "Please select a payment to edit", 
                    "No Selection", JOptionPane.WARNING_MESSAGE);
            }
        });
        
        deleteButton.addActionListener(e -> {
            int selectedRow = paymentsTable.getSelectedRow();
            if (selectedRow >= 0) {
                int paymentId = (int) tableModel.getValueAt(selectedRow, 0);
                deletePayment(paymentId);
            } else {
                JOptionPane.showMessageDialog(this, "Please select a payment to delete", 
                    "No Selection", JOptionPane.WARNING_MESSAGE);
            }
        });
        
        refreshButton.addActionListener(e -> loadPayments());
        
        statsButton.addActionListener(e -> showPaymentStats());
        
        buttonPanel.add(addButton);
        buttonPanel.add(editButton);
        buttonPanel.add(deleteButton);
        buttonPanel.add(refreshButton);
        buttonPanel.add(statsButton);
        
        contentPanel.add(searchPanel, BorderLayout.NORTH);
        contentPanel.add(scrollPane, BorderLayout.CENTER);
        contentPanel.add(buttonPanel, BorderLayout.SOUTH);
        
        add(contentPanel, BorderLayout.CENTER);
        
        // Load payments data
        loadPayments();
    }
    
    private JButton createButton(String text, Color bgColor) {
        JButton button = new JButton(text);
        button.setBackground(bgColor);
        button.setForeground(WHITE);
        button.setFocusPainted(false);
        return button;
    }
    
//    private void loadMembersToCombo() {
//        try (Connection conn = DBConnection.getConnection()) {
//            String query = "SELECT member_id, first_name, last_name FROM members ORDER BY last_name, first_name";
//            PreparedStatement stmt = conn.prepareStatement(query);
//            ResultSet rs = stmt.executeQuery();
//            
//            while (rs.next()) {
//                memberCombo.addItem(rs.getInt("member_id") + " - " + 
//                                  rs.getString("first_name") + " " + rs.getString("last_name"));
//            }
//        } catch (SQLException e) {
//            JOptionPane.showMessageDialog(this, "Error loading members: " + e.getMessage(), 
//                "Error", JOptionPane.ERROR_MESSAGE);
//        }
//    }
    
//    private void loadPayments() {
//        tableModel.setRowCount(0); // Clear existing data
//        
//        String selectedMember = (String) memberCombo.getSelectedItem();
//        int memberId = -1;
//        
//        if (selectedMember != null && !selectedMember.equals("All Members")) {
//            String[] parts = selectedMember.split(" - ");
//            memberId = Integer.parseInt(parts[0]);
//        }
//        
//        try (Connection conn = DBConnection.getConnection()) {
//            String query;
//            PreparedStatement stmt;
//            
//            if (memberId == -1) {
//                query = "SELECT p.*, m.first_name, m.last_name FROM payments p " +
//                       "LEFT JOIN members m ON p.member_id = m.member_id " +
//                       "ORDER BY p.payment_date DESC";
//                stmt = conn.prepareStatement(query);
//            } else {
//                query = "SELECT p.*, m.first_name, m.last_name FROM payments p " +
//                       "LEFT JOIN members m ON p.member_id = m.member_id " +
//                       "WHERE p.member_id = ? ORDER BY p.payment_date DESC";
//                stmt = conn.prepareStatement(query);
//                stmt.setInt(1, memberId);
//            }
//            
//            ResultSet rs = stmt.executeQuery();
//            
//            while (rs.next()) {
//                Vector<Object> row = new Vector<>();
//                row.add(rs.getInt("payment_id"));
//                row.add(rs.getString("first_name") + " " + rs.getString("last_name"));
//                row.add(rs.getDate("payment_date"));
//                row.add(rs.getDouble("amount"));
//                row.add(rs.getString("payment_method"));
//                row.add(rs.getString("payment_for"));
//                tableModel.addRow(row);
//            }
//        } catch (SQLException e) {
//            JOptionPane.showMessageDialog(this, "Error loading payments: " + e.getMessage(), 
//                "Error", JOptionPane.ERROR_MESSAGE);
//        }
//    }
    
    private void loadPayments() {
    tableModel.setRowCount(0); // Clear existing data
    
    try (Connection conn = DBConnection.getConnection()) {
        String query = "SELECT p.*, m.first_name, m.last_name FROM payments p " +
                     "LEFT JOIN members m ON p.member_id = m.member_id " +
                     "ORDER BY p.payment_date DESC";
        
        PreparedStatement stmt = conn.prepareStatement(query);
        ResultSet rs = stmt.executeQuery();
        
        while (rs.next()) {
            Vector<Object> row = new Vector<>();
            row.add(rs.getInt("payment_id"));
            row.add(rs.getString("first_name") + " " + rs.getString("last_name"));
            row.add(rs.getDate("payment_date"));
            row.add(rs.getDouble("amount"));
            row.add(rs.getString("payment_method"));
            row.add(rs.getString("payment_for"));
            tableModel.addRow(row);
        }
    } catch (SQLException e) {
        JOptionPane.showMessageDialog(this, "Error loading payments: " + e.getMessage(), 
            "Error", JOptionPane.ERROR_MESSAGE);
    }
}
    
//    private void showAddEditPaymentDialog(Integer paymentId) {
//        JDialog dialog = new JDialog();
//        dialog.setTitle(paymentId == null ? "Add New Payment" : "Edit Payment");
//        dialog.setSize(500, 400);
//        dialog.setLocationRelativeTo(this);
//        dialog.setModal(true);
//        
//        JPanel panel = new JPanel(new GridLayout(0, 2, 10, 10));
//        panel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
//        
//        // Form fields
//        JComboBox<String> memberSelectionCombo = new JComboBox<>();
//        JTextField paymentDateField = new JTextField();
//        JTextField amountField = new JTextField();
//        JComboBox<String> methodCombo = new JComboBox<>(new String[]{"Cash", "Credit Card", "Debit Card", "Bank Transfer"});
//        JTextField paymentForField = new JTextField();
//        
//        // Load members to combo
//        try (Connection conn = DBConnection.getConnection()) {
//            String query = "SELECT member_id, first_name, last_name FROM members ORDER BY last_name, first_name";
//            PreparedStatement stmt = conn.prepareStatement(query);
//            ResultSet rs = stmt.executeQuery();
//            
//            while (rs.next()) {
//                memberSelectionCombo.addItem(rs.getInt("member_id") + " - " + 
//                                           rs.getString("first_name") + " " + rs.getString("last_name"));
//            }
//        } catch (SQLException e) {
//            JOptionPane.showMessageDialog(this, "Error loading members: " + e.getMessage(), 
//                "Error", JOptionPane.ERROR_MESSAGE);
//        }
//        
//        // If editing, load existing data
//        if (paymentId != null) {
//            try (Connection conn = DBConnection.getConnection()) {
//                String query = "SELECT p.*, m.first_name, m.last_name FROM payments p " +
//                              "LEFT JOIN members m ON p.member_id = m.member_id " +
//                              "WHERE p.payment_id = ?";
//                PreparedStatement stmt = conn.prepareStatement(query);
//                stmt.setInt(1, paymentId);
//                ResultSet rs = stmt.executeQuery();
//                
//                if (rs.next()) {
//                    // Set member in combo
//                    String memberText = rs.getInt("member_id") + " - " + 
//                                      rs.getString("first_name") + " " + rs.getString("last_name");
//                    memberSelectionCombo.setSelectedItem(memberText);
//                    
//                    paymentDateField.setText(rs.getDate("payment_date").toString());
//                    amountField.setText(String.valueOf(rs.getDouble("amount")));
//                    methodCombo.setSelectedItem(rs.getString("payment_method"));
//                    paymentForField.setText(rs.getString("payment_for"));
//                }
//            } catch (SQLException e) {
//                JOptionPane.showMessageDialog(this, "Error loading payment data: " + e.getMessage(), 
//                    "Error", JOptionPane.ERROR_MESSAGE);
//            }
//        }
//        
//        // Add fields to panel
//        panel.add(new JLabel("Member:"));
//        panel.add(memberSelectionCombo);
//        panel.add(new JLabel("Payment Date (YYYY-MM-DD):"));
//        panel.add(paymentDateField);
//        panel.add(new JLabel("Amount:"));
//        panel.add(amountField);
//        panel.add(new JLabel("Payment Method:"));
//        panel.add(methodCombo);
//        panel.add(new JLabel("Payment For:"));
//        panel.add(paymentForField);
//        
//        // Buttons
//        JButton saveButton = new JButton("Save");
//        saveButton.setBackground(WHITE);
//        saveButton.setForeground(BLACK);
//        saveButton.addActionListener(e -> {
//            try {
//                String selectedMember = (String) memberSelectionCombo.getSelectedItem();
//                String[] parts = selectedMember.split(" - ");
//                int memberId = Integer.parseInt(parts[0]);
//                
//                if (paymentId == null) {
//                    // Add new payment
//                    addPayment(
//                        memberId,
//                        paymentDateField.getText(),
//                        Double.parseDouble(amountField.getText()),
//                        (String) methodCombo.getSelectedItem(),
//                        paymentForField.getText()
//                    );
//                } else {
//                    // Update existing payment
//                    updatePayment(
//                        paymentId,
//                        memberId,
//                        paymentDateField.getText(),
//                        Double.parseDouble(amountField.getText()),
//                        (String) methodCombo.getSelectedItem(),
//                        paymentForField.getText()
//                    );
//                }
//                dialog.dispose();
//                loadPayments();
//            } catch (Exception ex) {
//                JOptionPane.showMessageDialog(dialog, "Error saving payment: " + ex.getMessage(), 
//                    "Error", JOptionPane.ERROR_MESSAGE);
//            }
//        });
//        
//        JButton cancelButton = new JButton("Cancel");
//        cancelButton.setBackground(WHITE);
//        cancelButton.setForeground(BLACK);
//        cancelButton.addActionListener(e -> dialog.dispose());
//        
//        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 10));
//        buttonPanel.add(saveButton);
//        buttonPanel.add(cancelButton);
//        
//        dialog.add(panel, BorderLayout.CENTER);
//        dialog.add(buttonPanel, BorderLayout.SOUTH);
//        dialog.setVisible(true);
//    }
    //YP98HROERJH0ER8SAJHP0AJR5AE5908JHO0SAEWH;JSAHR5P09
    
    private void showAddEditPaymentDialog(Integer paymentId) {
    JDialog dialog = new JDialog();
    dialog.setTitle(paymentId == null ? "Add New Payment" : "Edit Payment");
    dialog.setSize(500, 400);
    dialog.setLocationRelativeTo(this);
    dialog.setModal(true);
    
    JPanel panel = new JPanel(new GridLayout(0, 2, 10, 10));
    panel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
    
    // Form fields
    JComboBox<String> memberSelectionCombo = new JComboBox<>();
    JTextField paymentDateField = new JTextField();
    JComboBox<String> membershipTypeCombo = new JComboBox<>(new String[]{"VIP (₱1500)", "Premium (₱1000)", "Basic (₱500)", "Custom Amount"});
    JTextField amountField = new JTextField();
    JComboBox<String> methodCombo = new JComboBox<>(new String[]{"Cash", "Credit Card", "Debit Card", "Bank Transfer"});
    
    // Add listener for membership type selection
    membershipTypeCombo.addActionListener(e -> {
        String selected = (String) membershipTypeCombo.getSelectedItem();
        if (selected.contains("VIP")) {
            amountField.setText("1500");
            amountField.setEditable(false);
        } else if (selected.contains("Premium")) {
            amountField.setText("1000");
            amountField.setEditable(false);
        } else if (selected.contains("Basic")) {
            amountField.setText("500");
            amountField.setEditable(false);
        } else {
            amountField.setText("");
            amountField.setEditable(true);
        }
    });
    
    // Load members to combo
    try (Connection conn = DBConnection.getConnection()) {
        String query = "SELECT member_id, first_name, last_name FROM members ORDER BY last_name, first_name";
        PreparedStatement stmt = conn.prepareStatement(query);
        ResultSet rs = stmt.executeQuery();
        
        while (rs.next()) {
            memberSelectionCombo.addItem(rs.getInt("member_id") + " - " + 
                                       rs.getString("first_name") + " " + rs.getString("last_name"));
        }
    } catch (SQLException e) {
        JOptionPane.showMessageDialog(this, "Error loading members: " + e.getMessage(), 
            "Error", JOptionPane.ERROR_MESSAGE);
    }
    
    // If editing, load existing data
    if (paymentId != null) {
        try (Connection conn = DBConnection.getConnection()) {
            String query = "SELECT p.*, m.first_name, m.last_name FROM payments p " +
                          "LEFT JOIN members m ON p.member_id = m.member_id " +
                          "WHERE p.payment_id = ?";
            PreparedStatement stmt = conn.prepareStatement(query);
            stmt.setInt(1, paymentId);
            ResultSet rs = stmt.executeQuery();
            
            if (rs.next()) {
                // Set member in combo
                String memberText = rs.getInt("member_id") + " - " + 
                                  rs.getString("first_name") + " " + rs.getString("last_name");
                memberSelectionCombo.setSelectedItem(memberText);
                
                paymentDateField.setText(rs.getDate("payment_date").toString());
                amountField.setText(String.valueOf(rs.getDouble("amount")));
                methodCombo.setSelectedItem(rs.getString("payment_method"));
                
                // Set membership type based on amount
                double amount = rs.getDouble("amount");
                if (amount == 1500) {
                    membershipTypeCombo.setSelectedItem("VIP (₱1500)");
                } else if (amount == 1000) {
                    membershipTypeCombo.setSelectedItem("Premium (₱1000)");
                } else if (amount == 500) {
                    membershipTypeCombo.setSelectedItem("Basic (₱500)");
                } else {
                    membershipTypeCombo.setSelectedItem("Custom Amount");
                    amountField.setEditable(true);
                }
            }
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, "Error loading payment data: " + e.getMessage(), 
                "Error", JOptionPane.ERROR_MESSAGE);
        }
    }
    
    // Add fields to panel
    panel.add(new JLabel("Member:"));
    panel.add(memberSelectionCombo);
    panel.add(new JLabel("Payment Date (YYYY-MM-DD):"));
    panel.add(paymentDateField);
    panel.add(new JLabel("Membership Type:"));
    panel.add(membershipTypeCombo);
    panel.add(new JLabel("Amount:"));
    panel.add(amountField);
    panel.add(new JLabel("Payment Method:"));
    panel.add(methodCombo);
    
    // Buttons
    JButton saveButton = new JButton("Save");
    saveButton.setBackground(WHITE);
    saveButton.setForeground(BLACK);
    saveButton.addActionListener(e -> {
        try {
            String selectedMember = (String) memberSelectionCombo.getSelectedItem();
            String[] parts = selectedMember.split(" - ");
            int memberId = Integer.parseInt(parts[0]);
            
            String paymentFor = (String) membershipTypeCombo.getSelectedItem();
            if (paymentFor.equals("Custom Amount")) {
                paymentFor = "Custom Payment";
            } else {
                paymentFor = paymentFor.split(" ")[0]; // Get just the type (VIP/Premium/Basic)
            }
            
            if (paymentId == null) {
                // Add new payment
                addPayment(
                    memberId,
                    paymentDateField.getText(),
                    Double.parseDouble(amountField.getText()),
                    (String) methodCombo.getSelectedItem(),
                    paymentFor
                );
            } else {
                // Update existing payment
                updatePayment(
                    paymentId,
                    memberId,
                    paymentDateField.getText(),
                    Double.parseDouble(amountField.getText()),
                    (String) methodCombo.getSelectedItem(),
                    paymentFor
                );
            }
            dialog.dispose();
            loadPayments();
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(dialog, "Error saving payment: " + ex.getMessage(), 
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
    
    private void addPayment(int memberId, String paymentDate, double amount, 
                          String paymentMethod, String paymentFor) throws SQLException {
        String query = "INSERT INTO payments (member_id, payment_date, amount, payment_method, payment_for) " +
                      "VALUES (?, ?, ?, ?, ?)";
        
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {
            
            stmt.setInt(1, memberId);
            stmt.setDate(2, Date.valueOf(paymentDate));
            stmt.setDouble(3, amount);
            stmt.setString(4, paymentMethod);
            stmt.setString(5, paymentFor);
            
            stmt.executeUpdate();
            JOptionPane.showMessageDialog(this, "Payment added successfully", 
                "Success", JOptionPane.INFORMATION_MESSAGE);
        }
    }
    
    private void updatePayment(int paymentId, int memberId, String paymentDate, double amount, 
                             String paymentMethod, String paymentFor) throws SQLException {
        String query = "UPDATE payments SET member_id = ?, payment_date = ?, amount = ?, " +
                      "payment_method = ?, payment_for = ? WHERE payment_id = ?";
        
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {
            
            stmt.setInt(1, memberId);
            stmt.setDate(2, Date.valueOf(paymentDate));
            stmt.setDouble(3, amount);
            stmt.setString(4, paymentMethod);
            stmt.setString(5, paymentFor);
            stmt.setInt(6, paymentId);
            
            int rowsAffected = stmt.executeUpdate();
            if (rowsAffected > 0) {
                JOptionPane.showMessageDialog(this, "Payment updated successfully", 
                    "Success", JOptionPane.INFORMATION_MESSAGE);
            }
        }
    }
    
    private void deletePayment(int paymentId) {
        int confirm = JOptionPane.showConfirmDialog(this, 
            "Are you sure you want to delete this payment?", 
            "Confirm Deletion", JOptionPane.YES_NO_OPTION);
        
        if (confirm == JOptionPane.YES_OPTION) {
            try (Connection conn = DBConnection.getConnection()) {
                String query = "DELETE FROM payments WHERE payment_id = ?";
                PreparedStatement stmt = conn.prepareStatement(query);
                stmt.setInt(1, paymentId);
                
                int rowsAffected = stmt.executeUpdate();
                if (rowsAffected > 0) {
                    JOptionPane.showMessageDialog(this, "Payment deleted successfully", 
                        "Success", JOptionPane.INFORMATION_MESSAGE);
                    loadPayments();
                }
            } catch (SQLException e) {
                JOptionPane.showMessageDialog(this, "Error deleting payment: " + e.getMessage(), 
                    "Error", JOptionPane.ERROR_MESSAGE);
            }
        }
    }
    
    private void showPaymentStats() {
        try (Connection conn = DBConnection.getConnection()) {
            // Get total payments
            String totalQuery = "SELECT SUM(amount) FROM payments";
            PreparedStatement totalStmt = conn.prepareStatement(totalQuery);
            ResultSet totalRs = totalStmt.executeQuery();
            totalRs.next();
            double totalPayments = totalRs.getDouble(1);
            
            // Get average payment
            String avgQuery = "SELECT AVG(amount) FROM payments";
            PreparedStatement avgStmt = conn.prepareStatement(avgQuery);
            ResultSet avgRs = avgStmt.executeQuery();
            avgRs.next();
            double avgPayment = avgRs.getDouble(1);
            
            // Get max payment
            String maxQuery = "SELECT MAX(amount) FROM payments";
            PreparedStatement maxStmt = conn.prepareStatement(maxQuery);
            ResultSet maxRs = maxStmt.executeQuery();
            maxRs.next();
            double maxPayment = maxRs.getDouble(1);
            
            // Get min payment
            String minQuery = "SELECT MIN(amount) FROM payments";
            PreparedStatement minStmt = conn.prepareStatement(minQuery);
            ResultSet minRs = minStmt.executeQuery();
            minRs.next();
            double minPayment = minRs.getDouble(1);
            
            // Get payment by method
            String methodQuery = "SELECT payment_method, SUM(amount) as total FROM payments GROUP BY payment_method";
            PreparedStatement methodStmt = conn.prepareStatement(methodQuery);
            ResultSet methodRs = methodStmt.executeQuery();
            
            StringBuilder methodStats = new StringBuilder();
            while (methodRs.next()) {
                methodStats.append(methodRs.getString("payment_method"))
                          .append(": $")
                          .append(String.format("%.2f", methodRs.getDouble("total")))
                          .append("\n");
            }
            
            // Create and show dialog
            JDialog statsDialog = new JDialog();
            statsDialog.setTitle("Payment Statistics");
            statsDialog.setSize(400, 600);
            statsDialog.setLocationRelativeTo(this);
            
            JPanel statsPanel = new JPanel(new GridLayout(0, 1, 10, 10));
            statsPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
            
            statsPanel.add(createStatLabel("Total Payments: $" + String.format("%.2f", totalPayments), MAROON));
            statsPanel.add(createStatLabel("Average Payment: $" + String.format("%.2f", avgPayment), GREEN));
            statsPanel.add(createStatLabel("Maximum Payment: $" + String.format("%.2f", maxPayment), BLACK));
            statsPanel.add(createStatLabel("Minimum Payment: $" + String.format("%.2f", minPayment), BLACK));
            
            JTextArea methodArea = new JTextArea(methodStats.toString());
            methodArea.setEditable(false);
            methodArea.setBackground(WHITE);
            statsPanel.add(new JLabel("Payments by Method:"));
            statsPanel.add(methodArea);
            
            statsDialog.add(statsPanel);
            statsDialog.setVisible(true);
            
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, "Error retrieving payment statistics: " + e.getMessage(), 
                "Error", JOptionPane.ERROR_MESSAGE);
        }
    }
    
    private JLabel createStatLabel(String text, Color color) {
        JLabel label = new JLabel(text);
        label.setFont(new Font("Arial", Font.BOLD, 14));
        label.setForeground(color);
        return label;
    }
    
  private void searchPaymentById(int paymentId) {
    tableModel.setRowCount(0); // Clear existing data
    
    try (Connection conn = DBConnection.getConnection()) {
        String query = "SELECT p.*, m.first_name, m.last_name FROM payments p " +
                     "LEFT JOIN members m ON p.member_id = m.member_id " +
                     "WHERE p.payment_id = ? " +
                     "ORDER BY p.payment_date DESC";
        
        PreparedStatement stmt = conn.prepareStatement(query);
        stmt.setInt(1, paymentId);
        
        ResultSet rs = stmt.executeQuery();
        
        if (rs.next()) {
            Vector<Object> row = new Vector<>();
            row.add(rs.getInt("payment_id"));
            row.add(rs.getString("first_name") + " " + rs.getString("last_name"));
            row.add(rs.getDate("payment_date"));
            row.add(rs.getDouble("amount"));
            row.add(rs.getString("payment_method"));
            row.add(rs.getString("payment_for"));
            tableModel.addRow(row);
        } else {
            JOptionPane.showMessageDialog(this, "No payment found with ID: " + paymentId, 
                "Not Found", JOptionPane.INFORMATION_MESSAGE);
            loadPayments(); // Show all if not found
        }
    } catch (SQLException e) {
        JOptionPane.showMessageDialog(this, "Error searching payment: " + e.getMessage(), 
            "Error", JOptionPane.ERROR_MESSAGE);
    }
}
    
}

