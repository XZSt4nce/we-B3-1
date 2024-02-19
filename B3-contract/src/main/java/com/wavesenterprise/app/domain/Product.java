package com.wavesenterprise.app.domain;

import java.util.Arrays;

public class Product {
    private final int id;
    private final String mader;
    private final String title;
    private String description;
    private String[] regions;
    private Integer minOrderCount;
    private Integer maxOrderCount;
    private String[] distributors;
    private boolean isConfirmed;

    public Product() {
        this.id = -1;
        this.mader = null;
        this.title = null;
    }

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
    }

    public void confirm(
            String description,
            String[] regions,
            Integer minOrderCount,
            Integer maxOrderCount,
            String[] distributors
    ) {
        this.description = description;
        this.regions = Arrays.stream(regions).map(String::toUpperCase).distinct().toList().toArray(new String[0]);
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
}
