package com.wavesenterprise.app.domain;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static com.wavesenterprise.app.api.IContract.Exceptions.NOT_ENOUGH_FUNDS;
import static com.wavesenterprise.app.api.IContract.Exceptions.NOT_ENOUGH_PRODUCTS;

public class User {
    private final String login;
    private final String password;
    private Integer balance;
    private String fullName;
    private String email;
    private String[] regions;
    private final List<Integer> productsProvided;
    private final Map<Integer, Integer> products;
    private final List<Integer> orders;
    private final UserRole role;
    private boolean isActivated;
    private boolean isBlocked;
    private final Integer organizationKey;

    public User() {
        this.login = null;
        this.password = null;
        this.productsProvided = new ArrayList<>();
        this.products = new HashMap<>();
        this.orders = null;
        this.role = null;
        this.organizationKey = null;
    }

    public User(
            String login,
            String password,
            String fullName,
            String email,
            String[] regions,
            int organizationKey,
            UserRole role
    ) {
        this.login = login;
        this.password = password;
        this.balance = 10_000;
        this.fullName = fullName;
        this.email = email;
        this.regions = regions;
        this.productsProvided = new ArrayList<>();
        this.products = new HashMap<>();
        this.orders = new ArrayList<>();
        this.isBlocked = false;
        this.isActivated = role == UserRole.OPERATOR;
        this.role = role;
        this.organizationKey = organizationKey;
    }

    public void activate(
            String fullName,
            String email,
            String[] regions
    ) {
        this.fullName = fullName;
        this.email = email;
        this.regions = regions;
        this.isActivated = true;
    }

    public void block() {
        this.isBlocked = true;
    }

    public void increaseBalance(int value) {
        this.balance += value;
    }

    public void decreaseBalance(int value) throws Exception {
        if (this.balance < value) {
            throw NOT_ENOUGH_FUNDS;
        }
        this.balance -= value;
    }

    public String getLogin() {
        return login;
    }

    public String getFullName() {
        return fullName;
    }

    public void setFullName(String fullName) {
        this.fullName = fullName;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public UserRole getRole() {
        return role;
    }

    public boolean isActivated() {
        return isActivated;
    }

    public Integer getOrganizationKey() {
        return organizationKey;
    }

    public boolean isBlocked() {
        return isBlocked;
    }

    public String[] getRegions() {
        return regions;
    }

    public void setRegions(String[] regions) {
        this.regions = regions;
    }

    public Map<Integer, Integer> getProducts() {
        return products;
    }

    public void incProduct(Integer productKey, int count) {
        int newCount = this.products.getOrDefault(productKey, 0) + count;
        this.products.put(productKey, newCount);
    }

    public void decProduct(Integer productKey, int count) throws Exception {
        int newCount = this.products.getOrDefault(productKey, 0) - count;
        if (newCount < 0) {
            throw NOT_ENOUGH_PRODUCTS;
        }
        this.products.put(productKey, newCount);
    }

    public int getBalance() {
        return balance;
    }

    public void setActivated(boolean activated) {
        isActivated = activated;
    }

    public void setBlocked(boolean blocked) {
        isBlocked = blocked;
    }

    public String getPassword() {
        return password;
    }

    public void setBalance(Integer balance) {
        this.balance = balance;
    }

    public List<Integer> getProductsProvided() {
        return productsProvided;
    }

    public void addProductProvided(int productKey) {
        productsProvided.add(productKey);
    }

    public List<Integer> getOrders() {
        return orders;
    }

    public void addOrder(int orderKey) {
        assert orders != null;
        orders.add(orderKey);
    }
}
