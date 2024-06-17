package learnAndCodeFinalProject.src.main.java.org.learnAndCode;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class DatabaseConnection {
    private static final String DB_URL = "jdbc:sqlserver://localhost;databaseName=learnAndCodeFinalProjectDB";
    private static final String USER = "sa";
    private static final String PASS = "Happymonkey5*";

    public static void main(String[] args) {
        Connection connection = null;

        try 
        {
            connection = DriverManager.getConnection(DB_URL, USER, PASS);
            System.out.println("Connected to the database successfully!");
        } 
        catch (SQLException e) 
        {
            e.printStackTrace();
        } 
        finally 
        {
            if (connection != null) 
            {
                try 
                {
                    connection.close();
                } catch (SQLException e) 
                {
                    e.printStackTrace();
                }
            }
        }
    }
}
