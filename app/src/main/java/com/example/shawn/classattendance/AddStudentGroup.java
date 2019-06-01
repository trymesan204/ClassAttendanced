package com.example.shawn.classattendance;

import android.Manifest;
import android.app.AlertDialog;
import android.content.ContentValues;
import android.content.DialogInterface;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;
import android.database.sqlite.SQLiteOpenHelper;
import android.os.AsyncTask;
import android.provider.MediaStore;
import android.support.design.widget.TextInputLayout;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

//import com.example.shawn.classattendance.Data.CSVInfo;
//import com.example.shawn.classattendance.Data.Student;
import com.example.shawn.classattendance.R;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;

public class AddStudentGroup extends AppCompatActivity implements View.OnClickListener {
    TextInputLayout startRoll, endRoll, excludeRoll;
    private String tableName;
    SQLiteDatabase db = null;
    int id;
    private static final int MY_PERMISSIONS_REQUEST_READ_STORAGE = 1;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_student_group);

        startRoll = findViewById(R.id.start_roll);
        endRoll = findViewById(R.id.end_roll);
        excludeRoll = findViewById(R.id.exclude_roll);
        tableName = getIntent().getStringExtra("TABLE_NAME");

        (findViewById(R.id.upload_csv_btn)).setOnClickListener(this);
        (findViewById(R.id.save_group_btn)).setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.upload_csv_btn:
                //Permission handling
                if (ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE)
                        != PackageManager.PERMISSION_GRANTED) {
                    // Permission is not granted
                    // Permission is not granted
                    // Should we show an explanation?

                    // No explanation needed; request the permission
                    ActivityCompat.requestPermissions(this,
                            new String[]{Manifest.permission.READ_EXTERNAL_STORAGE},
                            MY_PERMISSIONS_REQUEST_READ_STORAGE);

                    // MY_PERMISSIONS_REQUEST_READ_CONTACTS is an
                    // app-defined int constant. The callback method gets the
                    // result of the request.
                }
                else {
                    (new CSVListHandler()).execute();
                }
                break;
            case R.id.save_group_btn:
                saveGroup();
                break;

        }
    }

    public void saveGroup() {
        String startRollValue = startRoll.getEditText().getText().toString();
        String endRollValue = endRoll.getEditText().getText().toString();
        String[] excludeRollValues = excludeRoll.getEditText().getText().toString().split(" ");
        ArrayList<Student> studentsList= new ArrayList<>();
        if (validateErrors(startRollValue, endRollValue, excludeRollValues)) {
            for (int i= Integer.valueOf(startRollValue);i<= Integer.valueOf(endRollValue);i++){
                if (stringContainsItemFromList(String.valueOf(i), excludeRollValues))
                    continue;
                Student student = new Student("Student "+String.valueOf(i), String.valueOf(i));
                studentsList.add(student);
            }
            saveToDB(studentsList);
        }
    }


    public boolean validateErrors(String first, String second, String[] third) {

        startRoll.setErrorEnabled(false);
        endRoll.setErrorEnabled(false);
        excludeRoll.setErrorEnabled(false);
        if (first.length() == 0) {
            startRoll.setError("Cannot be blank");
        } else if (second.length() == 0) {
            endRoll.setError("Cannot be blank");
        } else if (!TextUtils.isDigitsOnly(first)) {
            startRoll.setError("Must be a number");
        } else if (!TextUtils.isDigitsOnly(second)) {
            endRoll.setError("Must be a number");
        } else if (Integer.valueOf(first)>= Integer.valueOf(second)){
            startRoll.setError("Must be less than end value");
            return false;
        }else{

            for (String el: third){
                if (!TextUtils.isDigitsOnly(el)||
                        Integer.valueOf(el)<=Integer.valueOf(first) ||
                        Integer.valueOf(el)>= Integer.valueOf(second)) {
                    excludeRoll.setError("Select valid value");
                    return false;
                }
            }
            startRoll.setErrorEnabled(false);
            startRoll.setErrorEnabled(false);
            excludeRoll.setErrorEnabled(false);
            return true;
        }
        return false;
    }

    public void saveToDB(ArrayList<Student> students) {
        try {
            SQLiteOpenHelper helper = new Database(AddStudentGroup.this);
            db = helper.getReadableDatabase();

            for (Student s: students) {
                ContentValues contentValues = new ContentValues();
                contentValues.put("NAME", s.getName());
                contentValues.put("ROLL", s.getRoll());
                contentValues.put("PRESENT", 0);
                contentValues.put("ABSENT", 0);
                contentValues.put("LECTURES", 0);
                db.insert(tableName, null, contentValues);
            }
            Toast.makeText(this, "Saved successfully", Toast.LENGTH_SHORT).show();
            finish();
        } catch (SQLiteException e) {
            Toast toast = Toast.makeText(this, "Database Unavialable", Toast.LENGTH_SHORT);
            toast.show();
        }
    }

    public class CSVListHandler extends AsyncTask<String, String, String> {
        ArrayList<CSVInfo> csvList;

        @Override
        protected String doInBackground(String... strings) {
            csvList= new ArrayList<>();
            String selection = "_data LIKE '%.csv'";
            Cursor cursor = null;
            try {
                cursor = getApplicationContext().getContentResolver().query(MediaStore.Files.getContentUri("external"), null, selection, null, "_id DESC");
                if (cursor == null || cursor.getCount() <= 0 || !cursor.moveToFirst()) {
                    // this means error, or simply no results found
                    return "Error";
                }
                do {
                    // Index 1: path, index 8: title
                    Log.i("myLog", cursor.getString(1));
                    Log.i("myLog", cursor.getString(8));
                    CSVInfo info = new CSVInfo();
                    info.name = cursor.getString(8);
                    info.path = cursor.getString(1);
                    csvList.add(info);
                } while (cursor.moveToNext());
                cursor.close();
            } catch (Exception e){

            }
            return null;
        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);
            showCsvList(csvList);
        }
    }

    public void showCsvList(final ArrayList<CSVInfo> csvInfoList){
        // setup the alert builder
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Choose a CSV File");
        // add a list
        ArrayList<String> csvNames = new ArrayList<String>();
        for (CSVInfo i : csvInfoList) {
            csvNames.add(i.name + ".csv");
        }
        String[] array = csvNames.toArray(new String[csvNames.size()]);

        builder.setItems(array, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                CSVInfo selected = csvInfoList.get(which);
                Log.i("myLog", selected.path);
                Log.i("myLog", selected.name);
                try {
                    readFileData(selected.path);
                } catch (FileNotFoundException e) {
                    e.printStackTrace();
                }
            }
        });
        // create and show the alert dialog
        AlertDialog dialog = builder.create();
        dialog.show();

    }

    void readFileData(String path) throws FileNotFoundException {
        ArrayList<Student> students = new ArrayList<>();
        String[] data;
        File file = new File(path);
        if (file.exists()) {
            BufferedReader br = new BufferedReader(new FileReader(file));
            try {
                String csvLine;
                while ((csvLine = br.readLine()) != null) {
                    data = csvLine.split(",");
                    Student s = new Student();
                    try {
                        s.setName(data[1]);
                        s.setRoll(data[0]);
                        students.add(s);
                    } catch (Exception e) {
                        Log.e("Problem", e.toString());
                        Toast.makeText(this, "Error in columns", Toast.LENGTH_SHORT).show();
                    }
                }
            } catch (IOException ex) {
                Toast.makeText(this, "Error reading csv", Toast.LENGTH_SHORT).show();
                throw new RuntimeException("Error in reading CSV file: " + ex);
            }
        } else {
            Toast.makeText(getApplicationContext(), "file not exists", Toast.LENGTH_SHORT).show();
        }
        saveToDB(students);
    }


    public static boolean stringContainsItemFromList(String inputStr, String[] items)
    {
        for (String item : items) {
            if (inputStr.contains(item)) {
                return true;
            }
        }
        return false;
    }

    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           String permissions[], int[] grantResults) {
        switch (requestCode) {
            case MY_PERMISSIONS_REQUEST_READ_STORAGE: {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    // permission was granted, yay! Do the
                    // task you need to do.
                    (new CSVListHandler()).execute();
                } else {
                    // permission denied, boo! Disable the
                    // functionality that depends on this permission.
                    Toast.makeText(this, "Permission not given", Toast.LENGTH_SHORT).show();
                }
                return;
            }

            // other 'case' lines to check for other
            // permissions this app might request.
        }
    }
}
