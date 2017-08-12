package com.sanath.bunkmaster.infoentry;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Vibrator;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.design.widget.TabLayout;
import android.support.multidex.MultiDex;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TableLayout;
import android.widget.TextView;

import com.sanath.bunkmaster.Help;
import com.sanath.bunkmaster.PreferenceManager;
import com.sanath.bunkmaster.R;
import com.sanath.bunkmaster.TimetableInterface;
import com.sanath.bunkmaster.TimetableModel;
import com.sanath.bunkmaster.database.SubjectDatabase;
import com.sanath.bunkmaster.database.TimetableDatabase;
import com.sanath.bunkmaster.entertimetable.Friday;
import com.sanath.bunkmaster.entertimetable.Monday;
import com.sanath.bunkmaster.entertimetable.Saturday;
import com.sanath.bunkmaster.entertimetable.Thursday;
import com.sanath.bunkmaster.entertimetable.Tuesday;
import com.sanath.bunkmaster.entertimetable.Wednesday;

import java.util.ArrayList;
import java.util.List;

public class TimetableEntry extends AppCompatActivity implements TimetableInterface.whenDone {

    TextView subjectCol1[] ,subjectCol2[], subjectCol3[];
    EditText working_days, hours_days;
    Button submit;
    FloatingActionButton help;
    TableLayout tableLayout;

    TimetableDatabase timedb;
    SubjectDatabase subdb;
    //InfoDatabase infodb;

    ArrayList<TimetableModel> arrayList = new ArrayList<>();
    ArrayList<String> subArray = new ArrayList<>();

    PreferenceManager preferenceManager;

    ViewPager viewPager;
    TabLayout tabLayout;

    @Override
    protected void attachBaseContext(Context newBase) {
        super.attachBaseContext(newBase);
        MultiDex.install(this);
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.timetable_entry);

        subdb = new SubjectDatabase(this);
        timedb = new TimetableDatabase(this);
        //infodb = new InfoDatabase(this);
        preferenceManager = new PreferenceManager(this);

        working_days = (EditText) findViewById(R.id.ed_weekdays);
        hours_days = (EditText) findViewById(R.id.ed_hours_per_day);
        tableLayout = (TableLayout) findViewById(R.id.subject_container);
        submit = (Button) findViewById(R.id.work_hours_enter);
        help = (FloatingActionButton) findViewById(R.id.help_time);

        help.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(TimetableEntry.this, Help.class));
            }
        });

        //Button that accepts working hours and hours per day
        submit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                boolean check = checkValues();

                if (check) {

                    //Change view to the time table view
                    working_days.setVisibility(View.GONE);
                    hours_days.setVisibility(View.GONE);

                    int h = Integer.parseInt(hours_days.getText().toString());
                    int w= Integer.parseInt(working_days.getText().toString());

                    preferenceManager.setHoursPerDay(h);
                    preferenceManager.setDaysPerWeek(w);
                    //infodb.updateDaysnHours(h, w, "1");
                    submit.setVisibility(View.GONE);

                    //Sets up the timetable days tab layout
                    genTimeTable();

                } else {
                    //Vibrates for 100ms
                    Vibrator vib = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE);
                    vib.vibrate(100);
                }

            }
        });
    }

    public void genTimeTable() {

        //Get values from edit text
        int work_days = Integer.parseInt(working_days.getText().toString());
        int work_hours = Integer.parseInt(hours_days.getText().toString());

        //Change layout to tab layout
        setContentView(R.layout.tab_timetable);

        viewPager = (ViewPager) findViewById(R.id.viewpager);
        tabLayout = (TabLayout) findViewById(R.id.tabs);

        Bundle bundle = new Bundle();
        bundle.putInt("hours", work_hours);

        //Check for correct values of working days
        if (work_days == 5) {
            setupViewPagerFor5(viewPager);
            tabLayout.setupWithViewPager(viewPager);
        } else if (work_days == 6) {
            setupViewPagerFor6(viewPager);
            tabLayout.setupWithViewPager(viewPager);
        } else
            working_days.setError("Please enter correct value!");
    }

    //If 5 days a week
    public void setupViewPagerFor5(ViewPager viewPager) {

        ViewPagerAdapter adapter = new ViewPagerAdapter(getSupportFragmentManager());
        adapter.addFragment(new Monday(this), "MONDAY");
        adapter.addFragment(new Tuesday(this), "TUESDAY");
        adapter.addFragment(new Wednesday(this), "WEDNESDAY");
        adapter.addFragment(new Thursday(this), "THURSDAY");
        adapter.addFragment(new Friday(this), "FRIDAY");
        viewPager.setAdapter(adapter);
    }

    //If 6 days a week
    public void setupViewPagerFor6(ViewPager viewPager) {

        ViewPagerAdapter adapter = new ViewPagerAdapter(getSupportFragmentManager());
        adapter.addFragment(new Monday(this), "MONDAY");
        adapter.addFragment(new Tuesday(this), "TUESDAY");
        adapter.addFragment(new Wednesday(this), "WEDNESDAY");
        adapter.addFragment(new Thursday(this), "THURSDAY");
        adapter.addFragment(new Friday(this), "FRIDAY");
        adapter.addFragment(new Saturday(this), "SATURDAY");
        viewPager.setAdapter(adapter);
    }

    //Get values from fragment using interface class
    @Override
    public void getSpinnerArray(String pageTitle, String hour, String subject) {

        TimetableModel timetableModel = new TimetableModel(pageTitle, hour, subject);
        arrayList.add(timetableModel);
    }

    public class ViewPagerAdapter extends FragmentStatePagerAdapter {

        final List<Fragment> mFragmentList = new ArrayList<>();
        final List<String> mFragmentTitleList = new ArrayList<>();

        ViewPagerAdapter(FragmentManager manager) {
            super(manager);
        }

        @Override
        public Fragment getItem(int position) {
            return mFragmentList.get(position);
        }

        @Override
        public int getCount() {
            return mFragmentList.size();
        }

        void addFragment(Fragment fragment, String title) {
            mFragmentList.add(fragment);
            mFragmentTitleList.add(title);
        }

        @Override
        public CharSequence getPageTitle(int position) {
            return mFragmentTitleList.get(position);
        }
    }

    //Check values of working days and hours per day
    public boolean checkValues() {

        boolean check = true;

        if (working_days.getText().toString().isEmpty() ||
                Integer.parseInt(working_days.getText().toString()) > 6 || Integer.parseInt(working_days.getText().toString()) < 5) {
            working_days.setError("Please Enter a Valid Number of Working Days!");
            check = false;
        }

        if (hours_days.getText().toString().isEmpty() ||
                Integer.parseInt(hours_days.getText().toString()) > 11 || Integer.parseInt(hours_days.getText().toString()) < 5) {
            hours_days.setError("Please Enter a Valid Number of Working Hours!");
            check = false;
        }

        return check;
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

        getMenuInflater().inflate(R.menu.add_timetable, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        switch (item.getItemId()) {
            case R.id.ic_tick:

                //Alert Dialog to check if they are sure about their timetable
                AlertDialog.Builder alert = new AlertDialog.Builder(TimetableEntry.this);
                alert.setMessage("Are you sure you want to confirm and save your timetable?")
                        .setPositiveButton("CONFIRM", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {

                                //To catch exception when they confirm without swiping through all the tabs
                                try {
                                    if (getTimeTable()) {

                                        timedb.open();

                                        for (i = 0; i < arrayList.size(); i++) {

                                            TimetableModel model = arrayList.get(i);
                                            timedb.insertTimetable(model.getDay(), model.getHour(), model.getSubject());
                                            //Toast.makeText(TimetableEntry.this, "" + model.getDay() + "\n" + model.getHour() +
                                            //        "\n" + model.getSubject(), Toast.LENGTH_SHORT).show();

                                        }

                                        timedb.close();
                                        preferenceManager.setTimetableEntryComplete(true);

                                        Snackbar snackbar = Snackbar.make(tabLayout, "Timetable Entered Successfully",
                                                Snackbar.LENGTH_SHORT);
                                        snackbar.setDuration(1500);
                                        snackbar.show();

                                        //To delay launch of next activity
                                        new Handler().postDelayed(new Runnable() {
                                            @Override
                                            public void run() {
                                                //Move to next activity
                                                startActivity(new Intent(TimetableEntry.this, StatusEntry.class));
                                                finish();
                                            }
                                        }, 1800);

                                    }
                                }
                                catch (Exception e)
                                {
                                    Log.e("T : ", e.toString());
                                    Snackbar snackbar = Snackbar.make(tabLayout, "Please enter the timetable for all days!",
                                            Snackbar.LENGTH_SHORT);
                                    snackbar.setDuration(1500);
                                    snackbar.show();
                                }

                            }
                        })
                        .setNegativeButton("CANCEL", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {

                            }
                        });
                AlertDialog dialog = alert.create();
                dialog.show();
                dialog.getButton(DialogInterface.BUTTON_NEGATIVE).setTextColor(getResources().getColor(R.color.colorPrimary));
                dialog.getButton(DialogInterface.BUTTON_POSITIVE).setTextColor(getResources().getColor(R.color.colorPrimary));
        }
        return super.onOptionsItemSelected(item);
    }

    //To get values from timetable of each day and store it in the database
    public boolean getTimeTable() {

        //ArrayList of timetable of each day
        ArrayList<TimetableModel> mondayList = new ArrayList<>(), tuesdayList = new ArrayList<>(),
                wednesdayList = new ArrayList<>(), thursdayList = new ArrayList<>(),
                fridayList = new ArrayList<>(), saturdayList = new ArrayList<>();

        int no_of_hours = Integer.parseInt(hours_days.getText().toString());

        for (int i = 0; i < arrayList.size(); i++) {

            //Segregate timetable based on the day
            TimetableModel timetableModel = arrayList.get(i);
            String s = timetableModel.getDay();

            switch (s) {
                case "Monday":
                    mondayList.add(arrayList.get(i));
                    break;

                case "Tuesday":
                    tuesdayList.add(arrayList.get(i));
                    break;

                case "Wednesday":
                    wednesdayList.add(arrayList.get(i));
                    break;

                case "Thursday":
                    thursdayList.add(arrayList.get(i));
                    break;

                case "Friday":
                    fridayList.add(arrayList.get(i));
                    break;

                case "Saturday":
                    saturdayList.add(arrayList.get(i));
                    if (!saturdayList.isEmpty())
                        saturdayList = checkDuplicates(saturdayList);
                    else
                        saturdayList.clear();
                    break;
            }
        }

        //Check each day for duplicate values which have come. Interface class was returning multiple values for each spinner
        mondayList = checkDuplicates(mondayList);
        tuesdayList = checkDuplicates(tuesdayList);
        wednesdayList = checkDuplicates(wednesdayList);
        thursdayList = checkDuplicates(thursdayList);
        fridayList = checkDuplicates(fridayList);

        arrayList.clear();

        //Add the final timetable of each day into the main list
        for (int i=0; i<no_of_hours; i++)
        {
            arrayList.add(mondayList.get(i));
            arrayList.add(tuesdayList.get(i));
            arrayList.add(wednesdayList.get(i));
            arrayList.add(thursdayList.get(i));
            arrayList.add(fridayList.get(i));
            if(!saturdayList.isEmpty())
            {
                arrayList.add(saturdayList.get(i));
            }
        }

        /*int no_of_hours = Integer.parseInt(hours_days.getText().toString());

        for(int i=1; i<=no_of_hours; i++)
        {
            TimetableModel temp1 = mondayList.get(i-1);
            Toast.makeText(TimetableEntry.this, ""+temp1.getHour()+"\n"+temp1.getSubject(), Toast.LENGTH_SHORT).show();

            TimetableModel temp2 = tuesdayList.get(i-1);
            Toast.makeText(TimetableEntry.this, ""+temp2.getHour()+"\n"+temp2.getSubject(), Toast.LENGTH_SHORT).show();

            TimetableModel temp3 = wednesdayList.get(i-1);
            Toast.makeText(TimetableEntry.this, ""+temp3.getHour()+"\n"+temp3.getSubject(), Toast.LENGTH_SHORT).show();

            TimetableModel temp4 = thursdayList.get(i-1);
            Toast.makeText(TimetableEntry.this, ""+temp4.getHour()+"\n"+temp4.getSubject(), Toast.LENGTH_SHORT).show();

            TimetableModel temp5 = fridayList.get(i-1);
            Toast.makeText(TimetableEntry.this, ""+temp5.getHour()+"\n"+temp5.getSubject(), Toast.LENGTH_SHORT).show();

        }*/
        return true;
    }

    public ArrayList<TimetableModel> checkDuplicates(ArrayList<TimetableModel> list) {

        //Use for loop to get last index of all the hours and store only those values
        int no_of_hours = Integer.parseInt(hours_days.getText().toString());
        int[] hours = new int[no_of_hours + 1];
        boolean toSaveComputationTime = true;

        for (int i = 0; i < list.size(); i++) {
            TimetableModel temp = list.get(i);
            toSaveComputationTime = true;
            for (int j = 1; j <= no_of_hours; j++) {
                if (temp.getHour().equals("Hour " + j) && toSaveComputationTime) {
                    hours[j] = i;
                    toSaveComputationTime = false;
                }
            }
        }

        ArrayList<TimetableModel> newList = new ArrayList<>();

        try {
            for (int i = 1; i <= no_of_hours; i++) {
                if(hours[i]==0)// && !(list.get(hours[i]).getHour().equals("Hour " + i)))
                    list.get(hours[i]).setSubject("Free");
                newList.add(list.get(hours[i]));
            }
            //TimetableModel temp = newList.get(i-1);
            //Toast.makeText(TimetableEntry.this, ""+temp.getHour()+"\n"+temp.getSubject(), Toast.LENGTH_SHORT).show();
        }
        catch (IndexOutOfBoundsException e)
        {
            Snackbar snackbar = Snackbar.make(tabLayout, "Please enter the timetable for all days!",
                    Snackbar.LENGTH_SHORT);
            snackbar.setDuration(1500);
            snackbar.show();
        }
        return newList;
    }
}