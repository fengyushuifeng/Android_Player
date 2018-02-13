package com.ledongli.player.view;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.support.annotation.DrawableRes;
import android.support.annotation.NonNull;
import android.view.View;
import android.widget.TextView;

import com.ledongli.player.R;
import com.ledongli.player.utils.AnimUtil;

/**
 * 功能描述：
 * Created by zpp_zoe on 2017/11/6.
 */

public class LoadingDialog extends Dialog{

    private Context ctx;
    private View viewIcon;
    private TextView tvTips;

    public LoadingDialog(@NonNull Context context) {
        super(context, R.style.LoadingDialogStyle);
        this.ctx = context;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.view_dialog_loading);
        viewIcon = findViewById(R.id.dialog_loading_ic);
        tvTips = findViewById(R.id.dialog_loading_tv);
    }

    public void setIcon(@DrawableRes int bgId) {
        viewIcon.setBackgroundResource(bgId);
    }

    public void setTvTips(String tips){
        tvTips.setText(tips);
    }

    @Override
    public void show() {
        super.show();
        if (null != ctx && null != viewIcon){
            AnimUtil.setAnimToViewAndStart(ctx,viewIcon);
        }
    }

    @Override
    public void dismiss() {
        if (null != viewIcon){
            viewIcon.clearAnimation();
        }
        super.dismiss();
    }
}
