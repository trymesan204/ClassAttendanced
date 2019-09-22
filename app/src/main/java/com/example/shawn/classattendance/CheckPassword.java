package com.example.shawn.classattendance;

import android.content.Intent;
import android.support.design.widget.TextInputEditText;
import android.support.design.widget.TextInputLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;

import java.util.Set;

public class CheckPassword extends AppCompatActivity {

    String className, subject, subjectId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_check_password);

        Intent intent = getIntent();
        className = intent.getStringExtra("TABLE_NAME");
        subject = intent.getStringExtra("SUBJECT_NAME");
        subjectId = intent.getStringExtra("SUBJECT_ID");
    }

    public void onClickPassword(View view){

        TextInputLayout textInputLayout = (TextInputLayout) findViewById(R.id.enterPassword);
        String password = textInputLayout.getEditText().getText().toString();

        Intent intent = new Intent(CheckPassword.this, SetIPAddress.class);
        intent.putExtra("TABLE_NAME",className);
        intent.putExtra("SUBJECT_NAME",subject);
        intent.putExtra("SUBJECT_ID",subjectId);
        intent.putExtra("PASSWORD",password);
        startActivity(intent);
    }
}
