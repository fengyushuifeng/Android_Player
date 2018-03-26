package com.ledongli.player;

import android.app.Application;

import org.xutils.x;

/**
 * Created by Gemini on 2018/3/25.
 */

public class BaseApplication extends Application {

    @Override
    public void onCreate() {
        super.onCreate();

        x.Ext.init(this);
        x.Ext.setDebug(true);
    }
}
