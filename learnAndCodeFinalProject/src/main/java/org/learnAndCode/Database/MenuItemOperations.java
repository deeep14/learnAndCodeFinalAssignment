package org.learnAndCode.Database;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class MenuItemOperations {

    // Add a new menu item
    public static boolean addMenuItem(String itemName, String available, Integer price) {
        String query = "INSERT INTO MenuItems (item_name, available, price) VALUES (?, ?, ?)";
        try (Connection connection = DBConnection.getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(query)) {

            preparedStatement.setString(1, itemName);
            preparedStatement.setString(2, available);
            preparedStatement.setInt(3, price);

            int rowsAffected = preparedStatement.executeUpdate();
            return rowsAffected > 0;
        } catch (SQLException e) {
            System.out.println("Database error: " + e.getMessage());
            return false;
        }
    }

    // Delete a menu item by ID
    public static boolean deleteMenuItem(int itemId) {
        String query = "DELETE FROM MenuItems WHERE item_id = ?";
        try (Connection connection = DBConnection.getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(query)) {

            preparedStatement.setInt(1, itemId);

            int rowsAffected = preparedStatement.executeUpdate();
            return rowsAffected > 0;
        } catch (SQLException e) {
            System.out.println("Database error: " + e.getMessage());
            return false;
        }
    }

    // Update a menu item by ID
    public static boolean updateMenuItem(int itemId, String itemName, String available) {
        String query = "UPDATE MenuItems SET item_name = ?, available = ? WHERE item_id = ?";
        try (Connection connection = DBConnection.getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(query)) {

            preparedStatement.setString(1, itemName);
            preparedStatement.setString(2, available);
            preparedStatement.setInt(3, itemId);

            int rowsAffected = preparedStatement.executeUpdate();
            return rowsAffected > 0;
        } catch (SQLException e) {
            System.out.println("Database error: " + e.getMessage());
            return false;
        }
    }
}