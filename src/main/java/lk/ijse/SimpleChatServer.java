import java.io.*;
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

            String clientMessage;
            while ((clientMessage = reader.readLine()) != null) {
                if (clientMessage.startsWith("[file]")) {
                    receiveFile(clientMessage, clientSocket.getInputStream());
                } else {
                    broadcastMessage(clientMessage);
                }
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

    private void receiveFile(String message, InputStream inputStream) {
        try {
            String[] parts = message.split(" ");
            String fileName = parts[2];
            File file = new File("received_files/" + fileName);

            FileOutputStream fileOutputStream = new FileOutputStream(file);
            BufferedOutputStream bufferedOutputStream = new BufferedOutputStream(fileOutputStream);

            byte[] buffer = new byte[1024];
            int bytesRead;
            while ((bytesRead = inputStream.read(buffer)) != -1) {
                bufferedOutputStream.write(buffer, 0, bytesRead);
            }

            bufferedOutputStream.close();
            System.out.println("File received: " + fileName);

            broadcastMessage("[file] " + fileName);

        } catch (IOException e) {
            e.printStackTrace();
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
