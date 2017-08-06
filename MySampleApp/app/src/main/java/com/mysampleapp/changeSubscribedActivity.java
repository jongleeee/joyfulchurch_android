//package com.mysampleapp;
//
//import android.content.Intent;
//import android.support.v7.app.ActionBar;
//import android.support.v7.app.AppCompatActivity;
//import android.os.Bundle;
//import android.support.v7.widget.Toolbar;
//import android.text.Html;
//import android.view.MenuItem;
//import android.view.View;
//import android.view.ViewGroup;
//import android.widget.AdapterView;
//import android.widget.ArrayAdapter;
//import android.widget.ListView;
//import android.widget.TextView;
//
////import com.amazon.mysampleapp.R;
//import com.mysampleapp.util.User;
//
//import java.util.ArrayList;
//import java.util.List;
//
//public class changeSubscribedActivity extends AppCompatActivity {
//    private ListView subscribedChannel;
//    private List<String> selectedChannels = new ArrayList<>();
//    private Toolbar toolbar;
//    private ActionBar actionBar;
//
//    @Override
//    protected void onCreate(Bundle savedInstanceState) {
//        super.onCreate(savedInstanceState);
//        setContentView(R.layout.activity_change_subscribed);
//
//        toolbar = (Toolbar) findViewById(R.id.toolbar3);
//        toolbar.setTitle(Html.fromHtml("<font color='#FFFFFF'>Subscribed Channels</font>"));
//        setSupportActionBar(toolbar);
//        actionBar = getSupportActionBar();
//        actionBar.setDisplayHomeAsUpEnabled(true);
//
//        subscribedChannel = (ListView) findViewById(R.id.categories);
//        String[] channels = getResources().getStringArray(R.array.channels);
//        ArrayAdapter<String> channelAdapter = new ArrayAdapter<String>(this, R.layout.channels_layout,
//                R.id.check_channels, channels) {
//            @Override
//            public View getView(int position, View convertView, ViewGroup parent){
//                // Get the current item from ListView
//                View view = super.getView(position,convertView,parent);
//
//                // Get the Layout Parameters for ListView Current Item View
//                ViewGroup.LayoutParams params = view.getLayoutParams();
//
//                // Set the height of the Item View
//                params.height = 120;
//                view.setLayoutParams(params);
//
//                return view;
//            }
//        };
//        subscribedChannel.setAdapter(channelAdapter);
//        selectedChannels = new User(getApplicationContext()).getSubscribedChannels();
//        for (int i = 0; i < channels.length; i++) {
//            if(selectedChannels.contains(subscribedChannel.getItemAtPosition(i))) {
//                subscribedChannel.setItemChecked(i, true);
//            }
//        }
//        subscribedChannel.setOnItemClickListener(new AdapterView.OnItemClickListener() {
//            @Override
//            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
//                String selectedItem = ((TextView) view).getText().toString();
//                if (selectedChannels.contains(selectedItem)) {
//                    selectedChannels.remove(selectedItem);  //uncheck item
//                } else {
//                    selectedChannels.add(selectedItem);
//                }
//                updateSubscribed();
//            }
//        });
//    }
//
//    public void updateSubscribed() {
//        new User(getApplicationContext()).updateSubscribedChannels(this.selectedChannels);
//    }
//
//    @Override
//    public boolean onOptionsItemSelected(MenuItem item) {
//        switch (item.getItemId()) {
//            case android.R.id.home:
//                Intent settingIntent = new Intent(this, HomeActivity.class);
//                settingIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
//                startActivity(settingIntent);
//                return true;
//        }
//        return super.onOptionsItemSelected(item);
//    }
//}
