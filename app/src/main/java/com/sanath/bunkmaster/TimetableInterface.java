package com.sanath.bunkmaster;

/**
 * Created by Sanath on 28-10-2016.
 */

public class TimetableInterface {

    public interface whenDone
    {
        //Interface class to send values from fragment to TimetableEntry class.
        void getSpinnerArray(String pageTitle, String hour, String subject);
    }

}
