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

public class ItemModify {
    private JFrame mainFrame;
    private JLabel headerLabel;
    private JPanel controlPanel;
    private JTextField searchField;
    private JTable table;
    private DefaultTableModel tableModel;

    public ItemModify() {
        prepareGUI();
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            try {
                ItemModify swingControlDemo = new ItemModify();
                swingControlDemo.showButtonDemo();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        });
    }

    private void prepareGUI() {
        mainFrame = new JFrame("Showing all items");
        Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
        mainFrame.setSize(screenSize.width, screenSize.height);
        mainFrame.getContentPane().setBackground(new Color(0, 153, 255));
        mainFrame.setLayout(new BorderLayout());
        mainFrame.addWindowListener(new WindowAdapter() {
            public void windowClosing(WindowEvent windowEvent) {
                System.exit(0);
            }
        });

        headerLabel = new JLabel("", JLabel.CENTER);
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
        insertButton.addActionListener(this::insertItem);

        JButton updateButton = new JButton("Update");
        updateButton.setBackground(new Color(0, 51, 102));
        updateButton.setForeground(Color.white);
        updateButton.addActionListener(this::updateItem);

        JButton deleteButton = new JButton("Delete");
        deleteButton.setBackground(new Color(0, 51, 102));
        deleteButton.setForeground(Color.white);
        deleteButton.addActionListener(this::deleteItem);

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
        ItemModify.MenuListener menuListener = new ItemModify.MenuListener();

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
                String sql = "SELECT * FROM Food WHERE f_name LIKE ?";
                PreparedStatement pst = connection.prepareStatement(sql);
                pst.setString(1, searchTerm + "%");
                ResultSet rs = pst.executeQuery();

                tableModel.setRowCount(0);

                while (rs.next()) {
                    Object[] row = {
                            rs.getInt("f_id"),
                            rs.getString("f_name"),
                            rs.getDouble("f_prize")
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

    private void insertItem(ActionEvent e) {
        ItemDialog dialog = new ItemDialog(mainFrame, "Insert Item");
        dialog.setVisible(true); // Display the dialog and wait for user input

        if (dialog.isConfirmed()) {
            int id = dialog.getID();
            String name = dialog.getName();
            double price = dialog.getPrice();

            try {
                Connection connection = getConnection();
                String sql = "INSERT INTO Food (f_id, f_name, f_prize) VALUES (?, ?, ?)";
                PreparedStatement pst = connection.prepareStatement(sql);
                pst.setInt(1, id);
                pst.setString(2, name);
                pst.setDouble(3, price);
                pst.executeUpdate();
                connection.close();
                refreshTable();
            } catch (SQLException ex) {
                ex.printStackTrace();
                JOptionPane.showMessageDialog(null, "Error inserting item.");
            }
        }
    }

    private void updateItem(ActionEvent e) {
        int selectedRow = table.getSelectedRow();
        if (selectedRow >= 0) {
            int id = (int) table.getValueAt(selectedRow, 0);
            ItemDialog dialog = new ItemDialog(mainFrame, "Update Item");
            dialog.setID(id); // Pre-fill ID field with the selected ID
            dialog.setVisible(true);

            if (dialog.isConfirmed()) {
                String name = dialog.getName();
                double price = dialog.getPrice();

                try {
                    Connection connection = getConnection();
                    String sql = "UPDATE Food SET f_name = ?, f_prize = ? WHERE f_id = ?";
                    PreparedStatement pst = connection.prepareStatement(sql);
                    pst.setString(1, name);
                    pst.setDouble(2, price);
                    pst.setInt(3, id);
                    pst.executeUpdate();
                    connection.close();
                    refreshTable();
                } catch (SQLException ex) {
                    ex.printStackTrace();
                    JOptionPane.showMessageDialog(null, "Error updating item.");
                }
            }
        }
    }


    private void deleteItem(ActionEvent e) {
        int selectedRow = table.getSelectedRow();
        if (selectedRow >= 0) {
            int id = (int) table.getValueAt(selectedRow, 0);
            try {
                Connection connection = getConnection();
                String sql = "DELETE FROM Food WHERE f_id = ?";
                PreparedStatement pst = connection.prepareStatement(sql);
                pst.setInt(1, id);
                pst.executeUpdate();
                connection.close();
                refreshTable();
            } catch (SQLException ex) {
                ex.printStackTrace();
                JOptionPane.showMessageDialog(null, "Error deleting item.");
            }
        }
    }

    private void refreshTable() {
        try {
            showButtonDemo();
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

    private void showButtonDemo() throws SQLException {
        headerLabel.setText("Yummy Kitchen Hub");
        String[] columnNames = {"ID", "Food Name", "Price"};
        Object[][] data = new Object[100][3]; // Adjust the array size accordingly
        Connection connection = null;
        String url = "jdbc:mysql://localhost:3306/SagarDatabase";
        String username = "root";
        String password = "";
        try {
            Class.forName("com.mysql.jdbc.Driver");
            connection = DriverManager.getConnection(url, username, password);
            String sql = "SELECT * FROM Food";
            PreparedStatement pst = connection.prepareStatement(sql);
            ResultSet rs = pst.executeQuery();
            int i = 0;
            while (rs.next()) {
                data[i][0] = rs.getInt("f_id");
                data[i][1] = rs.getString("f_name");
                data[i][2] = rs.getDouble("f_prize");
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