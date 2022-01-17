package com.project.timescheduler;

import com.project.timescheduler.Event;
import com.project.timescheduler.Priority;
import javafx.event.ActionEvent;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.input.MouseEvent;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.DatePicker;
import javafx.scene.control.TextField;
import javafx.stage.Stage;
import org.w3c.dom.Text;

import java.io.IOException;
import java.net.URL;
import java.sql.Connection;
import java.sql.Statement;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.LinkedList;
import java.util.ResourceBundle;

public class MenuController implements Initializable {

    @FXML
    private TextField eName;
    @FXML
    private ListView<String> eList;
    @FXML
    private ListView<String> pList;
    @FXML
    private ListView<String> spList;
    @FXML
    private TextField eLocation;
    @FXML
    private DatePicker eStartDate;
    @FXML
    private DatePicker eEndDate;
    @FXML
    private TextField eStartHour;
    @FXML
    private TextField eStartMin;
    @FXML
    private TextField eEndHour;
    @FXML
    private TextField eEndMin;
    @FXML
    private ChoiceBox<Priority> ePriority;
    private final Priority[] prio = {Priority.high,Priority.medium,Priority.low};

    String[] names = {"Mickey Lynn", "test test", "Cebi Stüller"}; //Load names here

    int i = -1;
    LinkedList participantsList = new LinkedList();

    private ObservableList<String> list = FXCollections.observableArrayList(names);

    public void initialize(URL arg0, ResourceBundle arg1){
        ePriority.getItems().addAll(prio);
        pList.setItems(list);
        pList.getSelectionModel().setSelectionMode(SelectionMode.MULTIPLE);
    }
    @FXML
    private void addNameSelection(MouseEvent mouseEvent){
        if(pList.getSelectionModel().getSelectedItem() == null){
            return;
        }else {
            eList.getItems().add(pList.getSelectionModel().getSelectedItem());
            spList.getItems().add(pList.getSelectionModel().getSelectedItem());
            participantsList.add(pList.getSelectionModel().getSelectedItem());
            i++;
            pList.getItems().remove(pList.getSelectionModel().getSelectedItem());
        }
    }
    @FXML
    private void removeNameSelection(MouseEvent mouseEvent){
        if(spList.getSelectionModel().getSelectedItem() == null){
            return;
        }else {
            pList.getItems().add(spList.getSelectionModel().getSelectedItem());
            for(int x = i; x >= 0; x--){
                if (spList.getSelectionModel().getSelectedItem() == participantsList.get(x)){
                    participantsList.remove(x);
                    i--;
                    break;
                }
            }
            eList.getItems().remove(spList.getSelectionModel().getSelectedItem());
            spList.getItems().remove(spList.getSelectionModel().getSelectedItem());
        }
    }


    //enter method on save button press
    public void createEvent() throws IOException{

        String name = eName.getText();
        String location = eLocation.getText();
        String participants = "";
        for(int y = i; y >= 0; y--){
            participants = participants + participantsList.get(y) + ",";
        }

        LocalDate startDate = eStartDate.getValue();
        LocalDate endDate = eEndDate.getValue();
        LocalTime startTime = LocalTime.now();
        LocalTime endTime = LocalTime.now();
        try {
            startTime = LocalTime.of(Integer.parseInt(eStartHour.getText()), Integer.parseInt(eStartMin.getText()));
            endTime = LocalTime.of(Integer.parseInt(eEndHour.getText()), Integer.parseInt(eEndMin.getText()));

        } catch (NumberFormatException nfe) {
            nfe.printStackTrace();
        }
        System.out.println(startTime);
        System.out.println(endTime);
        //System.out.println(startDate + " " + endDate);
        Priority priority = ePriority.getValue();
        Event event = new Event(name,location,participants,startDate,priority);
        event.printEvent();
        uploadEvent(name, startDate, endDate, startTime, endTime, location, priority);
    }

    public void uploadEvent(String name, LocalDate startDate, LocalDate endDate, LocalTime startTime, LocalTime endTime, String location, Priority priority) {
        String startDateTime_temp = "%s %s";
        String startDateTime = String.format(startDateTime_temp, startDate, startTime);
        String endDateTime = String.format(startDateTime_temp, endDate, endTime);
        //System.out.println(startDateTime);

        String sql_temp = "INSERT INTO sched_event (event_name, start_date, end_date, creator_name, location, priority) VALUES ('%s', '%s', '%s', '%s', '%s', '%s')";
        String sql = String.format(sql_temp, name, startDateTime, endDateTime, "test", location, priority);
        System.out.println(sql);

        String alter_date_format = "ALTER SESSION SET NLS_DATE_FORMAT = 'YYYY-MM-DD HH:MI'";

        try {
            DatabaseConnection databaseConnection = new DatabaseConnection();
            Connection connection = databaseConnection.getConnection();

            Statement statement = connection.createStatement();
            statement.executeUpdate(alter_date_format);
            statement.executeUpdate(sql);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
