package com.project.timescheduler.services;

import javafx.scene.control.Alert;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Validation {

    public boolean usernameValidation(String usernameFieldRegister){
        String regex = "^[a-zA-Z0-9._-]{2,32}$";

        Pattern pattern = Pattern.compile(regex);
        Matcher matcher = pattern.matcher(usernameFieldRegister);

        if (matcher.find()){
            return true;
        }
        else {
            Alert alert = new Alert(Alert.AlertType.WARNING);
            alert.setTitle("Validation error");
            alert.setHeaderText("Enter right Username");
            alert.setContentText("The username can only consist of the following characters:\nA-Z\na-z\n0-9\n.\t(Dot)\n_\t(Underscore)\n-\t(Dash)");

            alert.showAndWait();

            return false;
        }
    }

    public boolean emailValidation(String emailFieldRegister){
        String regex = "^(?=.{4,50}$)[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+$";

        Pattern pattern = Pattern.compile(regex);
        Matcher matcher = pattern.matcher(emailFieldRegister);

        if (matcher.find()){
            return true;
        }
        else {
            Alert alert = new Alert(Alert.AlertType.WARNING);
            alert.setTitle("Validation error");
            alert.setHeaderText(null);
            alert.setContentText("Enter right E-Mail address");

            alert.showAndWait();

            return false;
        }
    }

    public boolean passwordValidation(String passwordFieldRegister){
        String regex = "^(?=.*[0-9])(?=.*[a-z])(?=.*[A-Z])(?=.*[@#$%^&+=])(?=^\\S+$).{8,32}$";
        /*
        (?=.*[0-9])       # at least one digit
        (?=.*[a-z])       # at least one lower case letter
        (?=.*[A-Z])       # at least one upper case letter
        (?=.*[@#$%^&+=])  # at least one special character
        (?=^\S+$)          # no whitespace allowed
        .{8,32}           # between 8 and 32 characters
        */

        Pattern pattern = Pattern.compile(regex);
        Matcher matcher = pattern.matcher(passwordFieldRegister);

        if (matcher.find()){
            return true;
        }
        else {
            Alert alert = new Alert(Alert.AlertType.WARNING);
            alert.setTitle("Validation error");
            alert.setHeaderText("Enter right Password");
            alert.setContentText("The password can only consist of the following characters:\n- at least one digit\t\t\t\t0-9\n- at least one lower case letter\ta-z\n- at least one upper case letter\tA-Z\n- at least one special character\t@#$%^&+=\n- between 8 and 32 characters");

            alert.showAndWait();

            return false;
        }
    }
}
