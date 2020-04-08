package com.sanath.bunkmaster.viewtimetable;

import android.database.Cursor;
import android.os.Bundle;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.fragment.app.Fragment;

import com.sanath.bunkmaster.PreferenceManager;
import com.sanath.bunkmaster.R;
import com.sanath.bunkmaster.database.TimetableDatabase;

/**
 * Created by Sanath on 16-01-2017.
 */

public class FridayView extends Fragment {

    public FridayView() {    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        LinearLayout linearLayout;
        final TextView hour[], sub[];
        TimetableDatabase timedb;
        PreferenceManager preferenceManager;

        View rootView = inflater.inflate(R.layout.timetable_fragment, container, false);
        linearLayout = (LinearLayout) rootView.findViewById(R.id.fragment_container);

        //Set the parameters for the layout
        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);

        timedb = new TimetableDatabase(getContext());
        preferenceManager = new PreferenceManager(getContext());

        int hours = preferenceManager.HoursPerDay();

        hour = new TextView[hours];
        sub = new TextView[hours];

        //Get the timetable of the required day
        timedb.open();
        Cursor c1 = timedb.getQueryResult("SELECT SUBJECT FROM TIMETABLE WHERE DAY='Friday'");
        c1.moveToFirst();

        linearLayout.removeAllViews();

        for(int i =0; i<hours; i++)
        {
            hour[i] = new TextView(getContext());
            sub[i] = new TextView(getContext());

            LinearLayout layout = new LinearLayout(getContext());
            layout.setOrientation(LinearLayout.HORIZONTAL);

            for(int j = 1; j <= 2; j++)
            {
                if(j==1)
                {
                    hour[i].setLayoutParams(params);

                    hour[i].setText("Hour "+(i+1));
                    hour[i].setPadding(10, 100, 50, 15);
                    hour[i].setTextSize(20);
                    hour[i].setGravity(Gravity.CENTER);
                    hour[i].setTextColor(getResources().getColor(R.color.colorAccent));

                    layout.addView(hour[i]);
                }

                else if(j==2)
                {
                    sub[i].setLayoutParams(params);
                    //timedb.open();
                    //for (int k = 0; k < c1.getCount(); k++) {
                    String s = c1.getString(0);

                    sub[i].setText(s);
                    sub[i].setPadding(10, 100, 50, 15);
                    sub[i].setTextSize(20);
                    sub[i].setGravity(Gravity.CENTER|Gravity.CENTER_HORIZONTAL);
                    sub[i].setTextColor(getResources().getColor(R.color.colorPrimary));
                    //Toast.makeText(getContext(), ""+s, Toast.LENGTH_SHORT).show();
                    c1.moveToNext();
                    //}
                    //timedb.close();
                    layout.addView(sub[i]);
                }
            }
            linearLayout.addView(layout);
        }

        c1.close();
        timedb.close();
        return rootView;
    }
}
