package com.project.timescheduler.controllers;

import com.project.timescheduler.helpers.DBResults;
import com.project.timescheduler.services.DatabaseConnection;
import com.project.timescheduler.Main;
import com.project.timescheduler.services.Encryption;
import com.project.timescheduler.services.User;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.control.*;
import javafx.scene.layout.Pane;

import java.io.IOException;
import java.util.Objects;

public class LoginController {

    @FXML
    private Pane loginPane;
    @FXML
    private TextField usernameField;
    @FXML
    private PasswordField passwordField;
    @FXML
    private Button loginButton;
    @FXML
    private Label userNotExist, passNotExist;

    @FXML
    private void initialize() {
        loginButton.disableProperty().bind(
                usernameField.textProperty().isEmpty().or(passwordField.textProperty().isEmpty())
        );
        userNotExist.visibleProperty().bind(usernameField.textProperty().isEmpty());
        passNotExist.visibleProperty().bind(passwordField.textProperty().isEmpty());
    }

    @FXML
    private void switchToRegister() throws IOException {
        loginPane.getChildren().clear();
        loginPane.getChildren().add(FXMLLoader.load(Objects.requireNonNull(Main.class.getResource("register.fxml"))));
        Main.mainStage.setMinWidth(400);
        Main.mainStage.setMinHeight(350);
    }

    @FXML
    private void switchToAdminpanel() throws IOException {
        loginPane.getChildren().clear();
        loginPane.getChildren().add(FXMLLoader.load(Objects.requireNonNull(Main.class.getResource("adminview.fxml"))));
        Main.mainStage.setMinWidth(800);
        Main.mainStage.setMinHeight(600);
    }

    @FXML
    private void switchToTimescheduler() throws IOException {
        FXMLLoader loader = new FXMLLoader(Objects.requireNonNull(Main.class.getResource("timescheduler.fxml")));
        Parent root = loader.load();

        User currentUser = new User(usernameField.getText());

        TimeSchedulerController controller = loader.getController();
        controller.initialize(currentUser);

        loginPane.getChildren().clear();
        loginPane.getChildren().add(root);
        Main.mainStage.setMinWidth(800);
        Main.mainStage.setMinHeight(600);
    }

    @FXML
    private void login() throws IOException {
        String sql_user = String.format("SELECT * FROM sched_user WHERE username='%s'",
                usernameField.getText());
        DBResults checkUser = Main.connection.query(sql_user);

        if (usernameField.getText().equals(DatabaseConnection.adminUserName) &&
                passwordField.getText().equals(DatabaseConnection.adminPassword)) {
            switchToAdminpanel();
        }
        else if (checkUser.next()){
            Encryption encryption = new Encryption();
            if(checkUser.get("PASSWORD").equals(encryption.createHash(passwordField.getText())))
                switchToTimescheduler();
            else {
                passNotExist.setText("Wrong Password");
                passwordField.clear();
                passwordField.requestFocus();
            }
        }
        else {
            passNotExist.setText("");
            userNotExist.setText("User does not exist");
            usernameField.clear();
            passwordField.clear();
            usernameField.requestFocus();
        }
    }
}
