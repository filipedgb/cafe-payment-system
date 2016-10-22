package com.example.filipedgb.cmovproj1;

/**
 * Created by Filipe on 18/10/2016.
 */

import android.util.Log;

import com.google.firebase.database.Exclude;

import java.util.*;

public class Product {
    public String name = "";
    public Double price = 0.0;

    public Map<String, Double> products = new HashMap<>();

    public Product() {

    }

    public String getName() {
        return name;
    }

    public Double getPrice() {
        return price;
    }

    public Map<String, Double> getProducts() {
        return products;
    }

    public Product(String nameIn, Double priceIn) {
        this.name = nameIn;
        this.price = priceIn;

    }

    @Exclude
    public Map<String, Object> toMap() {
        HashMap<String, Object> result = new HashMap<>();
        result.put("name", name);
        result.put("price", price);

        return result;
    }


}
