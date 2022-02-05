package com.project.timescheduler.controllers;

import com.project.timescheduler.services.Event;
import javafx.fxml.FXML;
import javafx.scene.control.Label;


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

    private String currentUser;
    private Event event;

    @FXML
    public void initialize(Event event, String currentUser){
        this.currentUser = currentUser;
        this.event = event;
        loadData();
    }
    public void loadData(){

        lEventName.setText(event.getName());
        lStartDate.setText(event.getStartDate().toString());
        lEndDate.setText(event.getEndDate().toString());
        lCreatorName.setText(event.getCreatorName());
        lLocation.setText(event.getLocation());
        lPriority.setText(event.getPriority().toString());

    }

}
