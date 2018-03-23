package com.ledongli.player;

import android.content.Intent;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.ViewPager;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.ledongli.player.ui.FragmentVideoList;
import com.ledongli.player.ui.VideoSearchActivity;

import java.util.ArrayList;
import java.util.List;

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
// 看到了吗 en一个 Android -个project 怎么打包？
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
                //TODO 跳转搜索页面
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
