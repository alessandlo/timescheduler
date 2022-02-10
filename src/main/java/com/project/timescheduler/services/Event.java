package com.project.timescheduler.services;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.ArrayList;

public class Event {

    private String creatorName;
    private int eventId;

    private String name;
    private String location;
    private ArrayList<String> participants;

    private LocalDate startDate;
    private LocalDate endDate;

    private LocalTime startTime;
    private LocalTime endTime;

    private long reminder;

    private Priority priority;

    public enum Priority {
        high, medium, low
    }

    /**
     * Constructor, when called creates an object of Event
     * @param event_id event ID
     * @param creatorName name of the event creator
     * @param name event name
     * @param location location of the event
     * @param participants List of all participants
     * @param startDate date of the start of the event
     * @param endDate date of the end of the event
     * @param startTime time of the start of the event
     * @param endTime time of the end of the event
     * @param priority priority of the event
     * @param reminder time until the reminder in milliseconds
     */
    public Event(int event_id, String creatorName, String name, String location, ArrayList<String> participants, LocalDate startDate, LocalDate endDate, LocalTime startTime, LocalTime endTime, Priority priority, long reminder) {
        this.eventId = event_id;
        this.creatorName = creatorName;

        this.name = name;
        this.location = location;
        this.participants = participants;

        this.startDate = startDate;
        this.endDate = endDate;

        this.startTime = startTime;
        this.endTime = endTime;

        this.priority = priority;
        this.reminder = reminder;
    }

    /**
     * gets the eventID
     * @return eventId
     */
    public int getEventId() {
        return eventId;
    }

    /**
     * sets the eventId
     * @param event_id id of the event
     */
    public void setEventId(int event_id) {
        this.eventId = event_id;
    }

    /**
     * gets the name of the event creator
     * @return creator name
     */
    public String getCreatorName(){
        return creatorName;
    }

    /**
     * sets the creator name of the event
     * @param creator creator name
     */
    public void setCreatorName(String creator){
        this.creatorName = creator;
    }

    /**
     * gets the priority of the event
     * @return priority
     */
    public Priority getPriority() {
        return priority;
    }

    /**
     * gets the name of the event
     * @return event name
     */
    public String getName() {
        return name;
    }

    /**
     * sets the event name
     * @param name name of the event
     */
    public void setName(String name) {
        this.name = name;
    }

    /**
     * gets location the of the event
     * @return location
     */
    public String getLocation() {
        return location;
    }

    /**
     * sets the event location
     * @param location event location
     */
    public void setLocation(String location) {
        this.location = location;
    }

    /**
     * gets the participants of the event
     * @return all participants
     */
    public ArrayList<String> getParticipants() {
        return participants;
    }

    /**
     * sets participants of the event
     * @param participants participants
     */
    public void setParticipants(ArrayList<String> participants){
        this.participants.clear();
        this.participants.addAll(participants);
    }

    /**
     * gets the start date of the event
     * @return start date
     */
    public LocalDate getStartDate(){
        return startDate;
    }

    /**
     * sets the start date of the event
     * @param startDate start date
     */
    public void setStartDate(LocalDate startDate){
        this.startDate = startDate;
    }

    /**
     * gets the end date of the event
     * @return end date
     */
    public LocalDate getEndDate(){
        return endDate;
    }

    /**
     * sets the end date of the event
     * @param endDate end date
     */
    public void setEndDate(LocalDate endDate){
        this.endDate = endDate;
    }

    /**
     * gets the start time of the event
     * @return start time
     */
    public LocalTime getStartTime(){
        return startTime;
    }

    /**
     * sets start time of the event
     * @param startTime start time
     */
    public void setStartTime(LocalTime startTime){
        this.startTime = startTime;
    }

    /**
     * gets end time of the event
     * @return end time
     */
    public LocalTime getEndTime(){
        return endTime;
    }

    /**
     * sets end time of the event
     * @param endTime end time
     */
    public void setEndTime(LocalTime endTime){
        this.endTime = endTime;
    }

    /**
     * gets reminder of the event
     * @return reminder in milliseconds
     */
    public long getReminder() {
        return reminder;
    }

    /**
     * sets reminder of the event
     * @param reminder reminder in milliseconds
     */
    public void setReminder(long reminder) {
        this.reminder = reminder;
    }

    /**
     * converts time from reminder into string
     * @return reminder as a string
     */
    public String getReminderString() {
        String reminderString;

        if(reminder == 604800000) {
            reminderString = "1 week";
        } else if (reminder == 259200000) {
            reminderString = "3 days";
        } else if (reminder == 3600000) {
            reminderString = "1 hour";
        } else if (reminder == 600000) {
            reminderString = "10 minutes";
        } else {
            reminderString = "fehler";
        }
        return reminderString;
    }
}