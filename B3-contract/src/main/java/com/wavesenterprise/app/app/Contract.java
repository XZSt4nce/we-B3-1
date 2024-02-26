package com.wavesenterprise.app.app;

import com.google.common.hash.Hashing;
import com.wavesenterprise.app.api.IContract;
import com.wavesenterprise.app.domain.*;
import com.wavesenterprise.app.dto.order.*;
import com.wavesenterprise.app.dto.product.ConfirmationDTO;
import com.wavesenterprise.app.dto.product.CreationDTO;
import com.wavesenterprise.app.dto.user.ActivationDTO;
import com.wavesenterprise.app.dto.user.BlockDTO;
import com.wavesenterprise.app.dto.user.RegistrationDto;
import com.wavesenterprise.sdk.contract.api.annotation.ContractHandler;
import com.wavesenterprise.sdk.contract.api.state.ContractState;
import com.wavesenterprise.sdk.contract.api.state.TypeReference;
import com.wavesenterprise.sdk.contract.api.state.mapping.Mapping;

import java.lang.reflect.InvocationTargetException;
import java.nio.charset.StandardCharsets;
import java.util.*;

import static com.wavesenterprise.app.api.IContract.Exceptions.*;
import static com.wavesenterprise.app.api.IContract.Keys.*;
import static com.wavesenterprise.app.domain.OrderStatus.*;
import static com.wavesenterprise.app.domain.Role.*;

@ContractHandler
public class Contract implements IContract {
    private final ContractState contractState; // Хранит все значения контракта
    private final Mapping<User> userMapping; // Хранит всех пользователей
    private final List<Order> orderList; // Хранит все заказы
    private final List<Product> productList; // Хранит все продукты
    private final List<Organization> organizationList; // Хранит все организации


    public Contract(ContractState contractState) {
        this.contractState = contractState;
        this.userMapping = contractState.getMapping(User.class, USERS_MAPPING_PREFIX);
        this.orderList = getValue(ORDERS_LIST, new ArrayList<>());
        this.productList = getValue(PRODUCTS_LIST, new ArrayList<>());
        this.organizationList = getValue(ORGANIZATIONS_LIST, new ArrayList<>());
    }

    /*
        CONTRACT METHODS
     */

    // Метод инициализации контракта
    @Override
    public void init() {
        String login = "operator";
        this.contractState.put(CREATOR, login);
        this.contractState.put(ORDERS_LIST, this.orderList);
        this.contractState.put(PRODUCTS_LIST, this.productList);
        this.contractState.put(ORGANIZATIONS_LIST, this.organizationList);

        addUser(
                login,
                "123",
                "Profi",
                "Разработка решений с использованием блочейн технологий",
                "admin",
                "admin@adm.in",
                new String[]{"США", "Индия", "Япония"},
                OPERATOR
        );

        addUser(
                "supplier",
                "123",
                "SuperSupplier",
                "Wow description",
                "Саплаер Саплаеров Саплаерович",
                "supplier@mail.ru",
                new String[]{"USA", "RUSSIA"},
                SUPPLIER
        );

        addUser(
                "distributor",
                "123",
                "SuperDistributor",
                null,
                "Дистрибутор Дистрибуторов Дистрибуторович",
                "distributor@mail.ru",
                new String[]{"USA", "Japan", "usa"},
                DISTRIBUTOR
        );

        addUser(
                "client",
                "123",
                null,
                null,
                "Клиент Клиентов Клиентович",
                "client@mail.ru",
                new String[]{"RUSSIA"},
                CLIENT
        );

        addProduct(
                "supplier",
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
    public void signUp(RegistrationDto registrationDTO) throws Exception {
        String login = registrationDTO.getLogin();
        String password = registrationDTO.getPassword();
        String title = registrationDTO.getTitle();
        String description = registrationDTO.getDescription();
        String fullName = registrationDTO.getFullName();
        String email = registrationDTO.getEmail();
        String[] regions = registrationDTO.getRegions();
        int organizationKey = registrationDTO.getOrganizationKey();

        notSignedUp(login); // Проверка, что пользователя с таким логином нет в системе
        Role role = CLIENT;

        if (isPresent(organizationKey)) {
            if (isPresent(title) || isPresent(description)) {
                throw INCORRECT_DATA;
            }
            Organization organization = organizationExist(organizationKey);
            role = organization.getRole();
        } else if (isPresent(title)) {
            if (isPresent(description)) {
                role = OPERATOR;
            } else {
                role = DISTRIBUTOR;
            }
            Organization organization = new Organization(
                    login,
                    title,
                    description,
                    role
            );
            organizationKey = organizationList.size();
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
    public void activateUser(ActivationDTO activationDTO) throws Exception {
        String sender = activationDTO.getSender();
        String password = activationDTO.getPassword();
        String userPublicKey = activationDTO.getUserPublicKey();
        String fullName = activationDTO.getFullName();
        String email = activationDTO.getEmail();
        String[] regions = activationDTO.getRegions();

        userHaveAccess(sender, password); // Имеет ли пользователь доступ к системе
        onlyRole(sender, OPERATOR); // Метод может вызвать только оператор
        User user = userExist(userPublicKey);

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
                organizationList.set(organizationKey, organization);
                contractState.put(ORGANIZATIONS_LIST, organizationList);
            }
        }

        user.activate(fullName, email, regions); // Активация пользователя
        userMapping.put(userPublicKey, user); // Обновление учётной записи пользователя в системе
    }

    // Метод блокировки пользователя
    @Override
    public void blockUser(BlockDTO blockDTO) throws Exception {
        String sender = blockDTO.getSender();
        String password = blockDTO.getPassword();
        String userPublicKey = blockDTO.getUserPublicKey();

        User user = userHaveAccess(sender, password); // Имеет ли пользователь доступ к системе
        onlyRole(sender, OPERATOR); // Выполнять метод может только оператор
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
    public void createProduct(CreationDTO creationDTO) throws Exception {
        String sender = creationDTO.getSender();
        String password = creationDTO.getPassword();
        String title = creationDTO.getTitle();
        String description = creationDTO.getDescription();
        String[] regions = creationDTO.getRegions();

        User user = userHaveAccess(sender, password); // Имеет ли пользователь доступ к системе
        onlyRole(sender, SUPPLIER); // Выполнять метод может только поставщик
        int productId = productList.size();
        productList.add(new Product(productId, sender, title, description, regions)); // Добавление продукта в систему
        contractState.put(PRODUCTS_LIST, productList);
        user.addProductProvided(productId);
        userMapping.put(sender, user);
    }

    // Метод подтверждения карточки продукта
    @Override
    public void confirmProduct(ConfirmationDTO confirmationDTO) throws Exception {
        String sender = confirmationDTO.getSender();
        String password = confirmationDTO.getPassword();
        int productKey = confirmationDTO.getProductKey();
        String description = confirmationDTO.getDescription();
        String[] regions = confirmationDTO.getRegions();
        int minOrderCount = confirmationDTO.getMinOrderCount();
        int maxOrderCount = confirmationDTO.getMaxOrderCount();
        String[] distributors = confirmationDTO.getDistributors();

        userHaveAccess(sender, password); // Имеет ли пользователь доступ к системе
        onlyRole(sender, OPERATOR); // Выполнять метод может только оператор
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
        productList.set(productKey, product); // Обновление продукта в системе
        contractState.put(PRODUCTS_LIST, productList);
    }

    // Метод создания заказа
    @Override
    public void makeOrder(MakeDTO makeDTO) throws Exception {
        String sender = makeDTO.getSender();
        String password = makeDTO.getPassword();
        int productKey = makeDTO.getProductKey();
        String executorKey = makeDTO.getExecutorKey();
        int count = makeDTO.getCount();
        long desiredDeliveryLimit = makeDTO.getDesiredDeliveryLimit();
        String deliveryAddress = makeDTO.getDeliveryAddress();

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

        if (user.getRole() == CLIENT) {
            onlyRole(executorKey, DISTRIBUTOR);
            approvedDistributor(executorKey, productKey);
        } else {
            onlyRole(sender, DISTRIBUTOR);
            onlyRole(executorKey, SUPPLIER);
            if (executor.getProducts().get(productKey) < count) {
                throw NOT_ENOUGH_PRODUCTS;
            }
            if (!Objects.equals(product.getMader(), executorKey)) {
                throw INCORRECT_DATA;
            }
        }

        // Создание заказа и запись в систему
        Order order = new Order(orderList.size(), sender, executorKey, productKey, count, desiredDeliveryLimit, deliveryAddress);
        orderList.add(order);
        contractState.put(ORDERS_LIST, orderList);
    }

    // Метод уточнения данных заказа
    @Override
    public void clarifyOrder(ClarifyDTO clarifyDTO) throws Exception {
        String sender = clarifyDTO.getSender();
        String password = clarifyDTO.getPassword();
        int orderKey = clarifyDTO.getOrderKey();
        int totalPrice = clarifyDTO.getTotalPrice();
        long deliveryLimit = clarifyDTO.getDeliveryLimit();
        boolean isPrepaymentAvailable = clarifyDTO.isPrepaymentAvailable();

        userHaveAccess(sender, password); // Имеет ли пользователь доступ к системе
        Order order = orderExist(orderKey);
        onlyExecutor(sender, orderKey); // Вызвать метод может только исполнитель, у которого был совершён заказ
        onlyOrderStatus(orderKey, WAITING_FOR_EMPLOYEE); // Чтобы уточнить данные, заказ должен быть только создан
        // Если организацией (или её сотрудником) была изменена дата доставки, то перезаписать её
        deliveryLimit = deliveryLimit == 0 ? order.getDeliveryDate() : deliveryLimit;
        order.clarify(totalPrice, deliveryLimit, isPrepaymentAvailable); // Уточнение данных
        orderList.set(orderKey, order);
        contractState.put(ORDERS_LIST, orderList);
    }

    // Метод для подтверждения или отказа от заказа
    @Override
    public void confirmOrCancelOrder(ConfirmOrCancelDTO confirmOrCancelDTO) throws Exception {
        String sender = confirmOrCancelDTO.getSender();
        String password = confirmOrCancelDTO.getPassword();
        int orderKey = confirmOrCancelDTO.getOrderKey();
        boolean isConfirm = confirmOrCancelDTO.isConfirm();

        User user = userHaveAccess(sender, password); // Имеет ли пользователь доступ к системе
        Order order = orderExist(orderKey);
        onlyClient(sender, orderKey); // Вызвать метод может только клиент, который привязан к заказу
        onlyOrderStatus(orderKey, WAITING_FOR_CLIENT); // Чтобы принять/отвергнуть новые условия, заказ должен быть только уточнён
        // Если у пользователя не хватает денег на оплату заказа, то отказать в выполнении метода
        if (isConfirm && user.getBalance() < order.getPrice()) {
            throw NOT_ENOUGH_FUNDS;
        }
        order.confirmOrCancel(isConfirm);
        // Обновление заказа в системе
        orderList.set(orderKey, order);
        contractState.put(ORDERS_LIST, orderList);
    }

    // Метод оплаты заказа (до или после выполнения)
    @Override
    public void payOrder(PayDTO payDTO) throws Exception {
        String sender = payDTO.getSender();
        String password = payDTO.getPassword();
        int orderKey = payDTO.getOrderKey();

        userHaveAccess(sender, password); // Имеет ли пользователь доступ к системе
        Order order = orderExist(orderKey);
        onlyClient(sender, orderKey); // Вызвать метод может только клиент, который привязан к заказу
        // Оплата заказа
        transferMoney(sender, order.getExecutorKey(), order.getPrice());
        order.pay();
        // Обновление заказа в системе
        orderList.set(orderKey, order);
        contractState.put(ORDERS_LIST, orderList);
    }

    // Метод выполнения заказа
    @Override
    public void completeOrder(CompletionDTO completionDTO) throws Exception {
        String sender = completionDTO.getSender();
        String password = completionDTO.getPassword();
        int orderKey = completionDTO.getOrderKey();

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
        // Обновление заказа в системе
        orderList.set(orderKey, order);
        contractState.put(ORDERS_LIST, orderList);
    }

    // Метод получения заказа
    @Override
    public void takeOrder(TakeDTO takeDTO) throws Exception {
        String sender = takeDTO.getSender();
        String password = takeDTO.getPassword();
        int orderKey = takeDTO.getOrderKey();

        User user = userHaveAccess(sender, password); // Имеет ли пользователь доступ к системе
        Order order = orderExist(orderKey);
        onlyClient(sender, orderKey); // Вызвать метод может только клиент, который привязан к заказу
        onlyOrderStatus(orderKey, WAITING_FOR_TAKING); // Чтобы забрать продукта, заказ должен быть готов
        {
            // Добавление продукта(-ов) клиенту
            user.incProduct(order.getProductKey(), order.getAmount());
            userMapping.put(order.getClientKey(), user);
        }
        order.take(); // Получение заказа
        orderList.set(orderKey, order);
        contractState.put(ORDERS_LIST, orderList);
    }

    /*
        PRIVATE METHODS
     */

    private User userExist(String userPublicKey) throws Exception {
        Optional<User> user = userMapping.tryGet(userPublicKey);
        if (user.isPresent()) {
            return user.get();
        } else {
            throw USER_NOT_FOUND;
        }
    }

    private Organization organizationExist(int organizationKey) throws Exception {
        if (organizationKey < organizationList.size()) {
            return organizationList.get(organizationKey);
        } else {
            throw ORGANIZATION_NOT_FOUND;
        }
    }

    public User userHaveAccess(String userPublicKey, String password) throws Exception {
        User user = userExist(userPublicKey);
        if (!Objects.equals(user.getPassword(), hashPassword(userPublicKey, password))) {
            throw INCORRECT_LOGIN_OR_PASSWORD;
        }
        if (!user.isActivated()) {
            throw USER_IS_NOT_ACTIVATED;
        }
        if (user.isBlocked()) {
            throw USER_IS_BLOCKED;
        }
        return user;
    }

    private void notSignedUp(String userPublicKey) throws Exception {
        if (userMapping.has(userPublicKey)) {
            throw USER_ALREADY_SIGNED_UP;
        }
    }

    private void onlyRole(String userPublicKey, Role role) throws Exception {
        Role userRole = userExist(userPublicKey).getRole();
        if (userRole != role && (role != DISTRIBUTOR || userRole != OPERATOR)) {
            throw NOT_ENOUGH_RIGHTS;
        }
    }

    private Order orderExist(int orderKey) throws Exception {
        if (orderKey < orderList.size()) {
            return orderList.get(orderKey);
        } else {
            throw ORDER_NOT_FOUND;
        }
    }

    private void onlyOrderStatus(int orderKey, OrderStatus status) throws Exception {
        Order order = orderExist(orderKey);
        if (order.getStatus() == status) {
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
        if (productKey < productList.size()) {
            return productList.get(productKey);
        } else {
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
            String title,
            String description,
            String fullName,
            String email,
            String[] regions,
            Role role
    ) {
        Organization organization = new Organization(
                login,
                title,
                description,
                role
        );
        int organizationKey = organizationList.size();
        organizationList.add(organization);
        contractState.put(ORGANIZATIONS_LIST, organizationList);

        User newUser = new User(login, hashPassword(login, password), fullName, email, regions, organizationKey, role);
        newUser.activate(fullName, email, regions);
        userMapping.put(login, newUser);
    }

    private void addProduct(
            String sender,
            String title,
            String description,
            String[] regions,
            int minOrderCount,
            int maxOrderCount,
            String[] distributors
    ) {
        User user = userMapping.get(sender);
        int productId = productList.size();
        Product newProduct = new Product(productId, sender, title, description, regions);
        newProduct.confirm(description, regions, minOrderCount, maxOrderCount, distributors);
        productList.add(newProduct);
        contractState.put(PRODUCTS_LIST, productList);
        user.addProductProvided(productId);
        userMapping.put(sender, user);
    }

    private <T> T getValue(String key, T defaultValue) {
        T value = defaultValue;
        try {
            value = this.contractState.get(key, new TypeReference<>() {});
        } catch (Exception ignored) {}
        return value;
    }
}
