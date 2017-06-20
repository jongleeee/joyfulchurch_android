package com.mysampleapp;

import android.content.Intent;
import android.support.constraint.ConstraintLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.RelativeLayout;

public class HomeActivity extends AppCompatActivity {

    ConstraintLayout SermonHeader;
    ConstraintLayout AnnouncementHeader;
    ConstraintLayout InformationHeader;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        SermonHeader = (ConstraintLayout) findViewById(R.id.SermonHeaderLayout);
        AnnouncementHeader = (ConstraintLayout) findViewById(R.id.AnnouncementHeaderLayout);
        InformationHeader = (ConstraintLayout) findViewById(R.id.InformationHeaderLayout);


        SermonHeader.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(getApplicationContext(), SermonActivity.class));
            }
        });

        AnnouncementHeader.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(getApplicationContext(), AnnouncementActivity.class));
            }
        });

        InformationHeader.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(getApplicationContext(), InformationActivity.class));
            }
        });
    }


}
