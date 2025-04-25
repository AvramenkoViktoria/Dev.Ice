package org.naukma.dev_ice.repository;

import org.json.JSONObject;

import java.util.StringJoiner;

public class OrderQueryBuilder {

    public static String buildQuery(JSONObject queryParams) {
        StringBuilder sqlQuery = new StringBuilder("SELECT * FROM \"order\" o");

        StringJoiner whereConditions = new StringJoiner(" AND ");

        if (queryParams.has("filter")) {
            JSONObject filter = queryParams.getJSONObject("filter");

            addEqualsCondition(filter, whereConditions, "manager", "o.manager");
            addEqualsCondition(filter, whereConditions, "status", "o.status");
            addEqualsCondition(filter, whereConditions, "payed", "o.payed");
            addEqualsCondition(filter, whereConditions, "post", "o.post");

            addRangeCondition(filter, whereConditions, "orderAmount", "o.order_amount", false);
            addRangeCondition(filter, whereConditions, "placementDateRange", "o.placement_date", true);
            addRangeCondition(filter, whereConditions, "dispatchDateRange", "o.dispatch_date", true);

            if (filter.has("customerPhone")) {
                String phone = filter.getString("customerPhone");
                whereConditions.add("o.customer_id IN (SELECT customer_id FROM customer WHERE phone_num LIKE '%" + phone.replace("'", "''") + "%')");
            }
        }

        if (queryParams.has("search")) {
            JSONObject search = queryParams.getJSONObject("search");

            addEqualsCondition(search, whereConditions, "status", "o.status");
            if (search.has("id")) {
                whereConditions.add("o.order_id = " + search.getLong("id"));
            }
            if (search.has("customerPhone")) {
                String phone = search.getString("customerPhone");
                whereConditions.add("o.customer_id IN (SELECT customer_id FROM customer WHERE phone_num LIKE '%" + phone.replace("'", "''") + "%')");
            }
            addEqualsCondition(search, whereConditions, "manager", "o.manager");
        }

        if (whereConditions.length() > 0) {
            sqlQuery.append(" WHERE ").append(whereConditions);
        }

        if (queryParams.has("sort")) {
            JSONObject sort = queryParams.getJSONObject("sort");
            StringJoiner sortConditions = new StringJoiner(", ", " ORDER BY ", "");

            addSortCondition(sort, sortConditions, "id", "o.order_id");
            addSortCondition(sort, sortConditions, "dispatchDate", "o.dispatch_date");

            sqlQuery.append(sortConditions);
        }

        return sqlQuery.toString();
    }


    private static void addEqualsCondition(JSONObject obj, StringJoiner joiner, String key, String column) {
        if (obj.has(key)) {
            Object value = obj.get(key);
            if (value instanceof Boolean || value instanceof Number) {
                joiner.add(column + " = " + value);
            } else {
                joiner.add(column + " = '" + value.toString().replace("'", "''") + "'");
            }
        }
    }

    private static void addRangeCondition(JSONObject obj, StringJoiner joiner, String key, String column, boolean quoteValues) {
        if (obj.has(key)) {
            JSONObject range = obj.getJSONObject(key);
            String from = range.optString("from", null);
            String to = range.optString("to", null);

            if (from != null && !from.isEmpty()) {
                joiner.add(column + " >= " + (quoteValues ? "'" + from + "'" : from));
            }
            if (to != null && !to.isEmpty()) {
                joiner.add(column + " <= " + (quoteValues ? "'" + to + "'" : to));
            }
        }
    }

    private static void addSortCondition(JSONObject sortObj, StringJoiner joiner, String key, String column) {
        if (sortObj.has(key)) {
            String direction = sortObj.getString(key).toUpperCase();
            if ("ASC".equals(direction) || "DESC".equals(direction)) {
                joiner.add(column + " " + direction);
            }
        }
    }
}
