package com.example.shawn.classattendance;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListAdapter;
import android.widget.ListView;

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

public class SelectSubjects extends AppCompatActivity {

    String className;
    String batch, department, section, year, part, subName, subId;
    ArrayList<String> listSubject = new ArrayList<>();
    ArrayList<Subject> subjectList = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_select_subjects);

        Intent intent= getIntent();
        className= intent.getStringExtra("TABLE_NAME");
        batch= intent.getStringExtra("BATCH");
        department= intent.getStringExtra("DEPARTMENT");
        section= intent.getStringExtra("SECTION");
        year=intent.getStringExtra("YEAR");
        part=intent.getStringExtra("PART");

        ListView listView = (ListView) findViewById(R.id.list_subjects);

        populateSubjects();

        ArrayAdapter<String> arrayAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, listSubject );

        listView.setAdapter(arrayAdapter);

        listView.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                Subject sub = subjectList.get(position);
                subName = sub.getName();
                subId = sub.getId();

                Log.d("subName", subName);
                Log.d("subID",subId);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

    }

    public void onClickNext(View view) {
        Intent intent = new Intent(getApplicationContext(), SyncStudents.class);
        intent.putExtra("TABLE_NAME", className);
        intent.putExtra("BATCH",batch);
        intent.putExtra("DEPARTMENT",department);
        intent.putExtra("SECTION",section);
        intent.putExtra("YEAR",year);
        intent.putExtra("PART",part);
        intent.putExtra("SUBJECT_NAME", subName);
        intent.putExtra("SUBJECT_ID",subId);
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(intent);

    }



    public void populateSubjects() {

        String url = "http://pcampus.edu.np/api/subjects/";

        RequestQueue queue = Volley.newRequestQueue(getApplicationContext());

        final ArrayList<String> Subjects = new ArrayList<>();

        StringRequest sr = new StringRequest(Request.Method.POST, url, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                Log.d("getResponse",response);
                try {
                    JSONArray subjects = new JSONArray(response);
                    for (int i=0; i<subjects.length();i++) {

                        JSONArray subject = subjects.getJSONArray(i);

                        String subjectName = String.valueOf((subject.get(1)));

                        //Log.d("subject",subjectName);
                        listSubject.add(subjectName);

                        String subjectId = String.valueOf(subject.get(0));

                        Subject subjectObject= new Subject(subjectId, subjectName);

                        subjectList.add(subjectObject);

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
                params.put("year",year);
                params.put("part",part);

                return params;
            }
        };

        queue.add(sr);

    }
}
