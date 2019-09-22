package com.example.shawn.classattendance;

public class Subject {

    private String name;
    private String id;

    public Subject(String id, String name) {
        this.name = name;
        this.id = id;
    }

    public String getName(){
        return this.name;
    }

    public String getId() {
        return this.id;
    }
}
