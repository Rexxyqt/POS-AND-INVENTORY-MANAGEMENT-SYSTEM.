package src.dao;

import src.db.DatabaseConnection;
import src.model.Product;

import java.sql.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class ProductDAO {

    // METHODS
    private Product mapResultSetToProduct(ResultSet rs) throws SQLException {
        Product p = new Product();
        p.setId(rs.getInt("id"));
        p.setName(rs.getString("name"));
        p.setCategoryId(rs.getInt("category_id"));
        p.setBrandId(rs.getInt("brand_id"));
        p.setSupplierId(rs.getInt("supplier_id"));
        p.setUnit(rs.getString("unit"));

        try {
            p.setCostPrice(rs.getDouble("cost_price"));
            p.setMarkupPercentage(rs.getDouble("markup_percentage"));
            p.setSellingPrice(rs.getDouble("selling_price"));
        } catch (SQLException e) {
            double price = rs.getDouble("price");
            p.setSellingPrice(price);
            p.setCostPrice(price * 0.7);
            p.setMarkupPercentage(42.86);
        }

        p.setQuantity(rs.getInt("quantity"));
        p.setStockThreshold(rs.getInt("stock_threshold"));
        Date dateAdded = rs.getDate("date_added");
        if (dateAdded != null) {
            p.setDateAdded(dateAdded.toLocalDate());
        }
        return p;
    }

    public List<Product> getAllProducts() {
        List<Product> list = new ArrayList<>();
        String sql = "SELECT * FROM products ORDER BY name";

        try (Connection conn = DatabaseConnection.getConnection();
                Statement st = conn.createStatement();
                ResultSet rs = st.executeQuery(sql)) {

            while (rs.next()) {
                list.add(mapResultSetToProduct(rs));
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return list;
    }

    public List<Product> searchProducts(String name, Integer categoryId, Integer brandId, Integer supplierId) {
        List<Product> list = new ArrayList<>();
        StringBuilder sql = new StringBuilder("SELECT * FROM products WHERE 1=1");
        List<Object> params = new ArrayList<>();

        if (name != null && !name.trim().isEmpty()) {
            sql.append(" AND name LIKE ?");
            params.add("%" + name + "%");
        }
        if (categoryId != null && categoryId > 0) {
            sql.append(" AND category_id = ?");
            params.add(categoryId);
        }
        if (brandId != null && brandId > 0) {
            sql.append(" AND brand_id = ?");
            params.add(brandId);
        }
        if (supplierId != null && supplierId > 0) {
            sql.append(" AND supplier_id = ?");
            params.add(supplierId);
        }

        sql.append(" ORDER BY name");

        try (Connection conn = DatabaseConnection.getConnection();
                PreparedStatement ps = conn.prepareStatement(sql.toString())) {

            for (int i = 0; i < params.size(); i++) {
                ps.setObject(i + 1, params.get(i));
            }

            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                list.add(mapResultSetToProduct(rs));
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return list;
    }

    public boolean addProduct(Product p) {
        String sql = "INSERT INTO products(name, category_id, brand_id, supplier_id, unit, " +
                "cost_price, markup_percentage, selling_price, quantity, stock_threshold, date_added) " +
                "VALUES(?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";

        try (Connection conn = DatabaseConnection.getConnection();
                PreparedStatement ps = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

            ps.setString(1, p.getName());
            ps.setInt(2, p.getCategoryId());
            ps.setInt(3, p.getBrandId());
            ps.setInt(4, p.getSupplierId());
            ps.setString(5, p.getUnit());
            ps.setDouble(6, p.getCostPrice());
            ps.setDouble(7, p.getMarkupPercentage());
            ps.setDouble(8, p.getSellingPrice());
            ps.setInt(9, p.getQuantity());
            ps.setInt(10, p.getStockThreshold());
            ps.setDate(11, Date.valueOf(p.getDateAdded()));

            int result = ps.executeUpdate();
            if (result > 0) {
                try (ResultSet rs = ps.getGeneratedKeys()) {
                    if (rs.next()) {
                        int newId = rs.getInt(1);
                        String logSql = "INSERT INTO inventory_logs(product_id, activity_type, quantity_change, description, activity_date) VALUES(?, 'STOCK_IN', ?, 'Initial Stock', ?)";
                        try (PreparedStatement psLog = conn.prepareStatement(logSql)) {
                            psLog.setInt(1, newId);
                            psLog.setInt(2, p.getQuantity());
                            psLog.setTimestamp(3, Timestamp.valueOf(LocalDateTime.now()));
                            psLog.executeUpdate();
                        }
                    }
                }
                return true;
            }
            return false;

        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    public boolean updateProduct(Product p) {
        String sql = "UPDATE products SET name=?, category_id=?, brand_id=?, supplier_id=?, unit=?, " +
                "cost_price=?, markup_percentage=?, selling_price=?, quantity=?, stock_threshold=?, date_added=? " +
                "WHERE id=?";

        try (Connection conn = DatabaseConnection.getConnection();
                PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, p.getName());
            ps.setInt(2, p.getCategoryId());
            ps.setInt(3, p.getBrandId());
            ps.setInt(4, p.getSupplierId());
            ps.setString(5, p.getUnit());
            ps.setDouble(6, p.getCostPrice());
            ps.setDouble(7, p.getMarkupPercentage());
            ps.setDouble(8, p.getSellingPrice());
            ps.setInt(9, p.getQuantity());
            ps.setInt(10, p.getStockThreshold());
            ps.setDate(11, Date.valueOf(p.getDateAdded()));
            ps.setInt(12, p.getId());

            if (ps.executeUpdate() > 0) {
                return true;
            }
            return false;

        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    public boolean deleteProduct(int id) {
        String sql = "DELETE FROM products WHERE id=?";

        try (Connection conn = DatabaseConnection.getConnection();
                PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, id);
            return ps.executeUpdate() > 0;

        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    public Product getProductById(int id) {
        String sql = "SELECT * FROM products WHERE id=?";

        try (Connection conn = DatabaseConnection.getConnection();
                PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, id);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                return mapResultSetToProduct(rs);
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    public boolean updateQuantity(int productId, int quantityChange) {
        String sql = "UPDATE products SET quantity = quantity + ? WHERE id = ?";

        try (Connection conn = DatabaseConnection.getConnection();
                PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, quantityChange);
            ps.setInt(2, productId);
            return ps.executeUpdate() > 0;

        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    public List<Product> getProductsBelowQuantity(int limit) {
        List<Product> list = new ArrayList<>();
        String sql = "SELECT * FROM products WHERE quantity <= ? ORDER BY quantity";

        try (Connection conn = DatabaseConnection.getConnection();
                PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, limit);
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                list.add(mapResultSetToProduct(rs));
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return list;
    }

    public List<Product> getProductsBelowStockThreshold() {
        List<Product> list = new ArrayList<>();
        String sql = "SELECT * FROM products WHERE quantity <= stock_threshold ORDER BY quantity";

        try (Connection conn = DatabaseConnection.getConnection();
                PreparedStatement ps = conn.prepareStatement(sql)) {

            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                list.add(mapResultSetToProduct(rs));
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return list;
    }

    public boolean renumberProductIds() {
        throw new UnsupportedOperationException("Unimplemented method 'renumberProductIds'");
    }
}
