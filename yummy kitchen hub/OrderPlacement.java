import java.awt.*;
import java.awt.event.*;
import java.sql.*;
import javax.swing.*;
import javax.swing.table.*;
import java.util.ArrayList;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashSet;
import java.util.Set;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.TableCellRenderer;


public class OrderPlacement {
    private JFrame mainFrame;
    private JPanel controlPanel;
    private JLabel headerLabel;
    private JLabel tableNumberLabel; // New JLabel to display the table number

    private int selectedTableNumber; // New variable to hold the selected table number
    private boolean tableNumberEntered = false;
    private int currentTableNumber = 0; // Track the current table being processed
    private JTextField searchField;
    private JTable table;
    private DefaultTableModel tableModel;
    private JTextArea orderTextArea;
    private JTable orderTable;
    private DefaultTableModel orderTableModel;

    public OrderPlacement() {
        prepareGUI();
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            OrderPlacement OrderExample = new OrderPlacement();
        });
    }

    public class BillGeneratorDialog extends JDialog {
        public BillGeneratorDialog(JFrame parent, int tableNumber, ArrayList<BillItem> billItems, double totalAmount) {
            super(parent, "Bill", true);
            //Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
            //setSize(screenSize.width, screenSize.height);
            setSize(500,700);
            setLocationRelativeTo(parent);

            JPanel panel = new JPanel();
            panel.setLayout(new BoxLayout(panel, BoxLayout.PAGE_AXIS));

            // Create a header panel with background color spanning full width
            JPanel headerPanel = new JPanel();
            headerPanel.setBackground(new Color(0, 51, 102)); // Set background color (Orange)
            headerPanel.setLayout(new BorderLayout());

            // Add Yummy Kitchen Hub header label to the header panel
            JLabel headerLabel = new JLabel("Yummy Kitchen Hub");
            headerLabel.setFont(new Font(null, Font.BOLD, 24));
            headerLabel.setForeground(Color.white); // Set text color
            headerLabel.setHorizontalAlignment(JLabel.CENTER); // Center-align text
            headerPanel.add(headerLabel, BorderLayout.CENTER);

            // Add the header panel to the main panel with a one-row gap
            panel.add(headerPanel);
            panel.add(Box.createVerticalStrut(20)); // Add a one-row gap

            // Add date of bill
            SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd / HH:mm:ss");
            JLabel dateLabel = new JLabel("Date: " + dateFormat.format(new Date()));
            dateLabel.setFont(new Font(null, Font.PLAIN, 16));
            dateLabel.setAlignmentX(Component.LEFT_ALIGNMENT);
            panel.add(dateLabel);

            // Add table number with one-row gap
            JLabel tableLabel = new JLabel("Table Number: " + tableNumber);
            tableLabel.setFont(new Font(null, Font.PLAIN, 16));
            tableLabel.setAlignmentX(Component.LEFT_ALIGNMENT);
            panel.add(tableLabel);
            panel.add(Box.createVerticalStrut(20)); // Add a one-row gap

            // Create a table model for the bill items
            DefaultTableModel tableModel = new DefaultTableModel() {
                @Override
                public boolean isCellEditable(int row, int column) {
                    return false;
                }
            };
            tableModel.addColumn("Food Name");
            tableModel.addColumn("Quantity");
            tableModel.addColumn("Unit Price");
            tableModel.addColumn("Total Price");

            for (BillItem item : billItems) {
                double unitPrice = item.getTotalPrice() / item.getQuantity();
                tableModel.addRow(new Object[]{item.getFoodName(), item.getQuantity(), unitPrice, item.getTotalPrice()});
            }

            JTable table = new JTable(tableModel);

            // Center-align cell contents for all columns
            DefaultTableCellRenderer centerRenderer = new DefaultTableCellRenderer();
            centerRenderer.setHorizontalAlignment(JLabel.CENTER);
            table.setDefaultRenderer(Object.class, centerRenderer);

            // Increase column size and set font to bold
            table.getTableHeader().setFont(new Font(null, Font.BOLD, 18));

            // Remove bold from table records (set font to plain)
            Font plainFont = new Font(null, Font.PLAIN, 16);
            table.setFont(plainFont);
            table.getTableHeader().setFont(new Font(null, Font.BOLD, 18));
            table.setRowHeight(30);

            JScrollPane scrollPane = new JScrollPane(table);
            scrollPane.setAlignmentX(Component.CENTER_ALIGNMENT); // Center the table
            panel.add(scrollPane);

            // Add total bill amount
            JLabel totalLabel = new JLabel("Total Bill Amount: " + totalAmount);
            totalLabel.setFont(new Font(null, Font.BOLD, 20));
            totalLabel.setAlignmentX(Component.LEFT_ALIGNMENT);
            panel.add(totalLabel);

            // Add a one-row gap before the "Thank you" panel
            panel.add(Box.createVerticalStrut(20));

            // Create a panel for the "Thank you" message label with background color
            JPanel thankYouPanel = new JPanel();
            thankYouPanel.setBackground(new Color(0, 51, 102)); // Set background color (Orange)

            // Add "Thank you" message label to the panel
            JLabel thankYouLabel = new JLabel("Thank you for dining at Yummy Kitchen Hub!");
            thankYouLabel.setFont(new Font(null, Font.BOLD, 18));
            thankYouLabel.setForeground(Color.white); // Set text color
            thankYouLabel.setHorizontalAlignment(JLabel.CENTER); // Center-align text
            thankYouPanel.add(thankYouLabel);

            // Add the "Thank you" panel to the main panel
            panel.add(thankYouPanel);

            getContentPane().add(panel);
        }
    }
    private void prepareGUI() {
        mainFrame = new JFrame("Yummy Kitchen Hub");
        Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
        mainFrame.setSize(screenSize.width, screenSize.height);
        mainFrame.getContentPane().setBackground(new Color(0, 153, 255));
        mainFrame.setLayout(new BorderLayout());
        mainFrame.addWindowListener(new WindowAdapter() {
            public void windowClosing(WindowEvent windowEvent) {
                System.exit(0);
            }
        });

        controlPanel = new JPanel();
        controlPanel.setLayout(new BorderLayout());
        controlPanel.setBackground(new Color(0, 51, 102));

        headerLabel = new JLabel("", JLabel.CENTER);
        headerLabel.setFont(new Font(null, Font.BOLD, 25));
        headerLabel.setForeground(Color.white);

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

        // Create a panel for the right-hand side
        JPanel orderPanel = new JPanel();
        orderPanel.setBackground(new Color(220, 220, 220));
        orderPanel.setLayout(new BorderLayout());

        JSplitPane splitPane = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT);
        splitPane.setDividerLocation(500);
        splitPane.setLeftComponent(controlPanel);
        splitPane.setRightComponent(orderPanel);

        mainFrame.add(splitPane, BorderLayout.CENTER);

        // Create a new JTable instance
        table = new JTable();

        JScrollPane scrollPane = new JScrollPane(table);
        controlPanel.add(scrollPane, BorderLayout.CENTER);

        // Initialize orderTextArea
        orderTextArea = new JTextArea(10, 30);
        orderTextArea.setEditable(false);
        orderTextArea.setFont(new Font(null, Font.PLAIN, 14));

        // Create components for order placement
        JLabel orderLabel = new JLabel("Order Details");
        orderLabel.setFont(new Font(null, Font.BOLD, 20));
        orderLabel.setHorizontalAlignment(JLabel.CENTER);

        // Add components to the order panel
        orderPanel.add(orderLabel, BorderLayout.NORTH);
        orderPanel.add(orderTextArea, BorderLayout.CENTER);

        JButton tableSearchButton = new JButton("Table Status");
        JButton addToCartButton = new JButton("Place Order");
        JButton generateBillButton = new JButton("Generate Bill");
        JButton markAsPaidButton = new JButton("Mark as Paid");

        JPanel buttonPanel = new JPanel();
        buttonPanel.setLayout(new FlowLayout(FlowLayout.CENTER, 10, 10));
        buttonPanel.add(tableSearchButton);
        buttonPanel.add(addToCartButton);
        buttonPanel.add(generateBillButton);
        buttonPanel.add(markAsPaidButton); // Add the button to the buttonPanel
        orderPanel.add(buttonPanel, BorderLayout.SOUTH);

        // Create a new JTable for displaying orders
        orderTable = new JTable();
        orderTableModel = new DefaultTableModel();
        orderTableModel.addColumn("Food Name");
        orderTableModel.addColumn("Quantity");
        orderTableModel.addColumn("Total Price");

        // Set the model for the orderTable
        orderTable.setModel(orderTableModel);

        // Create a JScrollPane for the orderTable
        JScrollPane orderScrollPane = new JScrollPane(orderTable);

        // Add the JScrollPane to the orderPanel
        orderPanel.add(orderScrollPane, BorderLayout.CENTER);

        // Initialize the table number label
        tableNumberLabel = new JLabel("Table Number: ");
        tableNumberLabel.setFont(new Font(null, Font.BOLD, 20));
        tableNumberLabel.setHorizontalAlignment(JLabel.CENTER);
        orderPanel.add(tableNumberLabel, BorderLayout.NORTH);

        // Set the font for the orderTable headers
        JTableHeader orderTableHeader = orderTable.getTableHeader();
        orderTableHeader.setFont(new Font(null, Font.BOLD, 18));

        // Set the font for the orderTable rows
        orderTable.setFont(new Font(null, Font.PLAIN, 16));
        orderTable.setRowHeight(30);

        // Set the renderer to center-align the content
        DefaultTableCellRenderer centerRenderer = new DefaultTableCellRenderer();
        centerRenderer.setHorizontalAlignment(JLabel.CENTER);
        orderTable.setDefaultRenderer(Object.class, centerRenderer);

        JMenuBar menuBar = new JMenuBar();
        mainFrame.setJMenuBar(menuBar);

        JMenu aboutMenu = new JMenu("About");
        menuBar.add(aboutMenu);

        JMenu backMenu = new JMenu("Back");
        menuBar.add(backMenu);

        JMenu exitMenu = new JMenu("Exit");
        menuBar.add(exitMenu);

        // Create a menu listener
        OrderPlacement.MenuListener menuListener = new OrderPlacement.MenuListener();

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

        // Create "OK" button for finishing operations
        JButton okButton = new JButton("Finish Order");
        okButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                // Clear the orderTableModel
                orderTableModel.setRowCount(0);

                // Clear the orderTextArea
                orderTextArea.setText("");

                // Reset operations for the current table
                currentTableNumber = 0;

                // Clear the table number label
                tableNumberLabel.setText("Table Number: ");

                // Display a message to indicate finishing operations
                JOptionPane.showMessageDialog(mainFrame, "Operations finished for the current table.", "Finish", JOptionPane.INFORMATION_MESSAGE);
                // Change the button text to "Add to Cart"
                addToCartButton.setText("Place Order");
            }
        });

        // Add the "OK" button to the buttonPanel
        buttonPanel.add(okButton);

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

        addToCartButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if (currentTableNumber == 0) {
                    // Prompt user for table number since operations for the current table are finished
                    String tableNumberStr = JOptionPane.showInputDialog(mainFrame, "Enter table number (1-16):");
                    if (tableNumberStr != null && !tableNumberStr.isEmpty()) {
                        try {
                            int enteredTableNumber = Integer.parseInt(tableNumberStr);
                            if (enteredTableNumber >= 1 && enteredTableNumber <= 16) {
                                currentTableNumber = enteredTableNumber;
                                selectedTableNumber = currentTableNumber; // Update selectedTableNumber
                                // Update the table number label
                                tableNumberLabel.setText("Table Number: " + currentTableNumber);
                            } else {
                                JOptionPane.showMessageDialog(mainFrame, "Table number must be between 1 and 16.", "Error", JOptionPane.ERROR_MESSAGE);
                                return; // Don't proceed with adding an item
                            }
                        } catch (NumberFormatException ex) {
                            JOptionPane.showMessageDialog(mainFrame, "Invalid table number.", "Error", JOptionPane.ERROR_MESSAGE);
                            return; // Don't proceed with adding an item
                        }
                    } else {
                        return; // Don't proceed with adding an item if the user canceled the input dialog
                    }
                }
                // Change the button text to "Add to Cart"
                addToCartButton.setText("Add to Cart");
                int selectedRow = table.getSelectedRow();
                if (selectedRow >= 0) {
                    Object foodId = table.getValueAt(selectedRow, 0);
                    Object foodName = table.getValueAt(selectedRow, 1);
                    Object foodPrice = table.getValueAt(selectedRow, 2);

                    String quantityStr = JOptionPane.showInputDialog(mainFrame, "Enter quantity:");
                    if (quantityStr != null && !quantityStr.isEmpty()) {
                        try {
                            int quantity = Integer.parseInt(quantityStr);
                            if (quantity > 0) {
                                // Calculate the total price based on the quantity
                                double totalPrice = (double) foodPrice * quantity;

                                String cartItem = foodName + " - " + quantity + " x  " + foodPrice + " =  " + totalPrice;
                                orderTextArea.append(cartItem + "\n");

                                // Store the order in the "Orders" table with the specified quantity
                                try {
                                    storeOrder(selectedTableNumber, (int) foodId, foodName.toString(), (double) foodPrice, quantity); // Pass 'quantity'
                                } catch (SQLException ex) {
                                    ex.printStackTrace();
                                    JOptionPane.showMessageDialog(mainFrame, "Error storing order.");
                                }
                            } else {
                                JOptionPane.showMessageDialog(mainFrame, "Quantity must be greater than 0.", "Error", JOptionPane.ERROR_MESSAGE);
                            }
                        } catch (NumberFormatException ex) {
                            JOptionPane.showMessageDialog(mainFrame, "Invalid quantity.", "Error", JOptionPane.ERROR_MESSAGE);
                        }
                    } else {
                        return; // Don't proceed with adding an item if the user canceled the input dialog
                    }
                } else {
                    JOptionPane.showMessageDialog(mainFrame, "Please select a food item to add to the cart.");
                }

                // Display previous and new orders for the selected table
                displayPreviousOrders(selectedTableNumber);
            }
        });

        tableSearchButton.addActionListener(e -> {
            displayTableStatusButtons();
        });

        generateBillButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                // Prompt the user to enter the table number
                String tableNumberStr = JOptionPane.showInputDialog(mainFrame, "Enter table number to generate the bill:");

                if (tableNumberStr != null && !tableNumberStr.isEmpty()) {
                    try {
                        int tableNumber = Integer.parseInt(tableNumberStr);
                        generateBillForTable(tableNumber);
                    } catch (NumberFormatException ex) {
                        JOptionPane.showMessageDialog(mainFrame, "Invalid table number.", "Error", JOptionPane.ERROR_MESSAGE);
                    }
                }
            }
        });

        markAsPaidButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                // Get a list of pending table numbers
                ArrayList<Integer> pendingTables = getPendingTableNumbers();

                if (pendingTables.isEmpty()) {
                    JOptionPane.showMessageDialog(mainFrame, "No pending orders found.", "No Pending Orders", JOptionPane.INFORMATION_MESSAGE);
                    return;
                }

                // Prompt the user to choose a table number from the list of pending tables
                Integer selectedTableNumber = (Integer) JOptionPane.showInputDialog(mainFrame,
                        "Choose a table number to mark as paid (pending orders only):",
                        "Mark as Paid",
                        JOptionPane.QUESTION_MESSAGE,
                        null,
                        pendingTables.toArray(),
                        pendingTables.get(0)); // Default selection

                if (selectedTableNumber != null) { // Check if a selection was made
                    try {
                        // Confirm the marking as paid
                        int confirmResult = JOptionPane.showConfirmDialog(mainFrame,
                                "Are you sure you want to mark the orders for table " + selectedTableNumber + " as paid?",
                                "Confirm Mark as Paid",
                                JOptionPane.YES_NO_OPTION);

                        if (confirmResult == JOptionPane.YES_OPTION) {
                            // Update the database to mark the orders as paid
                            markTableOrdersAsPaid(selectedTableNumber);
                            JOptionPane.showMessageDialog(mainFrame, "Orders for table " + selectedTableNumber + " have been marked as paid.");

                            // Calculate the total amount for the selected table number
                            double totalAmount = calculateTotalAmount(selectedTableNumber);

                            // Insert the total amount and other details into the OrderReport table
                            insertOrderReport(selectedTableNumber, totalAmount);
                            // Remove records for the specific table from the Orders table
                            removeOrdersForTable(selectedTableNumber);
                        }
                    } catch (NumberFormatException ex) {
                        JOptionPane.showMessageDialog(mainFrame, "Invalid table number.", "Error", JOptionPane.ERROR_MESSAGE);
                    }
                }
            }
        });

        mainFrame.setVisible(true);
        mainFrame.setLocationRelativeTo(null);
        mainFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
    }

    private double calculateTotalAmount(int tableNumber) {
        double totalAmount = 0.0;
        Connection connection = null;
        try {
            connection = getConnection();
            String sql = "SELECT SUM(total_food_price) FROM Orders WHERE table_number = ? AND is_paid = 1";
            PreparedStatement pst = connection.prepareStatement(sql);
            pst.setInt(1, tableNumber);
            ResultSet rs = pst.executeQuery();
            rs.next();
            totalAmount = rs.getDouble(1);
        } catch (SQLException ex) {
            ex.printStackTrace();
            JOptionPane.showMessageDialog(mainFrame, "Error calculating total amount.", "Error", JOptionPane.ERROR_MESSAGE);
        } finally {
            if (connection != null) {
                try {
                    connection.close();
                } catch (SQLException ex) {
                    ex.printStackTrace();
                }
            }
        }
        return totalAmount;
    }

    private void removeOrdersForTable(int tableNumber) {
        Connection connection = null;
        try {
            connection = getConnection();
            String sql = "DELETE FROM Orders WHERE table_number = ?";
            PreparedStatement pst = connection.prepareStatement(sql);
            pst.setInt(1, tableNumber);
            int rowsDeleted = pst.executeUpdate();

            if (rowsDeleted > 0) {
                System.out.println("Orders for table " + tableNumber + " deleted successfully.");
            } else {
                System.out.println("No orders found for table " + tableNumber);
            }
        } catch (SQLException ex) {
            ex.printStackTrace();
            JOptionPane.showMessageDialog(mainFrame, "Error removing orders for table " + tableNumber, "Error", JOptionPane.ERROR_MESSAGE);
        } finally {
            if (connection != null) {
                try {
                    connection.close();
                } catch (SQLException ex) {
                    ex.printStackTrace();
                }
            }
        }
    }
    private void insertOrderReport(int tableNumber, double totalAmount) {
        Connection connection = null;
        try {
            connection = getConnection();
            String sql = "INSERT INTO OrderReport (table_number, total_amount, order_date) VALUES (?, ?, NOW())";
            PreparedStatement pst = connection.prepareStatement(sql);
            pst.setInt(1, tableNumber);
            pst.setDouble(2, totalAmount);
            int rowsInserted = pst.executeUpdate();
            if (rowsInserted > 0) {
                System.out.println("OrderReport inserted successfully.");
            } else {
                JOptionPane.showMessageDialog(mainFrame, "Error inserting into OrderReport.", "Error", JOptionPane.ERROR_MESSAGE);
            }
        } catch (SQLException ex) {
            ex.printStackTrace();
            JOptionPane.showMessageDialog(mainFrame, "Error inserting into OrderReport.", "Error", JOptionPane.ERROR_MESSAGE);
        } finally {
            if (connection != null) {
                try {
                    connection.close();
                } catch (SQLException ex) {
                    ex.printStackTrace();
                }
            }
        }
    }
    private ArrayList<Integer> getPendingTableNumbers() {
        ArrayList<Integer> pendingTables = new ArrayList<>();
        Connection connection = null;
        try {
            connection = getConnection();
            String sql = "SELECT DISTINCT table_number FROM Orders WHERE is_paid = 0";
            PreparedStatement pst = connection.prepareStatement(sql);
            ResultSet rs = pst.executeQuery();
            while (rs.next()) {
                int tableNumber = rs.getInt("table_number");
                pendingTables.add(tableNumber);
            }
        } catch (SQLException ex) {
            ex.printStackTrace();
            JOptionPane.showMessageDialog(mainFrame, "Error getting pending table numbers.", "Error", JOptionPane.ERROR_MESSAGE);
        } finally {
            if (connection != null) {
                try {
                    connection.close();
                } catch (SQLException ex) {
                    ex.printStackTrace();
                }
            }
        }
        return pendingTables;
    }
    private void markTableOrdersAsPaid(int tableNumber) {
        Connection connection = null;
        try {
            connection = getConnection();
            String sql = "UPDATE Orders SET is_paid = 1 WHERE table_number = ? AND is_paid = 0";
            PreparedStatement pst = connection.prepareStatement(sql);
            pst.setInt(1, tableNumber);
            int updatedRows = pst.executeUpdate();

            if (updatedRows > 0) {
            } else {
                JOptionPane.showMessageDialog(mainFrame, "No pending orders found for table " + tableNumber, "No Pending Orders", JOptionPane.INFORMATION_MESSAGE);
            }
        } catch (SQLException ex) {
            ex.printStackTrace();
            JOptionPane.showMessageDialog(mainFrame, "Error marking orders as paid.", "Error", JOptionPane.ERROR_MESSAGE);
        } finally {
            if (connection != null) {
                try {
                    connection.close();
                } catch (SQLException ex) {
                    ex.printStackTrace();
                }
            }
        }
    }
    private Connection establishConnection() {
        Connection connection = null;
        String url = "jdbc:mysql://localhost:3306/SagarDatabase";
        String username = "root";
        String password = "";
        try {
            Class.forName("com.mysql.jdbc.Driver");
            connection = DriverManager.getConnection(url, username, password);
        } catch (ClassNotFoundException | SQLException ex) {
            ex.printStackTrace();
            JOptionPane.showMessageDialog(mainFrame, "Error establishing database connection.", "Error", JOptionPane.ERROR_MESSAGE);
        }

        return connection;
    }
    private ArrayList<BillItem> retrieveOrderedItems(int tableNumber) {
        ArrayList<BillItem> billItems = new ArrayList<>();
        Connection connection = establishConnection(); // Get a valid database connection

        if (connection != null) {
            try {
                String sql = "SELECT food_name, quantity, total_food_price FROM Orders WHERE table_number = ?";
                PreparedStatement pst = connection.prepareStatement(sql);
                pst.setInt(1, tableNumber);
                ResultSet rs = pst.executeQuery();

                while (rs.next()) {
                    String foodName = rs.getString("food_name");
                    int quantity = rs.getInt("quantity");
                    double totalPrice = rs.getDouble("total_food_price");
                    billItems.add(new BillItem(foodName, quantity, totalPrice));
                }
            } catch (SQLException ex) {
                ex.printStackTrace();
                JOptionPane.showMessageDialog(mainFrame, "Error retrieving ordered items.", "Error", JOptionPane.ERROR_MESSAGE);
            } finally {
                try {
                    connection.close(); // Close the database connection when done
                } catch (SQLException ex) {
                    ex.printStackTrace();
                }
            }
        }
        return billItems;
    }
    private void generateBillForTable(int tableNumber) {
        Connection connection = null;
        ArrayList<BillItem> billItems = new ArrayList<>();
        double totalBillAmount = 0.0;

        try {
            // Initialize the database connection
            connection = getConnection();

            // Attempt to retrieve ordered items for the specified table number
            billItems = retrieveOrderedItems(tableNumber);

            if (billItems != null && !billItems.isEmpty()) {
                // Calculate unit prices and total prices for each item
                DefaultTableModel billTableModel = new DefaultTableModel();
                billTableModel.addColumn("Food Name");
                billTableModel.addColumn("Quantity");
                billTableModel.addColumn("Unit Price");
                billTableModel.addColumn("Total Price");

                for (BillItem item : billItems) {
                    double unitPrice = item.getTotalPrice() / item.getQuantity();
                    totalBillAmount += item.getTotalPrice();

                    // Add data to the billTableModel
                    billTableModel.addRow(new Object[]{item.getFoodName(), item.getQuantity(), unitPrice, item.getTotalPrice()});
                }

                // Create and display the BillGeneratorDialog with updated item details and total amount
                BillGeneratorDialog billDialog = new BillGeneratorDialog(mainFrame, tableNumber, billItems, totalBillAmount);
                billDialog.setVisible(true);
            } else {
                JOptionPane.showMessageDialog(mainFrame, "No items ordered for table " + tableNumber, "Bill", JOptionPane.INFORMATION_MESSAGE);
            }
        } catch (SQLException ex) {
            ex.printStackTrace();
            JOptionPane.showMessageDialog(mainFrame, "Error generating bill.", "Error", JOptionPane.ERROR_MESSAGE);
        } finally {
            // Close the database connection in the finally block
            if (connection != null) {
                try {
                    connection.close();
                } catch (SQLException ex) {
                    ex.printStackTrace();
                }
            }
        }
    }
    private double calculateTotalBill(ArrayList<BillItem> billItems) {
        double totalBillAmount = 0.0;

        for (BillItem item : billItems) {
            totalBillAmount += item.getTotalPrice();
        }
        return totalBillAmount;
    }

    private void displayTableStatusButtons() {
        // Create a new frame for the table status buttons
        JFrame tableStatusButtonsFrame = new JFrame("Table Status");
        tableStatusButtonsFrame.setSize(600, 500);

        JPanel tableStatusButtonsPanel = new JPanel();
        tableStatusButtonsPanel.setLayout(new GridLayout(0, 2, 10, 10)); // Two columns for table buttons
        tableStatusButtonsPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        try {
            Connection connection = getConnection();
            String sql = "SELECT DISTINCT table_number FROM Orders"; // Use DISTINCT to get unique table numbers
            PreparedStatement pst = connection.prepareStatement(sql);
            ResultSet rs = pst.executeQuery();

            // Create a set to store used table numbers for efficient lookup
            Set<Integer> usedTables = new HashSet<>();

            while (rs.next()) {
                int usedTableNumber = rs.getInt("table_number");
                usedTables.add(usedTableNumber);
            }

            connection.close();

            for (int tableNumber = 1; tableNumber <= 16; tableNumber++) {
                final int finalTableNumber = tableNumber; // Declare as final

                JButton tableButton = new JButton("Table " + finalTableNumber);

                boolean isUsed = usedTables.contains(finalTableNumber); // Check if the table is used

                if (isUsed) {
                    // Display the table button as occupied (red)
                    tableButton.setBackground(Color.RED);
                    tableButton.setForeground(Color.WHITE);
                    tableButton.setFont(new Font(null, Font.BOLD, 20));
                    tableButton.setToolTipText("Table Occupied");
                } else {
                    // Display the table button as available (green)
                    tableButton.setBackground(Color.GREEN);
                    tableButton.setForeground(Color.BLACK);
                    tableButton.setFont(new Font(null, Font.BOLD, 20));
                    tableButton.setToolTipText("Table Available");
                }

                // Add an action listener to the table button
                tableButton.addActionListener(e -> {
                    // Handle the table button click event
                    if (isUsed) {
                        JOptionPane.showMessageDialog(tableStatusButtonsFrame, "Table " + finalTableNumber + " is currently in use.");
                    } else {
                        JOptionPane.showMessageDialog(tableStatusButtonsFrame, "Table " + finalTableNumber + " is available.");
                    }
                });

                tableStatusButtonsPanel.add(tableButton);
            }
        } catch (SQLException ex) {
            ex.printStackTrace();
            JOptionPane.showMessageDialog(null, "Error retrieving table status.");
        }

        tableStatusButtonsFrame.add(tableStatusButtonsPanel);
        tableStatusButtonsFrame.setLocationRelativeTo(mainFrame); // Display the frame near the main frame
        tableStatusButtonsFrame.setVisible(true);
    }

    private void displayPreviousOrders(int tableNumber) {
        try {
            Connection connection = getConnection();
            String sql = "SELECT food_name, food_price, quantity FROM Orders WHERE table_number = ?";
            PreparedStatement pst = connection.prepareStatement(sql);
            pst.setInt(1, tableNumber);
            ResultSet rs = pst.executeQuery();

            // Clear the existing table data
            orderTableModel.setRowCount(0);

            while (rs.next()) {
                String foodName = rs.getString("food_name");
                double foodPrice = rs.getDouble("food_price");
                int quantity = rs.getInt("quantity");

                // Calculate the total price for each item based on its quantity
                double totalPrice = foodPrice * quantity;

                // Add the data to the orderTableModel
                orderTableModel.addRow(new Object[]{foodName, quantity, totalPrice});
            }

            connection.close();
        } catch (SQLException ex) {
            ex.printStackTrace();
            JOptionPane.showMessageDialog(null, "Error displaying previous orders.");
        }
    }

    private void storeOrder(int tableNumber, int foodId, String foodName, double foodPrice, int quantity) throws SQLException {
        Connection connection = getConnection();

        try {
            String sqlCheck = "SELECT COUNT(*) FROM Orders WHERE table_number = ? AND food_name = ?";
            PreparedStatement pstCheck = connection.prepareStatement(sqlCheck);
            pstCheck.setInt(1, tableNumber);
            pstCheck.setString(2, foodName);
            ResultSet rs = pstCheck.executeQuery();
            rs.next();
            int rowCount = rs.getInt(1);

            if (rowCount > 0) {
                // Food item already exists for this table, update quantity and total_food_price
                String sqlUpdate = "UPDATE Orders SET quantity = quantity + ?, total_food_price = total_food_price + ? WHERE table_number = ? AND food_name = ?";
                PreparedStatement pstUpdate = connection.prepareStatement(sqlUpdate);
                pstUpdate.setInt(1, quantity); // Update quantity
                pstUpdate.setDouble(2, foodPrice * quantity); // Update total_food_price
                pstUpdate.setInt(3, tableNumber);
                pstUpdate.setString(4, foodName);
                pstUpdate.executeUpdate();
            } else {
                // Food item doesn't exist for this table, insert new row with the specified quantity
                String sqlInsert = "INSERT INTO Orders (table_number, food_id, food_name, food_price, total_food_price, quantity) VALUES (?, ?, ?, ?, ?, ?)";
                PreparedStatement pstInsert = connection.prepareStatement(sqlInsert);
                pstInsert.setInt(1, tableNumber);
                pstInsert.setInt(2, foodId);
                pstInsert.setString(3, foodName);
                pstInsert.setDouble(4, foodPrice);
                pstInsert.setDouble(5, foodPrice * quantity); // Update total_food_price with the current food_price * quantity
                pstInsert.setInt(6, quantity); // Set the quantity to the specified value
                pstInsert.executeUpdate();
            }
        } catch (SQLException ex) {
            ex.printStackTrace();
            JOptionPane.showMessageDialog(mainFrame, "Error storing order.");
        } finally {
            connection.close();
        }
    }

    private void performSearch() {
        String searchTerm = searchField.getText().trim();
        if (!searchTerm.isEmpty()) {
            try {
                Connection connection = getConnection();
                String sql = "SELECT * FROM Food WHERE f_name LIKE ?";
                PreparedStatement pst = connection.prepareStatement(sql);
                pst.setString(1, searchTerm + "%");  // Use searchTerm + "%" to match items starting with the search term
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

    // Create a separate class to handle menu events
    private class MenuListener implements ActionListener {
        public void actionPerformed(ActionEvent e) {
            String command = e.getActionCommand();
            switch (command) {
                case "About":
                    JOptionPane.showMessageDialog(mainFrame, "About Button clicked!");
                    break;
                case "Back":
                    mainFrame.dispose(); // Close the current page
                    StaffPage staffPage = new StaffPage();
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
    private void showButtonDemo() throws SQLException {
        headerLabel.setText("Yummy Kitchen Hub");
        String[] columnNames = {"ID", "Food Name", "Price"};
        Object[][] data = new Object[100][3];
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

        // Set the font for the orderTable headers
        JTableHeader orderTableHeader = orderTable.getTableHeader();
        orderTableHeader.setFont(new Font(null, Font.BOLD, 18));

        // Set the font for the orderTable rows
        orderTable.setFont(new Font(null, Font.PLAIN, 16));
        orderTable.setRowHeight(30);

        // Set the renderer to center-align the content
        orderTable.setDefaultRenderer(Object.class, centerRenderer);
    }
}