package org.naukma.dev_ice.entity;

import java.sql.Timestamp;

public class Order {
    private Long orderId;
    private Long managerId;
    private String customerEmail;
    private String status;
    private Timestamp placementDate;
    private Timestamp dispatchDate;
    private String paymentMethod;
    private Boolean payed;
    private String post;
    private String postOffice;
    private Double orderAmount;

    public Long getOrderId() {
        return orderId;
    }

    public void setOrderId(Long orderId) {
        this.orderId = orderId;
    }

    public Long getManagerId() {
        return managerId;
    }

    public void setManagerId(Long managerId) {
        this.managerId = managerId;
    }

    public String getCustomerEmail() {
        return customerEmail;
    }

    public void setCustomerEmail(String customerEmail) {
        this.customerEmail = customerEmail;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public Timestamp getPlacementDate() {
        return placementDate;
    }

    public void setPlacementDate(Timestamp placementDate) {
        this.placementDate = placementDate;
    }

    public Timestamp getDispatchDate() {
        return dispatchDate;
    }

    public void setDispatchDate(Timestamp dispatchDate) {
        this.dispatchDate = dispatchDate;
    }

    public String getPaymentMethod() {
        return paymentMethod;
    }

    public void setPaymentMethod(String paymentMethod) {
        this.paymentMethod = paymentMethod;
    }

    public Boolean getPayed() {
        return payed;
    }

    public void setPayed(Boolean payed) {
        this.payed = payed;
    }

    public String getPost() {
        return post;
    }

    public void setPost(String post) {
        this.post = post;
    }

    public String getPostOffice() {
        return postOffice;
    }

    public void setPostOffice(String postOffice) {
        this.postOffice = postOffice;
    }

    public Double getOrderAmount() {
        return orderAmount;
    }

    public void setOrderAmount(Double orderAmount) {
        this.orderAmount = orderAmount;
    }

    public String getStatus() {
        return status;
    }
}
