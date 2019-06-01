package com.example.shawn.classattendance;

import android.app.ActionBar;
import android.content.ContentValues;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;
import android.database.sqlite.SQLiteOpenHelper;
import android.support.design.widget.TextInputLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;


public class AddClasses extends AppCompatActivity {
    SQLiteDatabase db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_classes);
    }

    public void onClickDone(View view) {
        //getting values of new class
        TextInputLayout textInputLayout1= (TextInputLayout) findViewById(R.id.class_name);
        String className= textInputLayout1.getEditText().getText().toString();
        TextInputLayout textInputLayout2= (TextInputLayout) findViewById(R.id.class_year);
        String classYear= textInputLayout2.getEditText().getText().toString();
        TextInputLayout textInputLayout3 = (TextInputLayout) findViewById(R.id.subject_name);
        String subjectName = textInputLayout3.getEditText().getText().toString();

        //setting the edittext not to be blank
        if (className.length()==0 || classYear.length()==0 || subjectName.length()==0){
            if (className.length()==0)
                textInputLayout1.setError("cant be blank");
            else
                textInputLayout1.setErrorEnabled(false);

            if (classYear.length()==0)
                textInputLayout2.setError("cant be blank");
            else
                textInputLayout2.setErrorEnabled(false);

            if(subjectName.length()==0)
                textInputLayout3.setError("Can't be blank");
            else
                textInputLayout3.setErrorEnabled(false);

        }else {

            //inserting values in the table "CLASSNAME"

            try {
                boolean match=false;

                SQLiteOpenHelper helper = new Database(this);
                db = helper.getReadableDatabase();

                Cursor cursor = db.query("CLASSNAME",
                         new String[] {"_id","NAME"},
                        null, null, null, null, null);

                Cursor cursor1 = db.query("CLASSNAME",
                        new String[] {"COUNT(_id) AS count"},
                        null, null,null,null,null);

                cursor1.moveToFirst();

                //checking for already saved class

                if(!(cursor1.getInt(0)==0))
                {
                    cursor.moveToFirst();
                    do{
                        String name = cursor.getString(1);
                        if (name.equals(className)){
                            match=true;
                            Log.d("matches", "yes");
                            break;
                        }
                    }while(cursor.moveToNext());

                }

                if(!match) {
                    ContentValues contentValues = new ContentValues();
                    contentValues.put("NAME", className);
                    contentValues.put("YEAR", classYear);
                    contentValues.put("SUBJECT", subjectName);
                    db.insert("CLASSNAME", null, contentValues);
                }

                cursor1.close();
                cursor.close();

            } catch (SQLiteException e) {
                Toast toast = Toast.makeText(this, "Database Unavailable", Toast.LENGTH_SHORT);
                toast.show();
            }

            //create new table for a class
            try {
                db.execSQL("CREATE TABLE " + className + "(_id INTEGER PRIMARY KEY AUTOINCREMENT," +
                        "NAME TEXT," +
                        "ROLL TEXT," +
                        "PRESENT INTEGER," +
                        "ABSENT INTEGER," +
                        "LECTURES INTEGER)");

            } catch (SQLiteException e) {
                Toast toast = Toast.makeText(this,"Class Already Saved",Toast.LENGTH_SHORT);
                toast.show();
            }


            //create table to store dates of attendance
            String date = className + "DATE";
            try {

                db.execSQL("CREATE TABLE " + date + "(_id INTEGER PRIMARY KEY AUTOINCREMENT," +
                        "DATE TEXT)");
                Log.d("attdate","datecreated");

            }catch(SQLiteException e) {
                Toast toast = Toast.makeText(this,"Class Already Saved",Toast.LENGTH_SHORT);
                toast.show();
            }

            //create attendance sheet
            String sheet = className + "SHEET";
            try {
                db.execSQL("CREATE TABLE " + sheet + "(_id INTEGER PRIMARY KEY AUTOINCREMENT,"+
                        "ROLL TEXT,"+
                        "DATE TEXT," +
                        "STATUS TEXT)");
                Log.d("sheet","sheetcreated");

            } catch(SQLiteException e) {
                Toast toast = Toast.makeText(this, "Database Unavailable",Toast.LENGTH_SHORT);
                toast.show();
            }

            Intent intent = new Intent(AddClasses.this, ClassDetails.class);
            intent.putExtra("TABLE_NAME", className);
            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            startActivity(intent);
            finish();
        }
    }

    public void onClickCancel(View view) {
        Intent intent = new Intent(AddClasses.this, attendance.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(intent);
    }

}
