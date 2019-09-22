package com.example.shawn.classattendance;

import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.provider.ContactsContract;
import android.support.design.widget.TextInputLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

public class SetIPAddress extends AppCompatActivity {

    static String valueIP=null;
    public String className, subjectName, subjectId, year, part, classDate, password;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_set_ipaddress);

        Intent intent = getIntent();
        className=intent.getStringExtra("TABLE_NAME");
        subjectName = intent.getStringExtra("SUBJECT_NAME");
        subjectId = intent.getStringExtra("SUBJECT_ID");
        password = intent.getStringExtra("PASSWORD");

    }

    public void onClickSubmit(View view) throws ParseException {

        TextInputLayout textInputLayout = findViewById(R.id.new_ip);
        valueIP= textInputLayout.getEditText().getText().toString();

        if (valueIP.length()==0)
            valueIP= "http://10.100.20.251:3001/attendance/";



        SQLiteOpenHelper helper = new Database(getApplicationContext());
        SQLiteDatabase db = helper.getReadableDatabase();
        classDate = className + "DATE";

        Cursor cursor1 = db.query("CLASSNAME",
                new String[] {"_id","YEAR","PART"},
                "NAME=?",
                new String[] {className},
                null, null, null);

        if(cursor1.moveToFirst()){
            year=cursor1.getString(1);
            part= cursor1.getString(2);
        }

        Cursor cursor = db.query(classDate,
                new String[]{"DATE", "SYNC"},
                "SYNC=?",
                new String[]{String.valueOf(0)},
                null, null, null, null);


        boolean sendJson = cursor.moveToFirst();


        while(sendJson) {
            String date=cursor.getString(0);

            postNewComment(date);

            sendJson=cursor.moveToNext();
        }

        Intent intent = new Intent(getApplicationContext(), ClassDetails.class);
        intent.putExtra("TABLE_NAME",className);
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(intent);

    }


    public void postNewComment(final String date){

        RequestQueue queue = Volley.newRequestQueue(SetIPAddress.this);

        SQLiteOpenHelper helper = new Database(this);
        final SQLiteDatabase db = helper.getReadableDatabase();


        StringRequest sr = new StringRequest(Request.Method.POST, valueIP, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                Log.d("getResponse3",response);
                try {
                    JSONObject resp = new JSONObject(response);
                    int code = (int) resp.get("code");
                    Log.d("respCode", String.valueOf(code));
                    if(code==200) {
                        ContentValues changeSYNC = new ContentValues();
                        changeSYNC.put("SYNC", 1);
                        db.update(classDate,changeSYNC,"DATE=?",new String[]{date});
                        Toast.makeText(getApplicationContext(),"Data Sent to the Server",Toast.LENGTH_SHORT).show();
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }

            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.d("errors",error.toString());

                Toast.makeText(getApplicationContext(), "Error While Sending Data", Toast.LENGTH_LONG).show();

            }
        }){
            @Override
            public byte[] getBody() throws AuthFailureError {
                String json = null;

                try {
                    DumpJson dumpJson = new DumpJson(getApplicationContext(), className, date, subjectName, subjectId, year, part, password);
                    json = dumpJson.writeJson();
                } catch (IOException e) {
                    e.printStackTrace();
                }

                try {
                    return json == null ? null : json.getBytes("utf-8");
                } catch (UnsupportedEncodingException e) {
                    e.printStackTrace();
                    return null;
                }

            }

            @Override
            public String getBodyContentType() {
                return "application/json;charset utf-8";
            }

            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                Map<String, String> headers = new HashMap<>();
                headers.put("Content-Type", "application/json");
                return headers;
            }
        };
        queue.add(sr);
    }
}
