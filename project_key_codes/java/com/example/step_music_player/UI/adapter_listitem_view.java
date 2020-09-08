package com.example.step_music_player.UI;

import android.widget.ImageView;
import android.widget.TextView;

public class adapter_listitem_view {
    ImageView song_album_img;
    TextView song_name;
    TextView song_artist;
    TextView song_size;
    TextView song_duration;

    public void setSong_name(TextView songname){
        song_name=songname;
    }
    public TextView getSong_name(){
        return song_name;
    }
    public void setSong_artist(TextView songartist){
        song_artist=songartist;
    }
    public TextView getSong_artist(){
        return song_artist;
    }
    public void setSong_size(TextView songsize){
        song_size=songsize;
    }
    public TextView getSong_Size(){
        return song_size;
    }
    public void setSong_duration(TextView songduration){
        song_duration=songduration;
    }
    public TextView getSong_duration(){
        return song_duration;
    }
    public void setSong_album_img(ImageView albumimg){
        song_album_img=albumimg;
    }
    public ImageView getSong_album_img(){
        return song_album_img;
    }

}
