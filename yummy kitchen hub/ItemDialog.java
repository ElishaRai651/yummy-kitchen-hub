import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class ItemDialog extends JDialog {
    private JTextField idField;
    private JTextField nameField;
    private JTextField priceField;
    private JButton confirmButton;
    private JButton cancelButton;
    private boolean isConfirmed;

    public ItemDialog(JFrame parentFrame, String title) {
        super(parentFrame, title, true);
        setSize(350, 200);
        setLocationRelativeTo(parentFrame);

        JPanel panel = new JPanel(new GridBagLayout());
        GridBagConstraints constraints = new GridBagConstraints();
        constraints.fill = GridBagConstraints.HORIZONTAL;
        constraints.insets = new Insets(5, 10, 5, 10);

        idField = new JTextField(10);
        nameField = new JTextField(20);
        priceField = new JTextField(10);

        constraints.gridx = 0;
        constraints.gridy = 0;
        panel.add(new JLabel("ID:"), constraints);

        constraints.gridx = 1;
        panel.add(idField, constraints);

        constraints.gridx = 0;
        constraints.gridy = 1;
        panel.add(new JLabel("Name:"), constraints);

        constraints.gridx = 1;
        panel.add(nameField, constraints);

        constraints.gridx = 0;
        constraints.gridy = 2;
        panel.add(new JLabel("Price:"), constraints);

        constraints.gridx = 1;
        panel.add(priceField, constraints);

        confirmButton = new JButton("Confirm");
        cancelButton = new JButton("Cancel");

        constraints.gridx = 0;
        constraints.gridy = 3;
        constraints.gridwidth = 2;
        panel.add(confirmButton, constraints);

        constraints.gridy = 4;
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

    public String getName() {

        return nameField.getText();
    }

    public double getPrice() {
        return Double.parseDouble(priceField.getText());
    }

    public boolean isConfirmed() {
        return isConfirmed;
    }
}