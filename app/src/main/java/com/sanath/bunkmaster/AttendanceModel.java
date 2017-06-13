package com.sanath.bunkmaster;

/**
 * Created by Sanath on 21-01-2017.
 */

public class AttendanceModel {

    String subject;
    int total, attended, percent;

    public AttendanceModel(String subject, int total, int attended, int percent) {

        this.subject = subject;
        this.total = total;
        this.attended = attended;
        this.percent = percent;
    }

    public String getSubject() {
        return subject;
    }

    public void setSubject(String subject) {
        this.subject = subject;
    }

    public int getTotal() {
        return total;
    }

    public void setTotal(int total) {
        this.total = total;
    }

    public int getAttended() {
        return attended;
    }

    public void setAttended(int attended) {
        this.attended = attended;
    }

    public int getPercent() {
        return percent;
    }

    public void setPercent(int percent) {
        this.percent = percent;
    }
}
