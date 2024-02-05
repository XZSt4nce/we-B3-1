package com.wavesenterprise.app.domain;

import org.jetbrains.annotations.NotNull;

public class Product {
    private final String title;
    private String description;
    private String[] regions;
    private int minOrderCount;
    private int maxOrderCount;
    private String[] distributors;
    private boolean isConfirmed;


    public Product(
            @NotNull String title,
            @NotNull String description,
            @NotNull String[] regions
    ) {
        this.title = title;
        this.description = description;
        this.regions = regions;
        this.isConfirmed = false;
    }

    public void confirm(
            @NotNull String description,
            @NotNull String[] regions,
            @NotNull Integer minOrderCount,
            @NotNull Integer maxOrderCount,
            @NotNull String[] distributors
    ) {
        this.description = description;
        this.regions = regions;
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
}
