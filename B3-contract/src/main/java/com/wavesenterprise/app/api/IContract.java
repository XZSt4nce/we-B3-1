package com.wavesenterprise.app.api;

import com.wavesenterprise.app.domain.UserRole;
import com.wavesenterprise.sdk.contract.api.annotation.ContractAction;
import com.wavesenterprise.sdk.contract.api.annotation.ContractInit;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Date;

public interface IContract {
    @ContractInit
    void init();

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
    void registerUser(
            @NotNull String sender,
            @NotNull String userPublicKey,
            @Nullable String login,
            @Nullable String title,
            @Nullable String description,
            @Nullable String fullName,
            @Nullable String email,
            @Nullable String[] regions,
            @Nullable UserRole role,
            @Nullable String organizationKey
    ) throws Exception;

    @ContractAction
    void blockUser(
            @NotNull String sender,
            @NotNull String userPublicKey
    );

    @ContractAction
    void createProduct(
            @NotNull String sender,
            @NotNull String title,
            @NotNull String description,
            @NotNull String[] regions
    ) throws Exception;

    @ContractAction
    void registerProduct(
            @NotNull String sender,
            @NotNull String productKey,
            @Nullable String title,
            @Nullable String description,
            @Nullable String[] regions,
            @NotNull Integer minOrderCount,
            @NotNull Integer maxOrderCount,
            @NotNull String[] distributors
    );

    @ContractAction
    void makeOrder(
            @NotNull String sender,
            @NotNull String productKey,
            @NotNull String organization,
            @NotNull Integer count,
            @NotNull Date desiredDeliveryLimit,
            @NotNull String deliveryAddress
    );

    @ContractAction
    void confirmOrder(
            @NotNull String sender,
            @NotNull String orderKey,
            @NotNull Integer totalPrice,
            @NotNull Integer deliveryLimitUnixTime,
            @NotNull Boolean isPrepaymentPossible
    );

    @ContractAction
    void cancelOrder(
            @NotNull String sender,
            @NotNull String orderKey
    );

    @ContractAction
    void completeOrderStage(
            @NotNull String sender,
            @NotNull String orderKey
    );

    @ContractAction
    void addUser(
            String email,
            String login,
            String password
    );

    class Keys {
        public static final String OPERATOR = "OPERATOR";
        public static final String USERS_MAPPING_PREFIX = "USERS";
        public static final String ORDERS_MAPPING_PREFIX = "ORDERS";
        public static final String EMPLOYEES_MAPPING_PREFIX = "EMPLOYEES";
    }
}
