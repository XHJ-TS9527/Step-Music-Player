package com.example.step_music_player.music_tool;

import java.io.Serializable;

public class music_information implements Serializable {
    private int song_id; // the id of the song
    private String song_name; // the display name of the song
    private int song_duration; // the duration of the song
    private String song_artist; // the singer of the song
    private String song_album; // the album name of the song
    private String song_file_name; // the filename of the song
    private String song_path; // the path of the song file
    private String song_album_img_path; // the path of album image
    private int song_size; // the size of the song file

    public void music_information(){ // initialization
        song_id=-1;
        song_name=null;
        song_duration=-1;
        song_artist=null;
        song_album=null;
        song_file_name=null;
        song_path=null;
        song_album_img_path=null;
        song_size=-1;
    }

    // functions of getting and setting value of members
    public int getSong_id(){
        return song_id;
    }
    public void setSong_id(int target_id){
        song_id=target_id;
    }

    public String getSong_name(){
        return song_name;
    }
    public void setSong_name(String target_name){
        song_name=target_name;
    }
    public int getSong_duration(){
        return song_duration;
    }
    public void setSong_duration(int target_duration){
        song_duration=target_duration;
    }

    public String getSong_artist(){
        return song_artist;
    }
    public void setSong_artist(String target_artist){
        song_artist=target_artist;
    }

    public String getSong_album(){
        return song_album;
    }
    public void setSong_album(String target_album){
        song_album=target_album;
    }

    public String getSong_file_name(){
        return song_file_name;
    }
    public void setSong_file_name(String target_file_name){
        song_file_name=target_file_name;
    }

    public String getSong_path(){
        return song_path;
    }
    public void setSong_path(String target_path){
        song_path=target_path;
    }

    public String getSong_album_img_path(){
        return song_album_img_path;
    }
    public void setSong_album_img_path(String target_album_img_path){
        song_album_img_path=target_album_img_path;
    }

    public int getSong_size(){
        return song_size;
    }

    public void setSong_size(int target_size){
        song_size=target_size;
    }

    // when becoming a rubbish
    @Override
    protected void finalize() throws Throwable {
        super.finalize();
    }
}