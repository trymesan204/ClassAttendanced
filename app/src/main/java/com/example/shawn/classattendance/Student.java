package com.example.shawn.classattendance;

/**
 * Created by User on 3/9/2019.
 */

public class Student {
    private String name;
    private String roll;
    private int present, absent, lectures;

    public boolean isPresent() {
        return isPresent;
    }

    public void setPresent(boolean present) {
        isPresent = present;
    }

    private boolean isPresent;
    public Student(){

    }

    public Student(String name, String roll) {
        this.name = name;
        this.roll = roll;
        this.present = 0;
        this.absent = 0;
        this.lectures = 0;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getRoll() {
        return roll;
    }

    public void setRoll(String roll) {
        this.roll = roll;
    }

    public int getPresent() {
        return present;
    }

    public void setPresent(int present) {
        this.present = present;
    }

    public int getAbsent() {
        return absent;
    }

    public void setAbsent(int absent) {
        this.absent = absent;
    }

    public int getLectures() {
        return lectures;
    }

    public void setLectures(int lectures) {
        this.lectures = lectures;
    }
}
