package com.example.shawn.classattendance;

import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;
import android.database.sqlite.SQLiteOpenHelper;
import android.os.Environment;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.CursorAdapter;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.SimpleCursorAdapter;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextClock;
import android.widget.TextView;
import android.widget.Toast;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.sql.Time;
import java.text.SimpleDateFormat;
import java.util.Calendar;

public class ShowSheet extends AppCompatActivity {
    public static final String TABLE_NAME = "message";
    private Cursor cursor;
    private Cursor cursor1;
    private Cursor cursor2;
    String tableName;
    private SQLiteDatabase db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_show_sheet);
        Intent intent = getIntent();
        tableName = intent.getStringExtra("TABLE_NAME");

        init();
    }


    public void init() {

        SQLiteOpenHelper helper = new Database(this);
        db= helper.getReadableDatabase();
        try {


            cursor= db.query(tableName,
                    new String[] {"_id","ROLL","NAME","PRESENT","ABSENT","LECTURES"},
                    null, null, null, null, "ROLL ASC");

        }catch (SQLiteException e) {
            Toast.makeText(ShowSheet.this, "Database Unavailable", Toast.LENGTH_SHORT).show();
        }

        TableLayout tableLayout = (TableLayout) findViewById(R.id.show_table);
        TableRow row = new TableRow(this);
        TableRow.LayoutParams layoutParams = new TableRow.LayoutParams(TableRow.LayoutParams.WRAP_CONTENT);
        TextView t1= new TextView(this);
        TextView t2 = new TextView(this);
        TextView t3 = new TextView(this);
        TextView t4 = new TextView(this);
        TextView t5 = new TextView(this);
        t1.setText(" ROLL ");
        t1.setTextSize(16);
        t1.setTextColor(getResources().getColor(R.color.colorText));
        row.addView(t1);
        t2.setTextSize(16);
        t2.setGravity(Gravity.CENTER);
        t2.setTextColor(getResources().getColor(R.color.colorText));
        t2.setText(" NAME ");
        row.addView(t2);

        //add date in the table
        String t = tableName + "DATE";

        //getting value of dates
        try {
            cursor2 = db.query(t,
                    new String[] {"_id","DATE"},
                    null,null,null,null,null);
        }catch (SQLiteException e) {
            Toast.makeText(ShowSheet.this, "Database Unavailable", Toast.LENGTH_SHORT).show();
        }


        //adding extra columns for date
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("MM d");

        TextView tx= new TextView(this);
        boolean cursorValue1 = cursor2.moveToFirst();
        while(cursorValue1) {
            String date = " "+cursor2.getString(1)+" ";
            tx.setText(date);
            tx.setTextSize(16);
            tx.setTextColor(getResources().getColor(R.color.colorText));
            row.addView(tx);
            tx = new TextView(this);
            cursorValue1=cursor2.moveToNext();
        }


        t3.setText(" PRESENT ");
        t3.setTextSize(16);
        t3.setTextColor(getResources().getColor(R.color.colorText));
        row.addView(t3);
        t4.setText(" ABSENT ");
        t4.setTextSize(16);
        t4.setTextColor(getResources().getColor(R.color.colorText));
        row.addView(t4);
        t5.setText(" ATTENDANCE PERCENT ");
        t5.setTextSize(16);
        t5.setTextColor(getResources().getColor(R.color.colorText));
        row.addView(t5);
        tableLayout.addView(row);


        //entering data of students in the table
        boolean cursorValue= cursor.moveToFirst();
        int percentage= 0;
        int lectures = cursor.getInt(5);
        if(lectures==0)
            lectures=1;

        TextView roll,name, present,absent,percent;
        int i=0;

        while(cursorValue){
            row = new TableRow(this);

            roll = new TextView(this);
            name = new TextView(this);
            present = new TextView(this);
            absent =  new TextView( this);
            percent = new TextView(this);
            percentage = cursor.getInt(3)*100 /lectures;

            roll.setGravity(Gravity.CENTER);
            roll.setText((cursor.getString(1)));
            name.setGravity(Gravity.CENTER);
            name.setText(cursor.getString(2));
            present.setGravity(Gravity.CENTER);
            present.setText(String.valueOf(cursor.getInt(3)));
            absent.setGravity(Gravity.CENTER);
            absent.setText(String.valueOf(cursor.getInt(4)));
            percent.setGravity(Gravity.CENTER);
            percent.setText(String.valueOf(percentage));

            row.addView(roll);
            row.addView(name);

            //adding data of students
            t = tableName + "SHEET";
            try {
                cursor1 = db.query(t,
                        new String[] {"ROLL","STATUS"},
                        "ROLL=?",
                        new String[] {(cursor.getString(1))},
                        null,null,null);

            }catch (SQLiteException e) {
                Toast.makeText(ShowSheet.this, "Database Unavailable", Toast.LENGTH_SHORT).show();
            }
            //adding extra columns for date
            tx= new TextView(this);
            cursorValue1 = cursor1.moveToFirst();
            while(cursorValue1) {
                tx.setText((cursor1.getString(1)));
                tx.setGravity(Gravity.CENTER);
                row.addView(tx);
                tx = new TextView(this);
                cursorValue1=cursor1.moveToNext();
            }

            row.addView(present);
            row.addView(absent);
            row.addView(percent);


            tableLayout.addView(row);

            i++;
            cursorValue = cursor.moveToNext();
        }
    }


    private void exportTheDB() throws IOException
    {
        //SQLiteOpenHelper helper = new Database(this);
        //SQLiteDatabase sampleDB = helper.getReadableDatabase();
        File myFile;
        Calendar cal = Calendar.getInstance();
        SimpleDateFormat sdf = new SimpleDateFormat("MM-dd-yyyy");
        String TimeStampDB = sdf.format(cal.getTime());
        String path = Environment.getExternalStorageDirectory().getAbsolutePath()+"/Attendance/";
        Log.d("paths",path);

        try {
            new File(path  ).mkdir();
            myFile = new File(path+tableName+TimeStampDB+".csv");
            Log.d("Datases","1");
            myFile.createNewFile();
            Log.d("Datases","2");

            FileOutputStream fOut = new FileOutputStream(myFile);
            OutputStreamWriter myOutWriter = new OutputStreamWriter(fOut);

            //giving the column names
            myOutWriter.append("Roll,Name,");

            boolean cursorValue1 = cursor2.moveToFirst();
            while(cursorValue1) {
                myOutWriter.append((cursor2.getString(1)));
                myOutWriter.append(",");
                cursorValue1=cursor2.moveToNext();
            }

            myOutWriter.append("PRESENT,ABSENT,ATTENDANCE%");
            myOutWriter.append("\n");


            boolean cursorValue= cursor.moveToFirst();
            int percentage= 0;
            int lectures = cursor.getInt(5);
            if(lectures==0)
                lectures=1;

            while(cursorValue){

                percentage = cursor.getInt(3)*100 /lectures;

                myOutWriter.append(String.valueOf(cursor.getInt(1)));
                myOutWriter.append(",");
                myOutWriter.append(cursor.getString(2));
                myOutWriter.append(",");

                //data from sheet table
                String t=tableName+"SHEET";
                try {
                    cursor1 = db.query(t,
                            new String[] {"ROLL","STATUS"},
                            "ROLL=?",
                            new String[] {(cursor.getString(1))},
                            null,null,null);

                }catch (SQLiteException e) {
                    Toast.makeText(ShowSheet.this, "Database Unavailable", Toast.LENGTH_SHORT).show();
                }

                //adding extra columns for date
                cursorValue1 = cursor1.moveToFirst();
                while(cursorValue1) {
                    myOutWriter.append((cursor1.getString(1)));
                    myOutWriter.append(",");
                    cursorValue1=cursor1.moveToNext();
                }

                myOutWriter.append(String.valueOf(cursor.getInt(3)));
                myOutWriter.append(",");
                myOutWriter.append(String.valueOf(cursor.getInt(4)));
                myOutWriter.append(",");
                myOutWriter.append(String.valueOf(percentage));
                myOutWriter.append("\n");

                cursorValue = cursor.moveToNext();
            }
            myOutWriter.close();
            fOut.close();

        } catch (SQLiteException se)
        {
            Log.e("okas","Could not create or Open the database");
        }

    }

    public void onClickFloat(View view) {

        try {
            exportTheDB();
            Toast toast = Toast.makeText(this,"Status Downloaded",Toast.LENGTH_SHORT);
            toast.show();

        } catch (IOException e) {
            Toast toast = Toast.makeText(this,"Unavailable",Toast.LENGTH_SHORT);
            toast.show();
        }

    }


    @Override
    public void onDestroy() {
        super.onDestroy();
        cursor2.close();
        cursor1.close();
        cursor.close();
        db.close();
    }
}
