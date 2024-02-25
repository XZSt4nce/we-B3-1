package com.wavesenterprise.app.dto.user;

public class BlockDTO {
    String sender;
    String password;
    String userPublicKey;

    public BlockDTO() {}

    public BlockDTO(String sender, String password, String userPublicKey) {
        this.sender = sender;
        this.password = password;
        this.userPublicKey = userPublicKey;
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
}
