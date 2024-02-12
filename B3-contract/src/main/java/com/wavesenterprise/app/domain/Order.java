package com.wavesenterprise.app.domain;

import com.google.common.hash.Hashing;

import java.nio.charset.StandardCharsets;
import java.util.Date;

import static com.wavesenterprise.app.api.IContract.Exceptions.INCORRECT_DATA;
import static com.wavesenterprise.app.api.IContract.Exceptions.NOT_ENOUGH_RIGHTS;

public class Order {
    private String hash;
    private final String clientKey;
    private final String executorKey;
    private final int productKey;
    private final int amount;
    private Integer price;
    private Date deliveryDate;
    private final String deliveryAddress;
    private final Date orderCreationDate;
    private OrderStatus status;
    private boolean isPrepaymentAvailable;

    public Order() {
        this.clientKey = null;
        this.executorKey = null;
        this.productKey = 0;
        this.amount = 0;
        this.deliveryAddress = null;
        this.orderCreationDate = null;
    }

    public Order(
            String clientKey,
            String executorKey,
            int productKey,
            int amount,
            Date deliveryDate,
            String deliveryAddress
    ) {
        this.clientKey = clientKey;
        this.executorKey = executorKey;
        this.productKey = productKey;
        this.amount = amount;
        this.deliveryDate = deliveryDate;
        this.deliveryAddress = deliveryAddress;
        this.status = OrderStatus.WAITING_FOR_EMPLOYEE;
        this.orderCreationDate = new Date();
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
                        +this.orderCreationDate
                        +this.status
                        +this.isPrepaymentAvailable,
                        StandardCharsets.UTF_8
                )
                .toString();
    }

    public void clarify(
            Integer totalPrice,
            Date deliveryLimit,
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
        this.status = isConfirm ? OrderStatus.EXECUTING : OrderStatus.CANCELLED;
        updateHash();
    }

    public void pay() throws Exception {
        if (this.status == OrderStatus.WAITING_FOR_CLIENT) { // Если организация (или её сотрудник) уточнила данные заказа
            if (!this.isPrepaymentAvailable) { // Если предоплата недоступна
                throw NOT_ENOUGH_RIGHTS; // То отказать в выполнении метода
            }
            this.status = OrderStatus.EXECUTING_PAID;
        } else if (this.status == OrderStatus.WAITING_FOR_PAYMENT) {
            this.status = OrderStatus.WAITING_FOR_TAKING; // Присвоить статус заказа "Ожидает получения"
        } else {
            throw NOT_ENOUGH_RIGHTS;
        }
        updateHash();
    }

    public void complete() throws Exception {
        this.deliveryDate = new Date();
        if (this.status == OrderStatus.EXECUTING) {
            this.status = OrderStatus.WAITING_FOR_PAYMENT;
        } else if (this.status == OrderStatus.EXECUTING_PAID) {
            this.status = OrderStatus.WAITING_FOR_TAKING;
        } else {
            throw NOT_ENOUGH_RIGHTS;
        }
        updateHash();
    }

    public void take() {
        this.status = OrderStatus.TAKEN;
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

    public Date getOrderCreationDate() {
        return orderCreationDate;
    }

    public Date getDeliveryDate() {
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

    public void setDeliveryDate(Date deliveryDate) {
        this.deliveryDate = deliveryDate;
    }

    public void setStatus(OrderStatus status) {
        this.status = status;
    }

    public void setPrepaymentAvailable(boolean prepaymentAvailable) {
        isPrepaymentAvailable = prepaymentAvailable;
    }
}
