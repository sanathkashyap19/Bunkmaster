package com.sanath.bunkmaster.database;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

/**
 * Created by Sanath on 21-01-2017.
 */

public class AttendanceDatabase {

    private static final String TAG = "AttendanceDatabase";
    private static final String DATABASE_NAME = "ATTENDANCE_DATABASE";   //Database Name
    private static final int DATABASE_VERSION = 4; //Change Version to upgrade db

    private final Context context;

    private AttendanceDatabase.DatabaseHelper DBHelper;
    private SQLiteDatabase db;

    public AttendanceDatabase(Context ctx)
    {
        this.context = ctx;
        DBHelper = new AttendanceDatabase.DatabaseHelper(context);
    }

    private static class DatabaseHelper extends SQLiteOpenHelper
    {
        DatabaseHelper(Context context)
        {
            super(context, DATABASE_NAME, null, DATABASE_VERSION);
        }

        @Override
        public void onCreate(SQLiteDatabase db)
        {

            //Database of Attendance
            db.execSQL("CREATE TABLE ATTENDANCE(ID INTEGER PRIMARY KEY AUTOINCREMENT," +
                    "SUB TEXT NOT NULL, TOTAL NUMBER NOT NULL, ATTENDED NUMBER NOT NULL, PERCENT NOT NULL)");

            db.execSQL("CREATE TABLE DAY_ATTENDANCE(ID INTEGER PRIMARY KEY AUTOINCREMENT," +
                    "DAT TEXT NOT NULL, SUB TEXT NOT NULL, STATUS TEXT NOT NULL)");
        }

        @Override
        public void onUpgrade(SQLiteDatabase db, int oldVersion,
                              int newVersion)
        {
            Log.w(TAG, "Upgrading database from version " + oldVersion
                    + " to "
                    + newVersion + ", which will destroy all old data");

            //Admin Table
            db.execSQL("DROP TABLE IF EXISTS ATTENDANCE");

            onCreate(db);
        }
    }

    //---opens the database---
    public AttendanceDatabase open() throws SQLException
    {
        db = DBHelper.getWritableDatabase();
        return this;
    }

    //---closes the database---
    public void close()
    {
        DBHelper.close();
    }


    //---insert a User into the database---
    public long insertAttendance(String SUBJECT, int TOTAL, int ATTENDED, float PERCENT)
    {
        if(ATTENDED==TOTAL)
            PERCENT = 100;

        ContentValues initialValues = new ContentValues();

        initialValues.put("SUB", SUBJECT);
        initialValues.put("TOTAL", TOTAL);
        initialValues.put("ATTENDED", ATTENDED);
        initialValues.put("PERCENT", PERCENT);

        return db.insert("ATTENDANCE", null, initialValues);
    }

    public long insertDayAttendance(String DATE, String SUBJECT, String STATUS)
    {
        ContentValues initialValues = new ContentValues();

        initialValues.put("SUB", SUBJECT);
        initialValues.put("DAT", DATE);
        initialValues.put("STATUS", STATUS);

        return db.insert("DAY_ATTENDANCE", null, initialValues);
    }


    //---deletes a particular Invoice---
    public boolean delete(String date)
    {
        return db.delete("DAY_ATTENDANCE",  "DAT" +
                "='" + date +"'", null) > 0;
    }

    //---Truncate table (delete all record from selected table), if already exists---
    public void truncateTable(String TableName)
    {
        db.execSQL("DELETE FROM "+TableName);
    }


    //---updates a LOGIN_STATUS : Y or N
    public boolean updateUserStatus(String Status )
    {
        ContentValues args = new ContentValues();
        args.put("NAME", Status);
        return db.update("ATTENDANCE", args,
                null, null) > 0;
    }

    //---updates a LOGIN_STATUS : LOGIN or LOGOUT
    public boolean updateAttendance(String s, int total, int attended, float p)
    {
        if(attended==total)
            p = 100;
        ContentValues args = new ContentValues();
        args.put("SUB", s);
        args.put("TOTAL", total);
        args.put("ATTENDED", attended);
        args.put("PERCENT", p);

        return db.update("ATTENDANCE", args, "SUB='" + s+"'", null) > 0;
    }

    public boolean updateDayAttendance(String d, String s, String sta)
    {
        ContentValues args = new ContentValues();

        args.put("SUB", s);
        args.put("DAT", d);
        args.put("STATUS", sta);

        return db.update("DAY_ATTENDANCE", args, "DAT='" +d+"'", null) > 0;
    }


    //executing query and it returns fields which are specified in the query ..........
    public Cursor getQueryResult(String MY_QUERY) throws SQLException
    {
        return db.rawQuery(MY_QUERY, null);
    }
}
