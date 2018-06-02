package com.mysampleapp;

import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.CalendarView;
import android.widget.EditText;
import android.graphics.Color;

import com.mysampleapp.util.Announcement;
import com.mysampleapp.util.AnnouncementHandler;

import java.util.Date;

public class AddAnnouncementActivity extends AppCompatActivity {
    private EditText title;
    private EditText category;
    private EditText content;
    private CalendarView date;
    private Button save;
    private AnnouncementHandler announcementHandler = new AnnouncementHandler();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_announcement);

        this.title = (EditText) findViewById(R.id.add_announcement_title);
        this.category = (EditText) findViewById(R.id.add_announcement_category);
        this.content = (EditText) findViewById(R.id.add_announcement_content);
        this.date = (CalendarView) findViewById(R.id.add_announcement_date);
        this.save = (Button) findViewById(R.id.add_announcement_save);

        this.save.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String announcementTitle = title.getText().toString().trim();
                String announcementCategory = category.getText().toString().trim();
                String announcementContent = content.getText().toString().trim();

                Date announcementDate = new Date(date.getDate()/1000);
                Announcement announcement = new Announcement(announcementTitle, announcementCategory, announcementContent, announcementDate);

                new AsyncAddAnnouncementActivity().execute(announcement);
            }
        });
    }

    private class AsyncAddAnnouncementActivity extends AsyncTask<Announcement,Void,Void> {

        @Override
        protected Void doInBackground(Announcement... announcements) {
            announcementHandler.save(announcements[0]);
            return null;
        }
    }
}
