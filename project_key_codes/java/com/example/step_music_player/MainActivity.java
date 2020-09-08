package com.example.step_music_player;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import androidx.core.app.ActivityCompat;

import android.Manifest;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;

import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;

import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.os.PowerManager;
import android.os.StrictMode;
import android.provider.Settings;
import android.util.Log;
import android.view.Gravity;

import android.view.View;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import com.example.step_music_player.UI.conditionbar_setting;
import com.example.step_music_player.UI.listview_adapter;
import com.example.step_music_player.UI.playzone_operation;
import com.example.step_music_player.UI.song_listview_setting;
import com.example.step_music_player.music_tool.local_music_scan;
import com.example.step_music_player.music_tool.music_information;
import com.example.step_music_player.playing_service.broadcast_coper;
import com.example.step_music_player.playing_service.headphone_operation;
import com.example.step_music_player.playing_service.headphone_plug;
import com.example.step_music_player.playing_service.music_play_tool;
import com.example.step_music_player.playing_service.player_notification;
import com.example.step_music_player.playing_service.shakeshake_component;
import com.example.step_music_player.settings_and_menu.information_display_popwin;
import com.example.step_music_player.settings_and_menu.playmode_setting_popwin;
import com.example.step_music_player.settings_and_menu.popmenu_discovery;
import com.example.step_music_player.settings_and_menu.popmenu_settings;
import com.example.step_music_player.settings_and_menu.preference_setting_popwin;
import com.example.step_music_player.settings_and_menu.search_popwin;
import com.example.step_music_player.settings_and_menu.stepmode_setting_popwin;
import com.example.step_music_player.settings_and_menu.style_setting_popwin;
import com.example.step_music_player.step_sensor.sensor_broadcast_coper;
import com.example.step_music_player.step_sensor.step_GPS_component;
import com.example.step_music_player.step_sensor.step_NETWORK_component;
import com.example.step_music_player.step_sensor.step_sensor_component;

import java.io.IOException;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Random;

public class MainActivity extends AppCompatActivity{
    // permissions
    private final static String[] permissions=new String[]  {Manifest.permission.READ_EXTERNAL_STORAGE,
            Manifest.permission.WRITE_EXTERNAL_STORAGE,Manifest.permission.READ_PHONE_STATE,
            Manifest.permission.ACTIVITY_RECOGNITION,Manifest.permission.ACCESS_COARSE_LOCATION,
            Manifest.permission.ACCESS_FINE_LOCATION,Manifest.permission.INTERNET,
            Manifest.permission.VIBRATE,Manifest.permission.WAKE_LOCK,
            Manifest.permission.ACCESS_WIFI_STATE,Manifest.permission.CHANGE_WIFI_STATE} ;
    private AlertDialog mPermissionDialog;

    // song information and display issues
    private local_music_scan music_scan_cls;
    private List<music_information> song_list,search_cache_song_list;
    private int[] possible_position;
    private Thread rescan_thread;
    private search_popwin search_song_popwin;
    private Button search_btn;
    private boolean searching;
    private String search_text;

    // display setting
    private song_listview_setting display_setting_cls;
    private listview_adapter adapter;
    private ListView display_listview;
    private int listview_last_item;
    private boolean show_background;

    // song playing issues
    private int current_playing_song_ptr,cache_playing_song_ptr;
    private int looping_mode; //0: play list in order, 1: loop list, 2: loop single, 3: randomly
    private int playing_flag;
    private boolean dragging_seekbar,shake_next_song;
    private Random play_randnum;
    private MediaPlayer music_player;
    private music_play_tool musicPlayTool;
    private playmode_setting_popwin playmode_popwin;
    private Button submit_playmode_btn;
    private shakeshake_component shake_sensor_component;
    private player_notification notification;
    private broadcast_coper notification_broadcastCoper;
    private headphone_plug headphonePlug;
    private headphone_operation headphoneOperation;

    //playzone display issues
    private playzone_operation playzoneOperation;
    private ImageView front_button,playing_button,next_button,stepmode_icon,playmode_icon,album_img_circle;
    private TextView speed_text;
    private SeekBar playzone_seekbar;
    private Thread seekbar_thread;

    // theme and preference and UI and settings
    private SharedPreferences user_preference;
    private SharedPreferences.Editor preference_editor;
    private style_setting_popwin style_popwin;
    private popmenu_settings setting_menu;
    private popmenu_discovery discovery_menu;
    private preference_setting_popwin preference_popwin;
    private ImageView setting_icon,discovery_icon;
    private Button submit_theme_btn,submit_preference_btn;
    private String theme;
    private conditionbar_setting statusbar_setting;
    private Intent battery_intent;

    //speed sampling issue
    private step_sensor_component sensor_component;
    private step_GPS_component GPS_component;
    private step_NETWORK_component NETWORK_component;
    private sensor_broadcast_coper sensor_broadcast;
    private boolean has_sensor,GPS_opened,NETWORK_opened;
    private boolean remind_flag,gps_open_remind_flag,net_open_remind_flag;
    private Thread speed_thread;
    private float standard_speed,lowest_speed,change_weight,final_speed;
    private double last_speed;
    private int stepmode; //0: no step, 1: step sensor, 2: GPS, 3: network
    private stepmode_setting_popwin stepmode_popwin;
    private Button submit_stepmode_btn;
    private information_display_popwin infomation_popwin;
    private String info_popwin_info,datetime_info;
    private int step_number;
    private float gps_longtitude,gps_latitude,network_longtitude,network_latitude;
    private Thread refresh_info_popwin_thread;

    // system issues
    private static final int exit_time=2000;
    private long last_press_time;
    private PowerManager pm;
    private PowerManager.WakeLock lock;
    private boolean power_ok;

    //threads
    public class seekbar_progress implements Runnable{
        @Override
        public void run(){
            playzoneOperation.seekbar_progress(dragging_seekbar,music_player);
        }
    }

    public class component_progress extends Thread implements Runnable{
        @RequiresApi(api = Build.VERSION_CODES.M)
        @Override
        public void run(){
            Message msg;
            while (music_player.isPlaying()) {
                switch (stepmode){
                    case 0:{ final_speed=0.0f; }
                    break;
                    case 1:{
                        if(has_sensor){
                            final_speed= (float) (3 * sensor_component.getStep_number());
                            Log.i("Speeddetectinfo","I ran SENSOR speed detection, the speed is "+ final_speed);
                        }
                        else{
                            if(!remind_flag){
                                msg=new Message();
                                msg.what=0;
                                speed_Toast_handler.sendMessage(msg);
                                remind_flag=true;
                            }
                            final_speed=0.0f;
                        }
                    };
                    break;
                    case 2:{
                        Log.i("Speeddetectinfo","GPS_opened state: "+GPS_opened);
                        if (GPS_opened && checkSelfPermission(Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED &&
                                checkSelfPermission(Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED){
                            final_speed=GPS_component.getSpeed();
                            Log.i("Speeddetectinfo","I ran GPS speed detection, the speed is "+final_speed);
                            if (final_speed<0){
                                if(!gps_open_remind_flag) {
                                    msg=new Message();
                                    msg.what=1;
                                    speed_Toast_handler.sendMessage(msg);
                                    gps_open_remind_flag=true;
                                }
                                final_speed=0.0f;
                            }
                        }
                        else{
                            if(!remind_flag){
                                msg=new Message();
                                if(GPS_opened) { msg.what=2; }
                                else { msg.what=3; }
                                speed_Toast_handler.sendMessage(msg);
                                remind_flag=true;
                            }
                            final_speed=0.0f;
                        }
                    };
                    break;
                    default:{
                        Log.i("Speeddetectinfo","network_opened state: "+NETWORK_opened);
                        if(!NETWORK_opened){ NETWORK_opened=NETWORK_component.init_NETWORK(); }
                        if (NETWORK_opened && checkSelfPermission(Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED
                                && checkSelfPermission(Manifest.permission.INTERNET) == PackageManager.PERMISSION_GRANTED){
                            final_speed=NETWORK_component.getSpeed();
                            Log.i("Speeddetectinfo","I ran NETWORK speed detection, the speed is "+final_speed);
                            if (final_speed<0){
                                if(!net_open_remind_flag) {
                                    msg=new Message();
                                    msg.what=4;
                                    speed_Toast_handler.sendMessage(msg);
                                    net_open_remind_flag=true;
                                }
                                final_speed=0.0f;
                            }
                        }
                        else{
                            if(!remind_flag){
                                msg=new Message();
                                if(NETWORK_opened) { msg.what=5; }
                                else { msg.what=6; }
                                speed_Toast_handler.sendMessage(msg);
                                remind_flag=true;
                            }
                            final_speed=0.0f;
                        }
                    };
                    break;
                }
                if(stepmode>0){ last_speed = ((1 - change_weight) * last_speed) + (change_weight * (0.5 * (final_speed - lowest_speed) / (standard_speed - lowest_speed) + 1)); }
                if (last_speed>3.5) { last_speed=3.5f; }
                if (last_speed<0){ last_speed=0.0001f; }
                msg=new Message();
                msg.what=0;
                set_player_speed.sendMessage(msg);
                msg=new Message();
                msg.what=0;
                speed_text_handler.sendMessage(msg);
                try{ Thread.sleep(1000); }
                catch (InterruptedException e){ }
            }
        }
    }

    public class rescan_music_progress extends Thread implements Runnable{
        @RequiresApi(api = Build.VERSION_CODES.O)
        @Override
        public void run(){
            //get the temporary file
            String current_song_path;
            int idx;
            try{ current_song_path=song_list.get(current_playing_song_ptr).getSong_path();}
            catch(Exception e){ current_song_path=""; }
            song_list=music_scan_cls.scan_music(MainActivity.this);
            //get the ptr of the current file
            if(current_song_path.length()>0){
                for(idx=0;idx<song_list.size();idx++){
                    if(song_list.get(idx).getSong_path().equals(current_song_path)){ break; }
                }
                current_playing_song_ptr=idx;
            }
            adapter.setPosition_flag(current_playing_song_ptr);
            Message msg=new Message();
            msg.what=0;
            rescan_listview_handler.sendMessage(msg);
        }
    }

    public class renew_information_progress extends Thread implements Runnable{
        @RequiresApi(api = Build.VERSION_CODES.M)
        @Override
        public void run(){
            DecimalFormat df=new DecimalFormat("##0.00000");
            Date date;
            SimpleDateFormat formatter=new SimpleDateFormat("yyyy-MM-dd  HH:mm:ss z");
            Message msg;
            while(true) {
                if(!GPS_opened){ GPS_opened=GPS_component.init_GPS(); }
                info_popwin_info = "测定速度  " + df.format(final_speed) + "m/s\n";
                if (checkSelfPermission(Manifest.permission.ACTIVITY_RECOGNITION) != PackageManager.PERMISSION_GRANTED) {
                    info_popwin_info += ("记录步数  您未开放计步器权限\n");
                } else {
                    if (has_sensor) {
                        Log.i("SENSOR_info", "SENSOR changed flag: " + sensor_component.get_changed_flag());
                        if (sensor_component.get_changed_flag()) {
                            step_number = sensor_component.get_step_number();
                            info_popwin_info += ("记录步数  " + step_number + " 步\n计步状态  实时记录\n");
                        } else {
                            info_popwin_info += "记录步数  " + step_number + " 步\n计步状态  历史记录\n";
                        }
                        preference_editor.putInt("step number", step_number);
                    } else {
                        info_popwin_info += "记录步数  您的手机不支持计步\n计步状态  实时记录\n";
                    }
                }
                if (checkSelfPermission(Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED ||
                        checkSelfPermission(Manifest.permission.ACCESS_FINE_LOCATION)!= PackageManager.PERMISSION_GRANTED) {
                    info_popwin_info += "GPS经度  您未开放GPS权限\nGPS纬度  您未开放GPS权限\nGPS数据  实时数据\n";
                } else {
                    if (GPS_component.get_enable()) {
                        float[] gps_location = GPS_component.get_location();
                        gps_longtitude = gps_location[0];
                        gps_latitude = gps_location[1];
                        if(gps_longtitude==0.0f) { info_popwin_info+=("GPS经度  "+df.format(gps_longtitude)+"°\n"); }
                        else{
                            if(gps_longtitude>0){ info_popwin_info+=("GPS经度  "+df.format(gps_longtitude)+"°E\n"); }
                            else{ info_popwin_info+=("GPS经度  "+df.format(-gps_longtitude)+"°W\n"); }
                        }
                        if(gps_latitude==0.0f){ info_popwin_info+=("GPS纬度  "+df.format(gps_latitude)+"°\n"); }
                        else{
                            if(gps_latitude>0) { info_popwin_info+=("GPS纬度  "+df.format(gps_latitude)+"°N\n"); }
                            else { info_popwin_info+=("GPS纬度  "+df.format(-gps_latitude)+"°S\n"); }
                        }
                        info_popwin_info+="GPS数据  实时数据\n";
                        preference_editor.putFloat("gps latitude", gps_latitude);
                        preference_editor.putFloat("gps longtitude", gps_longtitude);
                    } else {
                        if (gps_longtitude > 180 || gps_latitude > 90) {
                            info_popwin_info += "GPS经度  您未打开GPS\nGPS纬度  您未打开GPS\nGPS数据  实时数据\n";
                        } else {
                            if(gps_longtitude==0.0f) { info_popwin_info+=("GPS经度  "+df.format(gps_longtitude)+"°\n"); }
                            else{
                                if(gps_longtitude>0){ info_popwin_info+=("GPS经度  "+df.format(gps_longtitude)+"°E\n"); }
                                else{ info_popwin_info+=("GPS经度  "+df.format(-gps_longtitude)+"°W\n"); }
                            }
                            if(gps_latitude==0.0f){ info_popwin_info+=("GPS纬度  "+df.format(gps_latitude)+"°\n"); }
                            else{
                                if(gps_latitude>0) { info_popwin_info+=("GPS纬度  "+df.format(gps_latitude)+"°N\n"); }
                                else { info_popwin_info+=("GPS纬度  "+df.format(-gps_latitude)+"°S\n"); }
                            }
                            info_popwin_info +="GPS数据  历史数据\n";
                        }
                    }
                }
                if (checkSelfPermission(Manifest.permission.ACCESS_FINE_LOCATION)!= PackageManager.PERMISSION_GRANTED ||
                       checkSelfPermission(Manifest.permission.INTERNET) != PackageManager.PERMISSION_GRANTED) {
                    info_popwin_info += ("网测经度  您未开放网络权限\n网测纬度  您未开放网络权限\n网测数据  实时数据\n");
                } else {
                    if (NETWORK_component.get_enable()) {
                        float[] network_location = NETWORK_component.get_location();
                        network_longtitude = network_location[0];
                        network_latitude = network_location[1];
                        if(network_longtitude==0.0f) { info_popwin_info+=("网测经度  "+df.format(network_longtitude)+"°\n"); }
                        else{
                            if(network_longtitude>0){ info_popwin_info+=("网测经度  "+df.format(network_longtitude)+"°E\n"); }
                            else{ info_popwin_info+=("网测经度  "+df.format(-network_longtitude)+"°W\n"); }
                        }
                        if(network_latitude==0.0f){ info_popwin_info+=("网测纬度  "+df.format(network_latitude)+"°\n"); }
                        else{
                            if(network_latitude>0) { info_popwin_info+=("网测纬度  "+df.format(network_latitude)+"°N\n"); }
                            else { info_popwin_info+=("网测纬度  "+df.format(-network_latitude)+"°S\n"); }
                        }
                        info_popwin_info +="网测数据  实时数据";
                        preference_editor.putFloat("network latitude", network_latitude);
                        preference_editor.putFloat("network longtitude", network_longtitude);
                    } else {
                        if (network_longtitude > 180 || network_latitude > 90) {
                            info_popwin_info += ("网测经度  您未连接网络\n网测纬度  您未连接网络\n网测数据  实时数据");
                        } else {
                            if(network_longtitude==0.0f) { info_popwin_info+=("网测经度  "+df.format(network_longtitude)+"°\n"); }
                            else{
                                if(network_longtitude>0){ info_popwin_info+=("网测经度  "+df.format(network_longtitude)+"°E\n"); }
                                else{ info_popwin_info+=("网测经度  "+df.format(-network_longtitude)+"°W\n"); }
                            }
                            if(network_latitude==0.0f){ info_popwin_info+=("网测纬度  "+df.format(network_latitude)+"°\n"); }
                            else{
                                if(network_latitude>0) { info_popwin_info+=("网测纬度  "+df.format(network_latitude)+"°N\n"); }
                                else { info_popwin_info+=("网测纬度  "+df.format(-network_latitude)+"°S\n"); }
                            }
                            info_popwin_info +="网测数据  实时数据";
                        }
                    }
                }
                date=new Date(System.currentTimeMillis());
                datetime_info=formatter.format(date);
                preference_editor.commit();
                msg = new Message();
                msg.what = 0;
                renew_information_handler.sendMessage(msg);
                try { Thread.sleep(100); }
                catch (Exception e){}
            }
        }
    }


    //handler
    private Handler set_player_speed=new Handler(){
        @RequiresApi(api = Build.VERSION_CODES.M)
        @Override
        public void handleMessage(Message msg){ if (msg.what==0){ musicPlayTool.setPlayerspeed(music_player, (float) last_speed); } }
    };

    private Handler speed_text_handler=new Handler(){
        @Override
        public void handleMessage(Message msg){ if(msg.what==0){ playzoneOperation.change_speedtext((float) last_speed); } }
    };

    private Handler speed_Toast_handler=new Handler(){
        @Override
        public void handleMessage(Message msg){
            Toast msgtoast;
            switch (msg.what){
                case 0:{ msgtoast=Toast.makeText(MainActivity.this,"您的手机不支持步数检测",Toast.LENGTH_SHORT); }
                break;
                case 1:{ msgtoast= Toast.makeText(MainActivity.this, "您未打开GPS", Toast.LENGTH_SHORT); }
                break;
                case 2:{ msgtoast=Toast.makeText(MainActivity.this,"您未开启GPS权限",Toast.LENGTH_SHORT); }
                break;
                case 3:{ msgtoast=Toast.makeText(MainActivity.this,"您未打开GPS或其权限",Toast.LENGTH_SHORT); }
                break;
                case 4:{ msgtoast= Toast.makeText(MainActivity.this, "您未连接网络", Toast.LENGTH_SHORT); }
                break;
                case 5:{ msgtoast=Toast.makeText(MainActivity.this,"您未开启网络权限",Toast.LENGTH_SHORT); }
                break;
                default:{ msgtoast=Toast.makeText(MainActivity.this,"您未打开网络或其权限",Toast.LENGTH_SHORT); }
            }
            msgtoast.setGravity(Gravity.BOTTOM,0,200);
            msgtoast.show();
        }
    };

    private Handler rescan_handler=new Handler(){
        @Override
        public void handleMessage(Message msg){
            if(msg.what==0){ rescan_music(); }
        }
    };

    private Handler rescan_listview_handler=new Handler(){
        @Override
        public void handleMessage(Message msg){
            if(msg.what==0){
                display_setting_cls.refresh_display(adapter,MainActivity.this,song_list);
                Toast rescantoast=Toast.makeText(MainActivity.this,"本地音乐扫描完成",Toast.LENGTH_SHORT);
                rescantoast.setGravity(Gravity.BOTTOM,0,200);
                rescantoast.show();
            }
        }
    };

    private Handler renew_information_handler=new Handler(){
        @Override
        public void handleMessage(Message msg){
            if(msg.what==0){
                try{ if(infomation_popwin.confirm_popwin_showing()){ infomation_popwin.refresh_info(info_popwin_info,datetime_info); } }
                catch (Exception e){}
            }
        }
    };

    private Handler shake_play_next_song=new Handler(){
        @Override
        public void handleMessage(Message msg){
            if(msg.what==0){
                if(shake_next_song) {play_next_song(); }
            }
        }
    };

    private Handler headphone_plug_handler=new Handler(){
        @RequiresApi(api = Build.VERSION_CODES.KITKAT)
        @Override
        public void handleMessage(Message msg){ if(msg.what==0){ player_pause(); } }
    };

    @Override
    public void onBackPressed(){ // press two times exit
        Toast remind_toast;
        if(last_press_time+exit_time>System.currentTimeMillis()){
            super.onBackPressed();
            return;
        }
        else{
            remind_toast=Toast.makeText(MainActivity.this,"再次点击返回退出",Toast.LENGTH_SHORT);
            remind_toast.setGravity(Gravity.BOTTOM,0,200);
            remind_toast.show();
            last_press_time=System.currentTimeMillis();
        }
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        last_press_time=0;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            StrictMode.VmPolicy.Builder builder = new StrictMode.VmPolicy.Builder();
            StrictMode.setVmPolicy(builder.build());
        }
        //check permission
        permission_check();
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        boolean has_deny=false;
        Log.i("Permission_info","GrantResults: "+grantResults.length);
        if(requestCode==1){
            for(int idx=0;idx<grantResults.length;idx++){
                Log.i("Permission_info","Permission inspect:" +grantResults[idx]);
                Log.i("Permission_info","The permission is "+permissions[idx]);
                if(grantResults[idx]==-1){
                    has_deny=true;
                }
            }
            if(has_deny){
                if(mPermissionDialog==null){
                    mPermissionDialog=new AlertDialog.Builder(this)
                            .setMessage("已禁用某些权限,请手动授予权限")
                            .setPositiveButton("设置", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    mPermissionDialog.dismiss();
                                    Uri uri=Uri.parse("package:"+getPackageName());
                                    Intent permission_intent=new Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS,uri);
                                    startActivity(permission_intent);
                                }
                            })
                            .setNegativeButton("取消", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    mPermissionDialog.dismiss();
                                    Toast remind_toast=Toast.makeText(MainActivity.this,"授予权限不足",Toast.LENGTH_SHORT);
                                    remind_toast.setGravity(Gravity.BOTTOM,0,200);
                                    remind_toast.show();
                                    MainActivity.super.onBackPressed();
                                }
                            })
                            .create();
                }
                mPermissionDialog.show();
            }
            else{ music_player_initialization(); }
        }
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    private void permission_check(){
        List<String> mpermission=new ArrayList<>();
        for(int idx=0;idx<permissions.length;idx++){ if(checkSelfPermission(permissions[idx])!=PackageManager.PERMISSION_GRANTED){ mpermission.add(permissions[idx]); } }
        if(mpermission.size()>0){ ActivityCompat.requestPermissions(this,permissions,1); }
        else{ music_player_initialization(); }
    }

    @SuppressLint({"WrongViewCast", "InvalidWakeLockTag"})
    @RequiresApi(api = Build.VERSION_CODES.O)
    private void music_player_initialization(){
        try{
            battery_intent=new Intent(Settings.ACTION_IGNORE_BATTERY_OPTIMIZATION_SETTINGS);
            battery_intent.setData(Uri.parse("package:"+getPackageName()));
            startActivity(battery_intent);
            startService(battery_intent);
        }
        catch (Exception e){  }

        //read sharedpreference
        user_preference=getSharedPreferences("settings",0);
        preference_editor=user_preference.edit();
        theme=user_preference.getString("theme","Keitaro-green");
        looping_mode=user_preference.getInt("loop mode",0);
        stepmode=user_preference.getInt("step mode",0);
        String current_playing_path=user_preference.getString("current playing path","-1");
        int seekbarprogress=user_preference.getInt("seekbar progress",0);
        standard_speed=user_preference.getFloat("standard speed",4.4f);
        lowest_speed=user_preference.getFloat("lowest speed",0.0f);
        change_weight=user_preference.getFloat("change weight",0.125f);
        last_speed=user_preference.getFloat("last speed",1.0f);
        step_number=user_preference.getInt("step number",0);
        gps_latitude=user_preference.getFloat("gps latitude",91.0f);
        gps_longtitude=user_preference.getFloat("gps longtitude",181.0f);
        network_latitude=user_preference.getFloat("network latitude",91.0f);
        network_longtitude=user_preference.getFloat("network longtitude",181.0f);
        show_background=user_preference.getBoolean("show background",true);
        shake_next_song=user_preference.getBoolean("shake next song",true);
        try {Thread.sleep(2000);}
        catch (Exception e){ }

        //theme setting
        statusbar_setting=new conditionbar_setting(this,MainActivity.this);
        set_theme(theme);
        setContentView(R.layout.activity_main);
        style_popwin=new style_setting_popwin(MainActivity.this);
        style_popwin.init_popwin();
        setting_icon=findViewById(R.id.setting_icon);
        submit_theme_btn=style_popwin.getSubmit_button();
        submit_theme_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) { update_theme(); }
        });
        infomation_popwin=new information_display_popwin(MainActivity.this);
        infomation_popwin.init_popwin();

        //searching music files, listview and adapter, and display the songs
        current_playing_song_ptr=-1;
        int idx;
        music_scan_cls=new local_music_scan();
        song_list=music_scan_cls.scan_music(MainActivity.this);
        for(idx=0;idx<song_list.size();idx++){
            if(song_list.get(idx).getSong_path().equals(current_playing_path)){
                current_playing_song_ptr=idx;
                break;
            }
        }
        Log.i("Pointer info","The current pointer is "+current_playing_song_ptr);
        Log.i("SlidingListActivity","Song list scan finished.");
        search_song_popwin=new search_popwin(MainActivity.this);
        search_song_popwin.init_popwin();
        search_btn=search_song_popwin.getSearch_btn();
        search_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) { search_song(); }
        });
        searching=false;

        // display the list
        display_listview=findViewById(R.id.music_display_listview);
        display_setting_cls=new song_listview_setting();
        adapter=new listview_adapter(MainActivity.this);
        adapter.setAdapter_listItems(MainActivity.this,song_list);
        adapter.setPosition_flag(current_playing_song_ptr);
        Log.i("myinfo","display setting class initialized1.");
        display_listview.setAdapter(adapter);
        Log.i("myinfo","display setting class initialized2.");
        display_setting_cls.refresh_display(adapter,MainActivity.this,song_list);
        listview_last_item=display_listview.getLastVisiblePosition();
        display_listview.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) { Listview_ItemClick(parent,view,position,id); }
        });
        display_listview.setOnScrollListener(new AbsListView.OnScrollListener(){
            @Override
            public void onScrollStateChanged(AbsListView view, int scrollState) {}

            @Override
            public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) { listview_last_item=visibleItemCount; }
        });
        display_setting_cls.update_background(MainActivity.this,display_listview,show_background);
        Log.i("Info","I am running displaying songlist.");

        // initialize the playzone operator
        playzoneOperation=new playzone_operation(this,this);
        playzoneOperation.change_speedtext((float) last_speed);
        playzone_seekbar=findViewById(R.id.playzone_seekbar);
        playzone_seekbar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) { seekbar_changed(progress); }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
                seekbar_start_dragging();
            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) { seekbar_end_dragging(); }
        });
        if(current_playing_song_ptr>=0){
            playzone_seekbar.setMax(song_list.get(current_playing_song_ptr).getSong_duration());
            seekbar_changed(seekbarprogress);
            playzone_seekbar.setProgress(seekbarprogress);
            playzoneOperation.update_playzone_information(song_list.get(current_playing_song_ptr),true);
        }
        speed_text=findViewById(R.id.playzone_seekbar_speed);
        speed_text.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) { nostep_setspeed(); }
        });
        stepmode_icon=findViewById(R.id.stepmode_icon);
        stepmode_icon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) { update_stepmode_icon(); }
        });
        playmode_icon=findViewById(R.id.playmode_icon);
        playmode_icon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) { update_playmode_icon(); }
        });
        playzoneOperation.setstepmode_icon(stepmode);
        playzoneOperation.setplaymode_icon(looping_mode);

        //initialize the player
        dragging_seekbar=false;
        playing_flag=0;
        playmode_popwin=new playmode_setting_popwin(MainActivity.this);
        playmode_popwin.init_popwin();
        submit_playmode_btn=playmode_popwin.getSubmit_button();
        submit_playmode_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) { update_playmode_popwin(); }
        });
        play_randnum=new Random();
        music_player=new MediaPlayer();
        music_player.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
            @Override
            public void onCompletion(MediaPlayer mp) { player_complete(); }
        });
        musicPlayTool=new music_play_tool();
        front_button=findViewById(R.id.playzone_fronticon);
        playing_button=findViewById(R.id.playzone_play_pause_icon);
        next_button=findViewById(R.id.playzone_nexticon);
        album_img_circle=findViewById(R.id.playzone_album_img);
        if(current_playing_song_ptr>=0){
            Log.i("PrefenceInfo","SeekbarProgress: "+seekbarprogress);
            try {
                music_player.reset();
                music_player.setDataSource(current_playing_path);
                music_player.prepare();
                music_player.start();
                music_player.pause();
            }
            catch (IOException e){ }
            musicPlayTool.setPlayerPosition(music_player,seekbarprogress);
            music_player.pause();
        }
        shake_sensor_component=new shakeshake_component(MainActivity.this);
        shake_sensor_component.init_sensor(shake_play_next_song);
        shake_sensor_component.setVibrate_ok(shake_next_song);
        headphonePlug=new headphone_plug(this, headphone_plug_handler);
        headphonePlug.init();
        headphoneOperation=new headphone_operation(this);
        headphoneOperation.setOnHeadsetListener(new headphone_operation.onHeadsetListener(){
            @Override
            public void playOrPause() { play_pause(); }

            @Override
            public void playNext() { play_next_song(); }

            @Override
            public void playPrevious() { play_front_song(); }
        });

        //initialize the notification zone
        notification=new player_notification(this,this);
        notification.initview();
        if(current_playing_song_ptr>=0){ notification.update_current_song_information(song_list.get(current_playing_song_ptr)); }
        notification_broadcastCoper=new broadcast_coper();
        notification_broadcastCoper.setOnnotificationListener(new broadcast_coper.onnotificationListener() {
            @Override
            public void playOrPause() { play_pause(); }

            @Override
            public void playNext() { play_next_song(); }

            @Override
            public void playPrevious() { play_front_song(); }
        });
        //notification_broadcastCoper.init();

        //bind the listeners
        front_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) { play_front_song(); }
        });
        next_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) { play_next_song(); }
        });
        playing_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) { play_pause(); }
        });
        album_img_circle.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) { play_pause(); }
        });

        //initialize the speed detectors
        remind_flag=false;
        gps_open_remind_flag=false;
        net_open_remind_flag=false;
        sensor_component=new step_sensor_component(MainActivity.this);
        has_sensor=sensor_component.init_sensor();
        sensor_broadcast=new sensor_broadcast_coper(MainActivity.this);
        sensor_broadcast.start(new sensor_broadcast_coper.screenstatuslistener() {
            @Override
            public void onScreenon() {
                sensor_component.reregister_sensor();
                shake_sensor_component.setScreen_off(false);
            }
            @Override
            public void onScreenoff() {
                sensor_component.reregister_sensor();
                shake_sensor_component.setScreen_off(true);
            }
            @Override
            public void onUserPresent() {
                sensor_component.reregister_sensor();
                shake_sensor_component.setScreen_off(false);
            }
        });
        GPS_component=new step_GPS_component(MainActivity.this);
        GPS_opened=GPS_component.init_GPS();
        NETWORK_component=new step_NETWORK_component(MainActivity.this);
        NETWORK_opened=NETWORK_component.init_NETWORK();
        stepmode_popwin=new stepmode_setting_popwin(MainActivity.this);
        stepmode_popwin.init_popwin();
        submit_stepmode_btn=stepmode_popwin.getSubmit_button();
        submit_stepmode_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) { update_stepmode_popwin(); }
        });

        //initialize the accelarator sensor

        preference_popwin=new preference_setting_popwin(MainActivity.this);
        preference_popwin.init_popwin();
        submit_preference_btn=preference_popwin.get_submit_btn();
        submit_preference_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) { set_preference(); }
        });

        // power issues
        if(ActivityCompat.checkSelfPermission(this,Manifest.permission.WAKE_LOCK)==PackageManager.PERMISSION_GRANTED){
            pm= (PowerManager) getSystemService(Context.POWER_SERVICE);
            lock=pm.newWakeLock(PowerManager.PARTIAL_WAKE_LOCK,"My tag");
            lock.acquire();
            power_ok=true;
        }
        else{ power_ok=false; }

        //initialize the menus
        setting_menu=new popmenu_settings(MainActivity.this);
        setting_icon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) { setting_menu.showmenu(v,style_popwin,stepmode_popwin,playmode_popwin,theme,stepmode,
                    new float[] {lowest_speed,standard_speed,change_weight},looping_mode,preference_popwin,
                    new boolean[] {show_background,shake_next_song}); }
        });
        discovery_menu=new popmenu_discovery(MainActivity.this);
        discovery_icon=findViewById(R.id.refresh_searching_icon);
        discovery_icon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(searching){
                    discovery_icon.setImageResource(R.mipmap.refresh_search_icon2);
                    adapter.setPosition_flag(current_playing_song_ptr);
                    playzoneOperation.update_maintitle(song_list.get(current_playing_song_ptr).getSong_name()+"〔"+
                            song_list.get(current_playing_song_ptr).getSong_artist()+"〕");
                    display_setting_cls.refresh_display(adapter,MainActivity.this,song_list);
                    searching=false;
                }
                else{ discovery_menu.showmenu(v,search_song_popwin,rescan_handler,infomation_popwin, info_popwin_info,datetime_info); }}
        });
        info_popwin_info="";
        datetime_info="";
        refresh_info_popwin_thread=new Thread(new renew_information_progress());
        refresh_info_popwin_thread.start();
    }


    @Override
    protected void onDestroy(){
        music_player.release();
        shake_sensor_component.unregister_sensor();
        sensor_component.unregister_sensor();
        sensor_broadcast.stop();
        GPS_component.destroy_GPS();
        notification.cancel_nm();
        //notification_broadcastCoper.destroy();
        headphonePlug.destroy();
        headphoneOperation.unregisterHeadsetReceiver();
        if(power_ok){ lock.release(); }
        try{ stopService(battery_intent); }
        catch (Exception e){}
        super.onDestroy();
    }

    private void Listview_ItemClick(AdapterView<?> parent, View v, int position, long id){
        //simple play
        if(searching){
            music_information new_song_information= search_cache_song_list.get(position);
            discovery_icon.setImageResource(R.mipmap.refresh_search_icon2);
            if(position==cache_playing_song_ptr){ }//pass, do nothing
            else{
                current_playing_song_ptr=possible_position[position];

                //play music
                play_target_song(current_playing_song_ptr);
            }
            searching=false;

            //update listview
            adapter.setPosition_flag(current_playing_song_ptr);
            display_setting_cls.refresh_display(adapter, MainActivity.this, song_list);

            // update playzone
            playzoneOperation.update_playzone_information(new_song_information, false);
        }
        else {
            music_information new_song_information = song_list.get(position);
            if (position == current_playing_song_ptr) { }//pass, do nothing
            else { //change a new song
                current_playing_song_ptr = position;

                //update listview
                adapter.setPosition_flag(current_playing_song_ptr);
                display_setting_cls.refresh_display(adapter, MainActivity.this, song_list);

                // update playzone
                playzoneOperation.update_playzone_information(new_song_information, false);

                //play music
                play_target_song(current_playing_song_ptr);
            }
        }
    }

    private void play_front_song(){
        if (looping_mode<3) {
            if (current_playing_song_ptr == 0) { play_target_song(song_list.size() - 1); }
            else { play_target_song(current_playing_song_ptr - 1); }
        }
        else{ play_target_song(play_randnum.nextInt(song_list.size())); }
    }

    private void play_next_song() {
        if (looping_mode<3) {
            if (current_playing_song_ptr == song_list.size() - 1) { play_target_song(0); }
            else { play_target_song(current_playing_song_ptr + 1); }
        }
        else { play_target_song(play_randnum.nextInt(song_list.size())); }
    }

    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    private void play_pause(){
        // change the condition of the player
        playzoneOperation.change_play_pause_icon(music_player);
        playzoneOperation.alter_album_img_anim(playing_flag);
        if(playing_flag==1){ notification.setPlaying(false); }
        else{ notification.setPlaying(true); }
        playing_flag=1-playing_flag;
        musicPlayTool.play_pause(music_player);

        //seekbar thread
        seekbar_thread=new Thread(new seekbar_progress());
        seekbar_thread.start();

        //sensor/GPS component thread
        speed_thread=new Thread(new component_progress());
        speed_thread.start();
    }

    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    private void player_pause(){
        if(music_player.isPlaying()){
            playzoneOperation.change_play_pause_icon(music_player);
            playzoneOperation.alter_album_img_anim(playing_flag);
            notification.setPlaying(false);
            playing_flag=1-playing_flag;
            musicPlayTool.play_pause(music_player);
        }
    }

    private void play_target_song(int target_position){
        // get the information
        current_playing_song_ptr=target_position;
        music_information new_song_information = song_list.get(current_playing_song_ptr);

        if(searching){
            boolean flag=false;
            int idx;
            for(idx=0;idx<song_list.size();idx++){
                if(current_playing_song_ptr==possible_position[idx]){
                    flag=true;
                    cache_playing_song_ptr=idx;
                    break;
                }
            }
            if(flag) {
                adapter.setPosition_flag(cache_playing_song_ptr);
                display_setting_cls.refresh_display(adapter, MainActivity.this, search_cache_song_list);
                display_setting_cls.auto_update_listview(display_listview, cache_playing_song_ptr, search_cache_song_list,
                        display_listview.getLastVisiblePosition());
            }

            //update playzone
            String main_title_string = playzoneOperation.get_maintitle();
            playzoneOperation.update_playzone_information(new_song_information,false);
            playzoneOperation.update_maintitle(main_title_string);

            //update notification
            notification.update_current_song_information(new_song_information);
        }
        else{
            //update listview
            adapter.setPosition_flag(current_playing_song_ptr);
            display_setting_cls.refresh_display(adapter,MainActivity.this,song_list);
            display_setting_cls.auto_update_listview(display_listview,current_playing_song_ptr,song_list,listview_last_item);

            // update playzone
            playzoneOperation.update_playzone_information(new_song_information,false);

            //update notification zone
            notification.update_current_song_information(new_song_information);
        }

        //play music
        musicPlayTool.play_song(music_player,new_song_information.getSong_path());

        //write to prefernce
        preference_editor.putString("current playing path",new_song_information.getSong_path());
        preference_editor.commit();

        //seekbar thread
        seekbar_thread=new Thread(new seekbar_progress());
        seekbar_thread.start();
        playing_flag=1;

        //sensor/GPS component thread
        speed_thread=new Thread(new component_progress());
        speed_thread.start();
    }

    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    private void player_complete(){
        switch (looping_mode){
            case 0:{
                if(current_playing_song_ptr!=song_list.size()-1){ play_target_song(current_playing_song_ptr+1); }
                else{
                    playing_flag=0;
                    playzoneOperation.in_order_last_stop();
                    notification.setPlaying(false);
                }
            }
            break;
            case 1:{ play_target_song((current_playing_song_ptr+1)%song_list.size()); }
            break;
            case 2:{ play_target_song(current_playing_song_ptr); }
            break;
            case 3:{ play_target_song(play_randnum.nextInt(song_list.size())); }
        }
    }

    private void seekbar_start_dragging(){ dragging_seekbar=true; }

    private void seekbar_end_dragging(){
        dragging_seekbar=false;
        musicPlayTool.setPlayerPosition(music_player,playzone_seekbar.getProgress());
        preference_editor.putInt("seekbar progress",playzone_seekbar.getProgress());
        preference_editor.commit();
        seekbar_thread=new Thread(new seekbar_progress());
        seekbar_thread.start();
    }

    private void seekbar_changed(int progress){
        preference_editor.putInt("seekbar progress",progress);
        preference_editor.commit();
        playzoneOperation.setseekbar_time(duration2str(progress));
    }

    private String duration2str(int duration){ // change duration time (unit: ms) to time string minute:second
        int minute=duration/60000;
        int second=(duration/1000)%60;
        if(second<10) { return minute+":0"+second; }
        else{ return minute+":"+second; }
    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    private void set_theme(String now_theme){
        switch (theme){
            case "Keitaro-green":{ setTheme(R.style.Keitaro_green); }
            break;
            case "Natsumi-blue":{ setTheme(R.style.Natsumi_blue); }
            break;
            case "Hiro-orange":{ setTheme(R.style.Hiro_orange); }
            break;
            case "Yoichi-purple":{ setTheme(R.style.Yoichi_purple); }
            break;
            case "Hunter-yellow":{ setTheme(R.style.Hunter_yellow); }
            break;
            case "Taiga-red":{ setTheme(R.style.Taiga_red); }
            break;
            default:{ setTheme(R.style.Default_black); }
        }
        statusbar_setting.setConditionBarColor();
    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    private void update_theme(){
        theme=style_popwin.final_get_theme();
        set_theme(theme);
        display_setting_cls.update_display_theme(MainActivity.this,display_listview,(LinearLayout)findViewById(R.id.main_title_section),show_background);
        if(searching){ display_setting_cls.refresh_display(adapter,MainActivity.this,search_cache_song_list); }
        else{display_setting_cls.refresh_display(adapter,MainActivity.this,song_list);}
        try{ playzoneOperation.update_playzone_theme(music_player,song_list.get(current_playing_song_ptr).getSong_album_img_path());}
        catch (Exception e){ playzoneOperation.update_playzone_theme(music_player,"-1");}
        style_popwin.init_popwin();
        submit_theme_btn=style_popwin.getSubmit_button();
        submit_theme_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) { update_theme(); }
        });
        stepmode_popwin.init_popwin();
        submit_stepmode_btn=stepmode_popwin.getSubmit_button();
        submit_stepmode_btn.setOnClickListener(new View.OnClickListener() {
            @RequiresApi(api = Build.VERSION_CODES.M)
            @Override
            public void onClick(View v) { update_stepmode_popwin(); }
        });
        playmode_popwin.init_popwin();
        submit_playmode_btn=playmode_popwin.getSubmit_button();
        submit_playmode_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) { update_playmode_popwin(); }
        });
        preference_popwin.init_popwin();
        submit_preference_btn=preference_popwin.get_submit_btn();
        submit_preference_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) { set_preference(); }
        });
        search_song_popwin.init_popwin();
        search_btn=search_song_popwin.getSearch_btn();
        search_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) { search_song(); }
        });
        infomation_popwin.init_popwin();
        notification.updateTheme();
        //write to preference
        preference_editor.putString("theme",theme);
        preference_editor.commit();
    }

    @RequiresApi(api = Build.VERSION_CODES.M)
    private void update_stepmode_popwin(){
        float[] set_result=stepmode_popwin.final_get_speeds();
        float cache_lowest_speed,cache_standard_speed,cache_changing_weight;
        cache_lowest_speed=set_result[0];
        cache_standard_speed=set_result[1];
        cache_changing_weight=set_result[2];
        Toast remind_toast;
        // validate data
            // changing weight
        if(cache_changing_weight<0||cache_changing_weight>1){
            if(cache_changing_weight<0){
                cache_changing_weight=0;
                remind_toast=Toast.makeText(MainActivity.this,"变化因数过小,已调整为0",Toast.LENGTH_SHORT);
            }
            else{
                cache_changing_weight=1;
                remind_toast=Toast.makeText(MainActivity.this,"变化因数过大,已调整为1",Toast.LENGTH_SHORT);
            }
            remind_toast.setGravity(Gravity.BOTTOM,0,200);
            remind_toast.show();
        }
        change_weight=cache_changing_weight;
            //lowest speed and standard speed
        if(cache_lowest_speed<0){
            cache_lowest_speed=0;
            remind_toast=Toast.makeText(MainActivity.this,"1x速度非负,已调整为0",Toast.LENGTH_SHORT);
            remind_toast.setGravity(Gravity.BOTTOM,0,200);
            remind_toast.show();
        }
        if(cache_standard_speed<0){
            cache_standard_speed=0;
            remind_toast=Toast.makeText(MainActivity.this,"1.5x速度非负,已调整为0",Toast.LENGTH_SHORT);
            remind_toast.setGravity(Gravity.BOTTOM,0,200);
            remind_toast.show();
        }
        if(cache_lowest_speed>cache_standard_speed){
            float swap_cache=cache_lowest_speed;
            cache_lowest_speed=cache_standard_speed;
            cache_standard_speed=swap_cache;
            remind_toast=Toast.makeText(MainActivity.this,"1x速度与1.5x速度大小反向,已对调",Toast.LENGTH_SHORT);
            remind_toast.setGravity(Gravity.BOTTOM,0,200);
            remind_toast.show();
        }
        lowest_speed=cache_lowest_speed;
        standard_speed=cache_standard_speed;
        stepmode=stepmode_popwin.final_get_playmode();
        update_stepmode_core();
    }

    @RequiresApi(api = Build.VERSION_CODES.M)
    private void update_stepmode_icon(){
        stepmode=(stepmode+1)%4;
        update_stepmode_core();
    }

    @RequiresApi(api = Build.VERSION_CODES.M)
    private void update_stepmode_core(){
        remind_flag=false;
        gps_open_remind_flag=false;
        net_open_remind_flag=false;
        Toast stepmode_toast;
        switch (stepmode){
            case 0:{ stepmode_toast=Toast.makeText(MainActivity.this,"不测速",Toast.LENGTH_SHORT); }
            break;
            case 1:{
                ;
                if(checkSelfPermission(Manifest.permission.ACTIVITY_RECOGNITION)!=PackageManager.PERMISSION_GRANTED){
                    ActivityCompat.requestPermissions(this,new String[]{Manifest.permission.ACTIVITY_RECOGNITION},1);
                }
                if(checkSelfPermission(Manifest.permission.ACTIVITY_RECOGNITION)!=PackageManager.PERMISSION_GRANTED){
                    Looper.prepare();
                    Toast remind_toast=Toast.makeText(MainActivity.this,"您未开启传感器需要的权限",Toast.LENGTH_SHORT);
                    remind_toast.setGravity(Gravity.BOTTOM,0,200);
                    remind_toast.show();
                    Looper.loop();
                }
                stepmode_toast=Toast.makeText(MainActivity.this,"计步采样",Toast.LENGTH_SHORT); }
            break;
            case 2:{
                if(checkSelfPermission(Manifest.permission.ACCESS_COARSE_LOCATION)!=PackageManager.PERMISSION_GRANTED){
                    ActivityCompat.requestPermissions(this,new String[]{Manifest.permission.ACCESS_COARSE_LOCATION},1);
                }
                if(checkSelfPermission(Manifest.permission.ACCESS_FINE_LOCATION)!=PackageManager.PERMISSION_GRANTED){
                    ActivityCompat.requestPermissions(this,new String[]{Manifest.permission.ACCESS_FINE_LOCATION},1);
                }// ask for GPS permission
                if(checkSelfPermission(Manifest.permission.ACCESS_COARSE_LOCATION)!=PackageManager.PERMISSION_GRANTED||
                        checkSelfPermission(Manifest.permission.ACCESS_FINE_LOCATION)!=PackageManager.PERMISSION_GRANTED){
                    Looper.prepare();
                    Toast remind_toast=Toast.makeText(MainActivity.this,"您未开启GPS权限",Toast.LENGTH_SHORT);
                    remind_toast.setGravity(Gravity.BOTTOM,0,200);
                    remind_toast.show();
                    Looper.loop();
                }
                stepmode_toast=Toast.makeText(MainActivity.this,"GPS测速",Toast.LENGTH_SHORT); }
            break;
            default:{
                if(checkSelfPermission(Manifest.permission.ACCESS_FINE_LOCATION)!=PackageManager.PERMISSION_GRANTED){
                    ActivityCompat.requestPermissions(this,new String[]{Manifest.permission.ACCESS_COARSE_LOCATION},1);
                }
                if(checkSelfPermission(Manifest.permission.INTERNET)!=PackageManager.PERMISSION_GRANTED){
                    ActivityCompat.requestPermissions(this,new String[]{Manifest.permission.INTERNET},1);
                }
                if(checkSelfPermission(Manifest.permission.ACCESS_COARSE_LOCATION)!=PackageManager.PERMISSION_GRANTED||
                        checkSelfPermission(Manifest.permission.INTERNET)!=PackageManager.PERMISSION_GRANTED){
                    Looper.prepare();
                    Toast remind_toast=Toast.makeText(MainActivity.this,"您未开启网络权限",Toast.LENGTH_SHORT);
                    remind_toast.setGravity(Gravity.BOTTOM,0,200);
                    remind_toast.show();
                    Looper.loop();
                }
                stepmode_toast=Toast.makeText(MainActivity.this,"网络测速",Toast.LENGTH_SHORT);
            }
            break;
        }
        stepmode_toast.setGravity(Gravity.BOTTOM,0,200);
        stepmode_toast.show();
        playzoneOperation.setstepmode_icon(stepmode);
        //write to preference
        preference_editor.putInt("step mode",stepmode);
        preference_editor.putFloat("standard speed",standard_speed);
        preference_editor.putFloat("lowest speed",lowest_speed);
        preference_editor.putFloat("change weight",change_weight);
        preference_editor.putFloat("last speed",(float)last_speed);
        preference_editor.commit();
    }

    private void update_playmode_popwin(){
        looping_mode=playmode_popwin.final_get_playmode();
        update_playmode_core();
    }

    private void update_playmode_icon(){
        looping_mode=(looping_mode+1)%4;
        update_playmode_core();
    }

    private void update_playmode_core(){
        Toast playmode_toast;
        switch (looping_mode){
            case 0:{ playmode_toast=Toast.makeText(MainActivity.this,"顺序播放",Toast.LENGTH_SHORT); }
            break;
            case 1:{ playmode_toast=Toast.makeText(MainActivity.this,"列表循环",Toast.LENGTH_SHORT); }
            break;
            case 2:{ playmode_toast=Toast.makeText(MainActivity.this,"单曲循环",Toast.LENGTH_SHORT); }
            break;
            default:{ playmode_toast=Toast.makeText(MainActivity.this,"随机播放",Toast.LENGTH_SHORT); }
            break;
        }
        playmode_toast.setGravity(Gravity.BOTTOM,0,200);
        playmode_toast.show();
        playzoneOperation.setplaymode_icon(looping_mode);
        //write to preference
        preference_editor.putInt("loop mode",looping_mode);
        preference_editor.commit();
    }

    private void rescan_music(){
        rescan_thread=new Thread(new rescan_music_progress());
        rescan_thread.start();
    }

    private void nostep_setspeed(){
        if(stepmode==0){
            if(last_speed==1.0f){ last_speed=1.25f; }
            else{
                if(last_speed==1.25f){ last_speed=1.5f; }
                else{
                    if(last_speed==1.5f){ last_speed=1.75f; }
                    else{
                        if(last_speed==1.75f){ last_speed=2.0f; }
                        else{
                            if(last_speed==2.0f){ last_speed=2.5f; }
                            else{
                                if(last_speed==2.5f){ last_speed=3.0f; }
                                else{
                                    if(last_speed==3.0f){last_speed=0.5f;}
                                    else{
                                        if(last_speed==0.5f){ last_speed=0.75f; }
                                        else{ last_speed=1.0f; }
                                    }
                                }
                            }
                        }
                    }
                }
            }
            Message msg;
            msg=new Message();
            msg.what=0;
            set_player_speed.sendMessage(msg);
            msg=new Message();
            msg.what=0;
            speed_text_handler.sendMessage(msg);
            preference_editor.putFloat("last speed",(float)last_speed);
            preference_editor.commit();
        }
        else{
            Toast remind_toast;
            switch (stepmode){
                case 1:{ remind_toast=Toast.makeText(MainActivity.this,"您处于计步采样变速状态,不能手动改变速度",Toast.LENGTH_SHORT); }
                break;
                case 2:{ remind_toast=Toast.makeText(MainActivity.this,"您处于GPS测速变速状态,不能手动改变速度",Toast.LENGTH_SHORT); }
                break;
                default:{ remind_toast=Toast.makeText(MainActivity.this,"您处于网络测速变速状态,不能手动改变速度",Toast.LENGTH_SHORT); }
                break;
            }
            remind_toast.setGravity(Gravity.BOTTOM,0,200);
            remind_toast.show();
        }
    }

    private void search_song(){
        Toast info_toast;
        search_text=search_song_popwin.get_final_search_text();
        if(search_text.equals("")){
            info_toast=Toast.makeText(MainActivity.this,"您未搜索任何内容",Toast.LENGTH_SHORT);
            info_toast.setGravity(Gravity.BOTTOM,0,200);
            info_toast.show();
            return;
        }
        // search songs in temporary song
        possible_position=music_scan_cls.search_songs(search_text,song_list);
        int idx,max_song_number=0;
        for(idx=0;idx<song_list.size();idx++){
            if(possible_position[idx]!=-1){ max_song_number++; }
        }
        if(max_song_number>0) {
            searching=true;
            playzoneOperation.update_maintitle("搜索: "+search_text);
            discovery_icon.setImageResource(R.mipmap.exit_icon);
            cache_playing_song_ptr=-1;
            search_cache_song_list = new ArrayList<>();
            for (idx = 0; idx < max_song_number; idx++) {
                if (song_list.get(current_playing_song_ptr).getSong_path().equals(song_list.get(possible_position[idx]).getSong_path())) {
                    cache_playing_song_ptr = idx;
                }
                search_cache_song_list.add(song_list.get(possible_position[idx]));
            }
            info_toast=Toast.makeText(MainActivity.this,"搜索完成",Toast.LENGTH_SHORT);
            info_toast.setGravity(Gravity.BOTTOM,0,200);
            info_toast.show();
            //display on the screen
            adapter.setPosition_flag(cache_playing_song_ptr);
            display_setting_cls.refresh_display(adapter,MainActivity.this,search_cache_song_list);
        }
        else{
            info_toast=Toast.makeText(MainActivity.this,"搜索完成",Toast.LENGTH_SHORT);
            info_toast.setGravity(Gravity.BOTTOM,0,200);
            info_toast.show();
        }
    }

    private void set_preference(){
        boolean[] preference=preference_popwin.final_get_prefence();
        show_background=preference[0];
        shake_next_song=preference[1];
        shake_sensor_component.setVibrate_ok(shake_next_song);
        display_setting_cls.update_background(MainActivity.this,display_listview,show_background);
        preference_editor.putBoolean("show background",show_background);
        preference_editor.putBoolean("shake next song",shake_next_song);
        preference_editor.commit();
    }

}