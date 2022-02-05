package com.project.timescheduler.services;

import com.project.timescheduler.Main;
import com.project.timescheduler.helpers.DBResults;

import java.sql.Date;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Objects;

public class User {
    private final String username;
    private String firstname;
    private String lastname;
    private String email;
    private String password;

    private ArrayList<Event> attendedEvents;
    private ArrayList<Event> hostedEvents;

    public User(String username){
        this.username = username;
        attendedEvents = new ArrayList<>();
        hostedEvents = new ArrayList<>();
        loadAllEvents();
    }

    //Constructor for Adminview
    public User(String username, String firstname, String lastname, String email, String password) {
        this.username = username;
        this.firstname = firstname;
        this.lastname = lastname;
        this.email = email;
        this.password = password;
    }

    private User(String username, ArrayList<Event> attendedEvents, ArrayList<Event> hostedEvents){
        this.username = username;
        this.attendedEvents = new ArrayList<>(attendedEvents);
        this.hostedEvents = new ArrayList<>(hostedEvents);
        loadAllEvents();
    }

    public String getUsername() {
        return username;
    }

    public String getFirstname() {
        return firstname;
    }

    public void setFirstname(String firstname) {
        this.firstname = firstname;
    }

    public String getLastname() {
        return lastname;
    }

    public void setLastname(String lastname) {
        this.lastname = lastname;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public void loadAllEvents(LocalDate... localDate){
        String sql;
        Main.connection.update("ALTER SESSION SET NLS_DATE_FORMAT = 'YYYY-MM-DD HH24:MI'");
        switch (localDate.length){
            case 0:
                sql = "SELECT se.EVENT_ID, sp.USERNAME, CREATOR_NAME, EVENT_NAME, START_DATE, END_DATE, LOCATION, PRIORITY " +
                        "FROM SCHED_EVENT se LEFT JOIN SCHED_PARTICIPATES_IN sp on se.EVENT_ID = sp.EVENT_ID ORDER BY se.EVENT_ID";
                break;
            case 1:
                sql = String.format("SELECT se.EVENT_ID, sp.USERNAME, CREATOR_NAME, EVENT_NAME, START_DATE, END_DATE, LOCATION, PRIORITY " +
                        "FROM SCHED_EVENT se LEFT JOIN SCHED_PARTICIPATES_IN sp on se.EVENT_ID = sp.EVENT_ID WHERE START_DATE > '%s 00:00'" +
                        "ORDER BY START_DATE", Date.valueOf(localDate[0]));
                break;
            case 2:
                if(localDate[0] == null) {
                    sql = String.format("SELECT se.EVENT_ID, sp.USERNAME, CREATOR_NAME, EVENT_NAME, START_DATE, END_DATE, LOCATION, PRIORITY " +
                            "FROM SCHED_EVENT se LEFT JOIN SCHED_PARTICIPATES_IN sp on se.EVENT_ID = sp.EVENT_ID WHERE END_DATE < '%s 23:59'" +
                            "ORDER BY START_DATE", Date.valueOf(localDate[1]));
                }else {
                    sql = String.format("SELECT se.EVENT_ID, sp.USERNAME, CREATOR_NAME, EVENT_NAME, START_DATE, END_DATE, LOCATION, PRIORITY " +
                            "FROM SCHED_EVENT se LEFT JOIN SCHED_PARTICIPATES_IN sp on se.EVENT_ID = sp.EVENT_ID " +
                            "WHERE START_DATE BETWEEN '%s 00:00' AND '%s 23:59' ORDER BY START_DATE", Date.valueOf(localDate[0]), Date.valueOf(localDate[1]));
                }
                break;
            default:
                System.err.println("ERROR");
                return;
        }

        DBResults rs = Main.connection.query(sql);

        attendedEvents.clear();
        hostedEvents.clear();

        ArrayList<String> participantsList = new ArrayList<>();
        Event event = null;
        int currentEventId = -1;

        while (rs.next()) {
            String username = rs.get("USERNAME");
            String creator = rs.get("CREATOR_NAME");

            //der user kann soll sich nicht hinzufügen können
            if(currentEventId != Integer.parseInt(rs.get("EVENT_ID"))){
                currentEventId = Integer.parseInt(rs.get("EVENT_ID"));

                if (event != null){
                    if(participantsList.contains(this.username)) {
                        attendedEvents.add(event);
                    }
                    if (event.getCreatorName().equals(this.username)){
                        hostedEvents.add(event);
                    }
                }

                participantsList = new ArrayList<>();
                event = new Event(currentEventId,
                        creator,
                        rs.get("EVENT_NAME"),
                        rs.get("LOCATION"),
                        participantsList,
                        rs.getDate("START_DATE").toLocalDate(),
                        rs.getDate("END_DATE").toLocalDate(),
                        rs.getTime("START_DATE").toLocalTime(),
                        rs.getTime("END_DATE").toLocalTime(),
                        Event.Priority.valueOf(rs.get("PRIORITY")),
                        100); //Platzhalter muss noch geändert werden!!!
            }
            Objects.requireNonNull(participantsList).add(username);
        }
        if (event != null){
            if(participantsList.contains(this.username)) {
                attendedEvents.add(event);
            }
            if (event.getCreatorName().equals(this.username)){
                hostedEvents.add(event);

            }
        }
    }

    public ArrayList<Event> getAllEvents(LocalDate... localDate){
        loadAllEvents(localDate);
        ArrayList<Event> allEvents = new ArrayList<>();
        allEvents.addAll(attendedEvents);
        allEvents.addAll(hostedEvents);
        return allEvents;
    }

    public ArrayList<Event> getAttendedEvents(LocalDate... localDate) {
        loadAllEvents(localDate);
        return attendedEvents;
    }

    public ArrayList<Event> getHostedEvents(LocalDate... localDate) {
        loadAllEvents(localDate);
        return hostedEvents;
    }

    public User copy(){
        return new User(username, attendedEvents, hostedEvents);
    }
}
