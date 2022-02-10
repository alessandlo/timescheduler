package com.project.timescheduler;

import com.project.timescheduler.services.DatabaseConnection;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.stage.Stage;

import java.io.IOException;

public class Main extends Application {

    public static Stage mainStage;
    public static DatabaseConnection connection;

    /** starts the GUI */
    @Override
    public void start(Stage stage) throws IOException {
        FXMLLoader fxmlLoader = new FXMLLoader(Main.class.getResource("login.fxml"));
        Scene scene = new Scene(fxmlLoader.load());
        mainStage = stage;
        mainStage.getIcons().add(new Image(String.valueOf(getClass().getResource("images/icon.png"))));
        mainStage.setTitle("TimeScheduler");
        mainStage.setScene(scene);
        mainStage.setMinWidth(300);
        mainStage.setMinHeight(300);
        mainStage.setOnCloseRequest(windowEvent -> System.exit(0));
        mainStage.show();
    }

    /** main method, intializes the static DatabaseConnection variable of Main and calls launch() */
    public static void main(String[] args) {
        connection = new DatabaseConnection();
        launch();
    }
}
