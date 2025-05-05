package org.naukma.dev_ice.repository;

import org.naukma.dev_ice.entity.Order;
import org.naukma.dev_ice.entity.OrderProduct;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import javax.sql.DataSource;
import java.sql.*;
import java.util.List;

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
            if (order.getManagerId() != null) {
                statement.setLong(1, order.getManagerId());
            } else {
                statement.setNull(1, java.sql.Types.BIGINT);
            }
            statement.setString(2, order.getCustomerEmail());
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

    public Order save(Order order, List<OrderProduct> orderProducts) throws SQLException {
        Order savedOrder = save(order);
        String orderProductSql = "INSERT INTO order_product (order_id, product_id, number) VALUES (?, ?, ?)";

        try (Connection connection = dataSource.getConnection();
             PreparedStatement ps = connection.prepareStatement(orderProductSql)) {

            for (OrderProduct op : orderProducts) {
                ps.setLong(1, savedOrder.getOrderId());
                ps.setLong(2, op.getProductId());
                ps.setInt(3, op.getNumber());
                ps.addBatch();
            }
            ps.executeBatch();
        }
        return savedOrder;
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

    public void update(Order order) throws SQLException {
        String updateOrderSql = """
        UPDATE orders SET
            manager_id = ?, customer_email = ?, status = ?, placement_date = ?, dispatch_date = ?,
            payment_method = ?, payed = ?, post = ?, post_office = ?, order_amount = ?
        WHERE order_id = ?
    """;

        try (Connection connection = dataSource.getConnection();
             PreparedStatement updateOrderStmt = connection.prepareStatement(updateOrderSql)) {

            updateOrderStmt.setLong(1, order.getManagerId());
            updateOrderStmt.setString(2, order.getCustomerEmail());
            updateOrderStmt.setString(3, order.getStatus());
            updateOrderStmt.setTimestamp(4, order.getPlacementDate());

            if (order.getDispatchDate() != null) {
                updateOrderStmt.setTimestamp(5, order.getDispatchDate());
            } else {
                updateOrderStmt.setNull(5, Types.TIMESTAMP);
            }

            updateOrderStmt.setString(6, order.getPaymentMethod());
            updateOrderStmt.setBoolean(7, order.getPayed());
            updateOrderStmt.setString(8, order.getPost());
            updateOrderStmt.setString(9, order.getPostOffice());
            updateOrderStmt.setDouble(10, order.getOrderAmount());
            updateOrderStmt.setLong(11, order.getOrderId());

            int updated = updateOrderStmt.executeUpdate();
            if (updated == 0) {
                throw new SQLException("No order found with ID: " + order.getOrderId());
            }
        }
    }

    public void update(Order order, List<OrderProduct> orderProducts) throws SQLException {
        String deleteOrderProductsSql = "DELETE FROM order_product WHERE order_id = ?";
        String insertOrderProductSql = """
        INSERT INTO order_product (order_id, product_id, number)
        VALUES (?, ?, ?)
    """;

        try (
                Connection connection = dataSource.getConnection();
                PreparedStatement deleteOrderProductsStmt = connection.prepareStatement(deleteOrderProductsSql);
                PreparedStatement insertOrderProductStmt = connection.prepareStatement(insertOrderProductSql)
        ) {
            connection.setAutoCommit(false);
            update(order);

            deleteOrderProductsStmt.setLong(1, order.getOrderId());
            deleteOrderProductsStmt.executeUpdate();

            for (OrderProduct op : orderProducts) {
                insertOrderProductStmt.setLong(1, order.getOrderId());
                insertOrderProductStmt.setLong(2, op.getProductId());
                insertOrderProductStmt.setInt(3, op.getNumber());
                insertOrderProductStmt.addBatch();
            }
            insertOrderProductStmt.executeBatch();

            connection.commit();

        } catch (SQLException e) {
            throw new RuntimeException("Failed to update order with its products", e);
        }
    }

    public void deleteById(Long orderId) throws SQLException {
        String deleteOrderProductSql = "DELETE FROM order_product WHERE order_id = ?";
        String deleteOrderSql = "DELETE FROM orders WHERE order_id = ?";

        try (
                Connection connection = dataSource.getConnection();
                PreparedStatement deleteOrderProductStmt = connection.prepareStatement(deleteOrderProductSql);
                PreparedStatement deleteOrderStmt = connection.prepareStatement(deleteOrderSql)
        ) {
            connection.setAutoCommit(false);

            deleteOrderProductStmt.setLong(1, orderId);
            deleteOrderProductStmt.executeUpdate();

            deleteOrderStmt.setLong(1, orderId);
            int deleted = deleteOrderStmt.executeUpdate();

            if (deleted == 0) {
                connection.rollback();
                throw new SQLException("No order found with ID: " + orderId);
            }

            connection.commit();
        } catch (SQLException e) {
            throw new RuntimeException("Failed to delete order and its products: " + e.getMessage(), e);
        }
    }

    public Order getById(Long orderId) throws SQLException {
        String orderSql = """
        SELECT * FROM orders WHERE order_id = ?
    """;
        String orderProductsSql = """
        SELECT * FROM order_product WHERE order_id = ?
    """;

        try (
                Connection connection = dataSource.getConnection();
                PreparedStatement orderStmt = connection.prepareStatement(orderSql);
                PreparedStatement productsStmt = connection.prepareStatement(orderProductsSql)
        ) {
            orderStmt.setLong(1, orderId);
            ResultSet orderRs = orderStmt.executeQuery();

            if (!orderRs.next()) {
                return null;
            }

            Order order = new Order();
            order.setOrderId(orderRs.getLong("order_id"));
            order.setManagerId(orderRs.getLong("manager_id"));
            order.setCustomerEmail(orderRs.getString("customer_email"));
            order.setStatus(orderRs.getString("status"));
            order.setPlacementDate(orderRs.getTimestamp("placement_date"));
            order.setDispatchDate(orderRs.getTimestamp("dispatch_date"));
            order.setPaymentMethod(orderRs.getString("payment_method"));
            order.setPayed(orderRs.getBoolean("payed"));
            order.setPost(orderRs.getString("post"));
            order.setPostOffice(orderRs.getString("post_office"));
            order.setOrderAmount(orderRs.getDouble("order_amount"));

            productsStmt.setLong(1, orderId);
            ResultSet productRs = productsStmt.executeQuery();

            List<OrderProduct> products = new java.util.ArrayList<>();
            while (productRs.next()) {
                OrderProduct op = new OrderProduct();
                op.setOrderId(productRs.getLong("order_id"));
                op.setProductId(productRs.getLong("product_id"));
                op.setNumber(productRs.getInt("number"));
                products.add(op);
            }

            order.setOrderProducts(products);

            return order;
        }
    }

}
