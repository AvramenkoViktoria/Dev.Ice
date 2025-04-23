package org.naukma.dev_ice.entity;

import jakarta.persistence.*;

import java.sql.Timestamp;

@Entity
@Table(name = "\"order\"")
public class Order {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "order_id")
    private Long orderId;

    @ManyToOne
    @JoinColumn(name = "manager_id", referencedColumnName = "manager_id")
    private Manager manager;

    @Column(name = "status", nullable = false)
    private String status;

    @Column(name = "placement_date", nullable = false)
    private Timestamp placementDate;

    @Column(name = "dispatch_date")
    private Timestamp dispatchDate;

    @Column(name = "payment_method", nullable = false)
    private String paymentMethod;

    @Column(name = "payed", nullable = false)
    private Boolean payed;

    @Column(name = "post", nullable = false)
    private String post;

    @Column(name = "post_office", nullable = false)
    private String postOffice;

    @Column(name = "order_amount", nullable = false)
    private Double orderAmount;

    public Long getOrderId() {
        return orderId;
    }

    public void setOrderId(Long orderId) {
        this.orderId = orderId;
    }

    public Manager getManager() {
        return manager;
    }

    public void setManager(Manager manager) {
        this.manager = manager;
    }

    public String getStatus() {
        return status;
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
}
