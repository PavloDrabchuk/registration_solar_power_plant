package com.example.solar_power_plant.model;

import java.math.BigDecimal;

public class Product {
    private String productId;
    private String description;
    private String imageUrl;
    private BigDecimal price;

    public Product() {
    }

    public Product(String productId, String description, String imageUrl, BigDecimal price) {
        this.productId = productId;
        this.description = description;
        this.imageUrl = imageUrl;
        this.price = price;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getProductId() {
        return productId;
    }

    public void setProductId(String productId) {
        this.productId = productId;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }

    public BigDecimal getPrice() {
        return price;
    }

    public void setPrice(BigDecimal price) {
        this.price = price;
    }

    @Override
    public String toString() {
        return "Product{" +
                "productId='" + productId + '\'' +
                ", description='" + description + '\'' +
                ", imageUrl='" + imageUrl + '\'' +
                ", price=" + price +
                '}';
    }
}
