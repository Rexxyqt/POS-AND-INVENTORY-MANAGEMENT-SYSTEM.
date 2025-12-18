package src.dao;

import src.db.DatabaseConnection;
import src.model.Product;
import src.model.Sale;
import src.model.SaleItem;

import java.sql.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class SalesDAO {

    // METHODS
    public int createSale(Sale sale) {
        String sql = "INSERT INTO sales(sale_date, subtotal, tax, total) VALUES(?, ?, ?, ?)";

        try (Connection conn = DatabaseConnection.getConnection();
                PreparedStatement ps = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

            ps.setTimestamp(1, Timestamp.valueOf(sale.getSaleDate()));
            ps.setDouble(2, sale.getSubtotal());
            ps.setDouble(3, sale.getTax());
            ps.setDouble(4, sale.getTotal());

            int rowsAffected = ps.executeUpdate();
            if (rowsAffected > 0) {
                ResultSet rs = ps.getGeneratedKeys();
                if (rs.next()) {
                    return rs.getInt(1);
                }
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }
        return -1;
    }

    public boolean addSaleItem(SaleItem item) {
        String sql = "INSERT INTO sale_items(sale_id, product_id, product_name, quantity, unit_price, subtotal) " +
                "VALUES(?, ?, ?, ?, ?, ?)";

        try (Connection conn = DatabaseConnection.getConnection();
                PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, item.getSaleId());
            ps.setInt(2, item.getProductId());
            ps.setString(3, item.getProductName());
            ps.setInt(4, item.getQuantity());
            ps.setDouble(5, item.getUnitPrice());
            ps.setDouble(6, item.getSubtotal());

            return ps.executeUpdate() > 0;

        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    public boolean createSaleTransaction(Sale sale) {
        Connection conn = null;
        try {
            conn = DatabaseConnection.getConnection();
            conn.setAutoCommit(false);

            int saleId = createSale(sale);
            if (saleId == -1) {
                conn.rollback();
                return false;
            }
            sale.setId(saleId);

            ProductDAO productDAO = new ProductDAO();
            for (SaleItem item : sale.getItems()) {
                item.setSaleId(saleId);

                Product product = productDAO.getProductById(item.getProductId());
                if (product == null || product.getQuantity() < item.getQuantity()) {
                    conn.rollback();
                    return false;
                }

                if (!addSaleItem(item)) {
                    conn.rollback();
                    return false;
                }

                if (!productDAO.updateQuantity(item.getProductId(), -item.getQuantity())) {
                    conn.rollback();
                    return false;
                }

                String logSql = "INSERT INTO inventory_logs(product_id, activity_type, quantity_change, description, activity_date) VALUES(?, 'STOCK_OUT', ?, ?, ?)";
                try (PreparedStatement psLog = conn.prepareStatement(logSql)) {
                    psLog.setInt(1, item.getProductId());
                    psLog.setInt(2, -item.getQuantity());
                    psLog.setString(3, "Sale ID: " + saleId);
                    psLog.setTimestamp(4, Timestamp.valueOf(sale.getSaleDate()));
                    psLog.executeUpdate();
                }
            }

            conn.commit();
            return true;

        } catch (SQLException e) {
            e.printStackTrace();
            try {
                if (conn != null)
                    conn.rollback();
            } catch (SQLException ex) {
                ex.printStackTrace();
            }
            return false;
        } finally {
            try {
                if (conn != null) {
                    conn.setAutoCommit(true);
                    conn.close();
                }
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }

    public List<Sale> getAllSales() {
        List<Sale> list = new ArrayList<>();
        String sql = "SELECT * FROM sales ORDER BY sale_date DESC";

        try (Connection conn = DatabaseConnection.getConnection();
                Statement st = conn.createStatement();
                ResultSet rs = st.executeQuery(sql)) {

            while (rs.next()) {
                Sale sale = new Sale();
                sale.setId(rs.getInt("id"));
                sale.setSaleDate(rs.getTimestamp("sale_date").toLocalDateTime());
                sale.setSubtotal(rs.getDouble("subtotal"));
                sale.setTax(rs.getDouble("tax"));
                sale.setTotal(rs.getDouble("total"));
                list.add(sale);
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return list;
    }

    public Sale getSaleById(int id) {
        String sql = "SELECT * FROM sales WHERE id=?";

        try (Connection conn = DatabaseConnection.getConnection();
                PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, id);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                Sale sale = new Sale();
                sale.setId(rs.getInt("id"));
                sale.setSaleDate(rs.getTimestamp("sale_date").toLocalDateTime());
                sale.setSubtotal(rs.getDouble("subtotal"));
                sale.setTax(rs.getDouble("tax"));
                sale.setTotal(rs.getDouble("total"));
                sale.setItems(getSaleItems(id));
                return sale;
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    public List<SaleItem> getSaleItems(int saleId) {
        List<SaleItem> list = new ArrayList<>();
        String sql = "SELECT * FROM sale_items WHERE sale_id=?";

        try (Connection conn = DatabaseConnection.getConnection();
                PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, saleId);
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                SaleItem item = new SaleItem();
                item.setId(rs.getInt("id"));
                item.setSaleId(rs.getInt("sale_id"));
                item.setProductId(rs.getInt("product_id"));
                item.setProductName(rs.getString("product_name"));
                item.setQuantity(rs.getInt("quantity"));
                item.setUnitPrice(rs.getDouble("unit_price"));
                item.setSubtotal(rs.getDouble("subtotal"));
                list.add(item);
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return list;
    }

    public List<Sale> getSalesByDateRange(LocalDateTime startDate, LocalDateTime endDate) {
        List<Sale> list = new ArrayList<>();
        String sql = "SELECT * FROM sales WHERE sale_date BETWEEN ? AND ? ORDER BY sale_date DESC";

        try (Connection conn = DatabaseConnection.getConnection();
                PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setTimestamp(1, Timestamp.valueOf(startDate));
            ps.setTimestamp(2, Timestamp.valueOf(endDate));
            ResultSet rs = ps.executeQuery();

            while (rs.next()) {
                Sale sale = new Sale();
                sale.setId(rs.getInt("id"));
                sale.setSaleDate(rs.getTimestamp("sale_date").toLocalDateTime());
                sale.setSubtotal(rs.getDouble("subtotal"));
                sale.setTax(rs.getDouble("tax"));
                sale.setTotal(rs.getDouble("total"));
                list.add(sale);
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return list;
    }

    public double getTotalRevenue(LocalDateTime startDate, LocalDateTime endDate) {
        String sql = "SELECT SUM(total) as total_revenue FROM sales WHERE sale_date BETWEEN ? AND ?";

        try (Connection conn = DatabaseConnection.getConnection();
                PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setTimestamp(1, Timestamp.valueOf(startDate));
            ps.setTimestamp(2, Timestamp.valueOf(endDate));
            ResultSet rs = ps.executeQuery();

            if (rs.next()) {
                return rs.getDouble("total_revenue");
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return 0.0;
    }

    public int getTransactionCount(LocalDateTime startDate, LocalDateTime endDate) {
        String sql = "SELECT COUNT(*) as count FROM sales WHERE sale_date BETWEEN ? AND ?";

        try (Connection conn = DatabaseConnection.getConnection();
                PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setTimestamp(1, Timestamp.valueOf(startDate));
            ps.setTimestamp(2, Timestamp.valueOf(endDate));
            ResultSet rs = ps.executeQuery();

            if (rs.next()) {
                return rs.getInt("count");
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return 0;
    }

    public List<Object[]> getTopSellingProducts(LocalDateTime startDate, LocalDateTime endDate, int limit) {
        List<Object[]> list = new ArrayList<>();
        String sql = "SELECT si.product_id, si.product_name, SUM(si.quantity) as total_quantity, " +
                "SUM(si.subtotal) as total_revenue " +
                "FROM sale_items si " +
                "INNER JOIN sales s ON si.sale_id = s.id " +
                "WHERE s.sale_date BETWEEN ? AND ? " +
                "GROUP BY si.product_id, si.product_name " +
                "ORDER BY total_quantity DESC " +
                "LIMIT ?";

        try (Connection conn = DatabaseConnection.getConnection();
                PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setTimestamp(1, Timestamp.valueOf(startDate));
            ps.setTimestamp(2, Timestamp.valueOf(endDate));
            ps.setInt(3, limit);
            ResultSet rs = ps.executeQuery();

            while (rs.next()) {
                Object[] row = new Object[4];
                row[0] = rs.getInt("product_id");
                row[1] = rs.getString("product_name");
                row[2] = rs.getInt("total_quantity");
                row[3] = rs.getDouble("total_revenue");
                list.add(row);
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return list;
    }
}
