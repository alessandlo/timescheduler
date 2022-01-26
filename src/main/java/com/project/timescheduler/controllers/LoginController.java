package com.project.timescheduler.controllers;

import com.project.timescheduler.services.DatabaseConnection;
import com.project.timescheduler.Main;
import com.project.timescheduler.services.Encryption;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.control.*;
import javafx.scene.layout.Pane;

import java.io.IOException;
import java.sql.ResultSet;
import java.sql.SQLException;
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

    public void initialize() {
        loginButton.disableProperty().bind(
                usernameField.textProperty().isEmpty().or(passwordField.textProperty().isEmpty())
        );
        userNotExist.visibleProperty().bind(usernameField.textProperty().isEmpty());
        passNotExist.visibleProperty().bind(passwordField.textProperty().isEmpty());
    }

    public void switchToRegister() throws IOException {
        loginPane.getChildren().clear();
        loginPane.getChildren().add(FXMLLoader.load(Objects.requireNonNull(Main.class.getResource("register.fxml"))));
    }

    public void switchToAdminpanel() throws IOException {
        loginPane.getChildren().clear();
        loginPane.getChildren().add(FXMLLoader.load(Objects.requireNonNull(Main.class.getResource("adminview.fxml"))));
        Main.mainStage.setMinWidth(800);
        Main.mainStage.setMinHeight(600);
    }

    public void switchToTimescheduler() throws IOException {
        FXMLLoader loader = new FXMLLoader(Objects.requireNonNull(Main.class.getResource("timescheduler.fxml")));
        Parent root = loader.load();

        TimeSchedulerController controller = loader.getController();
        controller.getCurrentUser(usernameField.getText());

        loginPane.getChildren().clear();
        loginPane.getChildren().add(root);
        Main.mainStage.setMinWidth(800);
        Main.mainStage.setMinHeight(600);
    }

    public void login() throws SQLException, IOException {
        DatabaseConnection connection = new DatabaseConnection();

        String sql_user = String.format("SELECT * FROM sched_user WHERE username='%s'",
                usernameField.getText());
        ResultSet checkUser = connection.query(sql_user);

        if (usernameField.getText().equals(DatabaseConnection.adminUserName) &&
                passwordField.getText().equals(DatabaseConnection.adminPassword)) {
            switchToAdminpanel();
        }
        else if (checkUser.next()){
            Encryption encryption = new Encryption();
            String sql_password = String.format("SELECT * FROM sched_user WHERE username='%s' AND password='%s'",
                    usernameField.getText(), encryption.createHash(passwordField.getText()));
            ResultSet checkPass = connection.query(sql_password);

            if (checkPass.next()) {
                switchToTimescheduler();
            }
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
