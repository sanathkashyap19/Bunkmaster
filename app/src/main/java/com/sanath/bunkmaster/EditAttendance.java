package com.sanath.bunkmaster;

import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Spinner;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;

import com.sanath.bunkmaster.database.AttendanceDatabase;
import com.sanath.bunkmaster.database.SubjectDatabase;
import com.sanath.bunkmaster.database.TimetableDatabase;

import java.util.ArrayList;

/**
 * Created by Sanath on 27-01-2017.
 */
public class EditAttendance extends Fragment {

    SubjectDatabase subdb;
    TimetableDatabase timedb;
    AttendanceDatabase attenddb;
    PreferenceManager preferenceManager;

    ArrayAdapter adapter;
    ArrayList<String> subArray = new ArrayList<>();
    ArrayList<String> selectedSub = new ArrayList<>();
    ArrayList<String> timetableArray = new ArrayList<>();
    String dat, weekDay, from;

    Spinner sub[];
    RadioButton attend[], bunk[], free[];
    RadioGroup attendance[];
    TableLayout tableLayout;
    TextView date, day;
    Button addHour, save, specialClass;

    public EditAttendance() {
    }

    @Nullable
    @Override
    public View onCreateView(final LayoutInflater inflater, @Nullable final ViewGroup container, @Nullable Bundle savedInstanceState) {

        View rootView = inflater.inflate(R.layout.today_attendance, container, false);
        tableLayout = (TableLayout) rootView.findViewById(R.id.attendance_container);
        date = (TextView) rootView.findViewById(R.id.date);
        day = (TextView) rootView.findViewById(R.id.day);
        //addHour = (Button) rootView.findViewById(R.id.add_hour);
        save = (Button) rootView.findViewById(R.id.save_attendance);
        //specialClass = (Button) rootView.findViewById(R.id.special_class);
        subdb = new SubjectDatabase(getContext());
        timedb = new TimetableDatabase(getContext());
        attenddb = new AttendanceDatabase(getContext());
        preferenceManager = new PreferenceManager(getContext());

        TableRow.LayoutParams rowParams = new TableRow.LayoutParams(TableRow.LayoutParams.WRAP_CONTENT, TableRow.LayoutParams.WRAP_CONTENT, 1);
        TableRow.LayoutParams spinparams = new TableRow.LayoutParams(0, TableRow.LayoutParams.WRAP_CONTENT, 1);
        final TableLayout.LayoutParams params = new TableLayout.LayoutParams(TableLayout.LayoutParams.MATCH_PARENT,
                TableLayout.LayoutParams.WRAP_CONTENT);

        Bundle bundle = this.getArguments();
        if (bundle != null)
        {
            dat = bundle.getString("Date");
            weekDay = bundle.getString("Day");
            from = bundle.getString("From");
        }

//        SimpleDateFormat dayFormat = new SimpleDateFormat("EEEE", Locale.ENGLISH);
//        SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy", Locale.ENGLISH);
//        Calendar calendar = Calendar.getInstance();
//        weekDay = dayFormat.format(calendar.getTime());
//        dat = dateFormat.format(calendar.getTime());

        int workdays = preferenceManager.DaysPerWeek();

        //When user attempts to enter attendance on Sunday or on Saturday and they have 5 working days
        if (weekDay.equals("Sunday") || (workdays == 5 && weekDay.equals("Saturday"))) {

            rootView = inflater.inflate(R.layout.sunday, container, false);

            TextView tv1 = (TextView) rootView.findViewById(R.id.textView);

            Animation slideFromBottom1 = AnimationUtils.loadAnimation(getContext(), R.anim.slide_from_bottom);
            tv1.setAnimation(slideFromBottom1);

            /*specialClass.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {

                    SpecialClass(inflater, container);
                }
            });*/
        }

        date.setText(dat);
        day.setText(weekDay);

        deletePreviousEntry();

        subdb.open();
        final Cursor c1 = subdb.getQueryResult("SELECT * FROM SUBJECT");
        c1.moveToFirst();

        subArray.clear();
        subArray.add("Free");

        for (int k = 0; k < c1.getCount(); k++) {

            String s = c1.getString(1);
            subArray.add(s);
            c1.moveToNext();
        }

        subdb.close();
        adapter = new ArrayAdapter<>(getContext(), R.layout.spinner_text, subArray);

        timedb.open();
        Cursor c = timedb.getQueryResult("SELECT SUBJECT FROM TIMETABLE WHERE DAY='" + weekDay + "'");
        //Cursor c = timedb.getQueryResult("SELECT SUBJECT FROM TIMETABLE WHERE DAY='Monday'");
        c.moveToFirst();

        timetableArray.clear();

        for (int k = 0; k < c.getCount(); k++) {

            String s = c.getString(0);
            timetableArray.add(s);
            c.moveToNext();
        }

        c.close();
        timedb.close();

        final int hours = preferenceManager.HoursPerDay();
        sub = new Spinner[hours + 5];
        attend = new RadioButton[hours + 5];
        bunk = new RadioButton[hours + 5];
        free = new RadioButton[hours + 5];
        attendance = new RadioGroup[hours + 5];

        {

            for (int i = 0; i < hours; i++) {
                sub[i] = new Spinner(getContext());
                attend[i] = new RadioButton(getContext());
                bunk[i] = new RadioButton(getContext());
                free[i] = new RadioButton(getContext());
                attendance[i] = new RadioGroup(getContext());

                TableRow tableRow = new TableRow(getContext());
                tableRow.setLayoutParams(params);
                tableRow.setPadding(10, 10, 10, 10);
                //LinearLayout tableRow = new LinearLayout(getContext());
                //tableRow.setLayoutParams(rowParams);
                //tableRow.setOrientation(LinearLayout.HORIZONTAL);

                for (int j = 1; j <= 4; j++) {

                    attendance[i].setLayoutParams(params);
                    if (j == 1) {
                        //sub[i].setLayoutParams(spinparams);
                        sub[i].setAdapter(adapter);
                        sub[i].setGravity(Gravity.CENTER);
                        sub[i].setPadding(5, 5, 5, 5);
                        //sub[i].setBackgroundResource(R.drawable.table_border);
                        sub[i].setSelection(subArray.indexOf(timetableArray.get(i)));
                        final int finalI1 = i;
                        sub[i].setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                            @Override
                            public void onItemSelected(AdapterView<?> adapterView, View view, int pos, long l) {

                                String subject = sub[finalI1].getItemAtPosition(pos).toString();
                                if(selectedSub.size() <= finalI1)
                                    selectedSub.add(subject);
                                else
                                    selectedSub.set(finalI1, subject);

                            }

                            @Override
                            public void onNothingSelected(AdapterView<?> adapterView) {

                            }
                        });


                        tableRow.addView(sub[i]);
                    } else if (j == 2) {
                        //attend[i].setLayoutParams(rowParams);
                        attend[i].setGravity(Gravity.CENTER);
                        attend[i].setPadding(5, 5, 5, 5);
                        attend[i].setText("A");
                        attend[i].setTextSize(20);
                        attend[i].setTextColor(getResources().getColor(R.color.colorPrimary));
                        //bunk[i].setBackgroundResource(R.drawable.table_border);
                        final int finalI = i;
                        attend[i].setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                            @Override
                            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                                if (bunk[finalI].isChecked() || free[finalI].isChecked()) {
                                    bunk[finalI].setChecked(false);
                                    free[finalI].setChecked(false);
                                }
                            }
                        });
                        tableRow.addView(attend[i]);
                    } else if (j == 3) {
                        //bunk[i].setLayoutParams(rowParams);
                        bunk[i].setGravity(Gravity.CENTER);
                        bunk[i].setPadding(5, 5, 5, 5);
                        bunk[i].setText("B");
                        bunk[i].setTextSize(20);
                        bunk[i].setTextColor(getResources().getColor(R.color.colorPrimary));
                        //bunk[i].setBackgroundResource(R.drawable.table_border);
                        final int finalI = i;
                        bunk[i].setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                            @Override
                            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                                if (attend[finalI].isChecked() || free[finalI].isChecked()) {
                                    attend[finalI].setChecked(false);
                                    free[finalI].setChecked(false);
                                }
                            }
                        });
                        tableRow.addView(bunk[i]);
                    } else if (j == 4) {
                        //free[i].setLayoutParams(rowParams);
                        free[i].setGravity(Gravity.CENTER);
                        free[i].setPadding(5, 5, 5, 5);
                        free[i].setText("F");
                        free[i].setTextSize(20);
                        free[i].setTextColor(getResources().getColor(R.color.colorPrimary));
                        //free[i].setBackgroundResource(R.drawable.table_border);
                        final int finalI = i;
                        free[i].setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                            @Override
                            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                                if (bunk[finalI].isChecked() || attend[finalI].isChecked()) {
                                    bunk[finalI].setChecked(false);
                                    attend[finalI].setChecked(false);
                                }
                            }
                        });

                        tableRow.addView(free[i]);
                    }
                    //tableRow.addView(attendance[i]);
                }
                tableLayout.addView(tableRow);
            }
        }

        save.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if (checkValues()) {

                    if(from.equals("Today")) {

                        attenddb.open();
                        attenddb.delete(dat);

                        for (int i = 0; i < hours; i++) {

                            String sub = selectedSub.get(i);
                            String status = "";

                            if (attend[i].isChecked())
                                status = "A";
                            else if (bunk[i].isChecked())
                                status = "B";
                            else if (free[i].isChecked())
                                status = "F";

                            attenddb.insertDayAttendance(dat, sub, status);
                            //if(l!=-1)
                            //Toast.makeText(getContext(), "" + dat + "\n" + sub + "\n" + status, Toast.LENGTH_SHORT).show();
                        }
                        attenddb.close();

                        Snackbar snackbar = Snackbar.make(tableLayout, "Attendance Record Registered!",
                                Snackbar.LENGTH_SHORT);
                        snackbar.setDuration(1500);
                        snackbar.show();

                        startActivity(new Intent(getContext(), AttendanceCalculator.class));
                        getFragmentManager().popBackStackImmediate();
                    }
                    else if(from.equals("Goto"))
                    {
                        AlertDialog.Builder alert = new AlertDialog.Builder(getContext());
                        alert.setMessage("Do you want to add this attendance to your total classes count?")
                                .setPositiveButton("YES", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialogInterface, int i) {

                                        attenddb.open();

                                        for (int j = 0; j < hours; j++) {

                                            String sub = selectedSub.get(j);
                                            String status = "";

                                            if (attend[j].isChecked())
                                                status = "A";
                                            else if (bunk[j].isChecked())
                                                status = "B";
                                            else if (free[j].isChecked())
                                                status = "F";

                                            attenddb.insertDayAttendance(dat, sub, status);
                                            //if(l!=-1)
                                            //Toast.makeText(getContext(), "" + dat + "\n" + sub + "\n" + status, Toast.LENGTH_SHORT).show();
                                        }
                                        attenddb.close();

                                        Snackbar snackbar = Snackbar.make(tableLayout, "Attendance Record Registered!",
                                                Snackbar.LENGTH_SHORT);
                                        snackbar.setDuration(1500);
                                        snackbar.show();

                                        Bundle bundle = new Bundle();
                                        bundle.putString("Date", dat);
                                        Intent activity = new Intent(getContext(), AttendanceCalculator.class);
                                        activity.putExtra("Bundle", bundle);
                                        startActivity(activity);
                                        getFragmentManager().popBackStackImmediate();

                                    }
                                })
                                .setNegativeButton("NO", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialogInterface, int i) {

                                        attenddb.open();
                                        attenddb.delete(dat);

                                        for (int j = 0; j < hours; j++) {

                                            String sub = selectedSub.get(j);
                                            String status = "";

                                            if (attend[j].isChecked())
                                                status = "A";
                                            else if (bunk[j].isChecked())
                                                status = "B";
                                            else if (free[j].isChecked())
                                                status = "F";

                                            attenddb.insertDayAttendance(dat, sub, status);
                                            //if(l!=-1)
                                            //Toast.makeText(getContext(), "" + dat + "\n" + sub + "\n" + status, Toast.LENGTH_SHORT).show();
                                        }
                                        attenddb.close();

                                        Snackbar snackbar = Snackbar.make(tableLayout, "Attendance Record Registered!",
                                                Snackbar.LENGTH_SHORT);
                                        snackbar.setDuration(1500);
                                        snackbar.show();

                                        getFragmentManager().popBackStackImmediate();
                                    }
                                })
                                .show();

                    }
                }
            }
        });
        return rootView;
    }

    private void deletePreviousEntry() {

        //Get the subject taught that day(timetable) and attendance status(attended or bunked or free) of those subjects for that date
        attenddb.open();
        Cursor c = attenddb.getQueryResult("SELECT SUB, STATUS FROM DAY_ATTENDANCE WHERE DAT='" + dat + "'");
        c.moveToFirst();

        for (int i = 0; i < c.getCount(); i++) {
            String subject = c.getString(0);
            //Get the total number of classes held and number of classes attended for each subject
            if (!subject.equals("Free")) {
                Cursor c1 = attenddb.getQueryResult("SELECT TOTAL, ATTENDED FROM ATTENDANCE WHERE SUB='" + subject + "'");
                c1.moveToFirst();
                int total = c1.getInt(0);
                int attended = c1.getInt(1);
                c1.close();
                float percent;

                String status = c.getString(1);
                //Toast.makeText(AttendanceCalculator.this, subject + "\n" + status + "\n" + total + "\n" + attended, Toast.LENGTH_SHORT).show();

                //If attended increment total classes and classes attended for that subject and update in the database
                if (status.equals("A")) {
                    total--;
                    attended--;
                    percent = ((float) attended / total) * 100;
                    attenddb.updateAttendance(subject, total, attended, percent);
                }
                //If bunked increment total classes only for that subject and update in the database
                else if (status.equals("B")) {
                    total--;
                    percent = ((float) attended / total) * 100;
                    attenddb.updateAttendance(subject, total, attended, percent);
                }

                //If free do nothing. Hence nothing done :P
                //Toast.makeText(AttendanceCalculator.this, subject + "\n" + status + "\n" + total + "\n" + attended, Toast.LENGTH_SHORT).show();
                c.moveToNext();
            }
        }
        c.close();
        attenddb.close();
    }


    private boolean checkValues() {

        boolean check = true;

        int hours = preferenceManager.HoursPerDay();
        for (int i = 0; i < hours; i++)
            if (!(attend[i].isChecked() || bunk[i].isChecked() || free[i].isChecked())) {

                Snackbar snackbar = Snackbar.make(tableLayout, "Enter Attendance for All Hours!",
                        Snackbar.LENGTH_SHORT);
                snackbar.setDuration(1500);
                snackbar.show();

                check = false;
            }

        return check;
    }
}
