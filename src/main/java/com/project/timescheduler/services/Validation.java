package com.project.timescheduler.services;

import javafx.scene.control.Alert;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * This class is responsible for the validation of the inputs
 */
public class Validation {

    /**
     * Checks the username
     * @param username Entered username
     * @param showAlert If the alert should be displayed
     * @return Returns result
     */
    public boolean usernameValidation(String username, boolean showAlert){
        String regex = "^(?=.{2,32}$)(?![_.])(?!.*[_.]{2})[a-zA-Z0-9._]+(?<![_.])$";
        /*
        (?=.{2,32}$)        ->   between 2 and 32 characters
        (?![_.])            ->   _ or . is not allowed as first character
        (?!.*[_.]{2})       ->   __, .., _., ._ are not allowed in general
        [a-zA-Z0-9._]+      ->   allowed characters
        (?<![_.])           ->   _ or . is not allowed as last character
        .{8,32}             ->   between 8 and 32 characters (any character)
        */

        Pattern pattern = Pattern.compile(regex);
        Matcher matcher = pattern.matcher(username);

        if (matcher.find()){
            return true;
        }
        else {
            if (showAlert){
                Alert alert = new Alert(Alert.AlertType.WARNING);
                alert.setTitle("Validation error");
                alert.setHeaderText("Enter right Username");
                alert.setContentText("The username can only consist of the following characters:\nLetters\nNumbers\n.\t(Dot)\n_\t(Underscore)");

                alert.showAndWait();
            }
            return false;
        }
    }

    /**
     * Checks the first/lastname
     * @param firstname Entered first/lastname
     * @param showAlert If the alert should be displayed
     * @return Returns result
     */
    public boolean nameValidation(String firstname, boolean showAlert){
        String regex = "^(?=.{2,32}$)[a-zA-ZÖöÜüÄä]+$";
        /*
        (?=.{2,32}$)        ->   between 2 and 32 characters
        [a-zA-ZÖöÜüÄä]+     ->   allowed characters
        */

        Pattern pattern = Pattern.compile(regex);
        Matcher matcher = pattern.matcher(firstname);

        if (matcher.find()){
            return true;
        }
        else {
            if (showAlert){
                Alert alert = new Alert(Alert.AlertType.WARNING);
                alert.setTitle("Validation error");
                alert.setHeaderText("Enter right Name");
                alert.setContentText("The first-/lastname can only consist of the following characters:\nLetters\nmin. 2 characters");

                alert.showAndWait();
            }
            return false;
        }
    }

    /**
     * Checks the email
     * @param email Entered email
     * @param showAlert If the alert should be displayed
     * @return Returns result
     */
    public boolean emailValidation(String email, boolean showAlert){
        String regex = "^(?=.{0,128}$)[\\w-]+(?:\\.[\\w-]+)*@(?:[a-zA-Z0-9-]+\\.)+[a-zA-Z]{2,6}$";
        /*
        (?=.{0,128}$)           ->   maximum 128 characters
        [\\w-]+                 ->   a-zA-Z0-9_- allowed
        ?:\.[\w-]+)*            ->   a-zA-Z0-9_-. allowed multiple times
        @                       ->   @ (only one time)
        (?:[a-zA-Z0-9-]+\.)+    ->   (a-zA-Z0-9- multiple times plus .) multiple times
        [a-zA-Z]{2,6}           ->   letters allowed between 2 and 6 times
        */

        Pattern pattern = Pattern.compile(regex);
        Matcher matcher = pattern.matcher(email);

        if (matcher.find()){
            return true;
        }
        else {
            if (showAlert){
                Alert alert = new Alert(Alert.AlertType.WARNING);
                alert.setTitle("Validation error");
                alert.setHeaderText(null);
                alert.setContentText("Enter right E-Mail address");

                alert.showAndWait();
            }

            return false;
        }
    }

    /**
     * Checks the password
     * @param password Entered password
     * @param showAlert If the alert should be displayed
     * @return Returns result
     */
    public boolean passwordValidation(String password, boolean showAlert){
        String regex = "^(?=.*[0-9])(?=.*[a-z])(?=.*[A-Z])(?=.*[@#$%^&+=])(?=^\\S+$).{8,32}$";
        /*
        (?=.*[0-9])         ->   at least one digit
        (?=.*[a-z])         ->   at least one lower case letter
        (?=.*[A-Z])         ->   at least one upper case letter
        (?=.*[@#$%^&+=])    ->   at least one special character
        (?=^\S+$)           ->   no whitespace allowed
        .{8,32}             ->   between 8 and 32 characters (any character)
        */

        Pattern pattern = Pattern.compile(regex);
        Matcher matcher = pattern.matcher(password);

        if (matcher.find()){
            return true;
        }
        else {
            if (showAlert){
                Alert alert = new Alert(Alert.AlertType.WARNING);
                alert.setTitle("Validation error");
                alert.setHeaderText("Enter right Password");
                alert.setContentText("The password can only consist of the following characters:\n- At least one digit\t\t\t\t0-9\n- At least one lower case letter\ta-z\n- At least one upper case letter\tA-Z\n- At least one special character\t@#$%^&+=\n- Between 8 and 32 characters");

                alert.showAndWait();
            }
            return false;
        }
    }
}
