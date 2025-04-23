package org.naukma.dev_ice.config;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;

@Component
public class DataBaseInitializer {

    private final JdbcTemplate jdbc;

    public DataBaseInitializer(JdbcTemplate jdbc) {
        this.jdbc = jdbc;
    }

    @PostConstruct
    public void initializeDatabase() {
        jdbc.execute("""
        CREATE TABLE IF NOT EXISTS Manager (
            manager_id SERIAL PRIMARY KEY,
            second_name VARCHAR(25) NOT NULL,
            first_name VARCHAR(25) NOT NULL,
            last_name VARCHAR(25) NOT NULL,
            start_date TIMESTAMP NOT NULL,
            finish_date TIMESTAMP,
            phone_num VARCHAR(20) NOT NULL
        )
    """);

        jdbc.execute("""
        CREATE TABLE IF NOT EXISTS Sale (
            sale_id SERIAL PRIMARY KEY,
            name VARCHAR(50) NOT NULL,
            discount_value INTEGER NOT NULL CHECK (discount_value >= 0 AND discount_value <= 100)
        )
    """);

        jdbc.execute("""
        CREATE TABLE IF NOT EXISTS Product (
            product_id SERIAL PRIMARY KEY,
            sale_id INTEGER REFERENCES Sale(sale_id),
            name VARCHAR(50) NOT NULL,
            selling_price DOUBLE PRECISION NOT NULL,
            purchase_price DOUBLE PRECISION NOT NULL,
            category VARCHAR(30) NOT NULL,
            in_stock BOOLEAN NOT NULL,
            storage_quantity INTEGER NOT NULL CHECK (storage_quantity >= 0),
            producer VARCHAR(50) NOT NULL,
            brand VARCHAR(30) NOT NULL
        )
    """);

        jdbc.execute("""
        CREATE TABLE IF NOT EXISTS Customer (
            email VARCHAR(50) PRIMARY KEY,
            phone_num VARCHAR(20) UNIQUE NOT NULL,
            first_name VARCHAR(25) NOT NULL,
            last_name VARCHAR(25) NOT NULL,
            start_date TIMESTAMP NOT NULL,
            password VARCHAR(50) NOT NULL
        )
    """);

        jdbc.execute("""
        CREATE TABLE IF NOT EXISTS "order" (
            order_id SERIAL PRIMARY KEY,
            manager_id INTEGER REFERENCES Manager(manager_id),
            status VARCHAR(50) NOT NULL,
            placement_date TIMESTAMP NOT NULL,
            dispatch_date TIMESTAMP,
            payment_method VARCHAR(50) NOT NULL,
            payed BOOLEAN NOT NULL,
            post VARCHAR(16) NOT NULL,
            post_office VARCHAR(50) NOT NULL,
            order_amount DOUBLE PRECISION NOT NULL
        )
    """);

        jdbc.execute("""
        CREATE TABLE IF NOT EXISTS Order_Product (
            order_id INTEGER NOT NULL REFERENCES "order"(order_id) ON DELETE CASCADE,
            product_id INTEGER NOT NULL REFERENCES Product(product_id) ON DELETE CASCADE,
            number INTEGER NOT NULL CHECK (number > 0),
            PRIMARY KEY (order_id, product_id)
        )
    """);
    }
}
