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

    private Priority priority;

    public enum Priority {
        high, medium, low;
    }

    public Event(int event_id, String creatorName, String name, String location, ArrayList<String> participants, LocalDate startDate, LocalDate endDate, LocalTime startTime, LocalTime endTime, Priority priority) {
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
    }

    public int getEventId() {
        return eventId;
    }

    public void setEventId(int event_id) {
        this.eventId = event_id;
    }

    public String getCreatorName(){
        return creatorName;
    }

    public void setCreatorName(String creator){
        this.creatorName = creator;
    }

    public Priority getPriority() {
        return priority;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public ArrayList<String> getParticipants() {
        return participants;
    }

    public void setParticipants(ArrayList<String> participants){
        this.participants.clear();
        this.participants.addAll(participants);
    }

    public LocalDate getStartDate(){
        return startDate;
    }

    public void setStartDate(LocalDate startDate){
        this.startDate = startDate;
    }

    public LocalDate getEndDate(){
        return endDate;
    }

    public void setEndDate(LocalDate endDate){
        this.endDate = endDate;
    }

    public LocalTime getStartTime(){
        return startTime;
    }

    public void setStartTime(LocalTime startTime){
        this.startTime = startTime;
    }

    public LocalTime getEndTime(){
        return endTime;
    }

    public void setEndDate(LocalTime endTime){
        this.endTime = endTime;
    }



}