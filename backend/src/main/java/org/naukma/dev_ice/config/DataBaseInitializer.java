package org.naukma.dev_ice.config;

import org.springframework.stereotype.Component;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;

@Component
public class DataBaseInitializer {
    private final DataSource dataSource;

    public DataBaseInitializer(DataSource dataSource) {
        this.dataSource = dataSource;
    }

    public void createTables() {
        String[] sqlStatements = {
                "CREATE TABLE IF NOT EXISTS sale (" +
                "sale_id SERIAL PRIMARY KEY, " +
                "name VARCHAR(255) NOT NULL, " +
                "discount_value INT NOT NULL" +
                ");",

                "CREATE TABLE IF NOT EXISTS product (" +
                "product_id SERIAL PRIMARY KEY, " +
                "sale_id BIGINT REFERENCES sale(sale_id), " +
                "name VARCHAR(255) NOT NULL, " +
                "selling_price DOUBLE PRECISION NOT NULL, " +
                "purchase_price DOUBLE PRECISION NOT NULL, " +
                "category VARCHAR(100), " +
                "in_stock BOOLEAN NOT NULL, " +
                "storage_quantity INT NOT NULL, " +
                "producer VARCHAR(255), " +
                "brand VARCHAR(100) " +
                ");",

                "CREATE TABLE IF NOT EXISTS manager (" +
                "manager_id SERIAL PRIMARY KEY, " +
                "second_name VARCHAR(100) NOT NULL, " +
                "first_name VARCHAR(100) NOT NULL, " +
                "last_name VARCHAR(100) NOT NULL, " +
                "start_date TIMESTAMP NOT NULL, " +
                "finish_date TIMESTAMP, " +
                "phone_num VARCHAR(20) NOT NULL, " +
                "email VARCHAR(255) UNIQUE NOT NULL, " +
                "password VARCHAR(255) NOT NULL" +
                ");",

                "CREATE TABLE IF NOT EXISTS customer (" +
                "email VARCHAR(255) PRIMARY KEY, " +
                "phone_num VARCHAR(20) UNIQUE NOT NULL, " +
                "second_name VARCHAR(100) NOT NULL, " +
                "first_name VARCHAR(100) NOT NULL, " +
                "last_name VARCHAR(100) NOT NULL, " +
                "password VARCHAR(255) NOT NULL" +
                ");",

                "CREATE TABLE IF NOT EXISTS orders (" +
                "order_id SERIAL PRIMARY KEY, " +
                "manager_id BIGINT REFERENCES manager(manager_id), " +
                "customer_email VARCHAR(255) REFERENCES customer(email), " +
                "status VARCHAR(50), " +
                "placement_date TIMESTAMP NOT NULL, " +
                "dispatch_date TIMESTAMP, " +
                "payment_method VARCHAR(50), " +
                "payed BOOLEAN NOT NULL, " +
                "post VARCHAR(255), " +
                "post_office VARCHAR(255), " +
                "order_amount DOUBLE PRECISION NOT NULL" +
                ");",

                "CREATE TABLE IF NOT EXISTS order_product (" +
                "order_id BIGINT REFERENCES orders(order_id), " +
                "product_id BIGINT REFERENCES product(product_id), " +
                "number INT NOT NULL, " +
                "PRIMARY KEY (order_id, product_id)" +
                ");"
        };

        try (Connection connection = dataSource.getConnection(); Statement statement = connection.createStatement()) {
            for (String sql : sqlStatements) {
                statement.executeUpdate(sql);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}
