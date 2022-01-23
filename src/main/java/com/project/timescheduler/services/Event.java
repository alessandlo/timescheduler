package com.project.timescheduler.services;

import java.time.LocalDate;

public class Event {

    public static Priority Priority;
    private String name;
    private String location;
    private String participants;

    private LocalDate date;
    private Priority priority;

    public enum Priority {
        high, medium, low;
    }


    public Event(String name, String location, String participants, LocalDate date, Priority priority) {
        this.name = name;
        this.location = location;
        this.participants = participants;

        this.date = date;
        this.priority = priority;
    }

    public Priority getPriority() {
        return priority;
    }

    public void setPriority(Priority priority) {
        this.priority = priority;
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

    public String getParticipants() {
        return participants;
    }

    public void setParticipants(String participants) {
        this.participants = participants;
    }

    public LocalDate getDate() {
        return date;
    }

    public void setDate(LocalDate date) {
        this.date = date;
    }

    public void printEvent(){
        System.out.println(name + "," +location + "," +participants + "," +date + "," +priority);
    }

}