package com.project.timescheduler.controllers;

import com.project.timescheduler.Main;
import com.project.timescheduler.services.Event;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.DatePicker;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

import java.time.LocalDateTime;
import java.time.LocalTime;


public class HostEventInformationController{

    @FXML
    private TextField tfEventName;
    @FXML
    private DatePicker tfStartDate;
    @FXML
    private DatePicker tfEndDate;
    @FXML
    private TextField tfStartHour;
    @FXML
    private TextField tfStartMin;
    @FXML
    private TextField tfEndHour;
    @FXML
    private TextField tfEndMin;
    @FXML
    private TextField tfLocation;
    @FXML
    private ChoiceBox<Event.Priority> cbPriority;
    @FXML
    private Button bSave;

    private String currentUser;
    private Event event;

    @FXML
    public void initialize(Event event, String currentUser){
        this.currentUser = currentUser;
        this.event = event;
        cbPriority.getItems().addAll(Event.Priority.values());
        loadData();
    }
    public void loadData(){
        tfEventName.setText(event.getName());
        tfStartDate.setValue(event.getStartDate());
        tfEndDate.setValue(event.getEndDate());
        tfStartHour.setText(String.valueOf(event.getStartTime().getHour()));
        tfStartMin.setText(String.valueOf(event.getStartTime().getMinute()));
        tfEndHour.setText(String.valueOf(event.getEndTime().getHour()));
        tfEndMin.setText(String.valueOf(event.getEndTime().getMinute()));
        tfLocation.setText(event.getLocation());
        cbPriority.setValue(event.getPriority());
        System.out.println(event.getStartTime());
    }
    public void updateData(){
        LocalTime startTime = LocalTime.now();
        LocalTime endTime = LocalTime.now();
        try {
            startTime = LocalTime.of(Integer.parseInt(tfStartHour.getText()), Integer.parseInt(tfStartMin.getText()));
            endTime = LocalTime.of(Integer.parseInt(tfEndHour.getText()), Integer.parseInt(tfEndMin.getText()));

        } catch (NumberFormatException e) {
            e.printStackTrace();
        }

        Event updatedEvent = new Event(
                event.getEventId(),
                event.getCreatorName(),
                tfEventName.getText(),
                tfLocation.getText(),
                null,
                tfStartDate.getValue(),
                tfEndDate.getValue(),
                startTime,
                endTime,
                cbPriority.getValue()
                );

        String startDateTime_temp = "%s %s";
        String startDateTime = String.format(startDateTime_temp, updatedEvent.getStartDate(), updatedEvent.getStartTime());
        String endDateTime = String.format(startDateTime_temp, updatedEvent.getEndDate(), updatedEvent.getEndTime());

        String sql = "UPDATE SCHED_EVENT\n" +
                "SET EVENT_NAME = '"+ updatedEvent.getName() +
                "', START_DATE = '"+ startDateTime +
                "', END_DATE = '"+ endDateTime +
                "', LOCATION = '"+  updatedEvent.getLocation() +
                "', PRIORITY = '"+ updatedEvent.getPriority().toString() +"'\n" +
                "WHERE EVENT_ID = '"+ event.getEventId() + "'";
        String alter_date_format = "ALTER SESSION SET NLS_DATE_FORMAT = 'YYYY-MM-DD HH24:MI'";
        Main.connection.update(alter_date_format);
        Main.connection.update(sql);
        Stage stage = (Stage) bSave.getScene().getWindow();
        stage.close();
    }
}