package com.mysampleapp;

/**
 * Created by SUNGJIN on 6/28/17.
 */

public class Announcement {
    public String title;
    public String month;
    public String date;
    public String year;

    public Announcement(){
        super();
    }

    public Announcement(String title, String month, String date, String year){
        super();
        this.title = title;
        this.month = month;
        this.date = date;
        this.year = year;
    }
}
