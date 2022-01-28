package com.project.timescheduler.controllers;

import com.project.timescheduler.helpers.DBResults;
import com.project.timescheduler.services.DatabaseConnection;
import com.project.timescheduler.services.Encryption;
import com.project.timescheduler.services.Validation;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;

public class UserSettingsController {

    @FXML
    Label usernameLabel, emailLabel, firstnameLabel, lastnameLabel, hostedLabel, attendingLabel;
    @FXML
    Label feedback;
    @FXML
    TextField emailField;
    @FXML
    PasswordField passwordField;
    @FXML
    PasswordField confirmPasswordField;
    @FXML
    Button confirmButton;

    private String activeUser;
    private String email;
    private String firstname;
    private String lastname;
    private int hosted;
    private int attended;
    private DatabaseConnection connection;
    Validation validation = new Validation();
    Encryption encryption = new Encryption();

    @FXML
    public void initialize(String currentUser) {
        connection = new DatabaseConnection();
        activeUser = currentUser;

        loadData();

        confirmButton.disableProperty().bind(emailField.textProperty().isEmpty().and(
                passwordField.textProperty().isEmpty().or(
                        confirmPasswordField.textProperty().isEmpty()))
        );
    }

    private void loadData() {
        String sql_details = String.format("SELECT * FROM SCHED_USER WHERE USERNAME = '%s'", activeUser);
        DBResults userDetails = connection.query(sql_details);

        while (userDetails.next()) {
            email = userDetails.get("email");
            firstname = userDetails.get("firstname");
            lastname = userDetails.get("lastname");
        }

        String sql_hosted = String.format("SELECT COUNT(*) AS count FROM SCHED_EVENT WHERE CREATOR_NAME='%s'", activeUser);
        DBResults hostedNumber = connection.query(sql_hosted);

        while (hostedNumber.next()) {
            hosted = Integer.parseInt(hostedNumber.get("count"));
        }

        String sql_attend = String.format("SELECT COUNT(USERNAME) AS count FROM SCHED_PARTICIPATES_IN WHERE USERNAME='%s'", activeUser);
        DBResults attendNumber = connection.query(sql_attend);

        while (attendNumber.next()) {
            attended = Integer.parseInt(attendNumber.get("count"));
        }

        setLabels(email, firstname, lastname, hosted, attended);
    }

    private  void setLabels(String email, String firstname, String lastname, int hosted, int attended){
        usernameLabel.setText("Username: " + activeUser);
        emailLabel.setText("Email: " + email);
        firstnameLabel.setText("Firstname: " + firstname);
        lastnameLabel.setText("Lastname: " + lastname);
        hostedLabel.setText("Hosted Events: " + hosted);
        attendingLabel.setText("Attending Events: " + attended);
    }

    @FXML
    public void editUser(ActionEvent event) {
        if (passwordField.getText().isEmpty()) {
            if (validation.emailValidation(emailField.getText(), true)){
                String sql_temp = "UPDATE SCHED_USER SET EMAIL='%s' WHERE USERNAME='%s'";
                String sql = String.format(sql_temp, emailField.getText(), activeUser);
                connection.update(sql);
                loadData();
                feedback.setText("E-Mail was changed!");
            }
        }
        else if (emailField.getText().isEmpty()){
            if (validation.passwordValidation(passwordField.getText(), true)) {
                    String sql_temp = "UPDATE SCHED_USER SET PASSWORD='%s' WHERE USERNAME='%s'";
                    String sql = String.format(sql_temp, encryption.createHash(passwordField.getText()), activeUser);
                    connection.update(sql);
                    feedback.setText("Password was changed!");
                }
        }
        else {
            if (validation.emailValidation(emailField.getText(), true) &&
                    validation.passwordValidation(passwordField.getText(), true) &&
                    passwordField.getText().equals(confirmPasswordField.getText())){
                String sql_temp = "UPDATE SCHED_USER SET EMAIL='%s', PASSWORD='%s' WHERE USERNAME='%s'";
                String sql = String.format(sql_temp, emailField.getText(), encryption.createHash(passwordField.getText()), activeUser);
                connection.update(sql);
                loadData();
                feedback.setText("E-Mail and Password were changed!");
            }
        }
    }
}