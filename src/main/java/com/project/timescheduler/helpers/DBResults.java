package com.project.timescheduler.helpers;

import java.sql.Date;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Time;
import java.util.ArrayList;

/**
 * This class is used to handle the try and catch
 * blocks of database queries.
 */
public class DBResults {
    ResultSet resultSet;

    public DBResults(ResultSet resultSet){
        this.resultSet = resultSet;
    }

    /**
     * Checks if entry is present in database
     * @return boolean
     */
    public boolean next(){
        try {
            return resultSet.next();
        }catch (SQLException e){
            e.printStackTrace();
        }
        return false;
    }

    /**
     * Loads string from database
     * @return string
     */
    public String get(String s){
        try {
            return resultSet.getString(s);
        }catch (SQLException e){
            e.printStackTrace();
        }
        return null;
    }

    /**
     * Loads stringList from database
     * @return arraylist
     */
    public ArrayList<String> get(String... args){
        ArrayList<String> arrayList = new ArrayList<>();
        try {
            for (String s : args) {
                arrayList.add(resultSet.getString(s));
            }
        }catch (SQLException e){
            e.printStackTrace();
        }
        return arrayList;
    }

    /**
     * Loads date from database
     * @return date
     */
    public Date getDate(String s) {
        try {
            return resultSet.getDate(s);
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * Loads time from database
     * @return time
     */
    public Time getTime(String s) {
        try {
            return resultSet.getTime(s);
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }
}
