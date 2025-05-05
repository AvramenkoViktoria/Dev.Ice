package org.naukma.dev_ice.repository;

import org.json.JSONArray;
import org.json.JSONObject;
import org.springframework.stereotype.Component;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.*;

@Component
public class ProductQueryBuilder {

    private final DataSource dataSource;

    public ProductQueryBuilder(DataSource dataSource) {
        this.dataSource = dataSource;
    }

    public QueryWithParams buildQuery(JSONObject queryParams) {
        StringBuilder sql = new StringBuilder("SELECT p.*, s.discount_value FROM product p LEFT JOIN sale s ON p.sale_id = s.sale_id");
        List<String> conditions = new ArrayList<>();
        Map<String, Object> params = new LinkedHashMap<>();
        int paramIndex = 0;

        if (queryParams.has("search")) {
            JSONObject search = queryParams.getJSONObject("search");
            for (String key : search.keySet()) {
                Object value = search.get(key);
                if (value instanceof JSONArray)
                    throw new IllegalArgumentException("Multiple values for key '" + key + "' are not allowed in 'search'");

                String paramName = key + paramIndex++;
                String column = mapFieldToColumn(key);
                if (column != null) {
                    if (key.equals("sale")) {
                        conditions.add("EXISTS (" +
                                       "SELECT 1 FROM product p2 " +
                                       "JOIN sale s ON p2.sale_id = s.sale_id " +
                                       "WHERE p2.product_id = p.product_id AND s.discount_value = :" + paramName +
                                       ")");
                    } else {
                        conditions.add(column + " = :" + paramName);
                    }
                    params.put(paramName, value);
                }
            }
        }

        if (queryParams.has("filter")) {
            JSONObject filter = queryParams.getJSONObject("filter");

            for (String key : filter.keySet()) {
                String column = mapFieldToColumn(key);
                if (column == null) continue;

                if (key.equals("sale")) {
                    JSONObject saleRange = filter.getJSONObject("sale");
                    int from = saleRange.getInt("from");
                    int to = saleRange.getInt("to");

                    if (from == 1 && to == 99) {
                        conditions.add("NOT EXISTS (" +
                                       "SELECT 1 FROM product p2 " +
                                       "WHERE p2.product_id = p.product_id AND NOT EXISTS (" +
                                       "SELECT 1 FROM product p3 " +
                                       "WHERE p3.product_id = p2.product_id AND p3.sale_id IS NOT NULL" +
                                       "))");
                        continue;
                    }

                    conditions.add("EXISTS (" +
                                   "SELECT 1 FROM product p2 " +
                                   "JOIN sale s ON p2.sale_id = s.sale_id " +
                                   "WHERE p2.product_id = p.product_id AND s.discount_value BETWEEN " + from + " AND " + to +
                                   ")");
                    continue;
                }

                Object filterValue = filter.get(key);
                if (filterValue instanceof JSONObject rangeObj) {
                    String fromParam = key + "_from" + paramIndex++;
                    String toParam = key + "_to" + paramIndex++;

                    if (rangeObj.has("from")) {
                        conditions.add(column + " >= :" + fromParam);
                        params.put(fromParam, rangeObj.get("from"));
                    }
                    if (rangeObj.has("to")) {
                        conditions.add(column + " <= :" + toParam);
                        params.put(toParam, rangeObj.get("to"));
                    }
                    continue;
                }

                JSONArray valuesArray = filter.optJSONArray(key);
                if (valuesArray == null)
                    valuesArray = new JSONArray().put(filterValue);

                List<String> subConditions = new ArrayList<>();
                for (int i = 0; i < valuesArray.length(); i++) {
                    Object val = valuesArray.get(i);
                    String paramName = key + paramIndex++;
                    subConditions.add(column + " = :" + paramName);
                    params.put(paramName, val);
                }

                if (!subConditions.isEmpty())
                    conditions.add("(" + String.join(" OR ", subConditions) + ")");
            }
        }

        if (!conditions.isEmpty()) {
            sql.append(" WHERE ").append(String.join(" AND ", conditions));
        }

        if (queryParams.has("sort")) {
            JSONObject sort = queryParams.getJSONObject("sort");
            Iterator<String> keys = sort.keys();
            if (keys.hasNext()) {
                String key = keys.next();
                String column = mapFieldToColumn(key);
                if (column != null) {
                    String direction = sort.optString(key).toUpperCase();
                    if ("ASC".equals(direction) || "DESC".equals(direction)) {
                        sql.append(" ORDER BY ").append(column).append(" ").append(direction);
                    }
                }
            }
        }

        System.out.println(sql.toString());
        System.out.println(params);
        return new QueryWithParams(sql.toString(), params);
    }

    private List<Integer> getAllSaleIds() {
        List<Integer> ids = new ArrayList<>();
        try (Connection conn = dataSource.getConnection();
             PreparedStatement stmt = conn.prepareStatement("SELECT sale_id FROM sale");
             ResultSet rs = stmt.executeQuery()) {
            while (rs.next()) {
                ids.add(rs.getInt("sale_id"));
            }
        } catch (SQLException e) {
            throw new RuntimeException("Failed to fetch sale IDs", e);
        }
        return ids;
    }

    private String mapFieldToColumn(String key) {
        return switch (key) {
            case "product_id" -> "p.product_id";
            case "sale" -> "sale";
            case "name" -> "p.name";
            case "selling_price" -> "p.selling_price";
            case "purchase_price" -> "p.purchase_price";
            case "category" -> "p.category";
            case "in_stock" -> "p.in_stock";
            case "storage_quantity" -> "p.storage_quantity";
            case "producer" -> "p.producer";
            case "brand" -> "p.brand";
            case "ram" -> "p.ram";
            case "color" -> "p.color";
            case "country" -> "p.country";
            case "prod_year" -> "p.prod_year";
            case "diagonal" -> "p.diagonal";
            case "internal_storage" -> "p.internal_storage";
            case "discount_value" -> "s.discount_value";
            default -> null;
        };
    }

}
