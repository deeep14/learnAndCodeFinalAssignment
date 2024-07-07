package org.learnAndCode;

import org.learnAndCode.Database.DBConnection;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Arrays;
import java.util.List;

public class Chef extends User{

    public Chef(String username, int roleId) {
        super(username, roleId);
    }

    public static void displayMenu(PrintWriter writer, BufferedReader reader) throws IOException {
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
                    viewDiscardedItems(writer);
                    break;
                default:
                    writer.println("Invalid option. Please try again.");
                    continue;
            }
            continueSession = askToContinue(writer, reader);
        }
        writer.println("Thank you for using our cafeteria app!");
    }

    private static boolean askToContinue(PrintWriter writer, BufferedReader reader) throws IOException {
        writer.println("Do you want to perform another function? (yes/no)");
        String response = reader.readLine();
        return "yes".equalsIgnoreCase(response);
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
        writer.println("Enter the item IDs and names of menu items you want to roll out:");
        String input = reader.readLine();
        List<String> entries = Arrays.asList(input.split(";"));
        writer.println("Enter the date for rolling out the menu (YYYY-MM-DD):");
        String date = reader.readLine();
        if (storeRolledOutItems(entries,date)) {
            writer.println("Items successfully rolled out.");
        } else {
            writer.println("Failed to roll out items.");
        }
    }

    private static void displayVotedItems(PrintWriter writer) {
        try (Connection connection = DBConnection.getConnection();
             PreparedStatement stmt = connection.prepareStatement(
                     "SELECT TOP 5 item_id, item_name, number_of_votes FROM votes " +
                             "WHERE date = (SELECT MAX(date) FROM votes) " +
                             "ORDER BY number_of_votes DESC");  // Using TOP 5 instead of LIMIT
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

    private static void viewDiscardedItems(PrintWriter writer) {
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
            }
        } catch (SQLException e) {
            writer.println("Error retrieving discarded items: " + e.getMessage());
            e.printStackTrace();
        }
    }

}