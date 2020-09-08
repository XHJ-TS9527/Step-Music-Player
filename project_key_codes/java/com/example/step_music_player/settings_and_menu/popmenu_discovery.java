package com.example.step_music_player.settings_and_menu;

import android.content.Context;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.ContextThemeWrapper;
import android.view.MenuItem;
import android.view.View;
import android.widget.PopupMenu;

import androidx.appcompat.app.AppCompatActivity;

import com.example.step_music_player.R;

public class popmenu_discovery extends AppCompatActivity {
    private Context context;
    private PopupMenu popupMenu;

    public popmenu_discovery(Context context){ this.context=context; }

    public void showmenu(final View v, final search_popwin search_song_popwin ,final Handler rescan_handler,
                         final information_display_popwin infomation_popwin, final String info,final String datetime){
        popupMenu=new PopupMenu(new ContextThemeWrapper(context,R.style.OverflowMenu),v);
        popupMenu.getMenuInflater().inflate(R.menu.discovery_menu,popupMenu.getMenu());
        Log.i("Menuinfo","Info is:"+info);
        popupMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                select_popwin(v,item,search_song_popwin,rescan_handler,infomation_popwin,info,datetime);
                return false;
            }
        });
        popupMenu.setOnDismissListener(new PopupMenu.OnDismissListener() {
            @Override
            public void onDismiss(PopupMenu menu) { }
        });
        popupMenu.show();
    }

    private void select_popwin(View v, MenuItem item, search_popwin search_song_popwin ,android.os.Handler rescan_handler,
                               information_display_popwin information_popwin, String info,String datetime){
        CharSequence title = item.getTitle();
        if ("搜索歌曲".equals(title)) { search_song_popwin.open_search_window(v); }
        else if ("刷新歌单".equals(title)) {
            Message msg=new Message();
            msg.what=0;
            rescan_handler.sendMessage(msg);
        }
        else if ("后台数据".equals(title)) { information_popwin.open_information_window(v,info,datetime); }
        popupMenu.dismiss();
    }

}
