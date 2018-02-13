package com.ledongli.player.net;

import com.ledongli.player.net.bean.MoviesListResult;

import okhttp3.RequestBody;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.Headers;
import retrofit2.http.POST;
import retrofit2.http.Query;
import rx.Observable;

/**
 * 功能描述：
 * Created by zpp_zoe on 2018/2/10.
 */

public interface VideoListService {

    /**
     * 首页-获取影片列表
     * @param currPage p:页码编号 暂定每页10条结果
     * @param sort r:排序方法 hot/latest
     * @param type c:all/om/rh/mh
     * @return
     */
    @GET("movie/movielist")
    Observable<MoviesListResult> getMoviesList(
            @Query("p") int currPage,
            @Query("r") String sort,
            @Query("c") String type);


    /**
     * get 影片搜素，数据结果和影片列表相同
     * @param currPage p:页码编号 暂定每页10条结果
     * @param type c:all/om/rh/mh
     * @param keywords q:搜索词 支持在分类下的搜索
     * @return
     */
    @GET("movie/moviesearch")
    Observable<MoviesListResult> getMovieSearchResultList(
            @Query("p") int currPage,
            @Query("c") String type,
            @Query("q") String keywords);

}
