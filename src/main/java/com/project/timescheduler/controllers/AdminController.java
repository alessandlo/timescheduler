package com.project.timescheduler.controllers;

import com.project.timescheduler.helpers.DBResults;
import com.project.timescheduler.services.*;
import javafx.beans.binding.Bindings;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.input.MouseEvent;

import java.sql.SQLException;

public class AdminController {

    @FXML
    private TableView<UserDetails> tableview;
    @FXML
    private TableColumn<UserDetails, String> col_username;
    @FXML
    private TableColumn<UserDetails, String> col_firstname;
    @FXML
    private TableColumn<UserDetails, String> col_lastname;
    @FXML
    private TableColumn<UserDetails, String> col_email;
    @FXML
    private TableColumn<UserDetails, String> col_password;
    @FXML
    private TextField emailEdit;
    @FXML
    private PasswordField passwordEdit;
    @FXML
    private Label feedback, selectedUser;
    @FXML
    private Button editButton, deleteButton;

    private DatabaseConnection connection;

    @FXML
    void initialize() {
        editButton.disableProperty().bind(Bindings.isEmpty(tableview.getSelectionModel().getSelectedItems()).or(
                emailEdit.textProperty().isEmpty().and(passwordEdit.textProperty().isEmpty())));
        deleteButton.disableProperty().bind(Bindings.isEmpty(tableview.getSelectionModel().getSelectedItems()));

        col_username.setCellValueFactory(new PropertyValueFactory<>("Username"));
        col_firstname.setCellValueFactory(new PropertyValueFactory<>("Firstname"));
        col_lastname.setCellValueFactory(new PropertyValueFactory<>("Lastname"));
        col_email.setCellValueFactory(new PropertyValueFactory<>("Email"));
        col_password.setCellValueFactory(new PropertyValueFactory<>("Password"));

        connection = new DatabaseConnection();

        loadData();
    }

    @FXML
    private void showUser(MouseEvent mouseEvent){
        try {
            selectedUser.setText(tableview.getSelectionModel().getSelectedItem().getUsername());
        }
        catch (Exception e){
            selectedUser.setText("No user selected");
        }
    }

    public void loadData(){
        ObservableList<UserDetails> data = FXCollections.observableArrayList();
        String sql = "SELECT * FROM sched_user";
        DBResults rs = connection.query(sql);

        while (rs.next()){
            UserDetails userDetails = new UserDetails(
                    rs.get("username"),
                    rs.get("firstname"),
                    rs.get("lastname"),
                    rs.get("email"),
                    rs.get("password")
            );
            data.add(userDetails);
        }
        tableview.setItems(data);
    }

    public void deleteUser(){
        String selectedItem = tableview.getSelectionModel().getSelectedItem().getUsername();

        String sql_temp = "DELETE FROM sched_user WHERE username='%s'";
        String sql = String.format(sql_temp, selectedItem);

        connection.update(sql);

        feedback.setText("User deleted");

        loadData();
    }

    public void editUser(){
        Validation validation = new Validation();
        Encryption encryption = new Encryption();

        String selectedItem = tableview.getSelectionModel().getSelectedItem().getUsername();
        String sql_temp, sql;

        if (passwordEdit.getText().isEmpty()) {
            if (validation.emailValidation(emailEdit.getText())){
                sql_temp = "UPDATE sched_user SET email='%s' WHERE username='%s'";
                sql = String.format(sql_temp, emailEdit.getText(), selectedItem);
                connection.update(sql);
                feedback.setText("Email changed");
            }
        }
        else if (emailEdit.getText().isEmpty()){
            if (validation.passwordValidation(passwordEdit.getText())){
                sql_temp = "UPDATE sched_user SET password='%s' WHERE username='%s'";
                sql = String.format(sql_temp, encryption.createHash(passwordEdit.getText()), selectedItem);
                connection.update(sql);
                feedback.setText("Password changed");
            }
        }
        else {
            if (validation.emailValidation(emailEdit.getText()) &&
                    validation.passwordValidation(passwordEdit.getText())) {
                sql_temp = "UPDATE sched_user SET email='%s', password='%s' WHERE username='%s'";
                sql = String.format(sql_temp, emailEdit.getText(), encryption.createHash(passwordEdit.getText()), selectedItem);
                connection.update(sql);
                feedback.setText("Email and Password changed");
            }
        }
        emailEdit.clear();
        passwordEdit.clear();

        loadData();
    }
}
