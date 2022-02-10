package com.project.timescheduler.services;

import com.project.timescheduler.Main;
import com.project.timescheduler.helpers.DBResults;
import javafx.scene.control.Alert;

import java.sql.Connection;
import java.sql.SQLException;
import java.sql.DriverManager;
import java.sql.Statement;

/** Class which is used for Database Interactions/Operations */
public class DatabaseConnection {

    private Connection connection;

    public static String adminUserName = "admin";
    public static String adminPassword = "admin";

    /** constructs a DatabaseConnection object, calls getConnnection() and alters the session nls_date_format to 'YYYY-MM-DD HH24:MI' */
    public DatabaseConnection(){
        getConnection();
        update("ALTER SESSION SET NLS_DATE_FORMAT = 'YYYY-MM-DD HH24:MI'");
    }

    /** returns a Connection Object linked to our Oracle database and sets the private connection attribute of this Class to the same Connection object
     * @return a Connection Object linked to our Oracle database */
    public Connection getConnection() {
        final String databaseUser = "S1_student2_19";
        final String databasePassword = "Zx08DBS";
        final String url = "jdbc:oracle:thin:@db1.fb2.frankfurt-university.de:1521:info01";

        try{
            connection = DriverManager.getConnection(url, databaseUser, databasePassword);
        }
        catch (SQLException e){
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Database error");
            alert.setHeaderText("No Connection");
            alert.setContentText("False Database credentials or Database offline");

            alert.showAndWait();

            Main.connection.close();
            Main.connection = new DatabaseConnection();
        }
        return connection;
    }


    public DBResults query(String sql) {
        DBResults resultSet = null;
        try {
            Statement statement = connection.createStatement();
            resultSet = new DBResults(statement.executeQuery(sql));
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return resultSet;
    }

    /** executes an update sql statement
     * @param sql String variable representing the sql update statement to be executed */
    public void update(String sql){
        try{
            Statement statement = connection.createStatement();
            statement.executeUpdate(sql);
        }catch (SQLException e){
            e.printStackTrace();
        }
    }
    /** closes the connection of this DatabaseConnection object */
    public void close(){
        try {
            connection.close();
        }catch (SQLException e){
            e.printStackTrace();
        }
    }
}
