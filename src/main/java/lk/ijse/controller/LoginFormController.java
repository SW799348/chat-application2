package lk.ijse.controller;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.TextField;
import java.io.IOException;
import java.net.Socket;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class LoginFormController {

    @FXML
    private TextField txtUsername;

    @FXML
    void btnLoginOnAction(ActionEvent event) {
        String username = txtUsername.getText(); // Get the username from the TextField

        Stage stage = new Stage();

        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/view/clientForm.fxml"));
            Parent root = loader.load();
            ClientFormController controller = loader.getController();

            // Set the username in ClientFormController
            controller.setUsername(username);

            Socket socket = new Socket("localhost", 8888);
            controller.initialize(socket); // Pass the username to initialize

            Scene scene = new Scene(root);
            stage.setScene(scene);
            stage.show();

        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}