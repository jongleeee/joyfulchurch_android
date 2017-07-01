package com.mysampleapp.util;

import android.app.Activity;
import android.view.View;
import android.widget.EditText;

import com.mysampleapp.SermonActivity;

import java.util.Date;

/**
 * Created by minjungkim on 6/26/17.
 */

public class Sermon {
    private String title;
    private String series;
    private String verse;
    private Date date;
    private String url;

    public Sermon(String title, String series, String verse, Date date, String url) {
        this.title = title;
        this.series = series;
        this.verse = verse;
        this.date = date;
        this.url = url;
    }

    public String getSeries() {
        return this.series;
    }
    public String getTitle() {
        return this.title;
    }
    public String getVerse() {
        return this.verse;
    }
    public Date getDate() {
        return this.date;
    }
    public String getSermonURL() {
        return url;
    }


}
