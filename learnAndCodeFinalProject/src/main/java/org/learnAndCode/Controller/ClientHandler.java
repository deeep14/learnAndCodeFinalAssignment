package org.learnAndCode.Controller;

import org.learnAndCode.Model.*;

import java.io.*;
import java.net.Socket;

public class ClientHandler extends Thread {
    private Socket socket;
    private String currentUsername;

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

            boolean loginSuccess = false;
            User user = null;

            while (true) {
                while (!loginSuccess) {
                    writer.println("Please enter your username:");
                    String username = reader.readLine();
                    System.out.println("Username entered: " + username);

                    writer.println("Please enter your password:");
                    String password = reader.readLine();
                    System.out.println("Password entered.");

                    user = Login.validateLogin(username, password);

                    if (user != null) {
                        loginSuccess = true;
                        currentUsername = username;
                        user.setUsername(currentUsername);
                    } else {
                        writer.println("Invalid login. Try again.");
                    }
                }

                int roleId = user.getRoleId();
                System.out.println("User role ID: " + roleId);

                boolean continueSession = false;
                switch (roleId) {
                    case 1:
                        writer.println("Welcome Admin!");
                        continueSession = Admin.displayMenu(writer, reader);
                        break;
                    case 2:
                        writer.println("Welcome Chef!");
                        continueSession = Chef.displayMenu(writer, reader);
                        break;
                    case 3:
                        writer.println("Welcome Employee!");
                        continueSession = Employee.displayMenu(writer, reader);
                        break;
                    default:
                        writer.println("Welcome User!");
                        break;
                }

                if (!continueSession) {
                    loginSuccess = false;
                }
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
}
