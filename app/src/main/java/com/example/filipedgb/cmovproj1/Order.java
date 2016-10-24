package com.example.filipedgb.cmovproj1;

import com.google.firebase.FirebaseApp;
import com.google.firebase.auth.FirebaseAuth;


import java.util.HashMap;

/**
 * Created by Filipe Batista on 23/10/2016.
 */

public class Order {
    private FirebaseApp app;
    private FirebaseAuth auth;
    private String order_id;
    private Double order_price;

    public String getUser_code() {
        return user_code;
    }

    public void setUser_code(String user_code) {
        this.user_code = user_code;
    }

    private String user_code;
    private HashMap<String, Integer> listOfProducts;
    private Boolean order_paid;

    public Order(String user_code_input) {
        order_paid = false;
        listOfProducts = new HashMap<String,Integer>();
        user_code = user_code_input;

    }

    public void addProductToOrder(Product product, Integer quantity) {
        listOfProducts.put(product.getId(),quantity);
    }

    public void setConfirmPayment() {
        order_paid = true;
    }


    public FirebaseApp getApp() {
        return app;
    }public void setApp(FirebaseApp app) {
        this.app = app;
    }public FirebaseAuth getAuth() {
        return auth;
    }public void setAuth(FirebaseAuth auth) {
        this.auth = auth;
    }public String getOrder_id() {
        return order_id;
    }public void setOrder_id(String order_id) {
        this.order_id = order_id;
    }public HashMap<String, Integer> getListOfProducts() {
        return listOfProducts;
    }public void setListOfProducts(HashMap<String, Integer> listOfProducts) {
        this.listOfProducts = listOfProducts;
    }public Boolean getOrder_paid() {
        return order_paid;
    }public void setOrder_paid(Boolean order_paid) {
        this.order_paid = order_paid;
    }

    public Double getOrder_price() {
        return order_price;
    }

    public void setOrder_price(Double order_price) {
        this.order_price = order_price;
    }



}

