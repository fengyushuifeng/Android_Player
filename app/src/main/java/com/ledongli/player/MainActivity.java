package com.ledongli.player;

import android.Manifest;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Environment;
import android.os.Handler;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.content.ContextCompat;
import android.support.v4.content.FileProvider;
import android.support.v4.view.ViewPager;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.ledongli.player.net.ApiManager;
import com.ledongli.player.net.BaseResult;
import com.ledongli.player.net.BaseSubscriber;
import com.ledongli.player.net.VersionService;
import com.ledongli.player.net.VersionServiceResult;
import com.ledongli.player.net.VersionItem;
import com.ledongli.player.net.VideoDetailService;
import com.ledongli.player.ui.FragmentVideoList;
import com.ledongli.player.ui.VideoSearchActivity;
import com.ledongli.player.utils.ToastUtils;

import org.xutils.common.Callback;
import org.xutils.http.RequestParams;
import org.xutils.x;

import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

import java.io.File;
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

        isVersion();


    }

    @Override
    public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

    }

    @Override
    public void onPageSelected(int position) {
        changeTabStatus(position);
    }

    private void changeTabStatus(int newPosi) {
        if (newPosi > 1) {
            //异常
            return;
        }
        if (currTabPosi == newPosi) {
            //刷新数据？
        } else {
            currTabPosi = newPosi;
            boolean isSelectedTabList = newPosi == 0;//是否是选中了首页选项
            //切换tab,改变view状态
            setTabViewStatus(isSelectedTabList);
        }
    }

    /**
     * 设置tv的颜色值和drawable
     *
     * @param isSelectedTabList
     */
    private void setTabViewStatus(boolean isSelectedTabList) {
        tvTabList.setTextColor(ContextCompat.getColor(getApplicationContext(),
                isSelectedTabList ? R.color.colorRed : R.color.colorTextTabGary));
        tvTabList.setCompoundDrawablesWithIntrinsicBounds(0,
                isSelectedTabList ? R.drawable.tab_list_on : R.drawable.tab_list, 0, 0);
        tvTabLike.setTextColor(ContextCompat.getColor(getApplicationContext(),
                !isSelectedTabList ? R.color.colorRed : R.color.colorTextTabGary));
        tvTabLike.setCompoundDrawablesWithIntrinsicBounds(0,
                !isSelectedTabList ? R.drawable.tab_like_on : R.drawable.tab_like, 0, 0);
    }

    @Override
    public void onPageScrollStateChanged(int state) {

    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
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
                Intent intent = new Intent(this, VideoSearchActivity.class);
                startActivity(intent);
                break;
        }
    }

    private void changeSortType(boolean isSortTypeHot) {
        if (isSortTypeHot) {
            tvListTypeHot.setTextColor(ContextCompat.getColor(getApplicationContext(), R.color.colorRed));
            tvListTypeHot.setBackground(ContextCompat.getDrawable(getApplicationContext(), R.drawable.bg_main_list_fire_selected));

            tvListTypeLastet.setTextColor(ContextCompat.getColor(getApplicationContext(), R.color.colorWhite));
            tvListTypeLastet.setBackground(ContextCompat.getDrawable(getApplicationContext(), R.drawable.bg_main_list_new_unselected));
        } else {
            tvListTypeHot.setTextColor(ContextCompat.getColor(getApplicationContext(), R.color.colorWhite));
            tvListTypeHot.setBackground(ContextCompat.getDrawable(getApplicationContext(), R.drawable.bg_main_list_fire_unselected));

            tvListTypeLastet.setTextColor(ContextCompat.getColor(getApplicationContext(), R.color.colorRed));
            tvListTypeLastet.setBackground(ContextCompat.getDrawable(getApplicationContext(), R.drawable.bg_main_list_new_selected));
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


    /*
    *  判断版本
    * */
    public void  isVersion(){

        Observable<BaseResult<VersionServiceResult>> call = ApiManager.getInstence()
                .createServiceFrom(VersionService.class)
                .getVersion();

        call.subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new BaseSubscriber<BaseResult<VersionServiceResult>>() {
                    @Override
                    public void onNext(final BaseResult<VersionServiceResult> result) {
                      
                       if(Float.parseFloat( result.ret.code ) >  getVerCode(MainActivity.this)){
                           //弹框 这个算不上强制更新，就这就行 不更新不能用  但是你点更新，这个限制就没了- -无所谓
                           final View view = LayoutInflater.from(MainActivity.this).inflate(R.layout.dialog_download,null);
                           new AlertDialog.Builder(MainActivity.this)
                                   .setTitle("新版本")
                                   .setMessage("该版本为强制更新，取消将退出app")
                                   .setPositiveButton("Update", new DialogInterface.OnClickListener() {
                                       @Override
                                       public void onClick(DialogInterface dialogInterface, int i) {
                                           //ContextCompat.checkSelfPermission(MainActivity.this,"android.permission.WRITE_EXTERNAL_STORAGE");

                                           //这块是检查权限，没有权限就会报异常，这个是android 6.0的新特性，到时候你上网找博客看下
                                           ActivityCompat.requestPermissions(MainActivity.this,new String[]{"android.permission.WRITE_EXTERNAL_STORAGE"},0);
                                            if(!getPackageManager().canRequestPackageInstalls()){

                                                ActivityCompat.requestPermissions(MainActivity.this,new String[]{Manifest.permission.REQUEST_INSTALL_PACKAGES},1);
                                            }
                                           AlertDialog dialog = new AlertDialog.Builder(MainActivity.this)
                                                   .setTitle("正在下载")
                                                   .setView(view)
                                                   .setCancelable(false)
                                                   .show();

                                           download(dialog,result.ret.download);

                                       }
                                   })
                                   .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                                       @Override
                                       public void onClick(DialogInterface dialogInterface, int i) {
                                           System.exit(1);
                                       }
                                   }).show();
                       }

                    }

                    @Override
                    public void onError(Throwable e) {
                        super.onError(e);
                    }
                });


    }

    private void download(final AlertDialog dialog ,String url){
        String fileName = "player.apk";
        //下面就是xUtils的网络请求，因为这个自带进度，retrofit不带，重写也费劲，用现成的
        File file = new File(Environment.getExternalStorageDirectory() + File.separator + fileName);

        final ProgressBar progressBar = dialog.findViewById(R.id.dialog_pb_progress);

        final TextView textView = dialog.findViewById(R.id.dialog_tv_progress);

        RequestParams params = new RequestParams(url);
        params.setAutoResume(true);
        params.setSaveFilePath(file.getAbsolutePath());
        x.http().get(params, new Callback.ProgressCallback<File>() {
            @Override
            public void onSuccess(File result) {
                Toast.makeText(MainActivity.this, "文件下载成功，存储在：" + result.getAbsolutePath(), Toast.LENGTH_SHORT).show();
                dialog.cancel();


                Intent intent = new Intent(Intent.ACTION_VIEW);
// 由于没有在Activity环境下启动Activity,设置下面的标签
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
//参数1 上下文, 参数2 Provider主机地址 和配置文件中保持一致   参数3  共享的文件
                Uri apkUri = FileProvider.getUriForFile(MainActivity.this, "com.example.chenfengyao.installapkdemo", result);
//添加这一句表示对目标应用临时授权该Uri所代表的文件
                intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
                intent.setDataAndType(apkUri, "application/vnd.android.package-archive");
                startActivity(intent);

            }

            @Override
            public void onError(Throwable ex, boolean isOnCallback) {

            }

            @Override
            public void onCancelled(CancelledException cex) {

            }

            @Override
            public void onFinished() {

            }

            @Override
            public void onWaiting() {
            //这个模拟器的文件浏览器在哪
            }

            @Override
            public void onStarted() {

            }

            // 你等下我找一个apk 的连接 你试试看看能不能安装
            @Override
            public void onLoading(long total, long current, boolean isDownloading) {

                progressBar.setProgress((int)(current * 1000 / total),true);
                //刚才断点下载了，
            }
        });
    }


    /*
    *  获取app版本
    * */
    public int getVerCode(Context context) {

        int verCode = 0;

        try {


            verCode = context.getPackageManager().getPackageInfo("com.ledongli.player", 0).versionCode;

        } catch (PackageManager.NameNotFoundException e) {

            Log.d("PackageManager", "");
        }

        return verCode;
    }

}