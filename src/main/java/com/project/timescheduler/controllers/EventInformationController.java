package com.project.timescheduler.controllers;

import com.project.timescheduler.Main;
import com.project.timescheduler.helpers.DBResults;
import com.project.timescheduler.services.Event;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;


public class EventInformationController{
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

    @FXML
    public void initialize(Event event, String currentUser){
        this.currentUser = currentUser;
        this.event = event;
        loadData();
    }
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

    }

}
