package org.learnAndCode.Server;

import org.learnAndCode.Database.Login;

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

                    writer.println("Please enter your password:");
                    String password = reader.readLine();

                    if (Login.validateLogin(username, password)) {
                        writer.println("Login successful!");
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
}
