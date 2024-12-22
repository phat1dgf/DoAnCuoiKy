package com.example.doancuoiky.Models;

public class Category {
    private int categoryImageSource;
    private String categoryName;

    public int getCategoryImageSource() {
        return categoryImageSource;
    }

    public void setCategoryImageSource(int categoryImageSource) {
        this.categoryImageSource = categoryImageSource;
    }

    public String getCategoryName() {
        return categoryName;
    }

    public void setCategoryName(String categoryName) {
        this.categoryName = categoryName;
    }

    public Category(int imageSource, String categoryName) {
        this.categoryImageSource = imageSource;
        this.categoryName = categoryName;
    }
}
