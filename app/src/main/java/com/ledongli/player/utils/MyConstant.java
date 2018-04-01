package com.ledongli.player.utils;

import android.os.Environment;

/**
 * 功能描述：
 * Created by zpp_zoe on 2018/2/10.
 */

public class MyConstant {


    // SDCard路径
    public static final String SD_PATH = Environment
            .getExternalStorageDirectory().getAbsolutePath();
    public static final String BASE_DIR = SD_PATH + "/LeDongLiPlayer/";
    public static final boolean isShowSysText = true;



}
