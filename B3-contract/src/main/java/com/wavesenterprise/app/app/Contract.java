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
    private final Mapping<List<String>> employeesMapping; // Хранит ключи всех сотрудников организаций


    public Contract(ContractState contractState) {
        this.contractState = contractState;
        this.userMapping = contractState.getMapping(User.class, USERS_MAPPING_PREFIX);
        this.orderMapping = contractState.getMapping(Order.class, ORDERS_MAPPING_PREFIX);
        this.productMapping = contractState.getMapping(new TypeReference<>() {
        }, PRODUCTS_MAPPING_PREFIX);
        this.employeesMapping = contractState.getMapping(new TypeReference<>() {
        }, EMPLOYEES_MAPPING_PREFIX);
    }

    /*
        CONTRACT METHODS
     */

    /*
        Метод деплоя контракта
        Вход: логин оператора
     */
    @Override
    public void init(@NotNull String login) {
        this.contractState.put(OPERATOR, login);
        User operator = new User(login, "superTitle", "superDescription", "Wa o Wee", "admin@adm.in", new String[]{"США", "Индия", "Япония"}, UserRole.OPERATOR, null);
        operator.activate(operator.getDescription(), operator.getFullName(), operator.getEmail(), operator.getRegions());
        userMapping.put(login, operator);
    }

    // Метод регистрации для поставщика (производителя)
    @Override
    public void signUpSupplier(
            @NotNull String login,
            @NotNull String title,
            @NotNull String description,
            @NotNull String fullName,
            @NotNull String email,
            @NotNull String[] regions,
            @Nullable String organizationKey
    ) throws Exception {
        notSignedUp(login); // Проверка, что пользователя с таким логином нет в системе

        // Если пользователь указал ключ организации, то она должна быть поставщиком
        if (isPresent(organizationKey)) {
            onlyRole(organizationKey, UserRole.SUPPLIER);
        }

        // Добавление пользователя в систему
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

    // Метод регистрации для дистрибутора
    @Override
    public void signUpDistributor(
            @NotNull String login,
            @NotNull String title,
            @NotNull String fullName,
            @NotNull String email,
            @NotNull String[] regions,
            @Nullable String organizationKey
    ) throws Exception {
        notSignedUp(login); // Проверка, что пользователя с таким логином нет в системе

        // Если пользователь указал ключ организации, то она должна быть дистрибутором
        if (isPresent(organizationKey)) {
            onlyRole(organizationKey, UserRole.DISTRIBUTOR);
        }

        // Добавление пользователя в систему
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

    // Метод регистрации для конечного клиента
    @Override
    public void signUpClient(
            @NotNull String login,
            @NotNull String fullName,
            @NotNull String email,
            @NotNull String region
    ) throws Exception {
        notSignedUp(login); // Проверка, что пользователя с таким логином нет в системе

        // Добавление пользователя в систему
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

    // Метод активации пользователя в системе
    @Override
    public void activateUser(
            @NotNull String sender,
            @NotNull String userPublicKey,
            @Nullable String description,
            @Nullable String fullName,
            @Nullable String email,
            @Nullable String[] regions
    ) throws Exception {
        onlyRole(sender, UserRole.OPERATOR); // Метод может вызвать только оператор
        userExist(userPublicKey); // Проверка на то, что пользователь с указанным ключом существует
        User user = userMapping.get(userPublicKey);

        // Если пользователь уже активирован, то отменить выполнение метода
        if (user.isActivated()) {
            throw USER_ALREADY_ACTIVATED;
        }

        {
            // Если оператор редактировал некоторые поля учётной записи, то перезаписать их
            description = isPresent(description) ? description : user.getDescription();
            fullName = isPresent(fullName) ? fullName : user.getFullName();
            email = isPresent(email) ? email : user.getEmail();
            regions = isPresent(regions) ? regions : user.getRegions();
        }

        {
            // Если пользователь указывал ключ организации, то добавить ключ пользователя к списку сотрудников организации
            String organizationKey = user.getOrganizationKey();
            if (isPresent(organizationKey)) {
                addToMappingStringList(employeesMapping, organizationKey, userPublicKey);
            }
        }

        user.activate(description, fullName, email, regions); // Активация пользователя
        System.out.println(user);
        userMapping.put(userPublicKey, user); // Обновление учётной записи пользователя в системе
    }

    // Метод блокировки пользователя
    @Override
    public void blockUser(
            @NotNull String sender,
            @NotNull String userPublicKey
    ) throws Exception {
        onlyRole(sender, UserRole.OPERATOR); // Выполнять метод может только оператор
        userExist(userPublicKey); // Проверка на то, что пользователь с указанным ключом существует

        // Если оператор пытается заблокировать сам себя, то отказать в выполнении метода
        if (sender.equals(userPublicKey)) {
            throw NOT_ENOUGH_RIGHTS;
        }

        User user = userMapping.get(userPublicKey);

        // Если пользователь уже заблокирован, то отменить выполнение метода
        if (user.isBlocked()) {
            throw USER_IS_BLOCKED;
        }

        user.block(); // Блокировка пользователя
        userMapping.put(userPublicKey, user); // Обновление учётной записи пользователя в системе
    }

    // Метод создания карточки продукта
    @Override
    public void createProduct(
            @NotNull String sender,
            @NotNull String title,
            @NotNull String description,
            @NotNull String[] regions
    ) throws Exception {
        onlyRole(sender, UserRole.SUPPLIER); // Выполнять метод может только поставщик
        productNotCreated(title); // Проверка на то, что такого продукта ещё нет в системе
        productMapping.put(title, new Product(title, description, regions)); // Добавление продукта в систему
    }

    // Метод подтверждения карточки продукта
    @Override
    public void confirmProduct(
            @NotNull String sender,
            @NotNull String productKey,
            @Nullable String description,
            @Nullable String[] regions,
            @NotNull Integer minOrderCount,
            @NotNull Integer maxOrderCount,
            @NotNull String[] distributors
    ) throws Exception {
        onlyRole(sender, UserRole.OPERATOR); // Выполнять метод может только оператор
        productExist(productKey); // Проверка на то, что продукт существует в системе

        // Если список дистрибуторов пустой, то отказать в выполнении метода
        if (distributors.length == 0) {
            throw INCORRECT_DATA;
        }

        // Если количество товара за один заказ минимальное больше максимального, то отказать в выполнении метода
        if (minOrderCount > maxOrderCount) {
            throw INCORRECT_DATA;
        }

        Product product = productMapping.get(productKey);

        // Если оператор редактирован некоторые параметры продукта, то перезаписать их
        description = isPresent(description) ? description : product.getDescription();
        if (isPresent(regions)) {
            // Если список регионов, введённый оператором, пустой, то отказать в выполнении метода
            if (regions.length == 0) {
                throw INCORRECT_DATA;
            }
        } else {
            regions = product.getRegions();
        }

        // Проверка на то, существуют ли дистрибуторы и могут ли они реализовать продукт в регионах, в которых он производится
        for (String distributor : distributors) {
            userExist(distributor);
            String[] distributorRegions = userMapping.get(distributor).getRegions();
            boolean notFound = true;
            for (String region : regions) {
                if (Arrays.asList(distributorRegions).contains(region)) {
                    notFound = false;
                    break;
                }
            }
            if (notFound) {
                throw CANNOT_SELL_PRODUCT;
            }
        }
        product.confirm(description, regions, minOrderCount, maxOrderCount, distributors); // Подтверждение продукта
        productMapping.put(productKey, product); // Обновление продукта в системе
    }

    // Метод создания заказа
    @Override
    public void makeOrder(
            @NotNull String sender,
            @NotNull String productKey,
            @NotNull String organizationKey,
            @NotNull Integer count,
            @NotNull Date desiredDeliveryLimit,
            @NotNull String deliveryAddress
    ) throws Exception {
        userNotBlocked(sender); // Клиент может вызвать метод, только если он не заблокирован оператором
        userNotBlocked(organizationKey); // Клиент может адресовать заказ только незаблокированной организации
        // Клиент может заказать продукт, только если хотя бы один из его регионов совпадает хотя бы с одним из регионов распространения продукта
        haveRegion(sender, productKey);

        // Если количество заказываемого товара равно нулю, то отказать в выполнении метода
        if (count == 0) {
            throw INCORRECT_DATA;
        }

        {
            // Если заказ был сотруднику некой организации, то перезаписать в заказе ключ сотрудника на ключ организации
            String actualOrganizationKey = userMapping.get(organizationKey).getOrganizationKey();
            if (isPresent(actualOrganizationKey)) {
                organizationKey = actualOrganizationKey;
            }
        }

        {
            // Если минимальное/максимальное количество товара за один заказ равно нулю, то ограничения нет
            Product product = productMapping.get(productKey);
            // Если количество заказываемого товара меньше минимального ограничения, то отказать в выполнении метода
            if (product.getMinOrderCount() != 0 && count < product.getMinOrderCount()) {
                throw TOO_FEW_PRODUCTS;
            }
            // Если количество заказываемого товара больше максимального ограничения, то отказать в выполнении метода
            if (product.getMaxOrderCount() != 0 && count > product.getMaxOrderCount()) {
                throw TOO_MANY_PRODUCTS;
            }
        }

        {
            User user = userMapping.get(sender);
            // Если клиент – сотрудник некой организации, то перезаписать в заказе ключ сотрудника на ключ организации
            if (isPresent(user.getOrganizationKey())) {
                sender = user.getOrganizationKey();
            }

            if (user.getRole() == UserRole.OPERATOR || user.getRole() == UserRole.DISTRIBUTOR) {
                // Если клиент – оператор или дистрибутор, то заказать можно только у производителя
                onlyRole(organizationKey, UserRole.SUPPLIER);
            } else {
                // Иначе заказчик должен быть конечным клиентом, а исполнитель – пользователем с правами дистрибутора
                onlyRole(sender, UserRole.CLIENT);
                onlyRole(organizationKey, UserRole.DISTRIBUTOR);
                // Заказать продукт у организации можно только тогда, когда у неё есть данный продукт в необходимом количестве
                haveProduct(organizationKey, productKey, count);
                User executor = userMapping.get(organizationKey);
                executor.removeProduct(productKey, count); // Удаление продукта(-ов) из имения исполнителя
                userMapping.put(organizationKey, executor); // Обновление данных исполнителя в системе
            }
        }

        // Создание заказа и запись в систему
        Order order = new Order(sender, organizationKey, productKey, count, desiredDeliveryLimit, deliveryAddress);
        String key = Integer.toHexString(Objects.hash(sender, productKey, organizationKey, count, order.getOrderCreationDate()));
        orderMapping.put(key, order);
    }

    // Метод уточнения данных заказа
    @Override
    public void clarifyOrder(
            @NotNull String sender,
            @NotNull String orderKey,
            @NotNull Integer totalPrice,
            @Nullable Integer deliveryLimitUnixTime,
            @NotNull Boolean isPrepaymentAvailable
    ) throws Exception {
        onlyOrganization(sender, orderKey); // Вызвать метод может только организация (или её сотрудник), у которой был совершён заказ
        onlyOrderStatus(orderKey, OrderStatus.WAITING_FOR_EMPLOYEE); // Чтобы уточнить данные, заказ должен быть только создан
        Order order = orderMapping.get(orderKey);
        // Если организацией (или её сотрудником) была изменена дата доставки, то перезаписать её
        Date deliveryLimit = isPresent(deliveryLimitUnixTime) ? new Date(deliveryLimitUnixTime) : order.getDeliveryDate();
        order.clarify(totalPrice, deliveryLimit, isPrepaymentAvailable); // Уточнение данных
        orderMapping.put(orderKey, order); // Обновление заказа в системе
    }

    // Метод для подтверждения или отказа от заказа
    @Override
    public void confirmOrCancelOrder(
            @NotNull String sender,
            @NotNull String orderKey,
            @NotNull Boolean isConfirm
    ) throws Exception {
        onlyClient(sender, orderKey); // Вызвать метод может только клиент (конечный клиент, организация или её сотрудник), который привязан к заказу
        onlyOrderStatus(orderKey, OrderStatus.WAITING_FOR_CLIENT); // Чтобы отказаться от заказа, заказ должен быть только уточнён
        Order order = orderMapping.get(orderKey);
        if (isConfirm) {
            // Если у пользователя не хватает денег на оплату заказа, то отказать в выполнении метода
            User user = userMapping.get(order.getClientKey());
            if (user.getBalance() < order.getPrice()) {
                throw NOT_ENOUGH_FUNDS;
            }
            order.confirm(); // Подтверждение заказа
        } else {
            User executor = userMapping.get(order.getOrganizationKey());
            executor.addProduct(order.getProductKey(), order.getCount()); // Восстановление продуктов у исполнителя
            userMapping.put(order.getOrganizationKey(), executor); // Обновление данных исполнителя в системе
            order.cancel(); // Отказ от заказа
        }
        orderMapping.put(orderKey, order); // Обновление заказа в системе
    }

    // Метод оплаты заказа (до или после выполнения)
    @Override
    public void payOrder(
            @NotNull String sender,
            @NotNull String orderKey
    ) throws Exception {
        onlyClient(sender, orderKey); // Вызвать метод может только клиент (конечный клиент, организация или её сотрудник), который привязан к заказу
        Order order = orderMapping.get(orderKey);

        // Оплата заказа
        transfer(sender, order.getOrganizationKey(), order.getPrice());
        order.pay();

        orderMapping.put(orderKey, order); // Обновление заказа в системе
    }

    // Метод выполнения заказа
    @Override
    public void completeOrder(
            @NotNull String sender,
            @NotNull String orderKey
    ) throws Exception {
        onlyOrganization(sender, orderKey); // Вызвать метод может только организация (или её сотрудник), у которой был совершён заказ
        Order order = orderMapping.get(orderKey);
        order.complete(); // Выполнение заказа
        orderMapping.put(orderKey, order); // Обновление заказа в системе
        System.out.println("lol");
    }

    // Метод получения заказа
    @Override
    public void takeOrder(
            @NotNull String sender,
            @NotNull String orderKey
    ) throws Exception {
        onlyClient(sender, orderKey); // Вызвать метод может только клиент (конечный клиент, организация или её сотрудник), который привязан к заказу
        onlyOrderStatus(orderKey, OrderStatus.WAITING_FOR_TAKING); // Чтобы забрать продукта, заказ должен быть готов
        Order order = orderMapping.get(orderKey);
        {
            // Добавление продукта(-ов) клиенту
            User user = userMapping.get(order.getClientKey());
            String productKey = order.getProductKey();
            user.addProduct(productKey, order.getCount());
            userMapping.put(order.getClientKey(), user);
        }
        // Присвоение заказу статуса "Получен"
        order.take();
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
        User user = userMapping.get(userPublicKey);
        if (!user.isActivated()) {
            throw USER_IS_NOT_ACTIVATED;
        }
        if (user.isBlocked()) {
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

    private void haveProduct(String userPublicKey, String productKey, int count) throws Exception {
        User user = userMapping.get(userPublicKey);
        // Производитель создаёт продукты для других, поэтому ему проверка не требуется
        if (user.getRole() != UserRole.SUPPLIER) {
            if (user.getProducts().stream().filter(userProductKey -> Objects.equals(userProductKey, productKey)).toArray().length < count) {
                throw NOT_ENOUGH_PRODUCTS;
            }
        }
    }

    private boolean isPresent(Object o) {
        return (o != null);
    }

    private boolean isPresent(String s) {
        if (s != null && s.length() != 0) {
            System.out.println("isPresent: " + s);
        }
        return (s != null && s.length() != 0);
    }

    private boolean isPresent(String[] sArr) {
        return (sArr != null && sArr.length != 0);
    }

    private void transfer(String from, String to, int amount) throws Exception {
        User fromUser = userMapping.get(from);
        User toUser = userMapping.get(to);

        fromUser.decreaseBalance(amount);
        toUser.increaseBalance(amount);

        userMapping.put(from, fromUser);
        userMapping.put(to, toUser);
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

    private void haveRegion(String userPublicKey, String productKey) throws Exception {
        String[] userRegions = userMapping.get(userPublicKey).getRegions();
        List<String> productRegions = Arrays.stream(productMapping.get(productKey).getRegions()).toList();
        boolean notFound = true;
        for (String userRegion : userRegions) {
            if (productRegions.contains(userRegion)) {
                notFound = false;
                break;
            }
        }
        if (notFound) {
            throw PRODUCT_NOT_IN_REGION;
        }
    }
}
