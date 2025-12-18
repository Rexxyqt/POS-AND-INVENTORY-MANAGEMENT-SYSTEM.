package src.ui;

import src.dao.CategoryDAO;
import src.dao.UnitDAO;
import src.model.Category;
import src.model.Unit;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

public class CategoryManagementFrame extends JFrame {

    // FIELDS
    private JTabbedPane tabbedPane;

    // CONSTRUCTOR
    public CategoryManagementFrame() {
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
        tabbedPane.addTab("Categories", new CategoryPanel());
        tabbedPane.addTab("Units", new UnitPanel());

        add(tabbedPane, BorderLayout.CENTER);
        setVisible(true);
    }

    // METHODS
    private class CategoryPanel extends JPanel {

        // FIELDS
        private JTable table;
        private DefaultTableModel model;
        private JTextField txtName;
        private CategoryDAO categoryDAO = new CategoryDAO();
        private int selectedId = -1;

        // CONSTRUCTOR
        public CategoryPanel() {
            setLayout(new BorderLayout(10, 10));

            model = new DefaultTableModel(new String[] { "No.", "ID", "Name" }, 0) {
                @Override
                public boolean isCellEditable(int row, int column) {
                    return false;
                }
            };
            table = new JTable(model);
            table = new JTable(model);
            table.setRowHeight(30);
            table.getTableHeader().setBackground(new java.awt.Color(0, 51, 0));
            table.getTableHeader().setForeground(Color.WHITE);
            table.getTableHeader().setFont(new Font("Segoe UI", Font.BOLD, 12));

            table.getColumnModel().getColumn(1).setMinWidth(0);
            table.getColumnModel().getColumn(1).setMaxWidth(0);
            table.getColumnModel().getColumn(1).setWidth(0);

            table.getColumnModel().getColumn(0).setPreferredWidth(40);

            loadCategories();
            add(new JScrollPane(table), BorderLayout.CENTER);

            JPanel form = new JPanel(new GridBagLayout());
            form.setBorder(BorderFactory.createTitledBorder("Category Details"));
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

            btnAdd.addActionListener(e -> addCategory());
            btnUpdate.addActionListener(e -> updateCategory());
            btnDelete.addActionListener(e -> deleteCategory());
            btnClear.addActionListener(e -> clearForm());

            buttonPanel.add(btnAdd);
            buttonPanel.add(btnUpdate);
            buttonPanel.add(btnDelete);
            buttonPanel.add(btnClear);
            form.add(buttonPanel, gbc);

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
        private void loadCategories() {
            model.setRowCount(0);
            int rowNum = 1;
            for (Category c : categoryDAO.getAllCategories()) {
                model.addRow(new Object[] { rowNum++, c.getId(), c.getName() });
            }
        }

        private void clearForm() {
            selectedId = -1;
            txtName.setText("");
            table.clearSelection();
        }

        private void addCategory() {
            String name = txtName.getText().trim();
            if (name.isEmpty()) {
                JOptionPane.showMessageDialog(this, "Please enter category name.", "Validation Error",
                        JOptionPane.WARNING_MESSAGE);
                return;
            }

            if (categoryDAO.addCategory(new Category(0, name))) {
                JOptionPane.showMessageDialog(this, "Category added successfully!", "Success",
                        JOptionPane.INFORMATION_MESSAGE);
                clearForm();
                loadCategories();
            } else {
                JOptionPane.showMessageDialog(this, "Failed to add category. It may already exist.", "Error",
                        JOptionPane.ERROR_MESSAGE);
            }
        }

        private void updateCategory() {
            if (selectedId == -1) {
                JOptionPane.showMessageDialog(this, "Please select a category to update.", "No Selection",
                        JOptionPane.WARNING_MESSAGE);
                return;
            }

            String name = txtName.getText().trim();
            if (name.isEmpty()) {
                JOptionPane.showMessageDialog(this, "Please enter category name.", "Validation Error",
                        JOptionPane.WARNING_MESSAGE);
                return;
            }

            if (categoryDAO.updateCategory(new Category(selectedId, name))) {
                JOptionPane.showMessageDialog(this, "Category updated successfully!", "Success",
                        JOptionPane.INFORMATION_MESSAGE);
                clearForm();
                loadCategories();
            } else {
                JOptionPane.showMessageDialog(this, "Failed to update category.", "Error",
                        JOptionPane.ERROR_MESSAGE);
            }
        }

        private void deleteCategory() {
            int row = table.getSelectedRow();
            if (row < 0) {
                JOptionPane.showMessageDialog(this, "Please select a category to delete.", "No Selection",
                        JOptionPane.WARNING_MESSAGE);
                return;
            }

            int id = (int) model.getValueAt(row, 1);
            String name = model.getValueAt(row, 2).toString();

            if (categoryDAO.isCategoryInUse(id)) {
                JOptionPane.showMessageDialog(this,
                        "Cannot delete category '" + name + "'. It is still linked to one or more products.",
                        "Cannot Delete", JOptionPane.WARNING_MESSAGE);
                return;
            }

            int confirm = JOptionPane.showConfirmDialog(this,
                    "Are you sure you want to delete category: " + name + "?",
                    "Confirm Delete", JOptionPane.YES_NO_OPTION, JOptionPane.WARNING_MESSAGE);

            if (confirm == JOptionPane.YES_OPTION) {
                if (categoryDAO.deleteCategory(id)) {
                    JOptionPane.showMessageDialog(this, "Category deleted successfully!", "Success",
                            JOptionPane.INFORMATION_MESSAGE);
                    clearForm();
                    loadCategories();
                } else {
                    JOptionPane.showMessageDialog(this, "Failed to delete category.", "Error",
                            JOptionPane.ERROR_MESSAGE);
                }
            }
        }
    }

    private class UnitPanel extends JPanel {

        // FIELDS
        private JTable table;
        private DefaultTableModel model;
        private JTextField txtName;
        private UnitDAO unitDAO = new UnitDAO();
        private int selectedId = -1;

        // CONSTRUCTOR
        public UnitPanel() {
            setLayout(new BorderLayout(10, 10));

            model = new DefaultTableModel(new String[] { "No.", "ID", "Name" }, 0) {
                @Override
                public boolean isCellEditable(int row, int column) {
                    return false;
                }
            };
            table = new JTable(model);
            table = new JTable(model);
            table.setRowHeight(30);
            table.getTableHeader().setBackground(new java.awt.Color(0, 51, 0));
            table.getTableHeader().setForeground(Color.WHITE);
            table.getTableHeader().setFont(new Font("Segoe UI", Font.BOLD, 12));

            table.getColumnModel().getColumn(1).setMinWidth(0);
            table.getColumnModel().getColumn(1).setMaxWidth(0);
            table.getColumnModel().getColumn(1).setWidth(0);

            table.getColumnModel().getColumn(0).setPreferredWidth(40);

            loadUnits();
            add(new JScrollPane(table), BorderLayout.CENTER);

            JPanel form = new JPanel(new GridBagLayout());
            form.setBorder(BorderFactory.createTitledBorder("Unit Details"));
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

            btnAdd.addActionListener(e -> addUnit());
            btnUpdate.addActionListener(e -> updateUnit());
            btnDelete.addActionListener(e -> deleteUnit());
            btnClear.addActionListener(e -> clearForm());

            buttonPanel.add(btnAdd);
            buttonPanel.add(btnUpdate);
            buttonPanel.add(btnDelete);
            buttonPanel.add(btnClear);
            form.add(buttonPanel, gbc);

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
        private void loadUnits() {
            model.setRowCount(0);
            int rowNum = 1;
            for (Unit u : unitDAO.getAllUnits()) {
                model.addRow(new Object[] { rowNum++, u.getId(), u.getName() });
            }
        }

        private void clearForm() {
            selectedId = -1;
            txtName.setText("");
            table.clearSelection();
        }

        private void addUnit() {
            String name = txtName.getText().trim();
            if (name.isEmpty()) {
                JOptionPane.showMessageDialog(this, "Please enter unit name.", "Validation Error",
                        JOptionPane.WARNING_MESSAGE);
                return;
            }

            if (unitDAO.addUnit(new Unit(0, name))) {
                JOptionPane.showMessageDialog(this, "Unit added successfully!", "Success",
                        JOptionPane.INFORMATION_MESSAGE);
                clearForm();
                loadUnits();
            } else {
                JOptionPane.showMessageDialog(this, "Failed to add unit. It may already exist.", "Error",
                        JOptionPane.ERROR_MESSAGE);
            }
        }

        private void updateUnit() {
            if (selectedId == -1) {
                JOptionPane.showMessageDialog(this, "Please select a unit to update.", "No Selection",
                        JOptionPane.WARNING_MESSAGE);
                return;
            }

            String name = txtName.getText().trim();
            if (name.isEmpty()) {
                JOptionPane.showMessageDialog(this, "Please enter unit name.", "Validation Error",
                        JOptionPane.WARNING_MESSAGE);
                return;
            }

            if (unitDAO.updateUnit(new Unit(selectedId, name))) {
                JOptionPane.showMessageDialog(this, "Unit updated successfully!", "Success",
                        JOptionPane.INFORMATION_MESSAGE);
                clearForm();
                loadUnits();
            } else {
                JOptionPane.showMessageDialog(this, "Failed to update unit.", "Error",
                        JOptionPane.ERROR_MESSAGE);
            }
        }

        private void deleteUnit() {
            int row = table.getSelectedRow();
            if (row < 0) {
                JOptionPane.showMessageDialog(this, "Please select a unit to delete.", "No Selection",
                        JOptionPane.WARNING_MESSAGE);
                return;
            }

            int id = (int) model.getValueAt(row, 1);
            String name = model.getValueAt(row, 2).toString();

            if (unitDAO.isUnitInUse(name)) {
                JOptionPane.showMessageDialog(this,
                        "Cannot delete unit '" + name + "'. It is still linked to one or more products.",
                        "Cannot Delete", JOptionPane.WARNING_MESSAGE);
                return;
            }

            int confirm = JOptionPane.showConfirmDialog(this,
                    "Are you sure you want to delete unit: " + name + "?",
                    "Confirm Delete", JOptionPane.YES_NO_OPTION, JOptionPane.WARNING_MESSAGE);

            if (confirm == JOptionPane.YES_OPTION) {
                if (unitDAO.deleteUnit(id)) {
                    JOptionPane.showMessageDialog(this, "Unit deleted successfully!", "Success",
                            JOptionPane.INFORMATION_MESSAGE);
                    clearForm();
                    loadUnits();
                } else {
                    JOptionPane.showMessageDialog(this, "Failed to delete unit.", "Error",
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
}
