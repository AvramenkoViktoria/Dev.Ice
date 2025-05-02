package org.naukma.dev_ice.repository;

import org.naukma.dev_ice.entity.Order;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import javax.sql.DataSource;
import java.sql.*;

@Repository
public class OrderRepository {

    private final DataSource dataSource;

    @Autowired
    public OrderRepository(DataSource dataSource) {
        this.dataSource = dataSource;
    }

    public Order save(Order order) throws SQLException {
        String sql = """
        INSERT INTO "orders" (
            manager_id, customer_email, status, placement_date, dispatch_date,
            payment_method, payed, post, post_office, order_amount
        ) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?)
    """;

        try (
                Connection connection = dataSource.getConnection();
                PreparedStatement statement = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)
        ) {
            statement.setLong(1, order.getManager().getManagerId());
            statement.setString(2, order.getCustomer().getEmail());
            statement.setString(3, order.getStatus());
            statement.setTimestamp(4, order.getPlacementDate());

            if (order.getDispatchDate() != null) {
                statement.setTimestamp(5, order.getDispatchDate());
            } else {
                statement.setNull(5, Types.TIMESTAMP);
            }

            statement.setString(6, order.getPaymentMethod());
            statement.setBoolean(7, order.getPayed());
            statement.setString(8, order.getPost());
            statement.setString(9, order.getPostOffice());
            statement.setDouble(10, order.getOrderAmount());

            int affectedRows = statement.executeUpdate();
            if (affectedRows == 0) {
                throw new SQLException("Creating order failed, no rows affected.");
            }

            try (ResultSet generatedKeys = statement.getGeneratedKeys()) {
                if (generatedKeys.next()) {
                    order.setOrderId(generatedKeys.getLong(1));
                } else {
                    throw new SQLException("Creating order failed, no ID obtained.");
                }
            }
        }

        return order;
    }

    public void updateOrderAmount(Long orderId, Double amount) throws SQLException {
        String sql = "UPDATE orders SET order_amount = ? WHERE order_id = ?";
        try (
                Connection connection = dataSource.getConnection();
                PreparedStatement statement = connection.prepareStatement(sql)
        ) {
            statement.setDouble(1, amount);
            statement.setLong(2, orderId);
            statement.executeUpdate();
        }
    }
}
