import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class StaffLogin implements ActionListener {
    private JFrame frame;
    private JPanel panel, loginPanel;
    private JLabel lblUsername, lblPassword, lblHeader;
    private JTextField txtUsername, txtPhoneNumber;
    private JPasswordField txtPassword, txtNewPassword;
    private JButton btnLogin, btnForgotPassword;

    public StaffLogin() {
        frame = new JFrame("Yummy Kitchen Hub");
        panel = new JPanel();
        loginPanel = new JPanel();
        lblUsername = new JLabel("Username: ");
        txtUsername = new JTextField(15);
        lblPassword = new JLabel("Password: ");
        lblHeader = new JLabel("STAFF LOGIN PAGE");
        txtPassword = new JPasswordField(15);
        btnLogin = new JButton("Login");
        btnForgotPassword = new JButton("Forgot Password ?");

        panel.setLayout(null);
        Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
        frame.setSize(screenSize.width, screenSize.height);
        frame.addWindowListener(new WindowAdapter() {
            public void windowClosing(WindowEvent windowEvent) {
                System.exit(0);
            }
        });

        panel.setBackground(new Color(0, 51, 102));

        loginPanel.setLayout(null);
        loginPanel.setBounds((screenSize.width - 400) / 2, (screenSize.height - 300) / 2, 400, 350);
        loginPanel.setBackground(Color.WHITE);

        lblHeader.setBounds((loginPanel.getWidth() - 300) / 2, 20, 300, 50);
        lblHeader.setFont(new Font("Arial", Font.BOLD, 28));
        lblHeader.setForeground(new Color(44, 62, 80));

        int x = 50, y = 80, width = 300, height = 30, spacing = 40;
        lblUsername.setBounds(x, y, width, height);
        txtUsername.setBounds(x, y + spacing, width, height);
        lblPassword.setBounds(x, y + 2 * spacing, width, height);
        txtPassword.setBounds(x, y + 3 * spacing, width, height);
        btnLogin.setBounds(50, y + 5 * spacing, 100, 30);
        btnForgotPassword.setBounds(200, y + 5 * spacing, 150, 30); // Add this line

        lblUsername.setFont(new Font("Arial", Font.PLAIN, 18));
        lblPassword.setFont(new Font("Arial", Font.PLAIN, 18));
        txtUsername.setFont(new Font("Arial", Font.PLAIN, 16));
        txtPassword.setFont(new Font("Arial", Font.PLAIN, 16));
        btnLogin.setFont(new Font("Arial", Font.BOLD, 14));
        btnLogin.setBackground(new Color(52, 152, 219));
        btnLogin.setForeground(Color.WHITE);

        loginPanel.add(lblHeader);
        loginPanel.add(lblUsername);
        loginPanel.add(txtUsername);
        loginPanel.add(lblPassword);
        loginPanel.add(txtPassword);
        loginPanel.add(btnLogin);
        loginPanel.add(btnForgotPassword);
        panel.add(loginPanel);
        frame.add(panel);

        JLabel headerLabel = new JLabel();
        headerLabel = new JLabel("STAFF LOGIN PAGE", JLabel.CENTER);
        headerLabel.setFont(new Font(null, Font.BOLD, 30));
        headerLabel.setForeground(Color.white);
        frame.getContentPane().setBackground(new Color(0, 153, 255));
        frame.add(headerLabel, BorderLayout.NORTH);

        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setVisible(true);
        frame.setResizable(false);
        frame.setLocationRelativeTo(null);

        btnLogin.addActionListener(this);
        btnForgotPassword.addActionListener(this);
        btnForgotPassword.setActionCommand("Forgot Password");

        JMenuBar menuBar = new JMenuBar();
        frame.setJMenuBar(menuBar);

        JMenu aboutMenu = new JMenu("About");
        menuBar.add(aboutMenu);

        JMenu backMenu = new JMenu("Back");
        menuBar.add(backMenu);

        JMenu exitMenu = new JMenu("Exit");
        menuBar.add(exitMenu);

        JMenuItem aboutMenuItem = new JMenuItem("About");
        aboutMenuItem.setActionCommand("About");
        aboutMenuItem.addActionListener(this);
        aboutMenu.add(aboutMenuItem);

        JMenuItem backMenuItem = new JMenuItem("Back");
        backMenuItem.setActionCommand("Back");
        backMenuItem.addActionListener(this);
        backMenu.add(backMenuItem);

        JMenuItem exitMenuItem = new JMenuItem("Exit");
        exitMenuItem.setActionCommand("Exit");
        exitMenuItem.addActionListener(this);
        exitMenu.add(exitMenuItem);
    }

    public void actionPerformed(ActionEvent e) {
        String command = e.getActionCommand();
        switch (command) {
            case "About":
                JOptionPane.showMessageDialog(frame, "About Button clicked!");
                break;
            case "Back":
                //JOptionPane.showMessageDialog(frame, "Back Button clicked!");
                // Handle the back action here
                frame.dispose();
                HomePage homePage = new HomePage();
                break;
            case "Exit":
                int option = JOptionPane.showConfirmDialog(frame, "Are you sure you want to exit?",
                        "Exit Confirmation", JOptionPane.YES_NO_OPTION, JOptionPane.QUESTION_MESSAGE);
                if (option == JOptionPane.YES_OPTION) {
                    System.exit(0);
                }
            case "Login":
                performLogin();
                StaffPage staffPage = new StaffPage();
                break;
            case "Forgot Password":
                showResetPasswordDialog();
                break;
        }
    }

    private void showResetPasswordDialog() {
        JFrame resetFrame = new JFrame("Reset Password");
        JPanel resetPanel = new JPanel();
        resetPanel.setLayout(null);

        JLabel lblResetHeader = new JLabel("Reset Password");
        lblResetHeader.setBounds(125, 20, 150, 30);
        lblResetHeader.setFont(new Font("Arial", Font.BOLD, 18));

        JLabel lblResetUsername = new JLabel("Username: ");
        lblResetUsername.setBounds(50, 70, 100, 20);
        JTextField txtResetUsername = new JTextField(15);
        txtResetUsername.setBounds(160, 70, 150, 20);

        JLabel lblResetPhoneNumber = new JLabel("Phone Number: ");
        lblResetPhoneNumber.setBounds(50, 100, 100, 20);
        JTextField txtResetPhoneNumber = new JTextField(15);
        txtResetPhoneNumber.setBounds(160, 100, 150, 20);

        JLabel lblNewPassword = new JLabel("New Password: ");
        lblNewPassword.setBounds(50, 130, 100, 20);
        JPasswordField txtNewPassword = new JPasswordField(15);
        txtNewPassword.setBounds(160, 130, 150, 20);

        JButton btnResetPassword = new JButton("Reset Password");
        btnResetPassword.setBounds(130, 170, 150, 30);

        resetPanel.add(lblResetHeader);
        resetPanel.add(lblResetUsername);
        resetPanel.add(txtResetUsername);
        resetPanel.add(lblResetPhoneNumber);
        resetPanel.add(txtResetPhoneNumber);
        resetPanel.add(lblNewPassword);
        resetPanel.add(txtNewPassword);
        resetPanel.add(btnResetPassword);

        resetFrame.add(resetPanel);
        resetFrame.setSize(400, 300);
        resetFrame.setResizable(false);
        resetFrame.setLocationRelativeTo(null);
        resetFrame.setVisible(true);

        btnResetPassword.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                String username = txtResetUsername.getText();
                String phoneNumber = txtResetPhoneNumber.getText();
                String newPassword = new String(txtNewPassword.getPassword());
                resetPassword(username, phoneNumber, newPassword);
                resetFrame.dispose();
            }
        });
    }

    private void performLogin() {
        String url = "jdbc:mysql://localhost:3306/SagarDatabase";
        String username = "root";
        String password = "";

        String inputUsername = txtUsername.getText();
        String inputPassword = new String(txtPassword.getPassword());

        try {
            Class.forName("com.mysql.jdbc.Driver");
            Connection connection = DriverManager.getConnection(url, username, password);
            String sql = "SELECT * FROM Staff WHERE s_username = ? AND s_password = ?";
            PreparedStatement preparedStatement = connection.prepareStatement(sql);
            preparedStatement.setString(1, inputUsername);
            preparedStatement.setString(2, inputPassword);
            ResultSet resultSet = preparedStatement.executeQuery();

            if (resultSet.next()) {
                JOptionPane.showMessageDialog(frame, "Login Successful!");
            } else {
                JOptionPane.showMessageDialog(frame, "Invalid Username or Password!");
            }

            resultSet.close();
            preparedStatement.close();
            connection.close();
        } catch (ClassNotFoundException | SQLException ex) {
            ex.printStackTrace();
            JOptionPane.showMessageDialog(frame, "Database Connection Error!");
        }
    }

    private void resetPassword(String username, String phoneNumber, String newPassword) {
        String url = "jdbc:mysql://localhost:3306/SagarDatabase";
        String dbUsername = "root";
        String dbPassword = "";

        try {
            Class.forName("com.mysql.jdbc.Driver");
            Connection connection = DriverManager.getConnection(url, dbUsername, dbPassword);
            String sql = "UPDATE Staff SET s_password = ? WHERE s_username = ? AND s_contact = ?";
            PreparedStatement preparedStatement = connection.prepareStatement(sql);
            preparedStatement.setString(1, newPassword);
            preparedStatement.setString(2, username);
            preparedStatement.setString(3, phoneNumber);
            int affectedRows = preparedStatement.executeUpdate();

            if (affectedRows > 0) {
                JOptionPane.showMessageDialog(frame, "Password Reset Successful!");
            } else {
                JOptionPane.showMessageDialog(frame, "Password Reset Failed. Please check your Username and Phone Number.");
            }

            preparedStatement.close();
            connection.close();
        } catch (ClassNotFoundException | SQLException ex) {
            ex.printStackTrace();
            JOptionPane.showMessageDialog(frame, "Database Connection Error!");
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new StaffLogin());
    }
}