package com.project.timescheduler;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.*;
import javafx.scene.layout.Pane;

import java.io.IOException;
import java.sql.Connection;
import java.sql.Statement;
import java.util.Objects;

public class RegisterController {

    @FXML
    private Pane registerPane;
    @FXML
    private Label passwordLabel;
    @FXML
    private TextField usernameFieldRegister, emailFieldRegister;
    @FXML
    private PasswordField passwordFieldRegister;
    @FXML
    private Tooltip passTooltip;
    @FXML
    private Button registerButton;

    public void initialize() {
        registerButton.disableProperty().bind(usernameFieldRegister.textProperty().isEmpty().and(emailFieldRegister.textProperty().isEmpty()).and(passwordFieldRegister.textProperty().isEmpty()));
    }

    public void switchToLogin() throws IOException {
        registerPane.getChildren().clear();
        registerPane.getChildren().add(FXMLLoader.load(Objects.requireNonNull(getClass().getResource("login.fxml"))));
    }

    public void createAccount() {
        passwordLabel.setTooltip(passTooltip);

        Validation validation = new Validation();

        if (validation.usernameValidation(usernameFieldRegister.getText()) && validation.emailValidation(emailFieldRegister.getText()) && validation.passwordValidation(passwordFieldRegister.getText())) {
            DatabaseConnection databaseConnection = new DatabaseConnection();
            Connection connection = databaseConnection.getConnection();
            try {
                String sql_temp = "INSERT INTO sched_user (username, email, password) VALUES ('%s','%s','%s')";
                String sql = String.format(sql_temp, usernameFieldRegister.getText(), emailFieldRegister.getText(), passwordFieldRegister.getText());

                Statement statement = connection.createStatement();
                statement.executeUpdate(sql);

                Alert alert = new Alert(Alert.AlertType.INFORMATION);
                alert.setTitle("Information Dialog");
                alert.setHeaderText("Registration successful");
                alert.setContentText("You have created an account");
                alert.showAndWait();

                switchToLogin();
            } catch (Exception e) {
                Alert alert = new Alert(Alert.AlertType.INFORMATION);
                alert.setTitle("Information Dialog");
                alert.setHeaderText("User already exists");
                alert.setContentText("Thus username already exists");
                alert.showAndWait();
            }
        }
    }
}
