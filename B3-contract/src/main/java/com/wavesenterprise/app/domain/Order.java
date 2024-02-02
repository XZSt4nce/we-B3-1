package com.wavesenterprise.app.domain;

import java.util.Date;

public class Order {
    private String clientKey;
    private String organizationKey;
    private String productKey;
    private int count;
    private int price;
    private Date deliveryDate;
    private String deliveryAddress;
    private Date orderCreationDate;
    private OrderStatus status;
    private Boolean isPrepaymentAvailable;

    public Order(
            String clientKey,
            String organizationKey,
            String productKey,
            int count,
            Date deliveryDate,
            String deliveryAddress
    ) {
        this.clientKey = clientKey;
        this.organizationKey = organizationKey;
        this.productKey = productKey;
        this.count = count;
        this.deliveryDate = deliveryDate;
        this.deliveryAddress = deliveryAddress;
        this.status = OrderStatus.WAITING_FOR_EMPLOYEE;
        this.orderCreationDate = new Date();
    }

    public String getClientKey() {
        return clientKey;
    }

    public void setClientKey(String clientPublicKey) {
        this.clientKey = clientPublicKey;
    }

    public String getOrganizationKey() {
        return organizationKey;
    }

    public void setOrganizationPublicKey(String organizationKey) {
        this.organizationKey = organizationKey;
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

    public Date getDeliveryDate() {
        return deliveryDate;
    }

    public void setDeliveryDate(Date deliveryDate) {
        this.deliveryDate = deliveryDate;
    }

    public OrderStatus getStatus() {
        return status;
    }

    public void setStatus(OrderStatus status) {
        this.status = status;
    }

    public void setOrganizationKey(String organizationKey) {
        this.organizationKey = organizationKey;
    }

    public int getPrice() {
        return price;
    }

    public void setPrice(int price) {
        this.price = price;
    }

    public Boolean isPrepaymentAvailable() {
        return isPrepaymentAvailable;
    }

    public void setPrepaymentAvailable(Boolean prepaymentAvailable) {
        isPrepaymentAvailable = prepaymentAvailable;
    }
}
