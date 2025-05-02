package org.naukma.dev_ice.repository;

import org.naukma.dev_ice.entity.Sale;
import org.springframework.stereotype.Repository;

import javax.sql.DataSource;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

@Repository
public class SaleRepository {

    private final DataSource dataSource;

    public SaleRepository(DataSource dataSource) {
        this.dataSource = dataSource;
    }

    public void save(Sale sale) {
        String query = "INSERT INTO sale (name, discount_value) VALUES (?, ?)";
        try (Connection conn = dataSource.getConnection();
             PreparedStatement ps = conn.prepareStatement(query)) {
            ps.setString(1, sale.getName());
            ps.setInt(2, sale.getDiscountValue());
            ps.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException("Failed to save sale", e);
        }
    }

    public int countAll() {
        String query = "SELECT COUNT(*) FROM sale";
        try (Connection conn = dataSource.getConnection();
             PreparedStatement ps = conn.prepareStatement(query);
             ResultSet rs = ps.executeQuery()) {
            return rs.next() ? rs.getInt(1) : 0;
        } catch (SQLException e) {
            throw new RuntimeException("Failed to count sales", e);
        }
    }

    public Sale findById(Long id) {
        String query = "SELECT * FROM sale WHERE sale_id = ?";
        try (Connection conn = dataSource.getConnection();
             PreparedStatement ps = conn.prepareStatement(query)) {
            ps.setLong(1, id);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    Sale sale = new Sale();
                    sale.setSaleId(rs.getLong("sale_id"));
                    sale.setName(rs.getString("name"));
                    sale.setDiscountValue(rs.getInt("discount_value"));
                    return sale;
                }
                return null;
            }
        } catch (SQLException e) {
            throw new RuntimeException("Failed to find sale by ID", e);
        }
    }

    public List<Sale> findAll() {
        String query = "SELECT * FROM sale";
        List<Sale> sales = new ArrayList<>();
        try (Connection conn = dataSource.getConnection();
             PreparedStatement ps = conn.prepareStatement(query);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                Sale sale = new Sale();
                sale.setSaleId(rs.getLong("sale_id"));
                sale.setName(rs.getString("name"));
                sale.setDiscountValue(rs.getInt("discount_value"));
                sales.add(sale);
            }
            return sales;
        } catch (SQLException e) {
            throw new RuntimeException("Failed to retrieve all sales", e);
        }
    }
}
