package com.example.shawn.classattendance;

import android.app.AlertDialog;
import android.app.ListActivity;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;
import android.database.sqlite.SQLiteOpenHelper;
import android.provider.ContactsContract;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.PopupMenu;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.ContextMenu;
import android.view.LayoutInflater;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.CursorAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.SimpleCursorAdapter;
import android.widget.Toast;

import java.io.InputStream;
import java.util.ArrayList;

public class ShowClasses extends AppCompatActivity {
    private SQLiteDatabase db;
    private Cursor cursor;
    ArrayList<String> className = new ArrayList<>();
    public ClassAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_show_classes);


        try {
            SQLiteOpenHelper helper = new Database(this);
            db= helper.getReadableDatabase();

            cursor= db.query("CLASSNAME",
                    new String[] {"_id", "NAME"},
                    null, null, null, null, null);

            //recycler view
            boolean cursorValue = cursor.moveToFirst();

            while(cursorValue) {
                String name= (cursor.getString(1));
                className.add(name);
                cursorValue=cursor.moveToNext();
            }

        } catch(SQLiteException e) {
            Toast toast = Toast.makeText(this, "Database Unavailable", Toast.LENGTH_SHORT);
            toast.show();
        }

        if (className.size()==0){

            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setTitle("No Classes Saved");

            builder.setPositiveButton("Okay", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {

                    dialog.dismiss();
                    Intent intent = new Intent(ShowClasses.this,attendance.class);
                    intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                    startActivity(intent);

                }
            });

            builder.create().show();
        }

        final RecyclerView recyclerView = (RecyclerView) findViewById(R.id.recycler_view);

        // Create adapter passing in the sample user data
        adapter = new ClassAdapter(getApplicationContext(),className);

        /*adapter.notifyItemRemoved(adapter.getAdapterPosition());
        adapter.notifyDataSetChanged();*/

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