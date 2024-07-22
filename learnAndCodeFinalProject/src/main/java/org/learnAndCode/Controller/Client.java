package org.learnAndCode.Controller;

import java.io.*;
import java.net.Socket;
import java.net.UnknownHostException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.List;
import java.util.stream.Collectors;

public class Client {
    private static final String HOSTNAME = "localhost";
    private static final int PORT = 9202;
    private static final String PROMPTS_FILE = "src/main/java/org/learnAndCode/Util/prompts.txt";
    private static List<String> PROMPTS;

    public static void main(String[] args) {
        try {
            PROMPTS = readPromptsFromFile(PROMPTS_FILE);

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
        } catch (IOException e) {
            System.err.println("Error reading prompts file: " + e.getMessage());
        }
    }

    private static boolean isPrompt(String message) {
        return PROMPTS.stream().anyMatch(message::contains);
    }

    private static List<String> readPromptsFromFile(String filePath) throws IOException {
        return Files.lines(Paths.get(filePath)).collect(Collectors.toList());
    }
}
