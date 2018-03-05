package com.ledongli.player.ui;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.view.View;
import android.widget.TextView;

import com.ledongli.player.BaseActivity;
import com.ledongli.player.BaseSecondFragment;
import com.ledongli.player.R;
import com.ledongli.player.utils.ToastUtils;

/**
 * 功能描述：
 * Created by zpp_zoe on 2018/2/13.
 */

public class SecondActivity extends BaseActivity implements View.OnClickListener {

    public static final String EXTRA_NAME_whichFra = "whichFra";
    public static final int WHITCH_VideoSearchResultList = 100;//搜索结果页


    BaseSecondFragment baseSecondFragment;


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.act_second);

        initContentFragment();
    }

    private void initContentFragment() {

        Intent intent = getIntent();
        int which = intent.getIntExtra(EXTRA_NAME_whichFra,0);
        switch(which) {
            case WHITCH_VideoSearchResultList:
                baseSecondFragment = new FragmentVideoList();
                if (null != intent) {
                    Bundle bundle = new Bundle();
                    bundle.putBoolean("isForSearchResultList",true);
                    bundle.putString("keywords", intent.getStringExtra("keywords"));
                    baseSecondFragment.setArguments(bundle);
                }
                setTopStyle("热搜","搜索列表");
                break;
        }

        if (null!= baseSecondFragment){
            FragmentManager fm = getSupportFragmentManager();
            FragmentTransaction transaction = fm.beginTransaction();
            transaction.replace(R.id.second_frame_content,baseSecondFragment);
            transaction.commit();
        }else{
            ToastUtils.showToast(getApplicationContext(),"无二级页面信息~");
            finish();
        }
    }


    private void setTopStyle(String back_text, String title){
        ((TextView)findViewById(R.id.second_tv_nav_back)).setText(back_text);
        ((TextView)findViewById(R.id.second_tv_nav_title)).setText(title);
        ((TextView)findViewById(R.id.second_tv_nav_back)).setOnClickListener(this);
        ((TextView)findViewById(R.id.second_tv_nav_title)).setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch(v.getId()){
            case R.id.second_tv_nav_back:
            case R.id.second_tv_nav_title:
                finish();
                break;
        }
    }
}
