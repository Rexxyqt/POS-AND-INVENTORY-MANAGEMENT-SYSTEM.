package src.ui;

import src.db.DatabaseConnection;
import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

public class LoginFrame extends JFrame {

    // FIELDS
    private JTextField txtUsername;
    private JPasswordField txtPassword;
    private JButton btnLogin;
    private JLabel lblStatus;

    // CONSTRUCTOR
    public LoginFrame() {
        setTitle("RJ HARDWARE AND ELECTRONICS");
        try {
            setIconImage(new ImageIcon(getClass().getResource("/src/util/images/icon.png")).getImage());
        } catch (Exception e) {
            System.out.println("Icon not found");
        }

        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setResizable(false);
        setSize(980, 680);
        setLocationRelativeTo(null);

        setLayout(null);
        getContentPane().setBackground(Color.WHITE);

        initUI();
    }

    // UI INITIALIZATION
    private void initUI() {
        JPanel leftPanel = new JPanel(new BorderLayout());
        leftPanel.setBackground(new Color(0, 51, 0));
        leftPanel.setBounds(0, 0, 490, 650);

        JPanel imageWrapper = new JPanel();
        imageWrapper.setLayout(new BoxLayout(imageWrapper, BoxLayout.Y_AXIS));
        imageWrapper.setBackground(new Color(0, 51, 0));

        JLabel imageLabel = new JLabel();
        try {
            ImageIcon originalIcon = new ImageIcon(
                    getClass().getResource("/src/util/images/RJ_HARDWARE_AND_ELECTRONICS.png"));
            int originalWidth = originalIcon.getIconWidth();
            int originalHeight = originalIcon.getIconHeight();

            int targetWidth = 380;
            int targetHeight = (int) ((double) originalHeight / originalWidth * targetWidth);

            Image scaledImage = originalIcon.getImage().getScaledInstance(targetWidth, targetHeight,
                    Image.SCALE_SMOOTH);
            imageLabel.setIcon(new ImageIcon(scaledImage));
        } catch (Exception e) {
            imageLabel.setText("RJ HARDWARE AND ELECTRONICS");
            imageLabel.setForeground(Color.WHITE);
        }
        imageLabel.setHorizontalAlignment(SwingConstants.CENTER);
        imageLabel.setAlignmentX(Component.CENTER_ALIGNMENT);

        imageWrapper.add(Box.createVerticalGlue());
        imageWrapper.add(imageLabel);
        imageWrapper.add(Box.createVerticalGlue());

        leftPanel.add(imageWrapper, BorderLayout.CENTER);
        add(leftPanel);

        // CENTERED COORDINATES
        int x = 561;
        int width = 347;

        JLabel lblProfileIcon = new JLabel();
        lblProfileIcon.setHorizontalAlignment(SwingConstants.CENTER);
        try {
            java.net.URL imgUrl = getClass().getResource("/src/util/images/profileIcon.png");
            if (imgUrl == null) {
                java.io.File imgFile = new java.io.File("src/util/images/profileIcon.png");
                if (imgFile.exists()) {
                    imgUrl = imgFile.toURI().toURL();
                } else {
                    java.io.File absFile = new java.io.File(
                            "c:\\Users\\rexje\\Downloads\\Myproj\\JavaProject\\src\\util\\images\\profileIcon.png");
                    if (absFile.exists()) {
                        imgUrl = absFile.toURI().toURL();
                    }
                }
            }

            if (imgUrl != null) {
                ImageIcon originalIcon = new ImageIcon(imgUrl);
                Image img = originalIcon.getImage();
                Image newImg = img.getScaledInstance(130, 130, java.awt.Image.SCALE_SMOOTH);
                lblProfileIcon.setIcon(new ImageIcon(newImg));
            } else {
                System.err.println("Profile icon not found");
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        lblProfileIcon.setBounds(x, 90, width, 130);
        add(lblProfileIcon);

        JLabel lblTitle = new JLabel("LOGIN ACCOUNT", SwingConstants.CENTER);
        lblTitle.setFont(new Font("Segoe UI", Font.BOLD, 26));
        lblTitle.setForeground(Color.BLACK);
        lblTitle.setBounds(x, 242, width, 40);
        add(lblTitle);

        txtUsername = new JTextField("Username");
        styleTextField(txtUsername);
        txtUsername.setBounds(x, 309, width, 39);
        add(txtUsername);

        txtPassword = new JPasswordField("Password");
        styleTextField(txtPassword);
        txtPassword.setEchoChar((char) 0);
        txtPassword.setBounds(x, 385, width, 39);
        add(txtPassword);

        btnLogin = new JButton("SIGN IN");
        btnLogin.setBackground(new Color(0, 51, 0));
        btnLogin.setForeground(Color.WHITE);
        btnLogin.setFont(new Font("Segoe UI", Font.BOLD, 14));
        btnLogin.setFocusPainted(false);
        btnLogin.setBounds(x, 476, width, 40);
        add(btnLogin);

        lblStatus = new JLabel("", SwingConstants.CENTER);
        lblStatus.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        lblStatus.setBounds(x, 534, width, 25);
        add(lblStatus);
        setupListeners();
        getRootPane().setDefaultButton(btnLogin);
    }

    // HELPERS
    private void styleTextField(JTextField field) {
        field.setFont(new Font("Segoe UI", Font.PLAIN, 18));
        field.setBackground(Color.WHITE);
        field.setForeground(new Color(150, 150, 150));
        field.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(Color.LIGHT_GRAY, 1),
                BorderFactory.createEmptyBorder(5, 10, 5, 10)));
    }

    // LISTENERS
    private void setupListeners() {
        txtUsername.addFocusListener(new FocusAdapter() {
            @Override
            public void focusGained(FocusEvent e) {
                if (txtUsername.getText().equals("Username")) {
                    txtUsername.setText("");
                    txtUsername.setForeground(Color.BLACK);
                }
            }

            @Override
            public void focusLost(FocusEvent e) {
                if (txtUsername.getText().isEmpty()) {
                    txtUsername.setText("Username");
                    txtUsername.setForeground(new Color(150, 150, 150));
                }
            }
        });

        txtPassword.addFocusListener(new FocusAdapter() {
            @Override
            public void focusGained(FocusEvent e) {
                if (String.valueOf(txtPassword.getPassword()).equals("Password")) {
                    txtPassword.setText("");
                    txtPassword.setForeground(Color.BLACK);
                    txtPassword.setEchoChar('•');
                }
            }

            @Override
            public void focusLost(FocusEvent e) {
                if (String.valueOf(txtPassword.getPassword()).isEmpty()) {
                    txtPassword.setText("Password");
                    txtPassword.setForeground(new Color(150, 150, 150));
                    txtPassword.setEchoChar((char) 0);
                }
            }
        });

        btnLogin.addActionListener(e -> performLogin());
    }

    // LOGIC
    private void performLogin() {
        String username = txtUsername.getText();
        String password = String.valueOf(txtPassword.getPassword());

        lblStatus.setText("");

        if (username.equals("Username") || password.equals("Password") || username.isEmpty() || password.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Please fill in the form.");
            return;
        }

        try (Connection conn = DatabaseConnection.getConnection()) {
            PreparedStatement pst = conn.prepareStatement("SELECT * FROM users WHERE username=? AND password=?");
            pst.setString(1, username);
            pst.setString(2, password);

            ResultSet rs = pst.executeQuery();

            if (rs.next()) {
                JOptionPane.showMessageDialog(this, "Login Successful!");
                MainMenuFrame menu = new MainMenuFrame();
                menu.setVisible(true);
                this.dispose();
            } else {
                lblStatus.setText("Invalid username or password.");
                lblStatus.setForeground(Color.RED);
            }

        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Database Error: " + e.getMessage());
        }
    }
}
