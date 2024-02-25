package com.wavesenterprise.app.dto.user;

public class RegistrationDto {
    private String login;
    private String password;
    private String title;
    private String description;
    private String fullName;
    private String email;
    private String[] regions;
    private int organizationKey;

    public RegistrationDto() {}

    public RegistrationDto(String login, String password, String title, String description, String fullName, String email, String[] regions, int organizationKey) {
        this.login = login;
        this.password = password;
        this.title = title;
        this.description = description;
        this.fullName = fullName;
        this.email = email;
        this.regions = regions;
        this.organizationKey = organizationKey;
    }

    public String getLogin() {
        return login;
    }

    public void setLogin(String login) {
        this.login = login;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
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

    public String[] getRegions() {
        return regions;
    }

    public void setRegions(String[] regions) {
        this.regions = regions;
    }

    public int getOrganizationKey() {
        return organizationKey;
    }

    public void setOrganizationKey(int organizationKey) {
        this.organizationKey = organizationKey;
    }
}
