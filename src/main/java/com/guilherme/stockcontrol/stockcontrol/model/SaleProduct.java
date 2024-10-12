package com.guilherme.stockcontrol.stockcontrol.model;

import java.util.Date;

public class SaleProduct {

    private int saleId;
    private String productName;
    private float salePrice;
    private Date saleDate;

    //Getter
    public int getSaleId() {
        return saleId;
    }

    public String getProductName() {
        return productName;
    }

    public float getSalePrice() {
        return salePrice;
    }

    public Date getSaleDate() {
        return saleDate;
    }
    //Getter

    //Setter
    public void setSaleId(int saleId) {
        this.saleId = saleId;
    }

    public void setProductName(String productName) {
        this.productName = productName;
    }

    public void setSalePrice(float salePrice) {
        this.salePrice = salePrice;
    }

    public void setSaleDate(Date saleDate) {
        this.saleDate = saleDate;
    }
    //Setter


}
