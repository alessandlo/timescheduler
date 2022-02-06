package com.project.timescheduler.controllers;

import com.project.timescheduler.Main;
import com.project.timescheduler.helpers.DBResults;
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
import javafx.scene.layout.Pane;
import javafx.scene.layout.StackPane;
import javafx.scene.text.Text;
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
    private ChoiceBox<String> eventReminder;

    private Event event;

    private ArrayList<String> participants;

    private OnActionListener listener;

    public interface OnActionListener {
        void onAction();
    }

    private String currentUser; //Current User

    private String attachmentPath = "null"; //Default value in case of not selecting an attachment

    @FXML
    public void initialize(OnActionListener listener, String currentUser){
        this.listener = listener;
        participants = new ArrayList<>();
        this.currentUser = currentUser;

        eventPriority.getItems().addAll(Event.Priority.values());
        eventParticipantList.getItems().add("Manage participants");
        eventReminder.getItems().addAll("1 week", "3 days", "1 hour", "10 minutes");
    }

    public void ButtonBorderColorReset() {
        //resets all Date and time field border colors to transparent
        eventStartDate.setStyle("-fx-border-color: transparent");
        eventStartHour.setStyle("-fx-border-color: transparent");
        eventStartMin.setStyle("-fx-border-color: transparent");
        eventEndDate.setStyle("-fx-border-color: transparent");
        eventEndHour.setStyle("-fx-border-color: transparent");
        eventEndMin.setStyle("-fx-border-color: transparent");
    }

    public void setBorderColor(int res, DatePicker datePicker, TextField hour, TextField min, String color) {
        //sets a group of Date and Time fields to red depending on the value of res
        switch (res) {
            case -1:
                break;
            case 1:
                datePicker.setStyle("-fx-border-color: " + color);
                break;
            case 2:
                hour.setStyle("-fx-border-color: " + color);
                break;
            case 3:
                min.setStyle("-fx-border-color: " + color);
                break;
            default:
                System.out.println("something went wrong in switch startDate - EventMenuController");
                break;
        }
    }

    public void resetBorderColor(MouseEvent mouseEvent) {
        //resets the bordercolor of Datepickers to transparent when they are clicked on
        System.out.println("reset border color");

        DatePicker datePicker = (DatePicker)((StackPane)mouseEvent.getTarget()).getParent();
        datePicker.setStyle("-fx-border-color: transparent");
    }

    public void resetBorderColorT(MouseEvent mouseEvent) {
        //resets the bordercolor of TextFields to transparent when they are clicked on
        TextField textField = new TextField();
        if(mouseEvent.getTarget() instanceof Pane) {
            System.out.println("Pane");
            System.out.println(mouseEvent.getTarget());

            System.out.println("Parent of Pane:");
            System.out.println(((Pane)mouseEvent.getTarget()).getParent());

            textField = (TextField)((Pane)mouseEvent.getTarget()).getParent();
        }
        else if(mouseEvent.getTarget() instanceof Text) {
            System.out.println("Text");
            System.out.println(mouseEvent.getTarget());

            System.out.println("Parent of Text:");
            System.out.println(((Text)mouseEvent.getTarget()).getParent());

            System.out.println("Parent of Parent of Text:");
            System.out.println(((Pane)((Text)mouseEvent.getTarget()).getParent()).getParent());

            textField = (TextField) ((Pane)((Text)mouseEvent.getTarget()).getParent()).getParent();
        }
        else if(mouseEvent.getTarget() instanceof TextField) {
            System.out.println("TextField");
            System.out.println(mouseEvent.getTarget());
            textField = (TextField) mouseEvent.getTarget();
        }
        textField.setStyle("-fx-border-color: transparent");
    }

    public int checkDate(LocalDateTime ldt1, LocalDateTime ldt2) {
        //Compares two LocalDateTime variables
        //returns different values depending on which component of LocalDateTime is causing ldt1 to be before ldt2
        // -1 -> ldt1 is not before ldt2
        // 1 -> Date of ldt1 is before Date of ldt2
        // 2 -> Hour of ldt1 is before Hour of ldt2
        // 3 -> Minute of ldt1 is before Minute of ldt2
        if(ldt1.isBefore(ldt2)){
            if (ldt1.getYear() == ldt2.getYear() && ldt1.getMonth() == ldt2.getMonth() && ldt1.getDayOfMonth() == ldt2.getDayOfMonth()) {
                if(ldt1.getHour() == ldt2.getHour()) {
                    return(3);
                } else {
                    return(2);
                }
            } else {
                return (1);
            }
        } else {
            return(-1);
        }
    }

    public long retrieveReminder() {
        String reminder = eventReminder.getValue();
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
        ButtonBorderColorReset();
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
                    eventPriority.getValue(),
                    retrieveReminder()
            );

            uploadEvent();

            //Das muss neu gemacht werden
            String getEvent_sql = "SELECT EVENT_ID FROM SCHED_EVENT where EVENT_NAME = '%s' AND LOCATION = '%s' AND PRIORITY = '%s'";
            getEvent_sql = String.format(getEvent_sql, event.getName(), event.getLocation(), event.getPriority());
            DBResults rsID = Main.connection.query(getEvent_sql);
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
                DBResults userDetails = Main.connection.query(user_sql);
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
                        attachmentPath,
                        Mail.Type.valueOf("create"));
                System.out.println("EMAIL SEND");
                i++;
            }
            listener.onAction();
        }
        else {
            System.out.println("Enter valid Start and End Times");

            setBorderColor(checkDate(startDateTime, LocalDateTime.now()), eventStartDate, eventStartHour, eventStartMin, "red");
            setBorderColor(checkDate(endDateTime, LocalDateTime.now()), eventEndDate, eventEndHour, eventEndMin, "red");

            setBorderColor(checkDate(endDateTime, startDateTime), eventEndDate, eventEndHour, eventEndMin, "orange");
        }
    }

    public void uploadEvent(){
        String startDateTime_temp = "%s %s";
        String startDateTime = String.format(startDateTime_temp, event.getStartDate(), event.getStartTime());
        String endDateTime = String.format(startDateTime_temp, event.getEndDate(), event.getEndTime());

        String sql_temp = "INSERT INTO sched_event (event_name, start_date, end_date, creator_name, location, priority, reminder) VALUES ('%s', '%s', '%s', '%s', '%s', '%s', '%s')";
        String sql = String.format(sql_temp, event.getName(), startDateTime, endDateTime, event.getCreatorName(), event.getLocation(), event.getPriority(), Long.toString(event.getReminder()));

        String alter_date_format = "ALTER SESSION SET NLS_DATE_FORMAT = 'YYYY-MM-DD HH24:MI'";
        System.out.println(startDateTime + " " + endDateTime);
        Main.connection.update(alter_date_format);
        Main.connection.update(sql);
    }

    public void uploadParticipants(ArrayList<String> user){
        String sql_user = "INSERT INTO SCHED_PARTICIPATES_IN (USERNAME, EVENT_ID) VALUES ('%s'," + event.getEventId() + ")";

        for (String user_temp : user) {
            String sql_user_temp = String.format(sql_user,user_temp);
            System.out.println(sql_user_temp);

            Main.connection.update(sql_user_temp);
        }
    }
    /** Attachment input and getting the path. **/
    public void attachment(ActionEvent e){
        Stage stage = (Stage) eventName.getScene().getWindow();
        FileChooser chooser = new FileChooser();
        File file = chooser.showOpenDialog(stage);
        attachmentPath = file.getAbsolutePath();
        System.out.println(attachmentPath);
    }

    @FXML
    private void exitMenu(){
        listener.onAction();
    }
}
