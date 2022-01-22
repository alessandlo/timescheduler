package com.project.timescheduler.services;

import javafx.scene.control.Alert;

import java.sql.ResultSet;
import java.sql.Connection;
import java.sql.SQLException;
import java.sql.DriverManager;
import java.sql.Statement;

public class DatabaseConnection {

    private Connection connection;

    public static String adminUserName = "admin";
    public static String adminPassword = "admin";


    public Connection getConnection(){
        //test comment tom
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

    public ResultSet query(String sql) throws SQLException {
        if (connection == null){
            getConnection();
        }
        Statement statement = connection.createStatement();
        return statement.executeQuery(sql);
    }

    public void update(String sql) throws SQLException{
        if (connection == null){
            getConnection();
        }
        Statement statement = connection.createStatement();
        statement.executeUpdate(sql);
    }
}
