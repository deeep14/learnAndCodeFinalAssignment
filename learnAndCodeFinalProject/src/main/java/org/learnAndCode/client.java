package learnAndCodeFinalProject.src.main.java.org.learnAndCode;

import java.io.*;
import java.net.Socket;
import java.net.UnknownHostException;

public class client {
    public static void main(String[] args) 
    {
        String hostname = "localhost";
        int port = 9202;

        try (Socket socket = new Socket(hostname, port)) 
        {

            InputStream input = socket.getInputStream();
            BufferedReader reader = new BufferedReader(new InputStreamReader(input));

            OutputStream output = socket.getOutputStream();
            PrintWriter writer = new PrintWriter(output, true);

            BufferedReader consoleReader = new BufferedReader(new InputStreamReader(System.in));

            String serverMessage = reader.readLine();
            System.out.println(serverMessage);

            String loginId = consoleReader.readLine();
            writer.println(loginId);

            serverMessage = reader.readLine();
            System.out.println(serverMessage);

            String username = consoleReader.readLine();
            writer.println(username);

            serverMessage = reader.readLine();
            System.out.println(serverMessage);

        } 
        catch (UnknownHostException e) 
        {
            System.out.println("Server not found: " + e.getMessage());
        } 
        catch (IOException e) 
        {
            System.out.println("I/O error: " + e.getMessage());
        }
    }
}
