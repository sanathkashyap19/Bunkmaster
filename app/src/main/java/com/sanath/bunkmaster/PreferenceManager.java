package com.sanath.bunkmaster;

import android.content.Context;
import android.content.SharedPreferences;

/**
 * Created by Sanath on 30-11-2016.
 */

public class PreferenceManager {

    SharedPreferences pref;
    SharedPreferences.Editor editor;
    Context context;

    // shared pref mode
    int PRIVATE_MODE = 0;

    // Shared preferences file name
    private static final String PREF_NAME = "Welcome to Bunktracker";

    private static final String IS_FIRST_TIME_LAUNCH = "IsFirstTimeLaunch";
    private static final String IS_INTRO_COMPLETE = "IsIntroComplete";
    private static final String IS_SUBJECT_ENTRY_COMPLETE = "IsSubjectEntryComplete";
    private static final String IS_TIMETABLE_ENTRY_COMPLETE = "IsTimetableEntryComplete";
    private static final String IS_STATUS_ENTRY_COMPLETE = "IsStatusEntryComplete";
    private static final String HOURS_PER_DAY = "HoursPerDay";
    private static final String LAST_VISIT_DAY = "LastVisitDay";
    private static final String DAYS_PER_WEEK = "DaysPerWeek";
    private static final String NO_OF_SUBJECTS = "NoOfSubjects";
    private static final String PERCENT_ATTENDANCE = "MinPercantageOfAttendance";
    private static final String DAILY_REMINDER = "DailyReminder";

    public PreferenceManager(Context context) {
        this.context = context;
        pref = this.context.getSharedPreferences(PREF_NAME, PRIVATE_MODE);
        editor = pref.edit();
    }

    public void setLastVisitDay(String lastVisitDay) {

        editor.putString(LAST_VISIT_DAY, lastVisitDay);
        editor.commit();
    }

    public String LastVisitDay() {
        return pref.getString(LAST_VISIT_DAY, " ");
    }

    public void clearLastVisit()
    {
        editor.remove(LAST_VISIT_DAY);
        editor.commit();
    }

    public void setNoOfSubjects(int noOfSubjects) {

        editor.putInt(NO_OF_SUBJECTS, noOfSubjects);
        editor.commit();
    }

    public int NoOfSubjects() {
        return pref.getInt(NO_OF_SUBJECTS, 0);
    }

    public void setHoursPerDay(int hoursPerDay) {

        editor.putInt(HOURS_PER_DAY, hoursPerDay);
        editor.commit();
    }

    public int HoursPerDay() {
        return pref.getInt(HOURS_PER_DAY, 0);
    }

    public void setDaysPerWeek(int daysPerWeek) {

        editor.putInt(DAYS_PER_WEEK, daysPerWeek);
        editor.commit();
    }

    public int DaysPerWeek() {
        return pref.getInt(DAYS_PER_WEEK, 0);
    }

    public void setPercentAttendance(int percentAttendance) {

        editor.putInt(PERCENT_ATTENDANCE, percentAttendance);
        editor.commit();
    }

    public int PercentAttendance() {
        return pref.getInt(PERCENT_ATTENDANCE, 0);
    }

    public void setFirstTimeLaunch(boolean isFirstTime) {

        editor.putBoolean(IS_FIRST_TIME_LAUNCH, isFirstTime);
        editor.commit();
    }

    public boolean isFirstTimeLaunch() {
        return pref.getBoolean(IS_FIRST_TIME_LAUNCH, true);
    }

    public boolean isIntroComplete()
    {
        return pref.getBoolean(IS_INTRO_COMPLETE, false);
    }

    public void setIntroComplete(boolean introComplete) {

        editor.putBoolean(IS_INTRO_COMPLETE, introComplete);
        editor.commit();
    }

    public boolean isSubjectEntryComplete()
    {
        return pref.getBoolean(IS_SUBJECT_ENTRY_COMPLETE, false);
    }

    public void setSubjectEntryComplete(boolean subjectEntryComplete) {

        editor.putBoolean(IS_SUBJECT_ENTRY_COMPLETE, subjectEntryComplete);
        editor.commit();
    }

    public boolean isTimetableEntryComplete()
    {
        return pref.getBoolean(IS_TIMETABLE_ENTRY_COMPLETE, false);
    }

    public void setTimetableEntryComplete(boolean timetableEntryComplete) {

        editor.putBoolean(IS_TIMETABLE_ENTRY_COMPLETE, timetableEntryComplete);
        editor.commit();
    }

    public boolean isStatusEntryComplete()
    {
        return pref.getBoolean(IS_STATUS_ENTRY_COMPLETE, false);
    }

    public void setIsStatusEntryComplete(boolean statusEntryComplete) {

        editor.putBoolean(IS_STATUS_ENTRY_COMPLETE, statusEntryComplete);
        editor.commit();
    }

    public boolean isDailyReminderSet()
    {
        return pref.getBoolean(DAILY_REMINDER, true);
    }

    public void setDailyReminder(boolean dailyReminder) {

        editor.putBoolean(DAILY_REMINDER, dailyReminder);
        editor.commit();
    }

    public void clearAll()
    {
        editor.clear();
        editor.commit();
    }
}