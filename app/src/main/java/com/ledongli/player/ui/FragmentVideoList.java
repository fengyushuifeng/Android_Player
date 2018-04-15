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
import com.ledongli.player.utils.SPUtils;
import com.ledongli.player.utils.ToastUtils;

import java.util.ArrayList;

import cn.jzvd.JZVideoPlayer;
import in.srain.cube.views.ptr.PtrClassicDefaultFooter;
import in.srain.cube.views.ptr.PtrClassicDefaultHeader;
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
    //view
    PtrFrameLayout ptrFrameLayout;
    ListView listView;

    //listview的适配器和数据
    VideoListAdapter mAdapter;
    ArrayList<MovieItemBean> dataList = new ArrayList<>();

    /////////////区分 不同页面/////////////////////
    /////首页///////
    boolean isForMainVideoList = false;//是否是首页 = index==0
    /////搜索结果页///////
    boolean isForSearchResultList = false;//是否是展示的视频搜索结果页
//    boolean isSearchByTag = false;//是否是根据tag搜索
    //////收藏页 ////////
    boolean isForCollect = false;//是否是收藏列表 = index==1
    //post数据
    String keywords;              //仅搜索结果页使用
//    HotTagBean tagInfo;           //仅搜索结果页使用
    int currPage = 1;//从？1？开始 //搜藏页+结果收藏页
    String sortPostValue = "hot"; //搜藏页+结果收藏页


    //
    MoviesListResult mMoviesListResult;

    public FragmentVideoList setIsForCollect(boolean isForCollect) {
        this.isForMainVideoList = !isForCollect;
        this.isForCollect = isForCollect;
        return this;
    }

    //获取intent携带的数据，用于设置当前页面的展示情况
    private void initData() {
        if (null != getArguments()){
            isForSearchResultList = getArguments().getBoolean("isForSearchResultList");
//            isSearchByTag = getArguments().getBoolean("isSearchByTag");
//            tagInfo = (HotTagBean) getArguments().getSerializable("tagInfo");
            keywords =  getArguments().getString("keywords");
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInastanceState) {
        initData();
        mAdapter = new VideoListAdapter(getActivity(), dataList);
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
                //TODO 跳转到视频详情页
                Intent intent = new Intent(getActivity(),VideoDetailActivity.class);
//                intent.putExtra("MovieItemBean",dataList.get(position));
                intent.putExtra("uuid",dataList.get(position).uuid);
                startActivity(intent);
            }
        });

        PtrClassicDefaultHeader header = new PtrClassicDefaultHeader(getActivity().getApplicationContext());
        ptrFrameLayout.setHeaderView(header);
        ptrFrameLayout.addPtrUIHandler(header);
        PtrClassicDefaultFooter footer = new PtrClassicDefaultFooter(getActivity().getApplicationContext());
        ptrFrameLayout.setFooterView(footer);
        ptrFrameLayout.addPtrUIHandler(footer);
        ptrFrameLayout.setPtrHandler(new PtrDefaultHandler2() {
            @Override
            public void onLoadMoreBegin(PtrFrameLayout frame) {
                currPage++;
                loadDataList();
                //TODO 以前的分页的代码里带的逻辑，后台服务器会返回总页数，当前请求的页数，以便APP判断
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

    boolean hasNotLoadDataWhenVisiableToUser = false;//fragment创建时就可见的，用该状态为true记录，并在onResume时加载数据

    @Override
    public void setUserVisibleHint(boolean isVisibleToUser) {
        super.setUserVisibleHint(isVisibleToUser);
        //viewpager中的fragment会预加载，此处是判断fragment是否是当前展示的页面，在onCreateView方法前会被调用
        if (isVisibleToUser){
            if (null != ptrFrameLayout){
                loadDataOnVisiableToUser();
            }else{
                hasNotLoadDataWhenVisiableToUser = true;
            }
        }
    }

    private void loadDataOnVisiableToUser() {
        if (isForMainVideoList){
            //网络请求
            loadDataListForMainVideoList();
        }
        if (isForCollect){
            //TODO 加载本地存储的收藏列表
            ArrayList<MovieItemBean> movieItemBeans = SPUtils.getCollectVideoList(getActivity().getApplicationContext());
            changeDataList(movieItemBeans,"暂无收藏视频");
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        if (hasNotLoadDataWhenVisiableToUser){
            loadDataOnVisiableToUser();
        }
        if (isForSearchResultList){
            loadDataListForSearchVideoResultList();
        }
    }

    private void loadDataList() {
        if (isForMainVideoList){
            //网络请求
//            if (MyConstant.isUseLocalData){
//                //使用测试数据,展示数据
//                mMoviesListResult = new Gson().fromJson(MyConstant.MovieListResult,MoviesListResult.class);
//                initDataResult();
//            }else{
//                loadDataListForMainVideoList();
//            }
            loadDataListForMainVideoList();
        }
        if (isForCollect){
            //加载本地存储的收藏列表
            ArrayList<MovieItemBean> movieItemBeans = SPUtils.getCollectVideoList(getActivity().getApplicationContext());
            changeDataList(movieItemBeans,"暂无收藏视频");
            ptrFrameLayout.refreshComplete();
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
        Observable<MoviesListResult> call = mService.getMovieSearchResultList(currPage,"all",keywords);
        getDataResult(call);
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
//            if(MyConstant.isUseLocalData){
//                mMoviesListResult.ret.addAll(mMoviesListResult.ret);
//                mMoviesListResult.ret.addAll(mMoviesListResult.ret);
//            }
            changeDataList(mMoviesListResult.ret,"暂无相关视频");
        }else{
            ToastUtils.showToast(getActivity().getApplicationContext(),
                    "请求失败："+mMoviesListResult.errormessage);
        }
        ptrFrameLayout.refreshComplete();
    }


    private void changeDataList(ArrayList<MovieItemBean> data,String noDataToast){
        if (currPage == 1 || isForCollect){
            dataList.clear();
        }
        if (null != data && data.size()>0){
            dataList.addAll(data);
        }else{
            ToastUtils.showToast(getActivity().getApplicationContext(),noDataToast);
        }
        mAdapter.notifyDataSetChanged();
    }


}
