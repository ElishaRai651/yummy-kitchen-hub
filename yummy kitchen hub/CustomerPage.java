import javax.swing.*;
import javax.swing.border.*;
import java.awt.*;
import java.awt.event.*;

public class CustomerPage {
    private JFrame mainFrame;
    private JPanel buttonPanel;
    private JLabel headerLabel;

    public CustomerPage() {
        prepareGUI();
    }

    private void prepareGUI() {
        mainFrame = new JFrame("Customer Page");
        Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
        mainFrame.setSize(screenSize.width, screenSize.height);
        mainFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        mainFrame.getContentPane().setBackground(new Color(0, 153, 255));
        mainFrame.addWindowListener(new WindowAdapter() {
            public void windowClosing(WindowEvent windowEvent) {
                System.exit(0);
            }
        });

        headerLabel = new JLabel("CUSTOMER PAGE", JLabel.CENTER);
        headerLabel.setFont(new Font(null, Font.BOLD, 30));
        headerLabel.setForeground(Color.white);
        mainFrame.add(headerLabel, BorderLayout.NORTH);

        buttonPanel = new JPanel();
        buttonPanel.setLayout(new GridBagLayout());
        buttonPanel.setBorder(BorderFactory.createEmptyBorder(20, 50, 20, 50));
        buttonPanel.setBackground(new Color(0, 51, 102)); // Set button panel background color

        mainFrame.add(buttonPanel, BorderLayout.CENTER);
        mainFrame.setVisible(true);
        mainFrame.setResizable(false);

        showButtonDemo();

        JMenuBar menuBar = new JMenuBar();
        mainFrame.setJMenuBar(menuBar);

        JMenu aboutMenu = new JMenu("About");
        menuBar.add(aboutMenu);

        JMenu backMenu = new JMenu("Back");
        menuBar.add(backMenu);

        JMenu exitMenu = new JMenu("Exit");
        menuBar.add(exitMenu);

        // Create a menu listener
        CustomerPage.MenuListener menuListener = new CustomerPage.MenuListener();

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
    public void showButtonDemo() {
        createButton("Menu Info", new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                //showMessage("Menu Info button clicked!");
                ItemInfo itemInfo = new ItemInfo();
            }
        });

        createButton("Exit", new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                //showMessage("Exit button clicked!");
                System.exit(0);
            }
        });

        createButton("Go Back", new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                //showMessage("Go Back Button clicked!");
                mainFrame.dispose();
                HomePage homePage = new HomePage();
            }
        });
        mainFrame.setLocationRelativeTo(null);
    }

    private void createButton(String buttonText, ActionListener actionListener) {
        JButton button = new JButton(buttonText);
        button.setFont(new Font(null, Font.BOLD, 24));
        button.setForeground(Color.white);
        button.setBackground(new Color(0, 102, 204)); // Updated button background color
        button.setFocusPainted(false);
        button.setBorder(new RoundedBorder(Color.white, 30)); // Adjusted border radius
        button.setPreferredSize(new Dimension(220, 70)); // button size

        button.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseEntered(MouseEvent e) {
                button.setBackground(new Color(0, 153, 255)); // Set background color when hovered
            }

            @Override
            public void mouseExited(MouseEvent e) {
                button.setBackground(new Color(0, 102, 204));
            }
        });

        button.addActionListener(actionListener);

        GridBagConstraints constraints = new GridBagConstraints();
        constraints.gridx = 0;
        constraints.gridy = GridBagConstraints.RELATIVE;
        constraints.fill = GridBagConstraints.HORIZONTAL;
        constraints.insets = new Insets(15, 0, 15, 0); // Increased vertical spacing
        buttonPanel.add(button, constraints);
    }

    private void showMessage(String message) {
        JOptionPane.showMessageDialog(mainFrame, message);
    }

    private static class RoundedBorder extends AbstractBorder {
        private final Color borderColor;
        private final int borderRadius;

        public RoundedBorder(Color borderColor, int borderRadius) {
            this.borderColor = borderColor;
            this.borderRadius = borderRadius;
        }

        @Override
        public void paintBorder(Component c, Graphics g, int x, int y, int width, int height) {
            Graphics2D g2 = (Graphics2D) g.create();

            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            g2.setColor(borderColor);
            g2.drawRoundRect(x, y, width - 1, height - 1, borderRadius, borderRadius);

            g2.dispose();
        }

        @Override
        public Insets getBorderInsets(Component c) {
            return new Insets(borderRadius, borderRadius, borderRadius, borderRadius);
        }

        @Override
        public Insets getBorderInsets(Component c, Insets insets) {
            insets.set(borderRadius, borderRadius, borderRadius, borderRadius);
            return insets;
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new CustomerPage());
    }
}