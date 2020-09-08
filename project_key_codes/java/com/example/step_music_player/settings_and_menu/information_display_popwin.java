package com.example.step_music_player.settings_and_menu;

import android.app.Activity;
import android.content.Context;
import android.graphics.drawable.ColorDrawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.PopupWindow;
import android.widget.TextView;

import com.example.step_music_player.R;
import com.example.step_music_player.animations.popwin_anim;

public class information_display_popwin extends Activity {
    private float screen_width,screen_height;
    private PopupWindow information_popupWindow;
    private popwin_anim popupWindow_anim;
    private View popupwin;
    private Context context;
    private ImageView exit_icon;
    private TextView info_textview,datetime_textview;

    public information_display_popwin(Context context){
        this.context=context;
        screen_height=context.getResources().getDisplayMetrics().heightPixels;
        screen_width=context.getResources().getDisplayMetrics().widthPixels;
    }

    public void init_popwin(){
        LayoutInflater layoutInflater=LayoutInflater.from(context);
        popupwin=layoutInflater.inflate(R.layout.information_popwin_layout,null);
        popupWindow_anim=new popwin_anim();
        popupWindow_anim.setPopupAnimation(popupwin);
        info_textview=popupwin.findViewById(R.id.info_detail);
        datetime_textview=popupwin.findViewById(R.id.info_refresh_time);
        exit_icon=popupwin.findViewById(R.id.info_checking_exit_icon);
        exit_icon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) { if(information_popupWindow.isShowing()){ information_popupWindow.dismiss(); } }
        });
    }

    public void open_information_window(View v,String info,String datetime){
        information_popupWindow=new PopupWindow(popupwin, ViewGroup.LayoutParams.WRAP_CONTENT,ViewGroup.LayoutParams.WRAP_CONTENT);
        information_popupWindow.setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));
        information_popupWindow.setFocusable(true);
        information_popupWindow.setOutsideTouchable(true);
        information_popupWindow.showAsDropDown(v,(int)(-0.79*screen_width),(int)(0.05*screen_height));
        info_textview.setText(info);
        datetime_textview.setText(datetime);
    }

    public boolean confirm_popwin_showing(){ return information_popupWindow.isShowing(); }

    public void close_popwin(){ information_popupWindow.dismiss(); }

    public void refresh_info(String new_info,String new_datetime){
        info_textview.setText(new_info);
        datetime_textview.setText(new_datetime);
    }
}
