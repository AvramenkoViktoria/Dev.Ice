package org.naukma.dev_ice.dto;

import org.naukma.dev_ice.entity.Order;
import org.naukma.dev_ice.entity.OrderProduct;

import java.util.List;

public class OrderUpdateRequestDto {
    private Order order;
    private List<OrderProduct> orderProducts;

    public Order getOrder() {
        return order;
    }

    public void setOrder(Order order) {
        this.order = order;
    }

    public List<OrderProduct> getOrderProducts() {
        return orderProducts;
    }

    public void setOrderProducts(List<OrderProduct> orderProducts) {
        this.orderProducts = orderProducts;
    }
}
