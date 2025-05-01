package org.naukma.dev_ice.entity;

import jakarta.persistence.*;
import java.sql.Date;

@Entity
@Table(name = "product")
public class Product {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "product_id")
    private Long productId;

    @ManyToOne
    @JoinColumn(name = "sale_id", referencedColumnName = "sale_id")
    private Sale sale;

    @Column(name = "name", nullable = false)
    private String name;

    @Column(name = "selling_price", nullable = false)
    private Double sellingPrice;

    @Column(name = "purchase_price", nullable = false)
    private Double purchasePrice;

    @Column(name = "category", nullable = false)
    private String category;

    @Column(name = "in_stock", nullable = false)
    private Boolean inStock;

    @Column(name = "storage_quantity", nullable = false)
    private Integer storageQuantity;

    @Column(name = "producer", nullable = false)
    private String producer;

    @Column(name = "brand", nullable = false)
    private String brand;

    @Column(name = "ram", nullable = false)
    private Integer ram;

    @Column(name = "color", nullable = false)
    private String color;

    @Column(name = "country", nullable = false)
    private String country;

    @Column(name = "prod_year", nullable = false)
    private Date prodYear;

    @Column(name = "diagonal", nullable = false)
    private Double diagonal;

    @Column(name = "internal_storage", nullable = false)
    private Integer internalStorage;

    public Long getProductId() {
        return productId;
    }

    public void setProductId(Long productId) {
        this.productId = productId;
    }

    public Sale getSale() {
        return sale;
    }

    public void setSale(Sale sale) {
        this.sale = sale;
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
