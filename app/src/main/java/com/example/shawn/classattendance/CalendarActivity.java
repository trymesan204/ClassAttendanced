package com.example.shawn.classattendance;

import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;
import android.database.sqlite.SQLiteOpenHelper;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.applandeo.materialcalendarview.CalendarView;
import com.applandeo.materialcalendarview.EventDay;
import com.applandeo.materialcalendarview.listeners.OnDayClickListener;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class CalendarActivity extends AppCompatActivity {
    private CalendarView mCalendarView;
    LinearLayout mainLinear;
    final int notificationIcon = R.drawable.tick;
    List<EventDay> events;
    List<Event> eventMaps;
    TextView selectedDate;
    DateFormat sdf;

    String tableName;



    Cursor cursor;

    public class Event{
        public Event(EventDay date, String title) {
            this.date = date;
            this.title = title;
        }
        public Event(){
            title = "";
        }

        public EventDay date;
        public String title;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_calendar);


        SQLiteOpenHelper helper = new Database(CalendarActivity.this);
        SQLiteDatabase db= helper.getReadableDatabase();

        mCalendarView = (CalendarView) findViewById(R.id.calendarView);
        mainLinear = findViewById(R.id.calendar_linear);
        selectedDate = findViewById(R.id.sel_date);

        Intent intent = getIntent();
        tableName = intent.getStringExtra("TABLE_NAME");


        //TEst events
        events = new ArrayList<>();
        eventMaps = new ArrayList<>();
        List<Calendar> calendarList = new ArrayList<>();

        sdf = new SimpleDateFormat("EEEE MMMM d, yyyy", Locale.ENGLISH);

        try {
            cursor = db.query("ATTENDANCEDATE",
                    new String[]{"_id","NAME","DATE","PRESENT","ABSENT"},
                    "NAME=?",
                    new String[] {tableName},
                    null, null, null);

        } catch (SQLiteException e) {
            Toast.makeText(this, "Database unavailable", Toast.LENGTH_SHORT).show();
        }

        boolean move = cursor.moveToFirst();

        while (move) {
            String date = cursor.getString(2);
            int present= cursor.getInt(3);
            int absent =cursor.getInt(4);
            try {
                Calendar calendar = Calendar.getInstance();
                calendar.setTime(sdf.parse(date));
                calendarList.add(calendar);
                EventDay eventDay = new EventDay(calendar, notificationIcon);
                events.add(eventDay);
                eventMaps.add(new Event(eventDay, "TotalPresent:  "+ present + "\nTotalAbsent: "+absent));
            } catch (ParseException e) {
                e.printStackTrace();
            }

            move = cursor.moveToNext();
        }


        int i = 0;
        for (Calendar c : calendarList) {
            i += 1;
        }

        //Set events in calendar
        mCalendarView.setDate((Calendar.getInstance()));
        mCalendarView.setEvents(events);

        selectedDate.setText(sdf.format(new Date()));

        mCalendarView.setOnDayClickListener(new OnDayClickListener() {
            @Override
            public void onDayClick(EventDay eventDay) {
                if (((LinearLayout) mainLinear).getChildCount() > 0)
                    ((LinearLayout) mainLinear).removeAllViews();
                selectedDate.setText(sdf.format(eventDay.getCalendar().getTime()));
                //selectedDate.setTextColor(getResources().getColor(R.color.colorText));
                showEvents(eventDay);
            }
        });

    }    //showing absents students


    public void showEvents(EventDay selectedDay){
        for (Event event: eventMaps){
            //If same day as selected
            if (areEqualDays(event.date.getCalendar(), selectedDay.getCalendar())) {
                TextView textView = new TextView(CalendarActivity.this);
                textView.setText(event.title);
                textView.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT));
                float scale = getResources().getDisplayMetrics().density;
                int dpAsPixels = (int) (10 * scale + 0.5f);
                textView.setPadding(dpAsPixels, dpAsPixels, dpAsPixels, dpAsPixels);
                mainLinear.addView(textView);
            }
        }
    }

    private static boolean areEqualDays(Calendar c1, Calendar c2) {
        SimpleDateFormat s = new SimpleDateFormat("dd-MM-yyyy", Locale.ENGLISH);
        return (s.format(c1.getTime()).equals(s.format(c2.getTime())));
    }
}
