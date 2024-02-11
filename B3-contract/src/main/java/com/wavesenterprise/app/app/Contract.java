package com.wavesenterprise.app.app;

import com.google.common.hash.Hashing;
import com.wavesenterprise.app.api.IContract;
import com.wavesenterprise.app.domain.*;
import com.wavesenterprise.sdk.contract.api.annotation.ContractHandler;
import com.wavesenterprise.sdk.contract.api.state.ContractState;
import com.wavesenterprise.sdk.contract.api.state.mapping.Mapping;

import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.Objects;

import static com.wavesenterprise.app.api.IContract.Exceptions.*;
import static com.wavesenterprise.app.api.IContract.Keys.*;

@ContractHandler
public class Contract implements IContract {
    private final ContractState contractState; // Хранит все значения контракта
    private final Mapping<User> userMapping; // Хранит всех пользователей
    private final Mapping<Order> orderMapping; // Хранит все заказы
    private final Mapping<Product> productMapping; // Хранит все продукты
    private final Mapping<Organization> organizationMapping; // Хранит все организации


    public Contract(ContractState contractState) {
        this.contractState = contractState;
        this.userMapping = contractState.getMapping(User.class, USERS_MAPPING_PREFIX);
        this.orderMapping = contractState.getMapping(Order.class, ORDERS_MAPPING_PREFIX);
        this.productMapping = contractState.getMapping(Product.class, PRODUCTS_MAPPING_PREFIX);
        this.organizationMapping = contractState.getMapping(Organization.class, ORGANIZATIONS_MAPPING_PREFIX);
    }

    /*
        CONTRACT METHODS
     */

    // Метод деплоя контракта
    @Override
    public void init() {
        String login = "operator";
        String password = "123";
        String organizationTitle = "Profi";
        String organizationDescription = "Разработка решений с использованием блочейн технологий";
        this.contractState.put(OPERATOR, login);
        this.contractState.put(LAST_PRODUCT_ID, 0);
        this.contractState.put(LAST_ORDER_ID, 0);
        this.contractState.put(LAST_ORGANIZATION_ID, 1);
        Organization organization = new Organization(
                login,
                organizationDescription,
                UserRole.DISTRIBUTOR
        );
        organizationMapping.put("0", organization);
        User operator = new User(
                login,
                hashPassword(login, password),
                "admin",
                "admin@adm.in",
                new String[]{"США", "Индия", "Япония"},
                organizationTitle,
                UserRole.OPERATOR
        );
        userMapping.put(login, operator);
    }

    // Метод регистрации
    @Override
    public void signUp(
            String login,
            String password,
            String title,
            String description,
            String fullName,
            String email,
            String[] regions,
            String organizationKey
    ) throws Exception {
        notSignedUp(login); // Проверка, что пользователя с таким логином нет в системе
        UserRole role = UserRole.CLIENT;

        if (isPresent(organizationKey)) {
            if (isPresent(title) || isPresent(description)) {
                throw INCORRECT_DATA;
            }
            Organization organization = organizationExist(organizationKey);
            role = organization.getRole();
        } else if (isPresent(title)) {
            if (isPresent(organizationKey)) {
                throw INCORRECT_DATA;
            } else if (isPresent(description)) {
                role = UserRole.SUPPLIER;
            } else {
                role = UserRole.DISTRIBUTOR;
            }
            int lastOrganizationId = contractState.get(LAST_ORGANIZATION_ID, int.class);
            Organization organization = new Organization(
                    login,
                    description,
                    role
            );
            organizationMapping.put(String.valueOf(lastOrganizationId), organization);
            contractState.put(LAST_ORGANIZATION_ID, lastOrganizationId + 1);
        } else if (isPresent(description)) {
            throw INCORRECT_DATA;
        }

        // Добавление пользователя в систему
        userMapping.put(login, new User(
                login,
                hashPassword(login, password),
                fullName,
                email,
                regions,
                organizationKey,
                role
        ));
    }

    // Метод активации пользователя в системе
    @Override
    public void activateUser(
            String sender,
            String password,
            String userPublicKey,
            String fullName,
            String email,
            String[] regions
    ) throws Exception {
        User user = userHaveAccess(sender, password); // Имеет ли пользователь доступ к системе
        onlyRole(sender, UserRole.OPERATOR); // Метод может вызвать только оператор
        userExist(userPublicKey);

        // Если пользователь уже активирован, то отменить выполнение метода
        if (user.isActivated()) {
            throw USER_ALREADY_ACTIVATED;
        }

        {
            // Если оператор редактировал некоторые поля учётной записи, то перезаписать их
            fullName = isPresent(fullName) ? fullName : user.getFullName();
            email = isPresent(email) ? email : user.getEmail();
            regions = isPresent(regions) ? regions : user.getRegions();
        }

        {
            // Если пользователь указывал ключ организации, то добавить ключ пользователя к списку сотрудников организации
            String organizationKey = user.getOrganizationKey();
            if (isPresent(organizationKey)) {
                Organization organization = organizationMapping.get(organizationKey);
                organization.addEmployee(userPublicKey);
                organizationMapping.put(organizationKey, organization);
            }
        }

        user.activate(fullName, email, regions); // Активация пользователя
        userMapping.put(userPublicKey, user); // Обновление учётной записи пользователя в системе
    }

    // Метод блокировки пользователя
    @Override
    public void blockUser(
            String sender,
            String password,
            String userPublicKey
    ) throws Exception {
        User user = userHaveAccess(sender, password); // Имеет ли пользователь доступ к системе
        onlyRole(sender, UserRole.OPERATOR); // Выполнять метод может только оператор
        userExist(userPublicKey);

        // Если оператор пытается заблокировать сам себя, то отказать в выполнении метода
        if (sender.equals(userPublicKey)) {
            throw NOT_ENOUGH_RIGHTS;
        }

        user.block(); // Блокировка пользователя
        userMapping.put(userPublicKey, user); // Обновление учётной записи пользователя в системе
    }

    // Метод создания карточки продукта
    @Override
    public void createProduct(
            String sender,
            String password,
            String title,
            String description,
            String[] regions
    ) throws Exception {
        userHaveAccess(sender, password); // Имеет ли пользователь доступ к системе
        onlyRole(sender, UserRole.SUPPLIER); // Выполнять метод может только поставщик
        productNotCreated(title); // Проверка на то, что такого продукта ещё нет в системе
        int lastProductId = contractState.get(LAST_PRODUCT_ID, int.class);
        productMapping.put(String.valueOf(lastProductId), new Product(sender, title, description, regions)); // Добавление продукта в систему
        contractState.put(LAST_PRODUCT_ID, lastProductId + 1);
    }

    // Метод подтверждения карточки продукта
    @Override
    public void confirmProduct(
            String sender,
            String password,
            String productKey,
            String description,
            String[] regions,
            int minOrderCount,
            int maxOrderCount,
            String[] distributors
    ) throws Exception {
        userHaveAccess(sender, password); // Имеет ли пользователь доступ к системе
        onlyRole(sender, UserRole.OPERATOR); // Выполнять метод может только оператор
        Product product = productExist(productKey); // Проверка на то, что продукт существует в системе

        // Если список дистрибуторов пустой, то отказать в выполнении метода
        if (distributors.length == 0) {
            throw INCORRECT_DATA;
        }

        // Проверка на то, существуют ли дистрибуторы и могут ли они реализовать продукт в регионах, в которых он производится
        for (String distributor : distributors) {
            User userDistributor = userExist(distributor);
            List<String> distributorRegions = Arrays.asList(userMapping.get(distributor).getRegions());
            if (distributorRegions
                    .stream()
                    .filter(Arrays.asList(regions)::contains).toList()
                    .isEmpty()
            ) {
                throw CANNOT_SELL_PRODUCT;
            }
            userDistributor.addProductProvided(productKey);
            userMapping.put(distributor, userDistributor);
        }

        // Если количество товара за один заказ минимальное больше максимального, то отказать в выполнении метода
        if (minOrderCount > maxOrderCount) {
            throw INCORRECT_DATA;
        }

        // Если оператор редактировал некоторые параметры продукта, то перезаписать их
        description = isPresent(description) ? description : product.getDescription();
        regions = isPresent(regions) ? regions : product.getRegions();

        product.confirm(description, regions, minOrderCount, maxOrderCount, distributors); // Подтверждение продукта
        productMapping.put(productKey, product); // Обновление продукта в системе
    }

    // Метод создания заказа
    @Override
    public void makeOrder(
            String sender,
            String password,
            String productKey,
            String executorKey,
            int count,
            Date desiredDeliveryLimit,
            String deliveryAddress
    ) throws Exception {
        User user = userHaveAccess(sender, password);
        userExist(executorKey);
        Product product = productExist(productKey);
        haveProductRegion(sender, productKey);

        if (count == 0) {
            throw INCORRECT_DATA;
        } else if (product.getMinOrderCount() != 0 && count < product.getMinOrderCount()) {
            throw TOO_FEW_PRODUCTS;
        } else if (product.getMaxOrderCount() != 0 && count > product.getMaxOrderCount()) {
            throw TOO_MANY_PRODUCTS;
        }

        if (user.getRole() == UserRole.CLIENT) {
            onlyRole(executorKey, UserRole.DISTRIBUTOR);
            approvedDistributor(executorKey, productKey);
        } else {
            onlyRole(sender, UserRole.DISTRIBUTOR);
            onlyRole(executorKey, UserRole.SUPPLIER);
            if (!Objects.equals(product.getMader(), executorKey)) {
                throw INCORRECT_DATA;
            }
        }

        // Создание заказа и запись в систему
        Order order = new Order(sender, executorKey, productKey, count, desiredDeliveryLimit, deliveryAddress);
        int lastOrderId = contractState.get(LAST_ORDER_ID, Integer.class);
        orderMapping.put(String.valueOf(lastOrderId), order);
        contractState.put(LAST_ORDER_ID, lastOrderId + 1);
    }

    // Метод уточнения данных заказа
    @Override
    public void clarifyOrder(
            String sender,
            String password,
            String orderKey,
            int totalPrice,
            Date deliveryLimit,
            Boolean isPrepaymentAvailable
    ) throws Exception {
        userHaveAccess(sender, password); // Имеет ли пользователь доступ к системе
        Order order = orderExist(orderKey);
        onlyExecutor(sender, orderKey); // Вызвать метод может только исполнитель, у которого был совершён заказ
        onlyOrderStatus(orderKey, OrderStatus.WAITING_FOR_EMPLOYEE); // Чтобы уточнить данные, заказ должен быть только создан
        // Если организацией (или её сотрудником) была изменена дата доставки, то перезаписать её
        deliveryLimit = isPresent(deliveryLimit) ? deliveryLimit : order.getDeliveryDate();
        order.clarify(totalPrice, deliveryLimit, isPrepaymentAvailable); // Уточнение данных
        orderMapping.put(orderKey, order); // Обновление заказа в системе
    }

    // Метод для подтверждения или отказа от заказа
    @Override
    public void confirmOrCancelOrder(
            String sender,
            String password,
            String orderKey,
            Boolean isConfirm
    ) throws Exception {
        User user = userHaveAccess(sender, password); // Имеет ли пользователь доступ к системе
        Order order = orderExist(orderKey);
        onlyClient(sender, orderKey); // Вызвать метод может только клиент, который привязан к заказу
        onlyOrderStatus(orderKey, OrderStatus.WAITING_FOR_CLIENT); // Чтобы принять/отвергнуть новые условия, заказ должен быть только уточнён
        // Если у пользователя не хватает денег на оплату заказа, то отказать в выполнении метода
        if (isConfirm && user.getBalance() < order.getPrice()) {
            throw NOT_ENOUGH_FUNDS;
        }
        order.confirmOrCancel(isConfirm);
        orderMapping.put(orderKey, order); // Обновление заказа в системе
    }

    // Метод оплаты заказа (до или после выполнения)
    @Override
    public void payOrder(
            String sender,
            String password,
            String orderKey
    ) throws Exception {
        userHaveAccess(sender, password); // Имеет ли пользователь доступ к системе
        Order order = orderExist(orderKey);
        onlyClient(sender, orderKey); // Вызвать метод может только клиент, который привязан к заказу
        // Оплата заказа
        transferMoney(sender, order.getExecutorKey(), order.getPrice());
        order.pay();
        orderMapping.put(orderKey, order); // Обновление заказа в системе
    }

    // Метод выполнения заказа
    @Override
    public void completeOrder(
            String sender,
            String password,
            String orderKey
    ) throws Exception {
        userHaveAccess(sender, password); // Имеет ли пользователь доступ к системе
        onlyExecutor(sender, orderKey); // Вызвать метод может только исполнитель, у которого был совершён заказ
        Order order = orderMapping.get(orderKey);
        {
            // Удаление продукта(-ов) у исполнителя
            User executor = userMapping.get(order.getExecutorKey());
            executor.decProduct(order.getProductKey(), order.getAmount());
            userMapping.put(order.getExecutorKey(), executor);
        }
        order.complete(); // Выполнение заказа
        orderMapping.put(orderKey, order); // Обновление заказа в системе
    }

    // Метод получения заказа
    @Override
    public void takeOrder(
            String sender,
            String password,
            String orderKey
    ) throws Exception {
        User user = userHaveAccess(sender, password); // Имеет ли пользователь доступ к системе
        onlyClient(sender, orderKey); // Вызвать метод может только клиент, который привязан к заказу
        onlyOrderStatus(orderKey, OrderStatus.WAITING_FOR_TAKING); // Чтобы забрать продукта, заказ должен быть готов
        Order order = orderMapping.get(orderKey);
        {
            // Добавление продукта(-ов) клиенту
            user.incProduct(order.getProductKey(), order.getAmount());
            userMapping.put(order.getClientKey(), user);
        }
        order.take(); // Получение заказа
        orderMapping.put(orderKey, order);
    }

    /*
        PRIVATE METHODS
     */

    private User userExist(String userPublicKey) throws Exception {
        if (!userMapping.has(userPublicKey)) {
            throw USER_NOT_FOUND;
        }
        User user = userMapping.get(userPublicKey);
        if (!user.isActivated()) {
            throw USER_IS_NOT_ACTIVATED;
        }
        if (user.isBlocked()) {
            throw USER_IS_BLOCKED;
        }
        return user;
    }

    private Organization organizationExist(String organizationKey) throws Exception {
        try {
            return organizationMapping.get(organizationKey);
        } catch (Exception e) {
            throw ORGANIZATION_NOT_FOUND;
        }
    }

    public User userHaveAccess(String userPublicKey, String password) throws Exception {
        User user = userExist(userPublicKey);
        if (!Objects.equals(user.getPassword(), hashPassword(userPublicKey, password))) {
            throw INCORRECT_LOGIN_OR_PASSWORD;
        }
        return user;
    }

    private void notSignedUp(String userPublicKey) throws Exception {
        if (userMapping.has(userPublicKey)) {
            throw USER_ALREADY_SIGNED_UP;
        }
    }

    private void onlyRole(String userPublicKey, UserRole role) throws Exception {
        UserRole userRole = userExist(userPublicKey).getRole();
        if (userRole != role && role != UserRole.DISTRIBUTOR && userRole != UserRole.OPERATOR) {
            throw NOT_ENOUGH_RIGHTS;
        }
    }

    private Order orderExist(String orderKey) throws Exception {
        try {
            return orderMapping.get(orderKey);
        } catch (Exception e) {
            throw ORDER_NOT_FOUND;
        }
    }

    private void onlyOrderStatus(String orderKey, OrderStatus status) throws Exception {
        Order order = orderExist(orderKey);
        if (order.getStatus() != status) {
            throw NOT_ENOUGH_RIGHTS;
        }
    }

    private void onlyClient(String userPublicKey, String orderKey) throws Exception {
        Order order = orderExist(orderKey);
        String clientKey = order.getClientKey();
        if (!Objects.equals(userPublicKey, clientKey)) {
            throw NOT_ENOUGH_RIGHTS;
        }
    }

    private void onlyExecutor(String userPublicKey, String orderKey) throws Exception {
        Order order = orderExist(orderKey);
        String executorKey = order.getExecutorKey();
        if (!Objects.equals(userPublicKey, executorKey)) {
            throw NOT_ENOUGH_RIGHTS;
        }
    }

    private void productNotCreated(String productKey) throws Exception {
        if (productMapping.has(productKey)) {
            throw PRODUCT_ALREADY_EXIST;
        }
    }

    private Product productExist(String productKey) throws Exception {
        try {
            return productMapping.get(productKey);
        } catch (Exception e) {
            throw PRODUCT_NOT_FOUND;
        }
    }

    private boolean isPresent(Object o) {
        return (o != null);
    }

    private boolean isPresent(String s) {
        if (s != null && !s.isEmpty()) {
            System.out.println("isPresent: " + s);
        }
        return (s != null && !s.isEmpty());
    }

    private boolean isPresent(String[] sArr) {
        return (sArr != null && sArr.length != 0);
    }

    private void transferMoney(String from, String to, int amount) throws Exception {
        User fromUser = userMapping.get(from);
        User toUser = userMapping.get(to);

        fromUser.decreaseBalance(amount);
        toUser.increaseBalance(amount);

        userMapping.put(from, fromUser);
        userMapping.put(to, toUser);
    }

    private String hashPassword(String login, String password) {
        return Hashing.sha256().hashString(login + password, StandardCharsets.UTF_8).toString();
    }

    private void haveProductRegion(String userPublicKey, String productKey) throws Exception {
        String[] userRegions = userMapping.get(userPublicKey).getRegions();
        List<String> productRegions = Arrays.stream(productMapping.get(productKey).getRegions()).toList();
        if (productRegions
                .stream()
                .filter(Arrays.asList(userRegions)::contains).toList()
                .isEmpty()
        ) {
            throw PRODUCT_NOT_IN_REGION;
        }
    }

    private void approvedDistributor(String distributorKey, String productKey) throws Exception {
        if (!Arrays.asList(productMapping.get(productKey).getDistributors()).contains(distributorKey)) {
            throw CANNOT_SELL_PRODUCT;
        }
    }
}
