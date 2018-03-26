package com.ledongli.player.utils.downloadfile;

import com.ledongli.player.net.BaseResult;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Query;
import retrofit2.http.Url;
import rx.Observable;

/**
 * 功能描述：
 * Created by zpp_zoe on 2017/12/18.
 */
public interface UpdateApkService {

    /**
     * {
     "errormesaage": "ok",
     "errorcode": 0,
     "ret": {
     "code": "0.1",
     "download": ""
     }
     }
     * @return
     */
    @GET("version")
    Observable<BaseResult<UpdateApkInfoResult>> getViewVersionInfo();
//     * @param version_id //本地版本号，version_num
//     * @param app_type //android or ios
//    Observable<BaseResult<UpdateApkInfoResult>> getViewVersionInfo(@Query("version_id") int version_id, @Query("app_type") String app_type);

    @GET
    Call<ResponseBody> downloadFileWithDynamicUrlSync(@Url String fileUrl);
}