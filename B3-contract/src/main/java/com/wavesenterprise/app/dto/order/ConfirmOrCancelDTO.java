package com.wavesenterprise.app.dto.order;

public class ConfirmOrCancelDTO {
    String sender;
    String password;
    int orderKey;
    boolean isConfirm;

    public ConfirmOrCancelDTO() {}

    public ConfirmOrCancelDTO(String sender, String password, int orderKey, boolean isConfirm) {
        this.sender = sender;
        this.password = password;
        this.orderKey = orderKey;
        this.isConfirm = isConfirm;
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

    public boolean isConfirm() {
        return isConfirm;
    }

    public void setConfirm(boolean confirm) {
        isConfirm = confirm;
    }
}
