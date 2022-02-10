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
    private ArrayList<Event> allEvents;

    /**
     * Constructor for username and eventlists
     * @param username username of the selected user
     */
    public User(String username){
        this.username = username;
        attendedEvents = new ArrayList<>();
        hostedEvents = new ArrayList<>();
        allEvents = new ArrayList<>();
    }

    /**
     * Constructer for details of a user (used for Adminpanel)
     * @param username username of the selected user
     * @param firstname firstname of the selected user
     * @param lastname lastname of the selected user
     * @param email email of the selected user
     * @param password password of the selected user
     */
    public User(String username, String firstname, String lastname, String email, String password) {
        this.username = username;
        this.firstname = firstname;
        this.lastname = lastname;
        this.email = email;
        this.password = password;
    }

    /**
     * Constructer for events of a user
     * @param username username of the selected user
     * @param attendedEvents attendedEvents of the selected user
     * @param hostedEvents hostedEvents of the selected user
     * @param allEvents allEvents of the selected user
     */
    private User(String username, ArrayList<Event> attendedEvents, ArrayList<Event> hostedEvents, ArrayList<Event> allEvents){
        this.username = username;
        this.attendedEvents = new ArrayList<>(attendedEvents);
        this.hostedEvents = new ArrayList<>(hostedEvents);
        this.allEvents = new ArrayList<>(allEvents);
    }

    /**
     * Loads all events by using database queries
     * @param localDate To restrict the time period of the events
     */
    public void loadAllEvents(LocalDate... localDate){
        String sql;

        switch (localDate.length){
            case 0:
                sql = "SELECT se.EVENT_ID, sp.USERNAME, CREATOR_NAME, EVENT_NAME, START_DATE, END_DATE, LOCATION, PRIORITY, REMINDER " +
                        "FROM SCHED_EVENT se LEFT JOIN SCHED_PARTICIPATES_IN sp on se.EVENT_ID = sp.EVENT_ID ORDER BY START_DATE";
                break;
            case 1:
                sql = String.format("SELECT se.EVENT_ID, sp.USERNAME, CREATOR_NAME, EVENT_NAME, START_DATE, END_DATE, LOCATION, PRIORITY, REMINDER " +
                        "FROM SCHED_EVENT se LEFT JOIN SCHED_PARTICIPATES_IN sp on se.EVENT_ID = sp.EVENT_ID WHERE START_DATE > '%s 00:00'" +
                        "ORDER BY START_DATE", Date.valueOf(localDate[0]));
                break;
            case 2:
                if(localDate[0] == null) {
                    sql = String.format("SELECT se.EVENT_ID, sp.USERNAME, CREATOR_NAME, EVENT_NAME, START_DATE, END_DATE, LOCATION, PRIORITY, REMINDER " +
                            "FROM SCHED_EVENT se LEFT JOIN SCHED_PARTICIPATES_IN sp on se.EVENT_ID = sp.EVENT_ID WHERE END_DATE < '%s 23:59'" +
                            "ORDER BY START_DATE", Date.valueOf(localDate[1]));
                }else {
                    sql = String.format("SELECT se.EVENT_ID, sp.USERNAME, CREATOR_NAME, EVENT_NAME, START_DATE, END_DATE, LOCATION, PRIORITY, REMINDER " +
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
        allEvents.clear();

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
                    if (event.getCreatorName().equals(this.username)){
                        hostedEvents.add(event);
                        allEvents.add(event);
                    }else if(participantsList.contains(this.username)) {
                        attendedEvents.add(event);
                        allEvents.add(event);
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
                        Long.parseLong(rs.get("REMINDER")));
            }
            Objects.requireNonNull(participantsList).add(username);
        }
        if (event != null){
            if (event.getCreatorName().equals(this.username)){
                hostedEvents.add(event);
                allEvents.add(event);
            }else if(participantsList.contains(this.username)) {
                attendedEvents.add(event);
                allEvents.add(event);
            }
        }
    }

    /**
     * Get all events
     * @param localDate To restrict the time period of the events
     * @return all events
     */
    public ArrayList<Event> getAllEvents(LocalDate... localDate){
        loadAllEvents(localDate);

        return allEvents;
    }

    /**
     * Get all participating events
     * @param localDate To restrict the time period of the events
     * @return all participating events
     */
    public ArrayList<Event> getAttendedEvents(LocalDate... localDate) {
        loadAllEvents(localDate);
        return attendedEvents;
    }

    /**
     * Get all hosted events
     * @param localDate To restrict the time period of the events
     * @return all created events
     */
    public ArrayList<Event> getHostedEvents(LocalDate... localDate) {
        loadAllEvents(localDate);
        return hostedEvents;
    }

    public User copy(){
        return new User(username, attendedEvents, hostedEvents, allEvents);
    }

    /**
     * Get username from user
     * @return username
     */
    public String getUsername() {
        return username;
    }

    /**
     * Get firstname from user
     * @return username
     */
    public String getFirstname() {
        return firstname;
    }

    /**
     * Set firstname of user
     */
    public void setFirstname(String firstname) {
        this.firstname = firstname;
    }

    /**
     * Get lastname from user
     * @return lastname
     */
    public String getLastname() {
        return lastname;
    }

    /**
     * Set lastname of user
     */
    public void setLastname(String lastname) {
        this.lastname = lastname;
    }

    /**
     * Get email from user
     * @return email
     */
    public String getEmail() {
        return email;
    }

    /**
     * Set email of user
     */
    public void setEmail(String email) {
        this.email = email;
    }

    /**
     * Get password from user
     * @return password
     */
    public String getPassword() {
        return password;
    }

    /**
     * Set password of user
     */
    public void setPassword(String password) {
        this.password = password;
    }
}
