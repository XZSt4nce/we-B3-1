package com.wavesenterprise.app.domain;

import java.util.Arrays;

public class Product {
    private int id;
    private String mader;
    private String title;
    private String description;
    private String[] regions;
    private int minOrderCount;
    private int maxOrderCount;
    private String[] distributors;
    private boolean isConfirmed;

    public Product() {}

    public Product(
            int id,
            String sender,
            String title,
            String description,
            String[] regions
    ) {
        this.id = id;
        this.mader = sender;
        this.title = title;
        this.description = description;
        this.regions = Arrays.stream(regions).map(String::toUpperCase).distinct().toList().toArray(new String[0]);
        this.isConfirmed = false;
        this.minOrderCount = -1;
        this.maxOrderCount = -1;
    }

    public void confirm(
            String description,
            Integer minOrderCount,
            Integer maxOrderCount,
            String[] distributors
    ) {
        this.description = description;
        this.minOrderCount = minOrderCount;
        this.maxOrderCount = maxOrderCount;
        this.distributors = distributors;
        this.isConfirmed = true;
    }

    public String getTitle() {
        return title;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String[] getRegions() {
        return regions;
    }

    public void setRegions(String[] regions) {
        this.regions = regions;
    }

    public int getMinOrderCount() {
        return minOrderCount;
    }

    public void setMinOrderCount(int minOrderCount) {
        this.minOrderCount = minOrderCount;
    }

    public int getMaxOrderCount() {
        return maxOrderCount;
    }

    public void setMaxOrderCount(int maxOrderCount) {
        this.maxOrderCount = maxOrderCount;
    }

    public String[] getDistributors() {
        return distributors;
    }

    public void setDistributors(String[] distributors) {
        this.distributors = distributors;
    }

    public boolean isConfirmed() {
        return isConfirmed;
    }

    public String getMader() {
        return mader;
    }

    public void setMinOrderCount(Integer minOrderCount) {
        this.minOrderCount = minOrderCount;
    }

    public void setMaxOrderCount(Integer maxOrderCount) {
        this.maxOrderCount = maxOrderCount;
    }

    public void setConfirmed(boolean confirmed) {
        isConfirmed = confirmed;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public void setMader(String mader) {
        this.mader = mader;
    }

    public void setTitle(String title) {
        this.title = title;
    }
}
