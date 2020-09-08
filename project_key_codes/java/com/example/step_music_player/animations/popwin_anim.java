package com.example.step_music_player.animations;

import android.view.View;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.view.animation.AnimationSet;
import android.view.animation.Interpolator;
import android.view.animation.ScaleAnimation;

public class popwin_anim {
    public void setPopupAnimation(View paramView) {
        AlphaAnimation localAlphaAnimation = new AlphaAnimation(0.0F, 1.0F);
        localAlphaAnimation.setInterpolator(new Interpolator() {
            public float getInterpolation(float paramFloat) {
                return 10.0F * paramFloat;
            }
        });
        localAlphaAnimation.setDuration(200L);
        ScaleAnimation localScaleAnimation = new ScaleAnimation(0F, 1.0F, 0F, 1.0F, Animation.ZORDER_TOP, 0.5F, Animation.ZORDER_TOP, 0F);
        localScaleAnimation.setDuration(200L);
        AnimationSet localAnimationSet = new AnimationSet(true);
        localAnimationSet.addAnimation(localScaleAnimation);
        localAnimationSet.addAnimation(localAlphaAnimation);
        paramView.startAnimation(localAnimationSet);
    }
}
