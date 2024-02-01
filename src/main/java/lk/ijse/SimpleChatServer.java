package lk.ijse;

import lk.ijse.controller.ClientFormController;
import lk.ijse.controller.LoginFormController;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;

public class SimpleChatServer {

    private final int port;
    private final List<PrintWriter> clients = new ArrayList<>();

    public SimpleChatServer(int port) {
        this.port = port;
    }

    public void start() {
        try (ServerSocket serverSocket = new ServerSocket(port)) {
            System.out.println("Server is listening on port " + port);

            while (true) {
                Socket clientSocket = serverSocket.accept();
                System.out.println("New client connected: " + clientSocket);

                PrintWriter writer = new PrintWriter(clientSocket.getOutputStream(), true);
                clients.add(writer);

                Thread clientListener = new Thread(() -> handleClient(clientSocket, writer));
                clientListener.start();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void handleClient(Socket clientSocket, PrintWriter writer) {
        try {
            BufferedReader reader = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
           // broadcastMessage(username + " has joined the chat.");

            String clientMessage;
            while ((clientMessage = reader.readLine()) != null) {
                broadcastMessage(clientMessage);
            }

        } catch (IOException e) {
            System.out.println("Error handling client: " + e.getMessage());
        } finally {
            clients.remove(writer);
            broadcastMessage("A client has left the chat.");
            try {
                clientSocket.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    private void broadcastMessage(String message) {
        for (PrintWriter client : clients) {
            client.println(message);
        }
    }

    public static void main(String[] args) {
        int port = 8888;
        SimpleChatServer server = new SimpleChatServer(port);
        server.start();
    }
}