package com.mysampleapp.util;

import android.content.Context;
import android.content.SharedPreferences;
import android.media.MediaPlayer;

import java.io.IOException;

/**
 * Created by minjungkim on 7/18/17.
 */

public class AudioPlayer {
    private String url;
    private MediaPlayer player;

    private AudioPlayer(String url) {
        setUrl(url);
    }

    private static volatile AudioPlayer audioPlayer;

    public static AudioPlayer INSTANCE(String url) {
        if  (audioPlayer == null) {
            synchronized (AudioPlayer.class) {
                if (audioPlayer == null) {
                    audioPlayer = new AudioPlayer(url);
                }
            }
        } else {
            audioPlayer.setUrl(url);
        }
        return audioPlayer;
    }

    public static AudioPlayer getsInstance() {
        return audioPlayer;
    }

    public String getUrl() {
        return this.url;
    }

    private void setUrl(String url) {
        this.url = url;
        player = new MediaPlayer();
        try {
            player.setDataSource("http://bitly.com/" + url);
            player.prepare();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public MediaPlayer getPlayer() {
        return this.player;
    }
}
