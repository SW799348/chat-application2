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
import lk.ijse.controller.ClientFormController;

public class LoginFormController {

    @FXML
    private TextField txtUsername;

    @FXML
    void btnLoginOnAction(ActionEvent event) {
        String username = txtUsername.getText(); // Get the username from the TextField

        try {
            // Load the clientForm.fxml file
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/view/clientForm.fxml"));
            Parent root = loader.load();

            // Get the controller associated with the loaded FXML
            ClientFormController controller = loader.getController();

            // Set the username in ClientFormController
            controller.setUsername(username);

            // Create a socket connection to the server (make sure the server is running)
            Socket socket = new Socket("localhost", 8888);

            // Initialize the ClientFormController with the socket
            controller.initialize(socket);

            // Create a new stage for the client application
            Stage clientStage = new Stage();
            Scene scene = new Scene(root);

            // Set the stage scene
            clientStage.setScene(scene);

            // Show the stage
            clientStage.show();

            // Close the current (login) stage
        //    txtUsername.getScene().getWindow().hide();

        } catch (IOException e) {
            e.printStackTrace();
            // Handle the exception appropriately (show an alert, log, etc.)
        }
    }
}
