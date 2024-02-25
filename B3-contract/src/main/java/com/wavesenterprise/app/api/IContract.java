package com.wavesenterprise.app.api;

import com.wavesenterprise.app.dto.order.*;
import com.wavesenterprise.app.dto.product.*;
import com.wavesenterprise.app.dto.user.*;
import com.wavesenterprise.sdk.contract.api.annotation.ContractAction;
import com.wavesenterprise.sdk.contract.api.annotation.ContractInit;
import com.wavesenterprise.sdk.contract.api.annotation.InvokeParam;

public interface IContract {
    @ContractInit
    void init();

    @ContractAction
    void signUp(@InvokeParam (name = "registrationDTO") RegistrationDto registrationDTO) throws Exception;

    @ContractAction
    void activateUser(@InvokeParam (name = "activationDTO") ActivationDTO activationDTO) throws Exception;

    @ContractAction
    void blockUser(@InvokeParam (name = "blockDTO") BlockDTO blockDTO) throws Exception;

    @ContractAction
    void createProduct(@InvokeParam (name = "creationDTO") CreationDTO creationDTO) throws Exception;

    @ContractAction
    void confirmProduct(@InvokeParam (name = "confirmationDTO") ConfirmationDTO confirmationDTO) throws Exception;

    @ContractAction
    void makeOrder(@InvokeParam (name = "makeDTO") MakeDTO makeDTO) throws Exception;

    @ContractAction
    void clarifyOrder(@InvokeParam (name = "clarifyDTO") ClarifyDTO clarifyDTO) throws Exception;

    @ContractAction
    void confirmOrCancelOrder(@InvokeParam (name = "confirmOrCancelDTO") ConfirmOrCancelDTO confirmOrCancelDTO) throws Exception;

    @ContractAction
    void payOrder(@InvokeParam (name = "payDTO") PayDTO payDTO) throws Exception;

    @ContractAction
    void completeOrder(@InvokeParam (name = "completionDTO") CompletionDTO completionDTO) throws Exception;

    @ContractAction
    void takeOrder(@InvokeParam (name = "takeDTO") TakeDTO takeDTO) throws Exception;

    class Keys {
        public static final String CREATOR = "CREATOR";
        public static final String USERS_MAPPING_PREFIX = "USERS";
        public static final String ORDERS_LIST = "ORDERS";
        public static final String PRODUCTS_LIST = "PRODUCTS";
        public static final String ORGANIZATIONS_LIST = "ORGANIZATIONS";
    }

    class Roles {
        public static final String OPERATOR = "OPERATOR";
        public static final String DISTRIBUTOR = "DISTRIBUTOR";
        public static final String SUPPLIER = "SUPPLIER";
        public static final String CLIENT = "CLIENT";
    }

    class OrderStatuses {
        public static final String WAITING_FOR_CLIENT = "WAITING_FOR_CLIENT";
        public static final String WAITING_FOR_EMPLOYEE = "WAITING_FOR_EMPLOYEE";
        public static final String WAITING_FOR_PAYMENT = "WAITING_FOR_PAYMENT";
        public static final String EXECUTING = "EXECUTING";
        public static final String EXECUTING_PAID = "EXECUTING_PAID";
        public static final String CANCELLED = "CANCELLED";
        public static final String WAITING_FOR_TAKING = "WAITING_FOR_TAKING";
        public static final String TAKEN = "TAKEN";
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
