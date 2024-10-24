package com.guilherme.stockcontrol.stockcontrol.dao;

import com.guilherme.stockcontrol.stockcontrol.factory.ConnectionFactory;
import com.guilherme.stockcontrol.stockcontrol.model.Product;
import com.guilherme.stockcontrol.stockcontrol.model.Sale;
import com.guilherme.stockcontrol.stockcontrol.model.SaleProduct;
import javafx.application.Platform;
import javafx.scene.control.Alert;

import java.sql.*;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import static com.guilherme.stockcontrol.stockcontrol.Util.*;

/**
 * Classe responsável pelo acesso aos dados relacionados ao estoque (DAO - Data Access Object).
 * Esta classe contém métodos para realizar operações no banco de dados,
 * como inserção, atualização, exclusão e consulta de dados de estoque.
 */

public class StockDAO {

    /**
     * Este metodo busca uma lista de produtos no banco de dados com base no nome do produto fornecido.
     * O nome do produto é pesquisado utilizando um padrão `LIKE`, o que permite buscar produtos cujo nome contenha a string fornecida.
     *
     * @param productName O nome ou parte do nome do produto que será usado para buscar os produtos no banco de dados.
     * @return Uma lista de objetos do tipo Product que correspondem à pesquisa.
     */

    public List<Product> fetchItems(String productName) {
        // Consulta SQL que busca todos os produtos cujo nome contenha a string fornecida.
        String sql = "SELECT * FROM products WHERE product_name LIKE ?";
        List<Product> items = new ArrayList<>();

        Connection conn = null;
        PreparedStatement pstm = null;
        ResultSet resultSet = null;

        try {
            // Cria uma conexão com o banco de dados
            conn = ConnectionFactory.createConnectionToMySql();

            // Prepara a instrução SQL com a consulta definida
            pstm = conn.prepareStatement(sql);

            // Define o parâmetro para a consulta, adicionando '%' para permitir busca parcial
            pstm.setString(1, "%" + productName + "%");

            // Executa a consulta e obtém os resultados
            resultSet = pstm.executeQuery();

            // Itera sobre os resultados e cria objetos Product para cada registro encontrado
            while (resultSet.next()) {
                Product product = new Product();

                // Define os valores do produto com base nos dados retornados pelo banco de dados
                product.setProduct_id(resultSet.getInt("product_id"));
                product.setProduct_name(resultSet.getString("product_name"));
                product.setProduct_description(resultSet.getString("product_description"));
                product.setPurchase_price(resultSet.getFloat("purchase_price"));
                product.setRetail_price(resultSet.getFloat("retail_price"));
                product.setStock_quantity(resultSet.getInt("stock_quantity"));
                product.setCreated_at(resultSet.getTimestamp("created_at").toLocalDateTime());
                product.setUpdated_at(resultSet.getTimestamp("updated_at").toLocalDateTime());

                // Adiciona o produto à lista de itens
                items.add(product);
            }

            // Retorna a lista de produtos encontrados
            return items;

        } catch (Exception e) {
            // Impressão da pilha de erros no console
            e.printStackTrace();

            // Lança uma exceção em tempo de execução com a mensagem do erro
            RuntimeException exception = new RuntimeException(e);

            // Exibe um alerta de erro para o usuário em caso de falha ao buscar os produtos
            Platform.runLater(() -> genericAlertDialog(Alert.AlertType.ERROR, "", getProp().getString("fetch.products.error"), exception.getMessage()));

            // Retorna a lista de produtos até o momento, que pode estar vazia
            return items;
        } finally {
            // Fecha a conexão, a instrução e o conjunto de resultados para liberar os recursos
            closeConnection(conn, pstm, resultSet);
        }

    }

    /**
     * Este metodo insere um novo produto no banco de dados.
     * Os valores do produto são passados como parâmetros e inseridos na tabela `products`.
     *
     * @param product O objeto Product que contém as informações do produto a serem inseridas no banco de dados.
     */

    public void insertProduct(Product product) {
        // Consulta SQL para inserir um novo produto no banco de dados.
        String sql = "INSERT INTO products(product_name, product_description, purchase_price, retail_price, stock_quantity) VALUES (?, ?, ?, ?, ?)";

        Connection conn = null;
        PreparedStatement pstm = null;

        try {
            conn = ConnectionFactory.createConnectionToMySql();
            pstm = conn.prepareStatement(sql);

            // Define os parâmetros com base nos valores do objeto Product fornecido
            pstm.setString(1, product.getProduct_name());
            pstm.setString(2, product.getProduct_description());
            pstm.setFloat(3, product.getPurchase_price());
            pstm.setFloat(4, product.getRetail_price());
            pstm.setInt(5, product.getStock_quantity());

            // Executa a inserção no banco de dados
            pstm.execute();

        } catch (Exception e) {
            e.printStackTrace();
            RuntimeException exception = new RuntimeException(e);
            Platform.runLater(() -> genericAlertDialog(Alert.AlertType.ERROR, "", getProp().getString("insert.product.error"), exception.getMessage()));
        } finally {
            // Executa a atualização no banco de dados
            closeConnection(conn, pstm, null);
        }

    }

    /**
     * Este metodo atualiza as informações de um produto existente no banco de dados.
     * As informações do produto, como nome, descrição, preços e quantidade em estoque, são atualizadas com base no ID do produto.
     *
     * @param product O objeto Product que contém as informações atualizadas do produto a serem persistidas no banco de dados.
     */

    public void updateProduct(Product product) {
        // Consulta SQL para atualizar um produto no banco de dados, identificando-o pelo ID
        String sql = "UPDATE products SET product_name = ?, product_description = ?, purchase_price = ?, retail_price = ?, stock_quantity = ? WHERE product_id = ?";

        Connection conn = null;
        PreparedStatement pstm = null;

        try {
            // Cria uma conexão com o banco de dados
            conn = ConnectionFactory.createConnectionToMySql();

            // Prepara a instrução SQL com a consulta definida
            pstm = conn.prepareStatement(sql);

            // Define os parâmetros com base nos valores do objeto Product fornecido
            pstm.setString(1, product.getProduct_name());
            pstm.setString(2, product.getProduct_description());
            pstm.setFloat(3, product.getPurchase_price());
            pstm.setFloat(4, product.getRetail_price());
            pstm.setFloat(5, product.getStock_quantity());
            pstm.setInt(6, product.getProduct_id());

            // Executa a atualização no banco de dados
            pstm.execute();

        } catch (Exception e) {
            e.printStackTrace();
            RuntimeException exception = new RuntimeException(e);
            Platform.runLater(() -> genericAlertDialog(Alert.AlertType.ERROR, "", getProp().getString("update.product.error"), exception.getMessage()));
        } finally {
            // Executa a atualização no banco de dados
            closeConnection(conn, pstm, null);
        }
    }

    /**
     * Este metodo exclui um produto do banco de dados com base no ID fornecido.
     * O produto é removido da tabela `products` e também todas as vendas relacionadas a esse produto são removidas da tabela `sales`.
     *
     * @param productId O ID do produto que será excluído do banco de dados.
     */

    public void deleteItemById(int productId) {
        // Consulta SQL para excluir as vendas associadas ao produto na tabela `sales`
        String salesSql = "DELETE FROM sales WHERE product_id = ?";

        // Consulta SQL para excluir o produto da tabela `products`
        String sql = "DELETE FROM products WHERE product_id = ?";

        Connection conn = null;
        PreparedStatement pstm = null;

        try {
            conn = ConnectionFactory.createConnectionToMySql();

            // Prepara e executa a instrução SQL para excluir as vendas associadas ao produto
            pstm = conn.prepareStatement(salesSql);
            pstm.setInt(1, productId);
            pstm.execute();

            // Prepara e executa a instrução SQL para excluir o produto
            pstm = conn.prepareStatement(sql);
            pstm.setInt(1, productId);
            pstm.execute();


        } catch (Exception e) {
            e.printStackTrace();
            RuntimeException exception = new RuntimeException(e);
            Platform.runLater(() -> genericAlertDialog(Alert.AlertType.ERROR, "", getProp().getString("delete.product.error"), exception.getMessage()));
        } finally {
            // Fecha a conexão e a instrução para liberar os recursos
            closeConnection(conn, pstm, null);
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
                pstm.setString(paramIndex++, formatDate(endDate)); // Data de término
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
     * Metodo utilitário responsável por fechar as conexões de banco de dados.
     * Fecha a conexão com o banco de dados.
     * Este metodo deve ser chamado no bloco `finally` após a execução de operações com banco de dados
     * para garantir que os recursos sejam liberados corretamente, evitando vazamentos de memória ou
     * conexões abertas indevidamente.
     *
     * @param conn      A conexão com o banco de dados que será fechada (pode ser nula).
     * @param pstm      O PreparedStatement que será fechado (pode ser nulo).
     * @param resultSet O ResultSet que será fechado (pode ser nulo).
     */
    private static void closeConnection(Connection conn, PreparedStatement pstm, ResultSet resultSet) {
        try {
            // Fecha a conexão com o banco de dados, se ela não for nula
            if (conn != null) {
                conn.close();
            }

            // Fecha o PreparedStatement, se ele não for nulo
            if (pstm != null) {
                pstm.close();
            }

            // Fecha o ResultSet, se ele não for nulo
            if (resultSet != null) {
                resultSet.close();
            }

        } catch (Exception e) {
            // Em caso de erro ao fechar as conexões, imprime a mensagem de erro
            System.out.println("Error When Closing Connections " + e);
        }
    }


}


