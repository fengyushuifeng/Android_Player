package com.ledongli.player.ui;


import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.ListView;

import com.google.gson.Gson;
import com.ledongli.player.BaseSecondFragment;
import com.ledongli.player.R;
import com.ledongli.player.adapter.VideoListAdapter;
import com.ledongli.player.net.ApiManager;
import com.ledongli.player.net.BaseSubscriber;
import com.ledongli.player.net.HotTagBean;
import com.ledongli.player.net.VideoListService;
import com.ledongli.player.net.bean.MovieItemBean;
import com.ledongli.player.net.bean.MoviesListResult;
import com.ledongli.player.utils.MyConstant;
import com.ledongli.player.utils.ToastUtils;

import java.util.ArrayList;

import cn.jzvd.JZVideoPlayer;
import in.srain.cube.views.ptr.PtrClassicDefaultFooter;
import in.srain.cube.views.ptr.PtrDefaultHandler2;
import in.srain.cube.views.ptr.PtrFrameLayout;
import rx.Observable;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

/**
 * 首页 + 搜藏 + 搜索结果展示页
 * Created by Nathen on 2017/6/9.
 */
public class FragmentVideoList extends BaseSecondFragment {

    //api
    VideoListService mService;

    PtrFrameLayout ptrFrameLayout;
    ListView listView;

    VideoListAdapter mAdapter;
    ArrayList<MovieItemBean> dataList = new ArrayList<>();

    /////////////区分 不同页面/////////////////////
    /////首页///////
    boolean isForMainVideoList = false;//是否是首页 = index==0
    /////搜索结果页///////
    boolean isForSearchResultList = false;//是否是展示的视频搜索结果页
    boolean isSearchByTag = false;//是否是根据tag搜索
    //////收藏页 ////////
    boolean isForCollect = false;//是否是收藏列表 = index==1
    //post数据
    String keywords;              //仅搜索结果页使用
    HotTagBean tagInfo;           //仅搜索结果页使用
    int currPage = 0;//从？1？开始 //搜藏页+结果收藏页
    String sortPostValue = "hot"; //搜藏页+结果收藏页


    //
    MoviesListResult mMoviesListResult;

    public FragmentVideoList setIsForCollect(boolean isForCollect) {
        this.isForMainVideoList = !isForCollect;
        this.isForCollect = isForCollect;
        return this;
    }
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInastanceState) {
        initData();
        mAdapter = new VideoListAdapter(getActivity(),dataList);
        ptrFrameLayout = (PtrFrameLayout) inflater.inflate(R.layout.layout_list, container, false);
        listView = (ListView) ptrFrameLayout.findViewById(R.id.list_listview);
        listView.setAdapter(mAdapter);
        listView.setOnScrollListener(new AbsListView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(AbsListView view, int scrollState) {

            }

            @Override
            public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {
                JZVideoPlayer.onScrollReleaseAllVideos(view, firstVisibleItem, visibleItemCount, totalItemCount);
            }
        });
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent intent = new Intent(getActivity(),VideoDetailActivity.class);
                intent.putExtra("MovieItemBean",dataList.get(position));
                startActivity(intent);
            }
        });

        PtrClassicDefaultFooter footer = new PtrClassicDefaultFooter(getActivity().getApplicationContext());
        ptrFrameLayout.setFooterView(footer);
        ptrFrameLayout.addPtrUIHandler(footer);
        ptrFrameLayout.setPtrHandler(new PtrDefaultHandler2() {
            @Override
            public void onLoadMoreBegin(PtrFrameLayout frame) {
                currPage++;
                loadDataList();
//                if(null != shopListResult && null != shopListResult.page){
//                    PageOfData pageData = shopListResult.page;
//                    if (pageData.page < pageData.page_total ){
//                        currPage +=1;
//                        loadShopList();
//                    }else{
//                        ToastUtils.showToast(getActivity().getApplicationContext(),"没有更多数据了");
//                        ptrFrameLayout.refreshComplete();
//                    }
//                }else{
//                    if (shopDataResultPageTotal>1 && null != shopDataList && shopDataList.size()>0){
//                        currPage +=1;
//                        loadShopList();
//                    }else{
//                        //暂无数据？需加载第一页？
//                        currPage = 1;
//                        loadShopList();
//                    }
//                }
            }
            @Override
            public void onRefreshBegin(PtrFrameLayout frame) {
                currPage = 1;
                loadDataList();
            }
        });

        return ptrFrameLayout;
    }

    private void initData() {
        if (null != getArguments()){
            isForSearchResultList = getArguments().getBoolean("isForSearchResultList");
            isSearchByTag = getArguments().getBoolean("isSearchByTag");
            tagInfo = (HotTagBean) getArguments().getSerializable("tagInfo");
            keywords =  getArguments().getString("keywords");
        }
    }



    @Override
    public void onResume() {
        super.onResume();
        loadDataList();
    }

    private void loadDataList() {
        if (isForMainVideoList){
            loadDataListForMainVideoList();
        }
        if (isForCollect){
            //加载本地存储的收藏列表
        }
        if (isForSearchResultList){
            loadDataListForSearchVideoResultList();
        }
    }

    /////////首页
    public void changeSortTypeOfMainVideoList(boolean isSortHotSelected){
        sortPostValue =  isSortHotSelected?"hot":"latest";
        currPage = 1;
        loadDataListForMainVideoList();
    }

    private void loadDataListForMainVideoList() {
        if (null == mService){
            mService = ApiManager.getInstence().createServiceFrom(VideoListService.class);
        }
        Observable<MoviesListResult> call = mService.getMoviesList(currPage,sortPostValue,"all");
        getDataResult(call);
    }

    ////////搜索结果页
    private void loadDataListForSearchVideoResultList() {
        if (null == mService){
            mService = ApiManager.getInstence().createServiceFrom(VideoListService.class);
        }
        if (isSearchByTag){
            //TODO 根据接口写出对应请求
//            Observable<MoviesListResult> call = mService.getMovieSearchResultList(currPage,"all","");
//            getDataResult(call);
        }else{
            Observable<MoviesListResult> call = mService.getMovieSearchResultList(currPage,"all",keywords);
            getDataResult(call);
        }
    }

    private void getDataResult(Observable<MoviesListResult> call) {
        call.subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new BaseSubscriber<MoviesListResult>(getActivity()) {
                    @Override
                    public void onNext(MoviesListResult moviesListResult) {
                        if (null != moviesListResult){
                            mMoviesListResult = moviesListResult;
                            initDataResult();
                        }
                    }
                });
    }

    private void initDataResult() {
        if (mMoviesListResult.errorcode == 0){
            changeDataList(mMoviesListResult.ret);
        }else{
            ToastUtils.showToast(getActivity().getApplicationContext(),
                    "请求失败："+mMoviesListResult.errormessage);
        }
        ptrFrameLayout.refreshComplete();
    }


    private void changeDataList(ArrayList<MovieItemBean> data){
        dataList.clear();
        if (null != data){
            dataList.addAll(data);
        }
        mAdapter.notifyDataSetChanged();
    }


}
