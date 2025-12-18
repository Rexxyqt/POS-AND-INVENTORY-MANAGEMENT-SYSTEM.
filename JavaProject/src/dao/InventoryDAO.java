package src.dao;

import src.db.DatabaseConnection;
import src.model.Product;

import java.sql.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class InventoryDAO {

    // METHODS
    public boolean logInventoryActivity(int productId, String activityType, int quantityChange,
            String description) {
        String sql = "INSERT INTO inventory_logs(product_id, activity_type, quantity_change, " +
                "description, activity_date) VALUES(?, ?, ?, ?, ?)";

        try (Connection conn = DatabaseConnection.getConnection();
                PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, productId);
            ps.setString(2, activityType);
            ps.setInt(3, quantityChange);
            ps.setString(4, description);
            ps.setTimestamp(5, Timestamp.valueOf(LocalDateTime.now()));

            return ps.executeUpdate() > 0;

        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    public List<Object[]> getInventoryLogs(int productId) {
        List<Object[]> list = new ArrayList<>();
        String sql = "SELECT * FROM inventory_logs WHERE product_id = ? ORDER BY activity_date DESC";

        try (Connection conn = DatabaseConnection.getConnection();
                PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, productId);
            ResultSet rs = ps.executeQuery();

            while (rs.next()) {
                Object[] row = new Object[6];
                row[0] = rs.getInt("id");
                row[1] = rs.getString("activity_type");
                row[2] = rs.getInt("quantity_change");
                row[3] = rs.getString("description");
                row[4] = rs.getTimestamp("activity_date");
                row[5] = rs.getInt("product_id");
                list.add(row);
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return list;
    }

    public List<Object[]> getAllInventoryLogs() {
        List<Object[]> list = new ArrayList<>();
        String sql = "SELECT il.*, p.name as product_name FROM inventory_logs il " +
                "LEFT JOIN products p ON il.product_id = p.id " +
                "ORDER BY il.activity_date DESC";

        try (Connection conn = DatabaseConnection.getConnection();
                Statement st = conn.createStatement();
                ResultSet rs = st.executeQuery(sql)) {

            while (rs.next()) {
                Object[] row = new Object[7];
                row[0] = rs.getInt("id");
                row[1] = rs.getString("activity_type");
                row[2] = rs.getInt("quantity_change");
                row[3] = rs.getString("description");
                row[4] = rs.getTimestamp("activity_date");
                row[5] = rs.getInt("product_id");
                row[6] = rs.getString("product_name");
                list.add(row);
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return list;
    }

    public List<Object[]> getInventorySummary(int productId, LocalDateTime startDate, LocalDateTime endDate) {
        List<Object[]> list = new ArrayList<>();
        String sql = "SELECT activity_type, SUM(quantity_change) as total_change " +
                "FROM inventory_logs " +
                "WHERE product_id = ? AND activity_date BETWEEN ? AND ? " +
                "GROUP BY activity_type";

        try (Connection conn = DatabaseConnection.getConnection();
                PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, productId);
            ps.setTimestamp(2, Timestamp.valueOf(startDate));
            ps.setTimestamp(3, Timestamp.valueOf(endDate));
            ResultSet rs = ps.executeQuery();

            while (rs.next()) {
                Object[] row = new Object[2];
                row[0] = rs.getString("activity_type");
                row[1] = rs.getInt("total_change");
                list.add(row);
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return list;
    }

    public List<Product> getLowStockProducts(int threshold) {
        ProductDAO productDAO = new ProductDAO();
        return productDAO.getProductsBelowQuantity(threshold);
    }

    public boolean adjustInventory(int productId, int quantityChange, String reason) {
        Connection conn = null;

        try {
            conn = DatabaseConnection.getConnection();
            conn.setAutoCommit(false);

            String updateSql = "UPDATE products SET quantity = quantity + ? WHERE id = ?";
            try (PreparedStatement psUpdate = conn.prepareStatement(updateSql)) {
                psUpdate.setInt(1, quantityChange);
                psUpdate.setInt(2, productId);
                if (psUpdate.executeUpdate() == 0) {
                    conn.rollback();
                    return false;
                }
            }

            String logSql = "INSERT INTO inventory_logs(product_id, activity_type, quantity_change, " +
                    "description, activity_date) VALUES(?, ?, ?, ?, ?)";
            try (PreparedStatement psLog = conn.prepareStatement(logSql)) {
                String activityType = quantityChange > 0 ? "ADJUSTMENT_IN" : "ADJUSTMENT_OUT";
                psLog.setInt(1, productId);
                psLog.setString(2, activityType);
                psLog.setInt(3, quantityChange);
                psLog.setString(4, reason);
                psLog.setTimestamp(5, Timestamp.valueOf(LocalDateTime.now()));

                if (psLog.executeUpdate() == 0) {
                    conn.rollback();
                    return false;
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
}
