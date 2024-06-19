package org.learnAndCode.Database;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class Login {

    public static User validateLogin(String username, String password) {
        String query = "SELECT * FROM dbo.\"User\" WHERE name = ? AND password = ?";
        try (Connection connection = DBConnection.getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(query)) {

            preparedStatement.setString(1, username);
            preparedStatement.setString(2, password);

            ResultSet resultSet = preparedStatement.executeQuery();

            if (resultSet.next()) {
                int roleId = resultSet.getInt("role_id");
                return new User(username, roleId);
            } else {
                return null; // returns null if no record is found
            }

        } catch (SQLException e) {
            System.out.println("Database error: " + e.getMessage());
            return null;
        }
    }
}
