import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class StaffDialog extends JDialog {
    private JTextField idField;
    private JTextField usernameField;
    private JPasswordField passwordField;
    private JTextField addressField;
    private JTextField contactField;
    private JTextField emailField;
    private JButton confirmButton;
    private JButton cancelButton;
    private boolean isConfirmed;

    public StaffDialog(JFrame parentFrame, String title) {
        super(parentFrame, title, true);
        setSize(400, 300);
        setLocationRelativeTo(parentFrame);

        JPanel panel = new JPanel(new GridBagLayout());
        GridBagConstraints constraints = new GridBagConstraints();
        constraints.fill = GridBagConstraints.HORIZONTAL;
        constraints.insets = new Insets(5, 10, 5, 10);

        idField = new JTextField(10);
        usernameField = new JTextField(20);
        passwordField = new JPasswordField(20);
        addressField = new JTextField(20);
        contactField = new JTextField(10);
        emailField = new JTextField(20);

        constraints.gridx = 0;
        constraints.gridy = 0;
        panel.add(new JLabel("ID:"), constraints);

        constraints.gridx = 1;
        panel.add(idField, constraints);

        constraints.gridx = 0;
        constraints.gridy = 1;
        panel.add(new JLabel("Username:"), constraints);

        constraints.gridx = 1;
        panel.add(usernameField, constraints);

        constraints.gridx = 0;
        constraints.gridy = 2;
        panel.add(new JLabel("Password:"), constraints);

        constraints.gridx = 1;
        panel.add(passwordField, constraints);

        constraints.gridx = 0;
        constraints.gridy = 3;
        panel.add(new JLabel("Address:"), constraints);

        constraints.gridx = 1;
        panel.add(addressField, constraints);

        constraints.gridx = 0;
        constraints.gridy = 4;
        panel.add(new JLabel("Contact:"), constraints);

        constraints.gridx = 1;
        panel.add(contactField, constraints);

        constraints.gridx = 0;
        constraints.gridy = 5;
        panel.add(new JLabel("Email:"), constraints);

        constraints.gridx = 1;
        panel.add(emailField, constraints);

        confirmButton = new JButton("Confirm");
        cancelButton = new JButton("Cancel");

        constraints.gridx = 0;
        constraints.gridy = 6;
        constraints.gridwidth = 2;
        panel.add(confirmButton, constraints);

        constraints.gridy = 7;
        panel.add(cancelButton, constraints);

        confirmButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                isConfirmed = true;
                dispose();
            }
        });

        cancelButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                isConfirmed = false;
                dispose();
            }
        });

        add(panel);
    }

    public int getID() {
        return Integer.parseInt(idField.getText());
    }

    public void setID(int id) {
        idField.setText(Integer.toString(id));
    }

    public String getUsername() {
        return usernameField.getText();
    }

    public String getPassword() {
        char[] passwordChars = passwordField.getPassword();
        return new String(passwordChars);
    }

    public String getAddress() {
        return addressField.getText();
    }

    public long getContact() {
        try {
            return Long.parseLong(contactField.getText());
        } catch (NumberFormatException e) {
            return 0;
        }
    }

    public String getEmail() {
        return emailField.getText();
    }

    public boolean isConfirmed() {
        return isConfirmed;
    }
}