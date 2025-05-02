package org.naukma.dev_ice.repository;

import org.json.JSONObject;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Map;

@Repository
public class OrderCustomRepository {

    private final NamedParameterJdbcTemplate jdbcTemplate;
    private final OrderQueryBuilder orderQueryBuilder;

    public OrderCustomRepository(NamedParameterJdbcTemplate jdbcTemplate,
                                 OrderQueryBuilder orderQueryBuilder) {
        this.jdbcTemplate = jdbcTemplate;
        this.orderQueryBuilder = orderQueryBuilder;
    }

    public List<Map<String, Object>> findOrders(JSONObject queryParams) {
        QueryWithParams built = orderQueryBuilder.buildQuery(queryParams);
        return jdbcTemplate.queryForList(built.getQuery(), built.getParams());
    }
}
