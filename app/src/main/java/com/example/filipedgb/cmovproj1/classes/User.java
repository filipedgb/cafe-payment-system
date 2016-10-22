package com.example.filipedgb.cmovproj1.classes;

/**
 * Created by Casa on 16/10/2016.
 */

public class User {

    private String uid;
    private String name;
    private String cardNumber;
    private String code;
    private String username;

    public User(String name, String cardNumber, String code, String username) {
        this.name = name;
        this.cardNumber = cardNumber;
        this.code = code;
        this.username=username;
    }

    public String getUid() {
        return uid;
    }

    public void setUid(String uid) {
        this.uid = uid;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getCardNumber() {
        return cardNumber;
    }

    public void setCardNumber(String cardNumber) {
        this.cardNumber = cardNumber;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }


    public String getUsername() {
        return username;
    }
}
