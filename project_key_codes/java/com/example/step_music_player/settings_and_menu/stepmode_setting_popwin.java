package com.example.step_music_player.settings_and_menu;

import android.app.Activity;
import android.content.Context;
import android.graphics.drawable.ColorDrawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.PopupWindow;
import android.widget.RadioButton;

import com.example.step_music_player.R;
import com.example.step_music_player.animations.popwin_anim;

import java.text.DecimalFormat;

public class stepmode_setting_popwin extends Activity {
    private float screen_width,screen_height;
    private PopupWindow stepmode_popupWindow;
    private popwin_anim popupWindow_anim;
    private View popupwin;
    private Context context;
    private RadioButton no_step,sensor_step,gps_step,network_step;
    private Button submit_button,reset_button;
    private ImageView exit_icon;
    private int stepmode;
    private EditText lowest_speed_text,standard_speed_text,changing_weight_text;
    private float lowest_speed,standard_speed,changing_weight;
    private DecimalFormat df;

    public stepmode_setting_popwin(Context context){
        this.context=context;
        screen_height=context.getResources().getDisplayMetrics().heightPixels;
        screen_width=context.getResources().getDisplayMetrics().widthPixels;
        df = new DecimalFormat("#0.0000");
    }

    public void init_popwin(){
        LayoutInflater layoutInflater=LayoutInflater.from(context);
        popupwin=layoutInflater.inflate(R.layout.stepmode_setting_popwin_layout,null);
        popupWindow_anim=new popwin_anim();
        popupWindow_anim.setPopupAnimation(popupwin);
        no_step=popupwin.findViewById(R.id.radiobtn_select_nostep);
        no_step.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) { RadiobtnClick(v); }
        });
        sensor_step=popupwin.findViewById(R.id.radiobtn_select_sensor);
        sensor_step.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) { RadiobtnClick(v); }
        });
        gps_step=popupwin.findViewById(R.id.radiobtn_select_GPS);
        gps_step.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) { RadiobtnClick(v); }
        });
        network_step=popupwin.findViewById(R.id.radiobtn_select_network);
        network_step.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) { RadiobtnClick(v); }
        });
        exit_icon=popupwin.findViewById(R.id.stepmode_setting_exit_icon);
        exit_icon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) { if(stepmode_popupWindow.isShowing()){ stepmode_popupWindow.dismiss(); } }
        });
        submit_button=popupwin.findViewById(R.id.submit_stepmode_btn);
        reset_button=popupwin.findViewById(R.id.reset_stepmode_btn);
        reset_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) { reset_text(); }
        });
        lowest_speed_text=popupwin.findViewById(R.id.lowest_speed_edittext);
        standard_speed_text=popupwin.findViewById(R.id.standard_speed_edittext);
        changing_weight_text=popupwin.findViewById(R.id.changing_weight_edittext);
    }

    public void open_theme_setting_popwin(View v,int now_stepmode, float[] datas){
        stepmode_popupWindow=new PopupWindow(popupwin, ViewGroup.LayoutParams.WRAP_CONTENT,ViewGroup.LayoutParams.WRAP_CONTENT);
        stepmode_popupWindow.setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));
        stepmode_popupWindow.setFocusable(true);
        stepmode_popupWindow.setOutsideTouchable(true);
        stepmode_popupWindow.showAsDropDown(v,(int)(0.05*screen_width),(int)(0.08*screen_height));

        //set the corresponding radio btn checked
        switch (now_stepmode){
            case 0:{ no_step.setChecked(true); }
            break;
            case 1:{ sensor_step.setChecked(true); }
            break;
            case 2:{ gps_step.setChecked(true); }
            break;
            case 3:{ network_step.setChecked(true); }
            break;
        }
        stepmode=now_stepmode;
        lowest_speed=datas[0];
        standard_speed=datas[1];
        changing_weight=datas[2];
        lowest_speed_text.setText(df.format(lowest_speed));
        standard_speed_text.setText(df.format(standard_speed));
        changing_weight_text.setText(df.format(changing_weight));
    }

    public boolean confirm_popwin_showing(){ return stepmode_popupWindow.isShowing(); }

    public void close_popwin(){ stepmode_popupWindow.dismiss(); }

    public Button getSubmit_button(){ return submit_button; }

    private void RadiobtnClick(View v) {
        switch (v.getId()){
            case R.id.radiobtn_select_nostep:{
                no_step.setChecked(true);
                stepmode=0;
            }
            break;
            case R.id.radiobtn_select_sensor:{
                sensor_step.setChecked(true);
                stepmode=1;
            }
            break;
            case R.id.radiobtn_select_GPS:{
                gps_step.setChecked(true);
                stepmode=2;
            }
            break;
            case R.id.radiobtn_select_network:{
                network_step.setChecked(true);
                stepmode=3;
            }
            break;
        }
    }

    public float[] final_get_speeds(){
        return new float[] {Float.parseFloat(lowest_speed_text.getText().toString()),
        Float.parseFloat(standard_speed_text.getText().toString()),Float.parseFloat(changing_weight_text.getText().toString())};
    }

    public int final_get_playmode(){
        stepmode_popupWindow.dismiss();
        return stepmode;
    }

    private void reset_text(){
        stepmode=0;
        no_step.setChecked(true);
        lowest_speed=0.0f;
        standard_speed=4.4f;
        changing_weight=0.125f;
        lowest_speed_text.setText(df.format(lowest_speed));
        standard_speed_text.setText(df.format(standard_speed));
        changing_weight_text.setText(df.format(changing_weight));
    }
}
