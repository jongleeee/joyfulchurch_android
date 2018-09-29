package com.mysampleapp;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.media.MediaPlayer;
import android.os.Handler;
import android.os.IBinder;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageButton;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import com.mysampleapp.util.AudioPlayer;
import com.mysampleapp.util.LockScreenService;
import com.mysampleapp.util.Util;

public class SermonPlay extends AppCompatActivity implements SermonPlayListener {

    public static final String TAG = "SermonMediaPlayActivity";

    private String currentSermonURL;


    private TextView mSermonTitle;
    private TextView mSermonSeries;
    private TextView mSermonVerse;
    private TextView mSermonMonth;
    private TextView mSermonDay;
    private TextView mSermonYear;

    private ImageButton mPlayButton;
    boolean isButtonPlay;
    private TextView mTotalTime;
    private TextView mCurrentTime;

    private SeekBar mSeekbarAudio;
    private boolean mUserIsSeeking = false;

    private AudioPlayer audioPlayer = AudioPlayer.INSTANCE();
    private LockScreenService player;
    boolean serviceBound = false;
    private Handler mHandler = new Handler();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sermon_play);

        initializeUI();
        initializeSermonInformation();
        initializeSeekbar();

        currentSermonURL = getIntent().getExtras().getString("sermonURL");

        if (audioPlayer.getMediaPlayer() == null) {
            audioPlayer.initializeMediaPlayer();
        }

        audioPlayer.addListener(this);
    }

    @Override
    public void sermonReadyToPlay() {
        //Invoked when the media source is ready for playback.
        audioPlayer.setIsReleased(false);

        if (audioPlayer.getLastCurrentPosition() > 0) {
            audioPlayer.seekTo(audioPlayer.getLastCurrentPosition());
            audioPlayer.setLastCurrentPosition(-1);
        }
        audioPlayer.start();
        updateProgreeBarAndTime();
    }

    @Override
    public void sermonFinishedPlaying() {
        // File has ended !!!
        mHandler.removeCallbacks(updateTimeTask);
        setPlayButton();
        mSeekbarAudio.setProgress(0);
        // Handle this in audioPlayer.
        audioPlayer.seekTo(0);
        mCurrentTime.setVisibility(View.INVISIBLE);
        mTotalTime.setVisibility(View.INVISIBLE);
    }

    private void initializeUI() {
        mSermonTitle = (TextView) findViewById(R.id.sermon_media_play_title);
        mSermonSeries = (TextView) findViewById(R.id.sermon_media_play_series);
        mSermonVerse = (TextView) findViewById(R.id.sermon_media_play_verse);
        mSermonMonth = (TextView) findViewById(R.id.sermon_media_play_month);
        mSermonDay = (TextView) findViewById(R.id.sermon_media_play_day);
        mSermonYear = (TextView) findViewById(R.id.sermon_media_play_year);

        mSeekbarAudio = (SeekBar) findViewById(R.id.sermon_media_play_seekbar);
        mSeekbarAudio.setEnabled(false);

        mCurrentTime = (TextView) findViewById(R.id.sermon_media_play_currentTime);
        mCurrentTime.setVisibility(View.INVISIBLE);
        mTotalTime = (TextView) findViewById(R.id.sermon_media_play_totalTime);
        mTotalTime.setVisibility(View.INVISIBLE);
        mPlayButton = (ImageButton) findViewById(R.id.sermon_media_play_button);
        setPlayButton();

        mPlayButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (isButtonPlay) {
                    playButtonPressed();
                } else {
                    pauseButtonPressed();
                }
            }
        });
    }

    private void initializeSermonInformation() {
        mSermonTitle.setText(getIntent().getExtras().getString("sermonTitle"));
        mSermonSeries.setText(getIntent().getExtras().getString("sermonSeries"));
        mSermonVerse.setText(getIntent().getExtras().getString("sermonVerse"));
        mSermonMonth.setText(getIntent().getExtras().getString("sermonMonth"));
        mSermonYear.setText(getIntent().getExtras().getString("sermonYear"));
        mSermonDay.setText(getIntent().getExtras().getString("sermonDay"));
    }

    private void initializeSeekbar() {
        mSeekbarAudio.setOnSeekBarChangeListener(
                new SeekBar.OnSeekBarChangeListener() {
                    int userSelectedPosition = 0;

                    @Override
                    public void onStartTrackingTouch(SeekBar seekBar) {
                        mUserIsSeeking = true;
                    }

                    @Override
                    public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                        if (fromUser) {
                            userSelectedPosition = progress;
                            updateCurrentTimeForUserInteration(progress);
                        }
                    }

                    @Override
                    public void onStopTrackingTouch(SeekBar seekBar) {
                        mUserIsSeeking = false;
                        audioPlayer.seekTo(userSelectedPosition);
                    }
                });
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (audioPlayer.isPlaying() && audioPlayer.isSameSermon(currentSermonURL)) {
            setPauseButton();
            updateProgreeBarAndTime();
        } else {
            setPlayButton();
            if (audioPlayer.isSameSermon(currentSermonURL)) {
                updateTotalTime();
                updateCurrentTimeWithLastCurrentTime();
            }
        }
    }

    protected void setPlayButton() {
        mPlayButton.setImageResource(R.drawable.play_icon);
        isButtonPlay = true;
    }

    protected void setPauseButton() {
        mPlayButton.setImageResource(R.drawable.pause_icon);
        isButtonPlay = false;
    }

    protected void updateTotalTime() {
        mTotalTime.setVisibility(View.VISIBLE);
        mTotalTime.setText(Util.convertMilliSecondToString(audioPlayer.getTotalTime()));
        mSeekbarAudio.setMax(audioPlayer.getTotalTime());
    }

    protected void updateCurrentTime() {
        mCurrentTime.setVisibility(View.VISIBLE);
        mCurrentTime.setText(Util.convertMilliSecondToString(audioPlayer.getCurrentTime()));
    }

    protected void updateCurrentTimeForUserInteration(int position) {
        mCurrentTime.setVisibility(View.VISIBLE);
        mCurrentTime.setText(Util.convertMilliSecondToString(position));
    }

    protected void updateCurrentTimeWithLastCurrentTime() {
        mCurrentTime.setVisibility(View.VISIBLE);
        mCurrentTime.setText(Util.convertMilliSecondToString(audioPlayer.getLastCurrentPosition()));
        mSeekbarAudio.setProgress(audioPlayer.getLastCurrentPosition());
    }

    protected void startPlayingSermon(String url) {
        // Check the released() case and handle.
        audioPlayer.playSermon(url);
    }

    public void updateProgreeBarAndTime() {
        mHandler.postDelayed(updateTimeTask, 100);
    }

    private Runnable updateTimeTask = new Runnable() {
        public void run() {
            if(audioPlayer != null){
                updateCurrentTime();
                /*
                 There is a delay in audioPlayer returning total time. So we will just handle here.
                 Also sometimes, audioPlayer.getTotalTime() returned some random number. So we will
                 just handle > 1000 for now.
                  */
                if (mTotalTime.getVisibility() == View.INVISIBLE && audioPlayer.getTotalTime() > 1) {
                    updateTotalTime();

                    mSeekbarAudio.setMax(audioPlayer.getTotalTime());
                }

                // If total time is visible, that means current audio player is ready and can be played.
                if (mTotalTime.getVisibility() == View.VISIBLE) {
                    mSeekbarAudio.setEnabled(true);
                    mSeekbarAudio.setProgress(audioPlayer.getCurrentTime());
                }
            }
            mHandler.postDelayed(this, 1000);
        }
    };


    protected void playButtonPressed() {
        setPauseButton();
        playSermon(currentSermonURL);
    }

    protected void pauseButtonPressed() {
        setPlayButton();
        mHandler.removeCallbacks(updateTimeTask);
        audioPlayer.pause();
    }

    @Override
    protected void onPause() {
        super.onPause();
        mHandler.removeCallbacks(updateTimeTask);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                // app icon in action bar clicked; go home
                Intent intent = new Intent(this, SermonActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(intent);
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    private ServiceConnection serviceConnection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            // We've bound to LocalService, cast the IBinder and get LocalService instance
            LockScreenService.LocalBinder binder = (LockScreenService.LocalBinder) service;
            player = binder.getService();
            audioPlayer.setIsServiceBound(true);

            Toast.makeText(SermonPlay.this, "Playing Sermon", Toast.LENGTH_SHORT).show();
        }

        @Override
        public void onServiceDisconnected(ComponentName name) {
            audioPlayer.setIsServiceBound(false);
        }
    };

    private void playSermon(String URL) {
        if (audioPlayer.getIsReleased()) {
            try {
                unbindService(serviceConnection);
            } catch (IllegalArgumentException e) {
            }
            Intent sermonPlayIntent = new Intent(this, LockScreenService.class);
            sermonPlayIntent.putExtra("sermonURL", URL);
            startService(sermonPlayIntent);
            bindService(sermonPlayIntent, serviceConnection, Context.BIND_AUTO_CREATE);
        } else {
            audioPlayer.playSermon(currentSermonURL);
            updateProgreeBarAndTime();
        }
    }

    @Override
    public void onSaveInstanceState(Bundle savedInstanceState) {
        savedInstanceState.putBoolean("ServiceState", serviceBound);
        super.onSaveInstanceState(savedInstanceState);
    }

    @Override
    public void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
        audioPlayer.setIsServiceBound(savedInstanceState.getBoolean("ServiceState"));
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
//        if (serviceBound) {
//            unbindService(serviceConnection);
//            //service is active
//            player.stopSelf();
//        }
    }
}
