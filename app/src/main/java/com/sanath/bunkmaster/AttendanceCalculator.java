package com.sanath.bunkmaster;

import android.app.Activity;
import android.content.Context;
import android.database.Cursor;
import android.os.Bundle;

import androidx.multidex.MultiDex;

import com.sanath.bunkmaster.database.AttendanceDatabase;
import com.sanath.bunkmaster.database.SubjectDatabase;

/**
 * Created by Sanath on 22-01-2017.
 */

public class AttendanceCalculator extends Activity{

    AttendanceDatabase attendanceDatabase;
    SubjectDatabase subjectDatabase;
    PreferenceManager preferenceManager;

    @Override
    protected void attachBaseContext(Context newBase) {
        super.attachBaseContext(newBase);
        MultiDex.install(this);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //setContentView(R.layout.sunday);

        attendanceDatabase = new AttendanceDatabase(this);
        subjectDatabase = new SubjectDatabase(this);
        preferenceManager = new PreferenceManager(this);

        String dat = null;
        Bundle bundle = getIntent().getBundleExtra("Bundle");
        if(bundle != null)
            dat = bundle.getString("Date");
        //Get the current date
//        SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy", Locale.ENGLISH);
//        Calendar calendar = Calendar.getInstance();
//        String dat = dateFormat.format(calendar.getTime());

        //Get the subject taught that day(timetable) and attendance status(attended or bunked or free) of those subjects for that date
        attendanceDatabase.open();
        Cursor c = attendanceDatabase.getQueryResult("SELECT SUB, STATUS FROM DAY_ATTENDANCE WHERE DAT='"+ dat + "'");
        c.moveToFirst();

        for(int i=0; i<c.getCount(); i++)
        {
            String subject = c.getString(0);
            //Get the total number of classes held and number of classes attended for each subject
            if(!subject.equals("Free")) {
                Cursor c1 = attendanceDatabase.getQueryResult("SELECT TOTAL, ATTENDED FROM ATTENDANCE WHERE SUB='" + subject + "'");
                c1.moveToFirst();
                int total = c1.getInt(0);
                int attended = c1.getInt(1);
                c1.close();
                float percent;

                String status = c.getString(1);
                //Toast.makeText(AttendanceCalculator.this, subject + "\n" + status + "\n" + total + "\n" + attended, Toast.LENGTH_SHORT).show();

                //If attended increment total classes and classes attended for that subject and update in the database
                if (status.equals("A")) {
                    total++;
                    attended++;
                    percent = ((float) attended / total) * 100;
                    attendanceDatabase.updateAttendance(subject, total, attended, percent);
                }
                //If bunked increment total classes only for that subject and update in the database
                else if (status.equals("B")) {
                    total++;
                    percent = ((float) attended / total) * 100;
                    attendanceDatabase.updateAttendance(subject, total, attended, percent);
                }

                //If free do nothing. Hence nothing done :P
                //Toast.makeText(AttendanceCalculator.this, subject + "\n" + status + "\n" + total + "\n" + attended, Toast.LENGTH_SHORT).show();
                c.moveToNext();
            }
        }
        c.close();

        preferenceManager.setLastVisitDay(dat);
        //startActivity(new Intent(AttendanceCalculator.this, Overview.class));
        finish();
    }
}
