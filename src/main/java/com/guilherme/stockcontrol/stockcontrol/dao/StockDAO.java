package com.guilherme.stockcontrol.stockcontrol.dao;

import com.guilherme.stockcontrol.stockcontrol.factory.ConnectionFactory;
import com.guilherme.stockcontrol.stockcontrol.model.Item;

import java.sql.Connection;
import java.sql.PreparedStatement;

public class StockDAO {
    public void insertItem(Item item) {
        String sql = "INSERT INTO items(itemName, itemDescription, itemQuantity, itemPrice) VALUES (?, ?, ?, ?)";
        Connection conn = null;
        PreparedStatement pstm = null;

        try {
            conn = ConnectionFactory.createConnectionToMySql();
            pstm = conn.prepareStatement(sql);

            pstm.setString(1, item.getItemName());
            pstm.setString(2, item.getItemDescription());
            pstm.setInt(3, item.getItemQuantity());
            pstm.setFloat(4, item.getItemPrice());

            pstm.execute();
        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException(e);
        } finally {
            try {

                if (pstm != null) {
                    pstm.close();
                }

                if (conn != null) {
                    conn.close();
                }

            } catch (Exception e) {
                System.out.println("Error When Closing Connections " + e);
            }
        }

    }
}


