package com.project.timescheduler;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.Statement;

public class AdminController {

    @FXML
    private TableView<UserDetails> tableview;
    @FXML
    private TableColumn<UserDetails, String> col_username;
    @FXML
    private TableColumn<UserDetails, String> col_email;
    @FXML
    private TableColumn<UserDetails, String> col_password;

    private Connection connection = null;

    @FXML
    void initialize() {
        col_username.setCellValueFactory(new PropertyValueFactory<>("Username"));
        col_email.setCellValueFactory(new PropertyValueFactory<>("Email"));
        col_password.setCellValueFactory(new PropertyValueFactory<>("Password"));

        DatabaseConnection databaseConnection = new DatabaseConnection();
        try {
            connection = databaseConnection.getConnection();
            loadData();
        }
        catch (Exception e){
            e.printStackTrace();
        }
    }

    public void loadData(){
        ObservableList<UserDetails> data = FXCollections.observableArrayList();
        try {
            String sql = "SELECT * FROM sched_user";
            Statement statement = connection.createStatement();
            ResultSet rs = statement.executeQuery(sql);
            while (rs.next()){
                UserDetails userDetails = new UserDetails(rs.getString("username"), rs.getString("email"), rs.getString("password"));
                data.add(userDetails);
            }
            tableview.setItems(data);
        }
        catch (Exception e){
            e.printStackTrace();
        }
    }

    public void deleteUser(){
        try {
            String selectedItem = tableview.getSelectionModel().getSelectedItem().getUsername();

            String sql_temp = "DELETE FROM sched_user WHERE username='%s'";
            String sql = String.format(sql_temp, selectedItem);

            Statement statement = connection.createStatement();
            statement.executeUpdate(sql);
            loadData();
        }
        catch (Exception e){
            e.printStackTrace();
        }
    }

    public void editUser() {
    }
}
