package com.wavesenterprise.app.domain;

import java.security.NoSuchAlgorithmException;
import java.util.Date;

public class Order {
    private int hash;
    private String clientPublicKey;
    private String organizationPublicKey;
    private String productKey;
    private int count;
    private Date desiredDeliveryLimit;
    private String deliveryAddress;
    private Date orderCreationDate;
    private String price;
    private boolean isPrepaymentPossible;
    private OrderStatus status;

    public void updateHash() {
        this.hash = hashCode();
    }

    public Order(
            String clientPublicKey,
            String organizationPublicKey,
            String productKey,
            int count,
            Date desiredDeliveryLimit,
            String deliveryAddress
    ) throws NoSuchAlgorithmException {
        this.clientPublicKey = clientPublicKey;
        this.organizationPublicKey = organizationPublicKey;
        this.productKey = productKey;
        this.count = count;
        this.desiredDeliveryLimit = desiredDeliveryLimit;
        this.deliveryAddress = deliveryAddress;
        this.status = OrderStatus.WAITING_FOR_EMPLOYEE;
        this.orderCreationDate = new Date();
        updateHash();
    }

    public int getHash() {
        return hash;
    }

    public String getClientPublicKey() {
        return clientPublicKey;
    }

    public void setClientPublicKey(String clientPublicKey) {
        this.clientPublicKey = clientPublicKey;
    }

    public String getOrganizationPublicKey() {
        return organizationPublicKey;
    }

    public void setOrganizationPublicKey(String organizationPublicKey) {
        this.organizationPublicKey = organizationPublicKey;
    }

    public String getProductKey() {
        return productKey;
    }

    public void setProductKey(String productKey) {
        this.productKey = productKey;
    }

    public int getCount() {
        return count;
    }

    public void setCount(int count) {
        this.count = count;
    }

    public String getDeliveryAddress() {
        return deliveryAddress;
    }

    public void setDeliveryAddress(String deliveryAddress) {
        this.deliveryAddress = deliveryAddress;
    }

    public Date getOrderCreationDate() {
        return orderCreationDate;
    }

    public void setOrderCreationDate(Date orderCreationDate) {
        this.orderCreationDate = orderCreationDate;
    }

    public Date getDesiredDeliveryLimit() {
        return desiredDeliveryLimit;
    }

    public void setDesiredDeliveryLimit(Date desiredDeliveryLimit) {
        this.desiredDeliveryLimit = desiredDeliveryLimit;
    }

    public String getPrice() {
        return price;
    }

    public void setPrice(String price) {
        this.price = price;
    }

    public boolean isPrepaymentPossible() {
        return isPrepaymentPossible;
    }

    public void setPrepaymentPossible(boolean prepaymentPossible) {
        isPrepaymentPossible = prepaymentPossible;
    }

    public OrderStatus getStatus() {
        return status;
    }

    public void setStatus(OrderStatus status) {
        this.status = status;
    }
}
