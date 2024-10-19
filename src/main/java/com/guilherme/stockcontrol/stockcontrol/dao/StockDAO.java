package com.guilherme.stockcontrol.stockcontrol.dao;

import com.guilherme.stockcontrol.stockcontrol.factory.ConnectionFactory;
import com.guilherme.stockcontrol.stockcontrol.model.Product;
import com.guilherme.stockcontrol.stockcontrol.model.MonthlySales;
import com.guilherme.stockcontrol.stockcontrol.model.SaleProduct;
import javafx.application.Platform;
import javafx.scene.control.Alert;

import java.sql.Connection;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.List;

import static com.guilherme.stockcontrol.stockcontrol.Util.*;

public class StockDAO {

    public List<Product> fetchItems(String productName) {
        String sql = "SELECT * FROM products WHERE product_name LIKE ?";
        List<Product> items = new ArrayList<>();

        Connection conn = null;
        PreparedStatement pstm = null;
        ResultSet resultSet = null;

        try {
            conn = ConnectionFactory.createConnectionToMySql();
            pstm = conn.prepareStatement(sql);

            pstm.setString(1, "%" + productName + "%");

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

            return items;

        } catch (Exception e) {
            e.printStackTrace();
            RuntimeException exception = new RuntimeException(e);
            Platform.runLater(() -> genericAlertDialog(Alert.AlertType.ERROR, "", getProp().getString("fetch.products.error"), exception.getMessage()));

            return items;
        } finally {
            closeConnection(conn, pstm, resultSet);
        }

    }

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
            RuntimeException exception = new RuntimeException(e);
            Platform.runLater(() -> genericAlertDialog(Alert.AlertType.ERROR, "", getProp().getString("insert.product.error"), exception.getMessage()));
        } finally {
            closeConnection(conn, pstm, null);
        }

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
            RuntimeException exception = new RuntimeException(e);
            Platform.runLater(() -> genericAlertDialog(Alert.AlertType.ERROR, "", getProp().getString("update.product.error"), exception.getMessage()));
        } finally {
            closeConnection(conn, pstm, null);
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
            RuntimeException exception = new RuntimeException(e);
            Platform.runLater(() -> genericAlertDialog(Alert.AlertType.ERROR, "", getProp().getString("delete.product.error"), exception.getMessage()));
        } finally {
            closeConnection(conn, pstm, null);
        }
    }

    public void insertSale(Product product, int salesCount) {
        String sql = "INSERT INTO sales(product_id, sale_price, sale_date) VALUES (?, ?, ?)";

        Connection conn = null;
        PreparedStatement pstm = null;

        for (int i = 0; i < salesCount; i++) {

            try {
                conn = ConnectionFactory.createConnectionToMySql();
                pstm = conn.prepareStatement(sql);

                pstm.setInt(1, product.getProduct_id());
                pstm.setFloat(2, product.getRetail_price());
                pstm.setDate(3, Date.valueOf(LocalDate.now()));
                pstm.execute();

            } catch (Exception e) {
                e.printStackTrace();
                RuntimeException exception = new RuntimeException(e);
                Platform.runLater(() -> genericAlertDialog(Alert.AlertType.ERROR, "", getProp().getString("insert.sale.error"), exception.getMessage()));
            } finally {
                closeConnection(conn, pstm, null);
            }
        }

    }

    public List<MonthlySales> fetchMonthlySales(String startDate, String endDate) {

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
            RuntimeException exception = new RuntimeException(e);
            Platform.runLater(() -> genericAlertDialog(Alert.AlertType.ERROR, "", getProp().getString("fetch.monthly.sales.error"), exception.getMessage()));
        } finally {
            closeConnection(conn, pstm, resultSet);
        }

        return sales;

    }

    public List<SaleProduct> fetchSaleProduct(String productName, String startDate, String endDate) {

        List<SaleProduct> saleProducts = new ArrayList<>();

        StringBuilder sql = new StringBuilder(
                "SELECT s.sale_id, p.product_name, p.product_description, s.sale_price, s.sale_date " +
                        "FROM sales s " +
                        "JOIN products p ON s.product_id = p.product_id " +
                        "WHERE 1=1 ");

        if (productName != null && !productName.isEmpty()) {
            sql.append("AND p.product_name LIKE ? ");
        }
        if (startDate != null && !startDate.isEmpty()) {
            sql.append("AND s.sale_date >= ? ");
        }
        if (endDate != null && !endDate.isEmpty()) {
            sql.append("AND s.sale_date <= ? ");
        }

        Connection conn = null;
        PreparedStatement pstm = null;
        ResultSet resultSet = null;

        try {

            conn = ConnectionFactory.createConnectionToMySql();
            pstm = conn.prepareStatement(sql.toString());

            int paramIndex = 1;

            // Definindo os parâmetros dinâmicos
            if (productName != null && !productName.isEmpty()) {
                pstm.setString(paramIndex++, "%" + productName + "%"); // Pesquisa com LIKE
            }
            if (startDate != null && !startDate.isEmpty()) {

                pstm.setString(paramIndex++, formatDate(startDate)); // Data de início

            }

            if (endDate != null && !endDate.isEmpty()) {
                pstm.setString(paramIndex++, formatDate(endDate)); // Data de término
            }

            resultSet = pstm.executeQuery();

            while (resultSet.next()) {

                SaleProduct saleProduct = new SaleProduct();

                saleProduct.setSaleId(resultSet.getInt("sale_id"));
                saleProduct.setProductName(resultSet.getString("product_name"));
                saleProduct.setSalePrice(resultSet.getFloat("sale_price"));
                saleProduct.setSaleDate(resultSet.getDate("sale_date"));

                saleProducts.add(saleProduct);

            }

            return saleProducts;

        } catch (Exception e) {
            e.printStackTrace();
            RuntimeException exception = new RuntimeException(e);
            Platform.runLater(() -> genericAlertDialog(Alert.AlertType.ERROR, "", getProp().getString("fetch.sales.error"), exception.getMessage()));

            return saleProducts;
        } finally {
            closeConnection(conn, pstm, resultSet);
        }

    }

    public int getProductTotalSales(int productId) {
        int total = 0;
        String sql = "SELECT COUNT(*) FROM sales WHERE product_id = ?";

        Connection conn = null;
        PreparedStatement pstm = null;
        ResultSet resultSet = null;

        try {

            conn = ConnectionFactory.createConnectionToMySql();
            pstm = conn.prepareStatement(sql);

            pstm.setString(1, String.valueOf(productId));

            resultSet = pstm.executeQuery();

            while (resultSet.next()) {
                total = resultSet.getInt(1);
            }

            return total;

        } catch (Exception e) {
            e.printStackTrace();
            RuntimeException exception = new RuntimeException(e);
            Platform.runLater(() -> genericAlertDialog(Alert.AlertType.ERROR, "", getProp().getString("fetch.product.total.sales"), exception.getMessage()));
            return total;
        } finally {
            closeConnection(conn, pstm, resultSet);
        }

    }

    public float getProductSaleAmount(int productId) {
        float saleAmount = 0;

        String sql = "SELECT SUM(sale_price) FROM sales WHERE product_id = ?";

        Connection conn = null;
        PreparedStatement pstm = null;
        ResultSet resultSet = null;

        try {
            conn = ConnectionFactory.createConnectionToMySql();
            pstm = conn.prepareStatement(sql);
            pstm.setInt(1, productId);

            resultSet = pstm.executeQuery();

            while (resultSet.next()) {
                saleAmount = resultSet.getInt(1);
            }

            return saleAmount;

        } catch (Exception e) {
            e.printStackTrace();
            RuntimeException exception = new RuntimeException(e);
            Platform.runLater(() -> genericAlertDialog(Alert.AlertType.ERROR, "", getProp().getString("fetch.product.sale.amount"), exception.getMessage()));
            return saleAmount;
        } finally {
            closeConnection(conn, pstm, resultSet);
        }

    }

    public float totalIncome() {
        float total = 0f;
        String sql = "SELECT SUM(sale_price) FROM sales";

        Connection conn = null;
        PreparedStatement pstm = null;
        ResultSet resultSet = null;

        try {
            conn = ConnectionFactory.createConnectionToMySql();
            pstm = conn.prepareStatement(sql);
            resultSet = pstm.executeQuery();

            while (resultSet.next()) {
                total = resultSet.getFloat(1);
            }

            return total;

        } catch (Exception e) {
            e.printStackTrace();
            RuntimeException exception = new RuntimeException(e);
            Platform.runLater(() -> genericAlertDialog(Alert.AlertType.ERROR, "", getProp().getString("fetch.total.income"), exception.getMessage()));
            return total;
        } finally {
            closeConnection(conn, pstm, resultSet);
        }

    }

    public float monthIncome() {
        float monthIncome = 0;
        String sql = "SELECT SUM(sale_price) FROM sales WHERE MONTH(sale_date) = ? AND YEAR(sale_date) = ?";

        Connection conn = null;
        PreparedStatement pstm = null;
        ResultSet resultSet = null;

        java.util.Date date = new java.util.Date();
        LocalDate localDate = date.toInstant().atZone(ZoneId.systemDefault()).toLocalDate();

        try {
            conn = ConnectionFactory.createConnectionToMySql();
            pstm = conn.prepareStatement(sql);

            pstm.setInt(1, localDate.getMonthValue());
            pstm.setInt(2, localDate.getYear());

            resultSet = pstm.executeQuery();

            while (resultSet.next()) {
                monthIncome = resultSet.getFloat(1);
            }

            return monthIncome;

        } catch (Exception e) {
            e.printStackTrace();
            RuntimeException exception = new RuntimeException(e);
            Platform.runLater(() -> genericAlertDialog(Alert.AlertType.ERROR, "", getProp().getString("fetch.month.income"), exception.getMessage()));
            return monthIncome;
        } finally {
            closeConnection(conn, pstm, resultSet);
        }

    }

    public float yearIncome() {
        float yearIncome = 0;
        String sql = "SELECT SUM(sale_price) FROM sales WHERE YEAR(sale_date) = ?";

        Connection conn = null;
        PreparedStatement pstm = null;
        ResultSet resultSet = null;

        java.util.Date date = new java.util.Date();
        LocalDate localDate = date.toInstant().atZone(ZoneId.systemDefault()).toLocalDate();

        try {

            conn = ConnectionFactory.createConnectionToMySql();
            pstm = conn.prepareStatement(sql);
            pstm.setInt(1, localDate.getYear());
            resultSet = pstm.executeQuery();

            while (resultSet.next()) {
                yearIncome = resultSet.getFloat(1);
            }

            return yearIncome;

        } catch (Exception e) {
            e.printStackTrace();
            RuntimeException exception = new RuntimeException(e);
            Platform.runLater(() -> genericAlertDialog(Alert.AlertType.ERROR, "", getProp().getString("fetch.year.income"), exception.getMessage()));
            return yearIncome;
        } finally {
            closeConnection(conn, pstm, resultSet);
        }

    }

    private static void closeConnection(Connection conn, PreparedStatement pstm, ResultSet resultSet) {
        try {

            if (conn != null) {
                conn.close();
            }

            if (pstm != null) {
                pstm.close();
            }

            if (resultSet != null) {
                resultSet.close();
            }

        } catch (Exception e) {
            System.out.println("Error When Closing Connections " + e);
        }
    }

}


