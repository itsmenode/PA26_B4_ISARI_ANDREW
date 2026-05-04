package org.example.client;

import lombok.extern.java.Log;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;

@Log
public class GameClient {

    private static final String DEFAULT_HOST = "localhost";
    private static final int DEFAULT_PORT = 5000;
    private static final String EXIT_COMMAND = "exit";

    private final String host;
    private final int port;

    public GameClient(String host, int port) {
        this.host = host;
        this.port = port;
    }

    public void run() {
        try (Socket socket = new Socket(host, port);
             BufferedReader serverIn = new BufferedReader(new InputStreamReader(socket.getInputStream()));
             PrintWriter serverOut = new PrintWriter(socket.getOutputStream(), true);
             BufferedReader keyboard = new BufferedReader(new InputStreamReader(System.in))) {

            log.info("Connected to " + host + ":" + port);

            String line;
            while (true) {
                System.out.print("> ");
                line = keyboard.readLine();
                if (line == null || EXIT_COMMAND.equalsIgnoreCase(line)) {
                    break;
                }
                serverOut.println(line);
                String response = serverIn.readLine();
                if (response == null) {
                    log.info("Server closed the connection");
                    break;
                }
                System.out.println(response);
            }
        } catch (IOException e) {
            log.severe("Client error: " + e.getMessage());
        }
    }

    public static void main(String[] args) {
        String host = args.length > 0 ? args[0] : DEFAULT_HOST;
        int port = args.length > 1 ? Integer.parseInt(args[1]) : DEFAULT_PORT;
        new GameClient(host, port).run();
    }
}
