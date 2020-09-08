package com.example.step_music_player.UI;

import android.content.Context;
import android.util.TypedValue;
import android.widget.LinearLayout;
import android.widget.ListView;

import com.example.step_music_player.music_tool.music_information;
import com.example.step_music_player.R;

import java.util.List;

public class song_listview_setting {

    public void refresh_display(listview_adapter adapter,Context context,List<music_information> new_information){
       adapter.setAdapter_listItems(context,new_information);
       adapter.notifyDataSetChanged();
    }

    public void auto_update_listview(ListView listView,int target_position, List<music_information> info_list, int last_item){
        if(target_position<=listView.getFirstVisiblePosition()){
            if(target_position==0&&listView.getLastVisiblePosition()==info_list.size()-1){ listView.setSelection(target_position); }
            else{ listView.smoothScrollToPosition(target_position); }
        }
        else{
            if(target_position>=listView.getLastVisiblePosition()){
                if(target_position==info_list.size()-1&&listView.getFirstVisiblePosition()==0) { listView.setSelection(target_position - last_item + 2); }
                else{ listView.smoothScrollToPosition(target_position); }
            }
        }
    }

    public void update_display_theme(Context context, ListView listView, LinearLayout title_section,boolean display_background){

       TypedValue tv_theme_color=new TypedValue();
       context.getTheme().resolveAttribute(R.attr.theme_color,tv_theme_color,true);
       title_section.setBackgroundColor(tv_theme_color.data);
       if(display_background) {
           TypedValue tv_listview_bg = new TypedValue();
           context.getTheme().resolveAttribute(R.attr.listview_background, tv_listview_bg, true);
           listView.setBackgroundResource(tv_listview_bg.resourceId);
       }
       else{ listView.setBackgroundResource(R.mipmap.default_background); }
    }

    public void update_background(Context context,ListView listView,boolean show_background){
        if(show_background){
            TypedValue tv=new TypedValue();
            context.getTheme().resolveAttribute(R.attr.listview_background,tv,true);
            listView.setBackgroundResource(tv.resourceId);
        }
        else{ listView.setBackgroundResource(R.mipmap.default_background); }
    }
}