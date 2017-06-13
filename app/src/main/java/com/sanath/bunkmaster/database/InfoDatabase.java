package com.sanath.bunkmaster.database;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

/**
 * Created by Sanath on 01-12-2016.
 */

public class InfoDatabase {

    private static final String TAG = "InfoDatabase";
    private static final String DATABASE_NAME = "INFO_DATABASE";   //Database Name
    private static final int DATABASE_VERSION = 3; //Change Version to upgrade db

    private final Context context;

    private InfoDatabase.DatabaseHelper DBHelper;
    private SQLiteDatabase db;

    public InfoDatabase(Context ctx)
    {
        this.context = ctx;
        DBHelper = new InfoDatabase.DatabaseHelper(context);
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

            //Database of Info
            db.execSQL("CREATE TABLE BASIC_INFO(ID INTEGER PRIMARY KEY AUTOINCREMENT," +
                    "NAME TEXT NOT NULL, COLLEGE TEXT NOT NULL," +
                    "SEM INT NOT NULL, YEAR NOT NULL)");
        }

        @Override
        public void onUpgrade(SQLiteDatabase db, int oldVersion,
                              int newVersion)
        {
            Log.w(TAG, "Upgrading database from version " + oldVersion
                    + " to "
                    + newVersion + ", which will destroy all old data");

            //Admin Table
            db.execSQL("DROP TABLE IF EXISTS BASIC_INFO");
            db.execSQL("DROP TABLE IF EXISTS NUMBERS");

            onCreate(db);
        }
    }

    //---opens the database---
    public InfoDatabase open() throws SQLException
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
    public long insertInfo(String NAME, String COLLEGE, int SEM, int YEAR)
    {
        ContentValues initialValues = new ContentValues();

        initialValues.put("NAME", NAME);
        initialValues.put("COLLEGE", COLLEGE);
        initialValues.put("SEM", SEM);
        initialValues.put("YEAR", YEAR);

        return db.insert("BASIC_INFO", null, initialValues);

    }


    //---deletes a particular Invoice---
    public boolean deleteInfo(int rowId)
    {
        return db.delete("BASIC_INFO",  "ID" +
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
        return db.update("SUBJECT", args,
                null, null) > 0;
    }

    //---updates a LOGIN_STATUS : LOGIN or LOGOUT
    public boolean updateInfo(String n, String c, int s, int y, String Id )
    {
        ContentValues args = new ContentValues();
        args.put("NAME", n);
        args.put("COLLEGE", c);
        args.put("SEM", s);
        args.put("YEAR", y);

        return db.update("BASIC_INFO", args,
                "ID='" + Id+"'", null) > 0;
    }

    public boolean updateSubject(int s, String Id )
    {
        ContentValues args = new ContentValues();
        args.put("SUBJECT", s);

        return db.update("NUMBERS", args,
                "ID='" + Id+"'", null) > 0;
    }

    //executing query and it returns fields which are specified in the query ..........
    public Cursor getQueryResult(String MY_QUERY) throws SQLException
    {
        return db.rawQuery(MY_QUERY, null);
    }
}
