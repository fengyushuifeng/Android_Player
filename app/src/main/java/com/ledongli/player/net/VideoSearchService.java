package com.ledongli.player.net;

import retrofit2.http.GET;
import retrofit2.http.Query;
import rx.Observable;

/**
 * 功能描述：
 * Created by zpp_zoe on 2018/2/13.
 */

public interface VideoSearchService {

    /**
     * 获取热门TAG 热搜墙使用该接口，一次返回所有TAG不分页
     */
    @GET("movie/hottag")
    Observable<HotTagListResult> getHotTags();

}
