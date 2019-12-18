package ru.smartcity.models;

import androidx.annotation.NonNull;

public class Comment {

    private String email;
    private String text;
    private String dateTime;

    public Comment() {

    }

    public Comment(String email, String dateTime, String text) {
        this.email = email;
        this.dateTime = dateTime;
        this.text = text;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public String getDateTime() {
        return dateTime;
    }

    public void setDateTime(String dateTime) {
        this.dateTime = dateTime;
    }

    @NonNull
    @Override
    public String toString() {
        return email + " " + dateTime + " " + text;
    }
}
