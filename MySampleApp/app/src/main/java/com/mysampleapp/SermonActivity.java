package com.mysampleapp;

import android.content.Context;
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

        Toolbar myToolbar = (Toolbar) findViewById(R.id.my_toolbar);
        setSupportActionBar(myToolbar);

        // Get a support ActionBar corresponding to this toolbar
        ActionBar ab = getSupportActionBar();
        ab.setTitle(Html.fromHtml("<font color='#FFFFFF'>설교 말씀</font>"));

        // Enable the Up button
        ab.setDisplayHomeAsUpEnabled(true);



        Sermon sermon_data[] = new Sermon[]
                {
                        new Sermon("창세기 족장 강해(30)", "미움 받는 사람 요셉", "창세기 37:1-11", "A P R", "09", "2 0 1 7"),
                        new Sermon("창세기 족장 강해(27)", "뒷북치는 야곱","창세기 34:1-31", "M A R", "23", "2 0 1 7"),
                        new Sermon("창세기 족장 강해(26)", "마음의 감옥에서 벗어나는 야곱", "창세기 33:1-17", "M A R", "18", "2 0 1 7"),
                        new Sermon("창세기 족장 강해(25)", "이름을 바꿔 주시는 하나님", "창세기 32:3-32", "M A R", "09", "2 0 1 7"),

                };

        SermonArrayAdapter adapter = new SermonArrayAdapter(this,
                R.layout.listview_item_row, sermon_data);

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
            listView.setOnItemClickListener(new AdapterView.OnItemClickListener(){
                @Override
                public void onItemClick(AdapterView<?> parent, View view, int position, long id){
                    Intent myintent = new Intent(view.getContext(),SermonPlay.class);
                    startActivity(myintent);
                }
            });
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
