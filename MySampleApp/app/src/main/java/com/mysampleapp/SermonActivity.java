package com.mysampleapp;

import android.content.Context;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import com.mysampleapp.util.Sermon;
import com.mysampleapp.util.SermonHandler;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class SermonActivity extends AppCompatActivity {

    ListView listView;
    SermonHandler sermon = new SermonHandler();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sermon);

        listView = (ListView) findViewById(R.id.SermonListView);

        //titles of sermons
        new AsyncSermonActivity().execute();
    }

    public void getSermonTitle(List<Sermon> sermons) {
        if (sermons != null) {
            List<String> sermonTitles = new ArrayList<>();
            for (Sermon sermon : sermons) {
                sermonTitles.add(sermon.getTitle());
            }

            ArrayAdapter<String> adapter = new ArrayAdapter<String>(this,
                    android.R.layout.simple_list_item_1, sermonTitles);
            listView.setAdapter(adapter);
        }
    }




    private class AsyncSermonActivity extends AsyncTask<Void, Void, Void> {
        List<Sermon> sermons;
        @Override
        protected Void doInBackground(Void... voids) {
            sermons = sermon.getAllSermon();
            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            getSermonTitle(sermons);
        }
    }


}
