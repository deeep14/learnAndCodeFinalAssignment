package org.learnAndCode.Server;

import org.learnAndCode.*;
import org.learnAndCode.Database.*;

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Arrays;
import java.util.List;

public class Server {
    public static void main(String[] args) {
        try (ServerSocket serverSocket = new ServerSocket(9202)) {
            System.out.println("Server is listening on port 9202");

            while (true) {
                Socket socket = serverSocket.accept();
                System.out.println("New client connected");
                new ClientHandler(socket).start();
            }
        } catch (IOException e) {
            System.out.println("Server exception: " + e.getMessage());
            e.printStackTrace();
        }
    }
}

class ClientHandler extends Thread {
    private Socket socket;

    public ClientHandler(Socket socket) {
        this.socket = socket;
    }

    @Override
    public void run() {
        try {
            InputStream input = socket.getInputStream();
            BufferedReader reader = new BufferedReader(new InputStreamReader(input));

            OutputStream output = socket.getOutputStream();
            PrintWriter writer = new PrintWriter(output, true);

            writer.println("Please enter your username:");
            String username = reader.readLine();
            System.out.println("Username entered: " + username);

            writer.println("Please enter your password:");
            String password = reader.readLine();
            System.out.println("Password entered.");

            User user = Login.validateLogin(username, password);

            if (user != null) {
                int roleId = user.getRoleId();
                System.out.println("User role ID: " + roleId);
                if (roleId == 1) {
                    writer.println("Welcome Admin!");
                    displayAdminMenu(writer, reader);
                } else if (roleId == 2) {
                    writer.println("Welcome Chef!");
                    displayChefMenu(writer, reader);
                } else if (roleId == 3) {
                    writer.println("Welcome Employee!");
                    displayEmployeeMenu(writer, reader);
                } else {
                    writer.println("Welcome User!");
                }
            } else {
                writer.println("Invalid login. Try again.");
            }
        } catch (IOException e) {
            System.out.println("Server exception: " + e.getMessage());
            e.printStackTrace();
        } finally {
            try {
                socket.close();
            } catch (IOException e) {
                System.out.println("Could not close socket: " + e.getMessage());
            }
        }
    }

    private static void displayAdminMenu(PrintWriter writer, BufferedReader reader) throws IOException {
        while (true) {
            writer.println("Admin Menu:");
            writer.println("1. Add menu item");
            writer.println("2. Delete menu item");
            writer.println("3. Update menu item");
            writer.println("4. Display menu items");
            writer.println("Please select an option:");

            String option = reader.readLine();
            System.out.println("Option selected: " + option);

            if (option == null) {
                writer.println("Invalid input. Please try again.");
                continue;
            }

            switch (option) {
                case "1":
                    writer.println("Enter item name:");
                    String itemName = reader.readLine();
                    writer.println("Is it available (Yes/No):");
                    String available = reader.readLine();
                    writer.println("What is the price of the item?");
                    Integer price = Integer.valueOf(reader.readLine());
                    if (MenuItemOperations.addMenuItem(itemName, available, price)) {
                        writer.println("Menu item added successfully.");
                    } else {
                        writer.println("Failed to add menu item.");
                    }
                    break;
                case "2":
                    writer.println("Enter item ID to delete:");
                    int itemIdToDelete = Integer.parseInt(reader.readLine());
                    if (MenuItemOperations.deleteMenuItem(itemIdToDelete)) {
                        writer.println("Menu item deleted successfully.");
                    } else {
                        writer.println("Failed to delete menu item.");
                    }
                    break;
                case "3":
                    writer.println("Enter item ID to update:");
                    int itemIdToUpdate = Integer.parseInt(reader.readLine());
                    writer.println("Enter new item name:");
                    String newItemName = reader.readLine();
                    writer.println("Is it available (Yes/No):");
                    String newAvailable = reader.readLine();
                    if (MenuItemOperations.updateMenuItem(itemIdToUpdate, newItemName, newAvailable)) {
                        writer.println("Menu item updated successfully.");
                    } else {
                        writer.println("Failed to update menu item.");
                    }
                    break;
                case "4":
                    writer.println("Displaying all menu items:");
                    List<MenuItem> menuItems = MenuItemOperations.getAllMenuItems();
                    if (menuItems.isEmpty()) {
                        writer.println("No menu items found.");
                    } else {
                        for (MenuItem item : menuItems) {
                            writer.println("Item ID: " + item.getItemId() + ", Name: " + item.getItemName() +
                                    ", Rating: " + item.getRating() + ", Review: " + item.getReview());
                        }
                    }
                    break;
                default:
                    writer.println("Invalid option. Please try again.");
                    continue;
            }
            break;
        }
    }

    private static void displayChefMenu(PrintWriter writer, BufferedReader reader) throws IOException {
        while (true) {
            writer.println("Chef Menu:");
            writer.println("1. Get recommendation");
            writer.println("2. Roll out menu");
            writer.println("3. Generate report");
            writer.println("4. Display menu items");
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
                    rollOutMenu(writer,reader);
                    break;
                case "3":
                    writer.println("Generating report...");
                    break;
                case "4":
                    writer.println("Displaying all menu items:");
                    List<MenuItem> menuItems = MenuItemOperations.getAllMenuItems();
                    if (menuItems.isEmpty()) {
                        writer.println("No menu items found.");
                    } else {
                        for (MenuItem item : menuItems) {
                            writer.println("Item ID: " + item.getItemId() + ", Name: " + item.getItemName() +
                                    ", Rating: " + item.getRating() + ", Review: " + item.getReview());
                        }
                    }
                    break;
                default:
                    writer.println("Invalid option. Please try again.");
                    continue;
            }
            break;
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
        writer.println("Enter the item IDs and names of menu items you want to roll out (format: ID,Name;ID,Name;...):");
        String input = reader.readLine();
        List<String> entries = Arrays.asList(input.split(";"));

        if (storeRolledOutItems(entries)) {
            writer.println("Items successfully rolled out.");
        } else {
            writer.println("Failed to roll out items.");
        }
    }

    private static boolean storeRolledOutItems(List<String> entries) {
        String query = "INSERT INTO rolled_out_items (item_id, item_name) VALUES (?, ?)";
        try (Connection connection = DBConnection.getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(query)) {

            for (String entry : entries) {
                String[] parts = entry.split(",");
                int itemId = Integer.parseInt(parts[0].trim());
                String itemName = parts[1].trim();

                preparedStatement.setInt(1, itemId);
                preparedStatement.setString(2, itemName);
                preparedStatement.addBatch();
            }

            int[] result = preparedStatement.executeBatch();
            return Arrays.stream(result).sum() == entries.size();

        } catch (SQLException | NumberFormatException e) {
            e.printStackTrace();
            return false;
        }
    }

    private static void displayEmployeeMenu(PrintWriter writer, BufferedReader reader) throws IOException {
        while (true) {
            writer.println("Employee Menu:");
            writer.println("1. View rolled out items: ");
            writer.println("2. Vote");
            writer.println("3. Give feedback");
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
                default:
                    writer.println("Invalid option. Please try again.");
                    continue;
            }
            break;
        }
    }

    private static void voteForMenuItem(PrintWriter writer, BufferedReader reader) throws IOException {
        writer.println("Rolled out items:");
        displayRolledOutItems(writer);

        writer.println("Enter the item ID you want to vote for:");
        int itemId = Integer.parseInt(reader.readLine());
        writer.println("Enter the item name:");
        String itemName = reader.readLine();

        if (updateVotes(itemId, itemName)) {
            writer.println("Vote successfully submitted!");
        } else {
            writer.println("Failed to submit vote.");
        }
    }

    private static boolean updateVotes(int itemId, String itemName) {
        String query = "IF EXISTS (SELECT 1 FROM votes WHERE item_id = ?) " +
                "BEGIN " +
                "    UPDATE votes SET number_of_votes = number_of_votes + 1 WHERE item_id = ? " +
                "END " +
                "ELSE " +
                "BEGIN " +
                "    INSERT INTO votes (item_id, item_name, number_of_votes) VALUES (?, ?, 1) " +
                "END";
        try (Connection connection = DBConnection.getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(query)) {

            preparedStatement.setInt(1, itemId);
            preparedStatement.setInt(2, itemId);
            preparedStatement.setInt(3, itemId);
            preparedStatement.setString(4, itemName);
            int result = preparedStatement.executeUpdate();
            return result > 0;

        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }




    private static void displayRolledOutItems(PrintWriter writer) {
        String query = "SELECT item_id, item_name FROM rolled_out_items"; // Adjust this query based on your actual table and column names
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
        String query = "INSERT INTO feedback (item_name, rating, review, item_id) VALUES (?, ?, ?, ?)";
        try (Connection connection = DBConnection.getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(query)) {

            preparedStatement.setString(1, feedback.getItemName());
            preparedStatement.setInt(2, feedback.getRating());
            preparedStatement.setString(3, feedback.getReview());
            preparedStatement.setInt(4, feedback.getItemId());
            int result = preparedStatement.executeUpdate();
            return result > 0;

        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }
}
