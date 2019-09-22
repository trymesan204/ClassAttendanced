package com.example.shawn.classattendance;

import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;
import android.database.sqlite.SQLiteOpenHelper;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.AdapterView;
import android.widget.CursorAdapter;
import android.widget.ListView;
import android.widget.SimpleCursorAdapter;
import android.widget.Toast;

import java.util.ArrayList;

public class RemoveStudents extends AppCompatActivity {
    private SQLiteDatabase db;
    private Cursor cursor;
    private String className;
    private RemoveList adapter;
    ArrayList<Student> studentList = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_remove_students);

        Intent intent = getIntent();
        className = intent.getStringExtra("TABLE_NAME");

        try {
            SQLiteOpenHelper helper = new Database(this);
            db= helper.getReadableDatabase();

            cursor= db.query(className,
                    new String[] {"_id","NAME","ROLL"},
                    null,null,null,null,null,null);

            //recycler view
            boolean cursorValue = cursor.moveToFirst();

            while(cursorValue) {
                String name= (cursor.getString(1));
                Student student = new Student();
                student.setName(cursor.getString(1));
                student.setRoll(cursor.getString(2));

                studentList.add(student);

                cursorValue=cursor.moveToNext();
            }

        } catch (SQLiteException e) {
            Toast toast = Toast.makeText(this, "Database Unavailable", Toast.LENGTH_SHORT);
            toast.show();
        }

        final RecyclerView recyclerView = (RecyclerView) findViewById(R.id.recycler_view);

        // Create adapter passing in the sample user data
        adapter = new RemoveList(getApplicationContext(),studentList, className);

        // Attach the adapter to the recyclerview to populate items
        recyclerView.setAdapter(adapter);

        // Set layout manager to position the items
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        //set divider
        RecyclerView.ItemDecoration itemDecoration = new DividerItemDecoration(this, DividerItemDecoration.VERTICAL);
        recyclerView.addItemDecoration(itemDecoration);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        cursor.close();
        db.close();
    }
}
