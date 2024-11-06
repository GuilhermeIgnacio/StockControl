package com.guilherme.stockcontrol.stockcontrol.model;

import java.text.SimpleDateFormat;
import java.util.Date;

public class MonthlySales {
    private int productId;
    private String month;
    private int totalSales;
    private String itemName;

    //Getter
    public int getProductId() {
        return productId;
    }

    public String getMonth() {
        return month;
    }

    public int getTotalSales() {
        return totalSales;
    }

    public String getItemName() {
        return itemName;
    }
    //Getter

    //Setter
    public void setProductId(int productId) {
        this.productId = productId;
    }

    public void setMonth(String month) {
        this.month = month;
    }

    public void setTotalSales(int totalSales) {
        this.totalSales = totalSales;
    }

    public void setItemName(String itemName) {
        this.itemName = itemName;
    }
    //Setter

    public String getFormattedMonth() {
        try {
            SimpleDateFormat originalFormat = new SimpleDateFormat("yyyy-MM");
            SimpleDateFormat targetFormat = new SimpleDateFormat("MMMM yyyy");
            Date date = originalFormat.parse(month);
            String formattedMonth = targetFormat.format(date);

            return formattedMonth.substring(0, 1).toUpperCase() + formattedMonth.substring(1).toLowerCase();
        } catch (Exception e) {
            e.printStackTrace();
            return month; // Retorna o valor original em caso de erro
        }
    }

}
