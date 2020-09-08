package com.example.step_music_player.UI;

import android.animation.ObjectAnimator;
import android.content.Context;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Build;
import android.util.TypedValue;
import android.view.animation.LinearInterpolator;
import android.widget.ImageView;
import android.widget.SeekBar;
import android.widget.TextView;

import androidx.annotation.RequiresApi;

import com.example.step_music_player.MainActivity;
import com.example.step_music_player.R;
import com.example.step_music_player.animations.GradientTextView;
import com.example.step_music_player.animations.MoveTextView;
import com.example.step_music_player.music_tool.music_information;

import java.io.File;
import java.text.DecimalFormat;

public class playzone_operation{

    private GradientTextView main_title;
    private MoveTextView playzone_name,playzone_artist;
    private TextView playzone_time,playzone_speed;
    private ImageView playzone_img,playzone_playpause_icon,playzone_front_icon,playzone_next_icon,playzone_stepmode_icon,playzone_playmode_icon;
    private SeekBar playzone_seekbar;
    private Context playzone_context;
    private ObjectAnimator playzone_img_anim;
    private TypedValue tv_seekbar_thumb_icon,tv_theme_color,tv_front_icon,tv_playpause_icon,tv_next_icon,tv_seekbar_bg;
    private Rect old_rect;
    private Drawable new_thumb;
    private boolean playing,anim_started;

    public playzone_operation(MainActivity mainActivity, Context context) {
        playzone_name=mainActivity.findViewById(R.id.playzone_songname);
        playzone_artist=mainActivity.findViewById(R.id.playzone_songartist);
        playzone_time=mainActivity.findViewById(R.id.playzone_seekbar_time);
        playzone_speed=mainActivity.findViewById(R.id.playzone_seekbar_speed);
        playzone_img=mainActivity.findViewById(R.id.playzone_album_img);
        playzone_img_anim= ObjectAnimator.ofFloat(playzone_img,"rotation",0.0f,360.0f);
        playzone_img_anim.setRepeatCount(-1);
        playzone_stepmode_icon=mainActivity.findViewById(R.id.stepmode_icon);
        playzone_playmode_icon=mainActivity.findViewById(R.id.playmode_icon);
        playzone_front_icon=mainActivity.findViewById(R.id.playzone_fronticon);
        playzone_playpause_icon=mainActivity.findViewById(R.id.playzone_play_pause_icon);
        playzone_next_icon=mainActivity.findViewById(R.id.playzone_nexticon);
        playzone_seekbar=mainActivity.findViewById(R.id.playzone_seekbar);
        main_title=mainActivity.findViewById(R.id.display_title);
        playzone_context=context;
        tv_seekbar_thumb_icon=new TypedValue();
        tv_theme_color=new TypedValue();
        tv_front_icon=new TypedValue();
        tv_playpause_icon=new TypedValue();
        tv_next_icon=new TypedValue();
        tv_seekbar_bg=new TypedValue();
    }

    public void update_playzone_information(music_information new_song_information,boolean start_flag){
        playzone_name.setText(cut_song_name(new_song_information.getSong_name()).trim());
        main_title.setText(cut_song_name(new_song_information.getSong_name()).trim()+'〔'+new_song_information.getSong_artist()+'〕');
        playzone_artist.setText(new_song_information.getSong_artist().trim());
        if (new_song_information.getSong_album_img_path().equals("-1")){ playzone_img.setImageResource(R.mipmap.default_album_photo); }
        else { playzone_img.setImageURI(Uri.fromFile(new File(new_song_information.getSong_album_img_path()))); }
        playzone_seekbar.setMax(new_song_information.getSong_duration());
        TypedValue tv=new TypedValue();
        if (start_flag){
            playzone_context.getTheme().resolveAttribute(R.attr.big_play_icon,tv,true);
            anim_started=false;
        }
        else{
            playzone_context.getTheme().resolveAttribute(R.attr.big_pause_icon,tv,true);
            anim_started=true;
            set_album_img_anim(8000);
        }
        playzone_playpause_icon.setImageResource(tv.resourceId);
    }

    public void setstepmode_icon(int stepmode){
        switch (stepmode){
            case 0:{ playzone_stepmode_icon.setImageResource(R.mipmap.nostep); }
            break;
            case 1:{ playzone_stepmode_icon.setImageResource(R.mipmap.footstep); }
            break;
            case 2:{ playzone_stepmode_icon.setImageResource(R.mipmap.gps); }
            break;
            case 3:{ playzone_stepmode_icon.setImageResource(R.mipmap.network); }
            break;
        }
    }

    public void setplaymode_icon(int playmode){
        switch (playmode){
            case 0:{ playzone_playmode_icon.setImageResource(R.mipmap.ordered); }
            break;
            case 1:{ playzone_playmode_icon.setImageResource(R.mipmap.loop_list); }
            break;
            case 2:{ playzone_playmode_icon.setImageResource(R.mipmap.loop1); }
            break;
            case 3:{ playzone_playmode_icon.setImageResource(R.mipmap.random_play); }
            break;
        }
    }

    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    public void in_order_last_stop(){
        TypedValue tv=new TypedValue();
        playzone_context.getTheme().resolveAttribute(R.attr.big_play_icon,tv,true);
        playzone_playpause_icon.setImageResource(tv.resourceId);
        playzone_img_anim.pause();
    }

    public void set_album_img_anim(int anmi_duration){
        playzone_img_anim.setDuration(anmi_duration);
        playzone_img_anim.setInterpolator(new LinearInterpolator());
        playzone_img_anim.start();
    }

    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    public void alter_album_img_anim(int change_toward){
        if (change_toward==0){
            if(anim_started){ playzone_img_anim.resume(); }
            else {
                set_album_img_anim(8000);
                anim_started=true;
            }
        }
        else{ playzone_img_anim.pause(); }
    }

    public void change_speedtext(float speed){
        DecimalFormat df = new DecimalFormat("0.00");
        playzone_speed.setText(df.format(speed)+'x');
    }

    public void change_play_pause_icon(MediaPlayer player){
        TypedValue tv=new TypedValue();
        if (player.isPlaying()){ // set to play
            playzone_context.getTheme().resolveAttribute(R.attr.big_play_icon,tv,true);
        }
        else{ playzone_context.getTheme().resolveAttribute(R.attr.big_pause_icon,tv,true); }
        playzone_playpause_icon.setImageResource(tv.resourceId);
    }

    public void seekbar_progress(boolean changing,MediaPlayer player){
        while(!changing&&player.isPlaying()){
            if(playing) {
                playzone_context.getTheme().resolveAttribute(R.attr.seekbar_thumb_icon, tv_seekbar_thumb_icon, true);
                old_rect = playzone_seekbar.getThumb().getBounds();
                new_thumb = playzone_context.getResources().getDrawable(tv_seekbar_thumb_icon.resourceId);
                new_thumb.setBounds(old_rect);
                playzone_seekbar.setThumb(new_thumb);
                playzone_context.getTheme().resolveAttribute(R.attr.seekbar_progress_background, tv_seekbar_bg, true);
                old_rect=playzone_seekbar.getProgressDrawable().getBounds();
                playzone_seekbar.setProgressDrawable(playzone_context.getResources().getDrawable(tv_seekbar_bg.resourceId));
                playzone_seekbar.getProgressDrawable().setBounds(old_rect);
                playing=false;
            }
            playzone_seekbar.setProgress(player.getCurrentPosition());
            try{ Thread.sleep(500); }
            catch (InterruptedException e){}
        }
    }

    public void setseekbar_time(String seekbar_progress){ playzone_time.setText(seekbar_progress.trim()); }

    private String cut_song_name(String song_name) {
        int name_length = song_name.length();
        String cutted_song_name = song_name;
        if(name_length>=5){
            switch (song_name.substring(name_length - 4)){
                case ".mp3":
                case ".aac":
                case ".m4a":
                case ".3gp":
                case ".amr":
                case ".wav":
                case ".wma":{
                    cutted_song_name = song_name.substring(0, name_length - 4).trim();
                }
            }
        }
        return cutted_song_name;
    }

    public void update_playzone_theme(MediaPlayer player, String current_song_img_file_path){
        playzone_context.getTheme().resolveAttribute(R.attr.theme_color,tv_theme_color,true);
        int theme_color=tv_theme_color.data;
        playzone_speed.setTextColor(theme_color);
        playzone_time.setTextColor(theme_color);
        playzone_context.getTheme().resolveAttribute(R.attr.front_button_icon,tv_front_icon,true);
        playzone_context.getTheme().resolveAttribute(R.attr.next_button_icon,tv_next_icon,true);
        playzone_front_icon.setImageResource(tv_front_icon.resourceId);
        playzone_next_icon.setImageResource(tv_next_icon.resourceId);
        if(player.isPlaying()){ playzone_context.getTheme().resolveAttribute(R.attr.big_pause_icon,tv_playpause_icon,true); }
        else{ playzone_context.getTheme().resolveAttribute(R.attr.big_play_icon,tv_playpause_icon,true); }
        playzone_playpause_icon.setImageResource(tv_playpause_icon.resourceId);
        if(current_song_img_file_path.equals("-1")){ playzone_img.setImageResource(R.mipmap.default_album_photo); }
        else { playzone_img.setImageURI(Uri.fromFile(new File(current_song_img_file_path))); }
        if(!player.isPlaying()){
            playzone_context.getTheme().resolveAttribute(R.attr.seekbar_thumb_icon,tv_seekbar_thumb_icon,true);
            old_rect=playzone_seekbar.getThumb().getBounds();
            new_thumb=playzone_context.getResources().getDrawable(tv_seekbar_thumb_icon.resourceId);
            new_thumb.setBounds(old_rect);
            playzone_seekbar.setThumb(new_thumb);
            playzone_context.getTheme().resolveAttribute(R.attr.seekbar_progress_background, tv_seekbar_bg, true);
            old_rect=playzone_seekbar.getProgressDrawable().getBounds();
            playzone_seekbar.setProgressDrawable(playzone_context.getResources().getDrawable(tv_seekbar_bg.resourceId));
            playzone_seekbar.getProgressDrawable().setBounds(old_rect);
            playing=false;
        }
        else{ playing=true; }
    }

    public void update_maintitle(String new_title){ main_title.setText(new_title); }
    public String get_maintitle(){ return (String) main_title.getText(); }
}
