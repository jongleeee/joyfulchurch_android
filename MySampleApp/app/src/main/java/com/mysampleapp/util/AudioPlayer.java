package com.mysampleapp.util;

import android.media.MediaPlayer;

import java.io.IOException;

/**
 * Created by minjungkim on 7/18/17.
 */

public class AudioPlayer extends MediaPlayer {

    private String currentSermonURL;

    private int lastTotalTime;

    // lastCurrentPosition is in milliseconds.
    private int lastCurrentPosition;

    private boolean isReleased = true;

    private boolean isServiceBound;

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

    public String getCurrentSermonURL() {
        return this.currentSermonURL;
    }

    public void setCurrentSermonURL(String sermonURL) {
        currentSermonURL = sermonURL;
    }

    public void playSermon(String sermonURL) {
        if (!isReleased && sermonURL.equals(currentSermonURL)) {
            super.start();
        } else {
            // start all over again
            // play with latest current time if available.
            currentSermonURL = sermonURL;
            super.reset();
            try {
                super.setDataSource("http://bitly.com/" + sermonURL);
                super.prepareAsync();
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

    @Override
    public boolean isPlaying() {
        return !getIsReleased() && super.isPlaying();
    }

    @Override
    public void pause() {
        lastCurrentPosition = this.getCurrentPosition();
        super.pause();
    }

    @Override
    public void release() {
        lastCurrentPosition = this.getCurrentPosition();
        lastTotalTime = getTotalTime();
        super.reset();
        setIsReleased(true);
        setIsServiceBound(false);
        super.release();
    }

    public boolean isSameSermon(String url) {
        return this.currentSermonURL != null && this.currentSermonURL.equals(url);
    }

    // returns time in millisecond
    public int getTotalTime() {
        if (getIsReleased()) {
            return getLastTotalTime();
        }
        return this.getDuration();
    }

    // returns time in millisecond
    public int getCurrentTime() {
        return this.getCurrentPosition();
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
        this.seekTo(0);
        pause();
    }
}
