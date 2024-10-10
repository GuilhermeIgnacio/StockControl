package com.guilherme.stockcontrol.stockcontrol.model;

import java.time.LocalDateTime;

public class Product {

    private int product_id;
    private String product_name;
    private String product_description;
    private float purchase_price;
    private float retail_price;
    private int stock_quantity;
    private LocalDateTime created_at;
    private LocalDateTime updated_at;

    //Getters

    public int getProduct_id() {
        return product_id;
    }
    public String getProduct_name() {
        return product_name;
    }
    public String getProduct_description() {
        return product_description;
    }
    public float getPurchase_price() {
        return purchase_price;
    }
    public float getRetail_price() {
        return retail_price;
    }
    public int getStock_quantity() {
        return stock_quantity;
    }
    public LocalDateTime getCreated_at() {
        return created_at;
    }
    public LocalDateTime getUpdated_at() {
        return updated_at;
    }

    //Getters

    //Setters

    public void setProduct_id(int product_id) {
        this.product_id = product_id;
    }
    public void setProduct_name(String product_name) {
        this.product_name = product_name;
    }
    public void setProduct_description(String product_description) {
        this.product_description = product_description;
    }
    public void setPurchase_price(float purchase_price) {
        this.purchase_price = purchase_price;
    }
    public void setRetail_price(float retail_price) {
        this.retail_price = retail_price;
    }
    public void setStock_quantity(int stock_quantity) {
        this.stock_quantity = stock_quantity;
    }
    public void setCreated_at(LocalDateTime created_at) {
        this.created_at = created_at;
    }
    public void setUpdated_at(LocalDateTime updated_at) {
        this.updated_at = updated_at;
    }

    //Setters



}
