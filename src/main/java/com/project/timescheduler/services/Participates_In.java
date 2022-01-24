package com.project.timescheduler.services;

public class Participates_In {
    private int event_ID;
    private String username;

    public Participates_In(int event_ID, String username) {
        this.event_ID = event_ID;
        this.username = username;
    }

    public int getEvent_ID() {
        return event_ID;
    }

    public void setEvent_ID(int event_ID) {
        this.event_ID = event_ID;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }
}
