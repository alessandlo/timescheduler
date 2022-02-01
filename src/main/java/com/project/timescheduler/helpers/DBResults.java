package com.project.timescheduler.helpers;

import java.sql.Date;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Time;
import java.util.ArrayList;

public class DBResults {
    ResultSet resultSet;

    public DBResults(ResultSet resultSet){
        this.resultSet = resultSet;
    }

    public boolean next(){
        try {
            return resultSet.next();
        }catch (SQLException e){
            e.printStackTrace();
        }
        return false;
    }

    public String get(String s){
        try {
            return resultSet.getString(s);
        }catch (SQLException e){
            e.printStackTrace();
        }
        return null;
    }

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

    public Date getDate(String s) {
        try {
            return resultSet.getDate(s);
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    public Time getTime(String s) {
        try {
            return resultSet.getTime(s);
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }
}
