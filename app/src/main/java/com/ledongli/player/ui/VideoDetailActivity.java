package com.ledongli.player.ui;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.content.ContextCompat;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.ledongli.player.BaseActivity;
import com.ledongli.player.R;
import com.ledongli.player.net.ApiManager;
import com.ledongli.player.net.BaseResult;
import com.ledongli.player.net.BaseSubscriber;
import com.ledongli.player.net.HotTagBean;
import com.ledongli.player.net.VideoDetailResult;
import com.ledongli.player.net.VideoDetailService;
import com.ledongli.player.net.bean.MovieItemBean;
import com.ledongli.player.utils.DensityUtil;
import com.ledongli.player.utils.MyConstant;
import com.ledongli.player.utils.ToastUtils;
import com.ledongli.player.view.FixGridLayout;
import com.ledongli.player.view.video.MyJZVideoPlayerStandard;
import com.squareup.picasso.Picasso;


import cn.jzvd.JZVideoPlayer;
import cn.jzvd.JZVideoPlayerStandard;
import rx.Observable;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

/**
 * 功能描述：
 * Created by zpp_zoe on 2018/2/11.
 */

public class VideoDetailActivity extends BaseActivity {

    MyJZVideoPlayerStandard myJZVideoPlayerStandard;

    TextView tvTitle;
    TextView tvDuration;
    TextView tvCreateTime;
    TextView tvActor;

    FixGridLayout fglTags;

//    MovieItemBean mMovieInfo;//不使用列表中的数据，使用加载的信息
    String uuid;

    VideoDetailResult mResult;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.act_videodetail);

        if (null != getIntent()){
//            mMovieInfo = (MovieItemBean) getIntent().getSerializableExtra("MovieItemBean");
            uuid = getIntent().getStringExtra("uuid");
        }

        tvTitle = (TextView) findViewById(R.id.videodetail_tv_title);
        tvCreateTime = (TextView) findViewById(R.id.videodetail_tv_create_time);
        tvDuration = (TextView) findViewById(R.id.videodetail_tv_duration);
        tvActor = (TextView) findViewById(R.id.videodetail_tv_actor);
        fglTags = (FixGridLayout) findViewById(R.id.videodetail_fgl_tags);

        // 导航返回键
        ImageButton back = (ImageButton)findViewById(R.id.Back);

        back.setOnClickListener(new View.OnClickListener() {

            @Override

            public void onClick(View view) {

                        finish();
            }
        });
    }

    @Override
    protected void onResume() {




        super.onResume();
        if (null == mResult){
            if (MyConstant.isUseLocalData){
                try{
                    VideoDetailResult detailData = new Gson().fromJson(MyConstant.MovieDetailResult,VideoDetailResult.class);
                    if (null != detailData){
                        initDataResult(detailData);
                    }
                }catch (Exception e){
                    e.printStackTrace();
                }
            }else{
                loadDetailInfos();
            }
        }
    }

    private void loadDetailInfos() {
        VideoDetailService mService = ApiManager.getInstence().createServiceFrom(VideoDetailService.class);
        Observable<BaseResult<VideoDetailResult>> call = mService.getVideoDetail(uuid);
        call.subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new BaseSubscriber<BaseResult<VideoDetailResult>>() {
                    @Override
                    public void onNext(BaseResult<VideoDetailResult> result) {
                        if (null != result){
                            if (result.errorcode == 0){
                                initDataResult(result.ret);
                            }else{
                                ToastUtils.showToast(getApplicationContext(),"获取详情失败:"+result.errorcode+","+result.errormessage);
                            }
                        }else{
                            ToastUtils.showToast(getApplicationContext(),"获取详情失败");
                        }
                    }
                });
    }

    private void initDataResult(VideoDetailResult detailResult) {


        myJZVideoPlayerStandard = findViewById(R.id.videodetail_videoplayer);

        myJZVideoPlayerStandard.setUp(detailResult.playurl
                , JZVideoPlayerStandard.SCREEN_WINDOW_NORMAL, detailResult.title);


        Picasso.with(this)
                .load(detailResult.getCoverimageUrl())
                .into(myJZVideoPlayerStandard.thumbImageView);

        tvTitle.setText(detailResult.title);

        tvCreateTime.setText(detailResult.getOnShowTimeStr());

        tvDuration.setText(detailResult.getDurationStr());

        tvActor.setText(detailResult.getActorStrs());

        if (null != detailResult.movietag){

            int fiveDP = DensityUtil.dip2px(getApplicationContext(),5);

            for (int i=0;i<detailResult.movietag.size() ; i++){
                TextView tv = new TextView(getApplicationContext());

                tv.setTag(detailResult.movietag.get(i).id);

                tv.setText(detailResult.movietag.get(i).tagname);

                tv.setPadding(fiveDP,fiveDP,fiveDP,fiveDP);

                tv.setTextColor(ContextCompat.getColor(getApplicationContext(),R.color.colorTextBlack));

                tv.setBackgroundResource(R.drawable.bg_videodetail_tags);

                fglTags.addView(tv);
            }
        }

    }

    /**
     * 暂停 /失去焦点
     * Activity或者按Home键之后会视频就会releas(释放)
     */
    @Override
    protected void onPause() {
        super.onPause();
        JZVideoPlayer.releaseAllVideos();
    }

    /**
     * backPress函数判断了点击回退按钮的相应，
     * 如果全屏会退出全屏播放，如果不是全屏则会交给Activity
     */
    @Override
    public void onBackPressed() {
        if (JZVideoPlayer.backPress()) {
            return;
        }
        super.onBackPressed();
    }

}
