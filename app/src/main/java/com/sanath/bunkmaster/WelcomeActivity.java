package com.sanath.bunkmaster;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.multidex.MultiDex;
import android.support.v4.app.Fragment;

import com.crashlytics.android.Crashlytics;
import com.github.paolorotolo.appintro.AppIntro;
import com.github.paolorotolo.appintro.AppIntroFragment;
import com.sanath.bunkmaster.infoentry.StatusEntry;
import com.sanath.bunkmaster.infoentry.SubjectEntry;
import com.sanath.bunkmaster.infoentry.TimetableEntry;

import io.fabric.sdk.android.Fabric;

/**
 * Created by Sanath on 30-11-2016.
 */

public class WelcomeActivity extends AppIntro {

    PreferenceManager preferenceManager;

    @Override
    protected void attachBaseContext(Context newBase) {
        super.attachBaseContext(newBase);
        MultiDex.install(this);
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Fabric.with(this, new Crashlytics());

        // TODO: Move this to where you establish a user session
        logUser();

//        RokoMobi.start(this);
//        RokoLogger.addEvents(new Event("Hello World")); // log an event to ROKO Mobi

        preferenceManager = new PreferenceManager(this);

        //Check if its first launch
        if (!preferenceManager.isFirstTimeLaunch()) {

            //Check if intro page is complete
//            if(preferenceManager.isIntroComplete()) {

                //Check if subjects have been entered
                if(preferenceManager.isSubjectEntryComplete()) {

                    //Check if timetable is entered
                    if(preferenceManager.isTimetableEntryComplete()) {

                        //Check if status of attendance is entered
                        if(preferenceManager.isStatusEntryComplete()) {

                            //Go to main activity if everything has been entered
                            startActivity(new Intent(this, Overview.class));
                            finish();
                        }
                        else
                        {
                            //Go to status entry activity to enter timetable
                            startActivity(new Intent(this, StatusEntry.class));
                            finish();
                        }
                    }
                    else
                    {
                        //Go to timetable activity to enter timetable
                        startActivity(new Intent(this, TimetableEntry.class));
                        finish();
                    }
                }
                else {
                    //Go to subject entry activity to enter subjects
                    startActivity(new Intent(this, SubjectEntry.class));
                    finish();
                }
//            }
//            else {
//                //Go to intro activity to enter info
//                startActivity(new Intent(this, Intro.class));
//                finish();
//            }
        }

        //Add slide(heading, description, image, background colour)
        //can add slide from fragment also
        addSlide(AppIntroFragment.newInstance("Welcome to Peek 'n' Bunk", "The app for all your attendance needs", R.mipmap.for_intro,
                getResources().getColor(R.color.introSliderBackground)));
        addSlide(AppIntroFragment.newInstance("Personal Attendance Manager", "Just enter your attendance everyday and let " +
                "the app take care of all the calculations!",
                0, getResources().getColor(R.color.dodgerBlue)));
        addSlide(AppIntroFragment.newInstance("All the Information you need!", "Get the your attendance percentage in each subject along" +
                "with how many classes you need to attend or how many hours you can bunk based on your percentage.", 0,
                getResources().getColor(R.color.crimsonRed)));
        addSlide(AppIntroFragment.newInstance("Free your Mind of Attendance", "As soon as you press that 'Done' you will be getting " +
                "rid of the attendance monster that haunts every semester forever!", 0,
                getResources().getColor(R.color.introSliderBackground)));

        //set transition animation
        setDepthAnimation();

        //separator is the bar which displays the skip done and the slide buttons
        //setSeparatorColor(getResources().getColor(R.color.colorAccent));

        showSkipButton(false);
    }

    //Action when done button is pressed
    @Override
    public void onDonePressed(Fragment currentFragment) {
        super.onDonePressed(currentFragment);

        preferenceManager.setFirstTimeLaunch(false);

        startActivity(new Intent(this, SubjectEntry.class));
        finish();
    }

    private void logUser() {
        // TODO: Use the current user's information
        // You can call any combination of these three methods
        Crashlytics.setUserIdentifier("12345");
        Crashlytics.setUserEmail("user@fabric.io");
        Crashlytics.setUserName("Test User");
    }

}
