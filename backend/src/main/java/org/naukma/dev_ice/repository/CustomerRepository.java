package org.naukma.dev_ice.repository;

import org.naukma.dev_ice.entity.Customer;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Repository;

import javax.sql.DataSource;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

@Repository
public class CustomerRepository {

    private final DataSource dataSource;

    public CustomerRepository(DataSource dataSource) {
        this.dataSource = dataSource;
    }

    public Customer findById(String email) throws SQLException {
        String sql = """
        SELECT email, phone_num, second_name, first_name, last_name, password
        FROM customer
        WHERE email = ?
    """;

        try (
                Connection connection = dataSource.getConnection();
                PreparedStatement statement = connection.prepareStatement(sql)
        ) {
            statement.setString(1, email);
            try (ResultSet rs = statement.executeQuery()) {
                if (rs.next()) {
                    Customer customer = new Customer();
                    customer.setEmail(rs.getString("email"));
                    customer.setPhoneNum(rs.getString("phone_num"));
                    customer.setSecondName(rs.getString("second_name"));
                    customer.setFirstName(rs.getString("first_name"));
                    customer.setLastName(rs.getString("last_name"));
                    customer.setPassword(rs.getString("password"));
                    return customer;
                } else {
                    return null;
                }
            }
        }
    }

    public boolean existsByEmail(String email) {
        String query = "SELECT 1 FROM customer WHERE email = ?";
        try (Connection conn = dataSource.getConnection();
             PreparedStatement ps = conn.prepareStatement(query)) {
            ps.setString(1, email);
            try (ResultSet rs = ps.executeQuery()) {
                return rs.next();
            }
        } catch (SQLException e) {
            throw new RuntimeException("Failed to check email existence", e);
        }
    }

    public boolean existsByPhoneNum(String phoneNum) {
        String query = "SELECT 1 FROM customer WHERE phone_num = ?";
        try (Connection conn = dataSource.getConnection();
             PreparedStatement ps = conn.prepareStatement(query)) {
            ps.setString(1, phoneNum);
            try (ResultSet rs = ps.executeQuery()) {
                return rs.next();
            }
        } catch (SQLException e) {
            throw new RuntimeException("Failed to check phone number existence", e);
        }
    }

    public Customer findByEmail(String email) {
        String query = "SELECT * FROM customer WHERE email = ?";
        try (Connection conn = dataSource.getConnection();
             PreparedStatement ps = conn.prepareStatement(query)) {
            ps.setString(1, email);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    Customer customer = new Customer();
                    customer.setEmail(rs.getString("email"));
                    customer.setPhoneNum(rs.getString("phone_num"));
                    customer.setSecondName(rs.getString("second_name"));
                    customer.setFirstName(rs.getString("first_name"));
                    customer.setLastName(rs.getString("last_name"));
                    customer.setPassword(rs.getString("password"));
                    return customer;
                } else {
                    return null;
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException("Failed to find customer by email", e);
        }
    }

    public List<Customer> findAll() {
        String query = "SELECT * FROM customer";
        List<Customer> customers = new ArrayList<>();

        try (Connection conn = dataSource.getConnection();
             PreparedStatement ps = conn.prepareStatement(query);
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                Customer customer = new Customer();
                customer.setSecondName(rs.getString("second_name"));
                customer.setFirstName(rs.getString("first_name"));
                customer.setLastName(rs.getString("last_name"));
                customer.setPhoneNum(rs.getString("phone_num"));
                customer.setEmail(rs.getString("email"));
                customer.setPassword(rs.getString("password"));
                customers.add(customer);
            }

        } catch (SQLException e) {
            throw new RuntimeException("Failed to retrieve customers", e);
        }

        return customers;
    }

    public void update(Customer customer) {
        String sql = """
        UPDATE customer
        SET phone_num = ?, second_name = ?, first_name = ?, last_name = ?, password = ?
        WHERE email = ?
    """;
        try (Connection conn = dataSource.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, customer.getPhoneNum());
            ps.setString(2, customer.getSecondName());
            ps.setString(3, customer.getFirstName());
            ps.setString(4, customer.getLastName());
            ps.setString(5, customer.getPassword());
            ps.setString(6, customer.getEmail());
            int updated = ps.executeUpdate();
            if (updated == 0) {
                throw new SQLException("Customer not found with email: " + customer.getEmail());
            }
        } catch (SQLException e) {
            throw new RuntimeException("Failed to update customer", e);
        }
    }

    public void deleteByEmail(String email) {
        String clearCustomerInOrders = "UPDATE orders SET customer_email = NULL WHERE customer_email = ?";
        String deleteCustomer = "DELETE FROM customer WHERE email = ?";

        Connection conn = null;
        try {
            conn = dataSource.getConnection();
            conn.setAutoCommit(false);

            try (PreparedStatement ps1 = conn.prepareStatement(clearCustomerInOrders)) {
                ps1.setString(1, email);
                ps1.executeUpdate();
            }

            try (PreparedStatement ps2 = conn.prepareStatement(deleteCustomer)) {
                ps2.setString(1, email);
                int deleted = ps2.executeUpdate();
                if (deleted == 0) {
                    conn.rollback();
                    throw new SQLException("Customer not found with email: " + email);
                }
            }

            conn.commit();
        } catch (SQLException e) {
            if (conn != null) {
                try {
                    conn.rollback();
                } catch (SQLException ex) {
                    throw new RuntimeException("Failed to rollback during delete", ex);
                }
            }
            throw new RuntimeException("Failed to delete customer", e);
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


    public void save(Customer customer) {
        String query = "INSERT INTO customer (email, phone_num, second_name, first_name, last_name, password) " +
                       "VALUES (?, ?, ?, ?, ?, ?)";
        try (Connection conn = dataSource.getConnection();
             PreparedStatement ps = conn.prepareStatement(query)) {
            ps.setString(1, customer.getEmail());
            ps.setString(2, customer.getPhoneNum());
            ps.setString(3, customer.getSecondName());
            ps.setString(4, customer.getFirstName());
            ps.setString(5, customer.getLastName());
            BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();
            ps.setString(6, encoder.encode(customer.getPassword()));
            ps.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException("Failed to save customer", e);
        }
    }
}
