package com.guilherme.stockcontrol.stockcontrol.dao;

import com.guilherme.stockcontrol.stockcontrol.factory.ConnectionFactory;
import com.guilherme.stockcontrol.stockcontrol.model.Sale;
import com.guilherme.stockcontrol.stockcontrol.model.SaleProduct;
import javafx.application.Platform;
import javafx.scene.control.Alert;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import static com.guilherme.stockcontrol.stockcontrol.Util.*;

/**
 * Todo: Comentários Aqui
 */
public class SalesDAO extends StockDAO {
    /**
     * Este metodo busca as vendas de produtos com base no nome do produto, data de início e data de término fornecidos.
     * Retorna uma lista de objetos `SaleProduct` contendo informações sobre a venda, como ID da venda, nome do produto,
     * preço de venda e data da venda.
     *
     * @param productName O nome do produto que será usado para filtrar os resultados. Se for nulo ou vazio, não será aplicado o filtro por nome.
     * @param startDate   A data de início no formato 'YYYY-MM-DD' para filtrar as vendas. Se for nula ou vazia, o filtro de data de início não será aplicado.
     * @param endDate     A data de término no formato 'YYYY-MM-DD' para filtrar as vendas. Se for nula ou vazia, o filtro de data de término não será aplicado.
     * @return Uma lista de objetos `SaleProduct` contendo informações sobre as vendas que correspondem aos filtros aplicados.
     */
    public List<SaleProduct> fetchSaleProduct(String productName, String startDate, String endDate) {

        // Lista que armazenará os resultados das vendas filtradas
        List<SaleProduct> saleProducts = new ArrayList<>();

        // Construção da query SQL com possíveis condições dinâmicas (filtros)
        StringBuilder sql = new StringBuilder(
                "SELECT s.sale_id, p.product_name, p.product_description, s.quantity, s.sale_price, s.price_unit, s.sale_date " +
                        "FROM sales s " +
                        "JOIN products p ON s.product_id = p.product_id " +
                        "WHERE 1=1 ");

        // Adiciona o filtro por nome do produto, caso tenha sido fornecido
        if (productName != null && !productName.isEmpty()) {
            sql.append("AND p.product_name LIKE ? ");
        }

        // Adiciona o filtro por data de início, caso tenha sido fornecido
        if (startDate != null && !startDate.isEmpty()) {
            sql.append("AND s.sale_date >= ? ");
        }

        // Adiciona o filtro por data de término, caso tenha sido fornecido
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
            resultSet = pstm.executeQuery();

            // Itera sobre os resultados e cria objetos SaleProduct para cada linha
            while (resultSet.next()) {

                SaleProduct saleProduct = new SaleProduct();
                saleProduct.setSaleId(resultSet.getInt("sale_id"));
                saleProduct.setProductName(resultSet.getString("product_name"));
                saleProduct.setQuantity(resultSet.getInt("quantity"));
                saleProduct.setSalePrice(resultSet.getFloat("sale_price"));
                saleProduct.setPriceUnit(resultSet.getFloat("price_unit"));
                saleProduct.setSaleDate(resultSet.getTimestamp("sale_date").toLocalDateTime());

                saleProducts.add(saleProduct);

            }

            return saleProducts;

        } catch (Exception e) {
            e.printStackTrace();
            RuntimeException exception = new RuntimeException(e);
            Platform.runLater(() -> genericAlertDialog(Alert.AlertType.ERROR, "", getProp().getString("fetch.sales.error"), exception.getMessage()));

            return saleProducts;
        } finally {
            // Fecha a conexão, a prepared statement e o resultSet para liberar os recursos
            closeConnection(conn, pstm, resultSet);
        }

    }

    /**
     * Insere uma lista de vendas no banco de dados.
     * Este metodo realiza a inserção em lote de múltiplas vendas, onde cada venda inclui:
     * o ID do produto, a quantidade vendida, o valor total da venda e o preço por unidade.
     * <p>
     * O metodo desativa o commit automático, adiciona cada venda ao batch e, em seguida,
     * executa todas as inserções em um único comando. Em caso de erro, um rollback é
     * realizado para desfazer as alterações. Uma mensagem de erro é exibida ao usuário
     * através de um alerta genérico.
     *
     * @param saleList Lista de vendas a serem inseridas no banco de dados.
     */
    public void insertSale(List<Sale> saleList) {
        String sql = "INSERT INTO sales (product_id, quantity, sale_price, price_unit) VALUES (?, ?, ?, ?)";

        Connection conn = null;
        PreparedStatement pstm = null;

        try {
            conn = ConnectionFactory.createConnectionToMySql();
            conn.setAutoCommit(false);  // Desativa o commit automático para controle manual

            pstm = conn.prepareStatement(sql);

            for (Sale sale : saleList) {
                pstm.setInt(1, sale.getProductId());
                pstm.setInt(2, sale.getQuantity());
                pstm.setFloat(3, sale.getSalePrice());
                pstm.setFloat(4, sale.getPriceUnit());
                pstm.addBatch();  // Adiciona ao batch em vez de executar imediatamente
            }

            pstm.executeBatch();  // Executa todos os comandos em lote
            conn.commit();  // Efetua o commit da transação

        } catch (Exception e) {
            if (conn != null) {
                try {
                    conn.rollback();  // Faz rollback em caso de erro
                } catch (SQLException rollbackEx) {
                    rollbackEx.printStackTrace();  // Adiciona um log mais apropriado aqui
                }
            }
            Platform.runLater(() -> genericAlertDialog(Alert.AlertType.ERROR, "", getProp().getString("insert.sale.error"), e.getMessage()));
            e.printStackTrace();
        } finally {
            closeConnection(conn, pstm, null);
        }
    }

    /**
     * Todo: Comentário Aqui
     */
    public void updateSale(SaleProduct saleProduct) {
        String sql = "UPDATE sales SET quantity = ?, sale_price = ?, price_unit = ? WHERE sale_id = ?";

        Connection conn = null;
        PreparedStatement pstm = null;

        try {
            conn = ConnectionFactory.createConnectionToMySql();
            pstm = conn.prepareStatement(sql);

            pstm.setInt(1, saleProduct.getQuantity());
            pstm.setFloat(2, saleProduct.getSalePrice());
            pstm.setFloat(3, saleProduct.getPriceUnit());
            pstm.setInt(4, saleProduct.getSaleId());

            pstm.executeUpdate();

        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            closeConnection(conn, pstm, null);
        }

    }

    /**
     * Todo: Comentário Aqui
     */
    public void deleteSale(List<Integer> saleIds) {
        String sql = "DELETE FROM sales WHERE sale_id IN (" +
                saleIds.stream().map(id -> "?").collect(Collectors.joining(", ")) +
                ")";

        Connection conn = null;
        PreparedStatement pstm = null;

        try {
            conn = ConnectionFactory.createConnectionToMySql();
            pstm = conn.prepareStatement(sql);

            for (int i = 0; i < saleIds.size(); i++) {
                pstm.setInt(i + 1, saleIds.get(i));
            }

            pstm.executeUpdate();

        } catch (Exception e) {
            e.printStackTrace();
            RuntimeException exception = new RuntimeException(e);
            genericAlertDialog(Alert.AlertType.ERROR, "", "Erro ao excluir venda(s)", exception.getMessage());
        } finally {
            closeConnection(conn, pstm, null);
        }

    }

    /**
     * Este metodo retorna o total de vendas de um produto específico com base no seu ID.
     * Ele executa uma consulta SQL que conta o número de registros de vendas associadas ao ID do produto fornecido.
     *
     * @param productId O ID do produto para o qual o total de vendas será calculado.
     * @return O número total de vendas do produto correspondente ao ID fornecido.
     */
    public int getProductTotalSales(int productId) {
        // Variável para armazenar o total de vendas
        int total = 0;

        // Query SQL que conta o número de vendas de um produto específico
        String sql = "SELECT SUM(quantity) from sales where product_id = ?";

        Connection conn = null;
        PreparedStatement pstm = null;
        ResultSet resultSet = null;

        try {

            conn = ConnectionFactory.createConnectionToMySql();
            pstm = conn.prepareStatement(sql);

            pstm.setString(1, String.valueOf(productId));

            resultSet = pstm.executeQuery();

            // Processando o resultado da query
            if (resultSet.next()) {
                total = resultSet.getInt(1);
            }

            // Retorna o total de vendas do produto
            return total;

        } catch (Exception e) {
            e.printStackTrace();
            RuntimeException exception = new RuntimeException(e);
            Platform.runLater(() -> genericAlertDialog(Alert.AlertType.ERROR, "", getProp().getString("fetch.product.total.sales"), exception.getMessage()));

            // Retorna o total, que será 0 em caso de erro
            return total;
        } finally {
            // Fecha a conexão, a prepared statement e o result set para liberar os recursos
            closeConnection(conn, pstm, resultSet);
        }

    }

    /**
     * Este metodo retorna o valor total arrecadado em vendas de um produto específico, com base no seu ID.
     * Ele executa uma consulta SQL que soma os preços de venda associados ao ID do produto fornecido.
     *
     * @param productId O ID do produto para o qual o valor total de vendas será calculado.
     * @return O valor total arrecadado nas vendas do produto correspondente ao ID fornecido.
     */
    public float getProductSaleAmount(int productId) {
        float saleAmount = 0;

        String sql = "SELECT SUM(sale_price) FROM sales WHERE product_id = ?";

        Connection conn = null;
        PreparedStatement pstm = null;
        ResultSet resultSet = null;

        try {
            conn = ConnectionFactory.createConnectionToMySql();
            pstm = conn.prepareStatement(sql);

            // Definindo o valor do parâmetro productId na query
            pstm.setInt(1, productId);

            // Executando a query
            resultSet = pstm.executeQuery();

            // Processando o resultado da query
            while (resultSet.next()) {
                saleAmount = resultSet.getInt(1);
            }

            // Retorna o valor total das vendas do produto
            return saleAmount;

        } catch (Exception e) {
            e.printStackTrace();
            RuntimeException exception = new RuntimeException(e);
            Platform.runLater(() -> genericAlertDialog(Alert.AlertType.ERROR, "", getProp().getString("fetch.product.sale.amount"), exception.getMessage()));

            // Retorna o valor total, que será 0 em caso de erro
            return saleAmount;
        } finally {
            // Fecha a conexão, a prepared statement e o result set para liberar os recursos
            closeConnection(conn, pstm, resultSet);
        }

    }

    /**
     * Este metodo retorna a receita total obtida a partir de todas as vendas registradas no banco de dados.
     * Ele executa uma consulta SQL que soma os preços de todas as vendas na tabela de vendas.
     *
     * @return O valor total das vendas como um float.
     */
    public float totalIncome() {
        float total = 0f;

        // Query SQL que soma os valores de todas as vendas
        String sql = "SELECT SUM(sale_price) FROM sales";

        Connection conn = null;
        PreparedStatement pstm = null;
        ResultSet resultSet = null;

        try {
            conn = ConnectionFactory.createConnectionToMySql();
            pstm = conn.prepareStatement(sql);
            resultSet = pstm.executeQuery();

            // Processando o resultado da query
            while (resultSet.next()) {
                total = resultSet.getFloat(1); // Obtém a soma dos valores de venda
            }

            // Retorna o valor total da receita
            return total;

        } catch (Exception e) {
            e.printStackTrace();
            RuntimeException exception = new RuntimeException(e);
            Platform.runLater(() -> genericAlertDialog(Alert.AlertType.ERROR, "", getProp().getString("fetch.total.income"), exception.getMessage()));

            // Retorna o valor total, que será 0 em caso de erro
            return total;
        } finally {
            // Fecha a conexão, a prepared statement e o result set para liberar os recursos
            closeConnection(conn, pstm, resultSet);
        }

    }

    /**
     * Este metodo retorna a receita total obtida no mês atual a partir de todas as vendas registradas no banco de dados.
     * Ele executa uma consulta SQL que soma os preços de todas as vendas cujo mês e ano correspondem ao mês e ano atuais.
     *
     * @return O valor total das vendas do mês atual como um float.
     */
    public float monthIncome() {
        // Variável para armazenar o valor total da receita do mês
        float monthIncome = 0;

        // Query SQL que soma os valores de vendas no mês e ano atuais
        String sql = "SELECT SUM(sale_price) FROM sales WHERE MONTH(sale_date) = ? AND YEAR(sale_date) = ?";

        Connection conn = null;
        PreparedStatement pstm = null;
        ResultSet resultSet = null;

        // Obtém a data atual
        java.util.Date date = new java.util.Date();
        LocalDate localDate = date.toInstant().atZone(ZoneId.systemDefault()).toLocalDate();

        try {
            conn = ConnectionFactory.createConnectionToMySql();
            pstm = conn.prepareStatement(sql);

            // Define os parâmetros de mês e ano para a consulta SQL
            pstm.setInt(1, localDate.getMonthValue());  // Define o mês atual
            pstm.setInt(2, localDate.getYear());        // Define o ano atual

            // Executa a consulta SQL
            resultSet = pstm.executeQuery();

            // Processa o resultado da consulta
            while (resultSet.next()) {
                monthIncome = resultSet.getFloat(1); // Obtém a soma dos valores de venda do mês atual
            }

            // Retorna o valor total da receita do mês
            return monthIncome;

        } catch (Exception e) {
            e.printStackTrace();
            RuntimeException exception = new RuntimeException(e);
            Platform.runLater(() -> genericAlertDialog(Alert.AlertType.ERROR, "", getProp().getString("fetch.month.income"), exception.getMessage()));
            return monthIncome; // Retorna o valor total (0 em caso de erro)
        } finally {
            // Fecha a conexão, a prepared statement e o result set para liberar os recursos
            closeConnection(conn, pstm, resultSet);
        }

    }

    /**
     * Este metodo retorna a receita total obtida no ano atual a partir de todas as vendas registradas no banco de dados.
     * Ele executa uma consulta SQL que soma os preços de todas as vendas cujo ano corresponde ao ano atual.
     *
     * @return O valor total das vendas do ano atual como um float.
     */
    public float yearIncome() {
        // Variável para armazenar o valor total da receita do ano
        float yearIncome = 0;

        // Query SQL que soma os valores de vendas no ano atual
        String sql = "SELECT SUM(sale_price) FROM sales WHERE YEAR(sale_date) = ?";

        Connection conn = null;
        PreparedStatement pstm = null;
        ResultSet resultSet = null;

        // Obtém a data atual
        java.util.Date date = new java.util.Date();
        LocalDate localDate = date.toInstant().atZone(ZoneId.systemDefault()).toLocalDate();

        try {

            conn = ConnectionFactory.createConnectionToMySql();
            pstm = conn.prepareStatement(sql);

            // Define o ano atual como parâmetro da consulta SQL
            pstm.setInt(1, localDate.getYear());

            // Executa a consulta SQL
            resultSet = pstm.executeQuery();

            // Processa o resultado da consulta
            while (resultSet.next()) {
                yearIncome = resultSet.getFloat(1);
            }

            // Retorna o valor total da receita do ano
            return yearIncome;

        } catch (Exception e) {
            e.printStackTrace();
            RuntimeException exception = new RuntimeException(e);
            Platform.runLater(() -> genericAlertDialog(Alert.AlertType.ERROR, "", getProp().getString("fetch.year.income"), exception.getMessage()));
            return yearIncome; // Retorna o valor total (0 em caso de erro)
        } finally {
            // Fecha a conexão, a prepared statement e o result set para liberar os recursos
            closeConnection(conn, pstm, resultSet);
        }

    }
}
