package org.learnAndCode.Model;

import org.learnAndCode.Service.DBConnection;
import org.learnAndCode.Service.MenuItemOperations;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

public class Admin extends User {
    private static final String YES_RESPONSE = "yes";
    public Admin(String username, int roleId) {
        super(username, roleId);
    }

    public static void displayMenu(PrintWriter writer, BufferedReader reader) throws IOException {
        boolean continueSession = true;
        while (continueSession) {
            writer.println("Admin Menu:");
            writer.println("1. Add menu item");
            writer.println("2. Delete menu item");
            writer.println("3. Update menu item");
            writer.println("4. Display menu items");
            writer.println("5. View discarded items");
            writer.println("Please select an option:");

            String option = reader.readLine();
            System.out.println("Option selected: " + option);

            if (option == null) {
                writer.println("Invalid input. Please try again.");
                continue;
            }

            switch (option) {
                case "1":
                    addMenuItem(writer, reader);
                    break;
                case "2":
                    deleteMenuItem(writer, reader);
                    break;
                case "3":
                    updateMenuItem(writer, reader);
                    break;
                case "4":
                    displayMenuItems(writer);
                    break;
                case "5":
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
        return YES_RESPONSE.equalsIgnoreCase(response);
    }

    private static void addMenuItem(PrintWriter writer, BufferedReader reader) throws IOException {
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
    }

    private static void deleteMenuItem(PrintWriter writer, BufferedReader reader) throws IOException {
        writer.println("Enter item ID to delete:");
        int itemIdToDelete = Integer.parseInt(reader.readLine());
        if (MenuItemOperations.deleteMenuItem(itemIdToDelete)) {
            writer.println("Menu item deleted successfully.");
        } else {
            writer.println("Failed to delete menu item.");
        }
    }

    private static void updateMenuItem(PrintWriter writer, BufferedReader reader) throws IOException {
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
    }

    private static void displayMenuItems(PrintWriter writer) {
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