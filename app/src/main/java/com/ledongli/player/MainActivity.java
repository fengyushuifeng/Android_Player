package com.ledongli.player;

import android.annotation.TargetApi;
import android.content.Intent;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.ViewPager;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.google.gson.Gson;
import com.ledongli.player.net.ApiManager;
import com.ledongli.player.net.BaseResult;
import com.ledongli.player.net.BaseSubscriber;
import com.ledongli.player.net.bean.MoviesListResult;
import com.ledongli.player.ui.FragmentVideoList;
import com.ledongli.player.ui.VideoSearchActivity;
import com.ledongli.player.utils.ActivityUtils;
import com.ledongli.player.utils.MyConstant;
import com.ledongli.player.utils.ToastUtils;
import com.ledongli.player.utils.UpdateManager;
import com.ledongli.player.utils.downloadfile.UpdateApkInfoResult;
import com.ledongli.player.utils.downloadfile.UpdateApkService;

import java.util.ArrayList;
import java.util.List;

import rx.Observable;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

public class MainActivity extends BaseActivity implements ViewPager.OnPageChangeListener,View.OnClickListener {
    List<FragmentVideoList> fragmentList = new ArrayList<>();

    FragmentVideoList fraList;
    FragmentVideoList fraLike;
    //view
    ViewPager viewPager;
    TextView tvTabList;
    TextView tvTabLike;

    LinearLayout lyListTopViews;
    TextView tvListTypeHot;
    TextView tvListTypeLastet;
    ImageView ivSearch;

    TextView tvLikeTitle;

    //status
    int currTabPosi = 0;//当前选中的tab的posi
    boolean isListCurrSortTypeHot = true;//最热门

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.act_main);
        viewPager = findViewById(R.id.main_viewPager);
        tvTabList = findViewById(R.id.main_tv_tab_list);
        tvTabLike = findViewById(R.id.main_tv_tab_like);
        ivSearch = findViewById(R.id.main_list_iv_search);
        tvLikeTitle = findViewById(R.id.main_like_tv_title);
        lyListTopViews = findViewById(R.id.main_list_ly_top_views);
        tvListTypeHot = findViewById(R.id.main_list_tv_fire);
        tvListTypeLastet = findViewById(R.id.main_list_tv_new);

        fraList = new FragmentVideoList().setIsForCollect(false);
        fraLike = new FragmentVideoList().setIsForCollect(true);
        fragmentList.add(fraList);
        fragmentList.add(fraLike);

        MyAdapter myAdapter = new MyAdapter(getSupportFragmentManager());
        viewPager.setAdapter(myAdapter);
        viewPager.setOnPageChangeListener(this);

        tvTabList.setOnClickListener(this);
        tvTabLike.setOnClickListener(this);
        ivSearch.setOnClickListener(this);
        tvListTypeHot.setOnClickListener(this);
        tvListTypeLastet.setOnClickListener(this);
    }

    @Override
    protected void onResume() {
        super.onResume();
        //检查存储权限
        boolean isPermissionOk = checkPermissionsOfStorage();
        if (isPermissionOk){
            checkApkUpdate();
        }
    }

    boolean isUseLocaData = false;
    int currVersionCode = 0;
//    private UpdateApkService mUpdateApkService;
    UpdateApkInfoResult mResult;
    private void checkApkUpdate() {
        UpdateApkService mUpdateApkService = ApiManager.getInstence().createServiceFrom(UpdateApkService.class);
        currVersionCode = ActivityUtils.getAppVersionCode(getApplicationContext());
        Observable<BaseResult<UpdateApkInfoResult>> call =
                mUpdateApkService.getViewVersionInfo();
        call.subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new BaseSubscriber<BaseResult<UpdateApkInfoResult>>(this) {
                    @Override
                    public void onNext(BaseResult<UpdateApkInfoResult> result) {
                        if (null != result){
                            if (result.errorcode == 0){
                                if (isUseLocaData){
                                    //TODO 使用测试数据,展示数据
                                    mResult = new UpdateApkInfoResult();
                                    mResult.code = "1.1";
                                    mResult.download = "http://oss.ucdl.pp.uc.cn/fs01/union_pack/Wandoujia_249423_web_inner_referral_binded.apk?x-oss-process=udf%2Fpp-udf%2CJjc3LiMnJ3R0dXN2";
                                }else{
                                    mResult = result.ret;
                                }
//                                mResult = result.ret;
                            }else{
                                ToastUtils.showToast(getApplicationContext(),"检查版本更新失败:"+result.errorcode+","+result.errormessage);
                            }
                        }else{
                            ToastUtils.showToast(getApplicationContext(),"检查版本更新失败");
                        }
                        initUpdateApkInfos();
                    }
                });
    }
    UpdateManager update;
    private void initUpdateApkInfos() {
        if (null != mResult && currVersionCode != 0 && mResult.code != null){
            try{
                mResult.version_num = Float.parseFloat(mResult.code);
            }catch (Exception e){
            }
            mResult.version_name = mResult.code;
            mResult.version_num = 100;
            String newApkUrl = mResult.download;
            if (!TextUtils.isEmpty(newApkUrl)){
                if (null == update){
                    update = new UpdateManager(this,currVersionCode,
                            newApkUrl,mResult.version_num,mResult.version_name,false);
                }
                update.showNoticeDialog();
            }
        }
    }


    @Override
    public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

    }

    @Override
    public void onPageSelected(int position) {
        changeTabStatus(position);
    }

    private void changeTabStatus(int newPosi) {
        if (newPosi > 1){
            //异常
            return;
        }
        if (currTabPosi == newPosi){
            //刷新数据？
        }else{
            currTabPosi = newPosi;
            boolean isSelectedTabList = newPosi==0;//是否是选中了首页选项
            //切换tab,改变view状态
            setTabViewStatus(isSelectedTabList);
        }
    }

    /**
     * 设置tv的颜色值和drawable
     * @param isSelectedTabList
     */
    private void setTabViewStatus(boolean isSelectedTabList){
        tvTabList.setTextColor(ContextCompat.getColor(getApplicationContext(),
                isSelectedTabList?R.color.colorRed:R.color.colorTextTabGary) );
        tvTabList.setCompoundDrawablesWithIntrinsicBounds(0,
                isSelectedTabList?R.drawable.tab_list_on:R.drawable.tab_list,0,0);
        tvTabLike.setTextColor(ContextCompat.getColor(getApplicationContext(),
                !isSelectedTabList?R.color.colorRed:R.color.colorTextTabGary) );
        tvTabLike.setCompoundDrawablesWithIntrinsicBounds(0,
                !isSelectedTabList?R.drawable.tab_like_on:R.drawable.tab_like,0,0);
    }

    @Override
    public void onPageScrollStateChanged(int state) {

    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.main_tv_tab_list:
                tvLikeTitle.setVisibility(View.GONE);
                lyListTopViews.setVisibility(View.VISIBLE);
                ivSearch.setVisibility(View.VISIBLE);
                //切换viewpager
                viewPager.setCurrentItem(0);
                break;
            case R.id.main_tv_tab_like:
                tvLikeTitle.setVisibility(View.VISIBLE);
                lyListTopViews.setVisibility(View.GONE);
                ivSearch.setVisibility(View.GONE);
                //切换viewpager
                viewPager.setCurrentItem(1);
                break;
            case R.id.main_list_tv_fire:
                isListCurrSortTypeHot = true;
                changeSortType(isListCurrSortTypeHot);
                fraList.changeSortTypeOfMainVideoList(isListCurrSortTypeHot);
                break;
            case R.id.main_list_tv_new:
                isListCurrSortTypeHot = false;
                changeSortType(isListCurrSortTypeHot);
                fraList.changeSortTypeOfMainVideoList(isListCurrSortTypeHot);
                break;
            case R.id.main_list_iv_search:
                //跳转搜索页面
                Intent intent  = new Intent(this, VideoSearchActivity.class);
                startActivity(intent);
                break;
        }
    }

    private void changeSortType(boolean isSortTypeHot) {
        if (isSortTypeHot){
            tvListTypeHot.setTextColor(ContextCompat.getColor(getApplicationContext(),R.color.colorRed));
            tvListTypeHot.setBackground(ContextCompat.getDrawable(getApplicationContext(),R.drawable.bg_main_list_fire_selected));

            tvListTypeLastet.setTextColor(ContextCompat.getColor(getApplicationContext(),R.color.colorWhite));
            tvListTypeLastet.setBackground(ContextCompat.getDrawable(getApplicationContext(),R.drawable.bg_main_list_new_unselected));
        }else{
            tvListTypeHot.setTextColor(ContextCompat.getColor(getApplicationContext(),R.color.colorWhite));
            tvListTypeHot.setBackground(ContextCompat.getDrawable(getApplicationContext(),R.drawable.bg_main_list_fire_unselected));

            tvListTypeLastet.setTextColor(ContextCompat.getColor(getApplicationContext(),R.color.colorRed));
            tvListTypeLastet.setBackground(ContextCompat.getDrawable(getApplicationContext(),R.drawable.bg_main_list_new_selected));
        }
    }

    public class MyAdapter extends FragmentPagerAdapter {

        public MyAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public Fragment getItem(int i) {
            return fragmentList.get(i);
        }

        @Override
        public int getCount() {
            return fragmentList.size();
        }

    }
}
