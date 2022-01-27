package com.project.timescheduler.controllers;

import com.project.timescheduler.Main;
import com.project.timescheduler.helpers.DBResults;
import com.project.timescheduler.services.DatabaseConnection;
import com.project.timescheduler.services.Event;
import com.project.timescheduler.services.Participates_In;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.Label;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.Pane;
import javafx.scene.layout.TilePane;
import javafx.scene.layout.VBox;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Objects;

public class EventViewerContoller {

    @FXML
    private TilePane tilePaneHosted;

    @FXML
    private TilePane tilePaneAttended;

    private DatabaseConnection connection;
    private String currentUser;

    @FXML
    public void initialize(String currentUser){
        connection = new DatabaseConnection();
        this.currentUser = currentUser;
        loadData();
    }

    private void loadData(){
        ArrayList<Event> dataList = new ArrayList<>();

        String sql = "SELECT * FROM SCHED_EVENT";
        DBResults rs = connection.query(sql);
        while (rs.next()){

            Event event = new Event(Integer.parseInt(rs.get("EVENT_ID")),
                    rs.get("CREATOR_NAME"),
                    rs.get("EVENT_NAME"),
                    rs.get("LOCATION"),
                    null,
                    rs.getDate("START_DATE").toLocalDate(),
                    rs.getDate("END_DATE").toLocalDate(),
                    null,
                    null,
                    Event.Priority.valueOf(rs.get("PRIORITY")));
            dataList.add(event);
        }
        setHostedEvents(dataList);
        setAttendingEvents(dataList);
    }

    public void setAttendingEvents(ArrayList<Event> events){
        ArrayList<Participates_In> participates_in = new ArrayList<>();
        ArrayList<Number> ids = new ArrayList<>();


        String sql = String.format("SELECT * FROM SCHED_PARTICIPATES_IN WHERE USERNAME = '%s'", currentUser);
        DBResults rs = connection.query(sql);
        while (rs.next()){
            Participates_In par_in = new Participates_In(Integer.parseInt(rs.get("EVENT_ID")), rs.get("USERNAME"));
            participates_in.add(par_in);
        }

        for (Participates_In p : participates_in) {
            ids.add(p.getEvent_ID());
        }
        for (Event e : events) {
            if (!e.getCreatorName().equals(currentUser) && ids.contains(e.getEventId()))
                loadEventViewerItem(tilePaneAttended, e.getName());
        }
    }
    public void setHostedEvents(ArrayList<Event>events){
        for (Event e : events) {
            if(e.getCreatorName().equals(currentUser))
                loadEventViewerItem(tilePaneHosted, e.getName());
                System.out.println("host");
        }
    }

    private void loadEventViewerItem(Pane rootPane, String eventName){
        try {
            FXMLLoader loader = new FXMLLoader(Objects.requireNonNull(Main.class.getResource("eventViewerItem.fxml")));
            AnchorPane anchorPane = loader.load();
            rootPane.getChildren().add(anchorPane);

            VBox vBox = (VBox) anchorPane.getChildren().get(0);
            Label label = (Label) vBox.getChildren().get(0);
            label.setText(eventName);
            vBox.setOnMouseClicked(this::onEventClicked);
        }catch (IOException e){
            e.printStackTrace();
        }
    }

    @FXML
    private void onEventClicked(MouseEvent mouseEvent){
        System.out.println(mouseEvent.getTarget());
    }
}
