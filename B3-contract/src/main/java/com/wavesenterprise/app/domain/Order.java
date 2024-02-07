package com.wavesenterprise.app.domain;

import com.google.common.hash.Hashing;
import org.jetbrains.annotations.NotNull;

import java.nio.charset.StandardCharsets;
import java.util.Date;

import static com.wavesenterprise.app.api.IContract.Exceptions.INCORRECT_DATA;
import static com.wavesenterprise.app.api.IContract.Exceptions.NOT_ENOUGH_RIGHTS;

public class Order {
    private String hash;
    private final String clientKey;
    private final String organizationKey;
    private final String productKey;
    private final Integer count;
    private Integer price;
    private Date deliveryDate;
    private final String deliveryAddress;
    private final Date orderCreationDate;
    private OrderStatus status;
    private Boolean isPrepaymentAvailable;

    public Order() {
        this.clientKey = null;
        this.organizationKey = null;
        this.productKey = null;
        this.count = null;
        this.deliveryAddress = null;
        this.orderCreationDate = null;
    }

    public Order(
            @NotNull String clientKey,
            @NotNull String organizationKey,
            @NotNull String productKey,
            @NotNull Integer count,
            @NotNull Date deliveryDate,
            @NotNull String deliveryAddress
    ) {
        this.clientKey = clientKey;
        this.organizationKey = organizationKey;
        this.productKey = productKey;
        this.count = count;
        this.deliveryDate = deliveryDate;
        this.deliveryAddress = deliveryAddress;
        this.status = OrderStatus.WAITING_FOR_EMPLOYEE;
        this.orderCreationDate = new Date();
        updateHash();
    }

    private void updateHash() {
        String newHash = Hashing.sha256()
                .hashString(
                        this.hash
                        +this.clientKey
                        +this.organizationKey
                        +this.productKey
                        +this.count
                        +this.price
                        +this.deliveryDate
                        +this.deliveryAddress
                        +this.orderCreationDate
                        +this.status
                        +this.isPrepaymentAvailable,
                        StandardCharsets.UTF_8
                )
                .toString();
        this.hash = newHash;
    }

    public void clarify(
            @NotNull Integer totalPrice,
            @NotNull Date deliveryLimit,
            @NotNull Boolean isPrepaymentAvailable
    ) throws Exception {
        if (totalPrice < 1) {
            throw INCORRECT_DATA;
        }
        this.price = totalPrice;
        this.deliveryDate = deliveryLimit;
        this.isPrepaymentAvailable = isPrepaymentAvailable;
        updateHash();
    }

    public void cancel() {
        this.status = OrderStatus.CANCELLED;
        updateHash();
    }

    public void confirm() {
        this.status = OrderStatus.EXECUTING;
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

    public String getOrganizationKey() {
        return organizationKey;
    }

    public String getProductKey() {
        return productKey;
    }

    public int getCount() {
        return count;
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

    public Boolean getPrepaymentAvailable() {
        return isPrepaymentAvailable;
    }
}
