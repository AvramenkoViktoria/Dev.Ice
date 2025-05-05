package org.naukma.dev_ice.repository;

import org.json.JSONArray;
import org.json.JSONObject;
import org.springframework.stereotype.Component;

import javax.sql.DataSource;
import java.sql.*;
import java.util.*;

@Component
public class OrderQueryBuilder {

    private final DataSource dataSource;

    public OrderQueryBuilder(DataSource dataSource) {
        this.dataSource = dataSource;
    }

    public QueryWithParams buildQuery(JSONObject queryParams) {
        StringBuilder sql = new StringBuilder(
                "SELECT " +
                "o.order_id, " +
                "o.status, " +
                "o.placement_date, " +
                "o.order_amount, " +
                "o.payment_method, " +
                "o.post, " +
                "o.payed, " +
                "o.post_office, " +
                "o.dispatch_date, " +
                "c.email AS customer_id, " +
                "c.phone_num AS customer_phone, " +
                "c.last_name AS customer_lastName, " +
                "c.first_name AS customer_firstName, " +
                "CONCAT(m.last_name, ' ', m.first_name) AS manager " +
                "FROM orders o " +
                "LEFT JOIN customer c ON o.customer_email = c.email " +
                "LEFT JOIN manager m ON o.manager_id = m.manager_id"
        );

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
                    conditions.add(column + " = :" + paramName);
                    params.put(paramName, value);
                }
            }
        }

        if (queryParams.has("filter")) {
            JSONObject filter = queryParams.getJSONObject("filter");

            for (String key : filter.keySet()) {
                String column = mapFieldToColumn(key);
                if (column == null) continue;

                JSONArray valuesArray = filter.optJSONArray(key);
                if (valuesArray == null)
                    valuesArray = new JSONArray().put(filter.get(key));

                if (key.equals("manager_id")) {
                    List<Integer> allManagerIds = getAllManagerIds();
                    List<Integer> providedIds = new ArrayList<>();
                    for (int i = 0; i < valuesArray.length(); i++) {
                        providedIds.add(valuesArray.getInt(i));
                    }

                    Collections.sort(allManagerIds);
                    Collections.sort(providedIds);

                    if (allManagerIds.equals(providedIds)) {
                        conditions.add("NOT EXISTS (" +
                                       "SELECT 1 FROM orders o2 " +
                                       "WHERE o2.order_id = o.order_id AND NOT EXISTS (" +
                                       "SELECT 1 FROM orders o3 " +
                                       "WHERE o3.order_id = o2.order_id AND o3.manager_id IS NOT NULL" +
                                       "))");
                        continue;
                    }
                }

                if (filter.get(key) instanceof JSONObject rangeObj) {
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

    private List<Integer> getAllManagerIds() {
        List<Integer> ids = new ArrayList<>();
        try (Connection conn = dataSource.getConnection();
             PreparedStatement stmt = conn.prepareStatement("SELECT manager_id FROM manager");
             ResultSet rs = stmt.executeQuery()) {
            while (rs.next()) {
                ids.add(rs.getInt("manager_id"));
            }
        } catch (SQLException e) {
            throw new RuntimeException("Failed to fetch manager IDs", e);
        }
        return ids;
    }

    private String mapFieldToColumn(String key) {
        return switch (key) {
            case "order_id" -> "o.order_id";
            case "manager_id" -> "o.manager_id";
            case "customer_email" -> "o.customer_email";
            case "status" -> "o.status";
            case "placement_date" -> "o.placement_date";
            case "dispatch_date" -> "o.dispatch_date";
            case "payment_method" -> "o.payment_method";
            case "payed" -> "o.payed";
            case "post" -> "o.post";
            case "post_office" -> "o.post_office";
            case "order_amount" -> "o.order_amount";
            default -> null;
        };
    }
}
