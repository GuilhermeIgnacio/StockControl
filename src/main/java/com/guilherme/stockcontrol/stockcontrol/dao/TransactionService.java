package com.guilherme.stockcontrol.stockcontrol.dao;

import com.guilherme.stockcontrol.stockcontrol.factory.ConnectionFactory;
import com.guilherme.stockcontrol.stockcontrol.model.Buy;
import com.guilherme.stockcontrol.stockcontrol.model.Product;

import java.sql.*;

/**
 * Serviço responsável por operações de transação envolvendo múltiplas inserções relacionadas a produtos e compras.
 * Realiza operações que necessitam de consistência entre tabelas, garantindo integridade dos dados com transações.
 */
public class TransactionService extends StockDAO {

    private ProductDAO productDAO;
    private BuyDAO buyDAO;

    public TransactionService(ProductDAO productDAO, BuyDAO buyDAO) {
        this.productDAO = productDAO;
        this.buyDAO = buyDAO;
    }

    /**
     * Insere um novo produto e registra uma compra associada em uma transação única.
     *
     * @param product Objeto Product que contém as informações do produto a ser inserido.
     * @param buy Objeto Buy que contém os detalhes da compra associada ao produto inserido.
     *
     * Este metodo inicia uma transação para garantir que tanto o produto quanto a compra
     * sejam inseridos com sucesso. Em caso de erro, a transação é revertida para manter a integridade dos dados.
     */
    public void insertProductAndBuy(Product product, Buy buy) {
        String productSql = "INSERT INTO products(product_name, product_description, purchase_price, retail_price, stock_quantity) VALUES (?, ?, ?, ?, ?)";
        String buySql = "INSERT INTO buy (product_id, quantity, buy_price, buy_price_unit) VALUES (?, ?, ?, ?)";
        Connection conn = null;
        PreparedStatement productStmt = null;
        PreparedStatement buyStmt = null;
        ResultSet rs = null;

        try {
            conn = ConnectionFactory.createConnectionToMySql();

            // Inicia a transação
            conn.setAutoCommit(false);

            // Prepara e executa a inserção do produto
            productStmt = conn.prepareStatement(productSql, Statement.RETURN_GENERATED_KEYS);
            productStmt.setString(1, product.getProduct_name());
            productStmt.setString(2, product.getProduct_description());
            productStmt.setFloat(3, product.getPurchase_price());
            productStmt.setFloat(4, product.getRetail_price());
            productStmt.setInt(5, product.getStock_quantity());
            productStmt.executeUpdate();

            // Recupera o product_id gerado
            rs = productStmt.getGeneratedKeys();
            int productId;
            if (rs.next()) {
                productId = rs.getInt(1);
            } else {
                throw new RuntimeException("Erro ao obter o ID do produto.");
            }

            // Prepara e executa a inserção da compra usando o product_id recuperado
            buyStmt = conn.prepareStatement(buySql);
            buyStmt.setInt(1, productId);
            buyStmt.setInt(2, buy.getQuantity());
            buyStmt.setFloat(3, buy.getBuyPrice());
            buyStmt.setFloat(4, buy.getBuyPriceUnit());
            buyStmt.executeUpdate();

            // Confirma a transação
            conn.commit();

        } catch (Exception e) {
            e.printStackTrace();
            try {
                if (conn != null) {
                    conn.rollback(); // Reverte a transação em caso de erro
                }
            } catch (SQLException ex) {
                ex.printStackTrace();
            }
        } finally {
            closeConnection(conn, productStmt, rs);
            closeConnection(null, buyStmt, null);
        }
    }

}

