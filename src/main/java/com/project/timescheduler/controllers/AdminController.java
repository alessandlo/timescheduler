package com.project.timescheduler.controllers;

import com.project.timescheduler.services.DatabaseConnection;
import com.project.timescheduler.services.UserDetails;
import com.project.timescheduler.services.Validation;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.PropertyValueFactory;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

public class AdminController {
    //testtetsasdfff
    @FXML
    private TableView<UserDetails> tableview;
    @FXML
    private TableColumn<UserDetails, String> col_username;
    @FXML
    private TableColumn<UserDetails, String> col_email;
    @FXML
    private TableColumn<UserDetails, String> col_password;
    @FXML
    private TextField emailEdit;
    @FXML
    private PasswordField passwordEdit;

    private DatabaseConnection connection;

    @FXML
    void initialize() {
        col_username.setCellValueFactory(new PropertyValueFactory<>("Username"));
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

        if (validation.emailValidation(emailEdit.getText()) &&
                validation.passwordValidation(passwordEdit.getText())){
            String selectedItem = tableview.getSelectionModel().getSelectedItem().getUsername();

            String sql_temp = "UPDATE sched_user SET Email='%s', Password='%s' WHERE Username='%s'";
            String sql = String.format(sql_temp, emailEdit.getText(), passwordEdit.getText(), selectedItem);

            connection.update(sql);
            loadData();
        }
    }
}
