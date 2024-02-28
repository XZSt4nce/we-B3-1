package com.wavesenterprise.app.domain;

import com.google.common.hash.Hashing;

import java.nio.charset.StandardCharsets;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Date;

import static com.wavesenterprise.app.api.IContract.Exceptions.INCORRECT_DATA;
import static com.wavesenterprise.app.api.IContract.Exceptions.NOT_ENOUGH_RIGHTS;
import static com.wavesenterprise.app.domain.OrderStatus.*;

public class Order {
    private String hash;
    private int id;
    private String clientKey;
    private String executorKey;
    private int productKey;
    private int amount;
    private int price;
    private String deliveryDate;
    private String deliveryAddress;
    private long creationDate;
    private OrderStatus status;
    private boolean isPrepaymentAvailable;

    public Order() {}

    public Order(
            int id,
            String clientKey,
            String executorKey,
            int productKey,
            int amount,
            String deliveryDate,
            String deliveryAddress
    ) {
        this.id = id;
        this.clientKey = clientKey;
        this.executorKey = executorKey;
        this.productKey = productKey;
        this.amount = amount;
        this.deliveryDate = deliveryDate;
        this.deliveryAddress = deliveryAddress;
        this.status = WAITING_FOR_EMPLOYEE;
        this.creationDate = new Date().getTime();
        updateHash();
    }

    private void updateHash() {
        this.hash = Hashing.sha256()
                .hashString(
                        this.hash
                        +this.clientKey
                        +this.executorKey
                        +this.productKey
                        +this.amount
                        +this.price
                        +this.deliveryDate
                        +this.deliveryAddress
                        +this.creationDate
                        +this.status
                        +this.isPrepaymentAvailable,
                        StandardCharsets.UTF_8
                )
                .toString();
    }

    public void clarify(
            Integer totalPrice,
            String deliveryLimit,
            boolean isPrepaymentAvailable
    ) throws Exception {
        if (totalPrice < 1) {
            throw INCORRECT_DATA;
        } else if (this.status != WAITING_FOR_EMPLOYEE) {
            throw NOT_ENOUGH_RIGHTS;
        }
        this.price = totalPrice;
        this.deliveryDate = deliveryLimit;
        this.isPrepaymentAvailable = isPrepaymentAvailable;
        this.status = WAITING_FOR_CLIENT;
        updateHash();
    }

    public void confirmOrCancel(boolean isConfirm) throws Exception {
        if (this.status != WAITING_FOR_CLIENT) {
            throw NOT_ENOUGH_RIGHTS;
        }

        if (isConfirm) {
            if (this.isPrepaymentAvailable) {
                this.status = WAITING_FOR_PAYMENT;
            } else {
                this.status = EXECUTING;
            }
        } else {
            this.status = CANCELLED;
        }
        updateHash();
    }

    public void pay() throws Exception {
        if (this.status != WAITING_FOR_PAYMENT) {
            throw NOT_ENOUGH_RIGHTS;
        }

        if (this.isPrepaymentAvailable) {
            this.status = EXECUTING_PAID;
        } else {
            this.status = WAITING_FOR_TAKING;
        }
        updateHash();
    }

    public void complete() throws Exception {
        this.deliveryDate = LocalDate.now().format(DateTimeFormatter.ISO_DATE);

        if (this.status == EXECUTING) {
            this.status = WAITING_FOR_PAYMENT;
        } else if (this.status == EXECUTING_PAID) {
            this.status = WAITING_FOR_TAKING;
        } else {
            throw NOT_ENOUGH_RIGHTS;
        }
        updateHash();
    }

    public void take() throws Exception {
        if (this.status != WAITING_FOR_TAKING) {
            throw NOT_ENOUGH_RIGHTS;
        }
        this.status = TAKEN;
        updateHash();
    }

    public String getClientKey() {
        return clientKey;
    }

    public String getExecutorKey() {
        return executorKey;
    }

    public int getProductKey() { return productKey; }

    public int getAmount() {
        return amount;
    }

    public String getDeliveryAddress() {
        return deliveryAddress;
    }

    public long getCreationDate() {
        return creationDate;
    }

    public String getDeliveryDate() {
        return deliveryDate;
    }

    public OrderStatus getStatus() {
        return status;
    }

    public int getPrice() {
        return price;
    }

    public String getHash() {
        return hash;
    }

    public boolean getPrepaymentAvailable() {
        return isPrepaymentAvailable;
    }

    public void setHash(String hash) {
        this.hash = hash;
    }

    public void setPrice(Integer price) {
        this.price = price;
    }

    public void setDeliveryDate(String deliveryDate) {
        this.deliveryDate = deliveryDate;
    }

    public void setStatus(OrderStatus status) {
        this.status = status;
    }

    public void setPrepaymentAvailable(boolean prepaymentAvailable) {
        isPrepaymentAvailable = prepaymentAvailable;
    }

    public int getId() {
        return id;
    }

    public boolean isPrepaymentAvailable() {
        return isPrepaymentAvailable;
    }

    public void setId(int id) {
        this.id = id;
    }

    public void setClientKey(String clientKey) {
        this.clientKey = clientKey;
    }

    public void setExecutorKey(String executorKey) {
        this.executorKey = executorKey;
    }

    public void setProductKey(int productKey) {
        this.productKey = productKey;
    }

    public void setAmount(int amount) {
        this.amount = amount;
    }

    public void setPrice(int price) {
        this.price = price;
    }

    public void setDeliveryAddress(String deliveryAddress) {
        this.deliveryAddress = deliveryAddress;
    }

    public void setCreationDate(long creationDate) {
        this.creationDate = creationDate;
    }
}
