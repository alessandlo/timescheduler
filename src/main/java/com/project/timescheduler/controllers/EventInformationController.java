package com.project.timescheduler.controllers;

import com.project.timescheduler.Main;
import com.project.timescheduler.helpers.DBResults;
import com.project.timescheduler.services.Event;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.stage.Stage;

/**
 * Displays all information of an Event the user attends
 */
public class EventInformationController{
    @FXML
    private Button bBack;
    @FXML
    private Label lEventName;
    @FXML
    private Label lStartDate;
    @FXML
    private Label lEndDate;
    @FXML
    private Label lCreatorName;
    @FXML
    private Label lLocation;
    @FXML
    private Label lPriority;
    @FXML
    private ListView<String> selectedParticipantsList;

    private String currentUser;
    private Event event;

    /**
     * called when opening the scene, initializes the values for selected event
     * calls loadData
     * @param event the Event the user clicked on in EventViewer
     * @param currentUser String of username from currently logged-in user
     */
    @FXML
    public void initialize(Event event, String currentUser){
        this.currentUser = currentUser;
        this.event = event;
        System.out.println("EventID: " + event.getEventId());
        loadData();
    }

    /**
     * sets text of labels, listView to event information
     */
    public void loadData(){

        String time_sql = String.format("SELECT * FROM SCHED_EVENT WHERE EVENT_ID = '%s'", event.getEventId());
        DBResults timeDetails = Main.connection.query(time_sql);
        timeDetails.next();

        lEventName.setText(event.getName());
        lStartDate.setText(timeDetails.get("START_DATE"));
        lEndDate.setText(timeDetails.get("END_DATE"));
        lCreatorName.setText(event.getCreatorName());
        lLocation.setText(event.getLocation());
        lPriority.setText(event.getPriority().toString());

        ObservableList<String> users = FXCollections.observableArrayList();

        String participants_sql = String.format("SELECT * FROM SCHED_PARTICIPATES_IN WHERE EVENT_ID = '%s'", event.getEventId());
        DBResults participants = Main.connection.query(participants_sql);

        while (participants.next()) {
            users.add(participants.get("USERNAME"));
        }   selectedParticipantsList.setItems(users);
    }

    /**
     * closes Window
     */
    public void exit (){
        Stage stage = (Stage) bBack.getScene().getWindow();
        stage.close();
    }
}
