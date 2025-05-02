package org.naukma.dev_ice.repository;

import org.naukma.dev_ice.entity.OrderProduct;
import org.springframework.stereotype.Repository;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

@Repository
public class OrderProductRepository {

    private final DataSource dataSource;

    public OrderProductRepository(DataSource dataSource) {
        this.dataSource = dataSource;
    }

    public void save(OrderProduct orderProduct) {
        String query = "INSERT INTO order_product (order_id, product_id, number) VALUES (?, ?, ?)";

        try (Connection conn = dataSource.getConnection();
             PreparedStatement ps = conn.prepareStatement(query)) {

            ps.setLong(1, orderProduct.getOrderId());
            ps.setLong(2, orderProduct.getProductId());
            ps.setInt(3, orderProduct.getNumber());

            ps.executeUpdate();

        } catch (SQLException e) {
            throw new RuntimeException("Failed to save order-product relation", e);
        }
    }
}
