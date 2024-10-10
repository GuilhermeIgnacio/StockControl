package com.guilherme.stockcontrol.stockcontrol.dao;

import com.guilherme.stockcontrol.stockcontrol.factory.ConnectionFactory;
import com.guilherme.stockcontrol.stockcontrol.model.Product;
import com.guilherme.stockcontrol.stockcontrol.model.MonthlySales;

import java.sql.Connection;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class StockDAO {

    public void insertItem(Product product) {
        String sql = "INSERT INTO products(product_name, product_description, purchase_price, retail_price, stock_quantity) VALUES (?, ?, ?, ?, ?)";
        Connection conn = null;
        PreparedStatement pstm = null;

        try {
            conn = ConnectionFactory.createConnectionToMySql();
            pstm = conn.prepareStatement(sql);

            pstm.setString(1, product.getProduct_name());
            pstm.setString(2, product.getProduct_description());
            pstm.setFloat(3, product.getPurchase_price());
            pstm.setFloat(4, product.getRetail_price());
            pstm.setInt(5, product.getStock_quantity());

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

    public List<Product> fetchItems() {
        String sql = "SELECT * FROM products";
        List<Product> items = new ArrayList<>();

        Connection conn;
        PreparedStatement pstm;
        ResultSet resultSet;

        try {
            conn = ConnectionFactory.createConnectionToMySql();
            pstm = conn.prepareStatement(sql);
            resultSet = pstm.executeQuery();

            while (resultSet.next()) {
                Product product = new Product();

                product.setProduct_id(resultSet.getInt("product_id"));
                product.setProduct_name(resultSet.getString("product_name"));
                product.setProduct_description(resultSet.getString("product_description"));
                product.setPurchase_price(resultSet.getFloat("purchase_price"));
                product.setRetail_price(resultSet.getFloat("retail_price"));
                product.setStock_quantity(resultSet.getInt("stock_quantity"));
                product.setCreated_at(resultSet.getTimestamp("created_at").toLocalDateTime());
                product.setUpdated_at(resultSet.getTimestamp("updated_at").toLocalDateTime());

                items.add(product);
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        return items;

    }

    public void updateItem(Product item) {
        String sql = "UPDATE products SET product_name = ?, product_description = ?, purchase_price = ?, retail_price = ?, stock_quantity = ? WHERE product_id = ?";

        Connection conn = null;
        PreparedStatement pstm = null;

        try {
            conn = ConnectionFactory.createConnectionToMySql();
            pstm = conn.prepareStatement(sql);

            pstm.setString(1, item.getProduct_name());
            pstm.setString(2, item.getProduct_description());
            pstm.setFloat(3, item.getPurchase_price());
            pstm.setFloat(4, item.getRetail_price());
            pstm.setFloat(5, item.getStock_quantity());
            pstm.setInt(6, item.getProduct_id());

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

    public void deleteItemById(int productId) {
        String sql = "DELETE FROM products WHERE product_id = ?";
        String salesSql = "DELETE FROM sales WHERE product_id = ?";
        Connection conn = null;
        PreparedStatement pstm = null;

        try {
            conn = ConnectionFactory.createConnectionToMySql();

            pstm = conn.prepareStatement(salesSql);
            pstm.setInt(1, productId);
            pstm.execute();

            pstm = conn.prepareStatement(sql);
            pstm.setInt(1, productId);
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

    public void insertSale(int productId, int salesCount) {
        String sql = "INSERT INTO sales(product_id, sale_date) VALUES (?, ?)";

        Connection conn = null;
        PreparedStatement pstm = null;


        for (int i = 0; i < salesCount; i++) {

            try {
                conn = ConnectionFactory.createConnectionToMySql();
                pstm = conn.prepareStatement(sql);

                pstm.setInt(1, productId);
                pstm.setDate(2, Date.valueOf(LocalDate.now()));
                pstm.execute();

            } catch (Exception e) {
                e.printStackTrace();
                throw new RuntimeException(e);
            } finally {

                try {
                    if (conn != null) {
                        conn.close();
                    }

                    if (pstm != null) {
                        pstm.close();
                    }

                } catch (Exception e) {
                    System.out.println("Error When Closing Connections.");
                }
            }
        }

    }

    public List<MonthlySales> fetchSales(String startDate, String endDate) {

        StringBuilder sql = new StringBuilder("SELECT DATE_FORMAT(s.sale_date, '%Y-%m') AS sale_month, " +
                "COUNT(s.id) AS total_sales, i.itemName, s.product_id " +
                "FROM sales s " +
                "JOIN items i ON s.product_id = i.id ");

        if (startDate != null && endDate != null) {
            if (!startDate.isBlank() && !endDate.isBlank()) {
                sql.append("WHERE s.sale_date BETWEEN ? AND ? ");
            }
        }

        sql.append("GROUP BY sale_month, s.product_id, i.itemName " +
                "ORDER BY sale_month, i.itemName");


        List<MonthlySales> sales = new ArrayList<>();

        Connection conn = null;
        PreparedStatement pstm = null;
        ResultSet resultSet = null;

        try {
            conn = ConnectionFactory.createConnectionToMySql();
            pstm = conn.prepareStatement(sql.toString());

            if (startDate != null && endDate != null) {
                if (!startDate.isEmpty() && !endDate.isEmpty()) {
                    pstm.setString(1, startDate);
                    pstm.setString(2, endDate);
                }
            }

            resultSet = pstm.executeQuery();

            while (resultSet.next()) {
                MonthlySales monthlySales = new MonthlySales();

                monthlySales.setMonth(resultSet.getString("sale_month"));
                monthlySales.setTotalSales(resultSet.getInt("total_sales"));
                monthlySales.setItemName(resultSet.getString("itemName"));
                monthlySales.setProductId(resultSet.getInt("product_id"));

                sales.add(monthlySales);
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        return sales;

    }

}


