package com.guilherme.stockcontrol.stockcontrol.factory;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

import static com.guilherme.stockcontrol.stockcontrol.Util.getProp;

public class ConnectionFactory {
    private static final String USERNAME = getProp().getString("sql.username");

    private static final String PASSWORD = getProp().getString("sql.password");

    private static final String DATABASE_URL = getProp().getString("sql.database.url");

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
