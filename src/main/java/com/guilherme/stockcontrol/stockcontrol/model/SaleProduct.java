package com.guilherme.stockcontrol.stockcontrol.model;

import java.util.Date;

/**
 * Classe que representa um produto vendido.
 * Contém informações sobre a venda, incluindo ID da venda,
 * nome do produto, preço da venda e data da venda.
 */

public class SaleProduct {

    private int saleId;         //ID único da venda
    private String productName; // Nome do produto vendido
    private float salePrice;    // Preço pelo qual o produto foi vendido
    private Date saleDate;      // Data da venda

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
