package org.naukma.dev_ice.entity;

import java.sql.Date;

public class Product {
    private Long productId;
    private Long saleId;
    private String name;
    private Double sellingPrice;
    private Double purchasePrice;
    private String category;
    private Boolean inStock;
    private Integer storageQuantity;
    private String producer;
    private String brand;
    private Integer ram;
    private String color;
    private String country;
    private Date prodYear;
    private Double diagonal;
    private Integer internalStorage;

    public Long getProductId() {
        return productId;
    }

    public void setProductId(Long productId) {
        this.productId = productId;
    }

    public Long getSaleId() {
        return saleId;
    }

    public void setSaleId(Long saleId) {
        this.saleId = saleId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Double getSellingPrice() {
        return sellingPrice;
    }

    public void setSellingPrice(Double sellingPrice) {
        this.sellingPrice = sellingPrice;
    }

    public Double getPurchasePrice() {
        return purchasePrice;
    }

    public void setPurchasePrice(Double purchasePrice) {
        this.purchasePrice = purchasePrice;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public Boolean getInStock() {
        return inStock;
    }

    public void setInStock(Boolean inStock) {
        this.inStock = inStock;
    }

    public Integer getStorageQuantity() {
        return storageQuantity;
    }

    public void setStorageQuantity(Integer storageQuantity) {
        this.storageQuantity = storageQuantity;
    }

    public String getProducer() {
        return producer;
    }

    public void setProducer(String producer) {
        this.producer = producer;
    }

    public String getBrand() {
        return brand;
    }

    public void setBrand(String brand) {
        this.brand = brand;
    }

    public Integer getRam() {
        return ram;
    }

    public void setRam(Integer ram) {
        this.ram = ram;
    }

    public String getColor() {
        return color;
    }

    public void setColor(String color) {
        this.color = color;
    }

    public String getCountry() {
        return country;
    }

    public void setCountry(String country) {
        this.country = country;
    }

    public Date getProdYear() {
        return prodYear;
    }

    public void setProdYear(Date prodYear) {
        this.prodYear = prodYear;
    }

    public Double getDiagonal() {
        return diagonal;
    }

    public void setDiagonal(Double diagonal) {
        this.diagonal = diagonal;
    }

    public Integer getInternalStorage() {
        return internalStorage;
    }

    public void setInternalStorage(Integer internalStorage) {
        this.internalStorage = internalStorage;
    }
}
