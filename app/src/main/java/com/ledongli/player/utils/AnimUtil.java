package com.ledongli.player.utils;

import android.content.Context;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.animation.LinearInterpolator;

import com.ledongli.player.R;


/**
 * 功能描述：
 * Created by zpp_zoe on 2017/12/7.
 */

public class AnimUtil {

    public static void setAnimToViewAndStart(Context context, View view){
        Animation circle_anim = AnimationUtils.loadAnimation(context, R.anim.anim_round_rotate);
        LinearInterpolator interpolator = new LinearInterpolator();  //设置匀速旋转，在xml文件中设置会出现卡顿
        circle_anim.setInterpolator(interpolator);
        if (circle_anim != null) {
            view.startAnimation(circle_anim);  //开始动画
        }
    }

}
