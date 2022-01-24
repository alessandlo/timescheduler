package com.project.timescheduler.controllers;

import com.project.timescheduler.Main;
import com.project.timescheduler.services.DatabaseConnection;
import com.project.timescheduler.services.Event;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.DatePicker;
import javafx.scene.control.TextField;
import javafx.scene.input.MouseEvent;
import javafx.stage.Modality;
import javafx.stage.Stage;

import java.io.IOException;
import java.sql.SQLException;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Objects;

public class EventMenuController{
    @FXML
    private TextField eventName;
    @FXML
    private ListView<String> eventParticipantList;
    @FXML
    private TextField eventLocation;
    @FXML
    private DatePicker eventStartDate;
    @FXML
    private DatePicker eventEndDate;
    @FXML
    private TextField eventStartHour;
    @FXML
    private TextField eventStartMin;
    @FXML
    private TextField eventEndHour;
    @FXML
    private TextField eventEndMin;
    @FXML
    private ChoiceBox<Event.Priority> eventPriority;
    @FXML
    private Button exitButton;

    private Event event;

    private ArrayList<String> participants;

    private OnActionListener listener;

    public interface OnActionListener {
        void onAction();

    }

    @FXML
    public void initialize(OnActionListener listener){
        this.listener = listener;
        participants = new ArrayList<>();

        eventPriority.getItems().addAll(Event.Priority.values());
        eventParticipantList.getItems().add("Manage participants");

    }

    public void manageParticipants(MouseEvent mouseEvent) throws IOException {
        if(Objects.equals(eventParticipantList.getSelectionModel().getSelectedItem(), "Manage participants")){
            FXMLLoader loader = new FXMLLoader(Main.class.getResource("participantsList.fxml"));
            Parent root = loader.load();
            ParticipantsListController controller = loader.getController();

            Stage participantsStage = new Stage();

            controller.initialize((list) -> {
                eventParticipantList.getItems().clear();
                eventParticipantList.getItems().add(0, "Manage participants");
                for (String participant: list) {
                    eventParticipantList.getItems().add(participant);
                }
                participantsStage.close();
            });
            loader.setController(controller);

            Scene scene = new Scene(root);
            participantsStage.setScene(scene);

            participantsStage.initModality(Modality.APPLICATION_MODAL);
            participantsStage.showAndWait();
        }
    }

    //enter method on save button press
    public void createEvent(){
        LocalTime startTime = LocalTime.now();
        LocalTime endTime = LocalTime.now();
        try {
            startTime = LocalTime.of(Integer.parseInt(eventStartHour.getText()), Integer.parseInt(eventStartMin.getText()));
            endTime = LocalTime.of(Integer.parseInt(eventEndHour.getText()), Integer.parseInt(eventEndMin.getText()));

        } catch (NumberFormatException e) {
            e.printStackTrace();
        }

        event = new Event(
                eventName.getText(),
                eventLocation.getText(),
                participants,
                eventStartDate.getValue(),
                eventEndDate.getValue(),
                startTime,
                endTime,
                eventPriority.getValue()
        );

        try {
            uploadEvent();
        } catch (SQLException e){
            e.printStackTrace();
        }
    }

    public void uploadEvent()  throws SQLException {
        String startDateTime_temp = "%s %s";
        String startDateTime = String.format(startDateTime_temp, event.getStartDate(), event.getStartTime());
        String endDateTime = String.format(startDateTime_temp, event.getEndDate(), event.getEndTime());

        String sql_temp = "INSERT INTO sched_event (event_name, start_date, end_date, creator_name, location, priority) VALUES ('%s', '%s', '%s', '%s', '%s', '%s')";
        String sql = String.format(sql_temp, event.getName(), startDateTime, endDateTime, event.getCreator(), event.getLocation(), event.getPriority());

        String alter_date_format = "ALTER SESSION SET NLS_DATE_FORMAT = 'YYYY-MM-DD HH24:MI'";

        DatabaseConnection connection = new DatabaseConnection();
        connection.update(alter_date_format);
        connection.update(sql);

    }

    public void exitMenu(){
        listener.onAction();
    }
}
