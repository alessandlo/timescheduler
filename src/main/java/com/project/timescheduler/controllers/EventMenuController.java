package com.project.timescheduler.controllers;

import com.project.timescheduler.Main;
import com.project.timescheduler.helpers.DBResults;
import com.project.timescheduler.services.DatabaseConnection;
import com.project.timescheduler.services.Event;
import com.project.timescheduler.services.Mail;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.DatePicker;
import javafx.scene.control.TextField;
import javafx.scene.input.MouseEvent;
import javafx.stage.FileChooser;
import javafx.stage.Modality;
import javafx.stage.Stage;

import java.io.File;
import java.io.IOException;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.ArrayList;
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

    private String currentUser;

    private String attachmentPath;

    private DatabaseConnection connection;

    @FXML
    public void initialize(OnActionListener listener, String currentUser){
        this.listener = listener;
        participants = new ArrayList<>();
        this.currentUser = currentUser;
        connection = new DatabaseConnection();

        eventPriority.getItems().addAll(Event.Priority.values());
        eventParticipantList.getItems().add("Manage participants");
    }

    public void resetBorderColorSD() {
        System.out.println("reset border color");
        eventStartDate.setStyle("-fx-border-color: transparent");
    }

    public void resetBorderColorED() {
        System.out.println("reset border color");
        eventEndDate.setStyle("-fx-border-color: transparent");
    }

    public void manageParticipants(MouseEvent mouseEvent) throws IOException {
        if(Objects.equals(eventParticipantList.getSelectionModel().getSelectedItem(), "Manage participants")){
            FXMLLoader loader = new FXMLLoader(Main.class.getResource("participantsList.fxml"));
            Parent root = loader.load();
            ParticipantsListController controller = loader.getController();

            Stage participantsStage = new Stage();

            ArrayList<String> participants = new ArrayList<>(eventParticipantList.getItems());
            participants.remove(0);

            controller.initialize((list) -> {
                eventParticipantList.getItems().clear();
                eventParticipantList.getItems().add(0, "Manage participants");
                for (String participant: list) {
                    eventParticipantList.getItems().add(participant);
                }
                participantsStage.close();
            }, participants);
            loader.setController(controller);

            Scene scene = new Scene(root);
            participantsStage.setScene(scene);
            participantsStage.initModality(Modality.APPLICATION_MODAL);
            participantsStage.showAndWait();
        }
    }

    //enter method on save button press
    public void createEvent() throws Exception {
        LocalTime startTime = LocalTime.now();
        LocalTime endTime = LocalTime.now();
        try {
            startTime = LocalTime.of(Integer.parseInt(eventStartHour.getText()), Integer.parseInt(eventStartMin.getText()));
            endTime = LocalTime.of(Integer.parseInt(eventEndHour.getText()), Integer.parseInt(eventEndMin.getText()));

        } catch (NumberFormatException e) {
            e.printStackTrace();
        }

        LocalDateTime startDateTime = eventStartDate.getValue().atTime(startTime);
        LocalDateTime endDateTime = eventEndDate.getValue().atTime(endTime);

        if(startDateTime.isAfter(LocalDateTime.now()) && endDateTime.isAfter(startDateTime)) {

            participants.addAll(eventParticipantList.getItems());
            participants.remove(0);

            event = new Event(
                    -1,
                    currentUser,
                    eventName.getText(),
                    eventLocation.getText(),
                    participants,
                    eventStartDate.getValue(),
                    eventEndDate.getValue(),
                    startTime,
                    endTime,
                    eventPriority.getValue()
            );

            uploadEvent();

            String getEvent_sql = "SELECT EVENT_ID FROM SCHED_EVENT where EVENT_NAME = '%s' AND LOCATION = '%s' AND PRIORITY = '%s'";
            getEvent_sql = String.format(getEvent_sql, event.getName(), event.getLocation(), event.getPriority());
            DBResults rsID = connection.query(getEvent_sql);
            if (rsID.next()) {
                event.setEventId(Integer.parseInt(rsID.get("Event_ID")));
            }
            uploadParticipants(participants);

            Mail mail = new Mail();
            int l = participants.size();
            int i = 0;
            while (i != l) {
                String user = participants.get(i);

                String user_sql = String.format("SELECT EMAIL FROM SCHED_USER WHERE USERNAME = '%s'", user);
                DBResults userDetails = connection.query(user_sql);
                userDetails.next();
                System.out.println(userDetails.get("EMAIL"));
                mail.sendMail(
                        currentUser,
                        user,
                        eventName.getText(),
                        eventLocation.getText(),
                        participants,
                        userDetails.get("EMAIL"),
                        eventStartDate.getValue(),
                        eventEndDate.getValue(),
                        startTime,
                        endTime,
                        eventPriority.getValue(),
                        attachmentPath);
                System.out.println("EMAIL SEND");
                i++;
            }
            listener.onAction();
        }
        else {
            System.out.println("Enter valid Start and End Times");
            if(startDateTime.getYear() == LocalDateTime.now().getYear() && startDateTime.getMonth() == LocalDateTime.now().getMonth() && startDateTime.getDayOfMonth() == LocalDateTime.now().getDayOfMonth()) {
                eventStartHour.setStyle("-fx-border-color: red");
                eventStartMin.setStyle("-fx-border-color: red");
                eventEndHour.setStyle("-fx-border-color: red");
                eventEndMin.setStyle("-fx-border-color: red");
            }
            else {
                eventStartDate.setStyle("-fx-border-color: red");
                eventEndDate.setStyle("-fx-border-color: red");
            }
        }
    }

    public void uploadEvent(){
        String startDateTime_temp = "%s %s";
        String startDateTime = String.format(startDateTime_temp, event.getStartDate(), event.getStartTime());
        String endDateTime = String.format(startDateTime_temp, event.getEndDate(), event.getEndTime());

        String sql_temp = "INSERT INTO sched_event (event_name, start_date, end_date, creator_name, location, priority) VALUES ('%s', '%s', '%s', '%s', '%s', '%s')";
        String sql = String.format(sql_temp, event.getName(), startDateTime, endDateTime, event.getCreatorName(), event.getLocation(), event.getPriority());

        String alter_date_format = "ALTER SESSION SET NLS_DATE_FORMAT = 'YYYY-MM-DD HH24:MI'";

        connection.update(alter_date_format);
        connection.update(sql);

    }

    public void uploadParticipants(ArrayList<String> user){
        String sql_user = "INSERT INTO SCHED_PARTICIPATES_IN (USERNAME, EVENT_ID) VALUES ('%s'," + event.getEventId() + ")";

        for (String user_temp : user) {
            String sql_user_temp = String.format(sql_user,user_temp);
            System.out.println(sql_user_temp);

            connection.update(sql_user_temp);
        }
    }

    public void sendMail(ActionEvent event)throws Exception {
        participants.addAll(eventParticipantList.getItems());
        participants.remove(0);
        /*Mail mail = new Mail();
        int l = participants.size();

        for (int i = 1; i<=l; i++){
           String user = participants.get(i);

           String user_sql = String.format("SELECT EMAIL FROM SCHED_USER WHERE USERNAME = '%s'", user);
           DBResults userDetails = connection.query(user_sql);
           userDetails.next();
            System.out.println(userDetails.get("EMAIL"));
       }*/
        int l = participants.size();
        String allParticipants = "";
        int x = 0;
        while(x != l){
            String currentUser = participants.get(x);
            allParticipants = allParticipants + currentUser + ", ";
            System.out.println("TEST " + allParticipants);
            x++;
        }

    }

    public void attachment(ActionEvent e){
        FileChooser chooser = new FileChooser();
        File file = chooser.showOpenDialog(null);
        attachmentPath = file.getAbsolutePath();
    }

    public void exitMenu(){
        listener.onAction();
    }
}
