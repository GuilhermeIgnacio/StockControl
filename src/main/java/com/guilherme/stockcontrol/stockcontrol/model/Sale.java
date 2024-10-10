package com.guilherme.stockcontrol.stockcontrol.model;

import java.util.Date;

public class Sale {
    private int saleId;
    private int productId;
    private float salePrice;
    private Date saleDate;


    //Getters
    public int getSaleId() {
        return saleId;
    }

    public int getProductId() {
        return productId;
    }

    public float getSalePrice() {
        return salePrice;
    }

    public Date getSaleDate() {
        return saleDate;
    }
    //Getters


    //Setters
    public void setSaleId(int saleId) {
        this.saleId = saleId;
    }

    public void setProductId(int productId) {
        this.productId = productId;
    }

    public void setSalePrice(float salePrice) {
        this.salePrice = salePrice;
    }

    public void setSaleDate(Date saleDate) {
        this.saleDate = saleDate;
    }
    //Setters

}
