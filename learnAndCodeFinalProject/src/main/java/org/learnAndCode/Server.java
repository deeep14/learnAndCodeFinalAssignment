package learnAndCodeFinalProject.src.main.java.org.learnAndCode;

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

                    writer.println("Hello user! Please enter your login ID:");
                    String loginId = reader.readLine();

                    writer.println("Please enter your username:");
                    String username = reader.readLine();

                    writer.println("Welcome, " + username + "!");
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
