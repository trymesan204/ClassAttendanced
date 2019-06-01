package com.example.shawn.classattendance;

import android.content.ContentValues;
import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

class Database extends SQLiteOpenHelper {

    //declare database name and version
    public static final String DB_NAME= "Attendance";
    public static final int DB_VERSION= 1;

    //create database
    Database (Context context) {
        super(context, DB_NAME, null, DB_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {

        //create table for storing routine of classes
        db.execSQL("CREATE TABLE TIMETABLE (_id INTEGER PRIMARY KEY AUTOINCREMENT,"+
                "NAME TEXT,"+
                "DAY TEXT,"+
                "TIME TEXT )");
        Log.d("created1","created1");

        //create table for storing names of classes
        db.execSQL( "CREATE TABLE CLASSNAME (_id INTEGER PRIMARY KEY AUTOINCREMENT," +
                " NAME TEXT," +
                " YEAR TEXT," +
                " DAY TEXT,"+
                " SUBJECT TEXT," +
                " TIME TEXT,"+
                " NUMBER INTEGER )" );
        Log.d("Created","Created");

        //table for storing name of absents
        db.execSQL("CREATE TABLE ATTENDANCEDATE (_id INTEGER PRIMARY KEY AUTOINCREMENT," +
                "NAME TEXT,"+
                "DATE TEXT," +
                "PRESENT INTEGER," +
                "ABSENT INTEGER)");
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }
}
