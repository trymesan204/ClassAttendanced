package com.example.shawn.classattendance;

import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;
import android.database.sqlite.SQLiteOpenHelper;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.CursorAdapter;
import android.widget.ListView;
import android.widget.SimpleCursorAdapter;
import android.widget.Toast;

public class RemoveStudents extends AppCompatActivity {
    private SQLiteDatabase db;
    private Cursor cursor;
    private String className;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_remove_students);

        Intent intent = getIntent();
        className = intent.getStringExtra("TABLE_NAME");

        ListView listView = (ListView) findViewById(R.id.show_student_list);

        try {
            SQLiteOpenHelper helper = new Database(this);
            db= helper.getReadableDatabase();

            cursor= db.query(className,
                    new String[] {"_id","NAME"},
                    null,null,null,null,null,null);

            CursorAdapter cursorAdapter = new SimpleCursorAdapter(this,
                    android.R.layout.simple_list_item_1,
                    cursor,
                    new String[] {"NAME"},
                    new int[] {android.R.id.text1},
                    0);

            listView.setAdapter(cursorAdapter);

            listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                    int x= position +1;
                    db.delete(className,"_id=?",new String[] {Integer.toString(x)});
                    Toast.makeText(getApplicationContext(), "Item number"+x+"removed", Toast.LENGTH_SHORT).show();
                }
            });

        } catch (SQLiteException e) {
            Toast toast = Toast.makeText(this, "Database Unavailable", Toast.LENGTH_SHORT);
            toast.show();
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        cursor.close();
        db.close();
    }
}
