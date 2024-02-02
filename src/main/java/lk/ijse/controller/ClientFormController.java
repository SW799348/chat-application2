package lk.ijse.controller;

import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;
import javafx.stage.FileChooser;

import javax.imageio.ImageIO;
import java.io.*;
import java.net.Socket;

public class ClientFormController {

    @FXML
    private TextField txtMsg;

    @FXML
    private VBox vbox;

    @FXML
    private Label lblName;

    @FXML
    private ScrollPane scrollPane;

    private PrintWriter writer;
    private Socket clientSocket;
    private String username;

    @FXML
    void btnSendOnAction(ActionEvent event) {
        String message = txtMsg.getText();
        writer.println(username + ": " + message);
        txtMsg.clear();
    }

    @FXML
    void imojiSendOnAction(MouseEvent event) {
        String emoji = "😊";
        writer.println(username + ": " + emoji);
    }

    @FXML
    void attachFileOnMOuseClickedOnAction(MouseEvent event) {
        FileChooser fileChooser = new FileChooser();
        File selectedFile = fileChooser.showOpenDialog(null);

        if (selectedFile != null) {
            sendFile(selectedFile);
        }
    }

    private void sendFile(File file) {
        try {
            // Send a special message to indicate file transfer
            writer.println(username + ": [file] " + file.getName());

            // Open streams for file transfer
            FileInputStream fileInputStream = new FileInputStream(file);
            BufferedInputStream bufferedInputStream = new BufferedInputStream(fileInputStream);
            OutputStream outputStream = clientSocket.getOutputStream();

            // Transfer file
            byte[] buffer = new byte[1024];
            int bytesRead;
            while ((bytesRead = bufferedInputStream.read(buffer)) != -1) {
                outputStream.write(buffer, 0, bytesRead);
            }

            // Close streams
            bufferedInputStream.close();
            outputStream.close();

            // Display the sent image
            displaySentImage(file);

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void displaySentImage(File file) {
        try {
            Image image = new Image(file.toURI().toString());
            ImageView imageView = new ImageView(image);

            // Update UI on JavaFX application thread
            Platform.runLater(() -> vbox.getChildren().add(imageView));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void receiveImage(InputStream inputStream, String fileName) {
        try {
            Image image = new Image(inputStream);
            ImageView imageView = new ImageView(image);

            // Update UI on JavaFX application thread
            Platform.runLater(() -> vbox.getChildren().add(imageView));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void setUsername(String username) {
        this.username = username;
        lblName.setText(username);
    }

    public void initialize(Socket socket) {
        try {
            clientSocket = socket;
            BufferedReader serverReader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            writer = new PrintWriter(socket.getOutputStream(), true);

            Thread serverListener = new Thread(() -> {
                try {
                    String serverMessage;
                    while ((serverMessage = serverReader.readLine()) != null) {
                        if (serverMessage.startsWith("[file]")) {
                            String[] parts = serverMessage.split(" ");
                            String fileName = parts[2];
                            receiveImage(socket.getInputStream(), fileName);
                        } else {
                            String finalServerMessage = serverMessage;
                            Text textNode = new Text(serverMessage);

                            // Update UI on JavaFX application thread
                            Platform.runLater(() -> vbox.getChildren().add(textNode));
                        }
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
            });
            serverListener.start();

        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
