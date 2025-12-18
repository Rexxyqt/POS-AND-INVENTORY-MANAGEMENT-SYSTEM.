package src.ui;

import src.dao.*;
import src.model.*;
import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.io.*;
import java.text.NumberFormat;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Locale;

public class ReportsFrame extends JFrame {

    // FIELDS
    private final ProductDAO productDAO = new ProductDAO();
    private final SalesDAO salesDAO = new SalesDAO();
    private final InventoryDAO inventoryDAO = new InventoryDAO();
    private final CategoryDAO categoryDAO = new CategoryDAO();
    private final BrandDAO brandDAO = new BrandDAO();
    private final SupplierDAO supplierDAO = new SupplierDAO();
    private JTabbedPane tabbedPane;
    private JTable productTable;
    private DefaultTableModel productTableModel;
    private JTable inventoryTable;
    private DefaultTableModel inventoryTableModel;
    private JTextArea salesReportArea;
    private LocalDateTime lastReportStart;
    private LocalDateTime lastReportEnd;
    private String lastReportType;

    // CONSTRUCTOR
    public ReportsFrame() {
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

        tabbedPane = new JTabbedPane();

        tabbedPane.addTab("Product Report", createProductReportPanel());

        tabbedPane.addTab("Sales Report", createSalesReportPanel());

        tabbedPane.addTab("Inventory Activity Report", createInventoryReportPanel());

        add(tabbedPane, BorderLayout.CENTER);
        setVisible(true);
    }

    // METHODS
    private JPanel createProductReportPanel() {
        JPanel panel = new JPanel(new BorderLayout(10, 10));

        productTableModel = new DefaultTableModel(new String[] {
                "No.", "ID", "Name", "Category", "Brand", "Supplier", "Unit",
                "Cost Price", "Markup %", "Selling Price", "Stock Qty" }, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        productTable = new JTable(productTableModel);
        productTable.setRowHeight(30);
        productTable.getTableHeader().setBackground(new java.awt.Color(0, 51, 0));
        productTable.getTableHeader().setForeground(Color.WHITE);
        productTable.getTableHeader().setFont(new Font("Segoe UI", Font.BOLD, 12));
        productTable.setFont(new Font("Segoe UI", Font.PLAIN, 13));

        productTable.getColumnModel().getColumn(1).setMinWidth(0);
        productTable.getColumnModel().getColumn(1).setMaxWidth(0);
        productTable.getColumnModel().getColumn(1).setWidth(0);

        productTable.getColumnModel().getColumn(0).setPreferredWidth(40);

        loadProductReport();

        panel.add(new JScrollPane(productTable), BorderLayout.CENTER);

        JPanel buttonPanel = new JPanel(new FlowLayout());
        JButton btnRefresh = new JButton("Refresh");
        styleButton(btnRefresh, new java.awt.Color(0, 51, 0), false);
        btnRefresh.addActionListener(e -> loadProductReport());
        buttonPanel.add(btnRefresh);

        JButton btnExportCSV = new JButton("Export as CSV");
        styleButton(btnExportCSV, new java.awt.Color(0, 51, 0), true);
        btnExportCSV.addActionListener(e -> exportTableToCSV(productTable, "Product_Report"));
        buttonPanel.add(btnExportCSV);

        panel.add(buttonPanel, BorderLayout.SOUTH);

        return panel;
    }

    private JPanel createSalesReportPanel() {
        JPanel panel = new JPanel(new BorderLayout(10, 10));

        JPanel datePanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 10));
        datePanel.setBorder(BorderFactory.createTitledBorder("Select Date Range"));

        datePanel.add(new JLabel("From:"));
        JSpinner spinnerFrom = new JSpinner(new SpinnerDateModel());
        JSpinner.DateEditor editorFrom = new JSpinner.DateEditor(spinnerFrom, "yyyy-MM-dd");
        spinnerFrom.setEditor(editorFrom);
        spinnerFrom.setValue(java.util.Calendar.getInstance().getTime());
        datePanel.add(spinnerFrom);

        datePanel.add(new JLabel("To:"));
        JSpinner spinnerTo = new JSpinner(new SpinnerDateModel());
        JSpinner.DateEditor editorTo = new JSpinner.DateEditor(spinnerTo, "yyyy-MM-dd");
        spinnerTo.setEditor(editorTo);
        spinnerTo.setValue(java.util.Calendar.getInstance().getTime());
        datePanel.add(spinnerTo);

        JButton btnGenerate = new JButton("Generate Report");
        styleButton(btnGenerate, new java.awt.Color(0, 51, 0), true);
        datePanel.add(btnGenerate);

        JComboBox<String> cmbReportType = new JComboBox<>(new String[] { "Daily", "Monthly", "Yearly" });
        datePanel.add(cmbReportType);

        panel.add(datePanel, BorderLayout.NORTH);

        salesReportArea = new JTextArea();
        salesReportArea.setEditable(false);
        salesReportArea.setFont(new Font("Monospaced", Font.PLAIN, 14));
        JScrollPane scrollPane = new JScrollPane(salesReportArea);
        scrollPane.setPreferredSize(new Dimension(800, 600));
        panel.add(scrollPane, BorderLayout.CENTER);

        JPanel exportPanel = new JPanel(new FlowLayout());
        JButton btnExportCSV = new JButton("Save as CSV");
        styleButton(btnExportCSV, new java.awt.Color(0, 51, 0), true);
        btnExportCSV.addActionListener(e -> exportSalesReportToCSV());
        exportPanel.add(btnExportCSV);

        panel.add(exportPanel, BorderLayout.SOUTH);

        btnGenerate.addActionListener(e -> {
            java.util.Date fromDate = (java.util.Date) spinnerFrom.getValue();
            java.util.Date toDate = (java.util.Date) spinnerTo.getValue();
            String reportType = (String) cmbReportType.getSelectedItem();

            generateSalesReport(salesReportArea, fromDate, toDate, reportType);
        });

        return panel;
    }

    private JPanel createInventoryReportPanel() {
        JPanel panel = new JPanel(new BorderLayout(10, 10));

        inventoryTableModel = new DefaultTableModel(new String[] {
                "No.", "Date", "Product", "Activity Type", "Quantity Change", "Description" }, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        inventoryTable = new JTable(inventoryTableModel);
        inventoryTable.setRowHeight(30);
        inventoryTable.getTableHeader().setBackground(new java.awt.Color(0, 51, 0));
        inventoryTable.getTableHeader().setForeground(Color.WHITE);
        inventoryTable.getTableHeader().setFont(new Font("Segoe UI", Font.BOLD, 12));
        inventoryTable.setFont(new Font("Segoe UI", Font.PLAIN, 13));

        inventoryTable.getColumnModel().getColumn(0).setPreferredWidth(40);

        loadInventoryActivityReport();

        panel.add(new JScrollPane(inventoryTable), BorderLayout.CENTER);

        JPanel buttonPanel = new JPanel(new FlowLayout());
        JButton btnRefresh = new JButton("Refresh");
        styleButton(btnRefresh, new java.awt.Color(0, 51, 0), false);
        btnRefresh.addActionListener(e -> loadInventoryActivityReport());
        buttonPanel.add(btnRefresh);

        JButton btnExportCSV = new JButton("Export as CSV");
        styleButton(btnExportCSV, new java.awt.Color(0, 51, 0), true);
        btnExportCSV.addActionListener(e -> exportTableToCSV(inventoryTable, "Inventory_Activity_Report"));
        buttonPanel.add(btnExportCSV);

        panel.add(buttonPanel, BorderLayout.SOUTH);

        return panel;
    }

    private void loadProductReport() {
        productTableModel.setRowCount(0);
        List<Product> products = productDAO.getAllProducts();
        NumberFormat currency = NumberFormat.getCurrencyInstance(new Locale("en", "PH"));

        int rowNum = 1;
        for (Product p : products) {
            Category cat = categoryDAO.getCategoryById(p.getCategoryId());
            Brand brand = brandDAO.getBrandById(p.getBrandId());
            Supplier sup = supplierDAO.getSupplierById(p.getSupplierId());

            productTableModel.addRow(new Object[] {
                    rowNum++,
                    p.getId(),
                    p.getName(),
                    cat != null ? cat.getName() : "N/A",
                    brand != null ? brand.getName() : "N/A",
                    sup != null ? sup.getName() : "N/A",
                    p.getUnit(),
                    currency.format(p.getCostPrice()),
                    String.format("%.2f%%", p.getMarkupPercentage()),
                    currency.format(p.getSellingPrice()),
                    p.getQuantity()
            });
        }
    }

    private void generateSalesReport(JTextArea reportArea, java.util.Date fromDate, java.util.Date toDate,
            String reportType) {
        LocalDateTime startDateTime = LocalDateTime.ofInstant(
                fromDate.toInstant(), java.time.ZoneId.systemDefault());
        LocalDateTime endDateTime = LocalDateTime.ofInstant(
                toDate.toInstant(), java.time.ZoneId.systemDefault());

        endDateTime = endDateTime.withHour(23).withMinute(59).withSecond(59);

        if ("Daily".equals(reportType)) {
            startDateTime = startDateTime.withHour(0).withMinute(0).withSecond(0);
        } else if ("Monthly".equals(reportType)) {
            startDateTime = startDateTime.withDayOfMonth(1).withHour(0).withMinute(0).withSecond(0);
            endDateTime = endDateTime.withDayOfMonth(endDateTime.toLocalDate().lengthOfMonth())
                    .withHour(23).withMinute(59).withSecond(59);
        } else if ("Yearly".equals(reportType)) {
            startDateTime = startDateTime.withDayOfYear(1).withHour(0).withMinute(0).withSecond(0);
            endDateTime = endDateTime.withDayOfYear(endDateTime.toLocalDate().lengthOfYear())
                    .withHour(23).withMinute(59).withSecond(59);
        }

        lastReportStart = startDateTime;
        lastReportEnd = endDateTime;
        lastReportType = reportType;

        double totalRevenue = salesDAO.getTotalRevenue(startDateTime, endDateTime);
        int transactionCount = salesDAO.getTransactionCount(startDateTime, endDateTime);
        List<Object[]> topProducts = salesDAO.getTopSellingProducts(startDateTime, endDateTime, 10);

        NumberFormat currency = NumberFormat.getCurrencyInstance(new Locale("en", "PH"));
        DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");

        StringBuilder report = new StringBuilder();
        report.append("=".repeat(80)).append("\n");
        report.append("                          SALES REPORT\n");
        report.append("=".repeat(80)).append("\n");
        report.append(String.format("Report Type: %s\n", reportType));
        report.append(String.format("Period: %s to %s\n",
                startDateTime.format(dateFormatter), endDateTime.format(dateFormatter)));
        report.append("-".repeat(80)).append("\n");
        report.append(String.format("Total Revenue: %s\n", currency.format(totalRevenue)));
        report.append(String.format("Total Transactions: %d\n", transactionCount));
        report.append("-".repeat(80)).append("\n");
        report.append("\nTOP SELLING PRODUCTS:\n");
        report.append("-".repeat(80)).append("\n");
        report.append(String.format("%-5s %-35s %-15s %-20s\n", "No.", "Product Name", "Quantity Sold", "Revenue"));
        report.append("-".repeat(80)).append("\n");

        int rowNum = 1;
        for (Object[] product : topProducts) {
            report.append(String.format("%-5d %-35s %-15d %-20s\n",
                    rowNum++, product[1], product[2], currency.format(product[3])));
        }

        report.append("=".repeat(80)).append("\n");

        reportArea.setText(report.toString());
    }

    private void loadInventoryActivityReport() {
        inventoryTableModel.setRowCount(0);
        List<Object[]> logs = inventoryDAO.getAllInventoryLogs();

        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");

        int rowNum = 1;
        for (Object[] log : logs) {
            String date = "";
            try {
                if (log[4] instanceof java.sql.Timestamp) {
                    date = ((java.sql.Timestamp) log[4]).toLocalDateTime().format(formatter);
                } else {
                    date = log[4].toString();
                }
            } catch (Exception e) {
                date = log[4].toString();
            }

            String productName = log[6] != null ? log[6].toString() : "N/A";
            String activityType = log[1].toString();
            int qtyChange = (int) log[2];
            String description = log[3] != null ? log[3].toString() : "";

            inventoryTableModel.addRow(new Object[] {
                    rowNum++,
                    date,
                    productName,
                    activityType,
                    qtyChange,
                    description
            });
        }
    }

    private String escapeCSV(String value) {
        if (value == null)
            return "";
        if (value.contains(",") || value.contains("\"") || value.contains("\n")) {
            return "\"" + value.replace("\"", "\"\"") + "\"";
        }
        return value;
    }

    private File getUniqueFile(File directory, String baseName, String extension) {
        File file = new File(directory, baseName + extension);
        int count = 1;
        while (file.exists()) {
            file = new File(directory, baseName + "(" + count + ")" + extension);
            count++;
        }
        return file;
    }

    private void exportTableToCSV(JTable table, String defaultFileName) {
        JFileChooser fileChooser = new JFileChooser();
        fileChooser.setDialogTitle("Save as CSV");

        File currentDir = fileChooser.getCurrentDirectory();
        File uniqueFile = getUniqueFile(currentDir, defaultFileName, ".csv");
        fileChooser.setSelectedFile(uniqueFile);

        int userSelection = fileChooser.showSaveDialog(this);
        if (userSelection == JFileChooser.APPROVE_OPTION) {
            File fileToSave = fileChooser.getSelectedFile();
            try (PrintWriter writer = new PrintWriter(fileToSave, java.nio.charset.StandardCharsets.UTF_8)) {
                writer.write('\uFEFF');

                boolean firstCol = true;
                for (int i = 0; i < table.getColumnCount(); i++) {
                    if (table.getColumnModel().getColumn(i).getWidth() > 0) {
                        if (!firstCol)
                            writer.print(",");
                        writer.print(escapeCSV(table.getColumnName(i)));
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
                            String text = (value != null) ? value.toString() : "";
                            writer.print(escapeCSV(text));
                            firstCol = false;
                        }
                    }
                    writer.println();
                }

                JOptionPane.showMessageDialog(this, "Report exported successfully!", "Success",
                        JOptionPane.INFORMATION_MESSAGE);
            } catch (IOException e) {
                JOptionPane.showMessageDialog(this, "Error exporting report: " + e.getMessage(),
                        "Error", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    private void exportSalesReportToCSV() {
        if (salesReportArea.getText().isEmpty()) {
            JOptionPane.showMessageDialog(this, "Please generate a report first!", "No Report",
                    JOptionPane.WARNING_MESSAGE);
            return;
        }

        if (lastReportStart == null || lastReportEnd == null) {
            JOptionPane.showMessageDialog(this, "Please generate a report first!", "No Report",
                    JOptionPane.WARNING_MESSAGE);
            return;
        }

        JFileChooser fileChooser = new JFileChooser();
        fileChooser.setDialogTitle("Save as CSV");

        File currentDir = fileChooser.getCurrentDirectory();
        File uniqueFile = getUniqueFile(currentDir, "Sales_Report", ".csv");
        fileChooser.setSelectedFile(uniqueFile);

        int userSelection = fileChooser.showSaveDialog(this);
        if (userSelection == JFileChooser.APPROVE_OPTION) {
            File fileToSave = fileChooser.getSelectedFile();
            try (PrintWriter writer = new PrintWriter(fileToSave, java.nio.charset.StandardCharsets.UTF_8)) {
                writer.write('\uFEFF');

                double totalRevenue = salesDAO.getTotalRevenue(lastReportStart, lastReportEnd);
                int transactionCount = salesDAO.getTransactionCount(lastReportStart, lastReportEnd);
                List<Object[]> topProducts = salesDAO.getTopSellingProducts(lastReportStart, lastReportEnd, 10);

                NumberFormat currency = NumberFormat.getCurrencyInstance(new Locale("en", "PH"));
                currency.setGroupingUsed(false);
                DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");

                writer.println("SALES REPORT");
                writer.println("Report Type," + escapeCSV(lastReportType));
                writer.println("Period From," + lastReportStart.format(dateFormatter));
                writer.println("Period To," + lastReportEnd.format(dateFormatter));
                writer.println("Total Revenue," + currency.format(totalRevenue));
                writer.println("Total Transactions," + transactionCount);
                writer.println();

                writer.println("TOP SELLING PRODUCTS");
                writer.println("No.,Product Name,Quantity Sold,Revenue");

                int rowNum = 1;
                for (Object[] product : topProducts) {
                    writer.print(rowNum++);
                    writer.print(",");
                    writer.print(escapeCSV(product[1] != null ? product[1].toString() : ""));
                    writer.print(",");
                    writer.print(product[2]);
                    writer.print(",");
                    writer.print(currency.format((double) product[3]));
                    writer.println();
                }

                JOptionPane.showMessageDialog(this, "Report exported successfully!", "Success",
                        JOptionPane.INFORMATION_MESSAGE);
            } catch (IOException e) {
                JOptionPane.showMessageDialog(this, "Error exporting report: " + e.getMessage(),
                        "Error", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    private void styleButton(JButton btn, Color bgColor, boolean bold) {
        btn.setBackground(bgColor);
        btn.setForeground(Color.WHITE);
        btn.setFont(new Font("Segoe UI", bold ? Font.BOLD : Font.PLAIN, 12));
        btn.setFocusPainted(false);
    }
}
