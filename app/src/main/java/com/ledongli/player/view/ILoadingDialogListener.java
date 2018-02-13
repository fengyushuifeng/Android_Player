package com.ledongli.player.view;

import android.support.annotation.DrawableRes;

/**
 * 功能描述：
 * Created by zpp_zoe on 2017/11/10.
 */

public interface ILoadingDialogListener {

    void setTips(String tips);
    void setIcon(@DrawableRes int icon);
    void showDialog();
    void dimissDialog();

}
