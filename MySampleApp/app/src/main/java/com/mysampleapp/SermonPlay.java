package com.mysampleapp;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.media.MediaPlayer;
import android.os.AsyncTask;
import android.os.Handler;
import android.os.IBinder;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.format.DateFormat;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ProgressBar;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import com.mysampleapp.util.AudioPlayer;
import com.mysampleapp.util.LockScreenService;

public class SermonPlay extends AppCompatActivity {
    private String url;
    private ImageButton buttonPlay;
    private SeekBar playBar;
    private TextView totalTime;
    private TextView currentTime;
    private TextView sermonSeries;
    private TextView sermonTitle;
    private TextView sermonVerse;
    private TextView sermonMonth;
    private TextView sermonDay;
    private TextView sermonYear;
    private Handler handler = new Handler();
    private Runnable runner;
    private AudioPlayer audioPlayer = AudioPlayer.INSTANCE();
    private ProgressBar progressBar;
    private boolean sameSermon;
    private LockScreenService player;
    boolean serviceBound = false;
    private String title;
    private String series;
    private Handler mHandler = new Handler();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sermon_play);



//        Intent lockScreenIntent = new Intent(getApplicationContext(), LockScreenService.class);
//        lockScreenIntent.setAction(LockScreenService.);
//        startService(lockScreenIntent);
//        playAudio("https://upload.wikimedia.org/wikipedia/commons/6/6c/Grieg_Lyric_Pieces_Kobold.ogg");

        buttonPlay = (ImageButton) findViewById(R.id.play);
        playBar = (SeekBar) findViewById(R.id.playbar);
        totalTime = (TextView) findViewById(R.id.totalTime);
        currentTime = (TextView) findViewById(R.id.currentTime);
        sermonSeries = (TextView) findViewById(R.id.sermon_play_series);
        sermonTitle = (TextView) findViewById(R.id.sermon_play_title);
        sermonVerse = (TextView) findViewById(R.id.sermon_play_verse);
        sermonMonth = (TextView) findViewById(R.id.sermon_play_month);
        sermonDay = (TextView) findViewById(R.id.sermon_play_day);
        sermonYear = (TextView) findViewById(R.id.sermon_play_year);
        progressBar = (ProgressBar) findViewById(R.id.progressBar);

        currentTime.setVisibility(View.INVISIBLE);
        totalTime.setVisibility(View.INVISIBLE);
        progressBar.setVisibility(View.GONE);

        this.url = getIntent().getExtras().getString("sermonURL");
        this.title = getIntent().getExtras().getString("sermonTitle");
        sermonTitle.setText(this.title);
        this.series = getIntent().getExtras().getString("sermonSeries");
        sermonSeries.setText(this.series);
        sermonVerse.setText(getIntent().getExtras().getString("sermonVerse"));
        sermonMonth.setText(getIntent().getExtras().getString("sermonMonth"));
        sermonYear.setText(getIntent().getExtras().getString("sermonYear"));
        sermonDay.setText(getIntent().getExtras().getString("sermonDay"));

//        sameSermon = audioPlayer.setUrl(this.url);
        setTime(totalTime, audioPlayer.getTotalTime());
        playBar.setMax(audioPlayer.getTotalTime());

        if (sameSermon) {
            if (audioPlayer.isPlaying()) {
                buttonPlay.setImageResource(R.drawable.pause_icon);
            }
            playBar.setMax(audioPlayer.getTotalTime());
            setTime(totalTime, audioPlayer.getTotalTime());
            setTime(currentTime, audioPlayer.getCurrentTime());
        }

        buttonPlay.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (!audioPlayer.isPlaying()) {

                    if (serviceBound) {
                        audioPlayer.start();
                        updateProgreeBarAndTime();
                    } else {
                        new AsyncSermonPlay().execute();

                        audioPlayer.setOnCompletionListener(new MediaPlayer.OnCompletionListener(){
                            @Override
                            public void onCompletion(MediaPlayer mp) {
                                // File has ended !!!
                                buttonPlay.setImageResource(R.drawable.play_icon);
                                audioPlayer.reset();
                                playBar.setProgress(0);
                                setTime(currentTime, 0);
                            }
                        });
                    }

                    buttonPlay.setImageResource(R.drawable.pause_icon);

                } else {
                    audioPlayer.pause();
                    mHandler.removeCallbacks(updateTimeTask);
                    buttonPlay.setImageResource(R.drawable.play_icon);
                }
            }
        });

        playBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean b) {
                if (b) {
                    audioPlayer.changeTime(progress);
                    playBar.setProgress(audioPlayer.getCurrentTime());
                    setTime(currentTime, audioPlayer.getCurrentTime());
                }
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {}

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {}
        });

    }

    public void updateProgreeBarAndTime() {
        mHandler.postDelayed(updateTimeTask, 100);
    }

    private Runnable updateTimeTask = new Runnable() {
        public void run() {
            if(audioPlayer != null){
                int mCurrentPosition = audioPlayer.getCurrentPosition() / 1000;
                progressBar.setProgress(mCurrentPosition);
                setTime(currentTime, audioPlayer.getCurrentTime());
            }
            mHandler.postDelayed(this, 1000);
        }
    };

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

    public void setTime(TextView textView, int time) {
        textView.setText(DateFormat.format("mm:ss", time));
    }

    private class AsyncSermonPlay extends AsyncTask<String, Void, Void> {

        @Override
        protected void onPreExecute() {
            buttonPlay.setVisibility(View.GONE);
            progressBar.setVisibility(View.VISIBLE);

        }

        @Override
        protected Void doInBackground(String... strings) {
//            audioPlayer.start();
            playAudio();
            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
//            progressDialog.dismiss();
            progressBar.setVisibility(View.GONE);
            buttonPlay.setVisibility(View.VISIBLE);
            updateProgreeBarAndTime();
        }
    }
    private ServiceConnection serviceConnection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            // We've bound to LocalService, cast the IBinder and get LocalService instance
            LockScreenService.LocalBinder binder = (LockScreenService.LocalBinder) service;
            player = binder.getService();
            serviceBound = true;

            Toast.makeText(SermonPlay.this, "Service Bound", Toast.LENGTH_SHORT).show();
        }

        @Override
        public void onServiceDisconnected(ComponentName name) {
            serviceBound = false;
        }
    };

    private void playAudio() {
        //Check is service is active
        if (!serviceBound) {
            Intent playerIntent = new Intent(this, LockScreenService.class);
//            playerIntent.putExtra("media", media);
            playerIntent.putExtra("sermonTitle", this.title);
            playerIntent.putExtra("sermonSeries", this.series);
            playerIntent.putExtra("sermonURL", this.url);
            startService(playerIntent);
            bindService(playerIntent, serviceConnection, Context.BIND_AUTO_CREATE);
        } else {
            //Service is active
            //Send media with BroadcastReceiver
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
        serviceBound = savedInstanceState.getBoolean("ServiceState");
    }

//    @Override
//    protected void onDestroy() {
//        super.onDestroy();
//        if (serviceBound) {
//            unbindService(serviceConnection);
//            //service is active
//            player.stopSelf();
//        }
//    }
}
