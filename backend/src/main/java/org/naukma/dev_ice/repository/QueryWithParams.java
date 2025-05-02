package org.naukma.dev_ice.repository;

import java.util.Map;

public class QueryWithParams {
    private final String query;
    private final Map<String, Object> params;

    public QueryWithParams(String query, Map<String, Object> params) {
        this.query = query;
        this.params = params;
    }

    public String getQuery() {
        return query;
    }

    public Map<String, Object> getParams() {
        return params;
    }
}
