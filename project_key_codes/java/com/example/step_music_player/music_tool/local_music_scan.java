package com.example.step_music_player.music_tool;

import android.content.ContentResolver;
import android.content.Context;

import android.content.res.Resources;
import android.database.Cursor;

import android.net.Uri;

import android.os.Build;

import android.provider.MediaStore;
import android.util.Log;

import androidx.annotation.RequiresApi;

import com.example.step_music_player.R;

import java.io.File;

import java.math.BigDecimal;

import java.util.ArrayList;
import java.util.List;

public class local_music_scan {
    private ContentResolver local_music_resolver,album_img_resolver;

    @RequiresApi(api = Build.VERSION_CODES.O)
    public List<music_information> scan_music(Context context){
        String album_img_path;
        local_music_resolver=context.getContentResolver();
        List<music_information> local_scan_music_list=new ArrayList<>();
        Cursor music_cursor=local_music_resolver.query(MediaStore.Audio.Media.EXTERNAL_CONTENT_URI,
                new String[] {
                        MediaStore.Audio.Media._ID,MediaStore.Audio.Media.DISPLAY_NAME,
                        MediaStore.Audio.Media.DURATION,MediaStore.Audio.Media.ARTIST,
                        MediaStore.Audio.Media.ALBUM,MediaStore.Audio.Media.TITLE,
                        MediaStore.Audio.Media.DATA, MediaStore.Audio.Media.SIZE,
                        MediaStore.Audio.Media.ALBUM_ID},
                null,null,MediaStore.Audio.AudioColumns.IS_MUSIC); // scan all local songs
        if(music_cursor!=null){
            for(int idx=0;idx<music_cursor.getCount();idx++){
                music_information this_song_info=new music_information();
                // move to next list item
                music_cursor.moveToNext();
                //set music information
                this_song_info.setSong_id(music_cursor.getInt(0));
                this_song_info.setSong_name(music_cursor.getString(1));
                this_song_info.setSong_duration(music_cursor.getInt(2));
                this_song_info.setSong_artist(music_cursor.getString(3));
                this_song_info.setSong_album(music_cursor.getString(4));
                this_song_info.setSong_file_name(music_cursor.getString(5));
                this_song_info.setSong_path(music_cursor.getString(6));
                this_song_info.setSong_size(music_cursor.getInt(7));
                album_img_path=get_album_img(music_cursor.getInt(8),context);
                //Log.i("Info","The song name is "+music_cursor.getString(1));
                //Log.i("Info","The artist name is "+music_cursor.getString(3));
                //Log.i("Info","The album image path is "+album_img_path);
                this_song_info.setSong_album_img_path(album_img_path);
                local_scan_music_list.add(this_song_info);
            }
        }
        return local_scan_music_list;
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    private String get_album_img(int album_id,Context context){ // function of questing album image path
        album_img_resolver=context.getContentResolver();
        String album_image_path=null;
        String projection[]=new String[] {MediaStore.Audio.Albums.ALBUM_ART};
        try{
            Cursor album_image_cursor=album_img_resolver.query(MediaStore.Audio.Albums.EXTERNAL_CONTENT_URI,
                    projection,MediaStore.Audio.Albums._ID+" = ?", new String[]{Integer.toString(album_id)},
                    null);
            if(album_image_cursor.moveToFirst()){
                album_image_path=album_image_cursor.getString(0);
            }
            album_image_cursor.close();
        }
        catch(Exception e){}
        if(album_image_path==null){ // no photo, use default album photo
            album_image_path="-1";
        }
        return album_image_path;
    }

    public int[] search_songs(String search_text,List<music_information> temporary_song_list){
        int[] search_result=new int[temporary_song_list.size()];
        int idx,cnt=0;
        String this_song_name;
        for(idx=0;idx<temporary_song_list.size();idx++){
            this_song_name=temporary_song_list.get(idx).getSong_name();
            if(this_song_name.indexOf(search_text)!=-1){
                search_result[cnt]=idx;
                cnt++;
            }
        }
        for(idx=cnt;idx<temporary_song_list.size();idx++){ search_result[idx]=-1; }
        return search_result;
    }

}