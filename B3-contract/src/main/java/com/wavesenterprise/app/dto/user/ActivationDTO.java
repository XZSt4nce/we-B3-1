package com.wavesenterprise.app.dto.user;

public class ActivationDTO {
    String sender;
    String password;
    String userPublicKey;
    String fullName;
    String email;
    String[] regions;

    public ActivationDTO() {}

    public ActivationDTO(String sender, String password, String userPublicKey, String fullName, String email, String[] regions) {
        this.sender = sender;
        this.password = password;
        this.userPublicKey = userPublicKey;
        this.fullName = fullName;
        this.email = email;
        this.regions = regions;
    }

    public String getSender() {
        return sender;
    }

    public void setSender(String sender) {
        this.sender = sender;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getUserPublicKey() {
        return userPublicKey;
    }

    public void setUserPublicKey(String userPublicKey) {
        this.userPublicKey = userPublicKey;
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
}
