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

    public Product findById(Long id) throws SQLException {
        String sql = """
        SELECT product_id, sale_id, name, selling_price, purchase_price,
               category, in_stock, storage_quantity, producer, brand,
               ram, color, country, prod_year, diagonal, internal_storage
        FROM product
        WHERE product_id = ?
    """;

        try (
                Connection connection = dataSource.getConnection();
                PreparedStatement statement = connection.prepareStatement(sql)
        ) {
            statement.setLong(1, id);

            try (ResultSet rs = statement.executeQuery()) {
                if (rs.next()) {
                    Product product = new Product();

                    product.setProductId(rs.getLong("product_id"));
                    Long saleId = rs.getObject("sale_id", Long.class);

                    if (saleId != null) {
                        SaleRepository saleRepository = new SaleRepository(dataSource);
                        Sale sale = saleRepository.findById(saleId);
                        product.setSale(sale);
                    }

                    product.setName(rs.getString("name"));
                    product.setSellingPrice(rs.getDouble("selling_price"));
                    product.setPurchasePrice(rs.getDouble("purchase_price"));
                    product.setCategory(rs.getString("category"));
                    product.setInStock(rs.getBoolean("in_stock"));
                    product.setStorageQuantity(rs.getInt("storage_quantity"));
                    product.setProducer(rs.getString("producer"));
                    product.setBrand(rs.getString("brand"));
                    product.setRam(rs.getObject("ram") != null ? rs.getInt("ram") : null);
                    product.setColor(rs.getString("color"));
                    product.setCountry(rs.getString("country"));
                    product.setProdYear(rs.getDate("prod_year"));
                    product.setDiagonal(rs.getObject("diagonal") != null ? rs.getDouble("diagonal") : null);
                    product.setInternalStorage(rs.getObject("internal_storage") != null ? rs.getInt("internal_storage") : null);

                    return product;
                } else {
                    return null;
                }
            }
        }
    }

    public void deleteById(Long productId) {
        String deleteOrderProductQuery = "DELETE FROM order_product WHERE product_id = ?";
        String deleteProductQuery = "DELETE FROM product WHERE product_id = ?";

        Connection conn = null;

        try {
            conn = dataSource.getConnection();
            conn.setAutoCommit(false);

            try (PreparedStatement deleteOrderProductStmt = conn.prepareStatement(deleteOrderProductQuery)) {
                deleteOrderProductStmt.setLong(1, productId);
                int deletedFromOrderProduct = deleteOrderProductStmt.executeUpdate();

                if (deletedFromOrderProduct == 0) {
                    System.out.println("No related records found in order_product for product_id: " + productId);
                }
            }

            try (PreparedStatement deleteProductStmt = conn.prepareStatement(deleteProductQuery)) {
                deleteProductStmt.setLong(1, productId);
                int affectedRows = deleteProductStmt.executeUpdate();

                if (affectedRows == 0) {
                    throw new RuntimeException("No product found with ID: " + productId);
                }
            }

            conn.commit();
        } catch (SQLException e) {
            if (conn != null) {
                try {
                    conn.rollback();
                } catch (SQLException rollbackException) {
                    throw new RuntimeException("Failed to rollback transaction", rollbackException);
                }
            }
            throw new RuntimeException("Failed to delete product with ID: " + productId, e);
        } finally {
            if (conn != null) {
                try {
                    conn.setAutoCommit(true);
                    conn.close();
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    public void update(Product product) {
        String query = "UPDATE product SET sale_id = ?, name = ?, selling_price = ?, purchase_price = ?, " +
                       "category = ?, in_stock = ?, storage_quantity = ?, producer = ?, brand = ?, ram = ?, " +
                       "color = ?, country = ?, prod_year = ?, diagonal = ?, internal_storage = ? " +
                       "WHERE product_id = ?";

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
            ps.setLong(16, product.getProductId());

            int affectedRows = ps.executeUpdate();
            if (affectedRows == 0) {
                throw new RuntimeException("No product found with ID: " + product.getProductId());
            }

        } catch (SQLException e) {
            throw new RuntimeException("Failed to update product with ID: " + product.getProductId(), e);
        }
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
