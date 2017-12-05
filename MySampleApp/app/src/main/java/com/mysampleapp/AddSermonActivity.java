package com.mysampleapp;

import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.CalendarView;
import android.widget.EditText;
import com.mysampleapp.util.Sermon;
import com.mysampleapp.util.SermonHandler;

import java.util.Date;

public class AddSermonActivity extends AppCompatActivity {
    private EditText title;
    private EditText verse;
    private EditText series;
    private EditText url;
    private CalendarView date;
    private Button save;
    private SermonHandler sermonHandler = new SermonHandler();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_sermon);

        this.title = (EditText) findViewById(R.id.add_sermon_title);
        this.verse = (EditText) findViewById(R.id.add_sermon_verse);
        this.series = (EditText) findViewById(R.id.add_sermon_series);
        this.url = (EditText) findViewById(R.id.add_sermon_url);
        this.date = (CalendarView) findViewById(R.id.add_sermon_date);
        this.save = (Button) findViewById(R.id.add_sermon_save);

        this.save.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String sermonTitle = title.getText().toString().trim();
                String sermonVerse = verse.getText().toString().trim();
                String sermonSeries = series.getText().toString().trim();
                String sermonURL = url.getText().toString().trim();
                Date sermonDate = new Date(date.getDate()/1000);
                Sermon sermonAdd = new Sermon(sermonTitle, sermonSeries, sermonVerse, sermonDate, sermonURL);

                new AsyncAddSermonActivity().execute(sermonAdd);
            }
        });
    }

    private class AsyncAddSermonActivity extends AsyncTask<Sermon, Void, Void> {
        @Override
        protected Void doInBackground(Sermon... sermons) {
            sermonHandler.save(sermons[0]);
            return null;
        }
    }
}
