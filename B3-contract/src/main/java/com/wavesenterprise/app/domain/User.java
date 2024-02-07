package com.wavesenterprise.app.domain;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;

import static com.wavesenterprise.app.api.IContract.Exceptions.NOT_ENOUGH_FUNDS;

public class User {
    private final String login;
    private Integer balance;
    private final String title;
    private String description;
    private String fullName;
    private String email;
    private String[] regions;
    private final List<String> products;
    private final UserRole role;
    private boolean isActivated;
    private boolean isBlocked;
    private String organizationKey;

    public User() {
        this.login = null;
        this.title = null;
        this.products = new ArrayList<>();
        this.role = null;
    }

    public User(
            @NotNull String login,
            @Nullable String title,
            @Nullable String description,
            @Nullable String fullName,
            @Nullable String email,
            @Nullable String[] regions,
            @Nullable UserRole role,
            @Nullable String organizationKey
    ) {
        this.login = login;
        this.balance = 10_000;
        this.title = title;
        this.description = description;
        this.fullName = fullName;
        this.email = email;
        this.regions = regions;
        this.products = new ArrayList<>();
        this.role = role;
        this.isActivated = false;
        this.isBlocked = false;
        this.organizationKey = organizationKey;
    }

    public void activate(
            @NotNull String description,
            @NotNull String fullName,
            @NotNull String email,
            @NotNull String[] regions
    ) {
        this.description = description;
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

    public String getTitle() { return title; }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
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

    public String getOrganizationKey() {
        return organizationKey;
    }

    public void setOrganizationKey(String organizationKey) {
        this.organizationKey = organizationKey;
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

    public List<String> getProducts() {
        return products;
    }

    public void addProduct(String productKey, int count) {
        for (int i = 0; i < count; i++) {
            this.products.add(productKey);
        }
    }

    public void removeProduct(String productKey, int count) {
        for (int i = 0; i < count; i++) {
            this.products.remove(productKey);
        }
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
}
