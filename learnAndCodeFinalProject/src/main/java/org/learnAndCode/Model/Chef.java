package org.learnAndCode.Model;

import org.learnAndCode.Service.DBConnection;
import org.learnAndCode.Service.MenuItemOperations;
import org.learnAndCode.Service.NotificationQueue;
import org.learnAndCode.Service.RecommendationEngine;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.List;

public class Chef extends User {
    private static final String YES_RESPONSE = "yes";
    public Chef(String username, int roleId) {
        super(username, roleId);
    }

    public static boolean displayMenu(PrintWriter writer, BufferedReader reader) throws IOException {
        boolean continueSession = true;
        while (continueSession) {
            writer.println("Chef Menu:");
            writer.println("1. Get recommendation");
            writer.println("2. Roll out menu");
            writer.println("3. Display voted items");
            writer.println("4. Display menu items");
            writer.println("5. Send Notification");
            writer.println("6. View Feedback");
            writer.println("7. Discard low rated Menu Items");
            writer.println("8. View Discarded Menu Items");
            writer.println("9. Logout");
            writer.println("Please select an option:");

            String option = reader.readLine();
            System.out.println("Option selected: " + option);

            if (option == null) {
                writer.println("Invalid input. Please try again.");
                continue;
            }

            switch (option) {
                case "1":
                    writer.println("Getting recommendation...");
                    getRecommendation(writer);
                    break;
                case "2":
                    writer.println("Rolling out menu...");
                    rollOutMenu(writer, reader);
                    break;
                case "3":
                    writer.println("Voted Items");
                    displayVotedItems(writer);
                    break;
                case "4":
                    writer.println("Displaying all menu items");
                    displayMenuItems(writer);
                    break;
                case "5":
                    sendNotification(writer, reader);
                    break;
                case "6":
                    writer.println("Viewing feedback");
                    viewFeedback(writer);
                    break;
                case "7":
                    writer.println("Discarding low-rated items...");
                    discardLowRatedItems(writer);
                    break;
                case "8":
                    writer.println("Viewing discarded items...");
                    viewDiscardedItems(writer, reader);
                    break;
                case "9":
                    writer.println("Thank you for using our cafeteria app!");
                    continueSession = false;
                    break;
                default:
                    writer.println("Invalid option. Please try again.");
                    continue;
            }

            if (continueSession) {
                continueSession = askToContinue(writer, reader);
            }
        }
        return continueSession;
    }

    private static boolean askToContinue(PrintWriter writer, BufferedReader reader) throws IOException {
        return true;
    }

    private static void displayMenuItems(PrintWriter writer) {
        List<MenuItem> menuItems = MenuItemOperations.getAllMenuItems();
        if (menuItems.isEmpty()) {
            writer.println("No menu items found.");
        } else {
            for (MenuItem item : menuItems) {
                writer.println("Item ID: " + item.getItemId() + ", Name: " + item.getItemName() +
                        ", Rating: " + item.getRating() + ", Review: " + item.getReview());
            }
        }
    }

    private static void getRecommendation(PrintWriter writer) {
        List<MenuItem> recommendations = RecommendationEngine.getRecommendations();
        writer.println("Top 5 Menu Items:");
        for (MenuItem item : recommendations) {
            writer.println("Name: " + item.getItemName() +
                    ", Rating: " + item.getRating() + ", Review: " + item.getReview());
        }
    }


    private static void rollOutMenu(PrintWriter writer, BufferedReader reader) throws IOException {
        List<MenuItem> recommendations = RecommendationEngine.getRecommendations();
        writer.println("Top 5 Menu Items:");
        for (MenuItem item : recommendations) {
            writer.println("Item ID: " + item.getItemId() + ", Name: " + item.getItemName() +
                    ", Rating: " + item.getRating() + ", Review: " + item.getReview());
        }

        writer.println("How many items do you want to roll out?");
        int itemCount;
        try {
            itemCount = Integer.parseInt(reader.readLine().trim());
        } catch (NumberFormatException e) {
            writer.println("Invalid number. Please enter a valid number of items.");
            return;
        }

        List<String> entries = new ArrayList<>();
        for (int i = 0; i < itemCount; i++) {
            writer.println("Enter item ID:");
            String itemId = reader.readLine().trim();
            writer.println("Enter item name:");
            String itemName = reader.readLine().trim();
            entries.add(itemId + "," + itemName);
        }

        Calendar calendar = Calendar.getInstance();
        calendar.add(Calendar.DAY_OF_YEAR, 1);
        java.sql.Date tomorrowDate = new java.sql.Date(calendar.getTimeInMillis());

        if (storeRolledOutItems(entries, tomorrowDate.toString())) {
            writer.println("Items successfully rolled out for " + tomorrowDate.toString() + ".");
        } else {
            writer.println("Failed to roll out items.");
        }
    }



    private static boolean storeRolledOutItems(List<String> entries, String date) {
        String query = "INSERT INTO rolled_out_items (item_id, item_name, date) VALUES (?, ?, ?)";
        try (Connection connection = DBConnection.getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(query)) {

            for (String entry : entries) {
                String[] parts = entry.split(",");
                int itemId = Integer.parseInt(parts[0].trim());
                String itemName = parts[1].trim();

                preparedStatement.setInt(1, itemId);
                preparedStatement.setString(2, itemName);
                preparedStatement.setDate(3, java.sql.Date.valueOf(date));
                preparedStatement.addBatch();
            }

            int[] result = preparedStatement.executeBatch();
            return Arrays.stream(result).sum() == entries.size();

        } catch (SQLException | NumberFormatException e) {
            e.printStackTrace();
            return false;
        }
    }


    private static void displayVotedItems(PrintWriter writer) {
        try (Connection connection = DBConnection.getConnection();
             PreparedStatement stmt = connection.prepareStatement(
                     "SELECT TOP 5 item_id, item_name, number_of_votes FROM votes " +
                             "WHERE date = (SELECT MAX(date) FROM votes) " +
                             "ORDER BY number_of_votes DESC"); 
             ResultSet resultSet = stmt.executeQuery()) {

            boolean found = false;
            while (resultSet.next()) {
                int itemId = resultSet.getInt("item_id");
                String itemName = resultSet.getString("item_name");
                int votes = resultSet.getInt("number_of_votes");
                writer.println("Item ID: " + itemId + ", Name: " + itemName + ", Votes: " + votes);
                found = true;
            }

            if (!found) {
                writer.println("No voted items found.");
            }
        } catch (SQLException e) {
            writer.println("Error retrieving voted items: " + e.getMessage());
            e.printStackTrace();
        }
    }

    private static void sendNotification(PrintWriter writer, BufferedReader reader) throws IOException {
        writer.println("Enter the notification message:");
        String message = reader.readLine();
        NotificationQueue.addNotification(message);
        writer.println("Notification sent successfully.");
    }

    private static void viewFeedback(PrintWriter writer) {
        String query = "SELECT item_id, item_name, rating, review FROM feedback " +
                "WHERE feedback_date = (SELECT MAX(feedback_date) FROM feedback) " +
                "ORDER BY item_id ASC";

        try (Connection connection = DBConnection.getConnection();
             PreparedStatement stmt = connection.prepareStatement(query);
             ResultSet resultSet = stmt.executeQuery()) {

            boolean found = false;
            while (resultSet.next()) {
                int itemId = resultSet.getInt("item_id");
                String itemName = resultSet.getString("item_name");
                int rating = resultSet.getInt("rating");
                String review = resultSet.getString("review");
                writer.println("Item ID: " + itemId + ", Item Name: " + itemName + ", Rating: " + rating + ", Review: " + review);
                found = true;
            }

            if (!found) {
                writer.println("No feedback found.");
            }
        } catch (SQLException e) {
            writer.println("Error retrieving feedback: " + e.getMessage());
            e.printStackTrace();
        }
    }

    private static void discardLowRatedItems(PrintWriter writer) {
        if (RecommendationEngine.discardLowRatedItems()) {
            writer.println("Low-rated items successfully discarded.");
        } else {
            writer.println("Failed to discard low-rated items.");
        }
    }

    private static void viewDiscardedItems(PrintWriter writer, BufferedReader reader) throws IOException {
        String query = "SELECT item_id, item_name, rating, review FROM discarded_menu_items ORDER BY item_id ASC";

        try (Connection connection = DBConnection.getConnection();
             PreparedStatement stmt = connection.prepareStatement(query);
             ResultSet resultSet = stmt.executeQuery()) {

            boolean found = false;
            while (resultSet.next()) {
                int itemId = resultSet.getInt("item_id");
                String itemName = resultSet.getString("item_name");
                int rating = resultSet.getInt("rating");
                String review = resultSet.getString("review");
                writer.println("Item ID: " + itemId + ", Item Name: " + itemName + ", Rating: " + rating + ", Review: " + review);
                found = true;
            }

            if (!found) {
                writer.println("No discarded items found.");
            } else {
                writer.println("1. Remove an item from the list");
                writer.println("2. Get more feedback on an item");
                writer.println("3. Go back");
                writer.println("Choose an option:");

                String option = reader.readLine();

                switch (option) {
                    case "1":
                        writer.println("Enter the Item ID to remove:");
                        int itemIdToRemove = Integer.parseInt(reader.readLine().trim());
                        removeDiscardedItem(writer, itemIdToRemove);
                        break;
                    case "2":
                        writer.println("Enter the Item ID to get more feedback:");
                        int itemIdForFeedback = Integer.parseInt(reader.readLine().trim());
                        getMoreFeedback(writer, itemIdForFeedback);
                        break;
                    case "3":
                        return;
                    default:
                        writer.println("Invalid option. Returning to menu.");
                }
            }
        } catch (SQLException e) {
            writer.println("Error retrieving discarded items: " + e.getMessage());
        }
    }

    private static void removeDiscardedItem(PrintWriter writer, int itemId) {
        String query = "DELETE FROM discarded_menu_items WHERE item_id = ?";

        try (Connection connection = DBConnection.getConnection();
             PreparedStatement stmt = connection.prepareStatement(query)) {

            stmt.setInt(1, itemId);
            int rowsAffected = stmt.executeUpdate();
            if (rowsAffected > 0) {
                writer.println("Item removed successfully.");
            } else {
                writer.println("Item not found.");
            }
        } catch (SQLException e) {
            writer.println("Error removing item: " + e.getMessage());
        }
    }

    private static void getMoreFeedback(PrintWriter writer, int itemId) {
        String query = "SELECT dislike_reason, improvement_suggestion, moms_recipe FROM detailed_feedback WHERE item_id = ?";

        try (Connection connection = DBConnection.getConnection();
             PreparedStatement stmt = connection.prepareStatement(query)) {

            stmt.setInt(1, itemId);
            ResultSet resultSet = stmt.executeQuery();
            if (resultSet.next()) {
                String detailedFeedback = resultSet.getString("dislike_reason");
                String improviseSuggestions = resultSet.getString("improvement_suggestion");
                String momsRecipe = resultSet.getString("moms_recipe");
                writer.println("Detailed Feedback: " + detailedFeedback);
                writer.println("Suggestions to Improvise: " + improviseSuggestions);
                writer.println("Mom's Recipe: " + momsRecipe);
            } else {
                writer.println("No detailed feedback found for the item.");
            }
        } catch (SQLException e) {
            writer.println("Error retrieving detailed feedback: " + e.getMessage());
        }
    }
}
