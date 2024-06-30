package org.learnAndCode.Client;

import java.io.*;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.Arrays;
import java.util.List;

public class Client {
    private static final String HOSTNAME = "localhost";
    private static final int PORT = 9202;
    private static final List<String> PROMPTS = Arrays.asList(
            "Please enter your username:",
            "Please enter your password:",
            "Enter item name:",
            "Is it available (Yes/No):",
            "Enter item ID to delete:",
            "Enter item ID to update:",
            "Enter new item name:",
            "Please select an option:",
            "What is the price of the item?",
            "Enter the item name:",
            "Enter your rating (1-5):",
            "Enter your review:",
            "Enter the item IDs and names of menu items you want to roll out (format: ID,Name;ID,Name;...):",
            "Enter the item ID for which you want to give feedback:",
            "Enter the item ID you want to vote for:"
    );

    public static void main(String[] args) {
        try (Socket socket = new Socket(HOSTNAME, PORT);
             InputStream input = socket.getInputStream();
             BufferedReader reader = new BufferedReader(new InputStreamReader(input));
             OutputStream output = socket.getOutputStream();
             PrintWriter writer = new PrintWriter(output, true);
             BufferedReader consoleReader = new BufferedReader(new InputStreamReader(System.in))) {

            String serverMessage;
            while ((serverMessage = reader.readLine()) != null) {
                System.out.println(serverMessage);
                if (isPrompt(serverMessage)) {
                    String userInput = consoleReader.readLine();
                    writer.println(userInput);
                }
            }
        } catch (UnknownHostException e) {
            System.err.println("Server not found: " + e.getMessage());
        } catch (IOException e) {
            System.err.println("I/O error: " + e.getMessage());
        }
    }

    private static boolean isPrompt(String message) {
        return PROMPTS.stream().anyMatch(message::contains);
    }
}
