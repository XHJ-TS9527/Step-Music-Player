package com.example.step_music_player.playing_service;

import android.media.MediaPlayer;
import android.media.PlaybackParams;
import android.os.Build;

import androidx.annotation.RequiresApi;

import java.io.IOException;

public class music_play_tool {

    public void play_song(MediaPlayer player,String song_path){
        if (player.isPlaying()){ player.pause(); }
        try{
            player.reset();
            player.setDataSource(song_path);
            player.prepare();
            player.start();
        } catch (IOException e) {}
    }

    public void play_pause(MediaPlayer player){
        if (player.isPlaying()){ player.pause(); }
        else{ player.start(); }
    }

    public void setPlayerPosition(MediaPlayer player,int target_position){ player.seekTo(target_position); }

    @RequiresApi(api = Build.VERSION_CODES.M)
    public void setPlayerspeed(MediaPlayer player, float target_speed){
        boolean playing=player.isPlaying();
        PlaybackParams params=player.getPlaybackParams();
        params.setSpeed(target_speed);
        player.setPlaybackParams(params);
        if(!playing){ player.pause(); }
    }

}
