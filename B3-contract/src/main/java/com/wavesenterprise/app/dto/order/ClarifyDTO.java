package com.wavesenterprise.app.dto.order;

public class ClarifyDTO {
    String sender;
    String password;
    int orderKey;
    int totalPrice;
    String deliveryLimit;
    boolean isPrepaymentAvailable;

    public ClarifyDTO() {}

    public ClarifyDTO(String sender, String password, int orderKey, int totalPrice, String deliveryLimit, boolean isPrepaymentAvailable) {
        this.sender = sender;
        this.password = password;
        this.orderKey = orderKey;
        this.totalPrice = totalPrice;
        this.deliveryLimit = deliveryLimit;
        this.isPrepaymentAvailable = isPrepaymentAvailable;
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

    public int getOrderKey() {
        return orderKey;
    }

    public void setOrderKey(int orderKey) {
        this.orderKey = orderKey;
    }

    public int getTotalPrice() {
        return totalPrice;
    }

    public void setTotalPrice(int totalPrice) {
        this.totalPrice = totalPrice;
    }

    public String getDeliveryLimit() {
        return deliveryLimit;
    }

    public void setDeliveryLimit(String deliveryLimit) {
        this.deliveryLimit = deliveryLimit;
    }

    public boolean isPrepaymentAvailable() {
        return isPrepaymentAvailable;
    }

    public void setPrepaymentAvailable(boolean prepaymentAvailable) {
        isPrepaymentAvailable = prepaymentAvailable;
    }
}
