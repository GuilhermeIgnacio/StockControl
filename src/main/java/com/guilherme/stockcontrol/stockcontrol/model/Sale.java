package com.guilherme.stockcontrol.stockcontrol.model;

import java.util.Date;

public class Sale {
    private int id;
    private int product_id;
    private Date saleDate; //Stores Sale Data

    //Getters
    public int getId() {
        return id;
    }

    public int getProduct_id() {
        return product_id;
    }

    public Date getSaleDate() {
        return saleDate;
    }


    //Getters


    //Setters
    public void setId(int id) {
        this.id = id;
    }

    public void setProduct_id(int product_id) {
        this.product_id = product_id;
    }

    public void setSaleDate(Date saleDate) {
        this.saleDate = saleDate;
    }

    //Setters

}
