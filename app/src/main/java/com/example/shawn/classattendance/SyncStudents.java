package com.example.shawn.classattendance;

import android.app.ActionBar;
import android.app.ProgressDialog;
import android.content.ContentValues;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;
import android.database.sqlite.SQLiteOpenHelper;
import android.os.Handler;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.WindowManager;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONException;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Map;

public class SyncStudents extends AppCompatActivity {
    SyncAdapter adapter;
    String className;
    String batch, department, section, year, part, subName, subjectId;
    SQLiteDatabase db;
    ArrayList<Student> studentList = new ArrayList<>();
    ProgressBar syncStudentsProgress;
    RecyclerView recyclerView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sync_students);

        Intent intent= getIntent();
        className= intent.getStringExtra("TABLE_NAME");
        batch= intent.getStringExtra("BATCH");
        department= intent.getStringExtra("DEPARTMENT");
        section= intent.getStringExtra("SECTION");
        year=intent.getStringExtra("YEAR");
        part=intent.getStringExtra("PART");
        subName = intent.getStringExtra("SUBJECT_NAME");
        subjectId = intent.getStringExtra("SUBJECT_ID");

        String section1 = String.valueOf(section.charAt(0));
        String section2 = String.valueOf(section.charAt(1));

        /*ContentValues contentValues = new ContentValues();
        contentValues.put("SUBJECT",subName);

        db.update("CLASSNAME", contentValues, "NAME=?", new String[] {className});*/
        syncStudentsProgress = new ProgressBar(this,null,android.R.attr.progressBarStyleLargeInverse);
        FrameLayout.LayoutParams params = new FrameLayout.LayoutParams(ActionBar.LayoutParams.WRAP_CONTENT, ActionBar.LayoutParams.WRAP_CONTENT);
        params.gravity = Gravity.CENTER;
        ((FrameLayout)getWindow().getDecorView().findViewById(android.R.id.content)).addView(syncStudentsProgress,params);
        syncStudentsProgress.setVisibility(View.VISIBLE);  //To show ProgressBar
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE,
                WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);


        //populate students table
        populateStudents(section1);
        populateStudents(section2);

        recyclerView = (RecyclerView) findViewById(R.id.recycler_view);

        // Create adapter passing in the sample user data
        adapter = new SyncAdapter(studentList);

        // Attach the adapter to the recyclerview to populate items
        recyclerView.setAdapter(adapter);

        // Set layout manager to position the items
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        //set divider
        RecyclerView.ItemDecoration itemDecoration = new DividerItemDecoration(this, DividerItemDecoration.VERTICAL);
        recyclerView.addItemDecoration(itemDecoration);

    }


    public void onClickButton(View view) {
        SQLiteOpenHelper helper = new Database(this);
        db = helper.getReadableDatabase();

        ContentValues contentValues = new ContentValues();
        contentValues.put("NAME", className);
        contentValues.put("SUBJECT", subName);
        contentValues.put("SUBID", subjectId);
        contentValues.put("YEAR", year);
        contentValues.put("PART", part);
        db.insert("CLASSNAME", null, contentValues);


        //create new table for a class
        try {
            db.execSQL("CREATE TABLE " + className + "(_id INTEGER PRIMARY KEY AUTOINCREMENT," +
                    "NAME TEXT," +
                    "ROLL TEXT," +
                    "PRESENT INTEGER," +
                    "ABSENT INTEGER," +
                    "LECTURES INTEGER)");
            //Log.d("Created", className);

        } catch (SQLiteException e) {
            //Log.d("Error",e.getMessage());
            Toast toast = Toast.makeText(this, "Database Unavailable", Toast.LENGTH_SHORT);
            toast.show();
        }


        //create table to store dates of attendance
        String date = className + "DATE";
        try {

            db.execSQL("CREATE TABLE " + date + "(_id INTEGER PRIMARY KEY AUTOINCREMENT," +
                    "DATE TEXT," +
                    "SYNC INTEGER)");

        } catch (SQLiteException e) {
            Toast toast = Toast.makeText(this, "Database Unavailable", Toast.LENGTH_SHORT);
            toast.show();
        }

        //create attendance sheet
        String sheet = className + "SHEET";
        try {
            db.execSQL("CREATE TABLE " + sheet + "(_id INTEGER PRIMARY KEY AUTOINCREMENT," +
                    "ROLL TEXT," +
                    "DATE TEXT," +
                    "STATUS TEXT)");

        } catch (SQLiteException e) {
            Toast toast = Toast.makeText(this, "Database Unavailable", Toast.LENGTH_SHORT);
            toast.show();
        }

        saveToDB(studentList);

        Intent intent = new Intent(getApplicationContext(), ClassDetails.class);
        intent.putExtra("TABLE_NAME", className);
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(intent);
    }



    //getting the data of students using API of Pulchowk Campus
    public void populateStudents(final String section){

        String url = "http://pcampus.edu.np/api/students/";

        RequestQueue queue = Volley.newRequestQueue(getApplicationContext());


        StringRequest sr = new StringRequest(Request.Method.POST, url, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                Log.d("getResponse2",response);
                try {
                    JSONArray students = new JSONArray(response);
                    for (int i=0; i<students.length();i++) {
                        JSONArray student = students.getJSONArray(i);
                        String studentRoll= String.valueOf(student.get(0))+String.valueOf(student.get(1))+String.valueOf(student.get(2));
                        String studentName = String.valueOf(student.get(3));

                        Student student1= new Student(studentName, studentRoll);
                        studentList.add(student1);

                    }

                    getWindow().clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);
                    syncStudentsProgress.setVisibility(View.GONE);
                    // Create adapter passing in the sample user data
                    adapter = new SyncAdapter(studentList);

                    // Attach the adapter to the recyclerview to populate items
                    recyclerView.setAdapter(adapter);

                    // Set layout manager to position the items
                    recyclerView.setLayoutManager(new LinearLayoutManager(getApplicationContext()));

                    //set divider
                    RecyclerView.ItemDecoration itemDecoration = new DividerItemDecoration(getApplicationContext(), DividerItemDecoration.VERTICAL);
                    recyclerView.addItemDecoration(itemDecoration);

                if(studentList.size()==0){
                    Toast.makeText(SyncStudents.this, "Error!", Toast.LENGTH_LONG).show();
                    Intent intent = new Intent(SyncStudents.this,AddClasses.class);
                    intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                    startActivity(intent);
                    finish();

                }

                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                getWindow().clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);
                syncStudentsProgress.setVisibility(View.GONE);
                Log.d("errors",error.toString());
            }
        }
        ) {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String , String> params = new HashMap<String, String>();
                params.put("prog",department);
                params.put("batch",batch);
                params.put("group",section);
                return params;
            }
        };

        queue.add(sr);


    }


    //saving the list of students in database
    public void saveToDB(ArrayList<Student> students) {
        try {
            SQLiteOpenHelper helper = new Database(getApplicationContext());
            db = helper.getReadableDatabase();

            for (Student s: students) {
                ContentValues contentValues = new ContentValues();
                contentValues.put("NAME", s.getName());
                contentValues.put("ROLL", s.getRoll());
                contentValues.put("PRESENT", 0);
                contentValues.put("ABSENT", 0);
                contentValues.put("LECTURES", 0);
                db.insert(className, null, contentValues);
            }
            finish();
            Toast.makeText(this, "Class Saved", Toast.LENGTH_SHORT).show();
        } catch (SQLiteException e) {
            Toast toast = Toast.makeText(this, "Database Unavialable", Toast.LENGTH_SHORT);
            toast.show();
        }
    }
}
