package com.wavesenterprise.app.app;

import com.wavesenterprise.app.api.IContract;
import com.wavesenterprise.app.domain.*;
import com.wavesenterprise.sdk.contract.api.annotation.ContractHandler;
import com.wavesenterprise.sdk.contract.api.state.ContractState;
import com.wavesenterprise.sdk.contract.api.state.TypeReference;
import com.wavesenterprise.sdk.contract.api.state.mapping.Mapping;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.*;

import static com.wavesenterprise.app.api.IContract.Keys.*;
import static com.wavesenterprise.app.api.IContract.Exceptions.*;

@ContractHandler
public class Contract implements IContract {
    private final ContractState contractState; // Хранит все значения контракта
    private final Mapping<User> userMapping; // Хранит всех пользователей
    private final Mapping<Order> orderMapping; // Хранит все заказы
    private final Mapping<Product> productMapping; // Хранит все продукты
    private final Mapping<List<String>> propertyMapping; // Хранит ключи всех продуктов пользователей
    private final Mapping<List<String>> employeesMapping; // Хранит ключи всех сотрудников организаций


    public Contract(ContractState contractState) {
        this.contractState = contractState;
        this.userMapping = contractState.getMapping(User.class, USERS_MAPPING_PREFIX);
        this.orderMapping = contractState.getMapping(Order.class, ORDERS_MAPPING_PREFIX);
        this.productMapping = contractState.getMapping(new TypeReference<>() {
        }, PRODUCTS_MAPPING_PREFIX);
        this.propertyMapping = contractState.getMapping(new TypeReference<>() {
        }, PROPERTY_MAPPING_PREFIX);
        this.employeesMapping = contractState.getMapping(new TypeReference<>() {
        }, EMPLOYEES_MAPPING_PREFIX);
    }

    /*
        CONTRACT METHODS
     */

    @Override
    public void init(@NotNull String login) {
        this.contractState.put(OPERATOR, login);
        userMapping.put(login, new User(login, null, null, null, null, null, UserRole.OPERATOR, null));
    }

    // Регистрация для поставщика (производителя)
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
        if (isPresent(organizationKey)) {
            onlyRole(organizationKey, UserRole.SUPPLIER);
        }
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

    // Регистрация для дистрибутора
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
        if (isPresent(organizationKey)) {
            onlyRole(organizationKey, UserRole.DISTRIBUTOR);
        }
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

    // Регистрация для конечного клиента
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
    public void registerUser(
            @NotNull String sender,
            @NotNull String userPublicKey,
            @Nullable String title,
            @Nullable String description,
            @Nullable String fullName,
            @Nullable String email,
            @Nullable String[] regions
    ) throws Exception {
        onlyRole(sender, UserRole.OPERATOR);
        userExist(userPublicKey);
        User user = userMapping.get(userPublicKey);
        if (user.isActivated()) {
            throw USER_ALREADY_REGISTERED;
        }

        if (isPresent(title)) {
            user.setTitle(title);
        }
        if (isPresent(description)) {
            user.setDescription(description);
        }
        if (isPresent(fullName)) {
            user.setFullName(fullName);
        }
        if (isPresent(email)) {
            user.setEmail(email);
        }
        if (isPresent(regions)) {
            user.setRegions(regions);
        }

        {
            String organizationKey = user.getOrganizationKey();
            if (isPresent(organizationKey)) {
                addToMappingStringList(employeesMapping, organizationKey, userPublicKey);
            }
        }

        user.setActivated(true);
        userMapping.put(userPublicKey, user);
    }

    @Override
    public void blockUser(
            @NotNull String sender,
            @NotNull String userPublicKey
    ) throws Exception {
        onlyRole(sender, UserRole.OPERATOR);
        userExist(userPublicKey);
        if (sender.equals(userPublicKey)) {
            throw NOT_ENOUGH_RIGHTS;
        }
        User user = userMapping.get(userPublicKey);
        if (user.isBlocked()) {
            throw new Exception("Пользователь уже заблокирован");
        }
        user.setBlocked(true);
        userMapping.put(userPublicKey, user);
    }

    @Override
    public void createProduct(
            @NotNull String sender,
            @NotNull String title,
            @NotNull String description,
            @NotNull String[] regions
    ) throws Exception {
        onlyRole(sender, UserRole.SUPPLIER);
        productNotCreated(title);
        productMapping.put(title, new Product(title, description, regions));
        addToMappingStringList(propertyMapping, sender, title);
    }

    @Override
    public void registerProduct(
            @NotNull String sender,
            @NotNull String productKey,
            @Nullable String description,
            @Nullable String[] regions,
            @NotNull Integer minOrderCount,
            @NotNull Integer maxOrderCount,
            @NotNull String[] distributors
    ) throws Exception {
        onlyRole(sender, UserRole.OPERATOR);
        productExist(productKey);
        Product product = productMapping.get(productKey);

        if (isPresent(description)) {
            product.setDescription(description);
        }
        if (isPresent(regions)) {
            if (regions.length == 0) {
                throw INCORRECT_DATA;
            }
            product.setRegions(regions);
        }

        product.setMinOrderCount(minOrderCount);
        product.setMaxOrderCount(maxOrderCount);

        if (distributors.length == 0) {
            throw INCORRECT_DATA;
        }

        // Проверка на то, реализуют ли дистрибуторы продукт в регионах, в которых он производится
        for (String distributor : distributors) {
            userExist(distributor);
            String[] distributorRegions = userMapping.get(distributor).getRegions();
            boolean notFound = true;
            for (String region : product.getRegions()) {
                if (Arrays.asList(distributorRegions).contains(region)) {
                    notFound = false;
                    break;
                }
            }
            if (notFound) {
                throw INCORRECT_DATA;
            }
        }
        product.setDistributors(distributors);
    }

    @Override
    public void makeOrder(
            @NotNull String sender,
            @NotNull String productKey,
            @NotNull String organizationKey,
            @NotNull Integer count,
            @NotNull Date desiredDeliveryLimit,
            @NotNull String deliveryAddress
    ) throws Exception {
        userNotBlocked(sender);
        userNotBlocked(organizationKey);
        haveProduct(organizationKey, productKey);
        User user = userMapping.get(sender);
        if (isPresent(user.getOrganizationKey())) {
            sender = user.getOrganizationKey();
        }

        if (user.getRole() == UserRole.OPERATOR || user.getRole() == UserRole.DISTRIBUTOR) {
            onlyRole(organizationKey, UserRole.SUPPLIER);
        } else {
            onlyRole(sender, UserRole.CLIENT);
            onlyRole(organizationKey, UserRole.DISTRIBUTOR);
        }

        Order order = new Order(sender, organizationKey, productKey, count, desiredDeliveryLimit, deliveryAddress);
        String hash = "hash"; // ToDo: hash method
        orderMapping.put(hash, order);
    }

    @Override
    public void clarifyOrder(
            @NotNull String sender,
            @NotNull String orderKey,
            @NotNull Integer totalPrice,
            @Nullable Integer deliveryLimitUnixTime,
            @NotNull Boolean isPrepaymentAvailable
    ) throws Exception {
        onlyOrganization(sender, orderKey);
        onlyOrderStatus(orderKey, OrderStatus.WAITING_FOR_EMPLOYEE);
        Order order = orderMapping.get(orderKey);

        order.setPrice(totalPrice);
        order.setPrepaymentAvailable(isPrepaymentAvailable);
        if (isPresent(deliveryLimitUnixTime)) {
            order.setDeliveryDate(new Date(deliveryLimitUnixTime));
        }

        orderMapping.put(orderKey, order);
    }

    @Override
    public void cancelOrder(
            @NotNull String sender,
            @NotNull String orderKey
    ) throws Exception {
        onlyClient(sender, orderKey);
        onlyOrderStatus(orderKey, OrderStatus.WAITING_FOR_CLIENT);
        Order order = orderMapping.get(orderKey);
        order.setStatus(OrderStatus.CANCELLED);
        orderMapping.put(orderKey, order);
    }

    @Override
    public void confirmOrder(
            @NotNull String sender,
            @NotNull String orderKey
    ) throws Exception {
        onlyClient(sender, orderKey);
        onlyOrderStatus(orderKey, OrderStatus.WAITING_FOR_CLIENT);
        Order order = orderMapping.get(orderKey);
        order.setStatus(OrderStatus.EXECUTING);
        orderMapping.put(orderKey, order);
    }

    @Override
    public void payOrder(
            @NotNull String sender,
            @NotNull String orderKey
    ) throws Exception {
        onlyClient(sender, orderKey);
        Order order = orderMapping.get(orderKey);
        if (order.getStatus() == OrderStatus.WAITING_FOR_CLIENT) {
            if (order.isPrepaymentAvailable()) {
                throw NOT_ENOUGH_RIGHTS;
            }
            order.setStatus(OrderStatus.EXECUTING_PAID);
        } else {
            onlyOrderStatus(orderKey, OrderStatus.WAITING_FOR_PAYMENT);
            order.setStatus(OrderStatus.WAITING_FOR_TAKING);
        }
        orderMapping.put(orderKey, order);
    }

    @Override
    public void completeOrder(
            @NotNull String sender,
            @NotNull String orderKey
    ) throws Exception {
        onlyOrganization(sender, orderKey);
        Order order = orderMapping.get(orderKey);
        order.setDeliveryDate(new Date());
        if (order.getStatus() == OrderStatus.EXECUTING) {
            order.setStatus(OrderStatus.WAITING_FOR_PAYMENT);
        } else if (order.getStatus() == OrderStatus.EXECUTING_PAID) {
            order.setStatus(OrderStatus.WAITING_FOR_TAKING);
        } else {
            throw NOT_ENOUGH_RIGHTS;
        }
        orderMapping.put(orderKey, order);
    }

    @Override
    public void takeOrder(
            @NotNull String sender,
            @NotNull String orderKey
    ) throws Exception {
        onlyClient(sender, orderKey);
        onlyOrderStatus(orderKey, OrderStatus.WAITING_FOR_TAKING);
        User user = userMapping.get(sender);
        if (isPresent(user.getOrganizationKey())) {
            user = userMapping.get(user.getOrganizationKey());
        }
        // ToDo: добавление продукта покупателю
        Order order = orderMapping.get(orderKey);
        order.setStatus(OrderStatus.TAKEN);
        orderMapping.put(orderKey, order);
    }

    /*
        PRIVATE METHODS
     */

    private void userExist(String userPublicKey) throws Exception {
        if (!userMapping.has(userPublicKey)) {
            throw USER_NOT_FOUND;
        }
    }

    public void userNotBlocked(String userPublicKey) throws Exception {
        userExist(userPublicKey);
        if (userMapping.get(userPublicKey).isBlocked()) {
            throw USER_IS_BLOCKED;
        }
    }

    private void notSignedUp(String userPublicKey) throws Exception {
        if (userMapping.has(userPublicKey)) {
            throw USER_ALREADY_SIGNED_UP;
        }
    }

    private void onlyRole(String userPublicKey, UserRole role) throws Exception {
        userNotBlocked(userPublicKey);
        UserRole userRole = userMapping.get(userPublicKey).getRole();
        if (userRole != role) {
            if (role != UserRole.DISTRIBUTOR || userRole != UserRole.OPERATOR) {
                throw NOT_ENOUGH_RIGHTS;
            }
        }
    }

    private void orderExist(String orderKey) throws Exception {
        if (!orderMapping.has(orderKey)) {
            throw ORDER_NOT_FOUND;
        }
    }

    private void onlyOrderStatus(String orderKey, OrderStatus status) throws Exception {
        orderExist(orderKey);
        if (orderMapping.get(orderKey).getStatus() != status) {
            throw NOT_ENOUGH_RIGHTS;
        }
    }

    private void onlyClient(String userPublicKey, String orderKey) throws Exception {
        userNotBlocked(userPublicKey);
        orderExist(orderKey);
        String clientKey = orderMapping.get(orderKey).getClientKey();
        if (!Objects.equals(userMapping.get(userPublicKey).getOrganizationKey(), clientKey) && !Objects.equals(userPublicKey, clientKey)) {
            throw NOT_ENOUGH_RIGHTS;
        }
    }

    private void onlyOrganization(String userPublicKey, String orderKey) throws Exception {
        userNotBlocked(userPublicKey);
        orderExist(orderKey);
        String organizationKey = orderMapping.get(orderKey).getOrganizationKey();
        if (!Objects.equals(userMapping.get(userPublicKey).getOrganizationKey(), organizationKey) && !Objects.equals(userPublicKey, organizationKey)) {
            throw NOT_ENOUGH_RIGHTS;
        }
    }

    private void productNotCreated(String productKey) throws Exception {
        if (productMapping.has(productKey)) {
            throw PRODUCT_ALREADY_EXIST;
        }
    }

    private void productExist(String productKey) throws Exception {
        if (!productMapping.has(productKey)) {
            throw PRODUCT_NOT_FOUND;
        }
    }

    private void haveProduct(String userPublicKey, String productKey) throws Exception {
        if (propertyMapping.has(userPublicKey)) {
            if (!propertyMapping.get(userPublicKey).contains(productKey)) {
                throw NO_PRODUCT;
            }
        } else {
            throw NO_PRODUCT;
        }
    }

    private boolean isPresent(Object o) {
        return (o != null);
    }

    private boolean isPresent(String s) {
        return (s != null && !s.equals(""));
    }

    private void addToMappingStringList(Mapping<List<String>> mapping, String key, String str) {
        if (mapping.has(key)) {
            List<String> list = mapping.get(key);
            list.add(str);
            mapping.put(key, list);
        } else {
            mapping.put(key, new ArrayList<>(List.of(str)));
        }
    }
}
