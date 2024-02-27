import java.awt.*;
import java.awt.event.*;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import javax.swing.*;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;

public class StaffInfo {
    private JFrame mainFrame;
    private JLabel headerLabel;
    private JPanel controlPanel;
    private JTextField searchField;
    private JTable table;
    private DefaultTableModel tableModel;

    public StaffInfo() {
        prepareGUI();
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            try {
                StaffInfo staffInfo = new StaffInfo();
                staffInfo.showTableData();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        });
    }

    private void prepareGUI() {
        mainFrame = new JFrame("Staff Information");
        Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
        mainFrame.setSize(screenSize.width, screenSize.height);
        mainFrame.getContentPane().setBackground(new Color(0, 153, 255));
        mainFrame.setLayout(new BorderLayout());
        mainFrame.addWindowListener(new WindowAdapter() {
            public void windowClosing(WindowEvent windowEvent) {
                System.exit(0);
            }
        });

        headerLabel = new JLabel("Staff Information", JLabel.CENTER);
        headerLabel.setFont(new Font(null, Font.BOLD, 25));
        headerLabel.setForeground(Color.white);

        controlPanel = new JPanel();
        controlPanel.setPreferredSize(new Dimension(screenSize.width, screenSize.height));
        controlPanel.setLayout(new BorderLayout());
        controlPanel.setBackground(new Color(0, 51, 102));

        JButton searchButton = new JButton("Search");
        searchField = new JTextField(20);
        JPanel searchPanel = new JPanel();
        searchPanel.add(searchField);
        searchPanel.add(searchButton);
        controlPanel.add(searchPanel, BorderLayout.NORTH);

        JButton refreshButton = new JButton("Refresh");
        refreshButton.setBackground(new Color(0, 51, 102));
        refreshButton.setForeground(Color.white);
        controlPanel.add(refreshButton, BorderLayout.SOUTH);

        JButton insertButton = new JButton("Insert");
        insertButton.setBackground(new Color(0, 51, 102));
        insertButton.setForeground(Color.white);
        insertButton.addActionListener(this::insertStaff);

        JButton updateButton = new JButton("Update");
        updateButton.setBackground(new Color(0, 51, 102));
        updateButton.setForeground(Color.white);
        updateButton.addActionListener(this::updateStaff);

        JButton deleteButton = new JButton("Delete");
        deleteButton.setBackground(new Color(0, 51, 102));
        deleteButton.setForeground(Color.white);
        deleteButton.addActionListener(this::deleteStaff);

        JPanel buttonPanel = new JPanel();
        buttonPanel.setLayout(new GridLayout(3, 1, 5, 5));
        buttonPanel.add(insertButton);
        buttonPanel.add(updateButton);
        buttonPanel.add(deleteButton);
        controlPanel.add(buttonPanel, BorderLayout.EAST);

        searchButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                performSearch();
            }
        });

        refreshButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                refreshTable();
            }
        });

        mainFrame.add(headerLabel, BorderLayout.NORTH);
        mainFrame.add(controlPanel, BorderLayout.CENTER);

        table = new JTable();
        JScrollPane scrollPane = new JScrollPane(table);
        controlPanel.add(scrollPane, BorderLayout.CENTER);

        mainFrame.setVisible(true);
        mainFrame.setLocationRelativeTo(null);
        mainFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        JMenuBar menuBar = new JMenuBar();
        mainFrame.setJMenuBar(menuBar);

        JMenu aboutMenu = new JMenu("About");
        menuBar.add(aboutMenu);

        JMenu backMenu = new JMenu("Back");
        menuBar.add(backMenu);

        JMenu exitMenu = new JMenu("Exit");
        menuBar.add(exitMenu);

        // Create a menu listener
        StaffInfo.MenuListener menuListener = new StaffInfo.MenuListener();

        JMenuItem aboutMenuItem = new JMenuItem("About");
        aboutMenuItem.setActionCommand("About");
        aboutMenuItem.addActionListener(menuListener);
        aboutMenu.add(aboutMenuItem);

        JMenuItem backMenuItem = new JMenuItem("Back");
        backMenuItem.setActionCommand("Back");
        backMenuItem.addActionListener(menuListener);
        backMenu.add(backMenuItem);

        JMenuItem exitMenuItem = new JMenuItem("Exit");
        exitMenuItem.setActionCommand("Exit");
        exitMenuItem.addActionListener(menuListener);
        exitMenu.add(exitMenuItem);
    }

    // Create a separate class to handle menu events
    private class MenuListener implements ActionListener {
        public void actionPerformed(ActionEvent e) {
            String command = e.getActionCommand();
            switch (command) {
                case "About":
                    JOptionPane.showMessageDialog(mainFrame, "About Button clicked!");
                    break;
                case "Back":
                    mainFrame.dispose();
                    HomePage homePage = new HomePage();
                    break;
                case "Exit":
                    int option = JOptionPane.showConfirmDialog(mainFrame, "Are you sure you want to exit?",
                            "Exit Confirmation", JOptionPane.YES_NO_OPTION, JOptionPane.QUESTION_MESSAGE);
                    if (option == JOptionPane.YES_OPTION) {
                        System.exit(0);
                    }
                    break;
            }
        }
    }

    private void performSearch() {
        String searchTerm = searchField.getText().trim();
        if (!searchTerm.isEmpty()) {
            try {
                Connection connection = getConnection();
                String sql = "SELECT * FROM Staff WHERE s_username LIKE ?";
                PreparedStatement pst = connection.prepareStatement(sql);
                pst.setString(1, searchTerm + "%");
                ResultSet rs = pst.executeQuery();

                tableModel.setRowCount(0);

                while (rs.next()) {
                    Object[] row = {
                            rs.getInt("s_id"),
                            rs.getString("s_username"),
                            rs.getString("s_password"),
                            rs.getString("s_address"),
                            rs.getLong("s_contact"),
                            rs.getString("s_email")
                    };
                    tableModel.addRow(row);
                }

                connection.close();
            } catch (SQLException ex) {
                ex.printStackTrace();
                JOptionPane.showMessageDialog(null, "Error !");
            }
        }
    }

    private void insertStaff(ActionEvent e) {
        StaffDialog dialog = new StaffDialog(mainFrame, "Insert Staff");
        dialog.setVisible(true); // Display the dialog and wait for user input

        if (dialog.isConfirmed()) {
            int id = dialog.getID();
            String username = dialog.getUsername();
            String password = dialog.getPassword();
            String address = dialog.getAddress();
            long contact = dialog.getContact();
            String email = dialog.getEmail();

            try {
                Connection connection = getConnection();
                String sql = "INSERT INTO Staff (s_id, s_username, s_password, s_address, s_contact, s_email) VALUES (?, ?, ?, ?, ?, ?)";
                PreparedStatement pst = connection.prepareStatement(sql);
                pst.setInt(1, id);
                pst.setString(2, username);
                pst.setString(3, password);
                pst.setString(4, address);
                pst.setLong(5, contact);
                pst.setString(6, email);
                pst.executeUpdate();
                connection.close();
                refreshTable();
            } catch (SQLException ex) {
                ex.printStackTrace();
                JOptionPane.showMessageDialog(null, "Error inserting staff.");
            }
        }
    }

    private void updateStaff(ActionEvent e) {
        int selectedRow = table.getSelectedRow();
        if (selectedRow >= 0) {
            int id = (int) table.getValueAt(selectedRow, 0);
            StaffDialog dialog = new StaffDialog(mainFrame, "Update Staff"); // Pass the staff ID for updating
            dialog.setID(id); // Pre-fill ID field with the selected ID
            dialog.setVisible(true);

            if (dialog.isConfirmed()) {
                String username = dialog.getUsername();
                String password = dialog.getPassword();
                String address = dialog.getAddress();
                long contact = dialog.getContact();
                String email = dialog.getEmail();

                try {
                    Connection connection = getConnection();
                    String sql = "UPDATE Staff SET s_username = ?, s_password = ?, s_address = ?, s_contact = ?, s_email = ? WHERE s_id = ?";
                    PreparedStatement pst = connection.prepareStatement(sql);
                    pst.setString(1, username);
                    pst.setString(2, password);
                    pst.setString(3, address);
                    pst.setLong(4, contact);
                    pst.setString(5, email);
                    pst.setInt(6, id);
                    pst.executeUpdate();
                    connection.close();
                    refreshTable();
                } catch (SQLException ex) {
                    ex.printStackTrace();
                    JOptionPane.showMessageDialog(null, "Error updating staff.");
                }
            }
        }
    }


    private void deleteStaff(ActionEvent e) {
        int selectedRow = table.getSelectedRow();
        if (selectedRow >= 0) {
            int id = (int) table.getValueAt(selectedRow, 0);
            try {
                Connection connection = getConnection();
                String sql = "DELETE FROM Staff WHERE s_id = ?";
                PreparedStatement pst = connection.prepareStatement(sql);
                pst.setInt(1, id);
                pst.executeUpdate();
                connection.close();
                refreshTable();
            } catch (SQLException ex) {
                ex.printStackTrace();
                JOptionPane.showMessageDialog(null, "Error deleting staff.");
            }
        }
    }

    private void refreshTable() {
        try {
            showTableData();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private Connection getConnection() throws SQLException {
        String url = "jdbc:mysql://localhost:3306/SagarDatabase";
        String username = "root";
        String password = "";
        return DriverManager.getConnection(url, username, password);
    }

    private void showTableData() throws SQLException {
        headerLabel.setText("Staff Information");
        String[] columnNames = {"ID", "Username", "Password", "Address", "Contact", "Email"};
        Object[][] data = new Object[100][6];
        Connection connection = null;
        String url = "jdbc:mysql://localhost:3306/SagarDatabase";
        String username = "root";
        String password = "";
        try {
            Class.forName("com.mysql.jdbc.Driver");
            connection = DriverManager.getConnection(url, username, password);
            String sql = "SELECT * FROM Staff";
            PreparedStatement pst = connection.prepareStatement(sql);
            ResultSet rs = pst.executeQuery();
            int i = 0;
            while (rs.next()) {
                data[i][0] = rs.getInt("s_id");
                data[i][1] = rs.getString("s_username");
                data[i][2] = rs.getString("s_password");
                data[i][3] = rs.getString("s_address");
                data[i][4] = rs.getLong("s_contact");
                data[i][5] = rs.getString("s_email");
                i++;
            }
        } catch (Exception ex) {
            ex.printStackTrace();
            JOptionPane.showMessageDialog(null, "Error !");
        } finally {
            if (connection != null) {
                connection.close();
            }
        }

        tableModel = new DefaultTableModel(data, columnNames);
        table.setModel(tableModel);

        table.getTableHeader().setFont(new Font(null, Font.BOLD, 18));
        table.setFont(new Font(null, Font.PLAIN, 16));
        table.setRowHeight(30);
        DefaultTableCellRenderer centerRenderer = new DefaultTableCellRenderer();
        centerRenderer.setHorizontalAlignment(JLabel.CENTER);
        table.setDefaultRenderer(Object.class, centerRenderer);
    }
}