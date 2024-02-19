package com.wavesenterprise.app.api;

import com.wavesenterprise.sdk.contract.api.annotation.ContractAction;
import com.wavesenterprise.sdk.contract.api.annotation.ContractInit;
import com.wavesenterprise.sdk.contract.api.annotation.InvokeParam;

public interface IContract {
    @ContractInit
    void init() throws Exception;

    @ContractAction
    void signUp(
            @InvokeParam (name = "login") String login,
            @InvokeParam (name = "password") String password,
            @InvokeParam (name = "title") String title,
            @InvokeParam (name = "description") String description,
            @InvokeParam (name = "fullName") String fullName,
            @InvokeParam (name = "email") String email,
            @InvokeParam (name = "regions") String[] regions,
            @InvokeParam (name = "organizationKey") Integer organizationKey
    ) throws Exception;

    @ContractAction
    void activateUser(
            @InvokeParam (name = "sender") String sender,
            @InvokeParam (name = "password") String password,
            @InvokeParam (name = "userPublicKey") String userPublicKey,
            @InvokeParam (name = "fullName") String fullName,
            @InvokeParam (name = "email") String email,
            @InvokeParam (name = "regions") String[] regions
    ) throws Exception;

    @ContractAction
    void blockUser(
            @InvokeParam (name = "sender") String sender,
            @InvokeParam (name = "password") String password,
            @InvokeParam (name = "userPublicKey") String userPublicKey
    ) throws Exception;

    @ContractAction
    void createProduct(
            @InvokeParam (name = "sender") String sender,
            @InvokeParam (name = "password") String password,
            @InvokeParam (name = "title") String title,
            @InvokeParam (name = "description") String description,
            @InvokeParam (name = "regions") String[] regions
    ) throws Exception;

    @ContractAction
    void confirmProduct(
            @InvokeParam (name = "sender") String sender,
            @InvokeParam (name = "password") String password,
            @InvokeParam (name = "productKey") int productKey,
            @InvokeParam (name = "description") String description,
            @InvokeParam (name = "regions") String[] regions,
            @InvokeParam (name = "minOrderCount") int minOrderCount,
            @InvokeParam (name = "maxOrderCount") int maxOrderCount,
            @InvokeParam (name = "distributors") String[] distributors
    ) throws Exception;

    @ContractAction
    void makeOrder(
            @InvokeParam (name = "sender") String sender,
            @InvokeParam (name = "password") String password,
            @InvokeParam (name = "productKey") int productKey,
            @InvokeParam (name = "organization") String organization,
            @InvokeParam (name = "count") int count,
            @InvokeParam (name = "desiredDeliveryLimit") long desiredDeliveryLimit,
            @InvokeParam (name = "deliveryAddress") String deliveryAddress
    ) throws Exception;

    @ContractAction
    void clarifyOrder(
            @InvokeParam (name = "sender") String sender,
            @InvokeParam (name = "password") String password,
            @InvokeParam (name = "orderKey") int orderKey,
            @InvokeParam (name = "totalPrice") int totalPrice,
            @InvokeParam (name = "deliveryLimit") long deliveryLimit,
            @InvokeParam (name = "isPrepaymentAvailable") boolean isPrepaymentAvailable
    ) throws Exception;

    @ContractAction
    void confirmOrCancelOrder(
            @InvokeParam (name = "sender") String sender,
            @InvokeParam (name = "password") String password,
            @InvokeParam (name = "orderKey") int orderKey,
            @InvokeParam (name = "isConfirm") boolean isConfirm
    ) throws Exception;

    @ContractAction
    void payOrder(
            @InvokeParam (name = "sender") String sender,
            @InvokeParam (name = "password") String password,
            @InvokeParam (name = "orderKey") int orderKey
    ) throws Exception;

    @ContractAction
    void completeOrder(
            @InvokeParam (name = "sender") String sender,
            @InvokeParam (name = "password") String password,
            @InvokeParam (name = "orderKey") int orderKey
    ) throws Exception;

    @ContractAction
    void takeOrder(
            @InvokeParam (name = "sender") String sender,
            @InvokeParam (name = "password") String password,
            @InvokeParam (name = "orderKey") int orderKey
    ) throws Exception;

    class Keys {
        public static final String OPERATOR = "OPERATOR";
        public static final String USERS_MAPPING_PREFIX = "USERS";
        public static final String ORDERS_LIST = "ORDERS";
        public static final String PRODUCTS_LIST = "PRODUCTS";
        public static final String ORGANIZATIONS_LIST = "ORGANIZATIONS";
    }

    class Exceptions {
        public static final Exception USER_NOT_FOUND = new Exception("Пользователь не найден");
        public static final Exception ORGANIZATION_NOT_FOUND = new Exception("Организация не найдена");
        public static final Exception USER_ALREADY_ACTIVATED = new Exception("Учётная запись пользователя уже активирована");
        public static final Exception USER_ALREADY_SIGNED_UP = new Exception("Пользователь с таким логином уже существует");
        public static final Exception NOT_ENOUGH_RIGHTS = new Exception("Недостаточно прав");
        public static final Exception ORDER_NOT_FOUND = new Exception("Заказ не найден");
        public static final Exception USER_IS_BLOCKED = new Exception("Пользователь заблокирован");
        public static final Exception USER_IS_NOT_ACTIVATED = new Exception("Пользователь не активирован");
        public static final Exception PRODUCT_NOT_FOUND = new Exception("Продукт не найдён");
        public static final Exception NOT_ENOUGH_PRODUCTS = new Exception("У исполнителя недостаточно продукта");
        public static final Exception INCORRECT_DATA = new Exception("Введены некорректные данные");
        public static final Exception TOO_FEW_PRODUCTS = new Exception("Слишком мало продуктов заказано");
        public static final Exception TOO_MANY_PRODUCTS = new Exception("Слишком много продуктов заказано");
        public static final Exception NOT_ENOUGH_FUNDS = new Exception("Недостаточно средств");
        public static final Exception CANNOT_SELL_PRODUCT = new Exception("Дистрибутор не может реализовать продукт");
        public static final Exception INCORRECT_DATA_REGIONS = new Exception("Введены некорректные данные (регионы)");
        public static final Exception INCORRECT_LOGIN_OR_PASSWORD = new Exception("Введённый логин/пароль неверен");
    }
}
