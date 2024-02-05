package com.wavesenterprise.app.api;

import com.wavesenterprise.sdk.contract.api.annotation.ContractAction;
import com.wavesenterprise.sdk.contract.api.annotation.ContractInit;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Date;

public interface IContract {
    @ContractInit
    void init(@NotNull String login);

    @ContractAction
    void signUp(
            @NotNull String login,
            @NotNull String title,
            @NotNull String description,
            @NotNull String fullName,
            @NotNull String email,
            @NotNull String[] regions,
            @Nullable String organizationKey
    ) throws Exception;

    @ContractAction
    void signUp(
            @NotNull String login,
            @NotNull String title,
            @NotNull String fullName,
            @NotNull String email,
            @NotNull String[] regions,
            @Nullable String organizationKey
    ) throws Exception;

    @ContractAction
    void signUp(
            @NotNull String login,
            @NotNull String fullName,
            @NotNull String email,
            @NotNull String region
    ) throws Exception;

    @ContractAction
    void activateUser(
            @NotNull String sender,
            @NotNull String userPublicKey,
            @Nullable String description,
            @Nullable String fullName,
            @Nullable String email,
            @Nullable String[] regions
    ) throws Exception;

    @ContractAction
    void blockUser(
            @NotNull String sender,
            @NotNull String userPublicKey
    ) throws Exception;

    @ContractAction
    void createProduct(
            @NotNull String sender,
            @NotNull String title,
            @NotNull String description,
            @NotNull String[] regions
    ) throws Exception;

    @ContractAction
    void confirmProduct(
            @NotNull String sender,
            @NotNull String productKey,
            @Nullable String description,
            @Nullable String[] regions,
            @NotNull Integer minOrderCount,
            @NotNull Integer maxOrderCount,
            @NotNull String[] distributors
    ) throws Exception;

    @ContractAction
    void makeOrder(
            @NotNull String sender,
            @NotNull String productKey,
            @NotNull String organization,
            @NotNull Integer count,
            @NotNull Date desiredDeliveryLimit,
            @NotNull String deliveryAddress
    ) throws Exception;

    @ContractAction
    void clarifyOrder(
            @NotNull String sender,
            @NotNull String orderKey,
            @NotNull Integer totalPrice,
            @Nullable Integer deliveryLimitUnixTime,
            @NotNull Boolean isPrepaymentAvailable
    ) throws Exception;

    @ContractAction
    void confirmOrCancelOrder(
            @NotNull String sender,
            @NotNull String orderKey,
            @NotNull Boolean isConfirm
    ) throws Exception;

    @ContractAction
    void payOrder(
            @NotNull String sender,
            @NotNull String orderKey
    ) throws Exception;

    @ContractAction
    void completeOrder(
            @NotNull String sender,
            @NotNull String orderKey
    ) throws Exception;

    @ContractAction
    void takeOrder(
            @NotNull String sender,
            @NotNull String orderKey
    ) throws Exception;

    class Keys {
        public static final String OPERATOR = "OPERATOR";
        public static final String USERS_MAPPING_PREFIX = "USERS";
        public static final String ORDERS_MAPPING_PREFIX = "ORDERS";
        public static final String PRODUCTS_MAPPING_PREFIX = "PRODUCTS";
        public static final String EMPLOYEES_MAPPING_PREFIX = "EMPLOYEES";
    }

    class Exceptions {
        public static final Exception USER_NOT_FOUND = new Exception("Пользователь не найден");
        public static final Exception USER_ALREADY_ACTIVATED = new Exception("Учётная запись пользователя уже активирована");
        public static final Exception USER_ALREADY_SIGNED_UP = new Exception("Пользователь с таким логином уже существует");
        public static final Exception NOT_ENOUGH_RIGHTS = new Exception("Недостаточно прав");
        public static final Exception ORDER_NOT_FOUND = new Exception("Заказ не найден");
        public static final Exception USER_IS_BLOCKED = new Exception("Пользователь заблокирован");
        public static final Exception PRODUCT_ALREADY_EXIST = new Exception("Продукт уже существует");
        public static final Exception PRODUCT_NOT_FOUND = new Exception("Продукт не найдён");
        public static final Exception NOT_ENOUGH_PRODUCTS = new Exception("У организации недостаточно продукта");
        public static final Exception INCORRECT_DATA = new Exception("Введены некорректные данные");
        public static final Exception TOO_FEW_PRODUCTS = new Exception("Слишком мало продуктов заказано");
        public static final Exception TOO_MANY_PRODUCTS = new Exception("Слишком много продуктов заказано");
        public static final Exception NOT_ENOUGH_FUNDS = new Exception("Недостаточно средств");
        public static final Exception CANNOT_SELL_PRODUCT = new Exception("Один из дистрибуторов не может реализовать продукт");
    }
}
