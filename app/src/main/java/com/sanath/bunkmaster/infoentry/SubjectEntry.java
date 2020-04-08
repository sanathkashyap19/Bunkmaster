package com.sanath.bunkmaster.infoentry;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.os.Vibrator;
import android.text.InputType;
import android.view.Gravity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.multidex.MultiDex;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;
import com.sanath.bunkmaster.Help;
import com.sanath.bunkmaster.PreferenceManager;
import com.sanath.bunkmaster.R;
import com.sanath.bunkmaster.database.SubjectDatabase;

public class SubjectEntry extends AppCompatActivity {

    LinearLayout l;
    EditText sub, percent;
    EditText et[];
    FloatingActionButton help;
    Button no_enter, sub_enter;
    int sub_num;

    SubjectDatabase subdb;
    //InfoDatabase infodb;
    PreferenceManager preferenceManager;

    @Override
    protected void attachBaseContext(Context newBase) {
        super.attachBaseContext(newBase);
        MultiDex.install(this);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.subject_entry);

        subdb = new SubjectDatabase(this);
        //infodb = new InfoDatabase(this);
        preferenceManager = new PreferenceManager(this);

        l = (LinearLayout) findViewById(R.id.sub_entry_layout);
        sub = (EditText) findViewById(R.id.ed_subject);
        percent = (EditText) findViewById(R.id.percent);
        no_enter = (Button) findViewById(R.id.sub_enter);
        help = (FloatingActionButton) findViewById(R.id.help_sub);

        help.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(SubjectEntry.this, Help.class));
            }
        });

        no_enter.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if(check()) {

                    //Get minimum percentage of attendance
                    int p = Integer.parseInt(percent.getText().toString());
                    preferenceManager.setPercentAttendance(p);

                    no_enter.setVisibility(View.INVISIBLE);

                    //Get number of subjects
                    String no_sub = sub.getText().toString();

                    sub_num = Integer.parseInt(no_sub);

                    et = new EditText[sub_num];

                    //To generate matrix of text view and edit text to enter the subject names
                    for (int i = 0; i < sub_num; i++) {
                        LinearLayout layout = new LinearLayout(SubjectEntry.this);
                        layout.setOrientation(LinearLayout.HORIZONTAL);

                        for (int j = 0; j < 2; j++) {

                            if (j == 0) {
                                TextView text = new TextView(SubjectEntry.this);
                                LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(
                                        LinearLayout.LayoutParams.WRAP_CONTENT,
                                        LinearLayout.LayoutParams.WRAP_CONTENT);
                                lp.setMargins(10, 10, 10, 10);
                                text.setLayoutParams(lp);

                                text.setText("Subject " + (i + 1));
                                text.setTextColor(getResources().getColor(R.color.colorPrimary));
                                text.setTextSize(20);
                                text.setPadding(5, 5, 5, 5);

                                layout.addView(text);
                            }
                            else if (j == 1) {
                                et[i] = new EditText(SubjectEntry.this);
                                LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(
                                        LinearLayout.LayoutParams.WRAP_CONTENT,
                                        LinearLayout.LayoutParams.WRAP_CONTENT);
                                lp.setMargins(10, 10, 10, 10);
                                et[i].setLayoutParams(lp);

                                et[i].setHint("Subject " + (i + 1));
                                et[i].setHintTextColor(getResources().getColor(R.color.colorAccent));
                                et[i].setEms(10);
                                et[i].setInputType(InputType.TYPE_TEXT_VARIATION_PERSON_NAME);
                                et[i].setBackground(getResources().getDrawable(R.drawable.ed_border));
                                et[i].setPadding(5, 5, 5, 5);
                                et[i].setTextSize(20);
                                et[i].setTextColor(Color.WHITE);

                                layout.addView(et[i]);
                            }
                        }

                        l.addView(layout);
                    }

                    //Generating button dynamically as well
                    sub_enter = new Button(SubjectEntry.this);
                    LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT,
                            LinearLayout.LayoutParams.WRAP_CONTENT);
                    lp.setMargins(10, 10, 10, 10);
                    lp.gravity = Gravity.END;
                    sub_enter.setLayoutParams(lp);

                    sub_enter.setText("Submit");
                    sub_enter.setPadding(10, 10, 10, 10);
                    sub_enter.setTextSize(18);

                    l.addView(sub_enter);

                    sub_enter.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {

                            boolean check = checkValues();

                            if (check) {

                                subdb.open();

                                //Insert values into database
                                for (int i = 0; i < sub_num; i++) {
                                    String s = et[i].getText().toString();
                                    subdb.insertSubject(s);
                                    //Toast.makeText(SubjectEntry.this, "" + id, Toast.LENGTH_SHORT).show();
                                }

                                subdb.close();

                                //infodb.open();
                                //infodb.updateSubject(sub_num, "1");

                                preferenceManager.setNoOfSubjects(sub_num);

                                preferenceManager.setSubjectEntryComplete(true);

                                Snackbar snackbar = Snackbar.make(l, "Subjects Entered Successfully!",
                                        Snackbar.LENGTH_SHORT);
                                snackbar.setDuration(1500);
                                snackbar.show();

                                new Handler().postDelayed(new Runnable() {
                                    @Override
                                    public void run() {
                                        //Move to next activity
                                        startActivity(new Intent(SubjectEntry.this, TimetableEntry.class));
                                        finish();
                                    }
                                }, 1800);

                            }
                            else {
                                //Vibrates for 100ms
                                Vibrator vib = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE);
                                vib.vibrate(100);
                            }
                        }
                    });
                }
            }
        });
    }

    private boolean check() {

        boolean ch = true;

        if(sub.getText().toString().isEmpty())
        {
            sub.setError("Please Enter Your Subjects!");
            Snackbar snackbar = Snackbar.make(l, "Please Enter Your Subjects!",
                    Snackbar.LENGTH_SHORT);
            snackbar.setDuration(1500);
            snackbar.show();
            ch = false;
        }

        return ch;
    }

    private boolean checkValues() {

        boolean check = true;

        for(int i=0; i<sub_num; i++)
        {
            if(et[i].getText().toString().isEmpty())
            {
                et[i].setError("Please enter the subjects you are studying");
                check = false;
            }
        }

        return check;
    }
}