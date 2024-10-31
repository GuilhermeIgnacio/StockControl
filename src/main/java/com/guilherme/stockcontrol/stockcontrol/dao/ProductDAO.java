package com.guilherme.stockcontrol.stockcontrol.dao;

import com.guilherme.stockcontrol.stockcontrol.factory.ConnectionFactory;
import com.guilherme.stockcontrol.stockcontrol.model.Product;
import javafx.application.Platform;
import javafx.scene.control.Alert;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;

import static com.guilherme.stockcontrol.stockcontrol.Util.genericAlertDialog;

/**
 * Classe DAO responsável pelas operações de banco de dados relacionadas aos produtos (products) no sistema de controle de estoque.
 */
public class ProductDAO extends StockDAO {

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
            Platform.runLater(() -> genericAlertDialog(Alert.AlertType.ERROR, "", "Erro Ao Buscar Produtos", exception.getMessage()));

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
            Platform.runLater(() -> genericAlertDialog(Alert.AlertType.ERROR, "", "Erro Ao Inserir Produto", exception.getMessage()));
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
            Platform.runLater(() -> genericAlertDialog(Alert.AlertType.ERROR, "", "Erro Ao Atualizar O Produto", exception.getMessage()));
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

        //Consulta SQL para excluir compras associadas ao produto na tabela `buy`
        String buySql = "DELETE FROM buy where product_id = ?";

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

            //Prepara e executa a instrução SQL para excluir a compra associada ao produto
            pstm = conn.prepareStatement(buySql);
            pstm.setInt(1, productId);
            pstm.execute();

            // Prepara e executa a instrução SQL para excluir o produto
            pstm = conn.prepareStatement(sql);
            pstm.setInt(1, productId);
            pstm.execute();


        } catch (Exception e) {
            e.printStackTrace();
            RuntimeException exception = new RuntimeException(e);
            Platform.runLater(() -> genericAlertDialog(Alert.AlertType.ERROR, "", "Erro Ao Deletar O Produto", exception.getMessage()));
        } finally {
            // Fecha a conexão e a instrução para liberar os recursos
            closeConnection(conn, pstm, null);
        }
    }
}
