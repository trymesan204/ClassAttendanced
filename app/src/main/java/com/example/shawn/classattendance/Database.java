package com.example.shawn.classattendance;

import android.content.ContentValues;
import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

class Database extends SQLiteOpenHelper {

    //declare database name and version
    //Attendance
    //Attendanced
    public static final String DB_NAME= "Attendances";
    public static final int DB_VERSION= 1;

    //create database
    Database (Context context) {
        super(context, DB_NAME, null, DB_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {


        //create table for storing names of classes
        db.execSQL( "CREATE TABLE CLASSNAME (_id INTEGER PRIMARY KEY AUTOINCREMENT," +
                "NAME TEXT," +
                "SUBJECT TEXT,"+
                "SUBID TEXT,"+
                "YEAR TEXT,"+
                "PART TEXT)" );


        //table for storing name of absents
        db.execSQL("CREATE TABLE ATTENDANCEDATE (_id INTEGER PRIMARY KEY AUTOINCREMENT," +
                "NAME TEXT,"+
                "DATE TEXT," +
                "PRESENT INTEGER," +
                "ABSENT INTEGER)");

        //table for user
        db.execSQL("CREATE TABLE USER (_id INTEGER PRIMARY KEY AUTOINCREMENT,"+
                "NAME TEXT,"+
                "MAIL TEXT)");
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }
}
