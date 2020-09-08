package com.example.step_music_player.settings_and_menu;

import android.content.Context;
import android.view.ContextThemeWrapper;
import android.view.MenuItem;
import android.view.View;
import android.widget.PopupMenu;

import androidx.appcompat.app.AppCompatActivity;

import com.example.step_music_player.R;

public class popmenu_settings extends AppCompatActivity {

    private Context context;
    private PopupMenu popupMenu;

    public popmenu_settings(Context context){ this.context=context; }

    public void showmenu(final View v, final style_setting_popwin style_popwin, final stepmode_setting_popwin stepmode_popwin,
                         final playmode_setting_popwin playmode_popwin, final String theme, final int stepmode,
                         final float[] speed_data, final int playmode, final preference_setting_popwin preference_popwin, final boolean[] preferences){
        popupMenu=new PopupMenu(new ContextThemeWrapper(context,R.style.OverflowMenu),v);
        popupMenu.getMenuInflater().inflate(R.menu.setting_menu,popupMenu.getMenu());
        popupMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                select_popwin(v,item,style_popwin,stepmode_popwin,playmode_popwin,theme,stepmode,speed_data,playmode,preference_popwin,
                        preferences);
                return false;
            }
        });
        popupMenu.setOnDismissListener(new PopupMenu.OnDismissListener() {
            @Override
            public void onDismiss(PopupMenu menu) { }
        });
        popupMenu.show();
    }

    private void select_popwin(View v,MenuItem item,style_setting_popwin styple_popwin, stepmode_setting_popwin stepmode_popwin,
                               playmode_setting_popwin playmode_popwin,String theme,int stepmode, float[] speed_data,int playmode,
                               preference_setting_popwin preference_popwin, boolean[] preferences){
        CharSequence title = item.getTitle();
        if ("主题选择".equals(title)) { styple_popwin.open_theme_setting_popwin(v,theme); }
        else if ("步奏设置".equals(title)) { stepmode_popwin.open_theme_setting_popwin(v,stepmode,speed_data);}
        else if ("播放模式".equals(title)) { playmode_popwin.open_theme_setting_popwin(v, playmode); }
        else if ("偏好设置".equals(title)) { preference_popwin.open_theme_setting_popwin(v,preferences); }
        popupMenu.dismiss();
    }

}
