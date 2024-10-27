package com.guilherme.stockcontrol.stockcontrol.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

/**
 * Classe responsável pelo acesso aos dados relacionados ao estoque (DAO - Data Access Object).
 * Esta classe contém métodos para realizar operações no banco de dados,
 * como inserção, atualização, exclusão e consulta de dados de estoque.
 */

public abstract class StockDAO {

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
    protected void closeConnection(Connection conn, PreparedStatement pstm, ResultSet resultSet) {
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


