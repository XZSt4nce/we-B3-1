package com.wavesenterprise.app.app;

import com.google.common.hash.Hashing;
import com.wavesenterprise.app.api.IContract;
import com.wavesenterprise.app.domain.*;
import com.wavesenterprise.sdk.contract.api.annotation.ContractHandler;
import com.wavesenterprise.sdk.contract.api.state.ContractState;
import com.wavesenterprise.sdk.contract.api.state.TypeReference;
import com.wavesenterprise.sdk.contract.api.state.mapping.Mapping;

import java.nio.charset.StandardCharsets;
import java.util.*;

import static com.wavesenterprise.app.api.IContract.Exceptions.*;
import static com.wavesenterprise.app.api.IContract.Keys.*;

@ContractHandler
public class Contract implements IContract {
    private final ContractState contractState; // Хранит все значения контракта
    private final Mapping<User> userMapping; // Хранит всех пользователей
    private List<Order> orderList = new ArrayList<>(); // Хранит все заказы
    private List<Product> productList = new ArrayList<>(); // Хранит все продукты
    private List<Organization> organizationList = new ArrayList<>(); // Хранит все организации


    public Contract(ContractState contractState) {
        this.contractState = contractState;
        this.userMapping = contractState.getMapping(User.class, USERS_MAPPING_PREFIX);
        this.contractState.put(ORDERS_LIST, orderList);
        this.contractState.put(PRODUCTS_LIST, productList);
        this.contractState.put(ORGANIZATIONS_LIST, organizationList);
    }

    /*
        CONTRACT METHODS
     */

    // Метод деплоя контракта
    @Override
    public void init() throws Exception {
        String login = "operator";
        String password = "123";
        String organizationTitle = "Profi";
        String organizationDescription = "Разработка решений с использованием блочейн технологий";
        this.contractState.put(OPERATOR, login);
        Organization organization = new Organization(
                login,
                organizationTitle,
                organizationDescription,
                UserRole.DISTRIBUTOR
        );
        organizationList.add(organization);
        contractState.put(ORGANIZATIONS_LIST, organizationList);
        User operator = new User(
                login,
                hashPassword(login, password),
                "admin",
                "admin@adm.in",
                new String[]{"США", "Индия", "Япония"},
                0,
                UserRole.OPERATOR
        );
        userMapping.put(login, operator);

        addUser(
                "supplier",
                "123",
                login,
                password,
                "SuperSupplier",
                "Wow description",
                "Саплаер Саплаеров Саплаерович",
                "supplier@mail.ru",
                new String[]{"USA", "RUSSIA"},
                -1
        );

        addUser(
                "distributor",
                "123",
                login,
                password,
                "SuperDistributor",
                null,
                "Дистрибутор Дистрибуторов Дистрибуторович",
                "distributor@mail.ru",
                new String[]{"USA", "Japan", "usa"},
                -1
        );

        addUser(
                "client",
                "123",
                login,
                password,
                null,
                null,
                "Клиент Клиентов Клиентович",
                "client@mail.ru",
                new String[]{"RUSSIA"},
                -1
        );

        addProduct(
                "supplier",
                "123",
                login,
                password,
                "Банан",
                "Свежий, жёлтый, большой",
                new String[]{"USA"},
                1,
                0,
                new String[]{"distributor"}
        );
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
            Integer organizationKey
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
            if (isPresent(description)) {
                role = UserRole.SUPPLIER;
            } else {
                role = UserRole.DISTRIBUTOR;
            }
            Organization organization = new Organization(
                    login,
                    title,
                    description,
                    role
            );
            organizationList = contractState.get(ORGANIZATIONS_LIST, new TypeReference<>() {});
            organizationList.add(organization);
            contractState.put(ORGANIZATIONS_LIST, organizationList);
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
            Integer organizationKey = user.getOrganizationKey();
            if (isPresent(organizationKey)) {
                Organization organization = organizationExist(organizationKey);
                organization.addEmployee(userPublicKey);
                organizationList = contractState.get(ORGANIZATIONS_LIST, new TypeReference<>() {});
                organizationList.set(organizationKey, organization);
                contractState.put(ORGANIZATIONS_LIST, organizationList);
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
        User user = userHaveAccess(sender, password); // Имеет ли пользователь доступ к системе
        onlyRole(sender, UserRole.SUPPLIER); // Выполнять метод может только поставщик
        productList = contractState.get(PRODUCTS_LIST, new TypeReference<>() {});
        int productId = productList.size();
        user.addProductProvided(productId);
        productList.add(new Product(productId, sender, title, description, regions)); // Добавление продукта в систему
        contractState.put(PRODUCTS_LIST, productList);
    }

    // Метод подтверждения карточки продукта
    @Override
    public void confirmProduct(
            String sender,
            String password,
            int productKey,
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
            haveRegion(distributor, regions);
            userDistributor.addProductProvided(productKey);
            userMapping.put(distributor, userDistributor);
        }

        if (minOrderCount < 1) {
            throw INCORRECT_DATA;
        }

        // Если количество товара за один заказ минимальное больше максимального, то отказать в выполнении метода
        if (minOrderCount > maxOrderCount) {
            throw INCORRECT_DATA;
        }

        // Если оператор редактировал некоторые параметры продукта, то перезаписать их
        description = isPresent(description) ? description : product.getDescription();
        regions = isPresent(regions) ? regions : product.getRegions();

        product.confirm(description, regions, minOrderCount, maxOrderCount, distributors); // Подтверждение продукта
        productList = contractState.get(PRODUCTS_LIST, new TypeReference<>() {});
        productList.set(productKey, product); // Обновление продукта в системе
        contractState.put(PRODUCTS_LIST, productList);
    }

    // Метод создания заказа
    @Override
    public void makeOrder(
            String sender,
            String password,
            int productKey,
            String executorKey,
            int count,
            long desiredDeliveryLimit,
            String deliveryAddress
    ) throws Exception {
        User user = userHaveAccess(sender, password);
        User executor = userExist(executorKey);
        Product product = productExist(productKey);
        haveRegion(sender, product.getRegions());

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
            if (executor.getProducts().get(productKey) < count) {
                throw NOT_ENOUGH_PRODUCTS;
            }
            if (!Objects.equals(product.getMader(), executorKey)) {
                throw INCORRECT_DATA;
            }
        }

        // Создание заказа и запись в систему
        Order order = new Order(orderList.size(), sender, executorKey, productKey, count, desiredDeliveryLimit, deliveryAddress);
        orderList = contractState.get(ORDERS_LIST, new TypeReference<>() {});
        orderList.add(order);
        contractState.put(ORDERS_LIST, orderList);
    }

    // Метод уточнения данных заказа
    @Override
    public void clarifyOrder(
            String sender,
            String password,
            int orderKey,
            int totalPrice,
            long deliveryLimit,
            boolean isPrepaymentAvailable
    ) throws Exception {
        userHaveAccess(sender, password); // Имеет ли пользователь доступ к системе
        Order order = orderExist(orderKey);
        onlyExecutor(sender, orderKey); // Вызвать метод может только исполнитель, у которого был совершён заказ
        onlyOrderStatus(orderKey, OrderStatus.WAITING_FOR_EMPLOYEE); // Чтобы уточнить данные, заказ должен быть только создан
        // Если организацией (или её сотрудником) была изменена дата доставки, то перезаписать её
        deliveryLimit = deliveryLimit == 0 ? order.getDeliveryDate() : deliveryLimit;
        order.clarify(totalPrice, deliveryLimit, isPrepaymentAvailable); // Уточнение данных
        orderList.set(orderKey, order); // Обновление заказа в системе
    }

    // Метод для подтверждения или отказа от заказа
    @Override
    public void confirmOrCancelOrder(
            String sender,
            String password,
            int orderKey,
            boolean isConfirm
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
        orderList.set(orderKey, order); // Обновление заказа в системе
    }

    // Метод оплаты заказа (до или после выполнения)
    @Override
    public void payOrder(
            String sender,
            String password,
            int orderKey
    ) throws Exception {
        userHaveAccess(sender, password); // Имеет ли пользователь доступ к системе
        Order order = orderExist(orderKey);
        onlyClient(sender, orderKey); // Вызвать метод может только клиент, который привязан к заказу
        // Оплата заказа
        transferMoney(sender, order.getExecutorKey(), order.getPrice());
        order.pay();
        orderList.set(orderKey, order); // Обновление заказа в системе
    }

    // Метод выполнения заказа
    @Override
    public void completeOrder(
            String sender,
            String password,
            int orderKey
    ) throws Exception {
        userHaveAccess(sender, password); // Имеет ли пользователь доступ к системе
        Order order = orderExist(orderKey);
        onlyExecutor(sender, orderKey); // Вызвать метод может только исполнитель, у которого был совершён заказ
        {
            // Удаление продукта(-ов) у исполнителя
            User executor = userMapping.get(order.getExecutorKey());
            executor.decProduct(order.getProductKey(), order.getAmount());
            userMapping.put(order.getExecutorKey(), executor);
        }
        order.complete(); // Выполнение заказа
        orderList.set(orderKey, order); // Обновление заказа в системе
    }

    // Метод получения заказа
    @Override
    public void takeOrder(
            String sender,
            String password,
            int orderKey
    ) throws Exception {
        User user = userHaveAccess(sender, password); // Имеет ли пользователь доступ к системе
        Order order = orderExist(orderKey);
        onlyClient(sender, orderKey); // Вызвать метод может только клиент, который привязан к заказу
        onlyOrderStatus(orderKey, OrderStatus.WAITING_FOR_TAKING); // Чтобы забрать продукта, заказ должен быть готов
        {
            // Добавление продукта(-ов) клиенту
            user.incProduct(order.getProductKey(), order.getAmount());
            userMapping.put(order.getClientKey(), user);
        }
        order.take(); // Получение заказа
        orderList.set(orderKey, order);
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

    private Organization organizationExist(Integer organizationKey) throws Exception {
        try {
            organizationList = contractState.get(ORGANIZATIONS_LIST, new TypeReference<>() {});
            return organizationList.get(organizationKey);
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
        if (userRole != role && (role != UserRole.DISTRIBUTOR || userRole != UserRole.OPERATOR)) {
            throw NOT_ENOUGH_RIGHTS;
        }
    }

    private Order orderExist(int orderKey) throws Exception {
        try {
            return orderList.get(orderKey);
        } catch (Exception e) {
            throw ORDER_NOT_FOUND;
        }
    }

    private void onlyOrderStatus(int orderKey, OrderStatus status) throws Exception {
        Order order = orderExist(orderKey);
        if (order.getStatus() != status) {
            throw NOT_ENOUGH_RIGHTS;
        }
    }

    private void onlyClient(String userPublicKey, int orderKey) throws Exception {
        Order order = orderExist(orderKey);
        String clientKey = order.getClientKey();
        if (!Objects.equals(userPublicKey, clientKey)) {
            throw NOT_ENOUGH_RIGHTS;
        }
    }

    private void onlyExecutor(String userPublicKey, int orderKey) throws Exception {
        Order order = orderExist(orderKey);
        String executorKey = order.getExecutorKey();
        if (!Objects.equals(userPublicKey, executorKey)) {
            throw NOT_ENOUGH_RIGHTS;
        }
    }

    private Product productExist(int productKey) throws Exception {
        try {
            return productList.get(productKey);
        } catch (Exception e) {
            throw PRODUCT_NOT_FOUND;
        }
    }

    private boolean isPresent(Integer i) {
        return (i != null && i >= 0);
    }

    private boolean isPresent(String s) {
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

    private void haveRegion(String userPublicKey, String[] regions) throws Exception {
        String[] userRegions = userMapping.get(userPublicKey).getRegions();
        List<String> productRegions = List.of(regions);
        if (Arrays.stream(userRegions)
                .filter(productRegions::contains)
                .toArray().length == 0
        ) {
            throw INCORRECT_DATA_REGIONS;
        }
    }

    private void approvedDistributor(String distributorKey, int productKey) throws Exception {
        if (!Arrays.asList(productList.get(productKey).getDistributors()).contains(distributorKey)) {
            throw CANNOT_SELL_PRODUCT;
        }
    }

    private void addUser(
            String login,
            String password,
            String operatorLogin,
            String operatorPassword,
            String title,
            String description,
            String fullName,
            String email,
            String[] regions,
            Integer organizationKey
    ) throws Exception {
        signUp(
                login,
                password,
                title,
                description,
                fullName,
                email,
                regions,
                organizationKey
        );
        activateUser(operatorLogin, operatorPassword, login, fullName, email, regions);
    }

    private void addProduct(
            String sender,
            String password,
            String operatorLogin,
            String operatorPassword,
            String title,
            String description,
            String[] regions,
            int minOrderCount,
            int maxOrderCount,
            String[] distributors
    ) throws Exception {
        createProduct(sender, password, title, description, regions);
        productList = contractState.get(PRODUCTS_LIST, new TypeReference<>() {});
        int productKey = productList.size();
        confirmProduct(operatorLogin, operatorPassword, productKey, description, regions, minOrderCount, maxOrderCount, distributors);
    }
}
