package com.sanath.bunkmaster;

import android.app.Activity;
import android.content.Context;
import android.database.Cursor;
import android.os.Bundle;
import android.text.Html;
import android.widget.TextView;

import androidx.multidex.MultiDex;

import com.sanath.bunkmaster.database.AttendanceDatabase;

/**
 * Created by Sanath on 22-01-2017.
 */
public class SubjectDetails extends Activity{

    TextView subject, total, attended, bunked, percent, available;

    AttendanceDatabase attendanceDatabase;
    PreferenceManager preferenceManager;

    @Override
    protected void attachBaseContext(Context newBase) {
        super.attachBaseContext(newBase);
        MultiDex.install(this);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.subject_details);

        subject = (TextView) findViewById(R.id.subject_name);
        total = (TextView) findViewById(R.id. total_display);
        attended = (TextView) findViewById(R.id. attended_display);
        bunked = (TextView) findViewById(R.id. bunked_display);
        percent = (TextView) findViewById(R.id. percent_display);
        available = (TextView) findViewById(R.id. available_attend);

        attendanceDatabase = new AttendanceDatabase(this);
        preferenceManager = new PreferenceManager(this);

        String sub = getIntent().getExtras().getString("Subject");
        //Toast.makeText(SubjectDetails.this, sub, Toast.LENGTH_SHORT).show();
        subject.setText(sub);

        //Get attendance details of the selected subject
        attendanceDatabase.open();
        Cursor c = attendanceDatabase.getQueryResult("SELECT TOTAL, ATTENDED, PERCENT FROM ATTENDANCE WHERE SUB='"+sub+"'");
        c.moveToFirst();

        int t = c.getInt(0);
        int a = c.getInt(1);
        int per = c.getInt(2);

        c.close();

        total.setText(""+t);
        attended.setText(""+a);
        percent.setText(""+per+"%");

        int bunk = t - a;
        bunked.setText(""+bunk);

        float p = per;
        int minPercent = preferenceManager.PercentAttendance();
        //If percentage of attendance is < min required percentage then calculate number of classes required to get it to that amount
        if(per < minPercent)
        {
            int i = 0;
            while (p < minPercent)
            {
                t++;
                a++;
                p = ((float) a/t)*100;
                i++;
            }
            String s = "<font color=#a9a9a9>Classes Required: </font><font color=#ff3300>"+i+"</font>";
            available.setText(Html.fromHtml(s));
            //available.setTextColor(Color.RED);
        }
        //If percentage of attendance is < min required percentage then calculate number of classes that can be safely bunked
        else if(per > minPercent)
        {
            int i = 0;
            while (p >= minPercent)
            {
                t++;
                p = ((float) a/t)*100;
                i++;
            }
            String s = "<font color=#a9a9a9>Bunks Available: </font><font color=#33cc33>"+(i-1)+"</font>";
            available.setText(Html.fromHtml(s));
        }
        else
        {
            available.setText("Just on the Border!");
            available.setTextColor(getResources().getColor(R.color.colorPrimary));
        }
    }
}
