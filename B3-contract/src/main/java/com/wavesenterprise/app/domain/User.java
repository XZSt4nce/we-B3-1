package com.wavesenterprise.app.domain;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;

public class User {
    private String login;
    private String title;
    private String description;
    private String fullName;
    private String email;
    private String[] regions;
    private List<Product> products;
    private UserRole role;
    private boolean isActivated;
    private boolean isBlocked;
    private String organizationKey;

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

    public String getLogin() {
        return login;
    }

    public void setLogin(String login) {
        this.login = login;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

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

    public String[] getRegion() {
        return regions;
    }

    public void setRegion(String[] regions) {
        this.regions = regions;
    }

    public UserRole getRole() {
        return role;
    }

    public void setRole(UserRole role) {
        this.role = role;
    }

    public boolean isActivated() {
        return isActivated;
    }

    public void setActivated(boolean activated) {
        isActivated = activated;
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

    public void setBlocked(boolean blocked) {
        isBlocked = blocked;
    }

    public String[] getRegions() {
        return regions;
    }

    public void setRegions(String[] regions) {
        this.regions = regions;
    }

    public List<Product> getProducts() {
        return products;
    }

    public void addProduct(Product product) {
        this.products.add(product);
    }
}
