package com.guilherme.stockcontrol.stockcontrol.dao;

import com.guilherme.stockcontrol.stockcontrol.factory.ConnectionFactory;
import com.guilherme.stockcontrol.stockcontrol.model.Item;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;

public class StockDAO {

    public void insertItem(Item item) {
        String sql = "INSERT INTO items(itemName, itemDescription, itemQuantity, purchasePrice, retailPrice) VALUES (?, ?, ?, ?, ?)";
        Connection conn = null;
        PreparedStatement pstm = null;

        try {
            conn = ConnectionFactory.createConnectionToMySql();
            pstm = conn.prepareStatement(sql);

            pstm.setString(1, item.getItemName());
            pstm.setString(2, item.getItemDescription());
            pstm.setInt(3, item.getItemQuantity());
            pstm.setFloat(4, item.getPurchasePrice());
            pstm.setFloat(5, item.getRetailPrice());

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

    public List<Item> fetchItems() {
        String sql = "SELECT * FROM items";
        List<Item> items = new ArrayList<>();

        Connection conn;
        PreparedStatement pstm;
        ResultSet resultSet;

        try {
            conn = ConnectionFactory.createConnectionToMySql();
            pstm = conn.prepareStatement(sql);
            resultSet = pstm.executeQuery();

            while (resultSet.next()) {
                Item item = new Item();

                item.setId(resultSet.getInt("id"));
                item.setItemName(resultSet.getString("itemName"));
                item.setItemDescription(resultSet.getString("itemDescription"));
                item.setItemQuantity(resultSet.getInt("itemQuantity"));
                item.setItemSales(resultSet.getInt("itemSales"));
                item.setPurchasePrice(resultSet.getFloat("purchasePrice"));
                item.setRetailPrice(resultSet.getFloat("retailPrice"));
                item.setCreatedAt(resultSet.getTimestamp("createdAt").toLocalDateTime());
                item.setUpdatedAt(resultSet.getTimestamp("updatedAt").toLocalDateTime());

                items.add(item);
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        return items;

    }

    public void updateItem(Item item) {
        String sql = "UPDATE items SET itemName = ?, itemDescription = ?, itemQuantity = ?, itemSales = ?, purchasePrice = ?, retailPrice = ? WHERE id = ?";

        Connection conn = null;
        PreparedStatement pstm = null;

        try {
            conn = ConnectionFactory.createConnectionToMySql();
            pstm = conn.prepareStatement(sql);

            pstm.setString(1, item.getItemName());
            pstm.setString(2, item.getItemDescription());
            pstm.setInt(3, item.getItemQuantity());
            pstm.setInt(4, item.getItemSales());
            pstm.setFloat(5, item.getPurchasePrice());
            pstm.setFloat(6, item.getRetailPrice());
            pstm.setInt(7, item.getId());

            pstm.execute();

        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try {

                if (conn != null) {
                    conn.close();
                }

                if (pstm != null) {
                    pstm.close();
                }

            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    public void deleteItemById(int id) {
        String sql = "DELETE FROM items WHERE id = ?";
        Connection conn = null;
        PreparedStatement pstm = null;

        try {
            conn = ConnectionFactory.createConnectionToMySql();
            pstm = conn.prepareStatement(sql);
            pstm.setInt(1, id);
            pstm.execute();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try {
                if (conn != null) {
                    conn.close();
                }

                if (pstm != null) {
                    pstm.close();
                }

            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

}


