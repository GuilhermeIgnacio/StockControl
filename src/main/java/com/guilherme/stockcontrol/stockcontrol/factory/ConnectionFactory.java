package com.guilherme.stockcontrol.stockcontrol.factory;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

/**
 * A classe ConnectionFactory é responsável por criar e gerenciar a conexão com o banco de dados MySQL.
 * As credenciais de conexão, como nome de usuário, senha e URL do banco de dados, são lidas de um arquivo de propriedades.
 */
public class ConnectionFactory {

    // Nome de usuário para a conexão com o banco de dados, lido de um arquivo de propriedades.
    private static final String USERNAME = "root";

    // Senha para a conexão com o banco de dados, lida de um arquivo de propriedades.
    private static final String PASSWORD = "";

    // URL do banco de dados, lida de um arquivo de propriedades.
    private static final String DATABASE_URL = "jdbc:mariadb://localhost:3306/StockDb";

    /**
     * Cria e retorna uma conexão com o banco de dados MySQL usando o DriverManager.
     *
     * @return A conexão estabelecida com o banco de dados MySQL.
     * @throws Exception Se ocorrer algum erro ao tentar se conectar ao banco de dados.
     */
    public static Connection createConnectionToMySql() throws Exception {

        //Class.forName("com.mysql.jdbc.Driver"); Deprecated

        // O metodo Class.forName() que carrega o driver do MySQL foi depreciado, não é mais necessário.
        return DriverManager.getConnection(DATABASE_URL, USERNAME, PASSWORD);

    }

    /**
     * Metodo principal para testar a conexão com o banco de dados.
     * Se a conexão for bem-sucedida, exibe uma mensagem de sucesso e fecha a conexão.
     *
     * @param args Argumentos da linha de comando (não utilizados).
     * @throws Exception Se ocorrer algum erro durante o processo de conexão ou ao fechar a conexão.
     */
    public static void main(String[] args) throws Exception {
        Connection con = createConnectionToMySql();

        if (con != null) {
            System.out.println("Connection Retrieved");
            con.close();
        }

    }

}
