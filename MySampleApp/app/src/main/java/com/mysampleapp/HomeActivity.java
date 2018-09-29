package com.mysampleapp;

import android.app.AlertDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Typeface;
import android.support.constraint.ConstraintLayout;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.RelativeLayout;

import com.mysampleapp.util.Announcement;

import java.lang.annotation.Inherited;

public class HomeActivity extends AppCompatActivity {
    /** Class name for log messages. */
    private static final String LOG_TAG = HomeActivity.class.getSimpleName();

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

    @Override
    protected void onResume() {
        super.onResume();

        // register notification receiver
        LocalBroadcastManager.getInstance(this).registerReceiver(notificationReceiver,
                new IntentFilter(PushListenerService.ACTION_SNS_NOTIFICATION));
    }

    private final BroadcastReceiver notificationReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            Log.d(LOG_TAG, "Received notification from local broadcast. Display it in a dialog.");

            Bundle data = intent.getBundleExtra(PushListenerService.INTENT_SNS_NOTIFICATION_DATA);
            String message = PushListenerService.getMessage(data);

            new AlertDialog.Builder(HomeActivity.this)
                    .setTitle(R.string.push_demo_title)
                    .setMessage(message)
                    .setPositiveButton(android.R.string.ok, null)
                    .show();
        }
    };

    @Override
    protected void onPause() {
        super.onPause();

        // unregister notification receiver
        LocalBroadcastManager.getInstance(this).unregisterReceiver(notificationReceiver);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

    @Override
    public void finish() {
        super.finish();
    }
}
