package org.learnAndCode;

import org.learnAndCode.Database.DBConnection;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class Employee {
    public static void displayMenu(PrintWriter writer, BufferedReader reader) throws IOException {
        boolean continueSession = true;
        while (continueSession) {
            writer.println("Employee Menu:");
            writer.println("1. View rolled out items");
            writer.println("2. Vote");
            writer.println("3. Give feedback");
            writer.println("4. Check Notifications");
            writer.println("Please select an option:");

            String option = reader.readLine();
            System.out.println("Option selected: " + option);

            if (option == null) {
                writer.println("Invalid input. Please try again.");
                continue;
            }

            switch (option) {
                case "1":
                    writer.println("Displaying rolled out items...");
                    displayRolledOutItems(writer);
                    break;
                case "2":
                    writer.println("Voting...");
                    voteForMenuItem(writer, reader);
                    break;
                case "3":
                    writer.println("Giving feedback...");
                    giveFeedback(writer, reader);
                    break;
                case "4":
                    writer.println("Checking notifications...");
                    checkNotification(writer);
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


    private static void voteForMenuItem(PrintWriter writer, BufferedReader reader) throws IOException {
        writer.println("Rolled out items:");
        displayRolledOutItems(writer);

        writer.println("Enter the item ID you want to vote for:");
        int itemId = Integer.parseInt(reader.readLine());
        writer.println("Enter the item name:");
        String itemName = reader.readLine();
        writer.println("Enter the date for your vote (YYYY-MM-DD):");
        String voteDate = reader.readLine();

        if (updateVotes(itemId, itemName, voteDate)) {
            writer.println("Vote successfully submitted!");
        } else {
            writer.println("Failed to submit vote.");
        }
    }

    private static boolean updateVotes(int itemId, String itemName, String voteDate) {
        // SQL statement to handle the voting logic
        String query = "BEGIN IF EXISTS (SELECT 1 FROM votes WHERE item_id = ? AND date = ?) " +
                "UPDATE votes SET number_of_votes = number_of_votes + 1 WHERE item_id = ? AND date = ? " +
                "ELSE " +
                "INSERT INTO votes (item_id, item_name, number_of_votes, date) VALUES (?, ?, 1, ?) " +
                "END";

        try (Connection connection = DBConnection.getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(query)) {

            preparedStatement.setInt(1, itemId);
            preparedStatement.setDate(2, java.sql.Date.valueOf(voteDate));

            preparedStatement.setInt(3, itemId);
            preparedStatement.setDate(4, java.sql.Date.valueOf(voteDate));

            preparedStatement.setInt(5, itemId);
            preparedStatement.setString(6, itemName);
            preparedStatement.setDate(7, java.sql.Date.valueOf(voteDate));

            int result = preparedStatement.executeUpdate();
            return result > 0;

        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }


    private static void displayRolledOutItems(PrintWriter writer) {
        String query = "SELECT item_id, item_name, date FROM rolled_out_items " +
                "WHERE date = (SELECT MAX(date) FROM rolled_out_items)";
        try (Connection connection = DBConnection.getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(query);
             ResultSet resultSet = preparedStatement.executeQuery()) {

            boolean itemsFound = false;
            while (resultSet.next()) {
                int itemId = resultSet.getInt("item_id");
                String itemName = resultSet.getString("item_name");
                writer.println("Item ID: " + itemId + ", Name: " + itemName);
                itemsFound = true;
            }

            if (!itemsFound) {
                writer.println("No items have been rolled out yet.");
            }

        } catch (SQLException e) {
            writer.println("Error retrieving rolled out items: " + e.getMessage());
            e.printStackTrace();
        }
    }


    private static void giveFeedback(PrintWriter writer, BufferedReader reader) throws IOException {
        writer.println("Enter the item ID for which you want to give feedback:");
        int itemId = Integer.parseInt(reader.readLine());
        writer.println("Enter the item name:");
        String itemName = reader.readLine();
        writer.println("Enter your rating (1-5):");
        int rating = Integer.parseInt(reader.readLine());
        writer.println("Enter your review:");
        String review = reader.readLine();

        Feedback feedback = new Feedback(0, itemName, rating, review, itemId);  // feedbackId is auto-generated
        if (storeFeedback(feedback)) {
            writer.println("Feedback successfully submitted!");
        } else {
            writer.println("Failed to submit feedback.");
        }
    }

    private static boolean storeFeedback(Feedback feedback) {
        String query = "INSERT INTO feedback (item_name, rating, review, item_id, feedback_date) VALUES (?, ?, ?, ?, ?)";
        try (Connection connection = DBConnection.getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(query)) {

            preparedStatement.setString(1, feedback.getItemName());
            preparedStatement.setInt(2, feedback.getRating());
            preparedStatement.setString(3, feedback.getReview());
            preparedStatement.setInt(4, feedback.getItemId());
            preparedStatement.setDate(5, new java.sql.Date(System.currentTimeMillis()));
            int result = preparedStatement.executeUpdate();
            return result > 0;

        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }


    private static void checkNotification(PrintWriter writer) {
        if (NotificationQueue.hasNotifications()) {
            writer.println("Notifications:");
            while (NotificationQueue.hasNotifications()) {
                writer.println("- " + NotificationQueue.getNextNotification());
            }
        } else {
            writer.println("No new notifications.");
        }
    }
}