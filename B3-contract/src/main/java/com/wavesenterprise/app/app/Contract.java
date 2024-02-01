package com.wavesenterprise.app.app;

import com.wavesenterprise.app.api.IContract;
import com.wavesenterprise.app.domain.Order;
import com.wavesenterprise.app.domain.User;
import com.wavesenterprise.app.domain.UserRole;
import com.wavesenterprise.sdk.contract.api.annotation.ContractHandler;
import com.wavesenterprise.sdk.contract.api.domain.ContractCall;
import com.wavesenterprise.sdk.contract.api.state.ContractState;
import com.wavesenterprise.sdk.contract.api.state.mapping.Mapping;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Date;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

import static com.wavesenterprise.app.api.IContract.Keys.*;

@ContractHandler
public class Contract implements IContract {
    private final ContractState contractState;
    private final ContractCall contractCall;
    private final Mapping<User> userMapping;
    private final Mapping<Order> orderMapping;
    private final Mapping<List> employeesMapping;


    public Contract(ContractState contractState, ContractCall contractCall) {
        this.contractState = contractState;
        this.contractCall = contractCall;
        this.userMapping = contractState.getMapping(User.class, USERS_MAPPING_PREFIX);
        this.orderMapping = contractState.getMapping(Order.class, ORDERS_MAPPING_PREFIX);
        this.employeesMapping = contractState.getMapping(List.class, EMPLOYEES_MAPPING_PREFIX);
    }

    @Override
    public void init() {
        this.contractState.put(OPERATOR, contractCall.getCaller());
    }

    @Override
    public void signUp(
            @NotNull String login,
            @NotNull String title,
            @NotNull String description,
            @NotNull String fullName,
            @NotNull String email,
            @NotNull String[] regions,
            @Nullable String organizationKey
    ) throws Exception {
        notSignedUp(login);
        userMapping.put(login, new User(
                login,
                title,
                description,
                fullName,
                email,
                regions,
                UserRole.SUPPLIER,
                organizationKey
        ));
    }

    @Override
    public void signUp(
            @NotNull String login,
            @NotNull String title,
            @NotNull String fullName,
            @NotNull String email,
            @NotNull String[] regions,
            @Nullable String organizationKey
    ) throws Exception {
        notSignedUp(login);
        userMapping.put(login, new User(
                login,
                null,
                null,
                fullName,
                email,
                regions,
                UserRole.DISTRIBUTOR,
                organizationKey
        ));
    }

    @Override
    public void signUp(
            @NotNull String login,
            @NotNull String fullName,
            @NotNull String email,
            @NotNull String region
    ) throws Exception {
        notSignedUp(login);
        userMapping.put(login, new User(
                login,
                null,
                null,
                fullName,
                email,
                new String[]{region},
                UserRole.CLIENT,
                null
        ));
    }

    @Override
    public void createProduct(
            @NotNull String sender,
            @NotNull String title,
            @NotNull String description,
            @NotNull String[] regions
    ) throws Exception {
        onlyRole(sender, UserRole.SUPPLIER);
    }

    @Override
    public void makeOrder(
            @NotNull String sender,
            @NotNull String productKey,
            @NotNull String organization,
            @NotNull Integer count,
            @NotNull Date desiredDeliveryLimit,
            @NotNull String deliveryAddress
    ) {

    }

    @Override
    public void confirmOrder(
            @NotNull String sender,
            @NotNull String orderKey,
            @NotNull Integer totalPrice,
            @NotNull Integer deliveryLimitUnixTime,
            @NotNull Boolean isPrepaymentPossible
    ) {

    }

    @Override
    public void cancelOrder(
            @NotNull String sender,
            @NotNull String orderKey
    ) {

    }

    @Override
    public void completeOrderStage(
            @NotNull String sender,
            @NotNull String orderKey
    ) {

    }

    @Override
    public void addUser(String email, String login, String password) {
        userMapping.put(login, new User(login, password, null, null, email, null, null, null));
    }

    @Override
    public void registerUser(
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
    ) throws Exception {
        onlyRole(sender, UserRole.OPERATOR);
    }

    @Override
    public void blockUser(
            @NotNull String sender,
            @NotNull String userPublicKey
    ) {

    }

    @Override
    public void registerProduct(
            @NotNull String sender,
            @NotNull String productKey,
            @Nullable String title,
            @Nullable String description,
            @Nullable String[] regions,
            @NotNull Integer minOrderCount,
            @NotNull Integer maxOrderCount,
            @NotNull String[] distributors
    ) {

    }

    private void onlyRole(String sender, UserRole role) throws Exception {
        Optional<User> user = userMapping.tryGet(sender);
        if (user.isPresent()) {
            if (user.get().getRole() != role) {
                throw new Exception("Недостаточно прав");
            }
        } else {
            throw new Exception("Пользователь не найден");
        }
    }

    private void onlyEmployee(String sender, String organizationKey) throws Exception {
        Optional<User> user = userMapping.tryGet(sender);
        if (user.isPresent()) {
            if (!Objects.equals(user.get().getOrganizationKey(), organizationKey)) {
                throw new Exception("Недостаточно прав");
            }
        } else {
            throw new Exception("Пользователь не найден");
        }
    }

    private void notSignedUp(String login) throws Exception {
        Optional<User> user = userMapping.tryGet(login);
        if (user.isPresent()) {
            throw new Exception("Пользователь с таким логином уже существует");
        }
    }
}
