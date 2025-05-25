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
import trainerAssignment.AssignTrainer;


public class MembersPanel extends JPanel {
    private GymManagementSystem parent;
    private Color MAROON;
    private Color WHITE;
    private Color BLACK;
    private Color RED;
    private Color GREEN;
    
    private JTable membersTable;
    private DefaultTableModel tableModel;
    
    public MembersPanel(GymManagementSystem parent) {
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
        
        JLabel titleLabel = new JLabel("Members Management", SwingConstants.CENTER);
        titleLabel.setFont(new Font("Arial", Font.BOLD, 24));
        titleLabel.setForeground(WHITE);
        headerPanel.add(titleLabel, BorderLayout.CENTER);
        
        // Back button
        JButton backButton = new JButton("Back to Dashboard");
        backButton.setBackground(BLACK);
        backButton.setForeground(RED);
        backButton.addActionListener(e -> parent.showDashboard());
        headerPanel.add(backButton, BorderLayout.WEST);
        
        add(headerPanel, BorderLayout.NORTH);
        
        // SEARCH PANEL


headerPanel.setPreferredSize(new Dimension(0, 100)); 

// Himoon ang search panel
JPanel searchPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 5));
searchPanel.setBackground(MAROON);
searchPanel.setBorder(BorderFactory.createEmptyBorder(0, 0, 10, 0));

// Search by ID nga bahin
JTextField searchField = new JTextField(10);
JButton searchButton = createButton("Search by ID", WHITE);
searchButton.addActionListener(e -> {
    try {
        int id = Integer.parseInt(searchField.getText());
        searchMemberById(id);
    } catch (NumberFormatException ex) {
       JOptionPane.showMessageDialog(this, "Please enter a valid Member ID", 
    "Error", JOptionPane.ERROR_MESSAGE);
    }
});

// Filter by status nga bahin
JComboBox<String> filterCombo = new JComboBox<>(new String[]{"All Members", "Active", "Inactive", "Suspended"});
filterCombo.addActionListener(e -> {
    String selected = (String)filterCombo.getSelectedItem();
    if (selected.equals("Tanang Members")) {
        loadMembers();
    } else {
        filterMembersByStatus(selected);
    }
});
//
// Idugang sa search panel
searchPanel.add(new JLabel("Search:") );
searchPanel.add(searchField);
searchPanel.add(searchButton);
searchPanel.add(new JLabel("Filter:"));
searchPanel.add(filterCombo);


JLabel searchLabel = new JLabel("Search:");
searchLabel.setForeground(Color.WHITE);  // Set text color to white
JLabel filterLabel = new JLabel("Filter:");
filterLabel.setForeground(Color.WHITE);  // Set text color to white

// Idugang ang search panel sa header
headerPanel.add(searchPanel, BorderLayout.SOUTH);
// ============ TAPOS NA ANG BAG-ONG PANEL ============
        // Content panel
        JPanel contentPanel = new JPanel(new BorderLayout());
        contentPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        contentPanel.setBackground(MAROON);
        
        // Table setup
       String[] columnNames = {"Member_id", "First Name", "Last Name", "Gender", "Birth Date", "Phone", "Assigned Trainer", "Status"};
tableModel = new DefaultTableModel(columnNames, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        
        membersTable = new JTable(tableModel);
        membersTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        JScrollPane scrollPane = new JScrollPane(membersTable);
        
        // Button panel
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 10));
        buttonPanel.setBackground(BLACK);
        
        JButton addButton = createButton("Add Member", WHITE);
        addButton.setForeground(BLACK);
        
        JButton editButton = createButton("Edit Member", WHITE);
         editButton.setForeground(BLACK);
         
        JButton deleteButton = createButton("Delete Member", WHITE);
         deleteButton.setForeground(BLACK);
         
        JButton refreshButton = createButton("Refresh", WHITE);
         refreshButton.setForeground(BLACK);
         
        addButton.addActionListener(e -> showAddEditMemberDialog(null));
        editButton.addActionListener(e -> {
            int selectedRow = membersTable.getSelectedRow();
            if (selectedRow >= 0) {
                int memberId = (int) tableModel.getValueAt(selectedRow, 0);
                showAddEditMemberDialog(memberId);
            } else {
                JOptionPane.showMessageDialog(this, "Please select a member to edit", 
                    "No Selection", JOptionPane.WARNING_MESSAGE);
            }
        });
        
        deleteButton.addActionListener(e -> {
            int selectedRow = membersTable.getSelectedRow();
            if (selectedRow >= 0) {
                int memberId = (int) tableModel.getValueAt(selectedRow, 0);
                deleteMember(memberId);
            } else {
                JOptionPane.showMessageDialog(this, "Please select a member to delete", 
                    "No Selection", JOptionPane.WARNING_MESSAGE);
            }
            
        });
        
// mao ni gikan sa assignTrainer naclass
             JButton assignTrainerButton = createButton("Assign Trainer", WHITE);
            assignTrainerButton.setForeground(BLACK);
          assignTrainerButton.addActionListener(e -> {  
           AssignTrainer assignFrame = new AssignTrainer();
           assignFrame.setVisible(true);
});
buttonPanel.add(assignTrainerButton);

        refreshButton.addActionListener(e -> loadMembers());
        
        buttonPanel.add(addButton);
        buttonPanel.add(editButton);
        buttonPanel.add(deleteButton);
        buttonPanel.add(refreshButton);
        
        contentPanel.add(scrollPane, BorderLayout.CENTER);
        contentPanel.add(buttonPanel, BorderLayout.SOUTH);
        
        add(contentPanel, BorderLayout.CENTER);
        
        // Load members data
        loadMembers();
    }
    
    private JButton createButton(String text, Color bgColor) {
        JButton button = new JButton(text);
        button.setBackground(bgColor);
        button.setForeground(BLACK);
        button.setFocusPainted(false);
        return button;
    }
    
   private void loadMembers() {
    tableModel.setRowCount(0);
    
    try (Connection conn = DBConnection.getConnection()) {
        String query = "SELECT m.*, t.first_name as t_first, t.last_name as t_last " +
                      "FROM members m LEFT JOIN trainers t ON m.trainer_id = t.trainer_id";
        PreparedStatement stmt = conn.prepareStatement(query);
        ResultSet rs = stmt.executeQuery();
        
        while (rs.next()) {
            Vector<Object> row = new Vector<>();
            row.add(rs.getInt("member_id"));
            row.add(rs.getString("first_name"));
            row.add(rs.getString("last_name"));
            row.add(rs.getString("gender"));
            row.add(rs.getDate("birth_date"));
            row.add(rs.getString("phone"));
            
            // Get trainer name (or "None" if not assigned)
            String trainerName = "None";
            if (rs.getString("t_first") != null) {
                trainerName = rs.getString("t_first") + " " + rs.getString("t_last");
            }
            row.add(trainerName);
            
            row.add(rs.getString("status"));
            tableModel.addRow(row);
        }
    } catch (SQLException e) {
        JOptionPane.showMessageDialog(this, "Error loading members: " + e.getMessage(), 
            "Error", JOptionPane.ERROR_MESSAGE);
        // Current code (around line 167):


    }
    
}
    
    
    private void showAddEditMemberDialog(Integer memberId) {
        JDialog dialog = new JDialog();
        dialog.setTitle(memberId == null ? "Add New Member" : "Edit Member");
        dialog.setSize(500, 500);
        dialog.setLocationRelativeTo(this);
        dialog.setModal(true);
        
        
        JPanel panel = new JPanel(new GridLayout(0, 2, 10, 10));
        panel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        
        // Form fields
        JTextField firstNameField = new JTextField();
        JTextField lastNameField = new JTextField();
        JComboBox<String> genderCombo = new JComboBox<>(new String[]{"Male", "Female", "Other"});
        JTextField birthDateField = new JTextField();
        JTextField phoneField = new JTextField();
        JTextField emailField = new JTextField();
        JTextField addressField = new JTextField();
        JTextField joinDateField = new JTextField();
        JComboBox<String> membershipCombo = new JComboBox<>(new String[]{"Basic", "Premium", "VIP"});
        JComboBox<String> statusCombo = new JComboBox<>(new String[]{"Active", "Inactive", "Suspended"});
      
      
        // If editing, load existing data
        if (memberId != null) {
            try (Connection conn = DBConnection.getConnection()) {
                String query = "SELECT * FROM members WHERE member_id = ?";
                PreparedStatement stmt = conn.prepareStatement(query);
                stmt.setInt(1, memberId);
                ResultSet rs = stmt.executeQuery();
                
                if (rs.next()) {
                    firstNameField.setText(rs.getString("first_name"));
                    lastNameField.setText(rs.getString("last_name"));
                    genderCombo.setSelectedItem(rs.getString("gender"));
                    birthDateField.setText(rs.getDate("birth_date").toString());
                    phoneField.setText(rs.getString("phone"));
                    emailField.setText(rs.getString("email"));
                    addressField.setText(rs.getString("address"));
                    joinDateField.setText(rs.getDate("join_date").toString());
                    membershipCombo.setSelectedItem(rs.getString("membership_type"));
                    statusCombo.setSelectedItem(rs.getString("status"));
                }
            } catch (SQLException e) {
                JOptionPane.showMessageDialog(this, "Error loading member data: " + e.getMessage(), 
                    "Error", JOptionPane.ERROR_MESSAGE);
            }
        }
        
        // Add fields to panel
        panel.add(new JLabel("First Name:"));
        panel.add(firstNameField);
        panel.add(new JLabel("Last Name:"));
        panel.add(lastNameField);
        panel.add(new JLabel("Gender:"));
        panel.add(genderCombo);
        panel.add(new JLabel("Birth Date (YYYY-MM-DD):"));
        panel.add(birthDateField);
        panel.add(new JLabel("Phone:"));
        panel.add(phoneField);
        panel.add(new JLabel("Email:"));
        panel.add(emailField);
        panel.add(new JLabel("Address:"));
        panel.add(addressField);
        panel.add(new JLabel("Join Date (YYYY-MM-DD):"));
        panel.add(joinDateField);
        panel.add(new JLabel("Membership Type:"));
        panel.add(membershipCombo);
        panel.add(new JLabel("Status:"));
        panel.add(statusCombo);
        
        // Buttons
        JButton saveButton = new JButton("Save");
        saveButton.setBackground(WHITE);
        saveButton.setForeground(BLACK);
        saveButton.addActionListener(e -> {
            try {
                if (memberId == null) {
                    // Add new member
                    addMember(
                        firstNameField.getText(),
                        lastNameField.getText(),
                        (String) genderCombo.getSelectedItem(),
                        birthDateField.getText(),
                        phoneField.getText(),
                        emailField.getText(),
                        addressField.getText(),
                        joinDateField.getText(),
                        (String) membershipCombo.getSelectedItem(),
                        (String) statusCombo.getSelectedItem()
                    );
                } else {
                    // Update existing member
                    updateMember(
                        memberId,
                        firstNameField.getText(),
                        lastNameField.getText(),
                        (String) genderCombo.getSelectedItem(),
                        birthDateField.getText(),
                        phoneField.getText(),
                        emailField.getText(),
                        addressField.getText(),
                        joinDateField.getText(),
                        (String) membershipCombo.getSelectedItem(),
                        (String) statusCombo.getSelectedItem()
                    );
                }
                dialog.dispose();
                loadMembers();
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(dialog, "Error saving member: " + ex.getMessage(), 
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

    private void addMember(String firstName, String lastName, String gender, String birthDate, 
                          String phone, String email, String address, String joinDate, 
                          String membershipType, String status) throws SQLException {
        String query = "INSERT INTO members (first_name, last_name, gender, birth_date, phone, " +
                      "email, address, join_date, membership_type, status) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";
        
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {
            
            stmt.setString(1, firstName);
            stmt.setString(2, lastName);
            stmt.setString(3, gender);
            stmt.setDate(4, Date.valueOf(birthDate));
            stmt.setString(5, phone);
            stmt.setString(6, email);
            stmt.setString(7, address);
            stmt.setDate(8, Date.valueOf(joinDate));
            stmt.setString(9, membershipType);
            stmt.setString(10, status);
            
            stmt.executeUpdate();
            JOptionPane.showMessageDialog(this, "Member added successfully", 
                "Success", JOptionPane.INFORMATION_MESSAGE);
        }
    }
    
   private void updateMember(int member_Id, String firstName, String lastName, String gender, 
                        String birthDate, String phone, String email, String address, 
                        String joinDate, String membershipType, String status) throws SQLException {
    String query = "UPDATE members SET first_name = ?, last_name = ?, gender = ?, " +
                  "birth_date = ?, phone = ?, email = ?, address = ?, join_date = ?, " +
                  "membership_type = ?, status = ? WHERE member_id = ?";  // Removed extra comma

    try (Connection conn = DBConnection.getConnection();
         PreparedStatement stmt = conn.prepareStatement(query)) {
        
        stmt.setString(1, firstName);
        stmt.setString(2, lastName);
        stmt.setString(3, gender);
        stmt.setDate(4, Date.valueOf(birthDate));
        stmt.setString(5, phone);
        stmt.setString(6, email);
        stmt.setString(7, address);
        stmt.setDate(8, Date.valueOf(joinDate));
        stmt.setString(9, membershipType);
        stmt.setString(10, status);
        stmt.setInt(11, member_Id);  // Added the missing parameter for member_id
        
        int rowsAffected = stmt.executeUpdate();
        if (rowsAffected > 0) {
            JOptionPane.showMessageDialog(this, "Member updated successfully", 
                "Success", JOptionPane.INFORMATION_MESSAGE);
        }
    }
}

    
    private void deleteMember(int memberId) {
        int confirm = JOptionPane.showConfirmDialog(this, 
            "Are you sure you want to delete this member?", 
            "Confirm Deletion", JOptionPane.YES_NO_OPTION);
        
        if (confirm == JOptionPane.YES_OPTION) {
            try (Connection conn = DBConnection.getConnection()) {
                String query = "DELETE FROM members WHERE member_id = ?";
                PreparedStatement stmt = conn.prepareStatement(query);
                stmt.setInt(1, memberId);
                
                int rowsAffected = stmt.executeUpdate();
                if (rowsAffected > 0) {
                    JOptionPane.showMessageDialog(this, "Member deleted successfully", 
                        "Success", JOptionPane.INFORMATION_MESSAGE);
                    loadMembers();
                }
            } catch (SQLException e) {
                JOptionPane.showMessageDialog(this, "Error deleting member: " + e.getMessage(), 
                    "Error", JOptionPane.ERROR_MESSAGE);
            }
        }
    }
    
    // ============ NEW METHODS ADDED ============
private void searchMemberById(int memberId) {
    tableModel.setRowCount(0); // Clear table
    
    try (Connection conn = DBConnection.getConnection()) {
        String query = "SELECT m.*, t.first_name as t_first, t.last_name as t_last " +
                     "FROM members m LEFT JOIN trainers t ON m.trainer_id = t.trainer_id " +
                     "WHERE m.member_id = ?";
        PreparedStatement stmt = conn.prepareStatement(query);
        stmt.setInt(1, memberId);
        ResultSet rs = stmt.executeQuery();
        
        if (rs.next()) {
            Vector<Object> row = new Vector<>();
            row.add(rs.getInt("member_id"));
            row.add(rs.getString("first_name"));
            row.add(rs.getString("last_name"));
            row.add(rs.getString("gender"));
            row.add(rs.getDate("birth_date"));
            row.add(rs.getString("phone"));
            
            String trainerName = "None";
            if (rs.getString("t_first") != null) {
                trainerName = rs.getString("t_first") + " " + rs.getString("t_last");
            }
            row.add(trainerName);
            row.add(rs.getString("status"));
            tableModel.addRow(row);
        } else {
            JOptionPane.showMessageDialog(this, "No member found with ID: " + memberId, 
                "Not Found", JOptionPane.INFORMATION_MESSAGE);
            loadMembers(); // Show all if not found
        }
    } catch (SQLException e) {
        JOptionPane.showMessageDialog(this, "Error searching member: " + e.getMessage(), 
            "Error", JOptionPane.ERROR_MESSAGE);
    }
}

private void filterMembersByStatus(String status) {
    tableModel.setRowCount(0); // Clear table
    
    try (Connection conn = DBConnection.getConnection()) {
        String query = "SELECT m.*, t.first_name as t_first, t.last_name as t_last " +
                     "FROM members m LEFT JOIN trainers t ON m.trainer_id = t.trainer_id " +
                     "WHERE m.status = ?";
        PreparedStatement stmt = conn.prepareStatement(query);
        stmt.setString(1, status);
        ResultSet rs = stmt.executeQuery();
        
        while (rs.next()) {
            Vector<Object> row = new Vector<>();
            row.add(rs.getInt("member_id"));
            row.add(rs.getString("first_name"));
            row.add(rs.getString("last_name"));
            row.add(rs.getString("gender"));
            row.add(rs.getDate("birth_date"));
            row.add(rs.getString("phone"));
            
            String trainerName = "None";
            if (rs.getString("t_first") != null) {
                trainerName = rs.getString("t_first") + " " + rs.getString("t_last");
            }
            row.add(trainerName);
            row.add(rs.getString("status"));
            tableModel.addRow(row);
        }
    } catch (SQLException e) {
        JOptionPane.showMessageDialog(this, "Error filtering members: " + e.getMessage(), 
            "Error", JOptionPane.ERROR_MESSAGE);
    }
}
// ============ END OF NEW METHODS ============
}