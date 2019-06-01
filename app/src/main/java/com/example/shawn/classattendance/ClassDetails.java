package com.example.shawn.classattendance;

import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;
import android.database.sqlite.SQLiteOpenHelper;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.CursorAdapter;
import android.widget.ListView;
import android.widget.SimpleCursorAdapter;
import android.widget.TextView;
import android.widget.Toast;

public class ClassDetails extends AppCompatActivity {
    public static final String TABLE_NAME = "message";
    SQLiteDatabase db;
    Cursor cursor;
    String name=null;
    String year,day, time, subject;
    int number;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_class_details);

        //getting table name
        Intent intent = getIntent();
        name=intent.getStringExtra("TABLE_NAME");

        try {
            SQLiteOpenHelper helper = new Database(this);
            db= helper.getReadableDatabase();

            //getting details of class
            cursor= db.query("CLASSNAME",
                    new String[] {"_id", "NAME", "YEAR", "SUBJECT", "NUMBER", "DAY","TIME"},
                    "NAME = ?",
                    new String[] {name},
                    null, null, null);

            if (cursor.moveToFirst()) {
                year = cursor.getString(2);
                subject = cursor.getString(3);
                day= cursor.getString(5);
                time = cursor.getString(6);
            }

            //setting values in textviews
            TextView textView = (TextView) findViewById(R.id.class_name);
            textView.setText(name);
            textView = (TextView) findViewById(R.id.class_year);
            textView.setText(year);
            textView = (TextView) findViewById(R.id.subject_name);
            textView.setText(subject);


            //counting the total number of students
            cursor= db.query(name,
                    new String[] {"COUNT(_id) AS count"},
                    null, null, null, null, null);
            if(cursor.moveToFirst()) {
                number= cursor.getInt(0);
            }
            textView = (TextView) findViewById(R.id.number_students);
            textView.setText(String.valueOf(number));

            //getting the total number of lectures
            cursor = db.query(name,
                    new String[] {"_id","LECTURES"},
                    "_id=?",
                    new String[] {Integer.toString(1)},
                    null, null, null);

            int lectures=0;
            if (cursor.moveToFirst()) {
                lectures = cursor.getInt(1);
            }
            number= cursor.getCount();
            textView= (TextView) findViewById(R.id.number_lectures);
            textView.setText(String.valueOf(lectures));


        } catch(SQLiteException e) {
            Toast toast = Toast.makeText(this, "Database Unavailable", Toast.LENGTH_SHORT);
            toast.show();
        }

        //when clicked on take attendace button
        Button button = (Button) findViewById(R.id.take_attendance);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent= new Intent(ClassDetails.this,TakeAttendance.class);
                intent.putExtra("TABLE_NAME",name);
                startActivity(intent);
            }
        });

        //when clicked on check status button
        Button button1 = (Button) findViewById(R.id.check_status);
        button1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent= new Intent(ClassDetails.this,ShowSheet.class);
                intent.putExtra("TABLE_NAME",name);
                startActivity(intent);
            }
        });

        //setting day and time of class
       /*ListView listView =(ListView) findViewById(R.id.class_day);
        ListView listView1 = (ListView) findViewById(R.id.class_time);

        try{
            cursor=db.query("TIMETABLE",
                    new String[] {"_id","NAME","DAY","TIME"},
                    "NAME = ?",
                    new String[] {name},
                    null,null, null);

            CursorAdapter cursorAdapter= new SimpleCursorAdapter(this,
                    android.R.layout.simple_list_item_1,
                    cursor,
                    new String[] {"DAY"},
                    new int[] {android.R.id.text1},
                    0);
            listView.setAdapter(cursorAdapter);

            CursorAdapter cursorAdapter1= new SimpleCursorAdapter(this,
                    android.R.layout.simple_list_item_1,
                    cursor,
                    new String[] {"TIME"},
                    new int[] {android.R.id.text1},
                    0);
            listView1.setAdapter(cursorAdapter1);

        }catch(SQLiteException e) {
            Toast.makeText(this,"Day and Time not saved",Toast.LENGTH_SHORT).show();
        }*/

    }


    @Override
    public void onDestroy() {
        super.onDestroy();
        cursor.close();
        db.close();
    }

    //creating menu in actionbar
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu2, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id= item.getItemId();
        if (id==R.id.add_students){
            Intent intent = new Intent(ClassDetails.this,SaveStudents.class);
            intent.putExtra("TABLE_NAME",name);
            startActivity(intent);
        }else if (id== R.id.remove_students) {
            Intent intent = new Intent (ClassDetails.this, RemoveStudents.class);
            intent.putExtra("TABLE_NAME",name);
            startActivity(intent);
        }else if(id==R.id.upload_csv) {
            Intent intent=new Intent(ClassDetails.this, AddStudentGroup.class);
            intent.putExtra("TABLE_NAME",name);
            startActivity(intent);
        }
        return super.onOptionsItemSelected(item);
    }

    public void onClickAttendanceHistory(View view) {
        Intent intent = new Intent (ClassDetails.this,CalendarActivity.class);
        intent.putExtra("TABLE_NAME",name);
        startActivity(intent);
    }

    /*public void onClickSheet(View view) {
        Intent intent = new Intent(ClassDetails.this, ShowSheet.class);
        intent.putExtra("TABLE_NAME",name);
        startActivity(intent);
    }*/
}
