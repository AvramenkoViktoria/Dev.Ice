package org.naukma.dev_ice.repository;

import org.naukma.dev_ice.entity.Product;
import org.naukma.dev_ice.entity.Sale;
import org.springframework.stereotype.Repository;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

@Repository
public class ProductRepository {

    private final DataSource dataSource;

    public ProductRepository(DataSource dataSource) {
        this.dataSource = dataSource;
    }

    public List<Product> findAll() {
        String query = "SELECT * FROM product";
        List<Product> products = new ArrayList<>();

        try (Connection conn = dataSource.getConnection();
             PreparedStatement ps = conn.prepareStatement(query);
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                Product product = new Product();
                product.setProductId(rs.getLong("product_id"));
                product.setName(rs.getString("name"));
                product.setSellingPrice(rs.getDouble("selling_price"));
                product.setPurchasePrice(rs.getDouble("purchase_price"));
                product.setCategory(rs.getString("category"));
                product.setInStock(rs.getBoolean("in_stock"));
                product.setStorageQuantity(rs.getInt("storage_quantity"));
                product.setProducer(rs.getString("producer"));
                product.setBrand(rs.getString("brand"));
                product.setRam(rs.getInt("ram"));
                product.setColor(rs.getString("color"));
                product.setCountry(rs.getString("country"));
                product.setProdYear(rs.getDate("prod_year"));
                product.setDiagonal(rs.getDouble("diagonal"));
                product.setInternalStorage(rs.getInt("internal_storage"));

                Long saleId = rs.getLong("sale_id");
                if (!rs.wasNull()) {
                    Sale sale = new Sale();
                    sale.setSaleId(saleId);
                    product.setSale(sale);
                }

                products.add(product);
            }

        } catch (SQLException e) {
            throw new RuntimeException("Failed to fetch products", e);
        }

        return products;
    }

    public void save(Product product) {
        String query = "INSERT INTO product (sale_id, name, selling_price, purchase_price, category, in_stock, " +
                       "storage_quantity, producer, brand, ram, color, country, prod_year, diagonal, internal_storage) " +
                       "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";

        try (Connection conn = dataSource.getConnection();
             PreparedStatement ps = conn.prepareStatement(query)) {

            if (product.getSale() != null) {
                ps.setLong(1, product.getSale().getSaleId());
            } else {
                ps.setNull(1, java.sql.Types.BIGINT);
            }

            ps.setString(2, product.getName());
            ps.setDouble(3, product.getSellingPrice());
            ps.setDouble(4, product.getPurchasePrice());
            ps.setString(5, product.getCategory());
            ps.setBoolean(6, product.getInStock());
            ps.setInt(7, product.getStorageQuantity());
            ps.setString(8, product.getProducer());
            ps.setString(9, product.getBrand());
            ps.setInt(10, product.getRam());
            ps.setString(11, product.getColor());
            ps.setString(12, product.getCountry());
            ps.setDate(13, product.getProdYear());
            ps.setDouble(14, product.getDiagonal());
            ps.setInt(15, product.getInternalStorage());

            ps.executeUpdate();

        } catch (SQLException e) {
            throw new RuntimeException("Failed to save product", e);
        }
    }
}
