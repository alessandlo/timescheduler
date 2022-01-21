package com.project.timescheduler;

import com.project.timescheduler.services.DatabaseConnection;
import javafx.application.Application;
import javafx.event.EventHandler;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;
import javafx.stage.WindowEvent;

import java.io.IOException;
import java.sql.Connection;
import java.sql.SQLException;

public class Main extends Application {

    public static Stage mainStage;

    @Override
    public void start(Stage stage) throws IOException {
        FXMLLoader fxmlLoader = new FXMLLoader(Main.class.getResource("login.fxml"));
        Scene scene = new Scene(fxmlLoader.load());
        mainStage = stage;
        stage.setTitle("TimeScheduler");
        stage.setScene(scene);
        stage.setMinWidth(400);
        stage.setMinHeight(300);
        stage.setOnCloseRequest(windowEvent -> System.exit(0));
        stage.show();
    }

    public static void main(String[] args) {
        DatabaseConnection connection = new DatabaseConnection();

        try {
            System.out.println(connection.query("SELECT * FROM sched_user WHERE username='test'").next());
            System.out.println(connection.query("SELECT * FROM sched_user WHERE username='test_user'").next());
        } catch (SQLException e) {
            e.printStackTrace();
        }
        launch();
    }
}
