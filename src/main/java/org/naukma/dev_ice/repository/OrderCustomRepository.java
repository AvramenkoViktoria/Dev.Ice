package org.naukma.dev_ice.repository;

import org.json.JSONObject;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Map;

@Repository
public class OrderCustomRepository {

    private final NamedParameterJdbcTemplate jdbcTemplate;

    public OrderCustomRepository(NamedParameterJdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    public List<Map<String, Object>> findOrders(JSONObject queryParams) {
        OrderQueryBuilder.QueryWithParams built = OrderQueryBuilder.buildQuery(queryParams);

        return jdbcTemplate.queryForList(built.sql, built.params);
    }
}
