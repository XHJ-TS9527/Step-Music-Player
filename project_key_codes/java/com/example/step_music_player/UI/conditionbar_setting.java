package com.example.step_music_player.UI;

import android.app.Activity;
import android.content.Context;
import android.os.Build;
import android.util.TypedValue;
import android.view.Window;
import android.view.WindowManager;

import androidx.annotation.RequiresApi;

import com.example.step_music_player.R;

public class conditionbar_setting {
    private Window window;
    private Context context;

    public conditionbar_setting(Activity activity,Context context){
        window=activity.getWindow();
        window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
        window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
        this.context=context;
    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    public void setConditionBarColor(){
        TypedValue tv=new TypedValue();
        context.getTheme().resolveAttribute(R.attr.theme_color,tv,true);
        window.setStatusBarColor(tv.data);
    }
}
