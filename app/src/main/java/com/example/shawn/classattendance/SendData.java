package com.example.shawn.classattendance;

import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
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

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.HashMap;
import java.util.Map;

public class SendData {

    private static String valueIP=null;
    public String className, subjectName, subjectId, year,part, password;
    Context context;

    public SendData(String className, String subjectName, String subjectId, Context context){
        this.className=className;
        this.subjectId=subjectId;
        this.subjectName=subjectName;
        this.context = context;
    }

    public void onClickSubmit() {

        valueIP= "http://192.168.1.78:3001/attendance/";

        SQLiteOpenHelper helper = new Database(context);
        SQLiteDatabase db = helper.getReadableDatabase();
        String classDate = className + "DATE";

        Cursor cursor = db.query(classDate,
                new String[]{"DATE", "SYNC"},
                "SYNC=?",
                new String[]{String.valueOf(0)},
                null, null, null, null);


        boolean sendJson = cursor.moveToFirst();

        if (!sendJson){
            Toast toast = Toast.makeText(context,"No Data to be Sent",Toast.LENGTH_LONG);
            toast.show();
        } else {
            Toast toast = Toast.makeText(context, "Data Sent to Server", Toast.LENGTH_LONG);
            toast.show();
        }

        while(sendJson) {
            String date=cursor.getString(0);
            Log.d("valueDate",date);
            postNewComment(date);


            ContentValues changeSYNC = new ContentValues();
            changeSYNC.put("SYNC", 1);
            db.update(classDate,changeSYNC,"DATE=?",new String[]{date});

            sendJson=cursor.moveToNext();
        }

    }


    public void postNewComment(final String date){
        RequestQueue queue = Volley.newRequestQueue(context);


        StringRequest sr = new StringRequest(Request.Method.POST, valueIP, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                Log.d("get_response",response);
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {

                Log.d("errors",error.toString());
            }
        }){
            @Override
            public byte[] getBody() throws AuthFailureError {
                String json = null;

                try {
                    DumpJson dumpJson = new DumpJson(context, className, date, subjectName, subjectId, year, part, password);
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
