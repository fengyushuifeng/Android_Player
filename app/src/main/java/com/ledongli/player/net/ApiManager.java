package com.ledongli.player.net;

import android.app.Activity;
import android.content.Context;
import android.text.TextUtils;

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

    //////////////////////////
//    public static final String MD5KEY = "sld_ddb";
//    public static String getMD5Result(String string){
//        if (TextUtils.isEmpty(string)) {
//            return "";
//        }
//        MessageDigest md5 = null;
//        try {
//            md5 = MessageDigest.getInstance("MD5");
//            byte[] bytes = md5.digest((string+MD5KEY).getBytes());
//            String result = "";
//            for (byte b : bytes) {
//                String temp = Integer.toHexString(b & 0xff);
//                if (temp.length() == 1) {
//                    temp = "0" + temp;
//                }
//                result += temp;
//            }
//            return result;
//        } catch (NoSuchAlgorithmException e) {
//            e.printStackTrace();
//        }
//        return "";
//    }

//    /**
//     * 统一弹出返回服务器返回的Json结果的错误信息
//     * @param act
//     * @param data
//     * @param apiName 为null时不展示toast
//     * @param <T>
//     * @return
//     */
//    //须要验证token的接口，若返回1004，则跳转登陆
//    public <T> T judgeBaseMessage(Activity act, BaseMessage<T> data, String apiName,boolean isForTokenOverDateOrWrong){
//        if (null == act ){
//            return null;
//        }
//        if (act instanceof BaseActivity){
//            return judgeBaseMessage((BaseActivity) act,data,apiName,isForTokenOverDateOrWrong);
//        }
//        return judgeBaseMessage(act.getApplicationContext(),data,apiName);
//
//    }
//    public <T> T judgeBaseMessage(BaseActivity act, BaseMessage<T> data, String apiName,boolean isForTokenOverDateOrWrong){
//        if (null == act){
//            return null;
//        }
//        if (null != data && data.getCode() == RESULTCode_ReLogin){
//            if (isForTokenOverDateOrWrong){
//                //token过期，则检测并登录
//                SPUtils.getInstance().clearLoginInfo(act.getApplicationContext());
//                ActivityUtils.checkIsLoginAndGoLogin(act);
//            }
//        }
//        return judgeBaseMessage(act.getApplicationContext(),data,apiName);
//    }
//    public <T> T judgeBaseMessage(Context ctx, BaseMessage<T> data, String apiName){
//        if (null == data ){
//            ToastUtils.showToast(ctx,"网络请求，获取数据失败");
//            return null;
//        }else if (data.getCode() != RESULTCode_Success){
//            if (StringUtils.checkIsNotNullStr(apiName) ||  MyConstant.isShowSysText){//api为null时不展示toast
//                ToastUtils.showToast(ctx,apiName+" 失败："+data.getCode()+"，"+data.getMessage());
//            }
//            return null;
//        }
//        return data.getData();
//    }

}
