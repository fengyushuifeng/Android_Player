package com.ledongli.player.net;

import retrofit2.http.GET;

import rx.Observable;

/**
 * Created by Gemini on 2018/3/25.
 */

public interface VersionService {


   // http://47.104.137.0:8080/v1/advertise

    @GET("version")
    Observable < BaseResult < VersionServiceResult > > getVersion();

}
