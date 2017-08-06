package com.mysampleapp;

import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import com.mysampleapp.util.Announcement;
import com.mysampleapp.util.AnnouncementHandler;
import com.mysampleapp.util.User;

import java.util.ArrayList;
import java.util.List;

public class AnnouncementActivity extends AppCompatActivity {
    ListView listView;
    AnnouncementHandler announcementHandler = new AnnouncementHandler();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_announcement);

        Toolbar myToolbar = (Toolbar) findViewById(R.id.my_announcement_toolbar);
        myToolbar.setTitle("교회 소식");
        setSupportActionBar(myToolbar);

        // Get a support ActionBar corresponding to this toolbar
        ActionBar ab = getSupportActionBar();
//        ab.setTitle(Html.fromHtml("<font color='#FFFFFF'>교회 소식</font>"));

        // Enable the Up button
        ab.setDisplayHomeAsUpEnabled(true);


        listView = (ListView) findViewById(R.id.announcement_listview);

        List<String> categories = new User(getApplicationContext()).getSubscribedChannels();

        new AsyncAnnouncementActivity().execute(categories);
    }

    public void getAnnouncementInfo(List<Announcement> announcements) {
        AnnouncementArrayAdapter adapter = new AnnouncementArrayAdapter(this,
                R.layout.listview_item_announcement_row, announcements);
        listView.setAdapter(adapter);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu)
    {
        getMenuInflater().inflate(R.menu.setting_button, menu);
        return true;
//        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        switch (item.getItemId()) {
            case android.R.id.home:
                // app icon in action bar clicked; go home
                Intent homeIntent = new Intent(this, HomeActivity.class);
                homeIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(homeIntent);
                return true;
            case R.id.setting:
                Intent settingIntent = new Intent(this, AnnouncementSettingActivity.class);
                settingIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(settingIntent);
                return true;

            default:
                return super.onOptionsItemSelected(item);
        }
    }

    private class AsyncAnnouncementActivity extends AsyncTask<List<String>, Void, Void> {
        List<Announcement> announcements;

        @Override
        protected Void doInBackground(List<String>... categories) {
            announcements = announcementHandler.getAnnouncementsWithChannels(categories[0]);
            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            getAnnouncementInfo(announcements);
        }
    }
}