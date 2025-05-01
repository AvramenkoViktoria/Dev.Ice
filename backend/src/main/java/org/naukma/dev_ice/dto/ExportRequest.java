package org.naukma.dev_ice.dto;
import java.util.List;

public class ExportRequest {
    private List<Long> orderIds;
    private String sortOrder;

    public List<Long> getOrderIds() {
        return orderIds;
    }

    public void setOrderIds(List<Long> orderIds) {
        this.orderIds = orderIds;
    }

    public String getSortOrder() {
        return sortOrder;
    }

    public void setSortOrder(String sortOrder) {
        this.sortOrder = sortOrder;
    }
}
