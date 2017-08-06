package com.mysampleapp;

import android.content.Intent;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.ListViewCompat;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;

import com.mysampleapp.util.User;

import java.util.ArrayList;
import java.util.List;

//import com.amazon.mysampleapp.R;

public class AnnouncementSettingActivity extends AppCompatActivity {
    private ListView subscribedChannel;
    private List<String> selectedChannels = new ArrayList<>();
    private Toolbar settingBar;
    private ActionBar actionBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_announcement_setting);

        settingBar = (Toolbar) findViewById(R.id.setting_toolbar);
        settingBar.setTitle("Setting Bar");
        setSupportActionBar(settingBar);
        actionBar = getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);

        subscribedChannel = (ListView) findViewById(R.id.categories);
        String[] channels = getResources().getStringArray(R.array.channels);
        ArrayAdapter<String> channelAdapter = new ArrayAdapter<String>(this, R.layout.channels_layout,
                R.id.check_channels, channels);
        subscribedChannel.setAdapter(channelAdapter);
        selectedChannels = new User(getApplicationContext()).getSubscribedChannels();
        for (int i = 0; i < channels.length; i++) {
            if(selectedChannels.contains(subscribedChannel.getItemAtPosition(i))) {
                subscribedChannel.setItemChecked(i, true);
            }
        }
        subscribedChannel.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                String selectedItem = ((TextView) view).getText().toString();
                if (selectedChannels.contains(selectedItem)) {
                    selectedChannels.remove(selectedItem);  //uncheck item
                } else {
                    selectedChannels.add(selectedItem);
                }
                updateSubscribed();
            }
        });
    }

    public void updateSubscribed() {
        new User(getApplicationContext()).updateSubscribedChannels(this.selectedChannels);
    }

//    @Override
//    public boolean onCreateOptionsMenu(Menu menu) {
//        getMenuInflater().inflate(R.menu.setting_button, menu);
//        return true;
//    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                Intent activityIntent = new Intent(this, AnnouncementActivity.class);
                activityIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(activityIntent);
                return true;
        }
        return super.onOptionsItemSelected(item);
    }
}
