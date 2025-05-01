package org.naukma.dev_ice.repository;

import org.json.JSONObject;

import java.util.*;

public class OrderQueryBuilder {

    public static class QueryWithParams {
        public final String sql;
        public final Map<String, Object> params;

        public QueryWithParams(String sql, Map<String, Object> params) {
            this.sql = sql;
            this.params = params;
        }
    }

    public static QueryWithParams buildQuery(JSONObject queryParams) {
        StringBuilder sql = new StringBuilder("SELECT * FROM \"order\" o");
        List<String> conditions = new ArrayList<>();
        Map<String, Object> params = new LinkedHashMap<>();
        int paramIndex = 0;

        if (queryParams.has("filter")) {
            JSONObject filter = queryParams.getJSONObject("filter");

            paramIndex = addEqualsCondition(filter, conditions, params, "manager", "o.manager", paramIndex);
            paramIndex = addEqualsCondition(filter, conditions, params, "status", "o.status", paramIndex);
            paramIndex = addEqualsCondition(filter, conditions, params, "payed", "o.payed", paramIndex);
            paramIndex = addEqualsCondition(filter, conditions, params, "post", "o.post", paramIndex);

            paramIndex = addRangeCondition(filter, conditions, params, "orderAmount", "o.order_amount", paramIndex);
            paramIndex = addRangeCondition(filter, conditions, params, "placementDateRange", "o.placement_date", paramIndex);
            paramIndex = addRangeCondition(filter, conditions, params, "dispatchDateRange", "o.dispatch_date", paramIndex);

            if (filter.has("customerPhone")) {
                String phone = filter.getString("customerPhone");
                String paramName = "customerPhone" + paramIndex++;
                conditions.add("o.customer_id IN (SELECT customer_id FROM customer WHERE phone_num LIKE :" + paramName + ")");
                params.put(paramName, "%" + phone + "%");
            }
        }

        if (queryParams.has("search")) {
            JSONObject search = queryParams.getJSONObject("search");

            paramIndex = addEqualsCondition(search, conditions, params, "status", "o.status", paramIndex);
            paramIndex = addEqualsCondition(search, conditions, params, "manager", "o.manager", paramIndex);

            if (search.has("id")) {
                String paramName = "orderId" + paramIndex++;
                conditions.add("o.order_id = :" + paramName);
                params.put(paramName, search.getLong("id"));
            }
            if (search.has("customerPhone")) {
                String phone = search.getString("customerPhone");
                String paramName = "customerPhone" + paramIndex++;
                conditions.add("o.customer_id IN (SELECT customer_id FROM customer WHERE phone_num LIKE :" + paramName + ")");
                params.put(paramName, "%" + phone + "%");
            }
        }

        if (!conditions.isEmpty()) {
            sql.append(" WHERE ").append(String.join(" AND ", conditions));
        }

        if (queryParams.has("sort")) {
            JSONObject sort = queryParams.getJSONObject("sort");
            List<String> sortFields = new ArrayList<>();

            addSortCondition(sort, sortFields, "id", "o.order_id");
            addSortCondition(sort, sortFields, "dispatchDate", "o.dispatch_date");

            if (!sortFields.isEmpty()) {
                sql.append(" ORDER BY ").append(String.join(", ", sortFields));
            }
        }

        return new QueryWithParams(sql.toString(), params);
    }

    private static int addEqualsCondition(
            JSONObject obj,
            List<String> conditions,
            Map<String, Object> params,
            String key, String column,
            int paramIndex) {
        if (obj.has(key)) {
            Object value = obj.get(key);
            String paramName = key + paramIndex;
            conditions.add(column + " = :" + paramName);
            params.put(paramName, value);
            paramIndex++;
        }
        return paramIndex;
    }

    private static int addRangeCondition(
            JSONObject obj,
            List<String> conditions,
            Map<String, Object> params,
            String key,
            String column,
            int paramIndex
    ) {
        if (obj.has(key)) {
            JSONObject range = obj.getJSONObject(key);

            Object from = range.opt("from");
            Object to = range.opt("to");

            if (from != null) {
                String paramName = key + "From" + paramIndex;
                conditions.add(column + " >= :" + paramName);
                params.put(paramName, from);
                paramIndex++;
            }

            if (to != null) {
                String paramName = key + "To" + paramIndex;
                conditions.add(column + " <= :" + paramName);
                params.put(paramName, to);
                paramIndex++;
            }
        }
        return paramIndex;
    }

    private static void addSortCondition(JSONObject sortObj, List<String> sortFields, String key, String column) {
        if (sortObj.has(key)) {
            String direction = sortObj.getString(key).toUpperCase();
            if ("ASC".equals(direction) || "DESC".equals(direction)) {
                sortFields.add(column + " " + direction);
            }
        }
    }
}
