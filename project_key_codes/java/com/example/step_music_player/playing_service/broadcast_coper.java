package com.example.step_music_player.playing_service;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Handler;
import android.os.Message;


public class broadcast_coper extends BroadcastReceiver{

    private static broadcast_coper.onnotificationListener notificationListener;
    /*
    private Handler song_handler;

    private playpause_receiver Playpause_Receiver;
    private front_receiver Front_Receiver;
    private next_receiver Next_Receiver;

     */

    public broadcast_coper(){
        super();
        notificationListener=null;
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        String broadcast_content=intent.getAction();
        Message msg;
        switch (broadcast_content){
            case "FRONT":{
                msg=new Message();
                msg.what=1;
            }
            break;
            case "NEXT":{
                msg=new Message();
                msg.what=2;
            }
            break;
            case "PLAYPAUSE":{
                msg=new Message();
                msg.what=0;
            }
            break;
            default:{
                msg=new Message();
                msg.what=9999;
            }
        }
        coper.sendMessage(msg);
    }

    private Handler coper=new Handler(){
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            try {
                if (msg.what == 0) { notificationListener.playOrPause(); }
                else if (msg.what == 2) { notificationListener.playNext(); }
                else if (msg.what == 1) { notificationListener.playPrevious(); } }
            catch (Exception e) { e.printStackTrace(); }
        }
    };

    public interface onnotificationListener{
        void playOrPause();
        void playNext();
        void playPrevious();
    }

    public void setOnnotificationListener(broadcast_coper.onnotificationListener newnotificationListener){
        notificationListener = newnotificationListener;
    }


    /*
    private class front_receiver extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            Message msg=new Message();
            msg.what=1;
            song_handler.sendMessage(msg);
        }
    }

    private class next_receiver extends BroadcastReceiver{
        @Override
        public void onReceive(Context context, Intent intent) {
            Message msg=new Message();
            msg.what=2;
            song_handler.sendMessage(msg);
        }
    }

    private class playpause_receiver extends BroadcastReceiver{
        @Override
        public void onReceive(Context context, Intent intent) {
            Message msg=new Message();
            msg.what=0;
            song_handler.sendMessage(msg);
        }
    }

    public broadcast_coper(Context context,Handler player_handler){
        song_handler=player_handler;
        this.context=context;
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

     */
    /*
    public void init(){

        Playpause_Receiver=new playpause_receiver();
        Front_Receiver=new front_receiver();
        Next_Receiver=new next_receiver();


        IntentFilter playpause_filter=new IntentFilter();
        playpause_filter.addAction("PLAYPAUSE");
        IntentFilter front_filter=new IntentFilter();
        front_filter.addAction("FRONT");
        IntentFilter next_filter=new IntentFilter();
        next_filter.addAction("NEXT");
        context.registerReceiver(Playpause_Receiver,playpause_filter);
        context.registerReceiver(Front_Receiver,front_filter);
        context.registerReceiver(Next_Receiver,next_filter);
    }

    public void destroy(){
        context.unregisterReceiver(Playpause_Receiver);
        context.unregisterReceiver(Front_Receiver);
        context.unregisterReceiver(Next_Receiver);
    }

         */

}
