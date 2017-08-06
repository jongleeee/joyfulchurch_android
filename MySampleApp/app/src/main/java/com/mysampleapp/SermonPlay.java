package com.mysampleapp;

import android.content.Intent;
import android.media.Image;
import android.media.MediaPlayer;
import android.media.session.MediaSession;
import android.os.AsyncTask;
import android.os.Handler;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.text.Html;
import android.text.format.DateFormat;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageButton;
import android.widget.SeekBar;
import android.widget.TextView;

import com.mysampleapp.util.AudioPlayer;
import com.mysampleapp.util.LockScreenPlay;

public class SermonPlay extends AppCompatActivity {
    private String url;
    private ImageButton buttonPlay;
    private SeekBar playBar;
    private TextView totalTime;
    private TextView currentTime;
    private Handler handler = new Handler();
    private Runnable runner;
    private AudioPlayer audioPlayer = AudioPlayer.getsInstance();
    private MediaPlayer sermonPlayer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sermon_play);



        Intent lockScreenIntent = new Intent(getApplicationContext(), LockScreenPlay.class);
        lockScreenIntent.setAction(LockScreenPlay.ACTION_PLAY);
        startService(lockScreenIntent);

        buttonPlay = (ImageButton) findViewById(R.id.play);
        playBar = (SeekBar) findViewById(R.id.playbar);
        totalTime = (TextView) findViewById(R.id.totalTime);
        currentTime = (TextView) findViewById(R.id.currentTime);

        Toolbar playToolbar = (Toolbar) findViewById(R.id.play_toolbar);
        setSupportActionBar(playToolbar);

        // Get a support ActionBar corresponding to this toolbar
        ActionBar ab = getSupportActionBar();
        ab.setTitle(Html.fromHtml("<font color='#FFFFFF'>설교 말씀</font>"));

        // Enable the Up button
        ab.setDisplayHomeAsUpEnabled(true);

        Bundle extras = getIntent().getExtras();
        if (extras != null) {
            this.url = extras.getString("sermonURL");
        }

        if (audioPlayer == null || !audioPlayer.getUrl().equals(this.url)) {
            new AsyncSermonPlay().execute(this.url);
        } else {
            sermonPlayer = audioPlayer.getPlayer();
            buttonPlay.setImageResource(R.drawable.pause_icon);
            playBar.setMax(sermonPlayer.getDuration());
            setTime(totalTime, sermonPlayer.getDuration());
            setTime(currentTime, sermonPlayer.getCurrentPosition());
            playCycle();
        }

        buttonPlay.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (sermonPlayer != null) {
                    if (!sermonPlayer.isPlaying()) {
                        buttonPlay.setImageResource(R.drawable.pause_icon);
                        sermonPlayer.start();
                        sermonPlayer.setOnCompletionListener(new MediaPlayer.OnCompletionListener(){
                            @Override
                            public void onCompletion(MediaPlayer mp) {
                                // File has ended !!!
                                buttonPlay.setImageResource(R.drawable.play_icon);
//                             sermonPlayer.stop();
                                sermonPlayer.seekTo(0);
                                sermonPlayer.pause();
                                playBar.setProgress(0);
                                setTime(currentTime, 0);
                            }
                        });
                    } else {
                        buttonPlay.setImageResource(R.drawable.play_icon);
                        sermonPlayer.pause();
                    }
                    playCycle();
                }
            }
        });

        playBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean b) {
                if (b) {
                    sermonPlayer.seekTo(progress);
                    playBar.setProgress(sermonPlayer.getCurrentPosition());
                    setTime(currentTime, sermonPlayer.getCurrentPosition());
                }
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {}

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {}
        });
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

    public void setTime(TextView textView, int time) {
        textView.setText(DateFormat.format("mm:ss", time));
    }

        public void playCycle() {
        playBar.setProgress(sermonPlayer.getCurrentPosition());
        if (sermonPlayer.isPlaying()) {
            runner = new Runnable() {
                @Override
                public void run() {
                    playCycle();
                    setTime(currentTime, sermonPlayer.getCurrentPosition());
                }
            };
            handler.postDelayed(runner, 1000);
        }
    }

    private class AsyncSermonPlay extends AsyncTask<String, Void, Void> {
        @Override
        protected Void doInBackground(String... strings) {
            if (audioPlayer != null) {
                audioPlayer.getPlayer().stop();
            }
            audioPlayer = audioPlayer.INSTANCE(strings[0]);
            sermonPlayer = audioPlayer.getPlayer();
            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            setTime(totalTime, sermonPlayer.getDuration());
            playBar.setMax(sermonPlayer.getDuration());
        }
    }
}
