package org.learnAndCode.Database;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class DBConnection {
    private static final String URL = "jdbc:sqlserver://localhost;databaseName=learnAndCodeFinalProjectDB";
    private static final String USER = "sa";
    private static final String PASSWORD = "Happymonkey5*";

    public static Connection getConnection() throws SQLException {
        Connection connection = null;
        try {
            connection = DriverManager.getConnection(URL, USER, PASSWORD);
            System.out.println("Connected to the database");
        } catch (SQLException e) {
            System.out.println("Connection failed: " + e.getMessage());
            throw e;
        }
        return connection;
    }
}
