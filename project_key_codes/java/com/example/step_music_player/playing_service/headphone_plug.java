package com.example.step_music_player.playing_service;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.media.AudioManager;
import android.os.Message;

import com.example.step_music_player.MainActivity;

public class headphone_plug {

    private Context context;
    private android.os.Handler playpause_handler;

    private BroadcastReceiver receiver=new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            if(intent.getAction().equals(AudioManager.ACTION_AUDIO_BECOMING_NOISY)){
                Message msg=new Message();
                msg.what=0;
                playpause_handler.sendMessage(msg);
            }
        }
    };

    public headphone_plug(MainActivity context, android.os.Handler headphone_plug_handler) {
        this.context=context;
        playpause_handler=headphone_plug_handler;
    }

    public void init(){
        IntentFilter intentFilter=new IntentFilter();
        intentFilter.addAction(AudioManager.ACTION_AUDIO_BECOMING_NOISY);
        context.registerReceiver(receiver,intentFilter);
    }

    public void destroy(){ context.unregisterReceiver(receiver); }

}
