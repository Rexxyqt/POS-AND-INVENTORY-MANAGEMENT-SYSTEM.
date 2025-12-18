package src.ui;

import javax.swing.*;
import java.awt.*;

public class MainMenuFrame extends JFrame {

    // CONSTRUCTOR
    public MainMenuFrame() {
        setTitle("RJ HARDWARE AND ELECTRONICS");
        setExtendedState(JFrame.MAXIMIZED_BOTH);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLayout(new BorderLayout(10, 10));

        try {
            setIconImage(new ImageIcon(getClass().getResource("/src/util/images/icon.png")).getImage());
        } catch (Exception e) {
            System.out.println("Icon not found, using default");
        }

        JPanel headerPanel = new JPanel(new BorderLayout());
        headerPanel.setBackground(new java.awt.Color(0, 51, 0));
        headerPanel.setBorder(BorderFactory.createEmptyBorder(15, 20, 15, 20));

        JLabel titleLabel = new JLabel("POINT-OF-SALE AND INVENTORY MANAGEMENT SYSTEM");
        titleLabel.setFont(new Font("Segoe UI", Font.BOLD, 24));
        titleLabel.setForeground(Color.WHITE);
        titleLabel.setHorizontalAlignment(SwingConstants.LEFT);
        headerPanel.add(titleLabel, BorderLayout.WEST);

        JLabel profileLabel = new JLabel();
        profileLabel.setCursor(new Cursor(Cursor.HAND_CURSOR));
        try {
            ImageIcon profileIcon = new ImageIcon(getClass().getResource("/src/util/images/menu_profileIcon.png"));
            Image scaledProfile = profileIcon.getImage().getScaledInstance(40, 40, Image.SCALE_SMOOTH);
            profileLabel.setIcon(new ImageIcon(scaledProfile));
        } catch (Exception e) {
            profileLabel.setText(" [Profile] ");
            profileLabel.setForeground(Color.WHITE);
        }

        JPopupMenu profileMenu = new JPopupMenu();

        JMenuItem logoutItem = new JMenuItem("LOGOUT");
        logoutItem.setFont(new Font("Segoe UI", Font.BOLD, 14));
        logoutItem.setBackground(new Color(180, 0, 0));
        logoutItem.setForeground(Color.WHITE);
        logoutItem.setOpaque(true);
        logoutItem.setPreferredSize(new Dimension(250, 40));
        logoutItem.addActionListener(e -> logout());
        profileMenu.add(logoutItem);

        profileLabel.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                profileMenu.show(profileLabel, evt.getX(), evt.getY());
            }
        });

        headerPanel.add(profileLabel, BorderLayout.EAST);

        add(headerPanel, BorderLayout.NORTH);

        JPanel menuPanel = new JPanel(new GridLayout(3, 2, 15, 15));
        menuPanel.setBorder(BorderFactory.createEmptyBorder(30, 30, 30, 30));
        menuPanel.setBackground(Color.WHITE);

        JButton btnProducts = createMenuButton("Product Management",
                "Manage products, categories, brands, and suppliers", "/src/util/images/product_icon.png");
        btnProducts.addActionListener(e -> {
            new ProductFrame().setVisible(true);
            this.dispose();
        });
        menuPanel.add(btnProducts);

        JButton btnSales = createMenuButton("Sales Module",
                "Process sales transactions and view sales history", "/src/util/images/sales_icon.png");
        btnSales.addActionListener(e -> {
            new SalesFrame().setVisible(true);
            this.dispose();
        });
        menuPanel.add(btnSales);

        JButton btnInventory = createMenuButton("Inventory Management",
                "View stock levels, low-stock alerts, and inventory logs", "/src/util/images/inventory_icon.png");
        btnInventory.addActionListener(e -> {
            new InventoryFrame().setVisible(true);
            this.dispose();
        });
        menuPanel.add(btnInventory);

        JButton btnReports = createMenuButton("Reports",
                "Generate product, sales, and inventory reports", "/src/util/images/Report.png");
        btnReports.addActionListener(e -> {
            new ReportsFrame().setVisible(true);
            this.dispose();
        });
        menuPanel.add(btnReports);

        JButton btnCategories = createMenuButton("Category Management",
                "Add, edit, and delete categories", "/src/util/images/category_icon.png");
        btnCategories.addActionListener(e -> {
            new CategoryManagementFrame().setVisible(true);
            this.dispose();
        });
        menuPanel.add(btnCategories);

        JButton btnBrandSupplier = createMenuButton("Brand & Supplier",
                "Manage brands and suppliers", "/src/util/images/brand_supplier_icon.png");
        btnBrandSupplier.addActionListener(e -> {
            new BrandSupplierManagementFrame().setVisible(true);
            this.dispose();
        });
        menuPanel.add(btnBrandSupplier);

        add(menuPanel, BorderLayout.CENTER);

        setVisible(true);
    }

    // METHODS
    private JButton createMenuButton(String title, String description, String iconPath) {
        JButton button = new JButton();
        button.setLayout(new BorderLayout(10, 10));
        button.setPreferredSize(new Dimension(250, 120));
        button.setBackground(Color.WHITE);
        button.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(Color.LIGHT_GRAY),
                BorderFactory.createEmptyBorder(15, 20, 15, 20)));
        button.setCursor(new Cursor(Cursor.HAND_CURSOR));

        JPanel leftPanel = new JPanel(new BorderLayout());
        leftPanel.setBackground(Color.WHITE);

        JPanel lightPanel = new JPanel();
        lightPanel.setPreferredSize(new Dimension(5, 0));
        lightPanel.setBackground(Color.WHITE);
        leftPanel.add(lightPanel, BorderLayout.WEST);

        JLabel iconLabel = new JLabel();
        try {
            ImageIcon originalIcon = new ImageIcon(getClass().getResource(iconPath));
            Image scaledImage = originalIcon.getImage().getScaledInstance(50, 50, Image.SCALE_SMOOTH);
            iconLabel.setIcon(new ImageIcon(scaledImage));
        } catch (Exception e) {
            try {
                ImageIcon defaultIcon = new ImageIcon(getClass().getResource("/src/util/images/default_icon.png"));
                Image scaledDefault = defaultIcon.getImage().getScaledInstance(50, 50, Image.SCALE_SMOOTH);
                iconLabel.setIcon(new ImageIcon(scaledDefault));
            } catch (Exception ex) {
                System.out.println("Default icon not found!");
            }
        }
        leftPanel.add(iconLabel, BorderLayout.CENTER);

        button.add(leftPanel, BorderLayout.WEST);

        JLabel titleLabel = new JLabel(title);
        titleLabel.setFont(new Font("Segoe UI", Font.BOLD, 18));
        titleLabel.setHorizontalAlignment(SwingConstants.LEFT);
        titleLabel.setForeground(Color.BLACK);
        button.add(titleLabel, BorderLayout.NORTH);

        JLabel descLabel = new JLabel("<html><p style='width:180px'>" + description + "</p></html>");
        descLabel.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        descLabel.setForeground(Color.DARK_GRAY);
        button.add(descLabel, BorderLayout.CENTER);

        button.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseEntered(java.awt.event.MouseEvent evt) {
                lightPanel.setBackground(new java.awt.Color(0, 51, 0));
                button.setBorder(BorderFactory.createCompoundBorder(
                        BorderFactory.createLineBorder(new java.awt.Color(0, 51, 0), 2),
                        BorderFactory.createEmptyBorder(15, 20, 15, 20)));
            }

            public void mouseExited(java.awt.event.MouseEvent evt) {
                lightPanel.setBackground(Color.WHITE);
                button.setBorder(BorderFactory.createCompoundBorder(
                        BorderFactory.createLineBorder(Color.LIGHT_GRAY),
                        BorderFactory.createEmptyBorder(15, 20, 15, 20)));
            }
        });

        return button;
    }

    private void logout() {
        int confirm = JOptionPane.showConfirmDialog(this,
                "Are you sure you want to logout?", "Logout",
                JOptionPane.YES_NO_OPTION);
        if (confirm == JOptionPane.YES_OPTION) {
            Window[] windows = Window.getWindows();
            for (Window window : windows) {
                if (window instanceof JFrame && !(window instanceof LoginFrame)) {
                    window.dispose();
                }
            }
            new LoginFrame().setVisible(true);
        }
    }

}
