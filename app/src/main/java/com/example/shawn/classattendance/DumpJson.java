package com.example.shawn.classattendance;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.JsonWriter;
import android.util.Log;

import java.io.IOException;
import java.io.StringWriter;

public class DumpJson {
    public static Context context;
    public static String className;
    public String date;
    public String subjectName, subjectId, year ,part, password;

    DumpJson(Context context, String className, String date, String subjectName, String subjectId, String year, String part, String password) {
        this.context = context;
        this.className=className;
        this.date = date;
        this.subjectName = subjectName;
        this.subjectId = subjectId;
        this.year = year;
        this.part = part;
        this.password= password;
    }

    public String writeJson() throws IOException {

        SQLiteOpenHelper helper = new Database(context);
        SQLiteDatabase db = helper.getReadableDatabase();

        Cursor cursor = db.query("USER",
                new String[] {"NAME", "MAIL"},
                null,null,null,null,null);

        String insName= null, insId = null;

        if(cursor.moveToFirst()){
            insName=cursor.getString(0);
            insId= cursor.getString(1);
        }

        cursor.close();
        db.close();

        StringWriter stringWriter = new StringWriter();
        JsonWriter jsonWriter = new JsonWriter(stringWriter);
        jsonWriter.beginObject();
        jsonWriter.name("InstructorId").value(insId);
        jsonWriter.name("Password").value(password);
        jsonWriter.name("Instructor").value(insName);
        jsonWriter.name("Date").value(date);
        jsonWriter.name("Class").value(className);
        jsonWriter.name("Year").value(year);
        jsonWriter.name("Part").value(part);
        jsonWriter.name("SubjectId").value(subjectId);
        jsonWriter.name("Subject").value(subjectName);
        jsonWriter.name("Students");

        students(jsonWriter);

        jsonWriter.endObject();

        Log.d("valueJson", String.valueOf(stringWriter));

        return String.valueOf(stringWriter);
    }


    public void students(JsonWriter jsonWriter) throws IOException{
        jsonWriter.beginArray();

        SQLiteOpenHelper helper = new Database(context);
        SQLiteDatabase db = helper.getReadableDatabase();
        String sheet = className + "SHEET";

        Cursor cursor= db.query(sheet,
                new String[] {"ROLL","STATUS"},
                "DATE=?",
                new String[] {String.valueOf(date)},
                null, null, "ROLL ASC");

        boolean cursorValue = cursor.moveToFirst();
        //Log.d("valuecursor", String.valueOf(cursorValue));

        Cursor cursor2= db.query(className,
                new String[] {"ROLL","NAME"},
                null, null, null, null, "ROLL ASC");

        boolean cursorValue1 = cursor2.moveToFirst();


        while (cursorValue && cursorValue1){
            String roll= (cursor.getString(0));
            String status = cursor.getString(1);
            String name= cursor2.getString(1);
            studentDetails(jsonWriter, roll, status, name);
            cursorValue=cursor.moveToNext();
            cursorValue1= cursor2.moveToNext();
        }

        jsonWriter.endArray();
    }

    public static void studentDetails(JsonWriter jsonWriter, String roll, String status, String name ) throws IOException{
        jsonWriter.beginObject();
        jsonWriter.name("Roll").value(roll);
        jsonWriter.name("Name").value(name);
        jsonWriter.name("Status").value(status);
        jsonWriter.endObject();
    }

}