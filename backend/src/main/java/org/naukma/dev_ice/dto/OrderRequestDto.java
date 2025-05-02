package org.naukma.dev_ice.dto;

import java.sql.Timestamp;
import java.util.List;

public class OrderRequestDto {
    public Long managerId;
    public String customerEmail;
    public String status;
    public Timestamp placementDate;
    public Timestamp dispatchDate;
    public String paymentMethod;
    public Boolean payed;
    public String post;
    public String postOffice;
    public Double orderAmount;

    public List<ProductQuantity> products;

    public static class ProductQuantity {
        public Long productId;
        public Integer number;
    }
}
