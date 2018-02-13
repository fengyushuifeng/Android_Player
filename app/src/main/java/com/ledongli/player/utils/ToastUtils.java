package com.ledongli.player.utils;

import android.app.Activity;
import android.content.Context;
import android.widget.Toast;

/**
 * 功能描述：
 * Created by zpp_zoe on 2017/11/1.
 */

public class ToastUtils {
    public static void showToast(Activity act, String str){
        if (null == act || act.isFinishing())
            return;
        showToast(act.getApplicationContext(),str);
    }
    public static void showToast(Context ctx,String str){
        if (null == ctx ) return;
        Toast.makeText(ctx,str,Toast.LENGTH_SHORT).show();
    }

}
