package org.example.server;

import lombok.Getter;
import lombok.extern.java.Log;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

@Log
@Getter
public class GameServer {

    private static final int DEFAULT_PORT = 5000;

    private final int port;
    private ServerSocket serverSocket;
    private volatile boolean running;

    public GameServer(int port) {
        this.port = port;
    }

    public void start() {
        try {
            serverSocket = new ServerSocket(port);
            running = true;
            log.info("GameServer listening on port " + port);

            while (running) {
                Socket client = serverSocket.accept();
                log.info("Accepted client " + client.getRemoteSocketAddress());
                new ClientThread(client, this).start();
            }
        } catch (IOException e) {
            if (running) {
                log.severe("Server error: " + e.getMessage());
            }
        }
    }

    public void shutdown() {
        running = false;
        try {
            if (serverSocket != null && !serverSocket.isClosed()) {
                serverSocket.close();
            }
            log.info("GameServer shut down");
        } catch (IOException e) {
            log.warning("Shutdown error: " + e.getMessage());
        }
    }

    public static void main(String[] args) {
        int port = args.length > 0 ? Integer.parseInt(args[0]) : DEFAULT_PORT;
        new GameServer(port).start();
    }
}
