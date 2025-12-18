package src.ui;

import src.dao.SalesDAO;
import src.model.Sale;
import src.model.SaleItem;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.text.NumberFormat;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Locale;

public class SalesHistoryFrame extends JFrame {

    // FIELDS
    private final SalesDAO salesDAO = new SalesDAO();
    private JTable table;
    private DefaultTableModel model;

    // CONSTRUCTOR
    public SalesHistoryFrame() {
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
        topPanel.setBackground(Color.LIGHT_GRAY);
        JButton btnBack = new JButton("← Back to Sales");
        styleButton(btnBack, new java.awt.Color(0, 51, 0), true);
        btnBack.addActionListener(e -> this.dispose());
        topPanel.add(btnBack);
        add(topPanel, BorderLayout.NORTH);

        model = new DefaultTableModel(new String[] {
                "Sale ID", "Date", "Subtotal", "Tax", "Total", "Items" }, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        table = new JTable(model);
        loadSales();

        add(new JScrollPane(table), BorderLayout.CENTER);

        JPanel buttonPanel = new JPanel(new FlowLayout());
        JButton btnRefresh = new JButton("Refresh");
        styleButton(btnRefresh, new java.awt.Color(0, 51, 0), false);
        btnRefresh.addActionListener(e -> loadSales());
        buttonPanel.add(btnRefresh);

        JButton btnClose = new JButton("Close");
        styleButton(btnClose, Color.GRAY, false);
        btnClose.addActionListener(e -> dispose());
        buttonPanel.add(btnClose);

        add(buttonPanel, BorderLayout.SOUTH);

        setVisible(true);
    }

    // METHODS
    private void loadSales() {
        model.setRowCount(0);
        List<Sale> sales = salesDAO.getAllSales();
        NumberFormat currency = NumberFormat.getCurrencyInstance(new Locale("en", "PH"));
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");

        for (Sale sale : sales) {
            List<SaleItem> items = salesDAO.getSaleItems(sale.getId());
            String itemsStr = items.size() + " item(s)";
            model.addRow(new Object[] {
                    sale.getId(),
                    sale.getSaleDate().format(formatter),
                    currency.format(sale.getSubtotal()),
                    currency.format(sale.getTax()),
                    currency.format(sale.getTotal()),
                    itemsStr
            });
        }
    }

    private void styleButton(JButton btn, Color bgColor, boolean bold) {
        btn.setBackground(bgColor);
        btn.setForeground(Color.WHITE);
        btn.setFont(new Font("Segoe UI", bold ? Font.BOLD : Font.PLAIN, 12));
        btn.setFocusPainted(false);
    }
}
