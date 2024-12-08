package com.example.doancuoiky.Instance;

import android.graphics.Bitmap;

import java.io.Serializable;

public class Product implements Serializable {
    private String userID;

    private String productImageSource;
    private String productState;
    private String productName;
    private int productPrice;
    private String location;

    private String category;
    private String brandName;
    private String guarantee;
    private String description;

    public Product(String userID,

                   String productImageSource,
                   String productState,
                   String productName,
                   int productPrice,
                   String location,
                   String category,
                   String brandName,
                   String guarantee,
                   String description) {
        this.userID = userID;
        this.productImageSource = productImageSource;
        this.productState = productState;
        this.productName = productName;
        this.productPrice = productPrice;
        this.location = location;
        this.category = category;
        this.brandName = brandName;
        this.guarantee = guarantee;
        this.description = description;
    }

    public String getProductImageSource() {
        return productImageSource;
    }

    public void setProductImageSource(String productImageSource) {
        this.productImageSource = productImageSource;
    }

    public String getProductState() {
        return productState;
    }

    public void setProductState(String productState) {
        this.productState = productState;
    }

    public String getProductName() {
        return productName;
    }

    public void setProductName(String productName) {
        this.productName = productName;
    }

    public int getProductPrice() {
        return productPrice;
    }

    public void setProductPrice(int productPrice) {
        this.productPrice = productPrice;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public String getBrandName() {
        return brandName;
    }

    public void setBrandName(String brandName) {
        this.brandName = brandName;
    }

    public String getGuarantee() {
        return guarantee;
    }

    public void setGuarantee(String guarantee) {
        this.guarantee = guarantee;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }
}
