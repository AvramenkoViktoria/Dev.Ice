package org.naukma.dev_ice.repository;

import org.naukma.dev_ice.entity.Manager;
import org.springframework.stereotype.Repository;

import javax.sql.DataSource;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

@Repository
public class ManagerRepository {

    private final DataSource dataSource;

    public ManagerRepository(DataSource dataSource) {
        this.dataSource = dataSource;
    }

    public Manager findById(Long id) throws SQLException {
        String sql = """
            SELECT manager_id, second_name, first_name, last_name, 
                   start_date, finish_date, phone_num, email, password
            FROM manager
            WHERE manager_id = ?
        """;

        try (
                Connection connection = dataSource.getConnection();
                PreparedStatement statement = connection.prepareStatement(sql)
        ) {
            statement.setLong(1, id);

            try (ResultSet rs = statement.executeQuery()) {
                if (rs.next()) {
                    Manager manager = new Manager();
                    manager.setManagerId(rs.getLong("manager_id"));
                    manager.setSecondName(rs.getString("second_name"));
                    manager.setFirstName(rs.getString("first_name"));
                    manager.setLastName(rs.getString("last_name"));
                    manager.setStartDate(rs.getTimestamp("start_date"));
                    manager.setFinishDate(rs.getTimestamp("finish_date"));
                    manager.setPhoneNum(rs.getString("phone_num"));
                    manager.setEmail(rs.getString("email"));
                    manager.setPassword(rs.getString("password"));
                    return manager;
                } else {
                    return null;
                }
            }
        }
    }

    public boolean existsByEmail(String email) {
        String query = "SELECT 1 FROM manager WHERE email = ?";
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

    public Manager findByEmail(String email) {
        String query = "SELECT * FROM manager WHERE email = ?";
        try (Connection conn = dataSource.getConnection();
             PreparedStatement ps = conn.prepareStatement(query)) {
            ps.setString(1, email);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    Manager manager = new Manager();
                    manager.setManagerId(rs.getLong("manager_id"));
                    manager.setSecondName(rs.getString("second_name"));
                    manager.setFirstName(rs.getString("first_name"));
                    manager.setLastName(rs.getString("last_name"));
                    manager.setStartDate(rs.getTimestamp("start_date"));
                    manager.setFinishDate(rs.getTimestamp("finish_date"));
                    manager.setPhoneNum(rs.getString("phone_num"));
                    manager.setEmail(rs.getString("email"));
                    manager.setPassword(rs.getString("password"));
                    return manager;
                }
                return null;
            }
        } catch (SQLException e) {
            throw new RuntimeException("Failed to find manager by email", e);
        }
    }

    public List<Manager> findAll() {
        String query = "SELECT * FROM manager";
        List<Manager> managers = new ArrayList<>();

        try (Connection conn = dataSource.getConnection();
             PreparedStatement ps = conn.prepareStatement(query);
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                Manager manager = new Manager();
                manager.setManagerId(rs.getLong("manager_id"));
                manager.setSecondName(rs.getString("second_name"));
                manager.setFirstName(rs.getString("first_name"));
                manager.setLastName(rs.getString("last_name"));
                manager.setStartDate(rs.getTimestamp("start_date"));
                manager.setFinishDate(rs.getTimestamp("finish_date"));
                manager.setPhoneNum(rs.getString("phone_num"));
                manager.setEmail(rs.getString("email"));
                manager.setPassword(rs.getString("password"));
                managers.add(manager);
            }

        } catch (SQLException e) {
            throw new RuntimeException("Failed to retrieve managers", e);
        }

        return managers;
    }

    public void update(Manager manager) {
        String sql = """
        UPDATE manager SET
            second_name = ?, first_name = ?, last_name = ?, start_date = ?, finish_date = ?,
            phone_num = ?, email = ?, password = ?
        WHERE manager_id = ?
    """;
        try (Connection conn = dataSource.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, manager.getSecondName());
            ps.setString(2, manager.getFirstName());
            ps.setString(3, manager.getLastName());
            ps.setTimestamp(4, manager.getStartDate());
            if (manager.getFinishDate() != null) {
                ps.setTimestamp(5, manager.getFinishDate());
            } else {
                ps.setNull(5, Types.TIMESTAMP);
            }
            ps.setString(6, manager.getPhoneNum());
            ps.setString(7, manager.getEmail());
            ps.setString(8, manager.getPassword());
            ps.setLong(9, manager.getManagerId());

            int updated = ps.executeUpdate();
            if (updated == 0) {
                throw new SQLException("No manager found with ID: " + manager.getManagerId());
            }
        } catch (SQLException e) {
            throw new RuntimeException("Failed to update manager", e);
        }
    }

    public void deleteById(Long managerId) {
        String updateOrdersSql = "UPDATE orders SET manager_id = NULL WHERE manager_id = ?";
        String deleteManagerSql = "DELETE FROM manager WHERE manager_id = ?";

        Connection conn = null;

        try {
            conn = dataSource.getConnection();
            conn.setAutoCommit(false); // Початок транзакції
            try (PreparedStatement updateStmt = conn.prepareStatement(updateOrdersSql)) {
                updateStmt.setLong(1, managerId);
                updateStmt.executeUpdate();
            }

            try (PreparedStatement deleteStmt = conn.prepareStatement(deleteManagerSql)) {
                deleteStmt.setLong(1, managerId);
                int deleted = deleteStmt.executeUpdate();

                if (deleted == 0) {
                    throw new SQLException("No manager found with ID: " + managerId);
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
            throw new RuntimeException("Failed to delete manager with ID: " + managerId, e);

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

    public void save(Manager manager) {
        String query = "INSERT INTO manager (second_name, first_name, last_name, start_date, finish_date, phone_num, email, password) " +
                       "VALUES (?, ?, ?, ?, ?, ?, ?, ?)";
        try (Connection conn = dataSource.getConnection();
             PreparedStatement ps = conn.prepareStatement(query)) {
            ps.setString(1, manager.getSecondName());
            ps.setString(2, manager.getFirstName());
            ps.setString(3, manager.getLastName());
            ps.setTimestamp(4, manager.getStartDate());
            if (manager.getFinishDate() != null) {
                ps.setTimestamp(5, manager.getFinishDate());
            } else {
                ps.setNull(5, Types.TIMESTAMP);
            }
            ps.setString(6, manager.getPhoneNum());
            ps.setString(7, manager.getEmail());
            ps.setString(8, manager.getPassword());
            ps.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException("Failed to save manager", e);
        }
    }
}
