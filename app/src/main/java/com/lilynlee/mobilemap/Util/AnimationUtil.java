package com.lilynlee.mobilemap.Util;

import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationSet;
import android.view.animation.ScaleAnimation;
import android.view.animation.TranslateAnimation;

/**
 * Created by admin on 2016/8/2.
 */
public class AnimationUtil {

    /**
     * 让View控件跳跃一下的效果
     * @param view 需要设置动画的控件
     */
    public void SkipAnimation(View view){
        Animation up_tran = new TranslateAnimation(0,0,0,-18);
        up_tran.setFillAfter(true);
        up_tran.setDuration(200);
        view.startAnimation(up_tran);
        Animation down_tran = new TranslateAnimation(0,0,0,18);
        down_tran.setFillAfter(true);
        down_tran.setDuration(200);
        down_tran.setStartOffset(200);
        AnimationSet set = new AnimationSet(true);
        set.addAnimation(up_tran);
        set.addAnimation(down_tran);
        view.startAnimation(set);
    }


    /**
     * 点击按钮时候的动画效果
     * 点击时候按钮会变小一点
     * @param view 需要设置动画的控件
     */
    public void ButtonClickSmallAnimation(View view){
        AnimationSet animation_set = new AnimationSet(true);
        ScaleAnimation scaleSmallAnimation = new ScaleAnimation(1.0f,0.8f,1.0f,0.8f
                ,Animation.RELATIVE_TO_SELF,0.5f,Animation.RELATIVE_TO_SELF,0.5f);
        scaleSmallAnimation.setDuration(100);
        scaleSmallAnimation.setFillAfter(true);
        animation_set.addAnimation(scaleSmallAnimation);
        ScaleAnimation scaleBigAnimation = new ScaleAnimation(0.8f,1.0f,0.8f,1.0f
                ,Animation.RELATIVE_TO_SELF,0.5f,Animation.RELATIVE_TO_SELF,0.5f);
        scaleBigAnimation.setDuration(100);
        scaleBigAnimation.setStartOffset(100);
        scaleBigAnimation.setFillAfter(true);
        animation_set.addAnimation(scaleBigAnimation);
        view.startAnimation(animation_set);
    }
}
