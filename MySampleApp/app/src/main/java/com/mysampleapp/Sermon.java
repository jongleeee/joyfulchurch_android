package com.mysampleapp;

/**
 * Created by SUNGJIN on 6/22/17.
 */

public class Sermon {
    public String title;
    public String title1;
    public String verse;
    public String month;
    public String date;
    public String year;

    public Sermon(){
        super();
    }

    public Sermon(String title, String title1, String verse, String month, String date, String year){
        super();
        this.title = title;
        this.title1 = title1;
        this.verse = verse;
        this.month = month;
        this.date = date;
        this.year = year;
    }
}
