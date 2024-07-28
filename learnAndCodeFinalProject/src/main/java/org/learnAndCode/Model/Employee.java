package org.learnAndCode.Model;

import org.learnAndCode.Service.DBConnection;
import org.learnAndCode.Service.MenuItemOperations;
import org.learnAndCode.Service.NotificationQueue;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.stream.Collectors;

public class Employee extends User {
    public Employee(String username, int roleId) {
        super(username, roleId);
    }

    public static boolean displayMenu(PrintWriter writer, BufferedReader reader) throws IOException {
        boolean continueSession = true;
        while (continueSession) {
            writer.println("Employee Menu:");
            writer.println("0. Display all menu items");
            writer.println("1. View rolled out items");
            writer.println("2. Vote");
            writer.println("3. Give feedback");
            writer.println("4. Check Notifications");
            writer.println("5. Give detailed feedback on discarded food items");
            writer.println("6. Update Profile");
            writer.println("7. Display Current Profile");
            writer.println("8. Logout");
            writer.println("Please select an option:");

            String option = reader.readLine();
            System.out.println("Option selected: " + option);

            if (option == null) {
                writer.println("Invalid input. Please try again.");
                continue;
            }

            switch (option) {
                case "0":
                    writer.println("Displaying all menu items");
                    displayMenuItems(writer);
                    break;
                case "1":
                    writer.println("Displaying rolled out items...");
                    displayRolledOutItems(writer, getUsername());
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
                case "5":
                    writer.println("Giving detailed feedback...");
                    giveDetailedFeedback(writer, reader);
                    break;
                case "6":
                    writer.println("Updating profile...");
                    updateProfile(writer, reader);
                    break;
                case "7":
                    writer.println("Displaying current profile...");
                    displayCurrentUserProfile(writer, getUsername());
                    break;
                case "8":
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

    private static void voteForMenuItem(PrintWriter writer, BufferedReader reader) throws IOException {
        writer.println("Rolled out items:");
        displayRolledOutItems(writer, null);

        writer.println("Enter the item ID you want to vote for:");
        int itemId = Integer.parseInt(reader.readLine());
        writer.println("Enter the item name:");
        String itemName = reader.readLine();

        Calendar calendar = Calendar.getInstance();
        calendar.add(Calendar.DAY_OF_YEAR, 1);
        java.sql.Date tomorrowDate = new java.sql.Date(calendar.getTimeInMillis());

        if (updateVotes(itemId, itemName, tomorrowDate.toString())) {
            writer.println("Vote successfully submitted!");
        } else {
            writer.println("Failed to submit vote.");
        }
    }

    private static boolean updateVotes(int itemId, String itemName, String voteDate) {
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

    private static void displayRolledOutItems(PrintWriter writer, String username) {
        List<RolledOutItem> rolledOutItems = new ArrayList<>();
        String foodType = getUserFoodType(username);

        String query = "SELECT item_id, item_name, date FROM rolled_out_items " +
                "WHERE date = (SELECT MAX(date) FROM rolled_out_items)";
        try (Connection connection = DBConnection.getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(query);
             ResultSet resultSet = preparedStatement.executeQuery()) {

            while (resultSet.next()) {
                int itemId = resultSet.getInt("item_id");
                String itemName = resultSet.getString("item_name");
                rolledOutItems.add(new RolledOutItem(itemId, itemName));
            }

            if (foodType != null) {
                if (foodType.equalsIgnoreCase("non veg")) {
                    rolledOutItems.sort((a, b) -> {
                        if (isNonVeg(a.getItemName()) && !isNonVeg(b.getItemName())) {
                            return -1;
                        } else if (!isNonVeg(a.getItemName()) && isNonVeg(b.getItemName())) {
                            return 1;
                        } else {
                            return 0;
                        }
                    });
                } else {
                    rolledOutItems.sort((a, b) -> {
                        if (isNonVeg(a.getItemName()) && !isNonVeg(b.getItemName())) {
                            return 1;
                        } else if (!isNonVeg(a.getItemName()) && isNonVeg(b.getItemName())) {
                            return -1;
                        } else {
                            return 0;
                        }
                    });
                }
            }

            if (rolledOutItems.isEmpty()) {
                writer.println("No items have been rolled out yet.");
            } else {
                for (RolledOutItem item : rolledOutItems) {
                    writer.println("Item ID: " + item.getItemId() + ", Name: " + item.getItemName());
                }
            }

        } catch (SQLException e) {
            writer.println("Error retrieving rolled out items: " + e.getMessage());
            e.printStackTrace();
        }
    }

    private static String getUserFoodType(String username) {
        String query = "SELECT food_type FROM profiles WHERE username = ?";
        try (Connection connection = DBConnection.getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(query)) {

            preparedStatement.setString(1, username);
            try (ResultSet resultSet = preparedStatement.executeQuery()) {
                if (resultSet.next()) {
                    return resultSet.getString("food_type");
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    private static boolean isNonVeg(String itemName) {
        return itemName.toLowerCase().contains("chicken") ||
                itemName.toLowerCase().contains("mutton") ||
                itemName.toLowerCase().contains("fish") ||
                itemName.toLowerCase().contains("egg") ||
                itemName.toLowerCase().contains("prawn") ||
                itemName.toLowerCase().contains("beef") ||
                itemName.toLowerCase().contains("pork");
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

    private static void giveDetailedFeedback(PrintWriter writer, BufferedReader reader) throws IOException {
        writer.println("Displaying discarded menu items...");
        displayDiscardedItems(writer);

        writer.println("Enter the item ID for which you want to give detailed feedback:");
        int itemId = Integer.parseInt(reader.readLine());

        writer.println("Enter the reason for dislike:");
        String dislikeReason = reader.readLine();

        writer.println("Enter your improvement suggestion:");
        String improvementSuggestion = reader.readLine();

        writer.println("Enter mom's recipe:");
        String momsRecipe = reader.readLine();

        DetailedFeedback detailedFeedback = new DetailedFeedback(itemId, dislikeReason, improvementSuggestion, momsRecipe);
        if (storeDetailedFeedback(detailedFeedback)) {
            writer.println("Detailed feedback successfully submitted!");
        } else {
            writer.println("Failed to submit detailed feedback.");
        }
    }

    private static void displayDiscardedItems(PrintWriter writer) {
        String query = "SELECT item_id, item_name, rating, review, discarded_date FROM discarded_menu_items";
        try (Connection connection = DBConnection.getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(query);
             ResultSet resultSet = preparedStatement.executeQuery()) {

            boolean itemsFound = false;
            while (resultSet.next()) {
                int itemId = resultSet.getInt("item_id");
                String itemName = resultSet.getString("item_name");
                int rating = resultSet.getInt("rating");
                String review = resultSet.getString("review");
                java.sql.Date discardedDate = resultSet.getDate("discarded_date");
                writer.println("Item ID: " + itemId + ", Name: " + itemName + ", Rating: " + rating + ", Review: " + review + ", Discarded Date: " + discardedDate);
                itemsFound = true;
            }

            if (!itemsFound) {
                writer.println("No items have been discarded yet.");
            }

        } catch (SQLException e) {
            writer.println("Error retrieving discarded items: " + e.getMessage());
            e.printStackTrace();
        }
    }

    private static boolean storeDetailedFeedback(DetailedFeedback feedback) {
        String query = "INSERT INTO detailed_feedback (item_id, dislike_reason, improvement_suggestion, moms_recipe, feedback_date) VALUES (?, ?, ?, ?, ?)";
        try (Connection connection = DBConnection.getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(query)) {

            preparedStatement.setInt(1, feedback.getItemId());
            preparedStatement.setString(2, feedback.getDislikeReason());
            preparedStatement.setString(3, feedback.getImprovementSuggestion());
            preparedStatement.setString(4, feedback.getMomsRecipe());
            preparedStatement.setDate(5, new java.sql.Date(System.currentTimeMillis()));
            int result = preparedStatement.executeUpdate();
            return result > 0;

        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    private static void updateProfile(PrintWriter writer, BufferedReader reader) throws IOException {

        writer.println("Enter your username:");
        String username = reader.readLine();
        writer.println("Enter your food type (veg, non veg, egg):");
        String foodType = reader.readLine();
        writer.println("Enter your spice level (high, medium, low):");
        String spiceLevel = reader.readLine();
        writer.println("Enter your region preference (north indian, south indian):");
        String regionPreference = reader.readLine();
        writer.println("Do you have a sweet tooth? (yes/no):");
        boolean sweetTooth = "yes".equalsIgnoreCase(reader.readLine());

        if (storeProfile(username, foodType, spiceLevel, regionPreference, sweetTooth)) {
            writer.println("Profile successfully updated!");
        } else {
            writer.println("Failed to update profile.");
        }
    }

    private static boolean storeProfile(String username, String foodType, String spiceLevel, String regionPreference, boolean sweetTooth) {
        String query = "MERGE INTO profiles AS target " +
                "USING (VALUES (?, ?, ?, ?, ?)) AS source (username, food_type, spice_level, region_preference, sweet_tooth) " +
                "ON target.username = source.username " +
                "WHEN MATCHED THEN " +
                "UPDATE SET food_type = source.food_type, spice_level = source.spice_level, region_preference = source.region_preference, sweet_tooth = source.sweet_tooth " +
                "WHEN NOT MATCHED THEN " +
                "INSERT (username, food_type, spice_level, region_preference, sweet_tooth) " +
                "VALUES (source.username, source.food_type, source.spice_level, source.region_preference, source.sweet_tooth);";

        try (Connection connection = DBConnection.getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(query)) {

            preparedStatement.setString(1, username);
            preparedStatement.setString(2, foodType);
            preparedStatement.setString(3, spiceLevel);
            preparedStatement.setString(4, regionPreference);
            preparedStatement.setBoolean(5, sweetTooth);

            int result = preparedStatement.executeUpdate();
            return result > 0;

        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    private static void displayCurrentUserProfile(PrintWriter writer, String username) {
        String query = "SELECT profile_id, username, food_type, spice_level, region_preference, sweet_tooth FROM profiles WHERE username = ?";

        try (Connection connection = DBConnection.getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(query)) {

            preparedStatement.setString(1, username);
            try (ResultSet resultSet = preparedStatement.executeQuery()) {
                if (resultSet.next()) {
                    int profileId = resultSet.getInt("profile_id");
                    String foodType = resultSet.getString("food_type");
                    String spiceLevel = resultSet.getString("spice_level");
                    String regionPreference = resultSet.getString("region_preference");
                    boolean sweetTooth = resultSet.getBoolean("sweet_tooth");

                    writer.println("Profile ID: " + profileId);
                    writer.println("Username: " + username);
                    writer.println("Food Type: " + foodType);
                    writer.println("Spice Level: " + spiceLevel);
                    writer.println("Region Preference: " + regionPreference);
                    writer.println("Sweet Tooth: " + (sweetTooth ? "Yes" : "No"));
                } else {
                    writer.println("Profile not found for username: " + username);
                }
            }
        } catch (SQLException e) {
            writer.println("Error retrieving profile: " + e.getMessage());
            e.printStackTrace();
        }
    }
}
