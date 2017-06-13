package com.sanath.bunkmaster;

import android.annotation.SuppressLint;
import android.content.Context;
import android.database.Cursor;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.sanath.bunkmaster.database.InfoDatabase;
import com.sanath.bunkmaster.database.SubjectDatabase;
import com.sanath.bunkmaster.database.TimetableDatabase;
import com.sanath.bunkmaster.viewtimetable.FridayView;
import com.sanath.bunkmaster.viewtimetable.MondayView;
import com.sanath.bunkmaster.viewtimetable.SaturdayView;
import com.sanath.bunkmaster.viewtimetable.ThursdayView;
import com.sanath.bunkmaster.viewtimetable.TuesdayView;
import com.sanath.bunkmaster.viewtimetable.WednesdayView;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;

/**
 * Created by Sanath on 01-12-2016.
 */
public class ViewTimetable extends android.support.v4.app.Fragment {

    TimetableDatabase timedb;
    SubjectDatabase subdb;
    InfoDatabase infodb;
    PreferenceManager preferenceManager;
    Context context;
    ArrayList<String> days = new ArrayList<String>();

    ViewPager viewPager;
    TabLayout tabLayout;

    public ViewTimetable(){}

    @SuppressLint("ValidFragment")
    public ViewTimetable(Context context) {
        this.context = context;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);

        View rootView = inflater.inflate(R.layout.tab_timetable, container, false);

        timedb = new TimetableDatabase(getActivity());
        subdb = new SubjectDatabase(getActivity());
        infodb = new InfoDatabase(getActivity());
        preferenceManager = new PreferenceManager(getActivity());

        viewPager = (ViewPager) rootView.findViewById(R.id.viewpager);
        tabLayout = (TabLayout) rootView.findViewById(R.id.tabs);

        int work_days = preferenceManager.DaysPerWeek();

        SimpleDateFormat dayFormat = new SimpleDateFormat("EEEE", Locale.ENGLISH);
        Calendar calendar = Calendar.getInstance();
        String weekDay = dayFormat.format(calendar.getTime());
        days = new ArrayList<>(Arrays.asList("Monday", "Tuesday", "Wednesday", "Thursday", "Friday", "Saturday"));

        timedb.open();
        Cursor c = timedb.getQueryResult("SELECT * FROM TIMETABLE");
        c.moveToFirst();
        for(int i=0; i<c.getCount(); i++) {
            //Toast.makeText(context, "" + c.getString(1), Toast.LENGTH_SHORT).show();
            c.moveToNext();
        }
        c.close();
        timedb.close();

        if (work_days == 5) {
            setupViewPagerFor5(viewPager);
            viewPager.setCurrentItem(days.indexOf(weekDay));
            tabLayout.setupWithViewPager(viewPager);
        } else if (work_days == 6) {
            setupViewPagerFor6(viewPager);
            tabLayout.setupWithViewPager(viewPager);
        }

        return rootView;
    }

    private void setupViewPagerFor5(ViewPager viewPager) {

        ViewPagerAdapter adapter = new ViewPagerAdapter(getFragmentManager());
        adapter.addFragment(new MondayView(), "MONDAY");
        adapter.addFragment(new TuesdayView(), "TUESDAY");
        adapter.addFragment(new WednesdayView(), "WEDNESDAY");
        adapter.addFragment(new ThursdayView(), "THURSDAY");
        adapter.addFragment(new FridayView(), "FRIDAY");
        viewPager.setAdapter(adapter);
    }

    private void setupViewPagerFor6(ViewPager viewPager) {

        ViewPagerAdapter adapter = new ViewPagerAdapter(getFragmentManager());
        adapter.addFragment(new MondayView(), "MONDAY");
        adapter.addFragment(new TuesdayView(), "TUESDAY");
        adapter.addFragment(new WednesdayView(), "WEDNESDAY");
        adapter.addFragment(new ThursdayView(), "THURSDAY");
        adapter.addFragment(new FridayView(), "FRIDAY");
        adapter.addFragment(new SaturdayView(), "SATURDAY");
        viewPager.setAdapter(adapter);
    }

    class ViewPagerAdapter extends FragmentStatePagerAdapter {

        private final List<Fragment> mFragmentList = new ArrayList<>();
        private final List<String> mFragmentTitleList = new ArrayList<>();

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

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        /*View rootView = getView();

        timedb = new TimetableDatabase(getActivity());
        subdb = new SubjectDatabase(getActivity());
        infodb = new InfoDatabase(getActivity());

        viewPager = (ViewPager) rootView.findViewById(R.id.viewpager);
        tabLayout = (TabLayout) rootView.findViewById(R.id.tabs);

        Toast.makeText(getActivity(), "It is Active", Toast.LENGTH_SHORT).show();

        Cursor c = subdb.getQueryResult("SELECT DAYS FROM TIMETABLE");
        c.moveToFirst();
        for(int i=0; i<c.getCount(); i++)
            Toast.makeText(getContext(), ""+c.getString(0), Toast.LENGTH_SHORT).show();*/
    }
}
