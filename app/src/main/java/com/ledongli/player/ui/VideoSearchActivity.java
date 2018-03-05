package com.ledongli.player.ui;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.content.ContextCompat;
import android.text.TextUtils;
import android.view.KeyEvent;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.ledongli.player.BaseActivity;
import com.ledongli.player.R;
import com.ledongli.player.net.ApiManager;
import com.ledongli.player.net.BaseSubscriber;
import com.ledongli.player.net.HotTagBean;
import com.ledongli.player.net.HotTagListResult;
import com.ledongli.player.net.VideoSearchService;
import com.ledongli.player.utils.DensityUtil;
import com.ledongli.player.utils.MyConstant;
import com.ledongli.player.utils.ToastUtils;
import com.ledongli.player.view.FixGridLayout;

import java.util.ArrayList;

import rx.Observable;

import cn.jzvd.JZVideoPlayer;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

/**
 * 功能描述：
 * Created by zpp_zoe on 2018/2/11.
 */

public class VideoSearchActivity extends BaseActivity implements View.OnKeyListener {

    FixGridLayout fglTags;

    EditText etSearchKey;
    TextView tvCancel;

    ImageView ivNavBack;

    private String keywords;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.act_videosearch);

        ivNavBack = (ImageView) findViewById(R.id.videosearch_iv_nav_back);
        tvCancel = (TextView) findViewById(R.id.videoseach_tv_cancel);
        etSearchKey = (EditText) findViewById(R.id.videoseach_et);
        fglTags = (FixGridLayout) findViewById(R.id.videosearch_fgl_tags);

        etSearchKey.setOnKeyListener(this);
        tvCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                etSearchKey.setText("");
                etSearchKey.clearFocus();
            }
        });
        ivNavBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

    }

    @Override
    protected void onResume() {
        super.onResume();
        if (MyConstant.isUseLocalData){
            //TODO 测试页面展示
            ArrayList<HotTagBean> tags = new ArrayList<>();
            for (int i= 0;i<10;i++){
                HotTagBean temp = new HotTagBean();
                temp.id = i;
                temp.tagname = "标签"+i;
                tags.add(temp);
            }
            initTagsInfo(tags);
        }else{
            //加载搜索标签
            loadTagsData();
        }

    }

    private void loadTagsData() {
        VideoSearchService mService = ApiManager.getInstence().createServiceFrom(VideoSearchService.class);
        Observable<HotTagListResult> call = mService.getHotTags();
        call.subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new BaseSubscriber<HotTagListResult>(this) {
                    @Override
                    public void onNext(HotTagListResult result) {
                        //处理数据
                        if (null != result){
                            if (result.errorcode == 0){
                                initTagsInfo(result.ret);
                            }else{
                                ToastUtils.showToast(getApplicationContext(),"获取热门标签失败:"+result.errorcode+","+result.errormessage);
                            }
                        }else{
                            ToastUtils.showToast(getApplicationContext(),"获取热门标签失败");
                        }
                    }
                });

    }

    void initTagsInfo(ArrayList<HotTagBean> tags){
        fglTags.removeAllViews();
        if (null != tags){
            int fiveDP = DensityUtil.dip2px(getApplicationContext(),5);
            for (int i=0;i<tags.size() ; i++){
                final HotTagBean tempTagInfo = tags.get(i);
                TextView tv = new TextView(getApplicationContext());
                tv.setTag(tags.get(i).id);
                tv.setText(tags.get(i).tagname);
                tv.setPadding(fiveDP,fiveDP,fiveDP,fiveDP);
                tv.setTextColor(ContextCompat.getColor(getApplicationContext(),R.color.colorTextBlack));
                tv.setBackgroundResource(R.drawable.bg_videodetail_tags);
                tv.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        //跳转到搜索结果页，根据标签搜索
                        goToVideoResultList(tempTagInfo);
                    }
                });
                fglTags.addView(tv);
            }
        }
    }


    @Override
    protected void onPause() {
        super.onPause();
        JZVideoPlayer.releaseAllVideos();
    }

    @Override
    public void onBackPressed() {
        if (JZVideoPlayer.backPress()) {
            return;
        }
        super.onBackPressed();
    }

    @Override
    public boolean onKey(View v, int keyCode, KeyEvent event) {
        if(keyCode == KeyEvent.KEYCODE_ENTER){//点击确认键进行搜索商品
            keywords = etSearchKey.getText().toString().trim();
            if (!TextUtils.isEmpty(keywords)){
                goToVideoResultList();
            }else{
                ToastUtils.showToast(getApplicationContext(),"请输入您要搜索的关键词或直接点击热门标签进行搜索~");
            }
            return true;
        }
        return false;
    }

    private void goToVideoResultList(){
        //跳转到搜索页面
        Intent intent = new Intent(this,SecondActivity.class);
        intent.putExtra(SecondActivity.EXTRA_NAME_whichFra,SecondActivity.WHITCH_VideoSearchResultList);
        intent.putExtra("isSearchByTag",false);
        intent.putExtra("keywords",etSearchKey.getText().toString());
        startActivity(intent);
        finish();
    }
    private void goToVideoResultList(HotTagBean tagInfo){
        //跳转到搜索页面
        Intent intent = new Intent(this,SecondActivity.class);
        intent.putExtra(SecondActivity.EXTRA_NAME_whichFra,SecondActivity.WHITCH_VideoSearchResultList);
        intent.putExtra("keywords",tagInfo.tagname);
        startActivity(intent);
        finish();
    }

}
