package com.sanath.bunkmaster.entertimetable;

import android.database.Cursor;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;

import com.sanath.bunkmaster.PreferenceManager;
import com.sanath.bunkmaster.R;
import com.sanath.bunkmaster.TimetableInterface;
import com.sanath.bunkmaster.database.SubjectDatabase;

import java.util.ArrayList;

/**
 * Created by Sanath on 23-10-2016.
 */

public class Friday extends Fragment {

    TimetableInterface.whenDone listener;
    PreferenceManager preferenceManager;
    int hours;

    public Friday(){}

    public Friday(TimetableInterface.whenDone listener) {
        this.listener = listener;
    }

    Spinner sp[];

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setRetainInstance(true);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        setRetainInstance(true);

        LinearLayout linearLayout;//, innerLayout;
        final TextView tv[];
        ArrayAdapter adapter;
        ArrayList<String> arrayList = new ArrayList<>();
        SubjectDatabase subdb;

        View rootView = inflater.inflate(R.layout.timetable_fragment, container, false);

        linearLayout = (LinearLayout) rootView.findViewById(R.id.fragment_container);

        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);

        subdb = new SubjectDatabase(getContext());
        preferenceManager = new PreferenceManager(getContext());
        hours = preferenceManager.HoursPerDay();//Integer.parseInt(TimetableEntry.hours_days.getText().toString());

        //linearLayout.addView(innerLayout);

        //String hour = this.getArguments().getString("hours");
        //int hours = Integer.parseInt(hour);

        tv = new TextView[hours];
        sp = new Spinner[hours];

        linearLayout.removeAllViews();

        for(int i =0; i<hours; i++)
        {
            tv[i] = new TextView(getContext());
            sp[i] = new Spinner(getContext());

            tv[i] = new TextView(getActivity().getApplicationContext());
            sp[i] = new Spinner(getActivity().getApplicationContext());

            LinearLayout layout = new LinearLayout(getContext());
            layout.setOrientation(LinearLayout.HORIZONTAL);

            for(int j = 1; j <= 2; j++)
            {
                if(j==1)
                {
                    tv[i].setLayoutParams(params);

                    tv[i].setText("Hour "+(i+1));
                    tv[i].setPadding(5, 15, 5, 15);
                    tv[i].setTextSize(20);
                    tv[i].setGravity(Gravity.CENTER);
                    tv[i].setTextColor(getResources().getColor(R.color.colorAccent));

                    layout.addView(tv[i]);
                }

                else if(j==2)
                {
                    sp[i].setLayoutParams(params);

                    subdb.open();

                    Cursor c1 = subdb.getQueryResult("SELECT * FROM SUBJECT");
                    c1.moveToFirst();

                    arrayList.clear();

                    arrayList.add("Free");

                    for (int k = 0; k < c1.getCount(); k++) {

                        String s = c1.getString(1);
                        arrayList.add(s);

                        //Toast.makeText(getContext(), ""+s, Toast.LENGTH_SHORT).show();

                        c1.moveToNext();
                    }
                    c1.close();
                    subdb.close();

                    adapter = new ArrayAdapter<>(getContext(), R.layout.spinner_text, arrayList);

                    sp[i].setAdapter(adapter);

                    sp[i].setGravity(Gravity.CENTER|Gravity.CENTER_HORIZONTAL);

                    final int finalI = i;

                    sp[i].setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                        @Override
                        public void onItemSelected(AdapterView<?> adapterView, View view, int pos, long l) {

                            listener.getSpinnerArray("Friday", tv[finalI].getText().toString(),
                                    sp[finalI].getItemAtPosition(pos).toString());

                        }

                        @Override
                        public void onNothingSelected(AdapterView<?> adapterView) {

                        }
                    });

                    layout.addView(sp[i]);
                }
            }

            linearLayout.addView(layout);

        }

        return rootView;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        if(savedInstanceState!=null)
        {
            for(int i=0; i<hours; i++)
            {
                sp[i].setSelection(savedInstanceState.getInt(""+i, 0));
                //Toast.makeText(getContext(), ""+savedInstanceState.getInt(""+i, 0), Toast.LENGTH_SHORT).show();
                Log.e("Restored", "Restored spinner "+i+" from bundle");
            }
        }
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);

        for(int i=0; i<hours; i++)
        {
            outState.putInt(""+i, sp[i].getSelectedItemPosition());
        }

        Log.e("SAVED INSTANCE", "Instance of activity saved");
    }

    @Override
    public void onViewStateRestored(@Nullable Bundle savedInstanceState) {
        super.onViewStateRestored(savedInstanceState);

        if(savedInstanceState!=null)
        {
            for(int i=0; i<hours; i++)
            {
                sp[i].setSelection(savedInstanceState.getInt(""+i, 0));
                //Toast.makeText(getContext(), ""+savedInstanceState.getInt(""+i, 0), Toast.LENGTH_SHORT).show();
            }
        }
    }

    public ArrayList getSpinnerValues(){

        final ArrayList<String> subject_Mon = new ArrayList<>();
        PreferenceManager preferenceManager = new PreferenceManager(getContext());
        int hours = preferenceManager.HoursPerDay();//Integer.parseInt(TimetableEntry.hours_days.getText().toString());


        subject_Mon.clear();

        for(int i = 0; i < hours; i++)
        {
            sp[i].setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {

                @Override
                public void onItemSelected(AdapterView<?> adapterView, View view, int pos, long l) {

                    if(pos==0)
                    {
                        //Toast.makeText(getContext(), "Select a subject", Toast.LENGTH_SHORT).show();
                        subject_Mon.add("--empty--");
                    }
                    else {

                        //Toast.makeText(getContext(), ""+adapterView.getItemAtPosition(pos).toString(), Toast.LENGTH_SHORT).show();
                        subject_Mon.add(adapterView.getItemAtPosition(pos).toString());
                    }
                }

                @Override
                public void onNothingSelected(AdapterView<?> adapterView) {

                }
            });
        }
        return subject_Mon;
    }
}