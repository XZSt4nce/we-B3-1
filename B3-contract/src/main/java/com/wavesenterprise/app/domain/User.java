package com.wavesenterprise.app.domain;

import java.util.*;

import static com.wavesenterprise.app.api.IContract.Exceptions.NOT_ENOUGH_FUNDS;
import static com.wavesenterprise.app.api.IContract.Exceptions.NOT_ENOUGH_PRODUCTS;

public class User {
    private String login;
    private String password;
    private int balance;
    private String fullName;
    private String email;
    private String[] regions;
    private List<Integer> productsProvided;
    private Map<Integer, Integer> products;
    private List<Integer> orders;
    private String role;
    private boolean isActivated;
    private boolean isBlocked;
    private int organizationKey;

    public User() {}

    public User(
            String login,
            String password,
            String fullName,
            String email,
            String[] regions,
            int organizationKey,
            String role
    ) {
        this.login = login;
        this.password = password;
        this.balance = 10_000;
        this.fullName = fullName;
        this.email = email;
        this.regions = Arrays.stream(regions).map(String::toUpperCase).distinct().toList().toArray(new String[0]);
        this.productsProvided = new ArrayList<>();
        this.products = new HashMap<>();
        this.orders = new ArrayList<>();
        this.isBlocked = false;
        this.isActivated = false;
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
        this.regions = Arrays.stream(regions).map(String::toUpperCase).distinct().toList().toArray(new String[0]);
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

    public String getRole() {
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

    public void setLogin(String login) {
        this.login = login;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public void setBalance(int balance) {
        this.balance = balance;
    }

    public void setProductsProvided(List<Integer> productsProvided) {
        this.productsProvided = productsProvided;
    }

    public void setProducts(Map<Integer, Integer> products) {
        this.products = products;
    }

    public void setOrders(List<Integer> orders) {
        this.orders = orders;
    }

    public void setRole(String role) {
        this.role = role;
    }

    public void setOrganizationKey(int organizationKey) {
        this.organizationKey = organizationKey;
    }
}
