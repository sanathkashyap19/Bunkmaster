package com.sanath.bunkmaster.database;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

/**
 * Created by Sanath on 13-11-2016.
 */

public class TimetableDatabase {

    private static final String TAG = "TimetableDatabase";
    private static final String DATABASE_NAME = "TIMETABLE _DATABASE";   //Database Name
    private static final int DATABASE_VERSION = 3; //Change Version to upgrade db

    private final Context context;

    private TimetableDatabase.DatabaseHelper DBHelper;
    private SQLiteDatabase db;

    public TimetableDatabase(Context ctx)
    {
        this.context = ctx;
        DBHelper = new TimetableDatabase.DatabaseHelper(context);
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

            //Database of timetable
            db.execSQL("CREATE TABLE TIMETABLE(ID INTEGER PRIMARY KEY AUTOINCREMENT," +
                    "DAY TEXT NOT NULL, HOUR TEXT NOT NULL, SUBJECT TEXT)");
        }

        @Override
        public void onUpgrade(SQLiteDatabase db, int oldVersion,
                              int newVersion)
        {
            Log.w(TAG, "Upgrading database from version " + oldVersion
                    + " to "
                    + newVersion + ", which will destroy all old data");

            //Admin Table
            db.execSQL("DROP TABLE IF EXISTS TIMETABLE");

            onCreate(db);
        }
    }

    //---opens the database---
    public TimetableDatabase open() throws SQLException
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
    public long insertTimetable(String DAY, String HOUR, String SUBJECT)
    {
        ContentValues initialValues = new ContentValues();

        initialValues.put("DAY", DAY);
        initialValues.put("HOUR", HOUR);
        initialValues.put("SUBJECT", SUBJECT);

        return db.insert("TIMETABLE", null, initialValues);

    }


    //---deletes a particular Invoice---
    public boolean delete(int rowId)
    {
        return db.delete("TIMETABLE",  "ID" +
                "=" + rowId, null) > 0;
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
        return db.update("TIMETABLE", args,
                null, null) > 0;
    }

    //---updates a LOGIN_STATUS : LOGIN or LOGOUT
    public boolean updateSubject(String DAY, String HOUR, String SUBJECT, String Id )
    {
        ContentValues args = new ContentValues();
        args.put("DAY", DAY);
        args.put("HOUR", HOUR);
        args.put("SUBJECT", SUBJECT);
        return db.update("SUBJECT", args,
                "ID='" + Id+"'", null) > 0;
    }


    //executing query and it returns fields which are specified in the query ..........
    public Cursor getQueryResult(String MY_QUERY) throws SQLException
    {
        return db.rawQuery(MY_QUERY, null);
    }
}
