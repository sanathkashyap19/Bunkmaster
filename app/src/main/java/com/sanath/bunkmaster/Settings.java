package com.sanath.bunkmaster;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.design.widget.Snackbar;
import android.support.multidex.MultiDex;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.CheckBox;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.sanath.bunkmaster.database.AttendanceDatabase;
import com.sanath.bunkmaster.database.InfoDatabase;
import com.sanath.bunkmaster.database.SubjectDatabase;
import com.sanath.bunkmaster.database.TimetableDatabase;
import com.sanath.bunkmaster.infoentry.StatusEntry;

/**
 * Created by Sanath on 22-01-2017.
 */

public class Settings extends AppCompatActivity{

    LinearLayout linearLayout;
    RelativeLayout dailyReminder;
    TextView myDetails, clearTimetable, clearAttendance, clearAll, help;
    CheckBox remainderCheckbox;

    AttendanceDatabase attendanceDatabase;
    InfoDatabase infoDatabase;
    SubjectDatabase subjectDatabase;
    TimetableDatabase timetableDatabase;
    PreferenceManager preferenceManager;

    @Override
    protected void attachBaseContext(Context newBase) {
        super.attachBaseContext(newBase);
        MultiDex.install(this);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.settings);

        linearLayout = (LinearLayout) findViewById(R.id.settings_container);
        dailyReminder = (RelativeLayout) findViewById(R.id.daily_reminder);
        myDetails = (TextView) findViewById(R.id.my_details);
        clearTimetable = (TextView) findViewById(R.id.clear_timetable);
        clearAttendance = (TextView) findViewById(R.id.clear_attendance);
        clearAll = (TextView) findViewById(R.id.clear_all);
        help = (TextView) findViewById(R.id.help);
        remainderCheckbox = (CheckBox) findViewById(R.id.reminder_checkBox);

        attendanceDatabase = new AttendanceDatabase(this);
        infoDatabase = new InfoDatabase(this);
        subjectDatabase = new SubjectDatabase(this);
        timetableDatabase = new TimetableDatabase(this);
        preferenceManager = new PreferenceManager(this);

        if(preferenceManager.isDailyReminderSet())
            remainderCheckbox.setChecked(true);
        else
            remainderCheckbox.setChecked(false);

        myDetails.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                startActivity(new Intent(Settings.this, MyDetails.class));

//                FragmentManager fragmentManager = getSupportFragmentManager();
//                fragmentManager.popBackStackImmediate();
//                FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
//                fragmentTransaction.replace(R.id.settings_container, new MyDetails()).addToBackStack("Settings");
//                fragmentTransaction.commit();
            }
        });


        dailyReminder.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if(remainderCheckbox.isChecked())
                    remainderCheckbox.setChecked(false);
                else
                    remainderCheckbox.setChecked(true);
            }
        });

        clearTimetable.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                AlertDialog.Builder alert = new AlertDialog.Builder(Settings.this);
                alert.setMessage("Are you sure you want to delete your timetable?")
                        .setPositiveButton("CONFIRM", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {

                                timetableDatabase.truncateTable("TIMETABLE");

                                Snackbar snackbar = Snackbar.make(linearLayout, "Timetable Cleared Successfully",
                                        Snackbar.LENGTH_SHORT);
                                snackbar.setDuration(1500);
                                snackbar.show();

                                //To delay launch of next activity
                                new Handler().postDelayed(new Runnable() {
                                    @Override
                                    public void run() {
                                        //Move to next activity
                                        startActivity(new Intent(Settings.this, NewTimetable.class));
                                        finish();
                                    }
                                }, 1800);

                            }
                        })
                        .setNegativeButton("CANCEL", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {

                            }
                        })
                        .show();
            }
        });

        clearAttendance.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                AlertDialog.Builder alert1 = new AlertDialog.Builder(Settings.this);
                alert1.setMessage("WARNING: This will clear all your attendance record, including the record of everyday attendance! \n \n" +
                        "Are you sure you want to delete your attendance record?")
                        .setPositiveButton("CONFIRM", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {

                                preferenceManager.clearLastVisit();
                                attendanceDatabase.open();
                                attendanceDatabase.truncateTable("DAY_ATTENDANCE");
                                attendanceDatabase.close();

                                Snackbar snackbar = Snackbar.make(linearLayout, "Attendance Cleared Successfully",
                                        Snackbar.LENGTH_SHORT);
                                snackbar.setDuration(1500);
                                snackbar.show();

                                //To delay launch of next activity
                                new Handler().postDelayed(new Runnable() {
                                    @Override
                                    public void run() {
                                        //Move to next activity
                                        startActivity(new Intent(Settings.this, StatusEntry.class));
                                        finish();
                                    }
                                }, 1800);

                            }
                        })
                        .setNegativeButton("CANCEL", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {

                            }
                        })
                        .show();
            }
        });

        clearAll.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                AlertDialog.Builder alert2 = new AlertDialog.Builder(Settings.this);
                alert2.setMessage("WARNING: This will clear all your data and completely reset the app \n \n" +
                        " Are you sure you want to delete all your records?")
                        .setPositiveButton("CONFIRM", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {

                                preferenceManager.clearAll();
                                infoDatabase.open();
                                infoDatabase.truncateTable("BASIC_INFO");
                                subjectDatabase.open();
                                subjectDatabase.truncateTable("SUBJECT");
                                timetableDatabase.open();
                                timetableDatabase.truncateTable("TIMETABLE");
                                attendanceDatabase.open();
                                attendanceDatabase.truncateTable("ATTENDANCE");
                                attendanceDatabase.truncateTable("DAY_ATTENDANCE");

                                Snackbar snackbar = Snackbar.make(linearLayout, "Data Cleared Successfully!",
                                        Snackbar.LENGTH_SHORT);
                                snackbar.setDuration(1500);
                                snackbar.show();

                                //To delay launch of next activity
                                new Handler().postDelayed(new Runnable() {
                                    @Override
                                    public void run() {
                                        //Move to next activity
                                        startActivity(new Intent(Settings.this, WelcomeActivity.class));
                                        finish();
                                    }
                                }, 1800);

                            }
                        })
                        .setNegativeButton("CANCEL", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {

                            }
                        })
                        .show();
            }
        });

        help.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(Settings.this, Help.class));
            }
        });

        if(remainderCheckbox.isChecked())
            preferenceManager.setDailyReminder(true);
        else
            preferenceManager.setDailyReminder(false);
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();

        if(remainderCheckbox.isChecked())
            preferenceManager.setDailyReminder(true);
        else
            preferenceManager.setDailyReminder(false);
    }
}
