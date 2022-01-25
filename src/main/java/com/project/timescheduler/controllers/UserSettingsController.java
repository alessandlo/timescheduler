package com.project.timescheduler.controllers;

import com.project.timescheduler.services.DatabaseConnection;
import com.project.timescheduler.services.Encryption;
import com.project.timescheduler.services.Validation;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;

import java.sql.ResultSet;
import java.sql.SQLException;

public class UserSettingsController {

    @FXML
    Label currentMailLabel;
    @FXML
    Label feedback;
    @FXML
    TextField changedMail;
    @FXML
    PasswordField passwordField;
    @FXML
    PasswordField confirmPasswordField;

    private String activeUser;
    private String currentMail;
    private DatabaseConnection connection;
    Validation validation = new Validation();
    Encryption encryption = new Encryption();
    @FXML
    public void initialize(String currentUser) {
        connection = new DatabaseConnection();
        activeUser = currentUser;
        try {
            loadMail();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void loadMail() throws SQLException {

            String sql = "SELECT EMAIL FROM SCHED_USER WHERE USERNAME = '" + activeUser + "'";
            ResultSet rs = connection.query(sql);
            while (rs.next()) {
                currentMail = rs.getString("EMAIL");
                displayMail(currentMail);
            }
        }

    private void displayMail(String currentMail) {
        currentMailLabel.setText("Current Mail: " + currentMail);
    }

    @FXML
    public void editUser(ActionEvent event) throws SQLException {
        if (passwordField.getText().isEmpty() && validation.emailValidation(changedMail.getText())){
            String sql_temp = "UPDATE SCHED_USER SET EMAIL='%s' WHERE USERNAME='%s'";
            String sql = String.format(sql_temp, changedMail.getText(), activeUser);
            connection.update(sql);
            loadMail();
            feedback.setText("E-Mail was changed!");
        }   else if (changedMail.getText().isEmpty() && validation.passwordValidation(passwordField.getText())
                && passwordField.getText().equals(confirmPasswordField.getText())){
            String sql_temp = "UPDATE SCHED_USER SET PASSWORD='%s' WHERE USERNAME='%s'";
            String sql = String.format(sql_temp, encryption.createHash(passwordField.getText()), activeUser);
            connection.update(sql);
            feedback.setText("Password was changed!");
        }
        else if (validation.emailValidation(changedMail.getText()) && validation.passwordValidation(passwordField.getText())
                && passwordField.getText().equals(confirmPasswordField.getText())){
            String sql_temp = "UPDATE SCHED_USER SET EMAIL='%s', PASSWORD='%s' WHERE USERNAME='%s'";
            String sql = String.format(sql_temp, changedMail.getText(), encryption.createHash(passwordField.getText()), activeUser);
            connection.update(sql);
            loadMail();
            feedback.setText("E-Mail and Password were changed!");
        }
    }
}