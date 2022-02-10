package com.project.timescheduler.controllers;

import com.project.timescheduler.Main;
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

/** This controller class helps the users navigate their own information and change their important data
 * such as email and/or password on their very own. **/
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

    private String currentUser;
    private String email;
    private String firstname;
    private String lastname;
    private int hosted;
    private int attended;
    Validation validation = new Validation();
    Encryption encryption = new Encryption();

    private OnActionListener listener;

    public interface OnActionListener {
        void onAction();
    }

    @FXML
    public void initialize(OnActionListener listener, String currentUser) {
        this.listener = listener;
        this.currentUser = currentUser;

        confirmButton.disableProperty().bind(emailField.textProperty().isEmpty().and(
                passwordField.textProperty().isEmpty().or(
                        confirmPasswordField.textProperty().isEmpty()))
        );

        loadData();
    }

    /** Receiving and loading information of logged-in user from database. **/
    private void loadData() {
        String sql_details = String.format("SELECT * FROM SCHED_USER WHERE USERNAME = '%s'", currentUser);
        DBResults userDetails = Main.connection.query(sql_details);

        while (userDetails.next()) {
            email = userDetails.get("email");
            firstname = userDetails.get("firstname");
            lastname = userDetails.get("lastname");
        }

        String sql_hosted = String.format("SELECT COUNT(*) AS count FROM SCHED_EVENT WHERE CREATOR_NAME='%s'", currentUser);
        DBResults hostedNumber = Main.connection.query(sql_hosted);

        while (hostedNumber.next()) {
            hosted = Integer.parseInt(hostedNumber.get("count"));
        }

        String sql_attend = String.format("SELECT COUNT(USERNAME) AS count FROM SCHED_PARTICIPATES_IN WHERE USERNAME='%s'", currentUser);
        DBResults attendNumber = Main.connection.query(sql_attend);

        while (attendNumber.next()) {
            attended = Integer.parseInt(attendNumber.get("count"));
        }

        setLabels(email, firstname, lastname, hosted, attended);
    }

    /** Setting the loaded up for display. **/
    private void setLabels(String email, String firstname, String lastname, int hosted, int attended){
        usernameLabel.setText("Username: " + currentUser);
        emailLabel.setText("Email: " + email);
        firstnameLabel.setText("Firstname: " + firstname);
        lastnameLabel.setText("Lastname: " + lastname);
        hostedLabel.setText("Hosted Events: " + hosted);
        attendingLabel.setText("Attending Events: " + attended);
    }

    /** Edit-function for changing logged-in user's password or/and E-Mail. **/
    @FXML
    public void editUser(ActionEvent event) {
        // Check if user only wants to change his E-Mail.
        if (passwordField.getText().isEmpty()) {
            if (validation.emailValidation(emailField.getText(), true)){
                String sql_temp = "UPDATE SCHED_USER SET EMAIL='%s' WHERE USERNAME='%s'";
                String sql = String.format(sql_temp, emailField.getText(), currentUser);
                Main.connection.update(sql);
                loadData();
                feedback.setText("E-Mail was changed!");
            }
        }
        // Check if user only wants to change his password.
        else if (emailField.getText().isEmpty()){
            if (validation.passwordValidation(passwordField.getText(), true)) {
                    String sql_temp = "UPDATE SCHED_USER SET PASSWORD='%s' WHERE USERNAME='%s'";
                    String sql = String.format(sql_temp, encryption.createHash(passwordField.getText()), currentUser);
                    Main.connection.update(sql);
                    feedback.setText("Password was changed!");
                }
        }
        // Neither password-field nor mail-field is empty, thus assume that the user wants to change both
        else {
            if (validation.emailValidation(emailField.getText(), true) &&
                    validation.passwordValidation(passwordField.getText(), true) &&
                    passwordField.getText().equals(confirmPasswordField.getText())){
                String sql_temp = "UPDATE SCHED_USER SET EMAIL='%s', PASSWORD='%s' WHERE USERNAME='%s'";
                String sql = String.format(sql_temp, emailField.getText(), encryption.createHash(passwordField.getText()), currentUser);
                Main.connection.update(sql);
                loadData();
                feedback.setText("E-Mail and Password were changed!");
            }
        }
    }

    @FXML
    private void exitDetails(){
        listener.onAction();
    }
}