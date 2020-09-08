package com.example.step_music_player.playing_service;

import android.annotation.SuppressLint;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.util.Log;
import android.util.TypedValue;
import android.widget.RemoteViews;

import androidx.core.app.NotificationCompat;

import com.example.step_music_player.MainActivity;
import com.example.step_music_player.R;
import com.example.step_music_player.music_tool.music_information;

import java.io.File;

public class player_notification {
    private Context context;
    private MainActivity mainActivity;
    private Notification notification;
    private RemoteViews notification_view;
    private NotificationManager nm;
    private boolean playing;
    private NotificationCompat.Builder builder;

    public player_notification(Context context, MainActivity mainActivity){
        this.context=context;
        this.mainActivity=mainActivity;
    }

    @SuppressLint("WrongConstant")
    public void initview(){
        TypedValue tv_front_icon, tv_next_icon, tv_big_play_icon;
        tv_front_icon=new TypedValue();
        tv_next_icon=new TypedValue();
        tv_big_play_icon=new TypedValue();
        notification_view=new RemoteViews(context.getPackageName(), R.layout.notification_layout);
        notification_view.setTextViewText(R.id.notification_songname, "歌曲名");
        notification_view.setTextViewText(R.id.notification_artist,"歌手");
        notification_view.setImageViewResource(R.id.notification_album_img, R.mipmap.default_album_photo);
        context.getTheme().resolveAttribute(R.attr.front_button_icon, tv_front_icon, true);
        notification_view.setImageViewResource(R.id.notifiation_front_icon, tv_front_icon.resourceId);
        context.getTheme().resolveAttribute(R.attr.next_button_icon, tv_next_icon, true);
        notification_view.setImageViewResource(R.id.notification_next_icon, tv_next_icon.resourceId);
        context.getTheme().resolveAttribute(R.attr.big_play_icon, tv_big_play_icon, true);
        notification_view.setImageViewResource(R.id.notification_playpause_icon, tv_big_play_icon.resourceId);
        Intent front_intent=new Intent("FRONT");
        PendingIntent pi_front=PendingIntent.getBroadcast(mainActivity,0, front_intent, PendingIntent.FLAG_UPDATE_CURRENT);
        notification_view.setOnClickPendingIntent(R.id.notifiation_front_icon, pi_front);
        Intent playpause_intent=new Intent("PLAYPAUSE");
        PendingIntent pi_playpause=PendingIntent.getBroadcast(mainActivity,0,playpause_intent,PendingIntent.FLAG_UPDATE_CURRENT);
        notification_view.setOnClickPendingIntent(R.id.notification_playpause_icon, pi_playpause);
        Intent next_intent=new Intent("NEXT");
        PendingIntent pi_next=PendingIntent.getBroadcast(mainActivity,0,next_intent,PendingIntent.FLAG_UPDATE_CURRENT);
        notification_view.setOnClickPendingIntent(R.id.notification_next_icon,pi_next);
        nm= (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel channel = nm.getNotificationChannel(context.getPackageName());
            if (channel == null) {
                nm.createNotificationChannel(new NotificationChannel(context.getPackageName(), "步奏Player消息", NotificationManager.IMPORTANCE_MIN));
            }
            builder=new NotificationCompat.Builder(context, context.getPackageName());
        }
        else{ builder=new NotificationCompat.Builder(context, null); }
        builder.setContent(notification_view);
        builder.setCustomContentView(notification_view);
        builder.setWhen(System.currentTimeMillis());
        builder.setOngoing(true);
        builder.setVisibility(Notification.VISIBILITY_PUBLIC);
        builder.setDefaults(Notification.DEFAULT_LIGHTS);
        builder.setSmallIcon(R.mipmap.icon_foreground);
        builder.setPriority(NotificationCompat.PRIORITY_MAX);
        builder.setAutoCancel(false);
        builder.setOngoing(true);
        notification=builder.build();
        notification.flags=Notification.FLAG_ONGOING_EVENT;
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.O){ nm.notify("步奏Player",2020,notification); }
        else{ nm.notify(2020,notification); }
        Log.i("Notification info","I ran notification init");
        playing=false;
    }

    public void cancel_nm(){ if(nm!=null){ nm.cancel(2020); } }

    public void setPlaying(boolean playing){
        this.playing=playing;
        TypedValue tv=new TypedValue();
        if (playing) { context.getTheme().resolveAttribute(R.attr.big_pause_icon, tv, true); }
        else { context.getTheme().resolveAttribute(R.attr.big_play_icon, tv, true); }
        notification_view.setImageViewResource(R.id.notification_playpause_icon,tv.resourceId);
        notification.contentView=notification_view;
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.O){ nm.notify("步奏Player",2020,notification); }
        else{ nm.notify(2020,notification); }
        Log.i("Notification info","I ran notification setplaying.");
    }

    public void update_current_song_information(music_information new_song_information){
        notification_view.setTextViewText(R.id.notification_songname,new_song_information.getSong_name());
        notification_view.setTextViewText(R.id.notification_artist,new_song_information.getSong_artist());
        if (new_song_information.getSong_album_img_path().equals("-1")) { notification_view.setImageViewResource(R.id.notification_album_img,R.mipmap.default_album_photo); }
        else { notification_view.setImageViewUri(R.id.notification_album_img,Uri.fromFile(new File(new_song_information.getSong_album_img_path()))); }
        notification.contentView=notification_view;
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.O){ nm.notify("步奏Player",2020,notification); }
        else{ nm.notify(2020,notification); }
        Log.i("Notification info","I ran notification update info.");
    }

    public void updateTheme(){
        TypedValue tv_theme_color=new TypedValue();
        TypedValue tv_front_icon=new TypedValue();
        TypedValue tv_next_icon=new TypedValue();
        TypedValue tv_playpause_icon=new TypedValue();
        context.getTheme().resolveAttribute(R.attr.theme_color, tv_theme_color, true);
        context.getTheme().resolveAttribute(R.attr.front_button_icon, tv_front_icon, true);
        context.getTheme().resolveAttribute(R.attr.next_button_icon, tv_next_icon, true);
        if (playing) { context.getTheme().resolveAttribute(R.attr.big_pause_icon, tv_playpause_icon, true); }
        else { context.getTheme().resolveAttribute(R.attr.big_play_icon, tv_playpause_icon, true); }
        notification_view.setTextColor(R.id.notification_songname,tv_theme_color.data);
        notification_view.setTextColor(R.id.notification_artist,tv_theme_color.data);
        notification_view.setImageViewResource(R.id.notifiation_front_icon,tv_front_icon.resourceId);
        notification_view.setImageViewResource(R.id.notification_next_icon,tv_next_icon.resourceId);
        notification_view.setImageViewResource(R.id.notification_playpause_icon,tv_playpause_icon.resourceId);
        notification.contentView=notification_view;
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.O){ nm.notify("步奏Player",2020,notification); }
        else{ nm.notify(2020,notification); }
        Log.i("Notification info","I ran notification theme change");
    }
}
