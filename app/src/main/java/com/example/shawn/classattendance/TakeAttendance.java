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
    private SQLiteDatabase db;
    String tableName;
    ListView listView;
    int noPresent;
    int noAbsent;
    int total;
    ArrayList<String> presentStudents = new ArrayList<>();
    ArrayList<String> absentStudents = new ArrayList<>();
    ArrayList<Student> studentList = new ArrayList<>();
    String today;

    private RecyclerView recyclerView;
                    StudentsAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_take_attendance);

        //getting date
        Date date = new Date();
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("EEEE MMMM d, yyyy");
        TextView textView = (TextView) findViewById(R.id.show_date);
        today = simpleDateFormat.format(date);
        textView.setText(today);
        Log.d("dates", String.valueOf(today));
        simpleDateFormat = new SimpleDateFormat(" MMM d ");
        today = simpleDateFormat.format(date);


        Intent intent = getIntent();
        tableName = intent.getStringExtra("TABLE_NAME");


        //showing studentss list for attendance with multiple choice
        try {
            SQLiteOpenHelper helper = new Database(this);
            db = helper.getReadableDatabase();

            cursor = db.query(tableName,
                    new String[]{"_id", "ROLL", "NAME"},
                    null, null, null, null, null);

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
       updateAttendance();

       //show today's report by using a alertdialog
       final AlertDialog.Builder dialog= new AlertDialog.Builder(TakeAttendance.this, R.style.AlertDialogTheme);
       dialog.setTitle(R.string.today_report);
       //Log.d("dialog","Dialog");

       dialog.setMessage("Total Present:\t\t\t"+noPresent+"\n" +"\n"+
                        "Total Absent:\t\t\t"+(total-noPresent));

       dialog.setPositiveButton(R.string.done, new DialogInterface.OnClickListener() {
           @Override
           public void onClick(DialogInterface dialog, int which) {
               Log.d("dialog1","Dialog1");
               dialog.dismiss();
               Intent intent= new Intent(TakeAttendance.this, ClassDetails.class);
               intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
               intent.putExtra("TABLE_NAME",tableName);
               startActivity(intent);
           }
       });

       dialog.create().show();

   }

   //updating attendance
    public void updateAttendance() {
        ArrayList<Student> students = adapter.getStudentList();
        try {
            SQLiteOpenHelper helper = new Database(this);
            db= helper.getReadableDatabase();

            //saving date in date table
            String t= tableName + "DATE";
            ContentValues date = new ContentValues();
            date.put("DATE",today);
            db.insert(t,null,date);

            //getting old records for updating from class table
            cursor= db.query(tableName,
                    new String[] {"_id","ROLL","PRESENT","ABSENT","LECTURES"},
                    null, null, null, null, null);

            int i=0;
            if (cursor.moveToFirst()) {
                i=0;
            }
            noPresent=0;

            do{
                //values for total present and total absent
                i++;
                int absent = cursor.getInt(3);
                int present= cursor.getInt(2);
                int lectures= cursor.getInt(4);
                lectures++;

                //values for attendance sheet
                t= tableName + "SHEET";
                ContentValues sheet = new ContentValues();

                //statements if checked or unchecked
//                if(listView.isItemChecked(i-1)) {
                if (students.get(i-1).isPresent()){
                    present++;
                    noPresent++;
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
                sheet.put("Date",today);
                db.insert(t, null, sheet);

                //inserting in class table
                ContentValues contentValues = new ContentValues();
                contentValues.put("PRESENT",present);
                contentValues.put("ABSENT",absent);
                contentValues.put("LECTURES",lectures);
                db.update( tableName, contentValues ,"_id=?", new String[] {Integer.toString(i)});

            } while(cursor.moveToNext());



            //calendar functions for total present and absent
            cursor = db.query(tableName,
                    new String[] {"COUNT(_id) AS count"},
                    null, null, null, null, null);
            if(cursor.moveToFirst())
                total = cursor.getInt(0);

            //today's date for calendar view
            SimpleDateFormat sdf = new SimpleDateFormat("EEEE MMMM d, yyyy");
            Date date1 = new Date();
            today= sdf.format(date1);

            ContentValues contentValues = new ContentValues();
            contentValues.put("NAME",tableName);
            contentValues.put("DATE", today);
            contentValues.put("PRESENT",noPresent);
            contentValues.put("ABSENT",total-noPresent);
            db.insert("ATTENDANCEDATE",null, contentValues);

        }catch (SQLiteException e) {
            Toast.makeText(TakeAttendance.this, "Database Unavailable", Toast.LENGTH_SHORT).show();
        }
    }
}