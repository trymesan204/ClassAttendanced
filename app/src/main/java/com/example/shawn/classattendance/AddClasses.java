package com.example.shawn.classattendance;

import android.app.ActionBar;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.ContentValues;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;
import android.database.sqlite.SQLiteOpenHelper;
import android.graphics.Color;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.TextInputLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.Spinner;
import android.widget.TextView;
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


public class AddClasses extends AppCompatActivity {
    SQLiteDatabase db;
    String className;
    SQLiteOpenHelper helper;
    ArrayList<Subject> subjectList = new ArrayList<>();
    String department, year, part, subjectName, subjectId;
    AlertDialog.Builder builder;
    ProgressBar subjectLoadingDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_classes);

        helper = new Database(this);
        db = helper.getReadableDatabase();

    }

    public void onClickGetSubjects(View view) {

        Spinner spinner = (Spinner) findViewById(R.id.select_department);
        department = spinner.getSelectedItem().toString();

        spinner = (Spinner) findViewById(R.id.select_year);
        year = spinner.getSelectedItem().toString();

        spinner = (Spinner) findViewById(R.id.select_part);
        part = spinner.getSelectedItem().toString();


        Log.d("addClass", department);
        Log.d("addClass", year);
        Log.d("addClass", part);

        final ArrayList<String> listSubject = new ArrayList<>();
        final ArrayList<String> listSubId = new ArrayList<>();

        builder = new AlertDialog.Builder(this);
        builder.setTitle("Subjects for "+department+' '+year+'/'+part);
        builder.setAdapter(new ArrayAdapter<String>(getApplicationContext(), android.R.layout.select_dialog_item, listSubject),
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        subjectName = (listSubject.get(which));
                        subjectId = (listSubId.get(which));

                        TextView selectSubject = findViewById(R.id.show_subject);
                        selectSubject.setText(subjectName);
                    }
                });

//      builder.create().show();
        //Show progressbar while volley request is serviced

        subjectLoadingDialog = new ProgressBar(this,null,android.R.attr.progressBarStyleLargeInverse);

        FrameLayout.LayoutParams params = new FrameLayout.LayoutParams(ActionBar.LayoutParams.WRAP_CONTENT, ActionBar.LayoutParams.WRAP_CONTENT);
        params.gravity = Gravity.CENTER;
        ((FrameLayout)getWindow().getDecorView().findViewById(android.R.id.content)).addView(subjectLoadingDialog,params);
        subjectLoadingDialog.setVisibility(View.VISIBLE);  //To show ProgressBar
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE,
                WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);


        populateSubjects(department, year, part, listSubject, listSubId);


    }

    public void onClickDone(View view) {

        //getting values of new class

        TextInputLayout textInputLayout1 = (TextInputLayout) findViewById(R.id.class_name);
        String batch = textInputLayout1.getEditText().getText().toString();


        Spinner spinner = (Spinner) findViewById(R.id.select_section);
        String section = spinner.getSelectedItem().toString();


        className = department + batch + '_' + section;

        //setting the edit text not to be blank

        if (batch.length() == 0 || subjectName==null ) {
            if (batch.length() == 0)
                textInputLayout1.setError("cant be blank");
            else
                textInputLayout1.setErrorEnabled(false);

            if (subjectName==null)
                Toast.makeText(this, "Select Subject First", Toast.LENGTH_LONG).show();

        } else {

            //inserting values in the table "CLASSNAME"

            try {
                boolean match = false;

                Cursor cursor = db.query("CLASSNAME",
                        new String[]{"_id", "NAME"},
                        null, null, null, null, null);

                /*Cursor cursor1 = db.query("CLASSNAME",
                        new String[]{"COUNT(_id) AS count"},
                        null, null, null, null, null);

                cursor1.moveToFirst();*/

                //checking for already saved class
                boolean cursorValue = cursor.moveToFirst();

                while(cursorValue) {
                    String name = cursor.getString(1);
                    if (name.equals(className)) {
                        match = true;
                        Log.d("matches", "yes");
                        break;
                    }
                    cursorValue= cursor.moveToNext();
                }

                cursor.close();

                if (!match) {

                    Intent intent = new Intent(getApplicationContext(), SyncStudents.class);
                    intent.putExtra("TABLE_NAME", className);
                    intent.putExtra("BATCH", batch);
                    intent.putExtra("DEPARTMENT", department);
                    intent.putExtra("SECTION", section);
                    intent.putExtra("YEAR", year);
                    intent.putExtra("PART", part);
                    intent.putExtra("SUBJECT_NAME", subjectName);
                    intent.putExtra("SUBJECT_ID",subjectId);
                    intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                    startActivity(intent);
                    finish();

                } else {
                    Toast.makeText(AddClasses.this, "Class Already Saved", Toast.LENGTH_LONG).show();
                }

            } catch (SQLiteException e) {
                Toast toast = Toast.makeText(this, "Database Unavailable", Toast.LENGTH_SHORT);
                toast.show();
            }

        }
    }


    public void onClickCancel(View view) {
        Intent intent = new Intent(AddClasses.this, attendance.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(intent);
    }


    @Override
    public void onDestroy() {
        super.onDestroy();
        db.close();
    }


    public boolean populateSubjects(final String department, final String year, final String part, final ArrayList<String> listSubject, final ArrayList<String> listSubId) {

        String url = "http://pcampus.edu.np/api/subjects/";

        RequestQueue queue = Volley.newRequestQueue(getApplicationContext());

        final ArrayList<String> Subjects = new ArrayList<>();

        StringRequest sr = new StringRequest(Request.Method.POST, url, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                Log.d("getResponse", response);
                try {
                    JSONArray subjects = new JSONArray(response);
                    for (int i = 0; i < subjects.length(); i++) {

                        JSONArray subject = subjects.getJSONArray(i);

                        listSubject.add(String.valueOf(subject.get(1)));
                        listSubId.add(String.valueOf(subject.get(0)));
                    }
                    //Log.d("getResponse", listSubject.toString());
                    builder.setAdapter(new ArrayAdapter<String>(getApplicationContext(), android.R.layout.select_dialog_item, listSubject){
                                           @NonNull
                                           @Override
                                           public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
                                               View view = super.getView(position, convertView, parent);
                                               TextView text1 = (TextView) view.findViewById(android.R.id.text1);
                                               text1.setTextColor(Color.BLACK);
                                               text1.setTextSize(TypedValue.COMPLEX_UNIT_SP, 14);
                                               return view;
                                           }
                                       },
                            new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    subjectName = (listSubject.get(which));
                                    subjectId = (listSubId.get(which));

                                    TextView selectSubject = findViewById(R.id.show_subject);
                                    selectSubject.setText(subjectName);
                                }


                            });
                    getWindow().clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);
                    subjectLoadingDialog.setVisibility(View.GONE);
                    builder.create().show();


                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                getWindow().clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);
                subjectLoadingDialog.setVisibility(View.GONE);
                Toast.makeText(AddClasses.this, "Error fetching data", Toast.LENGTH_SHORT).show();
                Log.d("errors", error.toString());
            }
        }
        ) {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> params = new HashMap<String, String>();
                params.put("prog", department);
                params.put("year", year);
                params.put("part", part);

                return params;
            }
        };

        queue.add(sr);

        return true;
    }
}
