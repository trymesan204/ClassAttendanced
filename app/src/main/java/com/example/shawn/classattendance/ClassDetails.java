package com.example.shawn.classattendance;

import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;
import android.database.sqlite.SQLiteOpenHelper;
import android.support.v7.app.AlertDialog;
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

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.bumptech.glide.load.data.HttpUrlFetcher;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.ProtocolException;
import java.net.SocketTimeoutException;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;
import java.util.List;

public class ClassDetails extends AppCompatActivity {
    public static final String TABLE_NAME = "message";
    SQLiteDatabase db;
    Cursor cursor;
    String className=null;
    String subject, subjectId;
    int number;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_class_details);


        //getting table name
        Intent intent = getIntent();
        className=intent.getStringExtra("TABLE_NAME");

        try {
            SQLiteOpenHelper helper = new Database(this);
            db= helper.getReadableDatabase();

            //getting details of class
            cursor= db.query("CLASSNAME",
                    new String[] {"_id", "NAME", "SUBJECT","SUBID","YEAR","PART"},
                    "NAME = ?",
                    new String[] {className},
                    null, null, null);

            String year = null,part=null;

            if (cursor.moveToFirst()) {
                subject = cursor.getString(2);
                subjectId = cursor.getString(3);
                year=cursor.getString(4);
                part = cursor.getString(5);
            }
            //setting values in textviews
            TextView textView = (TextView) findViewById(R.id.class_name);
            textView.setText(className);
            textView = (TextView) findViewById(R.id.subject_name);
            textView.setText(subject);
            textView = findViewById(R.id.class_year);
            textView.setText(year);
            textView = findViewById(R.id.class_part);
            textView.setText(part);



            //counting the total number of students
            cursor= db.query(className,
                    new String[] {"COUNT(_id) AS count"},
                    null, null, null, null, null);

            if(cursor.moveToFirst()) {
                number= cursor.getInt(0);
            }
            textView = (TextView) findViewById(R.id.number_students);
            textView.setText(String.valueOf(number));

            //getting the total number of lectures
            cursor = db.query(className,
                    new String[] {"_id","LECTURES"},
                    "_id=?",
                    new String[] {Integer.toString(1)},
                    null, null, null);

            int lectures=0;
            if (cursor.moveToFirst()) {
                lectures = cursor.getInt(1);
            }

            textView= (TextView) findViewById(R.id.number_lectures);
            textView.setText(String.valueOf(lectures));

            cursor.close();

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
                intent.putExtra("TABLE_NAME",className);
                startActivity(intent);
            }
        });

        //when clicked on check status button
        Button button1 = (Button) findViewById(R.id.check_status);
        button1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent= new Intent(ClassDetails.this,DisplaySheet.class);
                intent.putExtra("TABLE_NAME",className);
                startActivity(intent);
            }
        });

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
            intent.putExtra("TABLE_NAME",className);
            startActivity(intent);
        }else if (id== R.id.remove_students) {
            Intent intent = new Intent (ClassDetails.this, RemoveStudents.class);
            intent.putExtra("TABLE_NAME",className);
            startActivity(intent);
        }else if(id==R.id.upload_csv) {
            Intent intent=new Intent(ClassDetails.this, AddStudentGroup.class);
            intent.putExtra("TABLE_NAME",className);
            startActivity(intent);
        }else if (id==R.id.upload_json){
/*
            SendData sendData = new SendData(className, subject, subjectId, getApplicationContext());
            sendData.onClickSubmit();



            AlertDialog.Builder dialog = new AlertDialog.Builder(this);
            dialog.setTitle("Data Sent to the Server");
            dialog.setPositiveButton("Okay", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    dialog.dismiss();
                }
            });

            dialog.create().show();
*/


            Intent intent = new Intent(ClassDetails.this, CheckPassword.class);
            intent.putExtra("TABLE_NAME",className);
            intent.putExtra("SUBJECT_NAME",subject);
            intent.putExtra("SUBJECT_ID",subjectId);
            startActivity(intent);

        }


        return super.onOptionsItemSelected(item);
    }

    public void onClickAttendanceHistory(View view) {
        Intent intent = new Intent (ClassDetails.this,CalendarActivity.class);
        intent.putExtra("TABLE_NAME",className);
        startActivity(intent);
    }

    
}
