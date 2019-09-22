package com.example.shawn.classattendance;

import android.content.ContentValues;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.support.design.widget.TextInputLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;


public class LoginPage extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login_page);
    }

    public void onClickLogin(View view) {

        TextInputLayout textInputLayout1= (TextInputLayout) findViewById(R.id.user_name);
        String userName= textInputLayout1.getEditText().getText().toString();

        TextInputLayout textInputLayout2 = (TextInputLayout) findViewById(R.id.user_mail);
        String userMail = textInputLayout2.getEditText().getText().toString();

        if (userName.length()==0 || userMail.length()==0){
            if (userName.length()==0)
                textInputLayout1.setError("cant be blank");
            else
                textInputLayout1.setErrorEnabled(false);

            if(userMail.length()==0)
                textInputLayout2.setError("Can't be blank");
            else
                textInputLayout2.setErrorEnabled(false);

        }else {
            SQLiteOpenHelper helper = new Database(this);
            SQLiteDatabase db = helper.getReadableDatabase();

            ContentValues contentValues = new ContentValues();
            contentValues.put("NAME", userName);
            contentValues.put("MAIL", userMail);
            db.insert("USER", null, contentValues);

            db.close();

            getSharedPreferences("BOOT_PREF",MODE_PRIVATE)
                    .edit()
                    .putBoolean("firstboot", false)
                    .commit();

        }

        Intent intent = new Intent(getApplicationContext(), attendance.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(intent);
        finish();
    }
}
