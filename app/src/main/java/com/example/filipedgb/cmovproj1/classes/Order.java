package com.example.filipedgb.cmovproj1.classes;

import android.util.Log;

import com.example.filipedgb.cmovproj1.Product;
import com.google.firebase.FirebaseApp;
import com.google.firebase.auth.FirebaseAuth;


import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;

/**
 * Created by Filipe Batista on 23/10/2016.
 */

public class Order implements Serializable {
    private FirebaseApp app;
    private FirebaseAuth auth;
    private String order_id;
    private Double order_price;
    private String user_code;
    private String created_at;
    private HashMap<String, Integer> listOfProducts;
    private Boolean order_paid;
    private HashMap<String,String> vouchers_to_use;


    public Order() {

    }

    public Order(String user_code_input) {
        order_paid = false;
        listOfProducts = new HashMap<String,Integer>();
        vouchers_to_use = new HashMap<String,String>();

        user_code = user_code_input;

    }

    public void addProductToOrder(Product product, Integer quantity) {
        listOfProducts.put(product.getId(),quantity);
    }


    public void addVoucherToOrder(String voucher_key, String voucher_signature) {
        if(vouchers_to_use.size() <= 3) {
            vouchers_to_use.put(voucher_key, voucher_signature.replace("{","{\"").replace("==",""));
        } else {
            Log.e("WARNING","No more vouchers added because the limit was exceeded");
        }
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

    public String getUser_code() {
        return user_code;
    }

    public void setUser_code(String user_code) {
        this.user_code = user_code;
    }

    public HashMap<String, String> getVouchers_to_use() {
        return vouchers_to_use;
    }

    public void setVouchers_to_use(HashMap<String, String> vouchers_to_use) {
        this.vouchers_to_use = vouchers_to_use;
    }


    public String getCreated_at() {
        return created_at;
    }

    public void setCreated_at(String created_at) {
        this.created_at = created_at;
    }

}

