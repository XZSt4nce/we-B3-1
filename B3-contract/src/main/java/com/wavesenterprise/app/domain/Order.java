package com.wavesenterprise.app.domain;

import com.google.common.hash.Hashing;

import java.nio.charset.StandardCharsets;
import java.util.Date;

import static com.wavesenterprise.app.api.IContract.Exceptions.INCORRECT_DATA;
import static com.wavesenterprise.app.api.IContract.Exceptions.NOT_ENOUGH_RIGHTS;

public class Order {
    private String hash;
    private final int id;
    private final String clientKey;
    private final String executorKey;
    private final int productKey;
    private final int amount;
    private Integer price;
    private long deliveryDate;
    private final String deliveryAddress;
    private final Date creationDate;
    private OrderStatus status;
    private boolean isPrepaymentAvailable;

    public Order() {
        this.id = -1;
        this.clientKey = null;
        this.executorKey = null;
        this.productKey = 0;
        this.amount = 0;
        this.deliveryAddress = null;
        this.creationDate = null;
    }

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
        this.status = OrderStatus.WAITING_FOR_EMPLOYEE;
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
        this.deliveryDate = new Date().getTime();
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
}
