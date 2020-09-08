package com.example.step_music_player.step_sensor;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.PowerManager;;

public class sensor_broadcast_coper {
    private Context context;
    private sceenbroadcastreceiver screen_broadcast_receiver;
    private screenstatuslistener screen_status_listener;

    private class sceenbroadcastreceiver extends BroadcastReceiver{
        private String action=null;
        @Override
        public void onReceive(Context context, Intent intent) {
            action=intent.getAction();
            if(Intent.ACTION_SCREEN_ON.equals(action)){ screen_status_listener.onScreenon(); }
            else if(Intent.ACTION_SCREEN_OFF.equals(action)){ screen_status_listener.onScreenoff(); }
            else if(Intent.ACTION_USER_PRESENT.equals(action)){ screen_status_listener.onUserPresent(); }
        }
    }

    public interface screenstatuslistener{
        public void onScreenon();
        public void onScreenoff();
        public void onUserPresent();
    }

    public sensor_broadcast_coper(Context context){
        this.context=context;
        screen_broadcast_receiver=new sceenbroadcastreceiver();
    }

    public void start(screenstatuslistener screen_status_listener){
        this.screen_status_listener=screen_status_listener;
        registerListener();
        getscreenstate();
    }

    private void getscreenstate(){
        PowerManager pm=(PowerManager) context.getSystemService(Context.POWER_SERVICE);
        if(pm.isScreenOn()){
            if(screen_status_listener!=null){ screen_status_listener.onScreenon(); }
            else{
                if(screen_status_listener!=null){ screen_status_listener.onScreenoff(); }
            }
        }
    }

    private void registerListener(){
        IntentFilter filter=new IntentFilter();
        filter.addAction(Intent.ACTION_SCREEN_ON);
        filter.addAction(Intent.ACTION_SCREEN_OFF);
        filter.addAction(Intent.ACTION_USER_PRESENT);
        context.registerReceiver(screen_broadcast_receiver,filter);
    }

    public void stop(){ context.unregisterReceiver(screen_broadcast_receiver); }

}
