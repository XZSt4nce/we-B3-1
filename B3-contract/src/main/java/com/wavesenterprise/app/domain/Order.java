package com.wavesenterprise.app.domain;

import com.google.common.hash.Hashing;

import java.nio.charset.StandardCharsets;
import java.util.Date;
import java.util.Objects;

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
    private long deliveryDate;
    private String deliveryAddress;
    private Date creationDate;
    private OrderStatus status;
    private boolean isPrepaymentAvailable;

    public Order() {}

    public Order(
            int id,
            String clientKey,
            String executorKey,
            int productKey,
            int amount,
            long deliveryDate,
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
        this.creationDate = new Date();
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
            long deliveryLimit,
            boolean isPrepaymentAvailable
    ) throws Exception {
        if (totalPrice < 1) {
            throw INCORRECT_DATA;
        }
        this.price = totalPrice;
        this.deliveryDate = deliveryLimit;
        this.isPrepaymentAvailable = isPrepaymentAvailable;
        updateHash();
    }

    public void confirmOrCancel(boolean isConfirm) {
        this.status = isConfirm ? EXECUTING : CANCELLED;
        updateHash();
    }

    public void pay() throws Exception {
        if (Objects.equals(this.status, WAITING_FOR_CLIENT)) { // Если организация (или её сотрудник) уточнила данные заказа
            if (!this.isPrepaymentAvailable) { // Если предоплата недоступна
                throw NOT_ENOUGH_RIGHTS; // То отказать в выполнении метода
            }
            this.status = EXECUTING_PAID;
        } else if (Objects.equals(this.status, WAITING_FOR_PAYMENT)) {
            this.status = WAITING_FOR_TAKING; // Присвоить статус заказа "Ожидает получения"
        } else {
            throw NOT_ENOUGH_RIGHTS;
        }
        updateHash();
    }

    public void complete() throws Exception {
        this.deliveryDate = new Date().getTime();
        if (Objects.equals(this.status, EXECUTING)) {
            this.status = WAITING_FOR_PAYMENT;
        } else if (Objects.equals(this.status, EXECUTING_PAID)) {
            this.status = WAITING_FOR_TAKING;
        } else {
            throw NOT_ENOUGH_RIGHTS;
        }
        updateHash();
    }

    public void take() {
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

    public Date getCreationDate() {
        return creationDate;
    }

    public long getDeliveryDate() {
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

    public void setDeliveryDate(long deliveryDate) {
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

    public void setCreationDate(Date creationDate) {
        this.creationDate = creationDate;
    }
}
