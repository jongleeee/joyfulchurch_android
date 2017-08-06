package com.mysampleapp;

import android.content.Context;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.AsyncTask;
import android.content.Intent;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.text.Html;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;

import com.mysampleapp.util.Sermon;
import com.mysampleapp.util.SermonHandler;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class SermonActivity extends AppCompatActivity {

    ListView listView;
    SermonHandler sermonHandler = new SermonHandler();
    MediaPlayer sermonPlay;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sermon);

        Toolbar myToolbar = (Toolbar) findViewById(R.id.my_toolbar);
        setSupportActionBar(myToolbar);

        // Get a support ActionBar corresponding to this toolbar
        ActionBar ab = getSupportActionBar();
        ab.setTitle(Html.fromHtml("<font color='#FFFFFF'>설교 말씀</font>"));

        // Enable the Up button
        ab.setDisplayHomeAsUpEnabled(true);

        listView = (ListView) findViewById(R.id.SermonListView);
        //titles of sermons
        new AsyncSermonActivity().execute();
    }

    public void getSermonInfo(final List<Sermon> sermons) {
        if (sermons != null) {
            SermonArrayAdapter adapter = new SermonArrayAdapter(this,
                    R.layout.listview_item_row, sermons);
            listView.setAdapter(adapter);

            listView.setOnItemClickListener(new AdapterView.OnItemClickListener(){
                @Override
                public void onItemClick(AdapterView<?> parent, View view, int position, long id){
                    Intent myIntent = new Intent(view.getContext(),SermonPlay.class);
                    myIntent.putExtra("sermonURL", sermons.get(position).getSermonURL());
                    startActivity(myIntent);
                }
            });
        }
    }

    private class AsyncSermonActivity extends AsyncTask<Void, Void, Void> {
        List<Sermon> sermons;

        @Override
        protected Void doInBackground(Void... voids) {
            sermons = sermonHandler.getAllSermon();
            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            getSermonInfo(sermons);
        }
    }

    /*
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
*/
}
