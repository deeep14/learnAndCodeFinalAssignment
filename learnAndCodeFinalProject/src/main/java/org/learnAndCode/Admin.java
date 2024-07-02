package org.learnAndCode;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.List;

public class Admin extends User {

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
}