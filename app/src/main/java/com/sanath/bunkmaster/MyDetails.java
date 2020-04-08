package com.sanath.bunkmaster;

import android.content.Context;
import android.database.Cursor;
import android.os.Bundle;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.multidex.MultiDex;

import com.sanath.bunkmaster.database.InfoDatabase;

/**
 * Created by Sanath on 28-01-2017.
 */
public class MyDetails extends AppCompatActivity {

    TextView name, college, sem, year, subjects, hours, days, percent;

    InfoDatabase infoDatabase;
    PreferenceManager preferenceManager;

    @Override
    protected void attachBaseContext(Context newBase) {
        super.attachBaseContext(newBase);
        MultiDex.install(this);
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.my_details);

        name = (TextView) findViewById(R.id.user_name);
        college = (TextView) findViewById(R.id.user_college);
        sem = (TextView) findViewById(R.id.user_semester);
        year = (TextView) findViewById(R.id.user_year);
        subjects = (TextView) findViewById(R.id.user_subjects);
        hours = (TextView) findViewById(R.id.user_hours);
        days = (TextView) findViewById(R.id.user_days);
        percent = (TextView) findViewById(R.id.user_percent);

        infoDatabase = new InfoDatabase(this);
        preferenceManager = new PreferenceManager(this);

        infoDatabase.open();
        Cursor c = infoDatabase.getQueryResult("SELECT * FROM BASIC_INFO");
        c.moveToFirst();

        name.setText(c.getString(1));
        college.setText(c.getString(2));
        sem.setText(c.getString(3));
        year.setText(c.getString(4));

        subjects.setText(""+preferenceManager.NoOfSubjects());
        hours.setText(""+preferenceManager.HoursPerDay());
        days.setText(""+preferenceManager.DaysPerWeek());
        percent.setText(""+preferenceManager.PercentAttendance());

        c.close();
    }
}
