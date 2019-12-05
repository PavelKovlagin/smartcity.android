package ru.smartcity.models;

import androidx.annotation.NonNull;

import java.text.SimpleDateFormat;
import java.util.Date;

public class Event {

    private int event_id;
    private String eventName;
    private String eventDescription;
    private double latitude;
    private double longitude;
    private String event_date;
    private String dateChange;
    private int status_id;
    private String statusName;
    private int user_id;
    private String email;

    public int getEvent_id() {
        return event_id;
    }

    public void setEvent_id(int event_id) {
        this.event_id = event_id;
    }

    public String getEventName() {
        return eventName;
    }

    public void setEventName(String eventName) {
        this.eventName = eventName;
    }

    public String getEventDescription() {
        return eventDescription;
    }

    public void setEventDescription(String eventDescription) {
        this.eventDescription = eventDescription;
    }

    public double getLatitude() {
        return latitude;
    }

    public void setLatitude(double latitude) {
        this.latitude = latitude;
    }

    public double getLongitude() {
        return longitude;
    }

    public void setLongitude(double longitude) {
        this.longitude = longitude;
    }

    public String getEvent_date() {
        return event_date;
    }

    public void setEvent_date(String event_date) {

        this.event_date = event_date;
    }

    public String getDateChange() {
        return dateChange;
    }

    public void setDateChange(String dateChange) {
        this.dateChange = dateChange;
    }

    public int getStatus_id() {
        return status_id;
    }

    public void setStatus_id(int status_id) {
        this.status_id = status_id;
    }

    public String getStatusName() {
        return statusName;
    }

    public void setStatusName(String statusName) {
        this.statusName = statusName;
    }

    public int getUser_id() {
        return user_id;
    }

    public void setUser_id(int user_id) {
        this.user_id = user_id;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public Event () {

    }

    public Event (String eventName, String eventDescription, double latitude, double longitude,
                  int status_id, String statusName, int user_id, String email) {
        this.eventName = eventName;
        this.eventDescription = eventDescription;
        this.latitude = latitude;
        this.longitude = longitude;
        Date currentDate = new Date();
        this.event_date = new SimpleDateFormat("yyyy-MM-dd").format(currentDate);
        this.dateChange = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(currentDate);
        this.status_id = status_id;
        this.statusName = statusName;
        this.user_id = user_id;
        this.email = email;
    }

    @NonNull
    @Override
    public String toString() {
        return this.event_id + " "
                + this.eventName + " "
                + this.eventDescription + " "
                + this.latitude + " "
                + this.longitude + " "
                + this.event_date + " "
                + dateChange + " "
                + this.status_id + " "
                + this.statusName + " "
                + this.user_id + " "
                + this.email;
    }
}
