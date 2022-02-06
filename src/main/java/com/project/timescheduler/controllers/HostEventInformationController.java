package com.project.timescheduler.controllers;

import com.project.timescheduler.Main;
import com.project.timescheduler.services.Event;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.stage.Stage;

import java.time.LocalDateTime;
import java.time.LocalTime;


public class HostEventInformationController{

    @FXML
    private Label tfEventName;
    @FXML
    private Label tfCreatorName;
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
    private ListView<String> allParticipantsList;
    @FXML
    private ListView<String> selectedParticipantsList;
    @FXML
    private Button bSave;
    @FXML
    private ChoiceBox<String> cbReminder;

    private String currentUser;
    private Event event;

    @FXML
    public void initialize(Event event, String currentUser){
        this.currentUser = currentUser;
        this.event = event;
        cbPriority.getItems().addAll(Event.Priority.values());
        cbReminder.getItems().addAll("1 week", "3 days", "1 hour", "10 minutes");
        loadData();
    }
    public void loadData(){
        tfEventName.setText(event.getName());
        tfCreatorName.setText(event.getCreatorName() + " (You)");
        tfStartDate.setValue(event.getStartDate());
        tfEndDate.setValue(event.getEndDate());
        tfStartHour.setText(String.valueOf(event.getStartTime().getHour()));
        tfStartMin.setText(String.valueOf(event.getStartTime().getMinute()));
        tfEndHour.setText(String.valueOf(event.getEndTime().getHour()));
        tfEndMin.setText(String.valueOf(event.getEndTime().getMinute()));
        tfLocation.setText(event.getLocation());
        cbPriority.setValue(event.getPriority());
        cbReminder.setValue(event.getReminderString());
        //TO DO: gesetzten reminder anzeigen lassen
        System.out.println(event.getStartTime());
    }

    public long retrieveReminderHost() {
        String reminder = cbReminder.getValue();
        long remindertime = -1;

        switch (reminder) {
            case "1 week":
                remindertime = 7 * 24 * 60 * 60 * 1000;
                break;
            case "3 days":
                remindertime = 3 * 24 * 60 * 60 * 1000;
                break;
            case "1 hour":
                remindertime = 60 * 60 * 1000;
                break;
            case "10 minutes":
                remindertime = 10 * 60 * 1000;
                break;
            default:
                remindertime = -1;
                break;
        }
        return remindertime;
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
                event.getName(),
                tfLocation.getText(),
                null,
                tfStartDate.getValue(),
                tfEndDate.getValue(),
                startTime,
                endTime,
                cbPriority.getValue(),
                retrieveReminderHost()
                );

        String startDateTime_temp = "%s %s";
        String startDateTime = String.format(startDateTime_temp, updatedEvent.getStartDate(), updatedEvent.getStartTime());
        String endDateTime = String.format(startDateTime_temp, updatedEvent.getEndDate(), updatedEvent.getEndTime());

        String sql = "UPDATE SCHED_EVENT\n" +
                "SET EVENT_NAME = '"+ updatedEvent.getName() +
                "', START_DATE = '"+ startDateTime +
                "', END_DATE = '"+ endDateTime +
                "', LOCATION = '"+  updatedEvent.getLocation() +
                "', PRIORITY = '"+ updatedEvent.getPriority().toString() +
                "', REMINDER = '"+ Long.toString(updatedEvent.getReminder()) +"'\n" +
                "WHERE EVENT_ID = '"+ event.getEventId() + "'";
        String alter_date_format = "ALTER SESSION SET NLS_DATE_FORMAT = 'YYYY-MM-DD HH24:MI'";
        Main.connection.update(alter_date_format);
        Main.connection.update(sql);
        Stage stage = (Stage) bSave.getScene().getWindow();
        stage.close();
    }
}