import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Date; // Import Date class
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.awt.Font;
import java.sql.Timestamp;
import javax.swing.table.DefaultTableCellRenderer;

public class OrderReport extends JFrame {
    private JTable table;
    private JScrollPane scrollPane;
    private DefaultTableModel model;
    private JTextField searchField;
    private JButton searchButton;
    private JLabel enterDateLabel;
    private JFormattedTextField dateTextField;
    private SimpleDateFormat dateFormat; // Declare dateFormat as an instance variable

    public OrderReport() {
        setTitle("Order Report");
        Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
        setSize(screenSize.width, screenSize.height);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        // Create a JPanel for search components
        JPanel searchPanel = new JPanel();
        searchField = new JTextField(20);
        searchButton = new JButton("Search");
        enterDateLabel = new JLabel("Enter Date (yyyy-MM-dd):");
        dateTextField = new JFormattedTextField(createDateFormat());
        dateTextField.setColumns(10);

        JMenuBar menuBar = new JMenuBar();
        setJMenuBar(menuBar);

        JMenu aboutMenu = new JMenu("About");
        menuBar.add(aboutMenu);

        JMenu backMenu = new JMenu("Back");
        menuBar.add(backMenu);

        JMenu exitMenu = new JMenu("Exit");
        menuBar.add(exitMenu);

        // Create a menu listener
        OrderReport.MenuListener menuListener = new OrderReport.MenuListener();

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

        // Add an ActionListener to the search button
        searchButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                try {
                    // Parse the entered date
                    Date searchDate = new Date(dateFormat.parse(dateTextField.getText()).getTime());
                    performSearch(searchDate);
                } catch (ParseException ex) {
                    JOptionPane.showMessageDialog(null, "Invalid date format. Please use yyyy-MM-dd.");
                }
            }
        });

        // Add components to the search panel
        searchPanel.add(enterDateLabel);
        searchPanel.add(dateTextField);
        searchPanel.add(searchButton);

        model = new DefaultTableModel();
        table = new JTable(model);
        scrollPane = new JScrollPane(table);

        // Define the column order
        String[] columnNames = {"Order Date", "Report ID", "Total Amount"};

        // Add columns to the model in the desired order
        for (String columnName : columnNames) {
            model.addColumn(columnName);
        }

        // Populate the table with data from the database
        fetchDataFromDatabase();

        // Center-align the table data
        DefaultTableCellRenderer centerRenderer = new DefaultTableCellRenderer();
        centerRenderer.setHorizontalAlignment(JLabel.CENTER);
        table.setDefaultRenderer(Object.class, centerRenderer);

        // Make the table columns bold and increase their font size
        Font columnFont = new Font("Arial", Font.BOLD, 16);
        table.getTableHeader().setFont(columnFont);

        // Increase the font size for the table records (rows) but don't make them bold
        Font rowFont = new Font("Arial", Font.PLAIN, 16);
        table.setFont(rowFont);

        // Increase the row height to make cells bigger and more attractive
        int rowHeight = 30; // Adjust this value as needed
        table.setRowHeight(rowHeight);

        getContentPane().add(searchPanel, BorderLayout.NORTH);
        getContentPane().add(scrollPane, BorderLayout.CENTER);

        // Initialize the dateFormat instance variable
        dateFormat = createDateFormat();
    }

    // Create a separate class to handle menu events
    private class MenuListener implements ActionListener {
        public void actionPerformed(ActionEvent e) {
            String command = e.getActionCommand();
            switch (command) {
                case "About":
                    JOptionPane.showMessageDialog(new JFrame(), "About Button clicked!");
                    break;
                case "Back":
                    dispose(); // Close the current HomePage frame
                    StaffPage staffPage = new StaffPage();
                    break;
                case "Exit":
                    int option = JOptionPane.showConfirmDialog(new JFrame(),"Are you sure you want to exit?",
                            "Exit Confirmation", JOptionPane.YES_NO_OPTION, JOptionPane.QUESTION_MESSAGE);
                    if (option == JOptionPane.YES_OPTION) {
                        System.exit(0);
                    }
                    break;
            }
        }
    }
    private SimpleDateFormat createDateFormat() {
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
        dateFormat.setLenient(false); // Disallow invalid date input
        return dateFormat;
    }

    private void fetchDataFromDatabase() {
        Connection connection = null;
        PreparedStatement preparedStatement = null;
        ResultSet resultSet = null;

        try {
            // Establish the database connection
            String jdbcUrl = "jdbc:mysql://localhost:3306/SagarDatabase";
            String username = "root";
            String password = "";

            // Load the JDBC driver class (handle the ClassNotFoundException)
            Class.forName("com.mysql.jdbc.Driver");

            connection = DriverManager.getConnection(jdbcUrl, username, password);

            // SQL query to retrieve data from the OrderReport table
            String query = "SELECT order_date, report_id, total_amount FROM OrderReport";
            preparedStatement = connection.prepareStatement(query);
            resultSet = preparedStatement.executeQuery();

            // Clear existing rows in the table
            model.setRowCount(0);

            // Fetch and display data in the table
            while (resultSet.next()) {
                Timestamp orderDate = resultSet.getTimestamp("order_date");
                int reportId = resultSet.getInt("report_id");
                double totalAmount = resultSet.getDouble("total_amount");
                // Add a row to the table
                model.addRow(new Object[]{orderDate, reportId, totalAmount});
            }
        } catch (SQLException | ClassNotFoundException e) {
            e.printStackTrace();
        } finally {
            try {
                if (resultSet != null) resultSet.close();
                if (preparedStatement != null) preparedStatement.close();
                if (connection != null) connection.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }

    private void performSearch(Date searchDate) {
        String searchTerm = searchField.getText().trim();
        Connection connection = null;
        PreparedStatement preparedStatement = null;
        ResultSet resultSet = null;

        try {
            // Establish the database connection
            String jdbcUrl = "jdbc:mysql://localhost:3306/SagarDatabase";
            String username = "root";
            String password = "";

            // Load the JDBC driver class (handle the ClassNotFoundException)
            Class.forName("com.mysql.jdbc.Driver");

            connection = DriverManager.getConnection(jdbcUrl, username, password);

            // SQL query to retrieve data from the OrderReport table based on the search term and date
            String query = "SELECT order_date, report_id, total_amount FROM OrderReport WHERE DATE(order_date) = ? AND (report_id LIKE ? OR total_amount LIKE ?)";
            preparedStatement = connection.prepareStatement(query);
            preparedStatement.setDate(1, searchDate);
            preparedStatement.setString(2, "%" + searchTerm + "%");
            preparedStatement.setString(3, "%" + searchTerm + "%");
            resultSet = preparedStatement.executeQuery();

            // Clear existing rows in the table
            model.setRowCount(0);

            // Fetch and display data in the table
            while (resultSet.next()) {
                Timestamp orderDate = resultSet.getTimestamp("order_date");
                int reportId = resultSet.getInt("report_id");
                double totalAmount = resultSet.getDouble("total_amount");
                // Add a row to the table
                model.addRow(new Object[]{orderDate, reportId, totalAmount});
            }
        } catch (SQLException | ClassNotFoundException e) {
            e.printStackTrace();
        } finally {
            try {
                if (resultSet != null) resultSet.close();
                if (preparedStatement != null) preparedStatement.close();
                if (connection != null) connection.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            OrderReport orderReport = new OrderReport();
            orderReport.setVisible(true);
        });
    }
}