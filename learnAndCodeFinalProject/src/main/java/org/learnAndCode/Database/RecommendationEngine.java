package org.learnAndCode.Database;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class RecommendationEngine {

    public static List<MenuItem> getRecommendations() {
        List<MenuItem> recommendations = new ArrayList<>();

        String query = "SELECT TOP 5 item_id, item_name, rating, review " +
                "FROM MenuItems " +
                "WHERE rating IS NOT NULL " +
                "ORDER BY rating DESC";

        try (Connection connection = DBConnection.getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(query)) {

            ResultSet resultSet = preparedStatement.executeQuery();

            while (resultSet.next()) {
                int itemId = resultSet.getInt("item_id");
                String itemName = resultSet.getString("item_name");
                int rating = resultSet.getInt("rating");
                String review = resultSet.getString("review");

                MenuItem menuItem = new MenuItem(itemId, itemName, rating, review);
                recommendations.add(menuItem);
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return recommendations;
    }
}
