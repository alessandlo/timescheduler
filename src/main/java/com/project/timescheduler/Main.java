package com.project.timescheduler;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.IOException;

public class Main extends Application {

    public static Stage mainStage;

    @Override
    public void start(Stage stage) throws IOException {
        FXMLLoader fxmlLoader = new FXMLLoader(Main.class.getResource("login.fxml"));
        Scene scene = new Scene(fxmlLoader.load());
        mainStage = stage;
        mainStage.setTitle("TimeScheduler");
        mainStage.setScene(scene);
        mainStage.setMinWidth(400);
        mainStage.setMinHeight(300);
        mainStage.setOnCloseRequest(windowEvent -> System.exit(0));
        mainStage.show();
    }

    public static void main(String[] args) {
        launch();
    }
}
