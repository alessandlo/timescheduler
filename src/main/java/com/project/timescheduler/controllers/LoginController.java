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
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Objects;

public class LoginController {

    @FXML
    private Pane loginPane;
    @FXML
    private TextField usernameFieldLogin;
    @FXML
    private PasswordField passwordFieldLogin;
    @FXML
    private Button loginButton;

    public void initialize() {
        loginButton.disableProperty().bind(
                usernameFieldLogin.textProperty().isEmpty().and(passwordFieldLogin.textProperty().isEmpty())
        );
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
        controller.getCurrentUser(usernameFieldLogin.getText());

        loginPane.getChildren().clear();
        loginPane.getChildren().add(root);
        Main.mainStage.setMinWidth(800);
        Main.mainStage.setMinHeight(600);
    }

    public void login() throws SQLException, IOException {

        if (usernameFieldLogin.getText().equals(DatabaseConnection.adminUserName) &&
                passwordFieldLogin.getText().equals(DatabaseConnection.adminPassword)) {
            switchToAdminpanel();
        } else {
            Encryption encryption = new Encryption();
            String sql = String.format("SELECT * FROM sched_user WHERE username='%s' AND password='%s'",
                    usernameFieldLogin.getText(), encryption.createHash(passwordFieldLogin.getText()));

            ResultSet resultSet = new DatabaseConnection().query(sql);

            if (resultSet.next()) {
                switchToTimescheduler();
            } else {
                Alert alert = new Alert(Alert.AlertType.INFORMATION);
                alert.setTitle("Information Dialog");
                alert.setHeaderText("User doesn't exists");
                alert.setContentText(null);
                alert.showAndWait();
            }
        }
    }
}
