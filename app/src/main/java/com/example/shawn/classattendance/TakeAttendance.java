package com.example.shawn.classattendance;

import android.app.AlertDialog;
import android.content.ContentValues;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;
import android.database.sqlite.SQLiteOpenHelper;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.CheckBox;
import android.widget.CursorAdapter;
import android.widget.ListView;
import android.widget.SimpleCursorAdapter;
import android.widget.TextView;
import android.widget.Toast;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;


public class TakeAttendance extends AppCompatActivity {
    public static final String TABLE_NAME = "message";
    private Cursor cursor;
    SQLiteDatabase db;
    SQLiteOpenHelper helper;
    String tableName;
    int noPresent;
    int total;
    ArrayList<String> presentStudents = new ArrayList<>();
    ArrayList<String> absentStudents = new ArrayList<>();
    ArrayList<Student> studentList = new ArrayList<>();

    String todayShow;
    String todayDB;


    private RecyclerView recyclerView;
                    StudentsAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_take_attendance);

        //getting date

        Date date = new Date();

        //showing date in attendance page

        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd");
        TextView textView = (TextView) findViewById(R.id.show_date);
        todayShow = simpleDateFormat.format(date);
        textView.setText(todayShow);

        //setting date into database

        simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd");
        todayDB = simpleDateFormat.format(date);


        //getting table name from intent

        Intent intent = getIntent();
        tableName = intent.getStringExtra("TABLE_NAME");


        //showing students list for attendance with multiple choice

        try {
            helper = new Database(this);
            db = helper.getReadableDatabase();

            cursor = db.query(tableName,
                    new String[]{"_id", "ROLL", "NAME"},
                    null, null, null,null, "ROLL ASC");

            boolean cursorValue = cursor.moveToFirst();

            while (cursorValue) {
                Student student = new Student();
                String name = cursor.getString(2);
                String roll = cursor.getString(1);
                student.setName(name);
                student.setRoll(roll);
                studentList.add(student);
                cursorValue = cursor.moveToNext();
            }

        } catch (SQLiteException e) {
            Toast.makeText(TakeAttendance.this, "Database Unavailable", Toast.LENGTH_SHORT).show();
        }


        //using recycler view

        recyclerView = (RecyclerView) findViewById(R.id.recycler_view_1);

        adapter = new StudentsAdapter(studentList);

        recyclerView.setAdapter(adapter);

        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        //divider
        RecyclerView.ItemDecoration itemDecoration = new DividerItemDecoration(this,DividerItemDecoration.VERTICAL);
        recyclerView.addItemDecoration(itemDecoration);

    }



    public void onClickCheckBox(View view) {
        CheckBox checkBox = (CheckBox) findViewById(R.id.check_box);
        boolean checked =((CheckBox)view).isChecked();
        for(int i=0; i<studentList.size(); i++) {
                studentList.get(i).setPresent(checked);
        }
        adapter.studentList = studentList;
        adapter.notifyDataSetChanged();
    }


   //saving the attendance
   public void onClickSave(View view) {

       onClickPositive();

       //show today's report using AlertDialog

       final AlertDialog.Builder dialog= new AlertDialog.Builder(TakeAttendance.this, R.style.AlertDialogTheme);
       dialog.setTitle(R.string.today_report);

       dialog.setMessage("Total Present:\t\t\t"+noPresent+"\n" +"\n"+
               "Total Absent:\t\t\t"+(total-noPresent));

       dialog.setPositiveButton(R.string.done, new DialogInterface.OnClickListener() {
           @Override
           public void onClick(DialogInterface dialog, int which) {
               dialog.dismiss();

               updateAttendance();


               Intent intent= new Intent(TakeAttendance.this, ClassDetails.class);
               intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
               intent.putExtra("TABLE_NAME",tableName);
               startActivity(intent);
           }
       });

       dialog.setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
           @Override
           public void onClick(DialogInterface dialog, int which) {
               dialog.dismiss();
           }
       });

       dialog.create().show();

   }

   public void onClickPositive() {

       total=studentList.size();

        int i=0;
        noPresent=0;

        while (i< total) {

            if (studentList.get(i).isPresent()){
                noPresent++;
            }

            i++;
        }
   }



   //updating attendance
    public void updateAttendance() {

        //saving date in date table
        String t= tableName + "DATE";

        Cursor cursorvalue = db.query(t,
                new String[] {"_id","DATE"},
                null, null, null, null, "DATE DESC", String.valueOf(1));

        if (cursorvalue.moveToFirst()) {
            if(cursorvalue.getString(1).equals(todayDB)){
                Toast.makeText(this,"Today's Attendance already Taken", Toast.LENGTH_SHORT).show();
                return;
            }else{
                ContentValues date = new ContentValues();
                date.put("DATE",todayDB);
                date.put("SYNC",0);
                db.insert(t,null,date);
            }
            Log.d("showDateX",cursorvalue.getString(1));
        }else {
            ContentValues date = new ContentValues();
            date.put("DATE",todayDB);
            date.put("SYNC",0);
            db.insert(t,null,date);
        }


        try {

            //getting old records for updating from class table

            cursor= db.query(tableName,
                    new String[] {"_id","ROLL","PRESENT","ABSENT","LECTURES"},
                    null, null, null, null, "ROLL ASC");

            int i=0;

            boolean cursorValue= cursor.moveToFirst();

            while(cursorValue){
                //values for attendance sheet

                String tableSheet= tableName + "SHEET";
                ContentValues sheet = new ContentValues();


                //values for total present and total absent of existing student
                int absent = cursor.getInt(3);
                int present= cursor.getInt(2);
                int lectures= cursor.getInt(4);
                lectures++;


                //statements if checked or unchecked

                if (studentList.get(i).isPresent()){
                    present++;
                    presentStudents.add(cursor.getString(1));
                    sheet.put("STATUS","P");
                }
                else {
                    absent++;
                    absentStudents.add(cursor.getString(1));
                    sheet.put("STATUS","A");
                }


                //inserting in sheet table
                sheet.put("ROLL",cursor.getString(1));
                sheet.put("Date",todayDB);
                db.insert(tableSheet, null, sheet);

                //updating the present and absent days in the students' table

                ContentValues contentValues = new ContentValues();
                contentValues.put("PRESENT",present);
                contentValues.put("ABSENT",absent);
                contentValues.put("LECTURES",lectures);
                db.update( tableName, contentValues ,"ROLL=?", new String[] {cursor.getString(1)});

                cursorValue=cursor.moveToNext();

                i++;

            }


            calendarFunction();

        }catch (SQLiteException e) {
            Toast.makeText(TakeAttendance.this, "Database Unavailable", Toast.LENGTH_SHORT).show();
        }
    }


    public void calendarFunction(){

        //today's date for calendar view
        ContentValues contentValues = new ContentValues();
        contentValues.put("NAME",tableName);
        contentValues.put("DATE", todayDB);
        contentValues.put("PRESENT",noPresent);
        contentValues.put("ABSENT",total-noPresent);
        db.insert("ATTENDANCEDATE",null, contentValues);
    }

}