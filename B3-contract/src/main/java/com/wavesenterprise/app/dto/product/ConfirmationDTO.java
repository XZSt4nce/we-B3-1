package com.wavesenterprise.app.dto.product;

public class ConfirmationDTO {
    String sender;
    String password;
    int productKey;
    String description;
    int minOrderCount;
    int maxOrderCount;
    String[] distributors;

    public ConfirmationDTO() {}

    public ConfirmationDTO(String sender, String password, int productKey, String description, int minOrderCount, int maxOrderCount, String[] distributors) {
        this.sender = sender;
        this.password = password;
        this.productKey = productKey;
        this.description = description;
        this.minOrderCount = minOrderCount;
        this.maxOrderCount = maxOrderCount;
        this.distributors = distributors;
    }

    public String getSender() {
        return sender;
    }

    public void setSender(String sender) {
        this.sender = sender;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public int getProductKey() {
        return productKey;
    }

    public void setProductKey(int productKey) {
        this.productKey = productKey;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
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
}
