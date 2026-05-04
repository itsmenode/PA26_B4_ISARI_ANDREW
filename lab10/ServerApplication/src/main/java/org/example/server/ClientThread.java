package org.example.server;

import lombok.RequiredArgsConstructor;
import lombok.extern.java.Log;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;

@Log
@RequiredArgsConstructor
public class ClientThread extends Thread {

    private final Socket socket;
    private final GameServer server;

    @Override
    public void run() {
        try (BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
             PrintWriter out = new PrintWriter(socket.getOutputStream(), true)) {

            String command;
            while ((command = in.readLine()) != null) {
                if ("stop".equalsIgnoreCase(command)) {
                    out.println("Server stopped");
                    server.shutdown();
                    break;
                }
                out.println("Server received the request " + command);
            }
        } catch (IOException e) {
            log.warning("Connection error: " + e.getMessage());
        } finally {
            try {
                socket.close();
            } catch (IOException ignored) {
            }
        }
    }
}
