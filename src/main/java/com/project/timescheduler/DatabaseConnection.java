package com.project.timescheduler;

import javafx.scene.control.Alert;

import java.sql.Connection;
import java.sql.DriverManager;

public class DatabaseConnection {

    public Connection connection;

    public Connection getConnection(){
        final String databaseUser = "S1_student2_19";
        final String databasePassword = "Zx08DBS";
        final String url = "jdbc:oracle:thin:@db1.fb2.frankfurt-university.de:1521:info01";


        try{
            Class.forName("oracle.jdbc.driver.OracleDriver");
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
}
