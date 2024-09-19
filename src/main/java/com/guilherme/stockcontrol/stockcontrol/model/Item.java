package com.guilherme.stockcontrol.stockcontrol.model;

import java.time.LocalDateTime;

public class Item {

    private int id;
    private String itemName;
    private String itemDescription;
    private int itemQuantity;
    private int itemSales;
    private float purchasePrice;
    private float retailPrice;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    //Getters

    public int getId() {
        return id;
    }
    public String getItemName() {
        return itemName;
    }
    public String getItemDescription() {
        return itemDescription;
    }
    public int getItemQuantity() {
        return itemQuantity;
    }
    public int getItemSales() {
        return itemSales;
    }
    public float getPurchasePrice() {
        return purchasePrice;
    }
    public float getRetailPrice() {
        return retailPrice;
    }
    public LocalDateTime getCreatedAt() {
        return createdAt;
    }
    public LocalDateTime getUpdatedAt() {
        return updatedAt;
    }

    //Getters

    //Setters

    public void setId(int id) {
        this.id = id;
    }
    public void setItemName(String itemName) {
        this.itemName = itemName;
    }
    public void setItemDescription(String itemDescription) {
        this.itemDescription = itemDescription;
    }
    public void setItemQuantity(int itemQuantity) {
        this.itemQuantity = itemQuantity;
    }
    public void setItemSales(int itemSales) {
        this.itemSales = itemSales;
    }
    public void setPurchasePrice(float purchasePrice) {
        this.purchasePrice = purchasePrice;
    }
    public void setRetailPrice(float retailPrice) {
        this.retailPrice = retailPrice;
    }
    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }
    public void setUpdatedAt(LocalDateTime updatedAt) {
        this.updatedAt = updatedAt;
    }

    //Setters



}
