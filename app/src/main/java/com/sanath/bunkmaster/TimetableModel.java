package com.sanath.bunkmaster;

/**
 * Created by Sanath on 13-11-2016.
 */

public class TimetableModel {

    String day, hour, subject;

    public TimetableModel(String day, String hour, String subject) {
        this.day = day;
        this.hour = hour;
        this.subject = subject;
    }

    public String getDay() {
        return day;
    }

    public void setDay(String day) {
        this.day = day;
    }

    public String getHour() {
        return hour;
    }

    public void setHour(String hour) {
        this.hour = hour;
    }

    public String getSubject() {
        return subject;
    }

    public void setSubject(String subject) {
        this.subject = subject;
    }
}
