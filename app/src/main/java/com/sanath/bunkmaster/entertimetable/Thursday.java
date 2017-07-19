package com.sanath.bunkmaster.entertimetable;

import android.content.ClipData;
import android.database.Cursor;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.DragEvent;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;
import android.widget.Toast;

import com.sanath.bunkmaster.PreferenceManager;
import com.sanath.bunkmaster.R;
import com.sanath.bunkmaster.TimetableInterface;
import com.sanath.bunkmaster.database.SubjectDatabase;

import java.util.ArrayList;

/**
 * Created by Sanath on 23-10-2016.
 */

public class Thursday extends Fragment{

    TimetableInterface.whenDone listener;
    PreferenceManager preferenceManager;
    int hours;

    public Thursday(){}

    public Thursday(TimetableInterface.whenDone listener) {

        this.listener = listener;
    }

    Spinner sp[];
    TextView subject[];

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setRetainInstance(true);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        setRetainInstance(true);

        LinearLayout linearLayout;//, innerLayout;
        TableLayout tableLayout;
        final TextView hour[],  subjectCol1[], subjectCol2[], subjectCol3[], subjectCol4[];
        ArrayAdapter adapter;
        ArrayList<String> arrayList = new ArrayList<>();
        SubjectDatabase subdb;

        View rootView = inflater.inflate(R.layout.timetable_fragment, container, false);

        linearLayout = (LinearLayout) rootView.findViewById(R.id.fragment_container);

        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT,
                LinearLayout.LayoutParams.WRAP_CONTENT);
        params.setMargins(16, 5, 0, 5);
        LinearLayout.LayoutParams paramsSubject = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT,
                LinearLayout.LayoutParams.MATCH_PARENT);
        paramsSubject.setMargins(40, 5, 0, 5);

        subdb = new SubjectDatabase(getContext());
        preferenceManager = new PreferenceManager(getContext());
        hours = preferenceManager.HoursPerDay();//Integer.parseInt(TimetableEntry.hours_days.getText().toString());

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

        hour = new TextView[hours];
        subject = new TextView[hours];

        linearLayout.removeAllViews();

        for(int i =0; i<hours; i++)
        {
            hour[i] = new TextView(getContext());
            subject[i] = new TextView(getContext());

            hour[i] = new TextView(getActivity().getApplicationContext());
            subject[i] = new TextView(getActivity().getApplicationContext());

            LinearLayout layout = new LinearLayout(getContext());
            layout.setOrientation(LinearLayout.HORIZONTAL);

            for(int j = 1; j <= 2; j++)
            {
                if(j==1)
                {
                    hour[i].setLayoutParams(params);

                    hour[i].setText("Hour "+(i+1));
                    hour[i].setPadding(5, 15, 5, 15);
                    hour[i].setTextSize(20);
                    hour[i].setGravity(Gravity.CENTER);
                    hour[i].setTextColor(getResources().getColor(R.color.colorAccent));

                    layout.addView(hour[i]);
                }

                else if(j==2)
                {

                    subject[i].setLayoutParams(paramsSubject);
                    subject[i].setHint("Drop Subjects Here");
                    subject[i].setPadding(8, 15, 8, 15);
                    subject[i].setTextSize(20);
                    subject[i].setId(i);
                    subject[i].setGravity(Gravity.CENTER|Gravity.CENTER_HORIZONTAL);
                    subject[i].setTextColor(getResources().getColor(R.color.colorPrimary));
                    subject[i].setBackgroundResource(R.drawable.table_border);
                    subject[i].setOnDragListener(new SubjectDragListener());
                    subject[i].setTag("Hour "+(i+1));
                    layout.addView(subject[i]);
                }
            }

            linearLayout.addView(layout);
        }

        tableLayout = (TableLayout) rootView.findViewById(R.id.subject_container);
        TableLayout.LayoutParams tableParam = new TableLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, 50);
        TableRow.LayoutParams rowParams = new TableRow.LayoutParams(0, TableLayout.LayoutParams.WRAP_CONTENT, 1);
        rowParams.setMargins(5, 8, 5, 8);

        subjectCol1 = new TextView[arrayList.size()];
        subjectCol2 = new TextView[arrayList.size()];
        subjectCol3 = new TextView[arrayList.size()];
        subjectCol4 = new TextView[arrayList.size()];

        for(int i=1; i<arrayList.size(); i++)
        {

            TableRow layout = new TableRow(getContext());
            layout.setLayoutParams(tableParam);
            layout.setOrientation(LinearLayout.HORIZONTAL);

            for(int j=1; j<=4; j++)
            {
                //First column
                if(j==1 && (i<arrayList.size()))
                {
                    subjectCol1[i] = new TextView(getContext());
                    subjectCol1[i].setLayoutParams(rowParams);
                    subjectCol1[i].setText(arrayList.get(i));
                    subjectCol1[i].setPadding(2, 4, 2, 4);
                    subjectCol1[i].setTextSize(16);
                    subjectCol1[i].setGravity(Gravity.CENTER);
                    subjectCol1[i].setTextColor(getResources().getColor(R.color.colorPrimary));
                    subjectCol1[i].setBackgroundResource(R.drawable.table_border);
                    subjectCol1[i].setOnTouchListener(new SubjectTouchListener());

                    layout.addView(subjectCol1[i]);
                    i++;
                }
                //Second Column
                else if(j==2 && (i<arrayList.size()))
                {
                    subjectCol2[i] = new TextView(getContext());
                    subjectCol2[i].setLayoutParams(rowParams);
                    subjectCol2[i].setText(arrayList.get(i));
                    subjectCol2[i].setPadding(2, 4, 2, 4);
                    subjectCol2[i].setTextSize(16);
                    subjectCol2[i].setGravity(Gravity.CENTER);
                    subjectCol2[i].setTextColor(getResources().getColor(R.color.colorPrimary));
                    subjectCol2[i].setBackgroundResource(R.drawable.table_border);
                    subjectCol2[i].setOnTouchListener(new SubjectTouchListener());

                    layout.addView(subjectCol2[i]);
                    i++;
                }
                //Third Column
                else if(j==3 && (i<arrayList.size()))
                {
                    subjectCol3[i] = new TextView(getContext());
                    subjectCol3[i].setLayoutParams(rowParams);
                    subjectCol3[i].setText(arrayList.get(i));
                    subjectCol3[i].setPadding(2, 4, 2, 4);
                    subjectCol3[i].setTextSize(16);
                    subjectCol3[i].setGravity(Gravity.CENTER);
                    subjectCol3[i].setTextColor(getResources().getColor(R.color.colorPrimary));
                    subjectCol3[i].setBackgroundResource(R.drawable.table_border);
                    subjectCol3[i].setOnTouchListener(new SubjectTouchListener());

                    layout.addView(subjectCol3[i]);
                    i++;
                }
                else if(j==4 && (i<arrayList.size()))
                {
                    subjectCol4[i] = new TextView(getContext());
                    subjectCol4[i].setLayoutParams(rowParams);
                    subjectCol4[i].setText(arrayList.get(i));
                    subjectCol4[i].setPadding(2, 4, 2, 4);
                    subjectCol4[i].setTextSize(16);
                    subjectCol4[i].setGravity(Gravity.CENTER);
                    subjectCol4[i].setTextColor(getResources().getColor(R.color.colorPrimary));
                    subjectCol4[i].setBackgroundResource(R.drawable.table_border);
                    subjectCol4[i].setOnTouchListener(new SubjectTouchListener());

                    layout.addView(subjectCol4[i]);
                }
            }
            tableLayout.addView(layout);
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
                //sp[i].setSelection(savedInstanceState.getInt(""+i, 0));
                subject[i].setText(savedInstanceState.getString(""+i));
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
            //outState.putInt(""+i, sp[i].getSelectedItemPosition());
            outState.putString(""+i, subject[i].getText().toString());
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
                //sp[i].setSelection(savedInstanceState.getInt(""+i, 0));
                subject[i].setText(savedInstanceState.getString(""+i));
                //Toast.makeText(getContext(), ""+savedInstanceState.getInt(""+i, 0), Toast.LENGTH_SHORT).show();
            }
        }
    }

    private final class SubjectTouchListener implements View.OnTouchListener
    {

        @Override
        public boolean onTouch(View view, MotionEvent motionEvent) {

            ClipData data = ClipData.newPlainText("", "");
            View.DragShadowBuilder shadow = new View.DragShadowBuilder(view);
            view.startDrag(data, shadow, view, 0);
            return false;
        }
    }

    private class SubjectDragListener implements View.OnDragListener
    {

        @Override
        public boolean onDrag(View view, DragEvent dragEvent) {

            int action = dragEvent.getAction();

            switch (action)
            {
                case DragEvent.ACTION_DRAG_STARTED:
                    //no action necessary
                    Log.e("DRAG:", "Drag Started");
                    break;

                case DragEvent.ACTION_DRAG_ENTERED:
                    //no action necessary
                    Log.e("DRAG:", "Drag Entered");
                    break;

                case DragEvent.ACTION_DRAG_EXITED:
                    //no action necessary
                    Log.e("DRAG:", "Drag Exited");
                    break;

                case DragEvent.ACTION_DROP:
                    //handle the dragged view being dropped over a drop view
                    Log.e("DRAG:", "Drag Drop");
                    //Toast.makeText(getContext(), "Dropped Perfectly!", Toast.LENGTH_SHORT).show();
                    View v = (View) dragEvent.getLocalState();
                    TextView dropTarget = (TextView) view;
                    TextView dropped = (TextView) v;
                    dropTarget.setText(dropped.getText());
                    listener.getSpinnerArray("Thursday", dropTarget.getTag().toString(), dropped.getText().toString());
                    Toast.makeText(getContext(), dropTarget.getTag().toString(), Toast.LENGTH_SHORT).show();
                    return true;

                case DragEvent.ACTION_DRAG_ENDED:
                    //no action necessary
                    Log.e("DRAG:", "Drag Ended");
                    return true;

                default:
                    break;
            }
            return true;
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