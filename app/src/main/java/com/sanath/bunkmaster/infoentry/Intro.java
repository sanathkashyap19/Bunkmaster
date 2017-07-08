package com.sanath.bunkmaster.infoentry;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Vibrator;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.multidex.MultiDex;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.rokolabs.sdk.RokoMobi;
import com.rokolabs.sdk.analytics.Event;
import com.rokolabs.sdk.analytics.RokoLogger;
import com.sanath.bunkmaster.Help;
import com.sanath.bunkmaster.PreferenceManager;
import com.sanath.bunkmaster.R;
import com.sanath.bunkmaster.database.InfoDatabase;

public class Intro extends AppCompatActivity{

    EditText name, college, year, sem, percent;
    RelativeLayout layout;
    Button submit;
    FloatingActionButton help;

    InfoDatabase infoDatabase;
    PreferenceManager preferenceManager;

    @Override
    protected void attachBaseContext(Context newBase) {
        super.attachBaseContext(newBase);
        MultiDex.install(this);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.login_intro);

        preferenceManager = new PreferenceManager(this);
        infoDatabase = new InfoDatabase(this);

        name = (EditText) findViewById(R.id.ed_name);
        college = (EditText) findViewById(R.id.college);
        year = (EditText) findViewById(R.id.year);
        sem = (EditText) findViewById(R.id.sem);
        percent = (EditText) findViewById(R.id.percent);
        layout = (RelativeLayout) findViewById(R.id.intro_layout);
        submit = (Button) findViewById(R.id.submit);
        help = (FloatingActionButton) findViewById(R.id.help_info);

        help.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(Intro.this, Help.class));
            }
        });

        submit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                boolean check = checkValues();

                if(check)
                {
                    //Enter data to database
                    infoDatabase.open();

                    //Get data from the screen
                    String n = name.getText().toString();
                    String c = college.getText().toString();
                    int s = Integer.parseInt(sem.getText().toString());
                    int y = Integer.parseInt(year.getText().toString());
                    int p = Integer.parseInt(percent.getText().toString());

                    infoDatabase.insertInfo(n, c, s, y);
                    //infoDatabase.insertNumbers(0, p, 0, 0);
                    preferenceManager.setPercentAttendance(p);

                    infoDatabase.close();

                    preferenceManager.setIntroComplete(true);

                    Snackbar snackbar = Snackbar.make(layout, "Info Entered Successfully!",
                            Snackbar.LENGTH_SHORT);
                    snackbar.setDuration(1500);
                    snackbar.show();

                    //To delay launch of next activity
                    new Handler().postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            startActivity(new Intent(Intro.this, SubjectEntry.class));
                            finish();
                        }
                    }, 1800);
                }

                else
                {
                    //Vibrates for 100ms
                    Vibrator vib = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE);
                    vib.vibrate(100);
                }

            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        return super.onOptionsItemSelected(item);
    }

    //Check if all entered values are proper
    private boolean checkValues() {

        boolean check = true;

        if(name.getText().toString().isEmpty())
        {
            name.setError("Please enter name");
            Snackbar snackbar = Snackbar.make(layout, "Please enter your name!",
                    Snackbar.LENGTH_SHORT);
            snackbar.setDuration(1500);
            snackbar.show();
            check = false;
        }

        if(college.getText().toString().isEmpty())
        {
            college.setError("Please enter college name");
            Snackbar snackbar = Snackbar.make(layout, "Please enter college name!",
                    Snackbar.LENGTH_SHORT);
            snackbar.setDuration(1500);
            snackbar.show();
            check = false;
        }

        if(year.getText().toString().isEmpty() || (Integer.parseInt(year.getText().toString())>6))
        {
            year.setError("Please enter year you are studying in");
            Snackbar snackbar = Snackbar.make(layout, "Please enter year you are studying in!",
                    Snackbar.LENGTH_SHORT);
            snackbar.setDuration(1500);
            snackbar.show();
            check = false;
        }

        if(sem.getText().toString().isEmpty() || (Integer.parseInt(year.getText().toString())>12))
        {
            sem.setError("Please enter semester you are studying in");
            Snackbar snackbar = Snackbar.make(layout, "Please enter semester you are studying in!",
                    Snackbar.LENGTH_SHORT);
            snackbar.setDuration(1500);
            snackbar.show();
            check = false;
        }

        if(percent.getText().toString().isEmpty())
        {
            percent.setError("Please enter min percentage of attendance required");
            Snackbar snackbar = Snackbar.make(layout, "Please enter min percentage of attendance required!",
                    Snackbar.LENGTH_SHORT);
            snackbar.setDuration(1500);
            snackbar.show();
            check = false;
        }
        return check;
    }
}
