package com.project.timescheduler.controllers;

import com.project.timescheduler.Main;
import com.project.timescheduler.helpers.DBResults;
import com.project.timescheduler.services.Event;
import com.project.timescheduler.services.Mail;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.stage.FileChooser;
import javafx.stage.Stage;

import java.io.File;
import java.time.LocalTime;
import java.util.ArrayList;

/**
 * Displays all information of an Event the logged-in user hosts
 * as well as offers the editing and deletion of the Event
 */
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
    private Button exit;
    @FXML
    private ChoiceBox<String> cbReminder;

    private String currentUser;
    private ArrayList<String> originalParticipants = new ArrayList<>();
    private ArrayList<String> selectedParticipants = new ArrayList<>();
    private ArrayList<String> removedParticipants = new ArrayList<>();
    private Event event;
    private String attachmentPath = "null"; //Default value in case of not selecting an attachment

    interface OnActionListener{
        void onExit();
    }

    private OnActionListener listener;

    /**
     * called when opening the scene, initializes the values for selected event, currentUser
     * calls loadData
     * @param event the Event the user clicked on in EventViewer
     * @param currentUser String of username from currently logged-in user
     * @param listener this listener triggers the function called on closing the stage
     */
    @FXML
    public void initialize(Event event, String currentUser, OnActionListener listener){
        this.currentUser = currentUser;
        this.event = event;
        this.listener = listener;
        System.out.println("EventID: " + event.getEventId());
        cbPriority.getItems().addAll(Event.Priority.values());
        cbReminder.getItems().addAll("1 week", "3 days", "1 hour", "10 minutes");
        loadData();
    }

    /**
     * sets text of TextFileds, ChoiceBox and ListViews to Event information
     */
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
        System.out.println(event.getStartTime());

        ObservableList<String> participants = FXCollections.observableArrayList(); //Load all participants from this event first

        String participants_sql = String.format("SELECT * FROM SCHED_PARTICIPATES_IN WHERE EVENT_ID = '%s'", event.getEventId());
        DBResults rs = Main.connection.query(participants_sql);
        while (rs.next()) {
            participants.add(rs.get("USERNAME"));
        } selectedParticipantsList.setItems(participants);
        originalParticipants.addAll(participants);

        ObservableList<String> users = FXCollections.observableArrayList(); //Load all users

        String user_sql = String.format("SELECT USERNAME FROM SCHED_USER");
        DBResults useRS = Main.connection.query(user_sql);
        while (useRS.next()) {
            users.add(useRS.get("USERNAME"));
        }   users.remove(TimeSchedulerController.getCurrentUser().getUsername());
        for(int i=0; i<participants.size(); i++){
            System.out.println(participants.get(i));
            users.remove(participants.get(i));
        }   allParticipantsList.setItems(users);
    }

    /** Handling the display of the item/participant when selecting to add. **/
    @FXML
    private void addNameSelection(){
        String selectedItem = allParticipantsList.getSelectionModel().getSelectedItem();
        if(selectedItem != null){
            if(removedParticipants.contains(selectedItem)){
                removedParticipants.remove(selectedItem);
            }
            selectedParticipantsList.getItems().add(selectedItem);
            allParticipantsList.getItems().remove(selectedItem);
            allParticipantsList.getSelectionModel().clearSelection();
        }
    }
    /** Handling the display of the item/participant when selecting to remove. **/
    @FXML
    private void removeNameSelection(){
        String selectedItem = selectedParticipantsList.getSelectionModel().getSelectedItem();
        if(selectedItem != null){
            removedParticipants.add(selectedItem);
            allParticipantsList.getItems().add(selectedItem);
            selectedParticipantsList.getItems().remove(selectedItem);
            selectedParticipantsList.getSelectionModel().clearSelection();
        }
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

    /**
     * creates new Event with the information from the ui
     * updates the event information in the database
     * sends out Email to all participants
     * @throws Exception
     */
    public void updateData() throws Exception {
        selectedParticipants.addAll(selectedParticipantsList.getItems());
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
                selectedParticipants,
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

        String deleteUser_sql = String.format("DELETE FROM SCHED_PARTICIPATES_IN WHERE EVENT_ID = '%s'", event.getEventId());
        Main.connection.update(deleteUser_sql);

        for(int x=0; x<selectedParticipants.size(); x++) {
            String addUser_sql = String.format
                    ("INSERT INTO SCHED_PARTICIPATES_IN " +
                                    "(USERNAME, EVENT_ID) " +
                                    "VALUES ('%s', '%s')",
                            selectedParticipants.get(x), event.getEventId());
            Main.connection.update(addUser_sql);
        }

        Mail mail = new Mail();
        int l = selectedParticipants.size();
        int y = 0;
        while (y != l) {
            String user = selectedParticipants.get(y);

            String user_sql = String.format("SELECT EMAIL FROM SCHED_USER WHERE USERNAME = '%s'", user);
            DBResults userDetails = Main.connection.query(user_sql);
            userDetails.next();
            System.out.println(userDetails.get("EMAIL"));
            mail.sendMail(
                    event.getCreatorName(),
                    user,
                    event.getName(),
                    tfLocation.getText(),
                    selectedParticipants,
                    userDetails.get("EMAIL"),
                    tfStartDate.getValue(),
                    tfEndDate.getValue(),
                    startTime,
                    endTime,
                    cbPriority.getValue(),
                    attachmentPath,
                    Mail.Type.valueOf("update"));
            System.out.println("EMAIL SEND");
            y++;
        }

        for(int z=0; z<removedParticipants.size(); z++){
                if(originalParticipants.contains(removedParticipants.get(z))){
                    String xUser_sql = String.format("SELECT EMAIL FROM SCHED_USER WHERE USERNAME = '%s'", removedParticipants.get(z));
                    DBResults xUserDetails = Main.connection.query(xUser_sql);
                    xUserDetails.next();
                    System.out.println(xUserDetails.get("EMAIL"));
                    mail.sendMail(
                            event.getCreatorName(),
                            removedParticipants.get(z),     //Essential
                            event.getName(),                //Essential
                            tfLocation.getText(),
                            removedParticipants,
                            xUserDetails.get("EMAIL"),      //Essential
                            tfStartDate.getValue(),
                            tfEndDate.getValue(),
                            startTime,
                            endTime,
                            cbPriority.getValue(),
                            attachmentPath,
                            Mail.Type.valueOf("remove"));   //Essential
                    System.out.println("EMAIL SEND");
                }
        }
        Stage stage = (Stage) bSave.getScene().getWindow();
        stage.close();
    }

    /** Attachment input and getting the path. **/
    public void attachment(){
        Stage stage = (Stage) bSave.getScene().getWindow();
        FileChooser chooser = new FileChooser();
        File file = chooser.showOpenDialog(stage);
        attachmentPath = file.getAbsolutePath();
        System.out.println(attachmentPath);
    }

    /**
     * deletes the Event out of the Database,
     * sends notification Mail to all former participants,
     * refreshes EventViewer,
     * closes Stage
     * @throws Exception
     */
    public void delete() throws Exception {

        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("Delete Event");
        alert.setHeaderText("You're about to delete your Event: " + event.getName());
        alert.setContentText("Are you sure?");

        if(alert.showAndWait().get() == ButtonType.OK){
        String deleteParticipants_sql = String.format("DELETE FROM SCHED_PARTICIPATES_IN WHERE EVENT_ID = '%s'", event.getEventId());
        String deleteEvent_sql = String.format("DELETE FROM SCHED_EVENT WHERE EVENT_ID = '%s'", event.getEventId());

        Mail deleteMail = new Mail();

        for(int d=0; d<originalParticipants.size(); d++){
            String xUser_sql = String.format("SELECT EMAIL FROM SCHED_USER WHERE USERNAME = '%s'", originalParticipants.get(d));
            DBResults xxUserDetails = Main.connection.query(xUser_sql);
            xxUserDetails.next();
            System.out.println(xxUserDetails.get("EMAIL"));
            deleteMail.sendMail(
                event.getCreatorName(),
                originalParticipants.get(d),     //Essential
                event.getName(),                //Essential
                tfLocation.getText(),
                originalParticipants,
                xxUserDetails.get("EMAIL"),      //Essential
                tfStartDate.getValue(),
                tfEndDate.getValue(),
                null,
                null,
                cbPriority.getValue(),
                attachmentPath,
                Mail.Type.valueOf("delete"));   //Essential
                System.out.println("EMAIL SEND");
    }

        Main.connection.update(deleteParticipants_sql);
        Main.connection.update(deleteEvent_sql);
        Stage stage = (Stage) bSave.getScene().getWindow();
        stage.close();
        listener.onExit();
    }
    }

    /**
     * closes Stage
     */
    public void exit (){
        Stage stage = (Stage) bSave.getScene().getWindow();
        stage.close();
    }
}