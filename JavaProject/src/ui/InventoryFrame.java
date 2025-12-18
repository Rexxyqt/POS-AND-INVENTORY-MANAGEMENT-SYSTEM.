package src.ui;

import src.dao.InventoryDAO;
import src.dao.ProductDAO;
import src.dao.CategoryDAO;
import src.model.Category;
import src.model.Product;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.List;

public class InventoryFrame extends JFrame {

    // FIELDS
    private final ProductDAO productDAO = new ProductDAO();
    private final InventoryDAO inventoryDAO = new InventoryDAO();
    private JTable stockTable;
    private DefaultTableModel stockModel;
    private JTable lowStockTable;
    private DefaultTableModel lowStockModel;
    private JTextField txtThreshold;
    private JTextField txtAdjustQty;
    private JTextField txtAdjustReason;
    private JTable productTable;
    private DefaultTableModel productModel;
    private JComboBox<String> actionCombo;

    // CONSTRUCTOR
    public InventoryFrame() {
        setTitle("RJ HARDWARE AND ELECTRONICS");
        setExtendedState(JFrame.MAXIMIZED_BOTH);
        setDefaultCloseOperation(DISPOSE_ON_CLOSE);
        setLayout(new BorderLayout(10, 10));

        try {
            setIconImage(new ImageIcon(getClass().getResource("/src/util/images/icon.png")).getImage());
        } catch (Exception e) {
            System.out.println("Icon not found, using default");
        }

        JPanel topPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        JButton btnBack = new JButton("← Back to Menu");
        styleButton(btnBack, new java.awt.Color(0, 51, 0), true);
        btnBack.addActionListener(e -> {
            new MainMenuFrame().setVisible(true);
            this.dispose();
        });
        topPanel.add(btnBack);
        add(topPanel, BorderLayout.NORTH);

        JTabbedPane tabbedPane = new JTabbedPane();

        tabbedPane.addTab("Stock Levels", createStockLevelsPanel());

        tabbedPane.addTab("Low Stock Alerts", createLowStockPanel());

        tabbedPane.addTab("Inventory Logs", createInventoryLogsPanel());

        tabbedPane.addTab("Stock Adjustment", createStockAdjustmentPanel());

        add(tabbedPane, BorderLayout.CENTER);

        tabbedPane.addChangeListener(e -> {
            if (tabbedPane.getSelectedIndex() == 3) {
                loadProductTable();
            }
        });

        setVisible(true);
    }

    // METHODS
    private JPanel createStockLevelsPanel() {
        JPanel panel = new JPanel(new BorderLayout(10, 10));

        stockModel = new DefaultTableModel(new String[] {
                "No.", "ID", "Product Name", "Category", "Current Stock", "Unit", "Status" }, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        stockTable = new JTable(stockModel);
        stockTable = new JTable(stockModel);
        stockTable.setRowHeight(30);
        stockTable.getTableHeader().setBackground(new java.awt.Color(0, 51, 0));
        stockTable.getTableHeader().setForeground(Color.WHITE);
        stockTable.getTableHeader().setFont(new Font("Segoe UI", Font.BOLD, 12));
        stockTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);

        stockTable.getColumnModel().getColumn(0).setMinWidth(30);
        stockTable.getColumnModel().getColumn(0).setMaxWidth(30);
        stockTable.getColumnModel().getColumn(0).setPreferredWidth(30);

        stockTable.getColumnModel().getColumn(1).setMinWidth(0);
        stockTable.getColumnModel().getColumn(1).setMaxWidth(0);
        stockTable.getColumnModel().getColumn(1).setWidth(0);

        loadStockLevels();

        panel.add(new JScrollPane(stockTable), BorderLayout.CENTER);

        JPanel buttonPanel = new JPanel(new FlowLayout());
        JButton btnRefresh = new JButton("Refresh");
        styleButton(btnRefresh, new java.awt.Color(0, 51, 0), false);
        btnRefresh.addActionListener(e -> loadStockLevels());
        buttonPanel.add(btnRefresh);
        panel.add(buttonPanel, BorderLayout.SOUTH);

        return panel;
    }

    private JPanel createLowStockPanel() {
        JPanel panel = new JPanel(new BorderLayout(10, 10));

        JPanel thresholdPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        thresholdPanel.add(new JLabel("Low Stock Threshold:"));
        txtThreshold = new JTextField(10);
        txtThreshold.setText("10");
        thresholdPanel.add(txtThreshold);

        JButton btnCheck = new JButton("Check Low Stock");
        styleButton(btnCheck, new java.awt.Color(0, 51, 0), true);
        btnCheck.addActionListener(e -> checkLowStock(true));
        thresholdPanel.add(btnCheck);
        panel.add(thresholdPanel, BorderLayout.NORTH);

        lowStockModel = new DefaultTableModel(new String[] {
                "No.", "ID", "Product Name", "Current Stock", "Threshold", "Status" }, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        lowStockTable = new JTable(lowStockModel);
        lowStockTable = new JTable(lowStockModel);
        lowStockTable.setRowHeight(30);
        lowStockTable.getTableHeader().setBackground(new java.awt.Color(0, 51, 0));
        lowStockTable.getTableHeader().setForeground(Color.WHITE);
        lowStockTable.getTableHeader().setFont(new Font("Segoe UI", Font.BOLD, 12));

        lowStockTable.getColumnModel().getColumn(0).setMinWidth(30);
        lowStockTable.getColumnModel().getColumn(0).setMaxWidth(30);
        lowStockTable.getColumnModel().getColumn(0).setPreferredWidth(30);

        lowStockTable.getColumnModel().getColumn(1).setMinWidth(0);
        lowStockTable.getColumnModel().getColumn(1).setMaxWidth(0);
        lowStockTable.getColumnModel().getColumn(1).setWidth(0);

        panel.add(new JScrollPane(lowStockTable), BorderLayout.CENTER);

        checkLowStock(false);

        return panel;
    }

    private JPanel createInventoryLogsPanel() {
        JPanel panel = new JPanel(new BorderLayout(10, 10));

        DefaultTableModel logsModel = new DefaultTableModel(new String[] {
                "Date", "Product", "Activity Type", "Quantity Change", "Description" }, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };

        JTable logsTable = new JTable(logsModel);
        logsTable.setRowHeight(30);
        logsTable.getTableHeader().setBackground(new java.awt.Color(0, 51, 0));
        logsTable.getTableHeader().setForeground(Color.WHITE);
        logsTable.getTableHeader().setFont(new Font("Segoe UI", Font.BOLD, 12));
        loadInventoryLogs(logsModel);

        panel.add(new JScrollPane(logsTable), BorderLayout.CENTER);

        JPanel buttonPanel = new JPanel(new FlowLayout());
        JButton btnRefresh = new JButton("Refresh");
        styleButton(btnRefresh, new java.awt.Color(0, 51, 0), false);
        btnRefresh.addActionListener(e -> loadInventoryLogs(logsModel));
        buttonPanel.add(btnRefresh);
        panel.add(buttonPanel, BorderLayout.SOUTH);

        return panel;
    }

    private JPanel createStockAdjustmentPanel() {
        JPanel panel = new JPanel(new BorderLayout(10, 10));

        productModel = new DefaultTableModel(new String[] {
                "ID", "Product Name", "Current Stock" }, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        productTable = new JTable(productModel);
        productTable = new JTable(productModel);
        productTable.setRowHeight(30);
        productTable.getTableHeader().setBackground(new java.awt.Color(0, 51, 0));
        productTable.getTableHeader().setForeground(Color.WHITE);
        productTable.getTableHeader().setFont(new Font("Segoe UI", Font.BOLD, 12));
        productTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);

        productTable.getColumnModel().getColumn(0).setMinWidth(0);
        productTable.getColumnModel().getColumn(0).setMaxWidth(0);
        productTable.getColumnModel().getColumn(0).setWidth(0);

        loadProductTable();

        JPanel tablePanel = new JPanel(new BorderLayout());
        tablePanel.setBorder(BorderFactory.createTitledBorder("Step 1: Select Product"));
        tablePanel.add(new JScrollPane(productTable), BorderLayout.CENTER);
        panel.add(tablePanel, BorderLayout.CENTER);

        JPanel form = new JPanel(new GridBagLayout());
        form.setBorder(BorderFactory.createTitledBorder("Step 2: Adjustment Details"));
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(10, 10, 10, 10);
        gbc.anchor = GridBagConstraints.WEST;

        gbc.gridx = 0;
        gbc.gridy = 0;
        form.add(new JLabel("Action:"), gbc);
        gbc.gridx = 1;
        actionCombo = new JComboBox<>(new String[] { "Add Stock (+)", "Remove Stock (-)" });
        actionCombo.setPreferredSize(new Dimension(200, 30));
        form.add(actionCombo, gbc);

        gbc.gridx = 0;
        gbc.gridy = 1;
        form.add(new JLabel("Quantity:"), gbc);
        gbc.gridx = 1;
        txtAdjustQty = new JTextField(15);
        form.add(txtAdjustQty, gbc);

        gbc.gridx = 0;
        gbc.gridy = 2;
        form.add(new JLabel("Reason:"), gbc);
        gbc.gridx = 1;
        txtAdjustReason = new JTextField(30);
        form.add(txtAdjustReason, gbc);

        gbc.gridx = 0;
        gbc.gridy = 3;
        gbc.gridwidth = 2;
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
        JButton btnAdjust = new JButton("Apply Adjustment");
        styleButton(btnAdjust, new java.awt.Color(0, 51, 0), true);
        btnAdjust.addActionListener(e -> applyAdjustment());
        buttonPanel.add(btnAdjust);
        form.add(buttonPanel, gbc);

        panel.add(form, BorderLayout.SOUTH);

        return panel;
    }

    private void loadProductTable() {
        productModel.setRowCount(0);
        List<Product> products = productDAO.getAllProducts();
        for (Product p : products) {
            productModel.addRow(new Object[] {
                    p.getId(),
                    p.getName(),
                    p.getQuantity()
            });
        }
    }

    private void loadStockLevels() {
        stockModel.setRowCount(0);

        List<Product> products = productDAO.getAllProducts();
        int rowNum = 1;
        for (Product p : products) {
            Category cat = new CategoryDAO().getCategoryById(p.getCategoryId());
            int threshold = p.getStockThreshold() > 0 ? p.getStockThreshold() : 10;
            String status = p.getQuantity() > threshold ? "In Stock"
                    : p.getQuantity() > 0 ? "Low Stock" : "Out of Stock";
            stockModel.addRow(new Object[] {
                    rowNum++,
                    p.getId(),
                    p.getName(),
                    cat != null ? cat.getName() : "N/A",
                    p.getQuantity(),
                    p.getUnit(),
                    status
            });
        }
    }

    private void checkLowStock(boolean showMessage) {
        lowStockModel.setRowCount(0);

        int threshold;
        try {
            threshold = Integer.parseInt(txtThreshold.getText().trim());
            if (threshold < 0) {
                if (showMessage) {
                    JOptionPane.showMessageDialog(this, "Threshold must be non-negative.", "Invalid Input",
                            JOptionPane.WARNING_MESSAGE);
                }
                return;
            }
        } catch (NumberFormatException e) {
            if (showMessage) {
                JOptionPane.showMessageDialog(this, "Please enter a valid threshold.", "Invalid Input",
                        JOptionPane.WARNING_MESSAGE);
            }
            return;
        }

        List<Product> lowStockProducts = inventoryDAO.getLowStockProducts(threshold);

        int rowNum = 1;
        for (Product p : lowStockProducts) {
            String status = p.getQuantity() == 0 ? "Out of Stock" : "Low Stock";
            lowStockModel.addRow(new Object[] {
                    rowNum++,
                    p.getId(),
                    p.getName(),
                    p.getQuantity(),
                    threshold,
                    status
            });
        }

        if (lowStockProducts.isEmpty() && showMessage) {
            JOptionPane.showMessageDialog(this,
                    "No products found below threshold of " + threshold, "No Low Stock",
                    JOptionPane.INFORMATION_MESSAGE);
        }
    }

    private void loadInventoryLogs(DefaultTableModel model) {
        model.setRowCount(0);
        List<Object[]> logs = inventoryDAO.getAllInventoryLogs();

        for (Object[] log : logs) {
            String date = log[4].toString();
            String productName = log[6] != null ? log[6].toString() : "N/A";
            String activityType = log[1].toString();
            int qtyChange = (int) log[2];
            String description = log[3] != null ? log[3].toString() : "";

            model.addRow(new Object[] {
                    date,
                    productName,
                    activityType,
                    qtyChange,
                    description
            });
        }
    }

    private void applyAdjustment() {
        int selectedRow = productTable.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this,
                    "Please select a product from the table.", "No Product Selected",
                    JOptionPane.WARNING_MESSAGE);
            return;
        }

        int productId = (int) productModel.getValueAt(selectedRow, 0);

        int quantityChange;
        try {
            quantityChange = Integer.parseInt(txtAdjustQty.getText().trim());
            if (quantityChange <= 0) {
                JOptionPane.showMessageDialog(this, "Quantity must be a positive number.", "Invalid Input",
                        JOptionPane.WARNING_MESSAGE);
                return;
            }
        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(this, "Please enter a valid quantity.", "Invalid Input",
                    JOptionPane.WARNING_MESSAGE);
            return;
        }

        String action = (String) actionCombo.getSelectedItem();
        if ("Remove Stock (-)".equals(action)) {
            quantityChange = -quantityChange;
        }

        String reason = txtAdjustReason.getText().trim();
        if (reason.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Please enter a reason for the adjustment.", "Validation Error",
                    JOptionPane.WARNING_MESSAGE);
            return;
        }

        Product product = productDAO.getProductById(productId);
        if (product != null && (product.getQuantity() + quantityChange) < 0) {
            JOptionPane.showMessageDialog(this,
                    "Adjustment would result in negative stock. Current stock: " + product.getQuantity(),
                    "Invalid Adjustment", JOptionPane.WARNING_MESSAGE);
            return;
        }

        if (inventoryDAO.adjustInventory(productId, quantityChange, reason)) {
            JOptionPane.showMessageDialog(this, "Stock adjustment applied successfully!", "Success",
                    JOptionPane.INFORMATION_MESSAGE);
            txtAdjustQty.setText("");
            txtAdjustReason.setText("");
            loadStockLevels();
            loadProductTable();
        } else {
            JOptionPane.showMessageDialog(this, "Failed to apply stock adjustment.", "Error",
                    JOptionPane.ERROR_MESSAGE);
        }
    }

    private void styleButton(JButton btn, Color bgColor, boolean bold) {
        btn.setBackground(bgColor);
        btn.setForeground(Color.WHITE);
        btn.setFont(new Font("Segoe UI", bold ? Font.BOLD : Font.PLAIN, 12));
        btn.setFocusPainted(false);
    }
}
