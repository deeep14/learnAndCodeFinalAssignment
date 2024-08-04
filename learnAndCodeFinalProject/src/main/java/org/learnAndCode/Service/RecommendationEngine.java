package org.learnAndCode;

import org.learnAndCode.Database.DBConnection;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class RecommendationEngine {

    public static List<MenuItem> getRecommendations() {
        List<MenuItem> recommendations = new ArrayList<>();

        String query = "SELECT item_id, item_name, rating, review " +
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

                if (rating > 3 && SentimentAnalysis.isPositiveReview(review)) {
                    MenuItem menuItem = new MenuItem(itemId, itemName, rating, review);
                    recommendations.add(menuItem);
                }
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return recommendations;
    }
    public static boolean discardLowRatedItems() {
        String selectQuery = "SELECT item_id, item_name, rating, review FROM MenuItems WHERE rating < 2 OR review IS NOT NULL";
        String insertQuery = "INSERT INTO discarded_menu_items (item_id, item_name, rating, review) VALUES (?, ?, ?, ?)";
        String deleteQuery = "DELETE FROM MenuItems WHERE item_id = ?";

        try (Connection connection = DBConnection.getConnection();
             PreparedStatement selectStatement = connection.prepareStatement(selectQuery);
             PreparedStatement insertStatement = connection.prepareStatement(insertQuery);
             PreparedStatement deleteStatement = connection.prepareStatement(deleteQuery);
             ResultSet resultSet = selectStatement.executeQuery()) {

            connection.setAutoCommit(false);

            while (resultSet.next()) {
                int itemId = resultSet.getInt("item_id");
                String itemName = resultSet.getString("item_name");
                int rating = resultSet.getInt("rating");
                String review = resultSet.getString("review");

                if (rating < 2 || SentimentAnalysis.isNegativeReview(review)) {
                    insertStatement.setInt(1, itemId);
                    insertStatement.setString(2, itemName);
                    insertStatement.setInt(3, rating);
                    insertStatement.setString(4, review);
                    insertStatement.addBatch();

                    deleteStatement.setInt(1, itemId);
                    deleteStatement.addBatch();
                }
            }

            insertStatement.executeBatch();
            deleteStatement.executeBatch();
            connection.commit(); 
            return true;

        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }


}