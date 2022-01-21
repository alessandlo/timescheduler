package com.project.timescheduler.controllers;

import com.project.timescheduler.Main;
import com.project.timescheduler.services.DatabaseConnection;
import com.project.timescheduler.services.Validation;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.*;
import javafx.scene.layout.Pane;

import java.io.IOException;
import java.sql.SQLException;
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
        registerButton.disableProperty().bind(
                usernameFieldRegister.textProperty().isEmpty().and(
                        emailFieldRegister.textProperty().isEmpty()).and(
                                passwordFieldRegister.textProperty().isEmpty()
                )
        );
    }

    public void switchToLogin() throws IOException {
        registerPane.getChildren().clear();
        registerPane.getChildren().add(FXMLLoader.load(Objects.requireNonNull(Main.class.getResource("login.fxml"))));
    }

    public void createAccount() throws SQLException, IOException{
        passwordLabel.setTooltip(passTooltip);

        Validation validation = new Validation();

        if (validation.usernameValidation(usernameFieldRegister.getText()) &&
                validation.emailValidation(emailFieldRegister.getText()) &&
                validation.passwordValidation(passwordFieldRegister.getText())) {

            DatabaseConnection connection = new DatabaseConnection();

            if(!connection.query(String.format("SELECT * FROM sched_user WHERE username='%s'",
                    usernameFieldRegister.getText())).next()) {

                String sql = String.format("INSERT INTO sched_user (username, email, password) VALUES ('%s','%s','%s')",
                        usernameFieldRegister.getText(), emailFieldRegister.getText(), passwordFieldRegister.getText());

                connection.update(sql);

                Alert alert = new Alert(Alert.AlertType.INFORMATION);
                alert.setTitle("Information Dialog");
                alert.setHeaderText("Registration successful");
                alert.setContentText("You have created an account");
                alert.showAndWait();

                switchToLogin();
            }else {
                Alert alert = new Alert(Alert.AlertType.INFORMATION);
                alert.setTitle("Information Dialog");
                alert.setHeaderText("User already exists");
                alert.setContentText("Thus username already exists");
                alert.showAndWait();
            }
        }
    }
}