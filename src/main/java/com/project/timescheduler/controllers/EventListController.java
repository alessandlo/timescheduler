package com.project.timescheduler.controllers;

import com.project.timescheduler.helpers.DBResults;
import com.project.timescheduler.services.DatabaseConnection;
import com.project.timescheduler.services.Event;
import com.project.timescheduler.services.Participates_In;
import javafx.fxml.FXML;
import javafx.scene.control.ListView;

import java.util.LinkedList;

public class EventListController{

    @FXML
    ListView attendingList;
    @FXML
    ListView hostedList;

    private LinkedList<String> attendingEventList = new LinkedList<>();
    private LinkedList<String> hostedEventList = new LinkedList<>();
    private String activeUser;
    private DatabaseConnection connection = new DatabaseConnection();

    @FXML
    public void initialize(String currentUser) {
        activeUser = currentUser;
        try {
            loadListData();
        }
        catch (Exception e){
            e.printStackTrace();
        }
        attendingList.getItems().addAll(attendingEventList);
        hostedList.getItems().addAll(hostedEventList);
    }
    public void loadListData(){
        LinkedList<Event> dataList = new LinkedList<>();

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
    public void setAttendingEvents(LinkedList<Event> events){
        LinkedList<Participates_In> participates_in = new LinkedList<>();
        LinkedList<Number> ids = new LinkedList<>();


        String sql = "SELECT * FROM SCHED_PARTICIPATES_IN WHERE USERNAME = '" + activeUser + "'";
        DBResults rs = connection.query(sql);
        while (rs.next()){
            Participates_In par_in = new Participates_In(Integer.parseInt(rs.get("EVENT_ID")), rs.get("USERNAME"));
            participates_in.add(par_in);
        }

        for (Participates_In p : participates_in) {
            ids.add(p.getEvent_ID());
        }
        for (Event e : events) {
            if (!e.getCreatorName().equals(activeUser) && ids.contains(e.getEventId()))
                attendingEventList.add(e.getName());
        }
    }
    public void setHostedEvents(LinkedList<Event>events){
        for (Event e : events) {
            if(e.getCreatorName().equals(activeUser))
                hostedEventList.add(e.getName());
        }
    }

}
