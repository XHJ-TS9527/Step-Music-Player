package com.example.step_music_player.settings_and_menu;

import android.app.Activity;
import android.content.Context;
import android.graphics.drawable.ColorDrawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.PopupWindow;
import android.widget.RadioButton;

import com.example.step_music_player.R;
import com.example.step_music_player.animations.popwin_anim;

public class playmode_setting_popwin extends Activity {
    private float screen_width,screen_height;
    private PopupWindow playmode_popupWindow;
    private popwin_anim popupWindow_anim;
    private View popupwin;
    private Context context;
    private RadioButton order,loop_list,loop_single,random_play;
    private Button submit_button;
    private ImageView exit_icon;
    private int playmode;

    public playmode_setting_popwin(Context context){
        this.context=context;
        screen_height=context.getResources().getDisplayMetrics().heightPixels;
        screen_width=context.getResources().getDisplayMetrics().widthPixels;
    }

    public void init_popwin(){
        LayoutInflater layoutInflater=LayoutInflater.from(context);
        popupwin=layoutInflater.inflate(R.layout.playmode_setting_popwin_layout,null);
        popupWindow_anim=new popwin_anim();
        popupWindow_anim.setPopupAnimation(popupwin);
        order=popupwin.findViewById(R.id.radiobtn_select_order);
        order.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) { RadiobtnClick(v); }
        });
        loop_list=popupwin.findViewById(R.id.radiobtn_select_order_loop);
        loop_list.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) { RadiobtnClick(v); }
        });
        loop_single=popupwin.findViewById(R.id.radiobtn_select_order_single);
        loop_single.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) { RadiobtnClick(v); }
        });
        random_play=popupwin.findViewById(R.id.radiobtn_select_random);
        random_play.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) { RadiobtnClick(v); }
        });
        exit_icon=popupwin.findViewById(R.id.playmode_setting_exit_icon);
        exit_icon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) { if(playmode_popupWindow.isShowing()){ playmode_popupWindow.dismiss(); } }
        });
        submit_button=popupwin.findViewById(R.id.submit_playmode_btn);
    }

    public void open_theme_setting_popwin(View v,int now_playmode){
        playmode_popupWindow=new PopupWindow(popupwin, ViewGroup.LayoutParams.WRAP_CONTENT,ViewGroup.LayoutParams.WRAP_CONTENT);
        playmode_popupWindow.setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));
        playmode_popupWindow.setFocusable(true);
        playmode_popupWindow.setOutsideTouchable(true);
        playmode_popupWindow.showAsDropDown(v,(int)(0.05*screen_width),(int)(0.15*screen_height));

        //set the corresponding radio btn checked
        switch (now_playmode){
            case 0:{ order.setChecked(true); }
            break;
            case 1:{ loop_list.setChecked(true); }
            break;
            case 2:{ loop_single.setChecked(true); }
            break;
            case 3:{ random_play.setChecked(true); }
            break;
        }
        playmode=now_playmode;
    }

    public boolean confirm_popwin_showing(){ return playmode_popupWindow.isShowing(); }

    public void close_popwin(){ playmode_popupWindow.dismiss(); }

    public Button getSubmit_button(){ return submit_button; }

    private void RadiobtnClick(View v) {
        switch (v.getId()){
            case R.id.radiobtn_select_order:{
                order.setChecked(true);
                playmode=0;
            }
            break;
            case R.id.radiobtn_select_order_loop:{
                loop_list.setChecked(true);
                playmode=1;
            }
            break;
            case R.id.radiobtn_select_order_single:{
                loop_single.setChecked(true);
                playmode=2;
            }
            break;
            case R.id.radiobtn_select_random:{
                random_play.setChecked(true);
                playmode=3;
            }
            break;
        }
    }

    public int final_get_playmode(){
        playmode_popupWindow.dismiss();
        return playmode;
    }

}
