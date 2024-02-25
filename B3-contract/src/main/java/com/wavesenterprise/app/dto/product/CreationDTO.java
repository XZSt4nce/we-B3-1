package com.wavesenterprise.app.dto.product;

public class CreationDTO {
    String sender;
    String password;
    String title;
    String description;
    String[] regions;

    public CreationDTO() {}

    public CreationDTO(String sender, String password, String title, String description, String[] regions) {
        this.sender = sender;
        this.password = password;
        this.title = title;
        this.description = description;
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

    public String[] getRegions() {
        return regions;
    }

    public void setRegions(String[] regions) {
        this.regions = regions;
    }
}
