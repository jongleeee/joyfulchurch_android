package com.mysampleapp.util;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.media.MediaPlayer;
import android.media.session.MediaController;
import android.media.session.MediaSession;
import android.media.session.MediaSessionManager;
import android.os.IBinder;
import android.support.annotation.IntDef;
import android.support.annotation.Nullable;

import com.mysampleapp.R;

import java.util.concurrent.locks.Lock;

/**
 * Created by minjungkim on 8/3/17.
 */

public class LockScreenPlay extends Service {
    private MediaSession lockScreenSession;
    private MediaPlayer lockScreenPlay;
    private MediaSessionManager mediaManager;
    private MediaController mediaController;

    public static final String ACTION_PLAY = "action_play";
    public static final String ACTION_PAUSE = "action_pause";

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public boolean onUnbind(Intent intent) {
        lockScreenSession.release();
        return super.onUnbind(intent);
    }

    public void handleIntent(Intent intent) {
        if (intent == null || intent.getAction() == null) {
            return;
        }
        String action = intent.getAction();
        if (action.equalsIgnoreCase(ACTION_PLAY)) {
            mediaController.getTransportControls().play();
        } else if (action.equalsIgnoreCase(ACTION_PAUSE)) {
            mediaController.getTransportControls().pause();
        }
    }

    private Notification.Action generateAction(int icon, String title, String intentAction) {
        Intent intent = new Intent(getApplicationContext(), LockScreenPlay.class);
        intent.setAction(intentAction );
        PendingIntent pendingIntent = PendingIntent.getService(getApplicationContext(), 1, intent, 0);
        return new Notification.Action.Builder(icon, title, pendingIntent).build();
    }

    private void buildNotification(Notification.Action action) {
        Notification.MediaStyle style = new Notification.MediaStyle();
        Intent intent = new Intent(getApplicationContext(), LockScreenPlay.class);
        intent.setAction(ACTION_PAUSE);
        PendingIntent pendingIntent = PendingIntent.getService(getApplicationContext(), 1,  intent, 0);
        Notification.Builder builder = new Notification.Builder(this)
                .setContentText("이상준 목사")
                .setContentTitle("Title")
                .setDeleteIntent(pendingIntent)
                .setStyle(style);

        builder.addAction(action);
        style.setShowActionsInCompactView(0);

        NotificationManager notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        notificationManager.notify(1, builder.build());
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
//        if (mediaManager == null) {
            initMediaSession();
//        }
//        handleIntent(intent);
        return super.onStartCommand(intent, flags, startId);
    }

    private void initMediaSession() {
        lockScreenPlay = new MediaPlayer();
        lockScreenSession = new MediaSession(getApplicationContext(), "example");
        mediaController = new MediaController(getApplicationContext(), lockScreenSession.getSessionToken());
        lockScreenSession.setFlags(MediaSession.FLAG_HANDLES_MEDIA_BUTTONS | MediaSession.FLAG_HANDLES_TRANSPORT_CONTROLS);
        lockScreenSession.setCallback(new MediaSession.Callback() {
            @Override
            public void onPlay() {
                super.onPlay();
                buildNotification(generateAction(R.drawable.pause_icon, "Pause", ACTION_PAUSE));
            }

            @Override
            public void onPause() {
                super.onPause();
                buildNotification(generateAction(R.drawable.play_icon, "Play", ACTION_PLAY));
            }
        });
    }
}
