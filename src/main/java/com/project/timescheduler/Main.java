package com.project.timescheduler;

import com.project.timescheduler.services.DatabaseConnection;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.IOException;

public class Main extends Application {

    public static Stage mainStage;
    public static DatabaseConnection connection;

    @Override
    public void start(Stage stage) throws IOException {
        FXMLLoader fxmlLoader = new FXMLLoader(Main.class.getResource("login.fxml"));
        Scene scene = new Scene(fxmlLoader.load());
        mainStage = stage;
        
        mainStage.setTitle("TimeScheduler");
        mainStage.setScene(scene);
        mainStage.setMinWidth(300);
        mainStage.setMinHeight(300);
        mainStage.setOnCloseRequest(windowEvent -> System.exit(0));
        mainStage.show();
    }

    public static void main(String[] args) {
        connection = new DatabaseConnection();

        launch();
    }
}
