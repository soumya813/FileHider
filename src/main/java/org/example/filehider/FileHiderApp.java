package org.example.filehider;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;
import views.fx.WelcomeController;

import java.io.IOException;

public class FileHiderApp extends Application {

    @Override
    public void start(Stage primaryStage) throws IOException {
        primaryStage.setTitle("FileHider - Secure File Management");

        // Set application icon (you can add an icon file later)
        // primaryStage.getIcons().add(new Image(getClass().getResourceAsStream("/icons/app-icon.png")));

        // Load the welcome/login screen
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/welcome.fxml"));
        Scene scene = new Scene(loader.load(), 800, 600);

        // Add CSS styling (we'll create this later)
        scene.getStylesheets().add(getClass().getResource("/css/styles.css").toExternalForm());

        primaryStage.setScene(scene);
        primaryStage.setMinWidth(600);
        primaryStage.setMinHeight(400);
        primaryStage.show();

        // Set the primary stage in the controller for navigation
        WelcomeController controller = loader.getController();
        controller.setPrimaryStage(primaryStage);
    }

    public static void main(String[] args) {
        launch(args);
    }
}
