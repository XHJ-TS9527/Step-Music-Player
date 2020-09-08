package com.example.step_music_player.UI;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Color;
import android.net.Uri;
import android.text.TextPaint;
import android.util.Log;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;
import com.example.step_music_player.R;

import com.example.step_music_player.music_tool.music_information;

import java.io.File;
import java.math.BigDecimal;
import java.util.List;

public class listview_adapter extends BaseAdapter {
    private List<music_information> adapter_listItems;
    private LayoutInflater layoutInflater;
    private int position_flag;
    private Context context;

    public void setAdapter_listItems(Context context, List<music_information> listItems) {
        this.context = context;
        adapter_listItems = listItems;
    }

    public List<music_information> getAdapter_listItems() {
        return adapter_listItems;
    }

    public listview_adapter(Context context) {
        layoutInflater = LayoutInflater.from(context);
    }

    public void setPosition_flag(int flag) {
        position_flag = flag;
    }

    public int getCount() {
        return adapter_listItems.size();
    }

    public Object getItem(int position) {
        return adapter_listItems.get(position);
    }

    public long getItemId(int i) {
        return i;
    }

    @SuppressLint("ResourceType")
    public View getView(int position, View convertView, ViewGroup parent) {
        adapter_listitem_view myadapter_listitemview;
        TextView song_name_textview;
        // bind widgets
        if (convertView == null) {
            myadapter_listitemview = new adapter_listitem_view();
            convertView = layoutInflater.inflate(R.layout.single_song_item_layout, null);
            myadapter_listitemview.setSong_name((TextView) convertView.findViewById(R.id.music_song_name));
            myadapter_listitemview.setSong_artist((TextView) convertView.findViewById(R.id.music_song_artist));
            myadapter_listitemview.setSong_size((TextView) convertView.findViewById(R.id.music_song_size));
            myadapter_listitemview.setSong_duration((TextView) convertView.findViewById(R.id.music_song_duration));
            myadapter_listitemview.setSong_album_img((ImageView) convertView.findViewById(R.id.music_song_album_img));
            convertView.setTag(myadapter_listitemview);
        } else {
            myadapter_listitemview = (adapter_listitem_view) convertView.getTag();
        }
        // set content
        // transfer filename to songname
        String song_name = adapter_listItems.get(position).getSong_name();
        Log.i("Adapter info", "song name: " + song_name);
        Log.i("Adapter info", "song artist: " + adapter_listItems.get(position).getSong_artist());
        Log.i("Adapter info", "song size: " + str_size_MB(adapter_listItems.get(position).getSong_size()));
        Log.i("Adapter info", "song duration: " + duration2str(adapter_listItems.get(position).getSong_duration()));
        // set themes
        TextPaint textpaint = myadapter_listitemview.song_name.getPaint();
        TypedValue tv_color,tv_play_icon;
        String album_img_path;
        int theme_color, theme_small_play_icon;
        tv_color=new TypedValue();
        tv_play_icon=new TypedValue();
        context.getTheme().resolveAttribute(R.attr.colorPrimary,tv_color,true);
        context.getTheme().resolveAttribute(R.attr.small_play_icon,tv_play_icon,true);
        theme_color=tv_color.data;
        theme_small_play_icon=tv_play_icon.resourceId;
        if (position == position_flag) { // playing song, set to theme color
            Log.i("Adapterinfo", "I ran this adapter selection.");
            Log.i("Adapterinfo","The color is"+theme_color);
            textpaint.setFakeBoldText(true);
            myadapter_listitemview.song_name.setTextColor(theme_color);
            myadapter_listitemview.song_artist.setTextColor(theme_color);
            myadapter_listitemview.song_size.setTextColor(theme_color);
            myadapter_listitemview.song_duration.setTextColor(theme_color);
            myadapter_listitemview.song_album_img.setImageResource(theme_small_play_icon);
        } else {
            textpaint.setFakeBoldText(false);
            myadapter_listitemview.song_name.setTextColor(Color.BLACK);
            myadapter_listitemview.song_artist.setTextColor(Color.BLACK);
            myadapter_listitemview.song_size.setTextColor(Color.BLACK);
            myadapter_listitemview.song_duration.setTextColor(Color.BLACK);
            album_img_path = adapter_listItems.get(position).getSong_album_img_path();
            if (album_img_path.equals("-1")) {
                myadapter_listitemview.song_album_img.setImageResource(R.mipmap.default_album_photo);
            } else {
                myadapter_listitemview.song_album_img.setImageURI(Uri.fromFile(new File(album_img_path)));
            }
            Log.i("Adapter info", "song album img: " + adapter_listItems.get(position).getSong_album_img_path());
        }
        //set contents
        myadapter_listitemview.song_name.setText(cut_song_name(song_name));
        myadapter_listitemview.song_artist.setText(adapter_listItems.get(position).getSong_artist());
        myadapter_listitemview.song_size.setText(str_size_MB(adapter_listItems.get(position).getSong_size()));
        myadapter_listitemview.song_duration.setText(duration2str(adapter_listItems.get(position).getSong_duration()));

        return convertView;
    }

    private String duration2str(int duration) { // change duration time (unit: ms) to time string minute:second
        int minute = duration / 60000;
        int second = (duration / 1000) % 60;
        if (second < 10) {
            return minute + ":0" + second;
        } else {
            return minute + ":" + second;
        }
    }

    private String str_size_MB(int size) { // change the size unit from Byte to MB
        double MB_size = size * 1.0 / 1024 / 1024;
        BigDecimal b = new BigDecimal(MB_size);
        float MB_size_2f = b.setScale(2, BigDecimal.ROUND_HALF_UP).floatValue();
        return MB_size_2f + "MB";
    }

    private String cut_song_name(String song_name) {
        int name_length = song_name.length();
        String cutted_song_name = song_name;
        if(name_length>=5){
            switch (song_name.substring(name_length - 4)){
                case ".mp3":
                case ".aac":
                case ".m4a":
                case ".3gp":
                case ".amr":
                case ".wav":
                case ".wma":{
                    cutted_song_name = song_name.substring(0, name_length - 4).trim();
                }
            }
        }
        return cutted_song_name;
    }

}
