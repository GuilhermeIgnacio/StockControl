package com.guilherme.stockcontrol.stockcontrol.factory;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class ConnectionFactory {
    private static final String USERNAME = "root";

    private static final String PASSWORD = "";

    private static final String DATABASE_URL = "jdbc:mysql://localhost:3306/StockDb";

    public static Connection createConnectionToMySql() throws Exception {

        //Class.forName("com.mysql.jdbc.Driver"); Deprecated

        return DriverManager.getConnection(DATABASE_URL, USERNAME, PASSWORD);

    }

    public static void main(String[] args) throws Exception {
        Connection con = createConnectionToMySql();

        if (con != null) {
            System.out.println("Connection Retrieved");
            con.close();
        }

    }

}
