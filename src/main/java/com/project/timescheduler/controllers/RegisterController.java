package com.project.timescheduler.controllers;

import com.project.timescheduler.Main;
import com.project.timescheduler.services.DatabaseConnection;
import com.project.timescheduler.services.Encryption;
import com.project.timescheduler.services.Validation;
import javafx.beans.binding.Bindings;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.*;
import javafx.scene.layout.Pane;

import java.io.IOException;
import java.util.Objects;

public class RegisterController {

    @FXML
    private Pane registerPane;
    @FXML
    private Label passwordLabel, checkUser, checkFirstname, checkLastname, checkEmail, checkPassword, userAlreadyExist;
    @FXML
    private TextField usernameField, firstnameField, lastnameField, emailField;
    @FXML
    private PasswordField passwordField;
    @FXML
    private Tooltip passTooltip;
    @FXML
    private Button registerButton;

    Validation validation = new Validation();
    String check = null;

    @FXML
    private void initialize() {
        registerButton.disableProperty().bind(usernameField.textProperty().isEmpty().or(
                firstnameField.textProperty().isEmpty().or(
                        lastnameField.textProperty().isEmpty().or(
                                emailField.textProperty().isEmpty()).or(
                                    passwordField.textProperty().isEmpty())))
        );
        checkUser.textProperty().bind(Bindings.createStringBinding(() -> {
            check = "✗";
            if (validation.usernameValidation(usernameField.getText(), false))
                check = "✓";
            return check;
        }, usernameField.textProperty()));
        checkFirstname.textProperty().bind(Bindings.createStringBinding(() -> {
            check = "✗";
            if (validation.nameValidation(firstnameField.getText(), false))
                check = "✓";
            return check;
        }, firstnameField.textProperty()));
        checkLastname.textProperty().bind(Bindings.createStringBinding(() -> {
            check = "✗";
            if (validation.nameValidation(lastnameField.getText(), false))
                check = "✓";
            return check;
        }, lastnameField.textProperty()));
        checkEmail.textProperty().bind(Bindings.createStringBinding(() -> {
            check = "✗";
            if (validation.emailValidation(emailField.getText(), false))
                check = "✓";
            return check;
        }, emailField.textProperty()));
        checkPassword.textProperty().bind(Bindings.createStringBinding(() -> {
            check = "✗";
            if (validation.passwordValidation(passwordField.getText(), false))
                check = "✓";
            return check;
        }, passwordField.textProperty()));

        userAlreadyExist.visibleProperty().bind(usernameField.textProperty().isEmpty());
    }

    @FXML
    private void switchToLogin() throws IOException {
        registerPane.getChildren().clear();
        registerPane.getChildren().add(FXMLLoader.load(Objects.requireNonNull(Main.class.getResource("login.fxml"))));
        Main.mainStage.setMinWidth(300);
        Main.mainStage.setMinHeight(300);
    }

    @FXML
    private void createAccount() throws IOException{
        passwordLabel.setTooltip(passTooltip);

        Validation validation = new Validation();

        if (validation.usernameValidation(usernameField.getText(), true) &&
                validation.nameValidation(firstnameField.getText(), true) &&
                validation.nameValidation(lastnameField.getText(), true) &&
                validation.emailValidation(emailField.getText(), true) &&
                validation.passwordValidation(passwordField.getText(), true)) {

            if(!Main.connection.query(String.format("SELECT * FROM sched_user WHERE username='%s'",
                    usernameField.getText())).next()) {
                Encryption encryption = new Encryption();
                String sql = String.format("INSERT INTO sched_user (username, firstname, lastname, email, password) VALUES ('%s','%s','%s','%s','%s')",
                        usernameField.getText(), firstnameField.getText(), lastnameField.getText(),
                        emailField.getText(), encryption.createHash(passwordField.getText()));

                Main.connection.update(sql);

                Alert alert = new Alert(Alert.AlertType.INFORMATION);
                alert.setTitle("Information Dialog");
                alert.setHeaderText("Registration successful");
                alert.setContentText("You have created an account");
                alert.showAndWait();

                switchToLogin();
            }else {
                userAlreadyExist.setText("User already exist");
                usernameField.clear();
                usernameField.requestFocus();
            }
        }
    }
}
