package com.project.timescheduler.controllers;

import com.project.timescheduler.services.DatabaseConnection;
import com.project.timescheduler.services.Encryption;
import com.project.timescheduler.services.UserDetails;
import com.project.timescheduler.services.Validation;
import javafx.beans.binding.Bindings;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;

import java.sql.ResultSet;
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
    private Label editedLabel;
    @FXML
    private Button editButton, deleteButton;

    private DatabaseConnection connection;

    @FXML
    void initialize() {
        editButton.disableProperty().bind(Bindings.isEmpty(tableview.getSelectionModel().getSelectedItems()).or(emailEdit.textProperty().isEmpty().and(passwordEdit.textProperty().isEmpty())));
        deleteButton.disableProperty().bind(Bindings.isEmpty(tableview.getSelectionModel().getSelectedItems()));

        col_username.setCellValueFactory(new PropertyValueFactory<>("Username"));
        col_firstname.setCellValueFactory(new PropertyValueFactory<>("Firstname"));
        col_lastname.setCellValueFactory(new PropertyValueFactory<>("Lastname"));
        col_email.setCellValueFactory(new PropertyValueFactory<>("Email"));
        col_password.setCellValueFactory(new PropertyValueFactory<>("Password"));

        connection = new DatabaseConnection();

        try {
            loadData();
        } catch (SQLException e){
            e.printStackTrace();
        }

    }

    public void loadData() throws SQLException{
        ObservableList<UserDetails> data = FXCollections.observableArrayList();

        String sql = "SELECT * FROM sched_user";
        ResultSet rs = connection.query(sql);

        while (rs.next()){
            UserDetails userDetails = new UserDetails(rs.getString("username"),
                    rs.getString("firstname"),
                    rs.getString("lastname"),
                    rs.getString("email"),
                    rs.getString("password")
            );
            data.add(userDetails);
        }
        tableview.setItems(data);
    }

    public void deleteUser() throws SQLException{
        String selectedItem = tableview.getSelectionModel().getSelectedItem().getUsername();

        String sql_temp = "DELETE FROM sched_user WHERE username='%s'";
        String sql = String.format(sql_temp, selectedItem);

        connection.update(sql);
        loadData();
    }

    public void editUser() throws SQLException{
        Validation validation = new Validation();
        Encryption encryption = new Encryption();

        int i = 0;

        if (passwordEdit.getText().isEmpty()) {
            if (validation.emailValidation(emailEdit.getText())){
                i = 1;
            }
        }
        else if (emailEdit.getText().isEmpty()){
            if (validation.passwordValidation(passwordEdit.getText())){
                i = 2;
            }
        }
        else {
            if (validation.emailValidation(emailEdit.getText()) && validation.passwordValidation(passwordEdit.getText())) {
                i = 3;
            }
        }

        String selectedItem = tableview.getSelectionModel().getSelectedItem().getUsername();
        String sql_temp, sql;

        switch (i) {
            case 1 -> {
                sql_temp = "UPDATE sched_user SET email='%s' WHERE username='%s'";
                sql = String.format(sql_temp, emailEdit.getText(), selectedItem);
                connection.update(sql);
                loadData();
                editedLabel.setText("Email changed");
            }
            case 2 -> {
                sql_temp = "UPDATE sched_user SET password='%s' WHERE username='%s'";
                sql = String.format(sql_temp, encryption.createHash(passwordEdit.getText()), selectedItem);
                connection.update(sql);
                loadData();
                editedLabel.setText("Password changed");
            }
            case 3 -> {
                sql_temp = "UPDATE sched_user SET email='%s', password='%s' WHERE username='%s'";
                sql = String.format(sql_temp, emailEdit.getText(), encryption.createHash(passwordEdit.getText()), selectedItem);
                connection.update(sql);
                loadData();
                editedLabel.setText("Email and Password changed");
            }
        }

        emailEdit.clear();
        passwordEdit.clear();
    }
}
