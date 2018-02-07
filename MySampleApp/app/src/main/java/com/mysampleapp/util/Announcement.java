package com.mysampleapp.util;

import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Created by minjungkim on 7/9/17.
 */

public class Announcement {
    private String title;
    private String category;
    private String content;
    private Date date;

    public Announcement(String title, String category, String content, Date date) {
        this.title = title;
        this.category = category;
        this.content = content;
        this.date = date;
    }

    public String getTitle() {
        return this.title;
    }

    public String getCategory() {
        return this.category;
    }

    public String getContent() {
        return this.content;
    }

    public Date getDate() {
        return this.date;
    }

    public String getDay() {
        SimpleDateFormat df = new SimpleDateFormat("dd");
        return df.format(getDate());
    }
    public String getMonth() {
        SimpleDateFormat df = new SimpleDateFormat("MMM");
        return df.format(getDate()).toUpperCase();
    }

    public String getYear() {
        SimpleDateFormat df = new SimpleDateFormat("yyyy");
        return df.format(getDate());
    }
}
