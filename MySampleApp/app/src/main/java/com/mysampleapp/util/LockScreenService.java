package com.mysampleapp.util;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.media.session.MediaSessionManager;
import android.os.Binder;
import android.os.IBinder;
import android.os.RemoteException;
import android.support.annotation.Nullable;
import android.support.v4.media.MediaMetadataCompat;
import android.support.v4.media.session.MediaControllerCompat;
import android.support.v4.media.session.MediaSessionCompat;
import android.support.v7.app.NotificationCompat;
import android.util.Log;

import com.mysampleapp.R;

import java.io.IOException;

import static android.support.v4.app.NotificationCompat.VISIBILITY_PUBLIC;

/**
 * Created by minjungkim on 8/8/17.
 */

public class LockScreenService extends Service implements MediaPlayer.OnCompletionListener,
        MediaPlayer.OnErrorListener, MediaPlayer.OnSeekCompleteListener,
        MediaPlayer.OnInfoListener, MediaPlayer.OnBufferingUpdateListener,
        AudioManager.OnAudioFocusChangeListener {

    private final IBinder iBinder = new LocalBinder();
    private int resumePosition;
    private String url;
    private String title;
    private String series;
    private AudioManager audioManager;
    private MediaPlayer mediaPlayer;

    private boolean wasStoppedForShortPeriod;

    public static final String ACTION_PLAY = "com.valdioveliu.valdio.audioplayer.ACTION_PLAY";
    public static final String ACTION_PAUSE = "com.valdioveliu.valdio.audioplayer.ACTION_PAUSE";
    public static final String SERMON_NOTIFICATION_CHANNEL = "SERMON_NOTIFICATION_CHANNEL";
    public static final String SERMON_NOTIFICATION_CHANNEL_NAME = "SERMON_NOTIFICATION_CHANNEL_NAME";
    public static final String SERMON_NOTIFICATION_CHANNEL_DESCRIPTION = "SERMON_NOTIFICATION_CHANNEL_DESCRIPTION";

    //MediaSession
    private MediaSessionManager mediaSessionManager;
    private MediaSessionCompat mediaSession;
    private MediaControllerCompat.TransportControls transportControls;

    //AudioPlayer notification ID
    private static final int NOTIFICATION_ID = 101;

    private AudioPlayer sermonOnPlay = AudioPlayer.INSTANCE();


    public class LocalBinder extends Binder {
        public LockScreenService getService() {
            return LockScreenService.this;
        }
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return iBinder;
    }

    @Override
    public void onBufferingUpdate(MediaPlayer audioPlayer, int i) {

    }

    @Override
    public void onCompletion(MediaPlayer audioPlayer) {
        //Invoked when playback of a media source has completed.
        stopMedia();
        //stop the service
        stopSelf();
    }

    @Override
    public boolean onError(MediaPlayer audioPlayer, int what, int extra) {
        //Invoked when there has been an error during an asynchronous operation
        switch (what) {
            case MediaPlayer.MEDIA_ERROR_NOT_VALID_FOR_PROGRESSIVE_PLAYBACK:
                Log.d("MediaPlayer Error", "MEDIA ERROR NOT VALID FOR PROGRESSIVE PLAYBACK " + extra);
                break;
            case MediaPlayer.MEDIA_ERROR_SERVER_DIED:
                Log.d("MediaPlayer Error", "MEDIA ERROR SERVER DIED " + extra);
                break;
            case MediaPlayer.MEDIA_ERROR_UNKNOWN:
                Log.d("MediaPlayer Error", "MEDIA ERROR UNKNOWN " + extra);
                break;
        }
        return false;
    }

    @Override
    public boolean onInfo(MediaPlayer audioPlayer, int i, int i1) {
        return false;
    }

//    @Override
//    public void onPrepared(MediaPlayer audioPlayer) {
//        //Invoked when the media source is ready for playback.
//        playMedia();
//    }

    @Override
    public void onSeekComplete(MediaPlayer audioPlayer) {

    }

    @Override
    public void onAudioFocusChange(int focusState) {
        //Invoked when the audio focus of the system is updated.
        switch (focusState) {
            case AudioManager.AUDIOFOCUS_GAIN:
                // resume playback
//                if (mediaPlayer == null) initMediaPlayer();
                if (!sermonOnPlay.getIsReleased() && wasStoppedForShortPeriod) {
                    sermonOnPlay.start();
                    wasStoppedForShortPeriod = false;
                }
//                mediaPlayer.setVolume(1.0f, 1.0f);
                break;
            case AudioManager.AUDIOFOCUS_LOSS:
                // Lost focus for an unbounded amount of time: stop playback and release media player
                if (sermonOnPlay.isPlaying()) {
                    sermonOnPlay.pause();
                    sermonOnPlay.release();
                }
//                mediaPlayer.release();
//                mediaPlayer = null;
                break;
            case AudioManager.AUDIOFOCUS_LOSS_TRANSIENT:
                // Lost focus for a short time, but we have to stop
                // playback. We don't release the media player because playback
                // is likely to resume
                if (sermonOnPlay.isPlaying()) {
                    sermonOnPlay.pause();
                    wasStoppedForShortPeriod = true;
                }
                break;
            case AudioManager.AUDIOFOCUS_LOSS_TRANSIENT_CAN_DUCK:
                // Lost focus for a short time, but it's ok to keep playing
                // at an attenuated level
                if (sermonOnPlay.isPlaying()) sermonOnPlay.getMediaPlayer().setVolume(0.1f, 0.1f);
                break;
        }
    }

    private boolean requestAudioFocus() {
        audioManager = (AudioManager) getSystemService(Context.AUDIO_SERVICE);
        int result = audioManager.requestAudioFocus(this, AudioManager.STREAM_MUSIC, AudioManager.AUDIOFOCUS_GAIN);
        if (result == AudioManager.AUDIOFOCUS_REQUEST_GRANTED) {
            //Focus gained
            return true;
        }
        //Could not gain focus
        return false;
    }

    private boolean removeAudioFocus() {
        return AudioManager.AUDIOFOCUS_REQUEST_GRANTED ==
                audioManager.abandonAudioFocus(this);
    }

    private void initMediaPlayer() {

//        sermonOnPlay.setOnCompletionListener(this);

        sermonOnPlay.getMediaPlayer().setOnErrorListener(this);
//        sermonOnPlay.setOnPreparedListener(this);
        sermonOnPlay.getMediaPlayer().setOnBufferingUpdateListener(this);
        sermonOnPlay.getMediaPlayer().setOnSeekCompleteListener(this);
        sermonOnPlay.getMediaPlayer().setOnInfoListener(this);

//        sermonOnPlay.setCurrentSermonURL(this.url);
//        sermonOnPlay.start();

        sermonOnPlay.playSermon(this.url);
    }

    private void playMedia() {
        if (!sermonOnPlay.isPlaying()) {
            sermonOnPlay.start();
        }
    }

    private void stopMedia() {
        if (sermonOnPlay == null) return;
        if (sermonOnPlay.isPlaying()) {
            sermonOnPlay.stop();
        }
    }

    private void pauseMedia() {
        if (sermonOnPlay.isPlaying()) {
            sermonOnPlay.pause();
            resumePosition = sermonOnPlay.getCurrentTime();
        }
    }

    private void resumeMedia() {
        if (!sermonOnPlay.isPlaying()) {
            sermonOnPlay.seekTo(resumePosition);
            sermonOnPlay.start();
        }
    }

    //The system calls this method when an activity, requests the service be started
    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        try {
            //An audio file is passed to the service through putExtra();
            this.title = intent.getExtras().getString("sermonTitle");
            this.series = intent.getExtras().getString("sermonSeries");
            this.url = intent.getExtras().getString("sermonURL");
        } catch (NullPointerException e) {
            stopSelf();
        }

        //Request audio focus
        if (requestAudioFocus() == false) {
            //Could not gain focus
            stopSelf();
        }

        if (mediaSessionManager == null) {
            try {
                initMediaSession();

            } catch (RemoteException e) {
                e.printStackTrace();
                stopSelf();
            }

        }

        if (sermonOnPlay.getIsReleased()) {
            initMediaPlayer();
            buildNotification(PlaybackStatus.PLAYING);
        }

        //Handle Intent action from MediaSession.TransportControls
        handleIncomingActions(intent);
        return super.onStartCommand(intent, flags, startId);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (sermonOnPlay != null) {
            stopMedia();
            sermonOnPlay.release();
            sermonOnPlay.setIsServiceBound(false);
        }
        removeAudioFocus();
    }

    private void initMediaSession() throws RemoteException {
        if (mediaSessionManager != null) return; //mediaSessionManager exists

        mediaSessionManager = (MediaSessionManager) getSystemService(Context.MEDIA_SESSION_SERVICE);
        // Create a new MediaSession
        mediaSession = new MediaSessionCompat(getApplicationContext(), "AudioPlayer");
        //Get MediaSessions transport controls
        transportControls = mediaSession.getController().getTransportControls();
        //set MediaSession -> ready to receive media commands
        mediaSession.setActive(true);
        //indicate that the MediaSession handles transport control commands
        // through its MediaSessionCompat.Callback.
        mediaSession.setFlags(MediaSessionCompat.FLAG_HANDLES_TRANSPORT_CONTROLS);

        //Set mediaSession's MetaData
        updateMetaData();

        // Attach Callback to receive MediaSession updates
        mediaSession.setCallback(new MediaSessionCompat.Callback() {
            // Implement callbacks
            @Override
            public void onPlay() {
                super.onPlay();
                resumeMedia();
                buildNotification(PlaybackStatus.PLAYING);
            }

            @Override
            public void onPause() {
                super.onPause();
                pauseMedia();
                buildNotification(PlaybackStatus.PAUSED);
            }

            @Override
            public void onSeekTo(long position) {
                super.onSeekTo(position);
            }
        });
    }

    private void updateMetaData() {
        Bitmap albumArt = BitmapFactory.decodeResource(getResources(),
                R.drawable.crossicon); //replace with medias albumArt
        // Update the current metadata
        mediaSession.setMetadata(new MediaMetadataCompat.Builder()
//                .putBitmap(MediaMetadataCompat.METADATA_KEY_ALBUM_ART, albumArt)
                .putString(MediaMetadataCompat.METADATA_KEY_ARTIST, "이상준 목사")
                .putString(MediaMetadataCompat.METADATA_KEY_ALBUM, this.series)
                .putString(MediaMetadataCompat.METADATA_KEY_TITLE, this.title)
                .build());
    }

    public enum PlaybackStatus {
        PLAYING,
        PAUSED
    }

    private void buildNotification(PlaybackStatus playbackStatus) {

        NotificationManager notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        String CHANNEL_ID = "my_channel_01";

        if (android.os.Build.VERSION.SDK_INT >= 26) {
            int importance = NotificationManager.IMPORTANCE_HIGH;
            NotificationChannel mChannel = new NotificationChannel(SERMON_NOTIFICATION_CHANNEL, SERMON_NOTIFICATION_CHANNEL_NAME, importance);
            mChannel.setDescription(SERMON_NOTIFICATION_CHANNEL_DESCRIPTION);
            mChannel.enableLights(true);
            mChannel.enableVibration(false);
            mChannel.setShowBadge(false);
            notificationManager.createNotificationChannel(mChannel);
        }

        int notificationAction = R.drawable.pause_icon;//needs to be initialized
        PendingIntent play_pauseAction = null;

        //Build a new notification according to the current state of the MediaPlayer
        if (playbackStatus == PlaybackStatus.PLAYING) {
            notificationAction = R.drawable.pause_icon;
            //create the pause action
            play_pauseAction = playbackAction(1);
        } else if (playbackStatus == PlaybackStatus.PAUSED) {
            notificationAction = R.drawable.play_icon;
            //create the play action
            play_pauseAction = playbackAction(0);
        }

        Bitmap largeIcon = BitmapFactory.decodeResource(getResources(),
                R.drawable.crossicon); //replace with your own image

        // Create a new Notification
        NotificationCompat.Builder notificationBuilder = (NotificationCompat.Builder) new NotificationCompat.Builder(this)
                .setVisibility(VISIBILITY_PUBLIC)
                .setShowWhen(false)
                // Set the Notification style
                .setStyle(new NotificationCompat.MediaStyle()
                        // Attach our MediaSession token
                        .setMediaSession(mediaSession.getSessionToken())
                        // Show our playback controls in the compact notification view.
                        .setShowActionsInCompactView(0))
                // Set the Notification color
                .setColor(getResources().getColor(R.color.black_overlay))
                // Set the large and small icons
                .setLargeIcon(largeIcon)
                .setSmallIcon(android.R.drawable.stat_sys_headset)
                // Set Notification content information
                .setContentText(this.series)
                .setContentTitle(this.title)
                .setContentInfo("이상준 목사")
                // Add playback actions
                .addAction(notificationAction, "pause", play_pauseAction)
                .setChannelId(CHANNEL_ID)
                .setOngoing(true);


        ((NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE))
                .notify(NOTIFICATION_ID, notificationBuilder.build());
    }

    private void removeNotification() {
        NotificationManager notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        notificationManager.cancel(NOTIFICATION_ID);
    }

    private PendingIntent playbackAction(int actionNumber) {
        Intent playbackAction = new Intent(this, LockScreenService.class);
        switch (actionNumber) {
            case 0:
                // Play
                playbackAction.setAction(ACTION_PLAY);
                return PendingIntent.getService(this, actionNumber, playbackAction, 0);
            case 1:
                // Pause
                playbackAction.setAction(ACTION_PAUSE);
                return PendingIntent.getService(this, actionNumber, playbackAction, 0);
        }
        return null;
    }

    private void handleIncomingActions(Intent playbackAction) {
        if (playbackAction == null || playbackAction.getAction() == null) return;

        String actionString = playbackAction.getAction();
        if (actionString.equalsIgnoreCase(ACTION_PLAY)) {
            transportControls.play();
        } else if (actionString.equalsIgnoreCase(ACTION_PAUSE)) {
            transportControls.pause();
        }
    }
}
