package com.sanath.bunkmaster;

import android.database.Cursor;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CalendarView;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.sanath.bunkmaster.database.AttendanceDatabase;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;

/**
 * Created by Sanath on 21-01-2017.
 */
public class Goto extends android.support.v4.app.Fragment {

    RelativeLayout relativeLayout;
    CalendarView calendarView;
    String todayDate;

    AttendanceDatabase attenddb;

    public Goto() {}

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        final View rootView = inflater.inflate(R.layout.goto_day, container, false);

        relativeLayout = (RelativeLayout) rootView.findViewById(R.id.calendar_container);
        calendarView = (CalendarView) rootView.findViewById(R.id.calendarView);

        attenddb = new AttendanceDatabase(getContext());

        calendarView.setOnDateChangeListener(new CalendarView.OnDateChangeListener() {
            @Override
            public void onSelectedDayChange(@NonNull CalendarView calendarView, int year, int month, int date) {

                month++;
                String weekDay = null;
                String dat = date + "/" + month + "/" + year;
                if(month<10)
                    dat = date + "/0" + month + "/" + year;
                if (date<10)
                    dat = "0" + date + "/" + month + "/" + year;

                SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy", Locale.ENGLISH);
                Calendar calendar = Calendar.getInstance();
                todayDate = dateFormat.format(calendar.getTime());

                Calendar cal = Calendar.getInstance();
                cal.set(year, month-1, date);
                int day = cal.get(Calendar.DAY_OF_WEEK);

                switch (day) {

                    case Calendar.MONDAY:
                        weekDay = "Monday";
                        break;
                    case Calendar.TUESDAY:
                        weekDay = "Tuesday";
                        break;
                    case Calendar.WEDNESDAY:
                        weekDay = "Wednesday";
                        break;
                    case Calendar.THURSDAY:
                        weekDay = "Thursday";
                        break;
                    case Calendar.FRIDAY:
                        weekDay = "Friday";
                        break;
                    case Calendar.SATURDAY:
                        weekDay = "Saturday";
                        break;
                    case Calendar.SUNDAY:
                        weekDay = "Sunday";
                        break;
                }

                //Toast.makeText(getContext(), weekDay, Toast.LENGTH_SHORT).show();
                attenddb.open();
                Cursor c = attenddb.getQueryResult("SELECT SUB, STATUS FROM DAY_ATTENDANCE WHERE DAT='"+ dat + "'");
                c.moveToFirst();

                Cursor c1 = attenddb.getQueryResult("SELECT * FROM DAY_ATTENDANCE");
                c1.moveToFirst();

                int todayMonth = calendar.get(Calendar.MONTH);
                todayMonth++;
                //Toast.makeText(getContext(), c1.getString(1), Toast.LENGTH_SHORT).show();

                if(c.getCount() > 0) {

                    Fragment fragment = new ShowAttendance();
                    Bundle bundle = new Bundle();
                    bundle.putString("Date", dat);
                    bundle.putString("Day", weekDay);
                    fragment.setArguments(bundle);

                    FragmentManager fragmentManager = getFragmentManager();
                    fragmentManager.popBackStackImmediate();
                    FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
                    fragmentTransaction.replace(R.id.content_frame, fragment).addToBackStack("Bunkmaster");
                    fragmentTransaction.commit();
                }
                else if((year>calendar.get(Calendar.YEAR)) || (month>todayMonth) || (month>todayMonth)&&(date>calendar.get(Calendar.DATE))
                        || (month==todayMonth)&&(date>calendar.get(Calendar.DATE)))
                {
                    Snackbar snackbar = Snackbar.make(relativeLayout, "You are trying to look into the Future!",
                            Snackbar.LENGTH_SHORT);
                    snackbar.setDuration(1500);
                    snackbar.show();
                }
                else
                {
                    Snackbar snackbar = Snackbar.make(relativeLayout, "No Records for this Day!",
                            Snackbar.LENGTH_SHORT);

                    final String finalDat = dat;
                    final String finalWeekDay = weekDay;

                    snackbar.setAction("ADD", new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {

                            if(!(finalWeekDay.equals("Sunday"))) {
                                Fragment fragment = new EditAttendance();
                                Bundle bundle = new Bundle();
                                bundle.putString("Date", finalDat);
                                bundle.putString("Day", finalWeekDay);
                                bundle.putString("From", "Goto");
                                fragment.setArguments(bundle);

                                FragmentManager fragmentManager = getFragmentManager();
                                fragmentManager.popBackStackImmediate();
                                FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
                                fragmentTransaction.replace(R.id.content_frame, fragment).addToBackStack("Bunkmaster");
                                fragmentTransaction.commit();
                            }
                            else
                                Toast.makeText(getContext(), "It's a Sunday!", Toast.LENGTH_SHORT).show();
                        }
                    });
                    snackbar.setDuration(1500);
                    snackbar.show();
                }
                c.close();
                c1.close();
                attenddb.close();
            }
        });
        return rootView;
    }
}