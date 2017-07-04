package com.mysampleapp;

import android.content.Context;
import android.content.Intent;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.text.Html;
import android.view.MenuItem;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import java.util.ArrayList;
import java.util.List;

public class AnnouncementActivity extends AppCompatActivity {

    ListView listView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_announcement);

        Toolbar myToolbar = (Toolbar) findViewById(R.id.my_announcement_toolbar);
        setSupportActionBar(myToolbar);

        // Get a support ActionBar corresponding to this toolbar
        ActionBar ab = getSupportActionBar();
        ab.setTitle(Html.fromHtml("<font color='#FFFFFF'>교회 소식</font>"));

        // Enable the Up button
        ab.setDisplayHomeAsUpEnabled(true);



        Announcement announcements_data[] = new Announcement[]
                {
                        new Announcement("카이로스 피크닉",  "M A Y", "14", "2 0 1 7"),
                        new Announcement("카리스마 여름학기",  "M A Y", "14", "2 0 1 7"),
                        new Announcement("목장 방학",  "M A Y", "07", "2 0 1 7"),
                        new Announcement("중국선교 기도모임",  "M A Y", "07", "2 0 1 7"),

                };

        AnnouncementArrayAdapter adapter = new AnnouncementArrayAdapter(this,
                R.layout.listview_item_announcement_row, announcements_data);


        listView = (ListView) findViewById(R.id.announcement_listview);
        listView.setAdapter(adapter);

    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        switch (item.getItemId()) {
            case android.R.id.home:
                // app icon in action bar clicked; go home
                Intent intent = new Intent(this, HomeActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(intent);
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

}