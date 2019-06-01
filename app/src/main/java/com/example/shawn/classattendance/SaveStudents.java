package com.example.shawn.classattendance;

import android.content.ContentValues;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;
import android.database.sqlite.SQLiteOpenHelper;
import android.support.design.widget.TextInputLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import org.w3c.dom.Text;

public class SaveStudents extends AppCompatActivity {
    private static final String TABLE_NAME = "message";
    private String tableName;
    SQLiteDatabase db = null;
    int id;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_save_students);

        Intent intent = getIntent();
        tableName = intent.getStringExtra("TABLE_NAME");
        Log.d("table",tableName);
    }

    public void onClickDone(View view) {

        TextInputLayout textInputLayout= (TextInputLayout) findViewById(R.id.student_name);
        String name= textInputLayout.getEditText().getText().toString();
        TextInputLayout textInputLayout1= (TextInputLayout) findViewById(R.id.student_roll);
        String roll = textInputLayout1.getEditText().getText().toString();
        if(name.length()==0 || roll.length()==0) {
            if (name.length()==0)
                textInputLayout.setError("Can't be blank");
            else
                textInputLayout.setErrorEnabled(false);
            if (roll.length()==0)
                textInputLayout1.setError("Can't be blank");
            else
                textInputLayout1.setErrorEnabled(false);
        }else {
            try{
                SQLiteOpenHelper helper= new Database(SaveStudents.this);
                db= helper.getReadableDatabase();

                ContentValues contentValues = new ContentValues();
                contentValues.put("NAME",name);
                contentValues.put("ROLL", roll);
                contentValues.put("PRESENT", 0);
                contentValues.put("ABSENT",0);
                contentValues.put("LECTURES",0);
                db.insert( tableName, null,contentValues);

            }catch (SQLiteException e) {
                Toast toast = Toast.makeText(this, "Database Unavialable",Toast.LENGTH_SHORT);
                toast.show();
            }

            Intent intent = new Intent(SaveStudents.this,ClassDetails.class);
            intent.putExtra("TABLE_NAME",tableName);
            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            startActivity(intent);
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }

    public void onClickCancel(View view) {
        Intent intent = new Intent(SaveStudents.this,ClassDetails.class);
        intent.putExtra("TABLE_NAME",tableName);
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(intent);
    }
}
