package src.ui;

import src.dao.BrandDAO;
import src.dao.SupplierDAO;
import src.model.Brand;
import src.model.Supplier;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

public class BrandSupplierManagementFrame extends JFrame {

    // FIELDS
    private JTabbedPane tabbedPane;

    // CONSTRUCTOR
    public BrandSupplierManagementFrame() {
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
        topPanel.setBackground(Color.WHITE);
        JButton btnBack = new JButton("← Back to Menu");
        styleButton(btnBack, new java.awt.Color(0, 51, 0), true);
        btnBack.addActionListener(e -> {
            new MainMenuFrame().setVisible(true);
            this.dispose();
        });
        topPanel.add(btnBack);
        add(topPanel, BorderLayout.NORTH);

        tabbedPane = new JTabbedPane();
        tabbedPane.addTab("Brands", new BrandPanel());
        tabbedPane.addTab("Suppliers", new SupplierPanel());

        add(tabbedPane, BorderLayout.CENTER);
        setVisible(true);
    }

    // METHODS
    private class BrandPanel extends JPanel {

        // FIELDS
        private JTable table;
        private DefaultTableModel model;
        private JTextField txtName;
        private BrandDAO brandDAO = new BrandDAO();
        private int selectedId = -1;

        // CONSTRUCTOR
        public BrandPanel() {
            setLayout(new BorderLayout(10, 10));

            model = new DefaultTableModel(new String[] { "No.", "ID", "Name" }, 0) {
                @Override
                public boolean isCellEditable(int row, int column) {
                    return false;
                }

                @Override
                public Class<?> getColumnClass(int column) {
                    return Object.class;
                }
            };
            table = new JTable(model);
            table = new JTable(model);
            table.setRowHeight(30);
            table.getTableHeader().setBackground(new java.awt.Color(0, 51, 0));
            table.getTableHeader().setForeground(Color.WHITE);
            table.getTableHeader().setFont(new Font("Segoe UI", Font.BOLD, 12));

            table.getColumnModel().getColumn(0).setMinWidth(30);
            table.getColumnModel().getColumn(0).setMaxWidth(30);
            table.getColumnModel().getColumn(0).setPreferredWidth(30);

            table.getColumnModel().getColumn(1).setMinWidth(0);
            table.getColumnModel().getColumn(1).setMaxWidth(0);
            table.getColumnModel().getColumn(1).setWidth(0);

            loadBrands();

            JPanel centerPanel = new JPanel(new BorderLayout(10, 10));
            centerPanel.add(new JScrollPane(table), BorderLayout.CENTER);

            JPanel form = new JPanel(new GridBagLayout());
            form.setBorder(BorderFactory.createTitledBorder("Brand Details"));
            GridBagConstraints gbc = new GridBagConstraints();
            gbc.insets = new Insets(5, 5, 5, 5);

            gbc.gridx = 0;
            gbc.gridy = 0;
            form.add(new JLabel("Name:"), gbc);
            gbc.gridx = 1;
            txtName = new JTextField(20);
            form.add(txtName, gbc);

            gbc.gridx = 0;
            gbc.gridy = 1;
            gbc.gridwidth = 2;
            JPanel buttonPanel = new JPanel(new FlowLayout());
            JButton btnAdd = new JButton("Add");
            JButton btnUpdate = new JButton("Update");
            JButton btnDelete = new JButton("Delete");
            JButton btnClear = new JButton("Clear");

            styleButton(btnAdd, new java.awt.Color(0, 51, 0), true);
            styleButton(btnUpdate, new java.awt.Color(0, 51, 0), false);
            styleDangerButton(btnDelete);
            styleButton(btnClear, new java.awt.Color(100, 100, 100), false);

            btnAdd.addActionListener(e -> addBrand());
            btnUpdate.addActionListener(e -> updateBrand());
            btnDelete.addActionListener(e -> deleteBrand());
            btnClear.addActionListener(e -> clearForm());

            buttonPanel.add(btnAdd);
            buttonPanel.add(btnUpdate);
            buttonPanel.add(btnDelete);
            buttonPanel.add(btnClear);

            JButton btnExport = new JButton("Export CSV");
            styleButton(btnExport, new java.awt.Color(0, 51, 0), true);
            btnExport.addActionListener(e -> exportTableToCSV(table, "Brand_List"));
            buttonPanel.add(btnExport);

            form.add(buttonPanel, gbc);

            add(centerPanel, BorderLayout.CENTER);
            add(form, BorderLayout.SOUTH);

            table.addMouseListener(new MouseAdapter() {
                public void mouseClicked(MouseEvent e) {
                    int row = table.getSelectedRow();
                    if (row >= 0) {
                        selectedId = (int) model.getValueAt(row, 1);
                        txtName.setText(model.getValueAt(row, 2).toString());
                    }
                }
            });
        }

        // METHODS
        private void loadBrands() {
            model.setRowCount(0);
            int rowNum = 1;
            for (Brand b : brandDAO.getAllBrands()) {
                model.addRow(new Object[] { rowNum++, b.getId(), b.getName() });
            }
        }

        private void clearForm() {
            selectedId = -1;
            txtName.setText("");
            table.clearSelection();
        }

        private void addBrand() {
            String name = txtName.getText().trim();
            if (name.isEmpty()) {
                JOptionPane.showMessageDialog(this, "Please enter brand name.", "Validation Error",
                        JOptionPane.WARNING_MESSAGE);
                return;
            }

            if (brandDAO.addBrand(new Brand(0, name))) {
                JOptionPane.showMessageDialog(this, "Brand added successfully!", "Success",
                        JOptionPane.INFORMATION_MESSAGE);
                clearForm();
                loadBrands();
            } else {
                JOptionPane.showMessageDialog(this, "Failed to add brand. It may already exist.", "Error",
                        JOptionPane.ERROR_MESSAGE);
            }
        }

        private void updateBrand() {
            if (selectedId == -1) {
                JOptionPane.showMessageDialog(this, "Please select a brand to update.", "No Selection",
                        JOptionPane.WARNING_MESSAGE);
                return;
            }

            String name = txtName.getText().trim();
            if (name.isEmpty()) {
                JOptionPane.showMessageDialog(this, "Please enter brand name.", "Validation Error",
                        JOptionPane.WARNING_MESSAGE);
                return;
            }

            if (brandDAO.updateBrand(new Brand(selectedId, name))) {
                JOptionPane.showMessageDialog(this, "Brand updated successfully!", "Success",
                        JOptionPane.INFORMATION_MESSAGE);
                clearForm();
                loadBrands();
            } else {
                JOptionPane.showMessageDialog(this, "Failed to update brand.", "Error",
                        JOptionPane.ERROR_MESSAGE);
            }
        }

        private void deleteBrand() {
            int row = table.getSelectedRow();
            if (row < 0) {
                JOptionPane.showMessageDialog(this, "Please select a brand to delete.", "No Selection",
                        JOptionPane.WARNING_MESSAGE);
                return;
            }

            int id = (int) model.getValueAt(row, 1);
            String name = model.getValueAt(row, 2).toString();

            if (brandDAO.isBrandInUse(id)) {
                JOptionPane.showMessageDialog(this,
                        "Cannot delete brand '" + name + "'. It is still linked to one or more products.",
                        "Cannot Delete", JOptionPane.WARNING_MESSAGE);
                return;
            }

            int confirm = JOptionPane.showConfirmDialog(this,
                    "Are you sure you want to delete brand: " + name + "?",
                    "Confirm Delete", JOptionPane.YES_NO_OPTION, JOptionPane.WARNING_MESSAGE);

            if (confirm == JOptionPane.YES_OPTION) {
                if (brandDAO.deleteBrand(id)) {
                    JOptionPane.showMessageDialog(this, "Brand deleted successfully!", "Success",
                            JOptionPane.INFORMATION_MESSAGE);
                    clearForm();
                    loadBrands();
                } else {
                    JOptionPane.showMessageDialog(this, "Failed to delete brand.", "Error",
                            JOptionPane.ERROR_MESSAGE);
                }
            }
        }

    }

    private class SupplierPanel extends JPanel {

        // FIELDS
        private JTable table;
        private DefaultTableModel model;
        private JTextField txtName;
        private SupplierDAO supplierDAO = new SupplierDAO();
        private int selectedId = -1;

        // CONSTRUCTOR
        public SupplierPanel() {
            setLayout(new BorderLayout(10, 10));

            model = new DefaultTableModel(new String[] { "No.", "ID", "Name" }, 0) {
                @Override
                public boolean isCellEditable(int row, int column) {
                    return false;
                }

                @Override
                public Class<?> getColumnClass(int column) {
                    return Object.class;
                }
            };
            table = new JTable(model);
            table = new JTable(model);
            table.setRowHeight(30);
            table.getTableHeader().setBackground(new java.awt.Color(0, 51, 0));
            table.getTableHeader().setForeground(Color.WHITE);
            table.getTableHeader().setFont(new Font("Segoe UI", Font.BOLD, 12));

            table.getColumnModel().getColumn(0).setMinWidth(30);
            table.getColumnModel().getColumn(0).setMaxWidth(30);
            table.getColumnModel().getColumn(0).setPreferredWidth(30);

            table.getColumnModel().getColumn(1).setMinWidth(0);
            table.getColumnModel().getColumn(1).setMaxWidth(0);
            table.getColumnModel().getColumn(1).setWidth(0);

            loadSuppliers();

            JPanel centerPanel = new JPanel(new BorderLayout(10, 10));
            centerPanel.add(new JScrollPane(table), BorderLayout.CENTER);

            JPanel form = new JPanel(new GridBagLayout());
            form.setBorder(BorderFactory.createTitledBorder("Supplier Details"));
            GridBagConstraints gbc = new GridBagConstraints();
            gbc.insets = new Insets(5, 5, 5, 5);

            gbc.gridx = 0;
            gbc.gridy = 0;
            form.add(new JLabel("Name:"), gbc);
            gbc.gridx = 1;
            txtName = new JTextField(20);
            form.add(txtName, gbc);

            gbc.gridx = 0;
            gbc.gridy = 1;
            gbc.gridwidth = 2;
            JPanel buttonPanel = new JPanel(new FlowLayout());
            JButton btnAdd = new JButton("Add");
            JButton btnUpdate = new JButton("Update");
            JButton btnDelete = new JButton("Delete");
            JButton btnClear = new JButton("Clear");

            styleButton(btnAdd, new java.awt.Color(0, 51, 0), true);
            styleButton(btnUpdate, new java.awt.Color(0, 51, 0), false);
            styleDangerButton(btnDelete);
            styleButton(btnClear, new java.awt.Color(100, 100, 100), false);

            btnAdd.addActionListener(e -> addSupplier());
            btnUpdate.addActionListener(e -> updateSupplier());
            btnDelete.addActionListener(e -> deleteSupplier());
            btnClear.addActionListener(e -> clearForm());

            JButton btnExport = new JButton("Export CSV");
            styleButton(btnExport, new java.awt.Color(0, 51, 0), true);
            btnExport.addActionListener(e -> exportTableToCSV(table, "Supplier_List"));

            buttonPanel.add(btnAdd);
            buttonPanel.add(btnUpdate);
            buttonPanel.add(btnDelete);
            buttonPanel.add(btnClear);
            buttonPanel.add(btnExport);
            form.add(buttonPanel, gbc);

            add(centerPanel, BorderLayout.CENTER);
            add(form, BorderLayout.SOUTH);

            table.addMouseListener(new MouseAdapter() {
                public void mouseClicked(MouseEvent e) {
                    int row = table.getSelectedRow();
                    if (row >= 0) {
                        selectedId = (int) model.getValueAt(row, 1);
                        txtName.setText(model.getValueAt(row, 2).toString());
                    }
                }
            });
        }

        // METHODS
        private void loadSuppliers() {
            model.setRowCount(0);
            int rowNum = 1;
            for (Supplier s : supplierDAO.getAllSuppliers()) {
                model.addRow(new Object[] { rowNum++, s.getId(), s.getName() });
            }
        }

        private void clearForm() {
            selectedId = -1;
            txtName.setText("");
            table.clearSelection();
        }

        private void addSupplier() {
            String name = txtName.getText().trim();
            if (name.isEmpty()) {
                JOptionPane.showMessageDialog(this, "Please enter supplier name.", "Validation Error",
                        JOptionPane.WARNING_MESSAGE);
                return;
            }

            if (supplierDAO.addSupplier(new Supplier(0, name))) {
                JOptionPane.showMessageDialog(this, "Supplier added successfully!", "Success",
                        JOptionPane.INFORMATION_MESSAGE);
                clearForm();
                loadSuppliers();
            } else {
                JOptionPane.showMessageDialog(this, "Failed to add supplier. It may already exist.", "Error",
                        JOptionPane.ERROR_MESSAGE);
            }
        }

        private void updateSupplier() {
            if (selectedId == -1) {
                JOptionPane.showMessageDialog(this, "Please select a supplier to update.", "No Selection",
                        JOptionPane.WARNING_MESSAGE);
                return;
            }

            String name = txtName.getText().trim();
            if (name.isEmpty()) {
                JOptionPane.showMessageDialog(this, "Please enter supplier name.", "Validation Error",
                        JOptionPane.WARNING_MESSAGE);
                return;
            }

            if (supplierDAO.updateSupplier(new Supplier(selectedId, name))) {
                JOptionPane.showMessageDialog(this, "Supplier updated successfully!", "Success",
                        JOptionPane.INFORMATION_MESSAGE);
                clearForm();
                loadSuppliers();
            } else {
                JOptionPane.showMessageDialog(this, "Failed to update supplier.", "Error",
                        JOptionPane.ERROR_MESSAGE);
            }
        }

        private void deleteSupplier() {
            int row = table.getSelectedRow();
            if (row < 0) {
                JOptionPane.showMessageDialog(this, "Please select a supplier to delete.", "No Selection",
                        JOptionPane.WARNING_MESSAGE);
                return;
            }

            int id = (int) model.getValueAt(row, 1);
            String name = model.getValueAt(row, 2).toString();

            if (supplierDAO.isSupplierInUse(id)) {
                JOptionPane.showMessageDialog(this,
                        "Cannot delete supplier '" + name + "'. It is still linked to one or more products.",
                        "Cannot Delete", JOptionPane.WARNING_MESSAGE);
                return;
            }

            int confirm = JOptionPane.showConfirmDialog(this,
                    "Are you sure you want to delete supplier: " + name + "?",
                    "Confirm Delete", JOptionPane.YES_NO_OPTION, JOptionPane.WARNING_MESSAGE);

            if (confirm == JOptionPane.YES_OPTION) {
                if (supplierDAO.deleteSupplier(id)) {
                    JOptionPane.showMessageDialog(this, "Supplier deleted successfully!", "Success",
                            JOptionPane.INFORMATION_MESSAGE);
                    clearForm();
                    loadSuppliers();
                } else {
                    JOptionPane.showMessageDialog(this, "Failed to delete supplier.", "Error",
                            JOptionPane.ERROR_MESSAGE);
                }
            }
        }

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

    private java.io.File getUniqueFile(java.io.File directory, String baseName, String extension) {
        java.io.File file = new java.io.File(directory, baseName + extension);
        int count = 1;
        while (file.exists()) {
            file = new java.io.File(directory, baseName + "(" + count + ")" + extension);
            count++;
        }
        return file;
    }

    private void exportTableToCSV(JTable table, String defaultFileName) {
        JFileChooser fileChooser = new JFileChooser();
        fileChooser.setDialogTitle("Save as CSV");

        java.io.File currentDir = fileChooser.getCurrentDirectory();
        java.io.File uniqueFile = getUniqueFile(currentDir, defaultFileName, ".csv");
        fileChooser.setSelectedFile(uniqueFile);

        int userSelection = fileChooser.showSaveDialog(this);
        if (userSelection == JFileChooser.APPROVE_OPTION) {
            java.io.File fileToSave = fileChooser.getSelectedFile();
            try (java.io.PrintWriter writer = new java.io.PrintWriter(fileToSave)) {

                boolean firstCol = true;
                for (int i = 0; i < table.getColumnCount(); i++) {
                    if (table.getColumnModel().getColumn(i).getWidth() > 0) {
                        if (!firstCol)
                            writer.print(",");
                        writer.print(table.getColumnName(i));
                        firstCol = false;
                    }
                }
                writer.println();

                for (int row = 0; row < table.getRowCount(); row++) {
                    firstCol = true;
                    for (int col = 0; col < table.getColumnCount(); col++) {
                        if (table.getColumnModel().getColumn(col).getWidth() > 0) {
                            if (!firstCol)
                                writer.print(",");
                            Object value = table.getValueAt(row, col);
                            String text = (value != null) ? value.toString().replace(",", ";") : "";
                            writer.print(text);
                            firstCol = false;
                        }
                    }
                    writer.println();
                }

                JOptionPane.showMessageDialog(this, "List exported successfully!", "Success",
                        JOptionPane.INFORMATION_MESSAGE);
            } catch (java.io.IOException e) {
                JOptionPane.showMessageDialog(this, "Error exporting list: " + e.getMessage(),
                        "Error", JOptionPane.ERROR_MESSAGE);
            }
        }
    }
}
