package org.learnAndCode.Server;

import org.learnAndCode.Database.Login;
import org.learnAndCode.Database.MenuItemOperations;
import org.learnAndCode.Database.User;

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;

public class Server {
    public static void main(String[] args) {
        try (ServerSocket serverSocket = new ServerSocket(9202)) {
            System.out.println("Server is listening on port 9202");

            while (true) {
                try (Socket socket = serverSocket.accept()) {
                    System.out.println("New client connected");

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
                }
            }
        } catch (IOException e) {
            System.out.println("Server exception: " + e.getMessage());
            e.printStackTrace();
        }
    }

    private static void displayAdminMenu(PrintWriter writer, BufferedReader reader) throws IOException {
        while (true) {
            writer.println("Admin Menu:");
            writer.println("1. Add menu item");
            writer.println("2. Delete menu item");
            writer.println("3. Update menu item");
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
                default:
                    writer.println("Invalid option. Please try again.");
                    continue;
            }
            break; // Exit the loop if a valid option is chosen
        }
    }
    private static void displayChefMenu(PrintWriter writer, BufferedReader reader) throws IOException {
        while (true) {
            writer.println("Chef Menu:");
            writer.println("1. Get recommendation");
            writer.println("2. Roll out menu");
            writer.println("3. Generate report");
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
                    // Implement the logic to get recommendation
                    break;
                case "2":
                    writer.println("Rolling out menu...");
                    // Implement the logic to roll out menu
                    break;
                case "3":
                    writer.println("Generating report...");
                    // Implement the logic to generate report
                    break;
                default:
                    writer.println("Invalid option. Please try again.");
                    continue;
            }
            break; // Exit the loop if a valid option is chosen
        }
    }
    private static void displayEmployeeMenu(PrintWriter writer, BufferedReader reader) throws IOException {
        while (true) {
            writer.println("Employee Menu:");
            writer.println("1. Check Notifications");
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
                    writer.println("Checking Notifications...");
                    // Implement the logic to check menu
                    break;
                case "2":
                    writer.println("Voting...");
                    // Implement the logic to vote
                    break;
                case "3":
                    writer.println("Giving feedback...");
                    // Implement the logic to give feedback
                    break;
                default:
                    writer.println("Invalid option. Please try again.");
                    continue;
            }
            break; // Exit the loop if a valid option is chosen
        }
    }
}
