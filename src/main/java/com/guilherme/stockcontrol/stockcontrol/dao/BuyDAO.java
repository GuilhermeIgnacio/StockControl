package com.guilherme.stockcontrol.stockcontrol.dao;

import com.guilherme.stockcontrol.stockcontrol.factory.ConnectionFactory;
import com.guilherme.stockcontrol.stockcontrol.model.Buy;
import com.guilherme.stockcontrol.stockcontrol.model.BuyDetails;
import javafx.application.Platform;
import javafx.scene.control.Alert;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

import static com.guilherme.stockcontrol.stockcontrol.Util.*;

/**
 * Todo: Comentários Aqui
 */
public class BuyDAO extends StockDAO {

    /**
     * Todo: Comentários Aqui
     */
    public List<BuyDetails> fetchBuys(String productName, String startDate, String endDate) {

        // Lista que armazenará os resultados das vendas filtradas
        List<BuyDetails> buyDetailsList = new ArrayList<>();

        // Construção da query SQL com possíveis condições dinâmicas (filtros)
        StringBuilder sql = new StringBuilder(
                "SELECT buy.buy_id, buy.product_id, buy.quantity, buy.buy_price, buy.buy_price_unit, buy.buy_date, products.product_name " +
                        "FROM buy " +
                        "JOIN products ON buy.product_id = products.product_id WHERE 1=1 ");

        // Adiciona o filtro por nome do produto, caso tenha sido fornecido
        if (productName != null && !productName.isEmpty()) {
            sql.append("AND products.product_name LIKE ? ");
        }

        // Adiciona o filtro por data de início, caso tenha sido fornecido
        if (startDate != null && !startDate.isEmpty()) {
            sql.append("AND buy.buy_date >= ? ");
        }

        // Adiciona o filtro por data de término, caso tenha sido fornecido
        if (endDate != null && !endDate.isEmpty()) {
            sql.append("AND buy.buy_date <= ? ");
        }

        Connection conn = null;
        PreparedStatement pstm = null;
        ResultSet rs = null;

        try {

            conn = ConnectionFactory.createConnectionToMySql();
            pstm = conn.prepareStatement(sql.toString());

            int paramIndex = 1;

            // Definindo os parâmetros dinamicamente conforme a entrada do usuário
            if (productName != null && !productName.isEmpty()) {
                pstm.setString(paramIndex++, "%" + productName + "%"); // Pesquisa com LIKE
            }
            if (startDate != null && !startDate.isEmpty()) {
                pstm.setString(paramIndex++, formatDate(startDate)); // Data de início
            }
            if (endDate != null && !endDate.isEmpty()) {
                pstm.setString(paramIndex++, formatDate(endDate) + " 23:59:59"); // Data de término
            }

            // Executa a query e processa os resultados
            rs = pstm.executeQuery();

            // Itera sobre os resultados e cria objetos SaleProduct para cada linha
            while (rs.next()) {

                BuyDetails buyDetails = new BuyDetails();
                buyDetails.setBuyId(rs.getInt("buy_id"));
                buyDetails.setProductId(rs.getInt("product_id"));
                buyDetails.setProductName(rs.getString("product_name"));
                buyDetails.setQuantity(rs.getInt("quantity"));
                buyDetails.setBuyPrice(rs.getFloat("buy_price"));
                buyDetails.setBuyPriceUnit(rs.getFloat("buy_price_unit"));
                buyDetails.setBuyDate(rs.getTimestamp("buy_date").toLocalDateTime());

                buyDetailsList.add(buyDetails);

            }

            return buyDetailsList;

        } catch (Exception e) {
            e.printStackTrace();
            RuntimeException exception = new RuntimeException(e);
            Platform.runLater(() -> genericAlertDialog(Alert.AlertType.ERROR, "", getProp().getString("fetch.sales.error"), exception.getMessage()));

            return buyDetailsList;
        } finally {
            // Fecha a conexão, a prepared statement e o resultSet para liberar os recursos
            closeConnection(conn, pstm, rs);
        }

    }

    /**
     * Todo: Comentários Aqui
     */
    public void insertBuy(Buy buy) {
        String sql = "INSERT INTO buy (product_id, quantity, buy_price, buy_price_unit) VALUES (?, ?, ?, ?)";

        Connection conn = null;
        PreparedStatement pstm = null;

        try {
            conn = ConnectionFactory.createConnectionToMySql();
            pstm = conn.prepareStatement(sql);

            pstm.setInt(1, buy.getProductId());
            pstm.setInt(2, buy.getQuantity());
            pstm.setFloat(3, buy.getBuyPrice());
            pstm.setFloat(4, buy.getBuyPriceUnit());

            pstm.execute();

        } catch (Exception e) {
            RuntimeException runtimeException = new RuntimeException(e);
            runtimeException.printStackTrace();
            genericAlertDialog(Alert.AlertType.ERROR, "", "Erro ao Inserir Compra.", runtimeException.getMessage());
        } finally {
            closeConnection(conn, pstm, null);
        }

    }

    /**
     * Todo: Comentários Aqui
     */
    public void updateBuy(Buy buy) {
        String sql = "UPDATE buy SET quantity = ?, buy_price = ?, buy_price_unit = ? WHERE buy_id = ?";

        Connection conn = null;
        PreparedStatement pstm = null;

        try {
            conn = ConnectionFactory.createConnectionToMySql();
            pstm = conn.prepareStatement(sql);

            pstm.setInt(1, buy.getQuantity());
            pstm.setFloat(2, buy.getBuyPrice());
            pstm.setFloat(3, buy.getBuyPriceUnit());
            pstm.setInt(4, buy.getBuyId());

            pstm.execute();

        } catch (Exception e) {
            RuntimeException runtimeException = new RuntimeException(e);
            genericAlertDialog(Alert.AlertType.ERROR, "", "Erro ao atualizar compra", runtimeException.getMessage());
        } finally {
            closeConnection(conn, pstm, null);
        }

    }

    /**
     * Todo: Comentários Aqui
     */
    public void deleteBuy(List<Integer> buyIds) {
        String sql = "DELETE FROM buy WHERE buy_id IN (" +
                buyIds.stream().map(id -> "?").collect(Collectors.joining(", ")) +
                ")";

        Connection conn = null;
        PreparedStatement pstm = null;

        try {
            conn = ConnectionFactory.createConnectionToMySql();
            pstm = conn.prepareStatement(sql);

            for (int i = 0; i < buyIds.size(); i++) {
                pstm.setInt(i + 1, buyIds.get(i));
            }

            pstm.executeUpdate();

        } catch (Exception e) {
            e.printStackTrace();
            RuntimeException exception = new RuntimeException(e);
            genericAlertDialog(Alert.AlertType.ERROR, "", "Erro ao excluir compras(s)", exception.getMessage());
        } finally {
            closeConnection(conn, pstm, null);
        }

    }

    /**
     * Todo: Comentários Aqui
     */
    public float fetchTotalExpense() {
        float totalExpense = 0;
        String sql = "SELECT SUM(buy_price) FROM buy";

        Connection conn = null;
        PreparedStatement pstm = null;
        ResultSet rs = null;

        try {

            conn = ConnectionFactory.createConnectionToMySql();
            pstm = conn.prepareStatement(sql);
            rs = pstm.executeQuery();

            if (rs.next()) {
                totalExpense = rs.getFloat(1);
            }

            return totalExpense;

        } catch (Exception e) {
            RuntimeException runtimeException = new RuntimeException(e);
            genericAlertDialog(Alert.AlertType.ERROR, "", "Erro ao buscar gasto total", runtimeException.getMessage());
            return totalExpense;
        } finally {
            closeConnection(conn, pstm, rs);
        }

    }

    /**
     * Todo: Comentários Aqui
     */
    public float fetchMonthExpense() {
        float monthExpense = 0;
        String sql = "SELECT SUM(buy_price) FROM buy WHERE MONTH(buy_date) = ? AND YEAR(buy_date) = ?";

        Connection conn = null;
        PreparedStatement pstm = null;
        ResultSet rs = null;

        try {

            conn = ConnectionFactory.createConnectionToMySql();
            pstm = conn.prepareStatement(sql);


            Date date = new Date();
            LocalDate localDate = date.toInstant().atZone(ZoneId.systemDefault()).toLocalDate();

            pstm.setInt(1, localDate.getMonthValue());
            pstm.setInt(2, localDate.getYear());

            rs = pstm.executeQuery();

            if (rs.next()) {
                monthExpense = rs.getFloat(1);
            }

            return monthExpense;

        } catch (Exception e) {
            RuntimeException runtimeException = new RuntimeException(e);
            genericAlertDialog(Alert.AlertType.ERROR, "", "Erro ao buscar gasto total do mês", runtimeException.getMessage());
            return monthExpense;
        } finally {
            closeConnection(conn, pstm, rs);
        }
    }

    /**
     * Todo: Comentários Aqui
     */
    public float fetchYearExpense() {
        float yearExpense = 0;
        String sql = "SELECT SUM(buy_price) FROM buy WHERE YEAR(buy_date) = ?";

        Connection conn = null;
        PreparedStatement pstm = null;
        ResultSet rs = null;

        try {
            conn = ConnectionFactory.createConnectionToMySql();
            pstm = conn.prepareStatement(sql);

            Date date = new Date();
            LocalDate localDate = date.toInstant().atZone(ZoneId.systemDefault()).toLocalDate();

            pstm.setInt(1, localDate.getYear());

            rs = pstm.executeQuery();

            if (rs.next()) {
                yearExpense = rs.getFloat(1);
            }

            return yearExpense;

        } catch (Exception e) {
            e.printStackTrace();
            RuntimeException runtimeException = new RuntimeException(e);
            genericAlertDialog(Alert.AlertType.ERROR, "", "Erro ao buscar gasto total do ano", runtimeException.getMessage());
            return yearExpense;

        } finally {
            closeConnection(conn, pstm, rs);
        }

    }
}
