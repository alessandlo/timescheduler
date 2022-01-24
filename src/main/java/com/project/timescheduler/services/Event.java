package com.project.timescheduler.services;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.ArrayList;

public class Event {

    private String creator = "test";

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

    public Event(String name, String location, ArrayList<String> participants, LocalDate startDate, LocalDate endDate, LocalTime startTime, LocalTime endTime, Priority priority) {
        this.name = name;
        this.location = location;
        this.participants = participants;

        this.startDate = startDate;
        this.endDate = endDate;

        this.startTime = startTime;
        this.endTime = endTime;
    }

    public String getCreator(){
        return creator;
    }

    public void setCreator(String creator){
        this.creator = creator;
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

    public LocalDate getEndTime(){
        return endDate;
    }

    public void setEndDate(LocalTime endTime){
        this.endTime = endTime;
    }

}