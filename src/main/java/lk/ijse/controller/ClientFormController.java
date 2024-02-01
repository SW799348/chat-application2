package lk.ijse.controller;

import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TextField;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;

public class ClientFormController {

    @FXML
    private TextField txtMsg;

    @FXML
    private ScrollPane scrollPane;

    @FXML
    private VBox vbox;

    @FXML
    private Label lblName;

    private PrintWriter writer;
    private String username; // New field to store the username

    @FXML
    void btnSendOnAction(ActionEvent event) {
        String message = txtMsg.getText();
        writer.println(username + ": " + message); // Include username in the message
        txtMsg.clear();
    }

    public void setUsername(String username) {
        this.username = username;
        lblName.setText(username);
    }

    public void initialize(Socket socket) {
        try {
            BufferedReader serverReader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            writer = new PrintWriter(socket.getOutputStream(), true);

            Thread serverListener = new Thread(() -> {
                try {

                    String serverMessage;
                    while ((serverMessage = serverReader.readLine()) != null) {

                        // Update UI on JavaFX application thread
                        String finalServerMessage = serverMessage;

                        String smilingFace = "\uD83D\uDE04"; // 😀

                        Text textNode = new Text(smilingFace);

                        Platform.runLater(() -> vbox.getChildren().add(textNode));






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