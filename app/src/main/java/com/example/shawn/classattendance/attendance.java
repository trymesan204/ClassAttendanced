package com.example.shawn.classattendance;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;
import android.database.sqlite.SQLiteOpenHelper;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.content.Intent;
import android.widget.AdapterView;
import android.widget.CursorAdapter;
import android.widget.ListView;
import android.widget.SimpleCursorAdapter;
import android.widget.Toast;

import java.text.SimpleDateFormat;
import java.util.Date;

public class attendance extends AppCompatActivity {
    //private SQLiteDatabase db;
    //private Cursor cursor;
    //String tableName;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_attendance);


    }

    //action when add new class button is clicked
    public void onClickAddClass (View view) {

        Date date = new Date();
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("MMM d");
        String today = simpleDateFormat.format(date);
        Log.d("dates",String.valueOf(today));

        Intent intent = new Intent (this, AddClasses.class);
        startActivity(intent);
    }

    public void onClickShowClasses(View view) {
        Intent intent = new Intent(attendance.this,ShowClasses.class);
        startActivity(intent);
    }

}
