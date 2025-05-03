package org.naukma.dev_ice.service;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.naukma.dev_ice.dto.ProductSalesDto;
import org.springframework.stereotype.Service;

import javax.sql.DataSource;
import java.io.IOException;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

@Service
public class ManagerProductStatisticsService {
    private final DataSource dataSource;

    public ManagerProductStatisticsService(DataSource dataSource) {
        this.dataSource = dataSource;
    }

    public List<ProductSalesDto> getManagerProductSalesFromJson(String jsonInput) {
        List<ProductSalesDto> results = new ArrayList<>();

        ObjectMapper objectMapper = new ObjectMapper();
        long managerId;
        String fromDate;
        String toDate;

        try {
            JsonNode rootNode = objectMapper.readTree(jsonInput);
            managerId = rootNode.path("managerId").asLong();
            JsonNode dateRangeNode = rootNode.path("dateRange");
            fromDate = dateRangeNode.path("from").asText();
            toDate = dateRangeNode.path("to").asText();
        } catch (IOException e) {
            throw new RuntimeException("Invalid JSON input", e);
        }

        String sql = """
            SELECT
                p.product_id,
                p.name AS product_name,
                SUM(op.number) AS total_quantity_sold,
                SUM(op.number * p.selling_price) AS total_sales
            FROM
                orders o
            JOIN order_product op ON o.order_id = op.order_id
            JOIN product p ON op.product_id = p.product_id
            WHERE
                o.manager_id = ?
                AND o.placement_date BETWEEN ? AND ?
            GROUP BY
                p.product_id, p.name
            ORDER BY
                total_sales DESC;
        """;

        try (Connection conn = dataSource.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setLong(1, managerId);
            stmt.setString(2, fromDate);
            stmt.setString(3, toDate);

            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                ProductSalesDto dto = new ProductSalesDto();
                dto.setProductId(rs.getLong("product_id"));
                dto.setProductName(rs.getString("product_name"));
                dto.setTotalQuantitySold(rs.getInt("total_quantity_sold"));
                dto.setTotalSales(rs.getDouble("total_sales"));
                results.add(dto);
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return results;
    }
}
