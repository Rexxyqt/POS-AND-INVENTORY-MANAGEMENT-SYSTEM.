package src.ui;

import src.dao.CategoryDAO;
import src.dao.ProductDAO;
import src.dao.SalesDAO;
import src.model.Category;
import src.model.Product;
import src.model.Sale;
import src.model.SaleItem;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.*;

import java.text.NumberFormat;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class SalesFrame extends JFrame {

    // FIELDS
    private final ProductDAO productDAO = new ProductDAO();
    private final SalesDAO salesDAO = new SalesDAO();
    private final CategoryDAO categoryDAO = new CategoryDAO();
    private JTable productTable;
    private DefaultTableModel productModel;
    private JComboBox<Category> cmbCategoryFilter;
    private JTextField txtProductSearch;
    private JTable cartTable;
    private DefaultTableModel cartModel;
    private List<SaleItem> cartItems = new ArrayList<>();
    private JLabel lblSubtotal, lblTotal;
    private JTextField txtTax;
    private JTextField txtQuantity;

    // CONSTRUCTOR
    public SalesFrame() {
        setTitle("RJ HARDWARE AND ELECTRONICS");
        setExtendedState(JFrame.MAXIMIZED_BOTH);
        setDefaultCloseOperation(DISPOSE_ON_CLOSE);
        setLayout(new BorderLayout(10, 10));

        try {
            setIconImage(new ImageIcon(getClass().getResource("/src/util/images/icon.png")).getImage());
        } catch (Exception e) {
            System.out.println("Icon not found, using default");
        }

        JPanel leftPanel = createProductSelectionPanel();
        add(leftPanel, BorderLayout.WEST);

        JPanel centerPanel = createCartPanel();
        add(centerPanel, BorderLayout.CENTER);

        JPanel rightPanel = createTotalsPanel();
        add(rightPanel, BorderLayout.EAST);

        JPanel topPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        JButton btnBack = new JButton("← Back to Menu");
        styleButton(btnBack, new java.awt.Color(0, 51, 0), true);
        btnBack.addActionListener(e -> {
            new MainMenuFrame().setVisible(true);
            this.dispose();
        });
        topPanel.add(btnBack);
        add(topPanel, BorderLayout.NORTH);

        setVisible(true);
    }

    // METHODS
    private JPanel createProductSelectionPanel() {
        JPanel panel = new JPanel(new BorderLayout(10, 10));
        panel.setBorder(BorderFactory.createTitledBorder("Product Selection"));
        panel.setPreferredSize(new Dimension(550, 0));

        JPanel searchPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 5, 5));
        searchPanel.add(new JLabel("Search:"));
        txtProductSearch = new JTextField(15);
        txtProductSearch.addKeyListener(new KeyAdapter() {
            public void keyReleased(KeyEvent e) {
                filterProducts();
            }
        });
        searchPanel.add(txtProductSearch);

        searchPanel.add(new JLabel("Category:"));
        cmbCategoryFilter = new JComboBox<>();
        cmbCategoryFilter.addItem(new Category(0, "All"));
        for (Category c : categoryDAO.getAllCategories()) {
            cmbCategoryFilter.addItem(c);
        }
        cmbCategoryFilter.addActionListener(e -> filterProducts());
        searchPanel.add(cmbCategoryFilter);

        panel.add(searchPanel, BorderLayout.NORTH);

        productModel = new DefaultTableModel(new String[] {
                "No.", "ID", "Name", "Category", "Price", "Stock" }, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }

            @Override
            public Class<?> getColumnClass(int column) {
                return Object.class;
            }
        };
        productTable = new JTable(productModel);
        productTable.setRowHeight(30);
        productTable.getTableHeader().setBackground(new java.awt.Color(0, 51, 0));
        productTable.getTableHeader().setForeground(Color.WHITE);
        productTable.getTableHeader().setFont(new Font("Segoe UI", Font.BOLD, 12));
        productTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);

        productTable.getColumnModel().getColumn(0).setPreferredWidth(40);

        productTable.getColumnModel().getColumn(1).setMinWidth(0);
        productTable.getColumnModel().getColumn(1).setMaxWidth(0);
        productTable.getColumnModel().getColumn(1).setWidth(0);

        loadProducts();

        productTable.addMouseListener(new MouseAdapter() {
            public void mouseClicked(MouseEvent e) {
                if (e.getClickCount() == 2) {
                    addToCart();
                }
            }
        });

        panel.add(new JScrollPane(productTable), BorderLayout.CENTER);

        JPanel addPanel = new JPanel(new FlowLayout());
        addPanel.add(new JLabel("Quantity:"));
        txtQuantity = new JTextField(5);
        txtQuantity.setText("1");
        addPanel.add(txtQuantity);

        JButton btnAddToCart = new JButton("Add to Cart");
        styleButton(btnAddToCart, new java.awt.Color(0, 51, 0), true);
        btnAddToCart.addActionListener(e -> addToCart());
        addPanel.add(btnAddToCart);

        panel.add(addPanel, BorderLayout.SOUTH);

        return panel;
    }

    private JPanel createCartPanel() {
        JPanel panel = new JPanel(new BorderLayout(10, 10));
        panel.setBorder(BorderFactory.createTitledBorder("Shopping Cart"));

        cartModel = new DefaultTableModel(new String[] {
                "No.", "Product", "Quantity", "Unit Price", "Subtotal" }, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        cartTable = new JTable(cartModel);
        cartTable.setRowHeight(30);
        cartTable.getTableHeader().setBackground(new java.awt.Color(0, 51, 0));
        cartTable.getTableHeader().setForeground(Color.WHITE);
        cartTable.getTableHeader().setFont(new Font("Segoe UI", Font.BOLD, 12));
        cartTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);

        cartTable.getColumnModel().getColumn(0).setPreferredWidth(40);

        panel.add(new JScrollPane(cartTable), BorderLayout.CENTER);

        JPanel actionPanel = new JPanel(new FlowLayout());
        JButton btnRemove = new JButton("Remove Item");
        styleDangerButton(btnRemove);
        btnRemove.addActionListener(e -> removeFromCart());
        actionPanel.add(btnRemove);

        JButton btnClearCart = new JButton("Clear Cart");
        styleButton(btnClearCart, new Color(180, 0, 0), true);
        btnClearCart.addActionListener(e -> clearCart());
        actionPanel.add(btnClearCart);

        panel.add(actionPanel, BorderLayout.SOUTH);

        return panel;
    }

    private JPanel createTotalsPanel() {
        JPanel panel = new JPanel(new BorderLayout(10, 10));
        panel.setBorder(BorderFactory.createTitledBorder("Transaction Summary"));
        panel.setPreferredSize(new Dimension(300, 0));

        JPanel totalsPanel = new JPanel(new GridBagLayout());
        totalsPanel.setBackground(new Color(245, 248, 250));
        totalsPanel.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(220, 220, 220)),
                BorderFactory.createEmptyBorder(15, 15, 15, 15)));

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.weightx = 0.4;
        totalsPanel.add(new JLabel("Subtotal:"), gbc);

        gbc.gridx = 1;
        gbc.weightx = 0.6;
        lblSubtotal = new JLabel("PHP 0.00", SwingConstants.RIGHT);
        lblSubtotal.setFont(new Font("Arial", Font.BOLD, 14));
        totalsPanel.add(lblSubtotal, gbc);

        gbc.gridx = 0;
        gbc.gridy = 1;
        gbc.weightx = 0.4;
        totalsPanel.add(new JLabel("Tax (%):"), gbc);

        gbc.gridx = 1;
        gbc.weightx = 0.6;
        txtTax = new JTextField("0", 10);
        txtTax.setHorizontalAlignment(JTextField.RIGHT);
        txtTax.addKeyListener(new KeyAdapter() {
            public void keyReleased(KeyEvent e) {
                calculateTotals();
            }
        });
        totalsPanel.add(txtTax, gbc);

        gbc.gridx = 0;
        gbc.gridy = 2;
        gbc.gridwidth = 2;
        gbc.insets = new Insets(10, 5, 10, 5);
        totalsPanel.add(new JSeparator(), gbc);

        gbc.gridx = 0;
        gbc.gridy = 3;
        gbc.gridwidth = 1;
        gbc.insets = new Insets(5, 5, 5, 5);
        JLabel lblTotalLabel = new JLabel("Total:");
        lblTotalLabel.setFont(new Font("Arial", Font.BOLD, 18));
        totalsPanel.add(lblTotalLabel, gbc);

        gbc.gridx = 1;
        lblTotal = new JLabel("PHP 0.00", SwingConstants.RIGHT);
        lblTotal.setFont(new Font("Arial", Font.BOLD, 22));
        lblTotal.setForeground(new Color(0, 100, 0));
        totalsPanel.add(lblTotal, gbc);

        gbc.gridx = 0;
        gbc.gridy = 4;
        gbc.gridwidth = 2;
        gbc.weighty = 1.0;
        totalsPanel.add(new JLabel(), gbc);

        panel.add(totalsPanel, BorderLayout.CENTER);

        JPanel buttonPanel = new JPanel(new GridLayout(3, 1, 5, 5));
        buttonPanel.setBorder(BorderFactory.createEmptyBorder(15, 10, 10, 10));

        JButton btnProcessSale = new JButton("Process Sale");
        styleButton(btnProcessSale, new Color(0, 51, 0), true);
        btnProcessSale.setPreferredSize(new Dimension(0, 35));
        btnProcessSale.addActionListener(e -> processSale());
        buttonPanel.add(btnProcessSale);

        JButton btnViewHistory = new JButton("View Sales History");
        styleButton(btnViewHistory, new Color(255, 193, 7), false);
        btnViewHistory.setForeground(Color.BLACK);
        btnViewHistory.setPreferredSize(new Dimension(0, 35));
        btnViewHistory.addActionListener(e -> viewSalesHistory());
        buttonPanel.add(btnViewHistory);

        JButton btnBack = new JButton("Back to Menu");
        btnBack.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        btnBack.setFocusPainted(false);
        btnBack.setPreferredSize(new Dimension(0, 35));
        btnBack.addActionListener(e -> {
            new MainMenuFrame().setVisible(true);
            this.dispose();
        });
        buttonPanel.add(btnBack);

        panel.add(buttonPanel, BorderLayout.SOUTH);

        return panel;
    }

    private void loadProducts() {
        productModel.setRowCount(0);
        List<Product> products = productDAO.getAllProducts();
        NumberFormat currency = NumberFormat.getCurrencyInstance(new Locale("en", "PH"));

        int rowNum = 1;
        for (Product p : products) {
            // User Request: Do not sell if stock reached threshold (Reserved Stock)
            int limit = p.getStockThreshold();
            if (p.getQuantity() <= limit) {
                continue;
            }

            Category cat = categoryDAO.getCategoryById(p.getCategoryId());

            productModel.addRow(new Object[] {
                    rowNum++,
                    p.getId(),
                    p.getName(),
                    cat != null ? cat.getName() : "N/A",
                    currency.format(p.getSellingPrice()),
                    p.getQuantity()
            });
        }
    }

    private void filterProducts() {
        productModel.setRowCount(0);
        String searchText = txtProductSearch.getText().trim().toLowerCase();
        Category selectedCat = (Category) cmbCategoryFilter.getSelectedItem();
        Integer categoryId = (selectedCat != null && selectedCat.getId() > 0) ? selectedCat.getId() : null;

        List<Product> products = productDAO.searchProducts(
                searchText.isEmpty() ? null : searchText, categoryId, null, null);

        NumberFormat currency = NumberFormat.getCurrencyInstance(new Locale("en", "PH"));

        int rowNum = 1;
        for (Product p : products) {
            int limit = p.getStockThreshold();
            if (p.getQuantity() <= limit)
                continue;

            Category cat = categoryDAO.getCategoryById(p.getCategoryId());

            productModel.addRow(new Object[] {
                    rowNum++,
                    p.getId(),
                    p.getName(),
                    cat != null ? cat.getName() : "N/A",
                    currency.format(p.getSellingPrice()),
                    p.getQuantity()
            });
        }
    }

    private void addToCart() {
        int row = productTable.getSelectedRow();
        if (row < 0) {
            JOptionPane.showMessageDialog(this, "Please select a product.", "No Selection",
                    JOptionPane.WARNING_MESSAGE);
            return;
        }

        int productId = (int) productModel.getValueAt(row, 1);
        String productName = productModel.getValueAt(row, 2).toString();
        int stock = (int) productModel.getValueAt(row, 5);

        int quantity;
        try {
            quantity = Integer.parseInt(txtQuantity.getText().trim());
            if (quantity <= 0) {
                JOptionPane.showMessageDialog(this, "Quantity must be greater than 0.", "Invalid Input",
                        JOptionPane.WARNING_MESSAGE);
                return;
            }
        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(this, "Please enter a valid quantity.", "Invalid Input",
                    JOptionPane.WARNING_MESSAGE);
            return;
        }

        if (quantity > stock) {
            JOptionPane.showMessageDialog(this,
                    "Insufficient stock! Available: " + stock, "Insufficient Stock",
                    JOptionPane.WARNING_MESSAGE);
            return;
        }

        Product product = productDAO.getProductById(productId);
        if (product == null) {
            JOptionPane.showMessageDialog(this, "Product not found.", "Error",
                    JOptionPane.ERROR_MESSAGE);
            return;
        }

        for (SaleItem item : cartItems) {
            if (item.getProductId() == productId) {
                int newQty = item.getQuantity() + quantity;
                if (newQty > stock) {
                    JOptionPane.showMessageDialog(this,
                            "Total quantity exceeds available stock!", "Insufficient Stock",
                            JOptionPane.WARNING_MESSAGE);
                    return;
                }
                item.setQuantity(newQty);
                updateCartDisplay();
                return;
            }
        }

        SaleItem item = new SaleItem();
        item.setProductId(productId);
        item.setProductName(productName);
        item.setQuantity(quantity);
        item.setUnitPrice(product.getSellingPrice());
        item.setSubtotal(quantity * product.getSellingPrice());
        cartItems.add(item);

        updateCartDisplay();
        txtQuantity.setText("1");
    }

    private void removeFromCart() {
        int row = cartTable.getSelectedRow();
        if (row < 0) {
            JOptionPane.showMessageDialog(this, "Please select an item to remove.", "No Selection",
                    JOptionPane.WARNING_MESSAGE);
            return;
        }

        cartItems.remove(row);
        updateCartDisplay();
    }

    private void clearCart() {
        if (cartItems.isEmpty()) {
            return;
        }

        int confirm = JOptionPane.showConfirmDialog(this,
                "Are you sure you want to clear the cart?", "Clear Cart",
                JOptionPane.YES_NO_OPTION);

        if (confirm == JOptionPane.YES_OPTION) {
            cartItems.clear();
            updateCartDisplay();
        }
    }

    private void updateCartDisplay() {
        cartModel.setRowCount(0);
        NumberFormat currency = NumberFormat.getCurrencyInstance(new Locale("en", "PH"));

        int rowNum = 1;
        for (SaleItem item : cartItems) {
            cartModel.addRow(new Object[] {
                    rowNum++,
                    item.getProductName(),
                    item.getQuantity(),
                    currency.format(item.getUnitPrice()),
                    currency.format(item.getSubtotal())
            });
        }

        calculateTotals();
    }

    private void calculateTotals() {
        double subtotal = 0;
        for (SaleItem item : cartItems) {
            subtotal += item.getSubtotal();
        }

        double taxPercent = 0;
        try {
            taxPercent = Double.parseDouble(txtTax.getText().trim());
        } catch (NumberFormatException e) {
            taxPercent = 0;
        }

        double tax = subtotal * taxPercent / 100.0;
        double total = subtotal + tax;

        NumberFormat currency = NumberFormat.getCurrencyInstance(new Locale("en", "PH"));
        lblSubtotal.setText(currency.format(subtotal));
        lblTotal.setText(currency.format(total));
    }

    private void processSale() {
        if (cartItems.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Cart is empty. Please add items.", "Empty Cart",
                    JOptionPane.WARNING_MESSAGE);
            return;
        }

        double taxPercent = 0;
        try {
            taxPercent = Double.parseDouble(txtTax.getText().trim());
            if (taxPercent < 0) {
                JOptionPane.showMessageDialog(this, "Tax percentage cannot be negative.", "Invalid Input",
                        JOptionPane.WARNING_MESSAGE);
                return;
            }
        } catch (NumberFormatException e) {
            taxPercent = 0;
        }

        int confirm = JOptionPane.showConfirmDialog(this,
                "Are you sure you want to process this transaction?",
                "Confirm Sale",
                JOptionPane.YES_NO_OPTION);

        if (confirm != JOptionPane.YES_OPTION) {
            return;
        }

        Sale sale = new Sale();
        sale.setSaleDate(LocalDateTime.now());
        sale.setItems(cartItems);
        sale.calculateTotal();
        sale.setTax(sale.getSubtotal() * taxPercent / 100.0);
        sale.setTotal(sale.getSubtotal() + sale.getTax());

        if (salesDAO.createSaleTransaction(sale)) {
            JOptionPane.showMessageDialog(this,
                    "Sale processed successfully!\nTotal: " +
                            NumberFormat.getCurrencyInstance(new Locale("en", "PH")).format(sale.getTotal()),
                    "Success", JOptionPane.INFORMATION_MESSAGE);

            cartItems.clear();
            updateCartDisplay();
            loadProducts();

        } else {
            JOptionPane.showMessageDialog(this,
                    "Failed to process sale. Please check stock availability.", "Error",
                    JOptionPane.ERROR_MESSAGE);
        }
    }

    private void viewSalesHistory() {
        new SalesHistoryFrame().setVisible(true);
    }

    private void styleButton(JButton btn, Color bgColor, boolean bold) {
        btn.setBackground(bgColor);
        btn.setForeground(Color.WHITE);
        btn.setFont(new Font("Segoe UI", bold ? Font.BOLD : Font.PLAIN, 12));
        btn.setFocusPainted(false);
    }

    private void styleDangerButton(JButton btn) {
        btn.setBackground(new Color(180, 0, 0));
        btn.setForeground(Color.WHITE);
        btn.setFont(new Font("Segoe UI", Font.BOLD, 12));
        btn.setFocusPainted(false);
    }

}
