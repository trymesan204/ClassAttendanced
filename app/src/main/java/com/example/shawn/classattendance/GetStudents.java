package com.example.shawn.classattendance;

import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;
import android.view.View;
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
import java.util.HashMap;
import java.util.Map;

public class GetStudents {

    String className,batch,department,section, year,part, subjectName;
    Context context;
    ArrayList<Student> studentList = new ArrayList<>();

    public GetStudents(String className, String batch, String department,String section,String year,String part,String subjectName, Context context){
        this.className=className;
        this.batch=batch;
        this.department=department;
        this.section=section;
        this.year=year;
        this.part=part;
        this.subjectName=subjectName;
        this.context=context;
    }

    public void saveStudents() {

        String section1 = String.valueOf(section.charAt(0));
        String section2 = String.valueOf(section.charAt(1));

        populateStudents(section1,section2);

        saveToDB(studentList);
/*
        Intent intent = new Intent(context, ClassDetails.class);
        intent.putExtra("TABLE_NAME", className);
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        context.startActivity();*/
    }



    //getting the data of students using API of Pulchowk Campus
    public void populateStudents(final String section1, final String section2){

        String url = "http://pcampus.edu.np/api/students/";

        RequestQueue queue = Volley.newRequestQueue(context);


        StringRequest sr = new StringRequest(Request.Method.POST, url, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                Log.d("getResponse",response);
                try {
                    JSONArray students = new JSONArray(response);
                    for (int i=0; i<students.length();i++) {
                        JSONArray student = students.getJSONArray(i);
                        String studentRoll= String.valueOf(student.get(0))+String.valueOf(student.get(1))+String.valueOf(student.get(2));
                        String studentName = String.valueOf(student.get(3));

                        Student student1= new Student(studentName, studentRoll);
                        studentList.add(student1);
                    }

                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {

                Log.d("errors",error.toString());
            }
        }
        ) {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String , String> params = new HashMap<String, String>();
                params.put("prog",department);
                params.put("batch",batch);
                params.put("group",section1);
                return params;
            }
        };

        queue.add(sr);

        sr = new StringRequest(Request.Method.POST, url, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                Log.d("getresponse",response);
                try {
                    JSONArray students = new JSONArray(response);
                    for (int i=0; i<students.length();i++) {
                        JSONArray student = students.getJSONArray(i);
                        String studentRoll= String.valueOf(student.get(0))+String.valueOf(student.get(1))+String.valueOf((student.get(2)));
                        String studentName = String.valueOf(student.get(3));

                        Student student1= new Student(studentName, studentRoll);

                        studentList.add(student1);
                    }

                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {

                Log.d("errors",error.toString());
            }
        }
        )
        {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String , String> params = new HashMap<String, String>();
                params.put("prog",department);
                params.put("batch",batch);
                params.put("group",section2);
                return params;
            }
        };
        queue.add(sr);

    }


    //saving the list of students in database
    public void saveToDB(ArrayList<Student> students) {
        try {
            SQLiteOpenHelper helper = new Database(context);
            SQLiteDatabase db = helper.getReadableDatabase();

            for (Student s: students) {
                ContentValues contentValues = new ContentValues();
                contentValues.put("NAME", s.getName());
                contentValues.put("ROLL", s.getRoll());
                contentValues.put("PRESENT", 0);
                contentValues.put("ABSENT", 0);
                contentValues.put("LECTURES", 0);
                db.insert(className, null, contentValues);
            }
            Toast.makeText(context, "Saved successfully", Toast.LENGTH_SHORT).show();

        } catch (SQLiteException e) {

            Toast toast = Toast.makeText(context, "Database Unavialable", Toast.LENGTH_SHORT);
            toast.show();
        }
    }

}
