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
import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.Date;

public class OrderReportExample {
    private JFrame mainFrame;
    private JLabel headerLabel;
    private JPanel controlPanel;
    private JTextField searchField;
    private JTable table;
    private DefaultTableModel tableModel;
    private double totalAmount = 0.0;
    private JTextField totalAmountTextField;

    public OrderReportExample() {
        prepareGUI();
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            try {
                OrderReportExample orderReport = new OrderReportExample();
                orderReport.showOrderReport();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        });
    }

    private void prepareGUI() {
        mainFrame = new JFrame("Order Report");
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
        controlPanel.setBackground(new Color(0, 153, 255));

        JButton searchButton = new JButton("Search");
        searchField = new JTextField(20);
        JPanel searchPanel = new JPanel();
        JLabel enterDateLabel = new JLabel("Enter Date: ");
        searchPanel.add(enterDateLabel);
        searchPanel.add(searchField);
        searchPanel.add(searchButton);
        controlPanel.add(searchPanel, BorderLayout.NORTH);

        JButton refreshButton = new JButton("Refresh");
        refreshButton.setBackground(new Color(0, 51, 102)); // Set background color
        refreshButton.setForeground(Color.white); // Set text color
        refreshButton.setPreferredSize(new Dimension(500, 30)); // Set preferred size
        refreshButton.setFont(new Font(null, Font.BOLD, 18)); // Set font size

        // Create "Total Amount Earned" label and text field
        JLabel totalAmountLabel = new JLabel("    Final Total Amount Earned :    ");
        totalAmountLabel.setFont(new Font(null, Font.BOLD, 20)); // Set label font size
        totalAmountLabel.setForeground(Color.WHITE);
        totalAmountTextField = new JTextField(10);
        totalAmountTextField.setEditable(false); // Make it non-editable
        totalAmountTextField.setFont(new Font(null, Font.BOLD, 20)); // Set text field font size
        totalAmountTextField.setHorizontalAlignment(JTextField.CENTER);

        // Create a panel to hold the label and text field
        JPanel totalAmountPanel = new JPanel();
        totalAmountPanel.setLayout(new FlowLayout(FlowLayout.RIGHT)); // Right-align
        totalAmountPanel.setBackground(new Color(0, 51, 102));
        totalAmountPanel.setForeground(Color.WHITE);
        totalAmountPanel.add(totalAmountLabel);
        totalAmountPanel.add(totalAmountTextField);

        // Create a panel to hold the "Refresh" button
        JPanel refreshPanel = new JPanel();
        refreshPanel.setLayout(new FlowLayout(FlowLayout.LEFT)); // Left-align
        refreshPanel.add(refreshButton);

        // Create a panel to hold both the "Total Amount Earned" and "Refresh" components
        JPanel bottomPanel = new JPanel(new BorderLayout());
        bottomPanel.add(totalAmountPanel, BorderLayout.EAST);
        bottomPanel.add(refreshPanel, BorderLayout.WEST);
        // Set the background color for the entire bottomPanel
        bottomPanel.setBackground(Color.white);

        // Add the bottom panel to the control panel
        controlPanel.add(bottomPanel, BorderLayout.SOUTH);

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

        // Create a new JTable instance
        table = new JTable();

        // Add the table to the scroll pane and add it to the control panel
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
        OrderReportExample.MenuListener menuListener = new OrderReportExample.MenuListener();

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
                String sql = "SELECT * FROM OrderReport WHERE order_date LIKE ? ORDER BY order_date, report_id";
                PreparedStatement pst = connection.prepareStatement(sql);
                pst.setString(1, searchTerm + "%"); // Use searchTerm + "%" to match dates starting with the search term
                ResultSet rs = pst.executeQuery();

                tableModel.setRowCount(0);

                // Recalculate the total amount for the search results
                totalAmount = 0.0;

                while (rs.next()) {
                    Object[] row = {
                            rs.getTimestamp("order_date"),
                            rs.getInt("report_id"),
                            rs.getDouble("total_amount")
                    };
                    tableModel.addRow(row);

                    // Update the total amount
                    totalAmount += rs.getDouble("total_amount");
                }

                connection.close();

                // Update the totalAmountTextField with the calculated total amount
                totalAmountTextField.setText(String.valueOf(totalAmount));

            } catch (SQLException ex) {
                ex.printStackTrace();
                JOptionPane.showMessageDialog(null, "Error !");
            }
        }
    }

    private void refreshTable() {
        try {
            showOrderReport();
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

    private void showOrderReport() throws SQLException {
        headerLabel.setText("Order Report");
        String[] columnNames = {"Date", "Report ID", "Total Amount Earned"};
        Object[][] data = new Object[100][3];
        Connection connection = null;
        String url = "jdbc:mysql://localhost:3306/SagarDatabase";
        String username = "root";
        String password = "";
        try {
            Class.forName("com.mysql.jdbc.Driver");
            connection = DriverManager.getConnection(url, username, password);
            String sql = "SELECT * FROM OrderReport";
            PreparedStatement pst = connection.prepareStatement(sql);
            ResultSet rs = pst.executeQuery();
            int i = 0;

            // Initialize the total amount
            totalAmount = 0.0;

            while (rs.next()) {
                Timestamp timestamp = rs.getTimestamp("order_date");
                SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                String formattedDate = dateFormat.format(new Date(timestamp.getTime()));

                data[i][0] = formattedDate;
                data[i][1] = rs.getInt("report_id");
                double amountEarned = rs.getDouble("total_amount");
                data[i][2] = amountEarned;

                // Update the total amount
                totalAmount += amountEarned;
                i++;
            }

            // Update the totalAmountTextField with the calculated total amount
            totalAmountTextField.setText(String.valueOf(totalAmount));

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