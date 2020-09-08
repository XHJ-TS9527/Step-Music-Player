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

import com.example.step_music_player.R;
import com.example.step_music_player.UI.switch_button;
import com.example.step_music_player.animations.popwin_anim;

public class preference_setting_popwin extends Activity {
    private float screen_width,screen_height;
    private PopupWindow preference_popupWindow;
    private popwin_anim popupWindow_anim;
    private View popupwin;
    private Context context;
    private ImageView exit_icon;
    private Button submit_button,clear_btn;
    private switch_button bg_switch_btn,shake_switch_btn;

    public preference_setting_popwin(Context context){
        this.context=context;
        screen_height=context.getResources().getDisplayMetrics().heightPixels;
        screen_width=context.getResources().getDisplayMetrics().widthPixels;
    }

    public void init_popwin(){
        LayoutInflater layoutInflater=LayoutInflater.from(context);
        popupwin=layoutInflater.inflate(R.layout.preference_setting_popwin_layout,null);
        popupWindow_anim=new popwin_anim();
        popupWindow_anim.setPopupAnimation(popupwin);
        exit_icon=popupwin.findViewById(R.id.preference_window_exit_icon);
        exit_icon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) { if(preference_popupWindow.isShowing()){ preference_popupWindow.dismiss(); } }
        });
        submit_button=popupwin.findViewById(R.id.submit_preference_btn);
        clear_btn=popupwin.findViewById(R.id.reset_preference_btn);
        bg_switch_btn=popupwin.findViewById(R.id.select_background_btn);
        shake_switch_btn=popupwin.findViewById(R.id.select_shake_btn);
        clear_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                bg_switch_btn.changeOpenState(true);
                shake_switch_btn.changeOpenState(true);
            }
        });
    }

    public Button get_submit_btn(){ return submit_button; }

    public void open_theme_setting_popwin(View v,boolean[] preference_setting){
        preference_popupWindow=new PopupWindow(popupwin, ViewGroup.LayoutParams.WRAP_CONTENT,ViewGroup.LayoutParams.WRAP_CONTENT);
        preference_popupWindow.setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));
        preference_popupWindow.setFocusable(true);
        preference_popupWindow.setOutsideTouchable(true);
        preference_popupWindow.showAsDropDown(v,(int)(0.05*screen_width),(int)(0.15*screen_height));

        //set the corresponding radio btn checked
        bg_switch_btn.changeOpenState(preference_setting[0]);
        shake_switch_btn.changeOpenState(preference_setting[1]);
    }

    public boolean confirm_popwin_showing(){ return preference_popupWindow.isShowing(); }

    public void close_popwin(){ preference_popupWindow.dismiss(); }

    public boolean[] final_get_prefence(){
        preference_popupWindow.dismiss();
        return new boolean[] {bg_switch_btn.getOpenState(),shake_switch_btn.getOpenState()};
    }
}
