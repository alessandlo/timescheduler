package com.project.timescheduler.controllers;

import com.project.timescheduler.Main;
import com.project.timescheduler.helpers.DBResults;
import com.project.timescheduler.services.Event;
import com.project.timescheduler.services.Mail;
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
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.Objects;
/** This controller manages all the functionalities regarding the creation of a new event. **/
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

    /** Interface to refresh main page after closing EventMenu. **/
    public interface OnActionListener {
        void onAction();
    }

    private String currentUser; //Current User

    private String attachmentPath = "null"; //Default value in case of not selecting an attachment

    /**
     * called when opening the scene, initializes the values for currentUser, priority and participants.
     * @param listener
     * @param currentUser String of username from currently logged in user
     */
    @FXML
    public void initialize(OnActionListener listener, String currentUser, LocalDate localDate){
        this.listener = listener;
        participants = new ArrayList<>();
        this.currentUser = currentUser;

        eventPriority.getItems().addAll(Event.Priority.values());
        eventParticipantList.getItems().add("Manage participants");
        eventReminder.getItems().addAll("1 week", "3 days", "1 hour", "10 minutes");

        eventStartDate.setValue(localDate);
    }
    /** resets all Date and time field border colors to transparent **/
    public void ButtonBorderColorReset() {
        eventStartDate.setStyle("-fx-border-color: transparent");
        eventStartHour.setStyle("-fx-border-color: transparent");
        eventStartMin.setStyle("-fx-border-color: transparent");
        eventEndDate.setStyle("-fx-border-color: transparent");
        eventEndHour.setStyle("-fx-border-color: transparent");
        eventEndMin.setStyle("-fx-border-color: transparent");
    }
    /** sets a group of Date and Time fields to red depending on the value of res **/
    public void setBorderColor(int res, DatePicker datePicker, TextField hour, TextField min, String color) {
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
    /** resets the bordercolor of Datepickers to transparent when they are clicked on **/
    public void resetBorderColor(MouseEvent mouseEvent) {
        System.out.println("reset border color");
        DatePicker datePicker = (DatePicker)((StackPane)mouseEvent.getTarget()).getParent();
        datePicker.setStyle("-fx-border-color: transparent");
    }
    /** resets the bordercolor of TextFields to transparent when they are clicked on **/
    public void resetBorderColorT(MouseEvent mouseEvent) {
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

    /**Compares two LocalDateTime variables
     returns different values depending on which component of the LocalDateTime ldt1 is causing it to be before ldt2
     <p>-1 -> ldt1 is not before ldt2 </p>
     <p>1 -> Date of ldt1 is before Date of ldt2</p>
     <p>2 -> Hour of ldt1 is before Hour of ldt2</p>
     <p>3 -> Minute of ldt1 is before Minute of ldt2</p>
     @param ldt1 LocalDateTime for which the function checks if and why it is before ldt2
     @param ldt2 LocalDateTime ldt2**/
    public int checkDate(LocalDateTime ldt1, LocalDateTime ldt2) {
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
    /** converts the remindertime from a String into a long which represents the time in milliseconds
     * @return a long value representing the reminder of the event in milliseconds**/
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
    /** Function to manage a separate window for the participant's selection/removal. **/
    public void manageParticipants() throws IOException {
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
            participantsStage.getIcons().add(Main.mainStage.getIcons().get(0));
            participantsStage.setScene(scene);
            participantsStage.initModality(Modality.APPLICATION_MODAL);
            participantsStage.showAndWait();
        }
    }

    /**
     * uses the information entered in the ui to creates an Event
     * calls method uploadEvent
     * retrives EventID from the new event out of the database
     * call uploadParticipants
     * sends mail to each participant
     * @throws Exception
     */
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

    /**
     * adds new Event to database
     */
    public void uploadEvent(){
        String startDateTime_temp = "%s %s";
        String startDateTime = String.format(startDateTime_temp, event.getStartDate(), event.getStartTime());
        String endDateTime = String.format(startDateTime_temp, event.getEndDate(), event.getEndTime());

        String sql_temp = "INSERT INTO sched_event (event_name, start_date, end_date, creator_name, location, priority, reminder, remindersentflag) VALUES ('%s', '%s', '%s', '%s', '%s', '%s', '%s', 0)";
        String sql = String.format(sql_temp, event.getName(), startDateTime, endDateTime, event.getCreatorName(), event.getLocation(), event.getPriority(), Long.toString(event.getReminder()));

        String alter_date_format = "ALTER SESSION SET NLS_DATE_FORMAT = 'YYYY-MM-DD HH24:MI'";
        System.out.println(startDateTime + " " + endDateTime);
        Main.connection.update(alter_date_format);
        Main.connection.update(sql);
    }

    /**
     * adds all participants to database
     * @param user list of all users attending the Event
     */
    public void uploadParticipants(ArrayList<String> user){
        String sql_user = "INSERT INTO SCHED_PARTICIPATES_IN (USERNAME, EVENT_ID) VALUES ('%s'," + event.getEventId() + ")";

        for (String user_temp : user) {
            String sql_user_temp = String.format(sql_user,user_temp);
            System.out.println(sql_user_temp);

            Main.connection.update(sql_user_temp);
        }
    }
    /** Attachment input and getting the path. **/
    public void attachment(){
        Stage stage = (Stage) eventName.getScene().getWindow();
        FileChooser chooser = new FileChooser();
        File file = chooser.showOpenDialog(stage);
        attachmentPath = file.getAbsolutePath();
        System.out.println(attachmentPath);
    }
    /** Exit function/button. **/
    @FXML
    private void exitMenu(){
        listener.onAction();
    }
}
