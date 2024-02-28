package com.wavesenterprise.app.dto.order;

public class MakeDTO {
    String sender;
    String password;
    int productKey;
    String executorKey;
    int count;
    String desiredDeliveryLimit;
    String deliveryAddress;

    public MakeDTO() {}

    public MakeDTO(String sender, String password, int productKey, String executorKey, int count, String desiredDeliveryLimit, String deliveryAddress) {
        this.sender = sender;
        this.password = password;
        this.productKey = productKey;
        this.executorKey = executorKey;
        this.count = count;
        this.desiredDeliveryLimit = desiredDeliveryLimit;
        this.deliveryAddress = deliveryAddress;
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

    public String getExecutorKey() {
        return executorKey;
    }

    public void setExecutorKey(String executorKey) {
        this.executorKey = executorKey;
    }

    public int getCount() {
        return count;
    }

    public void setCount(int count) {
        this.count = count;
    }

    public String getDesiredDeliveryLimit() {
        return desiredDeliveryLimit;
    }

    public void setDesiredDeliveryLimit(String desiredDeliveryLimit) {
        this.desiredDeliveryLimit = desiredDeliveryLimit;
    }

    public String getDeliveryAddress() {
        return deliveryAddress;
    }

    public void setDeliveryAddress(String deliveryAddress) {
        this.deliveryAddress = deliveryAddress;
    }
}
