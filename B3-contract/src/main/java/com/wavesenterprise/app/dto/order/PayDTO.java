package com.wavesenterprise.app.dto.order;

public class PayDTO {
    String sender;
    String password;
    int orderKey;

    public PayDTO() {}

    public PayDTO(String sender, String password, int orderKey) {
        this.sender = sender;
        this.password = password;
        this.orderKey = orderKey;
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
}
