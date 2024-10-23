package com.guilherme.stockcontrol.stockcontrol.model;

import java.util.Date;

/**
 * Classe que representa uma venda no sistema.
 * Contém informações sobre a venda, incluindo ID da venda, ID do produto,
 * preço da venda e data da venda.
 */

public class Sale {

    private int saleId;         // ID único da venda
    private int productId;      // ID do produto vendido
    private int quantity;       // Quantidade que foi vendida
    private float salePrice;    // Preço pelo qual o produto foi vendido
    private Date saleDate;      // Data da venda

    //Getters

    public int getSaleId() {
        return saleId;
    }

    public int getProductId() {
        return productId;
    }

    public int getQuantity() {
        return quantity;
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

    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }

    public void setSalePrice(float salePrice) {
        this.salePrice = salePrice;
    }

    public void setSaleDate(Date saleDate) {
        this.saleDate = saleDate;
    }

    //Setters

}
