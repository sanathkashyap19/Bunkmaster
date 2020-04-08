package com.sanath.bunkmaster;

import android.database.Cursor;
import android.os.Bundle;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.sanath.bunkmaster.database.AttendanceDatabase;

/**
 * Created by Sanath on 27-01-2017.
 */
public class ShowAttendance extends Fragment {

    AttendanceDatabase attenddb;
    PreferenceManager preferenceManager;

    TableLayout tableLayout;
    TextView date, day, info;
    TextView subject[], status[];
    String dat, weekDay;
    Button save;

    public ShowAttendance() {
    }

    @Nullable
    @Override
    public View onCreateView(final LayoutInflater inflater, @Nullable final ViewGroup container, @Nullable Bundle savedInstanceState) {

        View rootView = inflater.inflate(R.layout.today_attendance, container, false);
        tableLayout = (TableLayout) rootView.findViewById(R.id.attendance_container);
        date = (TextView) rootView.findViewById(R.id.date);
        day = (TextView) rootView.findViewById(R.id.day);
        info = (TextView) rootView.findViewById(R.id.info);
        save = (Button) rootView.findViewById(R.id.save_attendance);

        attenddb = new AttendanceDatabase(getContext());
        preferenceManager = new PreferenceManager(getContext());

        Bundle bundle = this.getArguments();
        if (bundle != null)
        {
            dat = bundle.getString("Date");
            weekDay = bundle.getString("Day");
        }

        date.setText(dat);
        day.setText(weekDay);
        info.setVisibility(View.GONE);
        save.setVisibility(View.GONE);
        tableLayout.setGravity(Gravity.CENTER);

        attenddb.open();
        Cursor c = attenddb.getQueryResult("SELECT SUB, STATUS FROM DAY_ATTENDANCE WHERE DAT='"+ dat + "'");
        c.moveToFirst();

        TableRow.LayoutParams rowParams = new TableRow.LayoutParams(TableRow.LayoutParams.WRAP_CONTENT, TableRow.LayoutParams.WRAP_CONTENT, 1);
        TableRow.LayoutParams spinparams = new TableRow.LayoutParams(0, TableRow.LayoutParams.WRAP_CONTENT, 1);
        final TableLayout.LayoutParams params = new TableLayout.LayoutParams(TableLayout.LayoutParams.MATCH_PARENT,
                TableLayout.LayoutParams.WRAP_CONTENT);

        final int hours = preferenceManager.HoursPerDay();
        subject = new TextView[hours];
        status = new TextView[hours];

        for (int i = 0; i < hours; i++)
        {
            subject[i] = new TextView(getContext());
            status[i] = new TextView(getContext());

            String sub = c.getString(0);
            String stat = c.getString(1);

            TableRow tableRow = new TableRow(getContext());
            tableRow.setLayoutParams(params);
            tableRow.setPadding(10, 10, 10, 10);
            //LinearLayout tableRow = new LinearLayout(getContext());
            //tableRow.setLayoutParams(rowParams);
            //tableRow.setOrientation(LinearLayout.HORIZONTAL);

            for (int j = 1; j <= 4; j++) {

                if(sub.equals("Free"))
                    break;

                if (j == 1)
                {
                    subject[i].setLayoutParams(spinparams);
                    subject[i].setGravity(Gravity.CENTER);
                    subject[i].setPadding(5, 5, 5, 5);
                    subject[i].setText(sub);
                    subject[i].setTextSize(20);
                    subject[i].setTextColor(getResources().getColor(R.color.colorAccent));

                    tableRow.addView(subject[i]);
                }
                else if (j == 2)
                {
                    status[i].setLayoutParams(spinparams);
                    status[i].setGravity(Gravity.CENTER);
                    status[i].setPadding(5, 5, 5, 5);
                    switch (stat) {
                        case "A":
                            status[i].setText("Attended");
                            break;
                        case "B":
                            status[i].setText("Bunked");
                            break;
                        case "F":
                            status[i].setText("Free");
                            break;
                    }
                    status[i].setTextSize(20);
                    status[i].setTextColor(getResources().getColor(R.color.colorPrimary));

                    tableRow.addView(status[i]);
                }
                //tableRow.addView(attendance[i]);
            }
            c.moveToNext();
            tableLayout.addView(tableRow);
        }
        c.close();
        attenddb.close();
        return rootView;
    }
}