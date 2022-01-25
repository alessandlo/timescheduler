package com.project.timescheduler.controllers;

import com.project.timescheduler.services.DatabaseConnection;
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
    Label currentMail;
    @FXML
    TextField changedMail;
    @FXML
    PasswordField passwordField;
    @FXML
    PasswordField confirmPasswordField;

    private String activeUser;
    private String oldMail;
    private String newMail;

    Validation validation = new Validation();

    private DatabaseConnection connection;

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
                oldMail = rs.getString("email");
                displayCurrentMail(oldMail);
            }
        }

    private void displayCurrentMail(String oldMail) {
        currentMail.setText("Current Mail: " + oldMail);
    }
    @FXML
    private void changeMail(ActionEvent event) throws SQLException {
        if (validation.emailValidation(changedMail.getText())){
            String sql_temp = "UPDATE SCHED_USER SET EMAIL = '" + changedMail.getText() + "' WHERE USERNAME = '" + activeUser + "'";
            String sql = String.format(sql_temp, changedMail.getText());
            connection.update(sql);
            loadMail();
        }
    }
}