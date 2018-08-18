package com.mysampleapp.util;

import android.media.MediaPlayer;

import com.mysampleapp.SermonPlayListener;

import java.io.IOException;

/**
 * Created by minjungkim on 7/18/17.
 */

public class AudioPlayer {

    private String currentSermonURL;

    private int lastTotalTime;

    // lastCurrentPosition is in milliseconds.
    private int lastCurrentPosition = -1;

    private boolean isReleased = true;

    private boolean isServiceBound;

    private MediaPlayer mediaPlayer;

    private SermonPlayListener sermonPlayListener;

    private AudioPlayer() {
        super();
    }

    private static volatile AudioPlayer audioPlayer;

    public static AudioPlayer INSTANCE() {
        if  (audioPlayer == null) {
            synchronized (AudioPlayer.class) {
                if (audioPlayer == null) {
                    audioPlayer = new AudioPlayer();
                }
            }
        }
        return audioPlayer;
    }

    public void addListener(SermonPlayListener listener) {
        sermonPlayListener = listener;
    }

    public String getCurrentSermonURL() {
        return this.currentSermonURL;
    }

    public void setCurrentSermonURL(String sermonURL) {
        currentSermonURL = sermonURL;
    }

    public void initializeMediaPlayer() {
        mediaPlayer = new MediaPlayer();

        mediaPlayer.setOnCompletionListener(new MediaPlayer.OnCompletionListener(){
            @Override
            public void onCompletion(MediaPlayer mp) {
                sermonPlayListener.sermonFinishedPlaying();
            }
        });
//
        mediaPlayer.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
            @Override
            public void onPrepared(MediaPlayer mediaPlayer) {
                sermonPlayListener.sermonReadyToPlay();
            }
        });
    }

    public MediaPlayer getMediaPlayer() {
        return mediaPlayer;
    }

    public void playSermon(String sermonURL) {
        if (!isReleased && sermonURL.equals(currentSermonURL)) {
            mediaPlayer.start();
        } else {
            // start all over again
            // play with latest current time if available.
            setLastTotalTime(-1);
            currentSermonURL = sermonURL;
            if (!getIsReleased()) {
                reset();
                release();
            }
            initializeMediaPlayer();
            mediaPlayer.reset();
            try {
                mediaPlayer.setDataSource("http://bitly.com/" + sermonURL);
                mediaPlayer.prepareAsync();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public void setIsReleased(boolean isReleased) {
        this.isReleased = isReleased;
    }

    public boolean getIsReleased() {
        return isReleased;
    }

    public void setIsServiceBound(boolean isServiceBound) {
        this.isServiceBound = isServiceBound;
    }

    public boolean getIsServiceBound() {
        return isServiceBound;
    }

    public boolean isPlaying() {
        return !getIsReleased() && mediaPlayer.isPlaying();
    }

    public void pause() {
        lastCurrentPosition = mediaPlayer.getCurrentPosition();
        mediaPlayer.pause();
    }

    public void release() {
        lastCurrentPosition = mediaPlayer.getCurrentPosition();
        lastTotalTime = getTotalTime();
        setIsReleased(true);
        setIsServiceBound(false);
        mediaPlayer.release();
    }

    public boolean isSameSermon(String url) {
        return this.currentSermonURL != null && this.currentSermonURL.equals(url);
    }

    // returns time in millisecond
    public int getTotalTime() {
        if (getIsReleased()) {
            return getLastTotalTime();
        }
        return mediaPlayer.getDuration();
    }

    // returns time in millisecond
    public int getCurrentTime() {
        return mediaPlayer.getCurrentPosition();
    }

    // returns last current time in millisecond
    public int getLastCurrentPosition() {
        return lastCurrentPosition;
    }

    public void setLastCurrentPosition(int position) {
        lastCurrentPosition = position;
    }

    public int getLastTotalTime() {
        return lastTotalTime;
    }

    public void setLastTotalTime(int duration) {
        lastTotalTime = duration;
    }

    public void reset() {
        setLastCurrentPosition(-1);
        setLastTotalTime(-1);
        mediaPlayer.seekTo(0);
        pause();
    }

    public void seekTo(int duration) {
        mediaPlayer.seekTo(duration);
    }

    public void start() {
        mediaPlayer.start();
    }

    public void stop() {
        mediaPlayer.stop();
    }
}
