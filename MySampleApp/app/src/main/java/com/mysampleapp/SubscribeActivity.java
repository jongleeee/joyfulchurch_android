package com.mysampleapp;

import android.graphics.Color;
import android.graphics.Typeface;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.TypedValue;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;

import com.mysampleapp.util.Util;

import org.w3c.dom.Text;

import java.lang.reflect.Type;
import java.util.Arrays;
import java.util.List;

public class SubscribeActivity extends AppCompatActivity {

    ListView listView;
    final List<String> subscribeArray = Util.getAllChannels();
    private Typeface mTypeface;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_subscribe);

        listView = (ListView)findViewById(R.id.subscribe_listview);

        Toolbar myToolbar = (Toolbar) findViewById(R.id.subscribe_toolbar);
        myToolbar.setTitle("Subscribe");
        setSupportActionBar(myToolbar);

        mTypeface = Typeface.createFromAsset(getAssets(),"jung_regular_120.otf");

        ArrayAdapter adapter = new ArrayAdapter<String>(this, R.layout.listview_subscribe, R.id.subscribe_listview, subscribeArray) {
            @Override
            public View getView(int position, View convertView, ViewGroup parent){
                View view = super.getView(position, convertView, parent);

                TextView textview = (TextView) view.findViewById(R.id.subscribe_listview);

                textview.setTextColor(Color.parseColor("#000000"));
                textview.setTextSize(18);
                textview.setTypeface(mTypeface);
                textview.setPadding(15,30,15,30);

                return view;
            }
        };

        listView.setAdapter(adapter);
    }
}
