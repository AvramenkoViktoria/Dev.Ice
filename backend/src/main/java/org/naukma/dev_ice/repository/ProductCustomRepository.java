package org.naukma.dev_ice.repository;

import org.json.JSONObject;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Map;

@Repository
public class ProductCustomRepository {

    private final NamedParameterJdbcTemplate jdbcTemplate;
    private final ProductQueryBuilder productQueryBuilder;

    public ProductCustomRepository(NamedParameterJdbcTemplate jdbcTemplate,
                                   ProductQueryBuilder productQueryBuilder) {
        this.jdbcTemplate = jdbcTemplate;
        this.productQueryBuilder = productQueryBuilder;
    }

    public List<Map<String, Object>> findProducts(JSONObject queryParams) {
        QueryWithParams built = productQueryBuilder.buildQuery(queryParams);
        return jdbcTemplate.queryForList(built.getQuery(), built.getParams());
    }
}
