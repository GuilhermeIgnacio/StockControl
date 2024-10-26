package com.guilherme.stockcontrol.stockcontrol.model;

import java.time.LocalDateTime;

public class BuyDetails {

    private int buyId;
    private int productId;
    private String productName;
    private int quantity;
    private float buyPrice;
    private float buyPriceUnit;
    private LocalDateTime buyDate;

    //Getters
    public int getBuyId() {
        return buyId;
    }

    public int getProductId() {
        return productId;
    }

    public String getProductName() {
        return productName;
    }

    public int getQuantity() {
        return quantity;
    }

    public float getBuyPrice() {
        return buyPrice;
    }

    public float getBuyPriceUnit() {
        return buyPriceUnit;
    }

    public LocalDateTime getBuyDate() {
        return buyDate;
    }
    //Getters


    //Setters
    public void setBuyId(int buyId) {
        this.buyId = buyId;
    }

    public void setProductId(int productId) {
        this.productId = productId;
    }

    public void setProductName(String productName) {
        this.productName = productName;
    }

    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }

    public void setBuyPrice(float buyPrice) {
        this.buyPrice = buyPrice;
    }

    public void setBuyPriceUnit(float buyPriceUnit) {
        this.buyPriceUnit = buyPriceUnit;
    }

    public void setBuyDate(LocalDateTime buyDate) {
        this.buyDate = buyDate;
    }
    //Getters

}
