package com.sanath.bunkmaster.infoentry;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.os.Handler;
import android.support.design.widget.Snackbar;
import android.support.multidex.MultiDex;
import android.text.InputType;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;

import com.sanath.bunkmaster.Help;
import com.sanath.bunkmaster.Overview;
import com.sanath.bunkmaster.PreferenceManager;
import com.sanath.bunkmaster.R;
import com.sanath.bunkmaster.database.AttendanceDatabase;
import com.sanath.bunkmaster.database.SubjectDatabase;

import java.util.ArrayList;

/**
 * Created by Sanath on 20-01-2017.
 */

public class StatusEntry extends Activity{

    //LinearLayout linearLayout;
    TableLayout linearLayout;
    ScrollView parent;
    TextView subject[], help;
    EditText total[], attended[];
    Button statusSave;

    PreferenceManager preferenceManager;
    SubjectDatabase subdb;
    AttendanceDatabase attenddb;
    ArrayList<String> subArray = new ArrayList<>();

    @Override
    protected void attachBaseContext(Context newBase) {
        super.attachBaseContext(newBase);
        MultiDex.install(this);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.status_entry);

        //linearLayout = (LinearLayout) findViewById(R.id.status_container);
        linearLayout = (TableLayout) findViewById(R.id.status_container);
        parent = (ScrollView) findViewById(R.id.status_parent);
        statusSave = (Button) findViewById(R.id.status_save);
        subdb = new SubjectDatabase(this);
        attenddb = new AttendanceDatabase(this);
        preferenceManager = new PreferenceManager(this);
        help = (TextView) findViewById(R.id.help_status);

        help.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(StatusEntry.this, Help.class));
            }
        });

        //LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
        //        LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT, 1);

        //Parameters for TableLayout and its rows
        TableLayout.LayoutParams params = new TableLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, 50);
        TableRow.LayoutParams rowParams = new TableRow.LayoutParams(0, TableLayout.LayoutParams.WRAP_CONTENT, 1);

        //Getting list of subjects
        subdb.open();
        final Cursor c1 = subdb.getQueryResult("SELECT * FROM SUBJECT");
        c1.moveToFirst();

        subArray.clear();
        for (int k = 0; k < c1.getCount(); k++) {

            String s = c1.getString(1);
            subArray.add(s);
            c1.moveToNext();
        }
        c1.close();
        subdb.close();

        subject = new TextView[subArray.size()];
        total = new EditText[subArray.size()];
        attended = new EditText[subArray.size()];

        for(int i=0; i<subArray.size(); i++)
        {
            //Initialise TextView and EditText arrays and TableRow
            subject[i] = new TextView(this);
            total[i] = new EditText(this);
            attended[i] = new EditText(this);

            //LinearLayout layout = new LinearLayout(this);
            TableRow layout = new TableRow(this);
            layout.setLayoutParams(params);
            layout.setOrientation(LinearLayout.HORIZONTAL);

            for(int j=1; j<=3; j++)
            {
                //First column
                if(j==1)
                {
                    subject[i].setLayoutParams(rowParams);
                    subject[i].setText(subArray.get(i));
                    subject[i].setPadding(5, 15, 5, 15);
                    subject[i].setTextSize(20);
                    subject[i].setGravity(Gravity.CENTER);
                    subject[i].setTextColor(getResources().getColor(R.color.colorAccent));
                    subject[i].setBackgroundResource(R.drawable.table_border);

                    layout.addView(subject[i]);
                }
                //Second Column
                else if(j==2)
                {
                    total[i].setLayoutParams(rowParams);
                    total[i].setPadding(5, 15, 5, 15);
                    total[i].setTextSize(20);
                    total[i].setInputType(InputType.TYPE_CLASS_NUMBER);
                    total[i].setGravity(Gravity.CENTER);
                    total[i].setTextColor(getResources().getColor(R.color.colorPrimary));
                    total[i].setBackgroundResource(R.drawable.table_border);

                    layout.addView(total[i]);
                }
                //Third Column
                else if(j==3)
                {
                    attended[i].setLayoutParams(rowParams);
                    attended[i].setPadding(5, 15, 5, 15);
                    attended[i].setTextSize(20);
                    attended[i].setInputType(InputType.TYPE_CLASS_NUMBER);
                    attended[i].setGravity(Gravity.CENTER);
                    attended[i].setTextColor(getResources().getColor(R.color.colorPrimary));
                    attended[i].setBackgroundResource(R.drawable.table_border);

                    layout.addView(attended[i]);
                }
            }
            linearLayout.addView(layout);
        }
        //Listener for the save button
        statusSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                
                if(checkValues())
                {
                    preferenceManager.setIsStatusEntryComplete(true);

                    attenddb.open();
                    //Truncate table because each time this activity is called its for entering a new Attendance status
                    attenddb.truncateTable("ATTENDANCE");
                    //attenddb.truncateTable("DAY_ATTENDANCE");
                    for (int i = 0; i < subArray.size(); i++) {
                        String s = subject[i].getText().toString();
                        int t = Integer.parseInt(total[i].getText().toString());
                        int a = Integer.parseInt(attended[i].getText().toString());
                        float p = ((float) a/t)*100;
                        attenddb.insertAttendance(s, t, a, p);
                        //Toast.makeText(StatusEntry.this, "" + s + "\n" + t + "\n" + a, Toast.LENGTH_SHORT).show();
                    }
                    attenddb.close();

                    Snackbar snackbar = Snackbar.make(parent, "Attendance Status Updated Successfully!",
                            Snackbar.LENGTH_SHORT);
                    snackbar.setDuration(1500);
                    snackbar.show();

                    //To delay launch of next activity
                    new Handler().postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            //Move to next activity
                            startActivity(new Intent(StatusEntry.this, Overview.class));
                            finish();
                        }
                    }, 1800);
                }

            }
        });

    }

    private boolean checkValues() {

        boolean check = true;

        for(int i=0; i<subArray.size(); i++)
        {
            if(attended[i].getText().toString().isEmpty())
            {
                attended[i].setError("Error Here");
                Snackbar snackbar = Snackbar.make(parent, "Please Enter Valid Values!",
                        Snackbar.LENGTH_SHORT);
                snackbar.setDuration(1500);
                snackbar.show();

                check = false;
            }

            if(total[i].getText().toString().isEmpty())
            {
                total[i].setError("Error Here");
                Snackbar snackbar = Snackbar.make(parent, "Please Enter Valid Values!",
                        Snackbar.LENGTH_SHORT);
                snackbar.setDuration(1500);
                snackbar.show();

                check = false;
            }

            //Entered value of attended is less than value of total
            if(Integer.parseInt(attended[i].getText().toString()) > Integer.parseInt(total[i].getText().toString()))
            {
                attended[i].setError("Error Here");
                Snackbar snackbar = Snackbar.make(parent, "Enter Valid Attended Value!",
                        Snackbar.LENGTH_SHORT);
                snackbar.setDuration(1500);
                snackbar.show();

                check = false;
            }
        }
        return check;
    }
}
