package org.learnAndCode.Client;

import java.io.*;
import java.net.Socket;
import java.net.UnknownHostException;

public class Client {
    public static void main(String[] args) {
        String hostname = "localhost";
        int port = 9202;

        try (Socket socket = new Socket(hostname, port)) {
            InputStream input = socket.getInputStream();
            BufferedReader reader = new BufferedReader(new InputStreamReader(input));

            OutputStream output = socket.getOutputStream();
            PrintWriter writer = new PrintWriter(output, true);

            BufferedReader consoleReader = new BufferedReader(new InputStreamReader(System.in));

            String serverMessage;
            while ((serverMessage = reader.readLine()) != null) {
                System.out.println(serverMessage);
                if (serverMessage.contains("Please enter your username:") || serverMessage.contains("Please enter your password:") || serverMessage.contains("Enter item name:") || serverMessage.contains("Is it available (Yes/No):") || serverMessage.contains("Enter item ID to delete:") || serverMessage.contains("Enter item ID to update:") || serverMessage.contains("Enter new item name:") || serverMessage.contains("Please select an option:") || serverMessage.contains("What is the price of the item?")) {
                    String userInput = consoleReader.readLine();
                    writer.println(userInput);
                }
            }
        } catch (UnknownHostException e) {
            System.out.println("Server not found: " + e.getMessage());
        } catch (IOException e) {
            System.out.println("I/O error: " + e.getMessage());
        }
    }
}
