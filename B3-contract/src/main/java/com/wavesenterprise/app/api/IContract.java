package com.wavesenterprise.app.api;

import com.wavesenterprise.sdk.contract.api.annotation.ContractAction;
import com.wavesenterprise.sdk.contract.api.annotation.ContractInit;
import com.wavesenterprise.sdk.contract.api.annotation.InvokeParam;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Date;

public interface IContract {
    @ContractInit
    void init(@InvokeParam (name = "login") @NotNull String login);

    @ContractAction
    void changeRegions(
            @InvokeParam (name = "sender") @NotNull String sender,
            @InvokeParam (name = "regions") @NotNull String[] regions
    ) throws Exception;

    @ContractAction
    void signUp(
            @InvokeParam (name = "login") @NotNull String login,
            @InvokeParam (name = "title") @NotNull String title,
            @InvokeParam (name = "description") @NotNull String description,
            @InvokeParam (name = "fullName") @NotNull String fullName,
            @InvokeParam (name = "email") @NotNull String email,
            @InvokeParam (name = "regions") @NotNull String[] regions,
            @InvokeParam (name = "organizationKey") @Nullable String organizationKey
    ) throws Exception;

    @ContractAction
    void signUp(
            @InvokeParam (name = "login") @NotNull String login,
            @InvokeParam (name = "title") @NotNull String title,
            @InvokeParam (name = "fullName") @NotNull String fullName,
            @InvokeParam (name = "email") @NotNull String email,
            @InvokeParam (name = "regions") @NotNull String[] regions,
            @InvokeParam (name = "organizationKey") @Nullable String organizationKey
    ) throws Exception;

    @ContractAction
    void signUp(
            @InvokeParam (name = "login") @NotNull String login,
            @InvokeParam (name = "fullName") @NotNull String fullName,
            @InvokeParam (name = "email") @NotNull String email,
            @InvokeParam (name = "region") @NotNull String region
    ) throws Exception;

    @ContractAction
    void activateUser(
            @InvokeParam (name = "sender") @NotNull String sender,
            @InvokeParam (name = "userPublicKey") @NotNull String userPublicKey,
            @InvokeParam (name = "description") @Nullable String description,
            @InvokeParam (name = "fullName") @Nullable String fullName,
            @InvokeParam (name = "email") @Nullable String email,
            @InvokeParam (name = "regions") @Nullable String[] regions
    ) throws Exception;

    @ContractAction
    void blockUser(
            @InvokeParam (name = "sender") @NotNull String sender,
            @InvokeParam (name = "userPublicKey") @NotNull String userPublicKey
    ) throws Exception;

    @ContractAction
    void createProduct(
            @InvokeParam (name = "sender") @NotNull String sender,
            @InvokeParam (name = "title") @NotNull String title,
            @InvokeParam (name = "description") @NotNull String description,
            @InvokeParam (name = "regions") @NotNull String[] regions
    ) throws Exception;

    @ContractAction
    void confirmProduct(
            @InvokeParam (name = "sender") @NotNull String sender,
            @InvokeParam (name = "productKey") @NotNull String productKey,
            @InvokeParam (name = "description") @Nullable String description,
            @InvokeParam (name = "regions") @Nullable String[] regions,
            @InvokeParam (name = "minOrderCount") @NotNull Integer minOrderCount,
            @InvokeParam (name = "maxOrderCount") @NotNull Integer maxOrderCount,
            @InvokeParam (name = "distributors") @NotNull String[] distributors
    ) throws Exception;

    @ContractAction
    void makeOrder(
            @InvokeParam (name = "sender") @NotNull String sender,
            @InvokeParam (name = "productKey") @NotNull String productKey,
            @InvokeParam (name = "organization") @NotNull String organization,
            @InvokeParam (name = "count") @NotNull Integer count,
            @InvokeParam (name = "desiredDeliveryLimit") @NotNull Date desiredDeliveryLimit,
            @InvokeParam (name = "deliveryAddress") @NotNull String deliveryAddress
    ) throws Exception;

    @ContractAction
    void clarifyOrder(
            @InvokeParam (name = "sender") @NotNull String sender,
            @InvokeParam (name = "orderKey") @NotNull String orderKey,
            @InvokeParam (name = "totalPrice") @NotNull Integer totalPrice,
            @InvokeParam (name = "deliveryLimitUnixTime") @Nullable Integer deliveryLimitUnixTime,
            @InvokeParam (name = "isPrepaymentAvailable") @NotNull Boolean isPrepaymentAvailable
    ) throws Exception;

    @ContractAction
    void confirmOrCancelOrder(
            @InvokeParam (name = "sender") @NotNull String sender,
            @InvokeParam (name = "orderKey") @NotNull String orderKey,
            @InvokeParam (name = "isConfirm") @NotNull Boolean isConfirm
    ) throws Exception;

    @ContractAction
    void payOrder(
            @InvokeParam (name = "sender") @NotNull String sender,
            @InvokeParam (name = "orderKey") @NotNull String orderKey
    ) throws Exception;

    @ContractAction
    void completeOrder(
            @InvokeParam (name = "sender") @NotNull String sender,
            @InvokeParam (name = "orderKey") @NotNull String orderKey
    ) throws Exception;

    @ContractAction
    void takeOrder(
            @InvokeParam (name = "sender") @NotNull String sender,
            @InvokeParam (name = "orderKey") @NotNull String orderKey
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
