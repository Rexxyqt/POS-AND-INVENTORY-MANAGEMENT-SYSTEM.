package src.ui;

import src.dao.BrandDAO;
import src.dao.CategoryDAO;
import src.dao.ProductDAO;
import src.dao.SupplierDAO;
import src.dao.UnitDAO;
import src.model.Brand;
import src.model.Category;
import src.model.Product;
import src.model.Supplier;
import src.model.Unit;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.*;

import java.text.NumberFormat;
import java.time.LocalDate;
import java.util.List;
import java.util.Locale;

public class ProductFrame extends JFrame {

    // FIELDS
    private JTable table;
    private DefaultTableModel model;
    private JTextField txtName, txtCostPrice, txtMarkup, txtQty, txtThreshold;
    private JTextField txtSellingPrice;
    private JComboBox<Category> cmbCategory;
    private JComboBox<Brand> cmbBrand;
    private JComboBox<Supplier> cmbSupplier;
    private JComboBox<String> cmbUnit;
    private JLabel lblDate;
    private JTextField txtSearchName;
    private JComboBox<Category> cmbSearchCategory;
    private JComboBox<Brand> cmbSearchBrand;
    private JComboBox<Supplier> cmbSearchSupplier;
    private JButton btnAdd, btnUpdate, btnDelete;
    private ProductDAO productDAO = new ProductDAO();
    private CategoryDAO categoryDAO = new CategoryDAO();
    private BrandDAO brandDAO = new BrandDAO();
    private SupplierDAO supplierDAO = new SupplierDAO();
    private UnitDAO unitDAO = new UnitDAO();

    // CONSTRUCTOR
    public ProductFrame() {
        setTitle("RJ HARDWARE AND ELECTRONICS");
        setExtendedState(JFrame.MAXIMIZED_BOTH);
        setDefaultCloseOperation(DISPOSE_ON_CLOSE);
        setLayout(new BorderLayout(10, 10));
        getContentPane().setBackground(Color.WHITE);

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

        JPanel searchPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        searchPanel.setBackground(Color.WHITE);
        searchPanel.setBorder(BorderFactory.createTitledBorder("Search Products"));
        searchPanel.add(new JLabel("Search Name:"));
        txtSearchName = new JTextField(10);
        searchPanel.add(txtSearchName);

        searchPanel.add(new JLabel("Category:"));
        cmbSearchCategory = new JComboBox<>();
        cmbSearchCategory.addItem(null);
        searchPanel.add(cmbSearchCategory);

        searchPanel.add(new JLabel("Brand:"));
        cmbSearchBrand = new JComboBox<>();
        cmbSearchBrand.addItem(null);
        searchPanel.add(cmbSearchBrand);

        searchPanel.add(new JLabel("Supplier:"));
        cmbSearchSupplier = new JComboBox<>();
        cmbSearchSupplier.addItem(null);
        searchPanel.add(cmbSearchSupplier);

        JButton btnSearch = new JButton("Search");
        styleButton(btnSearch, new java.awt.Color(0, 51, 0), true);
        searchPanel.add(btnSearch);

        JButton btnClearSearch = new JButton("Clear");
        styleButton(btnClearSearch, new Color(180, 0, 0), false);
        searchPanel.add(btnClearSearch);

        JPanel northPanel = new JPanel(new BorderLayout());
        northPanel.add(topPanel, BorderLayout.NORTH);
        northPanel.add(searchPanel, BorderLayout.SOUTH);
        add(northPanel, BorderLayout.NORTH);

        model = new DefaultTableModel(new String[] {
                "No.", "ID", "Name", "Category", "Brand", "Supplier",
                "Unit", "Cost Price", "Markup %", "Selling Price", "Quantity", "Threshold", "Date Added" }, 0) {
            @Override
            public Class<?> getColumnClass(int column) {
                return Object.class;
            }
        };

        table = new JTable(model);
        table.setRowHeight(30);
        table.getTableHeader().setBackground(new java.awt.Color(0, 51, 0));
        table.getTableHeader().setForeground(Color.WHITE);
        table.getTableHeader().setFont(new Font("Segoe UI", Font.BOLD, 12));

        table.getColumnModel().getColumn(1).setMinWidth(0);
        table.getColumnModel().getColumn(1).setMaxWidth(0);
        table.getColumnModel().getColumn(1).setWidth(0);

        table.getColumnModel().getColumn(0).setPreferredWidth(40);

        loadProducts();

        JPanel centerPanel = new JPanel(new BorderLayout(10, 10));
        centerPanel.add(new JScrollPane(table), BorderLayout.CENTER);

        add(centerPanel, BorderLayout.CENTER);

        JPanel form = new JPanel(new GridLayout(7, 4, 5, 5));
        form.setBackground(Color.WHITE);

        form.add(styledLabel("Name:"));
        txtName = new JTextField();
        form.add(txtName);

        form.add(styledLabel("Category:"));
        cmbCategory = new JComboBox<>();
        JButton btnAddCategory = new JButton("+");
        styleButton(btnAddCategory, new java.awt.Color(0, 51, 0), true);
        JPanel catPanel = new JPanel(new BorderLayout());
        catPanel.add(cmbCategory, BorderLayout.CENTER);
        catPanel.add(btnAddCategory, BorderLayout.EAST);
        form.add(catPanel);

        form.add(styledLabel("Brand:"));
        cmbBrand = new JComboBox<>();
        JButton btnAddBrand = new JButton("+");
        styleButton(btnAddBrand, new java.awt.Color(0, 51, 0), true);
        JPanel brandPanel = new JPanel(new BorderLayout());
        brandPanel.add(cmbBrand, BorderLayout.CENTER);
        brandPanel.add(btnAddBrand, BorderLayout.EAST);
        form.add(brandPanel);

        form.add(styledLabel("Supplier:"));
        cmbSupplier = new JComboBox<>();
        JButton btnAddSupplier = new JButton("+");
        styleButton(btnAddSupplier, new java.awt.Color(0, 51, 0), true);
        JPanel supPanel = new JPanel(new BorderLayout());
        supPanel.add(cmbSupplier, BorderLayout.CENTER);
        supPanel.add(btnAddSupplier, BorderLayout.EAST);
        form.add(supPanel);

        form.add(styledLabel("Unit:"));
        cmbUnit = new JComboBox<>();
        JButton btnAddUnit = new JButton("+");
        styleButton(btnAddUnit, new java.awt.Color(0, 51, 0), true);
        JPanel unitPanel = new JPanel(new BorderLayout());
        unitPanel.add(cmbUnit, BorderLayout.CENTER);
        unitPanel.add(btnAddUnit, BorderLayout.EAST);
        form.add(unitPanel);

        form.add(styledLabel("Cost Price (PHP):"));
        txtCostPrice = new JTextField();
        form.add(txtCostPrice);

        form.add(styledLabel("Markup (%):"));
        txtMarkup = new JTextField();
        form.add(txtMarkup);

        form.add(styledLabel("Selling Price (PHP):"));
        txtSellingPrice = new JTextField();
        txtSellingPrice.setEditable(false);
        form.add(txtSellingPrice);

        form.add(styledLabel("Quantity:"));
        txtQty = new JTextField();
        form.add(txtQty);

        form.add(styledLabel("Threshold:"));
        txtThreshold = new JTextField("10"); // Default
        form.add(txtThreshold);

        lblDate = new JLabel(LocalDate.now().toString());

        btnAdd = new JButton("Add");
        btnUpdate = new JButton("Update");
        btnDelete = new JButton("Delete");

        styleButton(btnAdd, new java.awt.Color(0, 51, 0), true);
        styleButton(btnUpdate, new java.awt.Color(0, 51, 0), false);

        styleDangerButton(btnDelete);

        form.add(btnAdd);
        form.add(btnUpdate);
        form.add(btnDelete);

        add(form, BorderLayout.SOUTH);

        loadDropdowns();

        txtCostPrice.getDocument().addDocumentListener(new javax.swing.event.DocumentListener() {
            public void changedUpdate(javax.swing.event.DocumentEvent e) {
                updateSellingPrice();
            }

            public void removeUpdate(javax.swing.event.DocumentEvent e) {
                updateSellingPrice();
            }

            public void insertUpdate(javax.swing.event.DocumentEvent e) {
                updateSellingPrice();
            }
        });

        txtMarkup.getDocument().addDocumentListener(new javax.swing.event.DocumentListener() {
            public void changedUpdate(javax.swing.event.DocumentEvent e) {
                updateSellingPrice();
            }

            public void removeUpdate(javax.swing.event.DocumentEvent e) {
                updateSellingPrice();
            }

            public void insertUpdate(javax.swing.event.DocumentEvent e) {
                updateSellingPrice();
            }
        });

        btnAddCategory.addActionListener(e -> addNewCategory());
        btnAddBrand.addActionListener(e -> addNewBrand());
        btnAddSupplier.addActionListener(e -> addNewSupplier());
        btnAddUnit.addActionListener(e -> addNewUnit());

        btnSearch.addActionListener(e -> searchProducts());
        btnClearSearch.addActionListener(e -> clearSearch());

        btnAdd.addActionListener(e -> addProduct());
        btnUpdate.addActionListener(e -> updateProduct());
        btnDelete.addActionListener(e -> deleteProduct());

        table.addMouseListener(new MouseAdapter() {
            public void mouseClicked(MouseEvent e) {
                int row = table.getSelectedRow();
                if (row >= 0) {
                    txtName.setText(model.getValueAt(row, 2).toString());
                    cmbCategory.setSelectedItem(model.getValueAt(row, 3));
                    cmbBrand.setSelectedItem(model.getValueAt(row, 4));
                    cmbSupplier.setSelectedItem(model.getValueAt(row, 5));

                    cmbUnit.setSelectedItem(model.getValueAt(row, 6).toString());
                    txtCostPrice.setText(model.getValueAt(row, 7).toString().replace("PHP ", "").replace(",", ""));
                    txtMarkup.setText(model.getValueAt(row, 8).toString().replace("%", ""));
                    txtSellingPrice.setText(model.getValueAt(row, 9).toString().replace("PHP ", "").replace(",", ""));
                    txtQty.setText(model.getValueAt(row, 10).toString());
                    txtThreshold.setText(model.getValueAt(row, 11).toString());
                    lblDate.setText(model.getValueAt(row, 12).toString());

                    txtQty.setEditable(false);
                    txtQty.setBackground(Color.LIGHT_GRAY);
                    txtQty.setToolTipText(
                            "Stock quantity cannot be edited directly. Use Inventory Management to adjust stock.");
                }
            }
        });

        setVisible(true);
    }

    // METHODS
    private void loadProducts() {
        loadProducts(productDAO.getAllProducts());
    }

    private void loadProducts(List<Product> products) {
        model.setRowCount(0);
        NumberFormat currency = NumberFormat.getCurrencyInstance(new Locale("en", "PH"));

        int rowNum = 1;

        for (Product p : products) {
            Category cat = categoryDAO.getCategoryById(p.getCategoryId());
            Brand brand = brandDAO.getBrandById(p.getBrandId());
            Supplier sup = supplierDAO.getSupplierById(p.getSupplierId());

            model.addRow(new Object[] {
                    rowNum++,
                    p.getId(),
                    p.getName(),
                    cat,
                    brand,
                    sup,
                    p.getUnit(),
                    currency.format(p.getCostPrice()),
                    String.format("%.2f%%", p.getMarkupPercentage()),
                    currency.format(p.getSellingPrice()),
                    p.getQuantity(),
                    p.getStockThreshold(),
                    p.getDateAdded()
            });
        }
    }

    private void loadDropdowns() {
        cmbCategory.removeAllItems();
        for (Category c : categoryDAO.getAllCategories())
            cmbCategory.addItem(c);

        cmbBrand.removeAllItems();
        for (Brand b : brandDAO.getAllBrands())
            cmbBrand.addItem(b);

        cmbSupplier.removeAllItems();
        for (Supplier s : supplierDAO.getAllSuppliers())
            cmbSupplier.addItem(s);

        loadUnits();

        cmbSearchCategory.removeAllItems();
        cmbSearchCategory.addItem(null);
        for (Category c : categoryDAO.getAllCategories())
            cmbSearchCategory.addItem(c);

        cmbSearchBrand.removeAllItems();
        cmbSearchBrand.addItem(null);
        for (Brand b : brandDAO.getAllBrands())
            cmbSearchBrand.addItem(b);

        cmbSearchSupplier.removeAllItems();
        cmbSearchSupplier.addItem(null);
        for (Supplier s : supplierDAO.getAllSuppliers())
            cmbSearchSupplier.addItem(s);
    }

    private void loadUnits() {
        cmbUnit.removeAllItems();
        List<Unit> units = unitDAO.getAllUnits();
        for (Unit unit : units) {
            cmbUnit.addItem(unit.getName());
        }
    }

    private void addProduct() {
        if (!validateInputs())
            return;

        lockButtonsExcept(btnAdd);

        int confirm = JOptionPane.showConfirmDialog(this,
                "Are you sure you want to add this product?", "Confirm Add",
                JOptionPane.YES_NO_OPTION);

        if (confirm == JOptionPane.YES_OPTION) {
            try {
                Product p = new Product();
                p.setName(txtName.getText().trim());
                p.setCategoryId(((Category) cmbCategory.getSelectedItem()).getId());
                p.setBrandId(((Brand) cmbBrand.getSelectedItem()).getId());
                p.setSupplierId(((Supplier) cmbSupplier.getSelectedItem()).getId());
                p.setUnit((String) cmbUnit.getSelectedItem());
                p.setCostPrice(Double.parseDouble(txtCostPrice.getText()));
                p.setMarkupPercentage(Double.parseDouble(txtMarkup.getText()));
                p.setQuantity(Integer.parseInt(txtQty.getText()));
                p.setStockThreshold(Integer.parseInt(txtThreshold.getText()));
                p.setDateAdded(LocalDate.now());

                if (productDAO.addProduct(p)) {
                    JOptionPane.showMessageDialog(this, "Product added!");
                    loadProducts();
                    clearForm();
                }
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(this, "Error: " + ex.getMessage());
            } finally {
                unlockButtons();
            }
        } else {
            unlockButtons();
        }
    }

    private void updateProduct() {
        int row = table.getSelectedRow();
        if (row >= 0) {
            if (!validateInputs())
                return;

            lockButtonsExcept(btnUpdate);

            int confirm = JOptionPane.showConfirmDialog(this,
                    "Are you sure you want to update this product?", "Confirm Update",
                    JOptionPane.YES_NO_OPTION);

            if (confirm == JOptionPane.YES_OPTION) {
                try {
                    Product p = new Product();
                    p.setId((int) model.getValueAt(row, 1));
                    p.setName(txtName.getText().trim());
                    p.setCategoryId(((Category) cmbCategory.getSelectedItem()).getId());
                    p.setBrandId(((Brand) cmbBrand.getSelectedItem()).getId());
                    p.setSupplierId(((Supplier) cmbSupplier.getSelectedItem()).getId());
                    p.setUnit((String) cmbUnit.getSelectedItem());
                    p.setCostPrice(Double.parseDouble(txtCostPrice.getText()));
                    p.setMarkupPercentage(Double.parseDouble(txtMarkup.getText()));
                    p.setQuantity(Integer.parseInt(txtQty.getText()));
                    p.setStockThreshold(Integer.parseInt(txtThreshold.getText()));
                    p.setDateAdded(LocalDate.parse(lblDate.getText()));

                    if (productDAO.updateProduct(p)) {
                        JOptionPane.showMessageDialog(this, "Product updated!");
                        loadProducts();
                    }
                } catch (Exception ex) {
                    JOptionPane.showMessageDialog(this, "Error: " + ex.getMessage());
                } finally {
                    unlockButtons();
                }
            } else {
                unlockButtons();
            }
        } else {
            JOptionPane.showMessageDialog(this, "Please select a product to update.");
        }
    }

    private void deleteProduct() {
        int row = table.getSelectedRow();
        if (row >= 0) {
            String productName = model.getValueAt(row, 2).toString();

            lockButtonsExcept(btnDelete);

            int confirm = JOptionPane.showConfirmDialog(this,
                    "Are you sure you want to delete the product '" + productName + "'?\n\n" +
                            "This action cannot be undone and will also delete all related sales history.",
                    "Confirm Product Deletion",
                    JOptionPane.YES_NO_OPTION,
                    JOptionPane.WARNING_MESSAGE);

            if (confirm == JOptionPane.YES_OPTION) {
                try {
                    int id = (int) model.getValueAt(row, 1);
                    if (productDAO.deleteProduct(id)) {
                        JOptionPane.showMessageDialog(this, "Product deleted successfully!");
                        loadProducts();
                        clearForm();
                    } else {
                        JOptionPane.showMessageDialog(this, "Error deleting product!", "Error",
                                JOptionPane.ERROR_MESSAGE);
                    }
                } finally {
                    unlockButtons();
                }
            } else {
                unlockButtons();
            }
        } else {
            JOptionPane.showMessageDialog(this, "Please select a product to delete.");
        }
    }

    private void lockButtonsExcept(JButton activeButton) {
        if (activeButton != btnAdd) {
            btnAdd.setEnabled(false);
            btnAdd.setBackground(Color.GRAY);
        }
        if (activeButton != btnUpdate) {
            btnUpdate.setEnabled(false);
            btnUpdate.setBackground(Color.GRAY);
        }
        if (activeButton != btnDelete) {
            btnDelete.setEnabled(false);
            btnDelete.setBackground(Color.GRAY);
        }
    }

    private void unlockButtons() {
        btnAdd.setEnabled(true);
        btnUpdate.setEnabled(true);
        btnDelete.setEnabled(true);

        btnAdd.setBackground(new java.awt.Color(0, 51, 0));
        btnUpdate.setBackground(new java.awt.Color(0, 51, 0));
        btnDelete.setBackground(new Color(180, 0, 0));
    }

    private void addNewCategory() {
        String name = JOptionPane.showInputDialog(this, "Enter new category:");
        if (name != null && !name.isBlank()) {
            if (categoryDAO.addCategory(new Category(0, name))) {
                JOptionPane.showMessageDialog(this, "Category added!");
                loadDropdowns();
            }
        }
    }

    private void addNewBrand() {
        String name = JOptionPane.showInputDialog(this, "Enter new brand:");
        if (name != null && !name.isBlank()) {
            if (brandDAO.addBrand(new Brand(0, name))) {
                JOptionPane.showMessageDialog(this, "Brand added!");
                loadDropdowns();
            }
        }
    }

    private void addNewSupplier() {
        String name = JOptionPane.showInputDialog(this, "Enter new supplier:");
        if (name != null && !name.isBlank()) {
            if (supplierDAO.addSupplier(new Supplier(0, name))) {
                JOptionPane.showMessageDialog(this, "Supplier added!");
                loadDropdowns();
            }
        }
    }

    private void addNewUnit() {
        String newUnit = JOptionPane.showInputDialog(this, "Enter new unit:");
        if (newUnit != null && !newUnit.trim().isEmpty()) {
            if (unitDAO.getUnitByName(newUnit.trim()) != null) {
                JOptionPane.showMessageDialog(this, "Unit already exists!");
                return;
            }

            Unit unit = new Unit(0, newUnit.trim());
            if (unitDAO.addUnit(unit)) {
                JOptionPane.showMessageDialog(this, "Unit added!");
                loadUnits();
            } else {
                JOptionPane.showMessageDialog(this, "Error adding unit!");
            }
        }
    }

    private void updateSellingPrice() {
        try {
            String costText = txtCostPrice.getText().trim();
            String markupText = txtMarkup.getText().trim();

            if (!costText.isEmpty() && !markupText.isEmpty()) {
                double costPrice = Double.parseDouble(costText);
                double markupPercentage = Double.parseDouble(markupText);
                double sellingPrice = costPrice + (costPrice * markupPercentage / 100.0);
                txtSellingPrice.setText(String.format("%.2f", sellingPrice));
            } else {
                txtSellingPrice.setText("");
            }
        } catch (NumberFormatException e) {
            txtSellingPrice.setText("");
        }
    }

    private void searchProducts() {
        String name = txtSearchName.getText().trim();
        Integer categoryId = cmbSearchCategory.getSelectedItem() != null
                ? ((Category) cmbSearchCategory.getSelectedItem()).getId()
                : null;
        Integer brandId = cmbSearchBrand.getSelectedItem() != null ? ((Brand) cmbSearchBrand.getSelectedItem()).getId()
                : null;
        Integer supplierId = cmbSearchSupplier.getSelectedItem() != null
                ? ((Supplier) cmbSearchSupplier.getSelectedItem()).getId()
                : null;

        List<Product> results = productDAO.searchProducts(name, categoryId, brandId, supplierId);
        loadProducts(results);
    }

    private void clearSearch() {
        txtSearchName.setText("");
        cmbSearchCategory.setSelectedItem(null);
        cmbSearchBrand.setSelectedItem(null);
        cmbSearchSupplier.setSelectedItem(null);
        loadProducts();
    }

    private boolean validateInputs() {
        if (txtName.getText().trim().isEmpty()) {
            JOptionPane.showMessageDialog(this, "Product name is required!");
            txtName.requestFocus();
            return false;
        }
        if (cmbCategory.getSelectedItem() == null) {
            JOptionPane.showMessageDialog(this, "Please select a category!");
            cmbCategory.requestFocus();
            return false;
        }
        if (cmbBrand.getSelectedItem() == null) {
            JOptionPane.showMessageDialog(this, "Please select a brand!");
            cmbBrand.requestFocus();
            return false;
        }
        if (cmbSupplier.getSelectedItem() == null) {
            JOptionPane.showMessageDialog(this, "Please select a supplier!");
            cmbSupplier.requestFocus();
            return false;
        }
        if (cmbUnit.getSelectedItem() == null) {
            JOptionPane.showMessageDialog(this, "Please select a unit!");
            cmbUnit.requestFocus();
            return false;
        }
        try {
            Double.parseDouble(txtCostPrice.getText().trim());
        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(this, "Invalid cost price!");
            txtCostPrice.requestFocus();
            return false;
        }
        try {
            Double.parseDouble(txtMarkup.getText().trim());
        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(this, "Invalid markup percentage!");
            txtMarkup.requestFocus();
            return false;
        }
        try {
            Integer.parseInt(txtQty.getText().trim());
        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(this, "Invalid quantity!");
            txtQty.requestFocus();
            return false;
        }
        try {
            Integer.parseInt(txtThreshold.getText().trim());
        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(this, "Invalid threshold!");
            txtThreshold.requestFocus();
            return false;
        }
        return true;
    }

    private void clearForm() {
        txtName.setText("");
        cmbCategory.setSelectedIndex(0);
        cmbBrand.setSelectedIndex(0);
        cmbSupplier.setSelectedIndex(0);
        cmbUnit.setSelectedIndex(0);
        txtCostPrice.setText("");
        txtMarkup.setText("");
        txtSellingPrice.setText("");
        txtSellingPrice.setText("");
        txtQty.setText("");
        txtThreshold.setText("10");
        lblDate.setText(LocalDate.now().toString());

        txtQty.setEditable(true);
        txtQty.setBackground(Color.WHITE);
        txtQty.setToolTipText(null);
    }

    private JLabel styledLabel(String text) {
        JLabel lbl = new JLabel(text);
        lbl.setForeground(Color.BLACK);
        lbl.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        return lbl;
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
