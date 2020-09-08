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

public class style_setting_popwin extends Activity{

    private float screen_width,screen_height;
    private PopupWindow style_popupWindow;
    private popwin_anim popupWindow_anim;
    private View popupwin;
    private Context context;
    private RadioButton keitaro_green,natsumi_blue,hiro_orange,yoichi_purple,hunter_yellow,taiga_red,default_black;
    private Button submit_button;
    private ImageView exit_icon;
    private String theme;

    public style_setting_popwin(Context context){
        this.context=context;
        screen_height=context.getResources().getDisplayMetrics().heightPixels;
        screen_width=context.getResources().getDisplayMetrics().widthPixels;
    }

    public void init_popwin(){
        LayoutInflater layoutInflater=LayoutInflater.from(context);
        popupwin=layoutInflater.inflate(R.layout.theme_setting_popwin_layout,null);
        popupWindow_anim=new popwin_anim();
        popupWindow_anim.setPopupAnimation(popupwin);
        keitaro_green=popupwin.findViewById(R.id.radiobtn_select_keitaro_green);
        keitaro_green.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) { RadiobtnClick(v); }
        });
        natsumi_blue=popupwin.findViewById(R.id.radiobtn_select_natsumi_blue);
        natsumi_blue.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) { RadiobtnClick(v); }
        });
        hiro_orange=popupwin.findViewById(R.id.radiobtn_select_hiro_orange);
        hiro_orange.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) { RadiobtnClick(v); }
        });
        yoichi_purple=popupwin.findViewById(R.id.radiobtn_select_yoichi_purple);
        yoichi_purple.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) { RadiobtnClick(v); }
        });
        hunter_yellow=popupwin.findViewById(R.id.radiobtn_select_hunter_yellow);
        hunter_yellow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) { RadiobtnClick(v); }
        });
        taiga_red=popupwin.findViewById(R.id.radiobtn_select_taiga_red);
        taiga_red.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) { RadiobtnClick(v); }
        });
        default_black=popupwin.findViewById(R.id.radiobtn_select_default_black);
        default_black.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) { RadiobtnClick(v); }
        });
        exit_icon=popupwin.findViewById(R.id.theme_setting_exit_icon);
        exit_icon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) { if(style_popupWindow.isShowing()){ style_popupWindow.dismiss(); } }
        });
        submit_button=popupwin.findViewById(R.id.submit_theme_btn);
    }

    public void open_theme_setting_popwin(View v,String now_theme){
        style_popupWindow=new PopupWindow(popupwin,ViewGroup.LayoutParams.WRAP_CONTENT,ViewGroup.LayoutParams.WRAP_CONTENT);
        style_popupWindow.setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));
        style_popupWindow.setFocusable(true);
        style_popupWindow.setOutsideTouchable(true);
        style_popupWindow.showAsDropDown(v,(int)(0.05*screen_width),(int)(0.15*screen_height));

        //set the corresponding radio btn checked
        switch (now_theme){
            case "Keitaro-green":{ keitaro_green.setChecked(true); }
            break;
            case "Natsumi-blue":{ natsumi_blue.setChecked(true); }
            break;
            case "Hiro-orange":{ hiro_orange.setChecked(true); }
            break;
            case "Yoichi-purple":{ yoichi_purple.setChecked(true); }
            break;
            case "Hunter-yellow":{ hunter_yellow.setChecked(true); }
            break;
            case "Taiga-red":{ taiga_red.setChecked(true); }
            break;
            default:{ default_black.setChecked(true); }
        }
        theme=now_theme;
    }

    public boolean confirm_popwin_showing(){ return style_popupWindow.isShowing(); }

    public void close_popwin(){ style_popupWindow.dismiss(); }

    public Button getSubmit_button(){ return submit_button; }

    private void RadiobtnClick(View v) {
        switch (v.getId()){
            case R.id.radiobtn_select_keitaro_green:{
                keitaro_green.setChecked(true);
                theme="Keitaro-green";
            }
            break;
            case R.id.radiobtn_select_natsumi_blue:{
                natsumi_blue.setChecked(true);
                theme="Natsumi-blue";
            }
            break;
            case R.id.radiobtn_select_hiro_orange:{
                hiro_orange.setChecked(true);
                theme="Hiro-orange";
            }
            break;
            case R.id.radiobtn_select_yoichi_purple:{
                yoichi_purple.setChecked(true);
                theme="Yoichi-purple";
            }
            break;
            case R.id.radiobtn_select_hunter_yellow:{
                hunter_yellow.setChecked(true);
                theme="Hunter-yellow";
            }
            break;
            case R.id.radiobtn_select_taiga_red:{
                taiga_red.setChecked(true);
                theme="Taiga-red";
            }
            break;
            case R.id.radiobtn_select_default_black:{
                default_black.setChecked(true);
                theme="Default-black";
            }
            break;
        }
    }

    public String final_get_theme(){
        style_popupWindow.dismiss();
        return theme;
    }
}
