package org.naukma.dev_ice.service;

import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.naukma.dev_ice.dto.OrderExportDto;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Service;

import java.io.FileOutputStream;
import java.io.IOException;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Collections;
import java.util.List;

@Service
public class OrderExportService {
    private final JdbcTemplate jdbcTemplate;

    private static final List<String> ALLOWED_SORT_FIELDS = List.of(
            "order_id", "placement_date", "dispatch_date"
    );

    private static final List<String> ALLOWED_DIRECTIONS = List.of("ASC", "DESC");

    public OrderExportService(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    public void exportOrdersToExcel(List<Long> orderIds, String sortOrder, String filePath) throws IOException {
        validateSortOrder(sortOrder);

        String sql = "SELECT " +
                "    o.order_id, o.manager_id, o.customer_id, " +
                "    o.status, o.placement_date, o.dispatch_date, " +
                "    o.payment_method, o.payed, o.post, o.post_office, o.order_amount, " +
                "    c.first_name, c.second_name, c.phone_num, " +
                "    array_agg(p.name || ' ' || op.number) AS product_summary " +
                "FROM \"order\" o " +
                "JOIN customer c ON o.customer_id = c.email " +
                "JOIN order_product op ON o.order_id = op.order_id " +
                "JOIN product p ON op.product_id = p.product_id " +
                "WHERE o.order_id IN (" + String.join(",", Collections.nCopies(orderIds.size(), "?")) + ") " +
                "GROUP BY o.order_id, c.first_name, c.second_name, c.phone_num " +
                "ORDER BY " + sortOrder;

        List<OrderExportDto> orders = jdbcTemplate.query(sql, orderIds.toArray(), new RowMapper<OrderExportDto>() {
            @Override
            public OrderExportDto mapRow(ResultSet rs, int rowNum) throws SQLException {
                OrderExportDto order = new OrderExportDto();
                order.setOrderId(rs.getLong("order_id"));
                order.setManagerId(rs.getInt("manager_id"));
                order.setCustomerId(rs.getString("customer_id"));
                order.setStatus(rs.getString("status"));
                order.setPlacementDate(rs.getTimestamp("placement_date").toLocalDateTime());
                order.setDispatchDate(rs.getTimestamp("dispatch_date") != null ? rs.getTimestamp("dispatch_date").toLocalDateTime() : null);
                order.setPaymentMethod(rs.getString("payment_method"));
                order.setPayed(rs.getBoolean("payed"));
                order.setPost(rs.getString("post"));
                order.setPostOffice(rs.getString("post_office"));
                order.setOrderAmount(rs.getDouble("order_amount"));
                order.setCustomerFirstName(rs.getString("first_name"));
                order.setCustomerSecondName(rs.getString("second_name"));
                order.setCustomerPhone(rs.getString("phone_num"));
                order.setProductSummary(rs.getString("product_summary"));
                return order;
            }
        });

        try (Workbook workbook = new XSSFWorkbook()) {
            Sheet sheet = workbook.createSheet("Orders");

            String[] headers = {"Order ID", "Manager ID", "Customer ID", "Status", "Placement Date", "Dispatch Date",
                    "Payment Method", "Payed", "Post", "Post Office", "Order Amount",
                    "Customer First Name", "Customer Second Name", "Customer Phone", "Product Summary"};

            Row headerRow = sheet.createRow(0);
            for (int i = 0; i < headers.length; i++) {
                headerRow.createCell(i).setCellValue(headers[i]);
            }

            int rowNum = 1;
            for (OrderExportDto order : orders) {
                Row row = sheet.createRow(rowNum++);
                row.createCell(0).setCellValue(order.getOrderId());
                row.createCell(1).setCellValue(order.getManagerId());
                row.createCell(2).setCellValue(order.getCustomerId());
                row.createCell(3).setCellValue(order.getStatus());
                row.createCell(4).setCellValue(order.getPlacementDate().toString());
                row.createCell(5).setCellValue(order.getDispatchDate() != null ? order.getDispatchDate().toString() : "");
                row.createCell(6).setCellValue(order.getPaymentMethod());
                row.createCell(7).setCellValue(order.isPayed());
                row.createCell(8).setCellValue(order.getPost());
                row.createCell(9).setCellValue(order.getPostOffice());
                row.createCell(10).setCellValue(order.getOrderAmount());
                row.createCell(11).setCellValue(order.getCustomerFirstName());
                row.createCell(12).setCellValue(order.getCustomerSecondName());
                row.createCell(13).setCellValue(order.getCustomerPhone());
                row.createCell(14).setCellValue(order.getProductSummary());
            }

            for (int i = 0; i < headers.length; i++) {
                sheet.autoSizeColumn(i);
            }

            try (FileOutputStream fileOut = new FileOutputStream(filePath)) {
                workbook.write(fileOut);
            }
        }
    }

    private void validateSortOrder(String sortOrder) {
        if (sortOrder == null || sortOrder.isBlank()) {
            throw new IllegalArgumentException("Sort order must not be null or empty");
        }

        String[] parts = sortOrder.trim().split("\\s+");
        if (parts.length != 2) {
            throw new IllegalArgumentException("Sort order must be in format 'field ASC|DESC'");
        }

        String field = parts[0];
        String direction = parts[1].toUpperCase();

        if (!ALLOWED_SORT_FIELDS.contains(field) || !ALLOWED_DIRECTIONS.contains(direction)) {
            throw new IllegalArgumentException("Invalid sort order: " + sortOrder);
        }
    }
}
