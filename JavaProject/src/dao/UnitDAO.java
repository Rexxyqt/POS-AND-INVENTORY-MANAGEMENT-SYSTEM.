package src.dao;

import src.db.DatabaseConnection;
import src.model.Unit;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class UnitDAO {

    // METHODS
    public List<Unit> getAllUnits() {
        List<Unit> units = new ArrayList<>();
        String sql = "SELECT * FROM units ORDER BY name";

        try (Connection conn = DatabaseConnection.getConnection();
                Statement stmt = conn.createStatement();
                ResultSet rs = stmt.executeQuery(sql)) {

            while (rs.next()) {
                Unit unit = new Unit();
                unit.setId(rs.getInt("id"));
                unit.setName(rs.getString("name"));
                units.add(unit);
            }
        } catch (SQLException e) {
            System.err.println("Error getting all units: " + e.getMessage());
        }
        return units;
    }

    public Unit getUnitById(int id) {
        String sql = "SELECT * FROM units WHERE id = ?";

        try (Connection conn = DatabaseConnection.getConnection();
                PreparedStatement pst = conn.prepareStatement(sql)) {

            pst.setInt(1, id);
            ResultSet rs = pst.executeQuery();

            if (rs.next()) {
                Unit unit = new Unit();
                unit.setId(rs.getInt("id"));
                unit.setName(rs.getString("name"));
                return unit;
            }
        } catch (SQLException e) {
            System.err.println("Error getting unit by ID: " + e.getMessage());
        }
        return null;
    }

    public Unit getUnitByName(String name) {
        String sql = "SELECT * FROM units WHERE name = ?";

        try (Connection conn = DatabaseConnection.getConnection();
                PreparedStatement pst = conn.prepareStatement(sql)) {

            pst.setString(1, name);
            ResultSet rs = pst.executeQuery();

            if (rs.next()) {
                Unit unit = new Unit();
                unit.setId(rs.getInt("id"));
                unit.setName(rs.getString("name"));
                return unit;
            }
        } catch (SQLException e) {
            System.err.println("Error getting unit by name: " + e.getMessage());
        }
        return null;
    }

    public boolean addUnit(Unit unit) {
        String sql = "INSERT INTO units (name) VALUES (?)";

        try (Connection conn = DatabaseConnection.getConnection();
                PreparedStatement pst = conn.prepareStatement(sql)) {

            pst.setString(1, unit.getName());
            return pst.executeUpdate() > 0;

        } catch (SQLException e) {
            System.err.println("Error adding unit: " + e.getMessage());
            return false;
        }
    }

    public boolean updateUnit(Unit unit) {
        String sql = "UPDATE units SET name = ? WHERE id = ?";

        try (Connection conn = DatabaseConnection.getConnection();
                PreparedStatement pst = conn.prepareStatement(sql)) {

            pst.setString(1, unit.getName());
            pst.setInt(2, unit.getId());
            return pst.executeUpdate() > 0;

        } catch (SQLException e) {
            System.err.println("Error updating unit: " + e.getMessage());
            return false;
        }
    }

    public boolean deleteUnit(int id) {
        String sql = "DELETE FROM units WHERE id = ?";

        try (Connection conn = DatabaseConnection.getConnection();
                PreparedStatement pst = conn.prepareStatement(sql)) {

            pst.setInt(1, id);
            return pst.executeUpdate() > 0;

        } catch (SQLException e) {
            System.err.println("Error deleting unit: " + e.getMessage());
            return false;
        }
    }

    public boolean isUnitInUse(String unitName) {
        String sql = "SELECT COUNT(*) FROM products WHERE unit = ?";

        try (Connection conn = DatabaseConnection.getConnection();
                PreparedStatement pst = conn.prepareStatement(sql)) {

            pst.setString(1, unitName);
            ResultSet rs = pst.executeQuery();

            if (rs.next()) {
                return rs.getInt(1) > 0;
            }
        } catch (SQLException e) {
            System.err.println("Error checking if unit is in use: " + e.getMessage());
        }
        return false;
    }

    public void initializeDefaultUnits() {
        String[] defaultUnits = { "kg", "pcs", "liters", "grams", "box", "pack", "bottle", "can", "meters", "dozen" };

        for (String unitName : defaultUnits) {
            if (getUnitByName(unitName) == null) {
                Unit unit = new Unit(0, unitName);
                addUnit(unit);
            }
        }
    }
}
