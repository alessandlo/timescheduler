package com.project.timescheduler.services;

import com.project.timescheduler.helpers.DBResults;
import javafx.scene.control.Alert;

import java.sql.Connection;
import java.sql.SQLException;
import java.sql.DriverManager;
import java.sql.Statement;

public class DatabaseConnection {

    private Connection connection;

    public static String adminUserName = "admin";
    public static String adminPassword = "admin";


    private Connection getConnection(){

        final String databaseUser = "S1_student2_19";
        final String databasePassword = "Zx08DBS";
        final String url = "jdbc:oracle:thin:@db1.fb2.frankfurt-university.de:1521:info01";

        try{
            connection = DriverManager.getConnection(url, databaseUser, databasePassword);
        }
        catch (Exception e){
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Database error");
            alert.setHeaderText("No Connection");
            alert.setContentText("False Database credentials or Database offline");

            alert.showAndWait();
        }

        return connection;
    }

    public DBResults query(String sql) {
        if (connection == null) {
            getConnection();
        }

        DBResults resultSet = null;
        try {
            Statement statement = connection.createStatement();
            resultSet = new DBResults(statement.executeQuery(sql));
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return resultSet;
    }

    public void update(String sql){
        if (connection == null){
            getConnection();
        }

        try{
            Statement statement = connection.createStatement();
            statement.executeUpdate(sql);
        }catch (SQLException e){
            e.printStackTrace();
        }
    }


}
