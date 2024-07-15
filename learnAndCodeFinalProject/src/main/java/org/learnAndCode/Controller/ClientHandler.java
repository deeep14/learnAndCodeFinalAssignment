package org.learnAndCode.Server;

import org.learnAndCode.*;

import java.io.*;
import java.net.Socket;

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
                switch (roleId) {
                    case 1:
                        writer.println("Welcome Admin!");
                        Admin.displayMenu(writer, reader);
                        break;
                    case 2:
                        writer.println("Welcome Chef!");
                        Chef.displayMenu(writer, reader);
                        break;
                    case 3:
                        writer.println("Welcome Employee!");
                        Employee.displayMenu(writer, reader);
                        break;
                    default:
                        writer.println("Welcome User!");
                        break;
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
}
