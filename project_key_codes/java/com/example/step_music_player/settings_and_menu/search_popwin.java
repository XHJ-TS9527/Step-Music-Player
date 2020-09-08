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

import com.example.step_music_player.R;
import com.example.step_music_player.animations.popwin_anim;

public class search_popwin extends Activity {
    private float screen_width,screen_height;
    private PopupWindow search_popupWindow;
    private popwin_anim popupWindow_anim;
    private View popupwin;
    private Context context;
    private ImageView exit_icon;
    private Button search_btn,clear_btn;
    private EditText search_text;

    public search_popwin(Context context){
        this.context=context;
        screen_height=context.getResources().getDisplayMetrics().heightPixels;
        screen_width=context.getResources().getDisplayMetrics().widthPixels;
    }

    public void init_popwin(){
        LayoutInflater layoutInflater=LayoutInflater.from(context);
        popupwin=layoutInflater.inflate(R.layout.searching_popwin_layout,null);
        popupWindow_anim=new popwin_anim();
        popupWindow_anim.setPopupAnimation(popupwin);
        exit_icon=popupwin.findViewById(R.id.search_window_exit_icon);
        exit_icon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) { if(search_popupWindow.isShowing()){ search_popupWindow.dismiss(); } }
        });
        search_text=popupwin.findViewById(R.id.search_song_edittext);
        search_btn=popupwin.findViewById(R.id.submit_search_btn);
        clear_btn=popupwin.findViewById(R.id.reset_search_btn);
        clear_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) { search_text.setText(""); }
        });
    }

    public void open_search_window(View v){
        search_popupWindow=new PopupWindow(popupwin, ViewGroup.LayoutParams.WRAP_CONTENT,ViewGroup.LayoutParams.WRAP_CONTENT);
        search_popupWindow.setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));
        search_popupWindow.setFocusable(true);
        search_popupWindow.setOutsideTouchable(true);
        search_popupWindow.showAsDropDown(v,(int)(-0.79*screen_width),(int)(0.1*screen_height));
        search_text.setText("");
    }

    public boolean confirm_popwin_showing(){ return search_popupWindow.isShowing(); }
    public void close_popwin(){ search_popupWindow.dismiss(); }

    public Button getSearch_btn(){ return search_btn; }

    public String get_final_search_text(){
        search_popupWindow.dismiss();
        return search_text.getText().toString();
    }
}
