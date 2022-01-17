package com.project.timescheduler;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
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
        loginButton.disableProperty().bind(usernameFieldLogin.textProperty().isEmpty().and(passwordFieldLogin.textProperty().isEmpty()));
    }

    public void switchToRegister() throws IOException {
        loginPane.getChildren().clear();
        loginPane.getChildren().add(FXMLLoader.load(Objects.requireNonNull(getClass().getResource("register.fxml"))));
    }

    public void switchToAdminpanel() throws IOException {
        loginPane.getChildren().clear();
        loginPane.getChildren().add(FXMLLoader.load(Objects.requireNonNull(getClass().getResource("adminview.fxml"))));
        Main.mainStage.setMinWidth(800);
        Main.mainStage.setMinHeight(600);
    }

    public void switchToTimescheduler() throws IOException {
        loginPane.getChildren().clear();
        loginPane.getChildren().add(FXMLLoader.load(Objects.requireNonNull(getClass().getResource("timescheduler.fxml"))));
        Main.mainStage.setMinWidth(800);
        Main.mainStage.setMinHeight(600);
    }

    public void login() throws SQLException, IOException {

        if (usernameFieldLogin.getText().equals("admin") && passwordFieldLogin.getText().equals("admin")) {
            switchToAdminpanel();
        } else {
            DatabaseConnection databaseConnection = new DatabaseConnection();
            Connection connection = databaseConnection.getConnection();

            String sql_temp = "SELECT * FROM sched_user WHERE username='%s' AND password='%s'";
            String sql = String.format(sql_temp, usernameFieldLogin.getText(), passwordFieldLogin.getText());

            Statement statement = connection.createStatement();
            ResultSet rs = statement.executeQuery(sql);
            if (rs.next()) {
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
