package org.learnAndCode;

import org.learnAndCode.Database.DBConnection;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class MenuItemOperations {

    public static List<MenuItem> getAllMenuItems() {
        List<MenuItem> menuItems = new ArrayList<>();
        String query = "SELECT * FROM MenuItems";
        try (Connection connection = DBConnection.getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(query);
             ResultSet resultSet = preparedStatement.executeQuery()) {

            while (resultSet.next()) {
                int itemId = resultSet.getInt("item_id");
                String itemName = resultSet.getString("item_name");
                int rating = resultSet.getInt("rating"); // Assuming there's a 'rating' column
                String review = resultSet.getString("review"); // Assuming there's a 'review' column

                MenuItem menuItem = new MenuItem(itemId, itemName, rating, review);
                menuItems.add(menuItem);
            }
        } catch (SQLException e) {
            System.out.println("Database error: " + e.getMessage());
        }
        return menuItems;
    }
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