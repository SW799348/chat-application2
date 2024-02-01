package lk.ijse;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class Launcher extends Application {
    @Override
    public void start(Stage stage) throws Exception {
        Parent root = FXMLLoader.load(Launcher.class.getResource("/view/loginForm.fxml"));
        Scene scene = new Scene(root);
        stage.setScene(scene);
        //Image icon= new Image("images/logo.png");
        //stage.getIcons().add(icon);
        stage.setTitle("");
        stage.show();
    }

    public static void main(String[] args) {
        launch(args);
    }
}
