package com.ledongli.player.utils;

import android.app.Activity;
import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.os.Build;
/**
 * 功能描述：
 * Created by zpp_zoe on 2017/11/1.
 */

public class ActivityUtils {

    /**
     * 获取版本名称
     * @return 当前应用的版本名称
     */
    public static String getAppVersionName(Context ctx) {
        try {
            PackageManager manager = ctx.getPackageManager();
            PackageInfo info = manager.getPackageInfo(ctx.getPackageName(), 0);
            String versionName = info.versionName;
            return versionName;
        } catch (Exception e) {
            e.printStackTrace();
            return "";
        }
    }
    /**
     * 获取版本号
     * @return 当前应用的版本号
     */
    public static int getAppVersionCode(Context ctx) {
        try {
            PackageManager manager = ctx.getPackageManager();
            PackageInfo info = manager.getPackageInfo(ctx.getPackageName(), 0);
            int version = info.versionCode;
            return version;
        } catch (Exception e) {
            e.printStackTrace();
            return 0;
        }
    }
    public static boolean isActRunning(Activity act){
        if (act == null || act.isFinishing()) {
            return false;
        }else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1){
            if (act.isDestroyed()) return false;
        }
        return true;
    }
}
