package com.ledongli.player.net;

import retrofit2.http.GET;
import retrofit2.http.Path;
import retrofit2.http.Query;
import rx.Observable;

/**
 * 功能描述：
 * Created by zpp_zoe on 2018/2/13.
 */

public interface VideoDetailService {

    /**
     * 根据影片列表返回的uuid获取影片详情
     * @param uuid
     * @return
     */
    @GET("movie/moviedetail/{uuid}")
    Observable<BaseResult<VideoDetailResult>> getVideoDetail(
            @Path("uuid") String uuid
    );
}
