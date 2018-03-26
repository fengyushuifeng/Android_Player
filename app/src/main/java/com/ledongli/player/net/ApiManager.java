package com.ledongli.player.net;

import android.app.Activity;
import android.content.Context;
import android.text.TextUtils;

import com.ledongli.player.utils.downloadfile.ProgressListener;
import com.ledongli.player.utils.downloadfile.ProgressResponseBody;
import com.ledongli.player.utils.downloadfile.UpdateApkService;

import org.json.JSONObject;

import java.io.IOException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import okhttp3.Interceptor;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.RequestBody;
import okhttp3.ResponseBody;
import retrofit2.Callback;
import retrofit2.Retrofit;
import retrofit2.adapter.rxjava.RxJavaCallAdapterFactory;
import retrofit2.converter.gson.GsonConverterFactory;

/**
 * 功能描述：
 * Created by zpp_zoe on 2017/11/1.
 */

public class ApiManager {

    private static ApiManager mApiManager;
    //获取ApiManager的单例
    public static ApiManager getInstence() {
        if (mApiManager == null) {
            synchronized (ApiManager.class) {
                if (mApiManager == null) {
                    mApiManager = new ApiManager();
                }
            }
        }
        return mApiManager;
    }
    public static final int RESULTCode_Success = 0;

    public static final String BasePHPUrl = "http://47.104.137.0:8080/v1/";

    private static final int ConnectTimeOutSeconds = 5;//请求5秒超时

//    //封装请求体
//    public RequestBody getRequestBody(Map postData){
//        String jsonPostData = new JSONObject(postData).toString();
//        if (jsonPostData ==null){
//            return null;
//        }
//        //new JSONObject里的getMap()方法就是返回一个map，里面包含了你要传给服务器的各个键值对，然后根据接口文档的请求格式，直接拼接上相应的东西就行了
//        //比如{"data":{这里面是参数}}，那就在外面拼上大括号和"data"好了
//        RequestBody requestBody = RequestBody.create(
//                MediaType.parse("Content-Type, application/json"), jsonPostData );
//        return requestBody;
//    }

    /**
     * 获取API
     * @param serviceClass
     * @param <T>
     * @return
     */
    public <T> T createServiceFrom(final Class<T> serviceClass) {
        return createServiceFrom(serviceClass,BasePHPUrl);
    }
    public <T> T createServiceFrom(final Class<T> serviceClass, Interceptor interceptor) {
        return createServiceFrom(serviceClass,BasePHPUrl,interceptor);
    }
    public <T> T createServiceFrom(final Class<T> serviceClass, String baseUrl) {
        return createServiceFrom(serviceClass,baseUrl,null);
    }
    public <T> T createServiceFrom(final Class<T> serviceClass, String baseUrl, Interceptor interceptor) {

        OkHttpClient.Builder clientBulider = new OkHttpClient().newBuilder()
                .connectTimeout(ConnectTimeOutSeconds, TimeUnit.SECONDS)
                .addInterceptor(new MyLogInterceptor());//打印请求体信息

        if(null!=interceptor){
            clientBulider.addInterceptor(interceptor);
        }
        Retrofit adapter = new Retrofit.Builder()
                .baseUrl(baseUrl)
                .client(clientBulider.build())
                .addCallAdapterFactory(RxJavaCallAdapterFactory.create()) // 添加Rx适配器
                .addConverterFactory(GsonConverterFactory.create()) // 添加Gson转换器
                .build();
        return adapter.create(serviceClass);
    }

    //////////////////////////文件下载
    public static  Retrofit.Builder retrofitBuilder = new Retrofit.Builder()
            .addCallAdapterFactory(RxJavaCallAdapterFactory.create())
//            .addConverterFactory(ScalarsConverterFactory.create())
            .addConverterFactory(GsonConverterFactory.create())
            .baseUrl(BasePHPUrl);
    private static OkHttpClient okHttpClient = new OkHttpClient.Builder().connectTimeout(10000, TimeUnit.MILLISECONDS)
            .readTimeout(10000,TimeUnit.MILLISECONDS)
            .writeTimeout(10000,TimeUnit.MILLISECONDS).build();
    public static void downloadFileProgress(String apkUrl, final ProgressListener listener, Callback<ResponseBody> callback){
        //okhttp拦截
        OkHttpClient client = okHttpClient.newBuilder().addNetworkInterceptor(new Interceptor() {
            @Override
            public okhttp3.Response intercept(Chain chain) throws IOException {
                okhttp3.Response response = chain.proceed(chain.request());
                return response.newBuilder().body(new ProgressResponseBody(response.body(),listener)).build();
            }
        }).build();
        UpdateApkService downloadRetrofit =
                retrofitBuilder.client(client).build().create(UpdateApkService.class);
        downloadRetrofit.downloadFileWithDynamicUrlSync(apkUrl).enqueue(callback);
    }

}
