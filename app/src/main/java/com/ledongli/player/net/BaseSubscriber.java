package com.ledongli.player.net;

import android.app.Activity;

import com.google.gson.JsonSyntaxException;
import com.ledongli.player.BaseActivity;
import com.ledongli.player.utils.ActivityUtils;
import com.ledongli.player.utils.ToastUtils;

import java.lang.ref.WeakReference;
import java.net.ConnectException;
import java.net.SocketTimeoutException;
import java.util.concurrent.TimeoutException;

import retrofit2.HttpException;
import rx.Subscriber;

/**
 * 功能描述：
 * 添加弹框
 * 添加网络请求错误提示
 * Created by zpp_zoe on 2017/10/27.
 */

public abstract class BaseSubscriber<T> extends Subscriber<T>{

    private WeakReference<Activity> wAct;
    public String error;
    public OnRequestEndCallBack callback;
    public interface OnRequestEndCallBack{
        //用于请求结束时，ptr的回收
        void onCallBackEnd(int end_type, String error);//1 请求成功，来自onCompleted(),0 请求成功，来自onError
    }

    public BaseSubscriber() {
    }
    //展示dialog使用
    public BaseSubscriber(Activity act) {
        if (null != act && (act instanceof BaseActivity)){
            this.wAct = new WeakReference<>(act);
        }
    }

    @Override
    public void onStart() {
        super.onStart();
        showLoadingDialog();
    }

    @Override
    public void onCompleted() {
        dimissLoadingDialog();
        if (callback !=null ){
            callback.onCallBackEnd(1,error);
        }
    }

    @Override
    public void onError(Throwable e) {
        e.printStackTrace();
        int code = 100;
        if (e instanceof TimeoutException ) {
            error = "网络请求超时";
            code = 101;
        }else if(e instanceof SocketTimeoutException){
            error = "服务器响应超时";
            code = 102;
        }else if(e instanceof ConnectException){
            error = "网络链接失败，请检查您的网络";
            code = 103;
        }else if (e instanceof JsonSyntaxException){
            error = "返回数据格式有误";
            code = 104;
            //假如导致这个异常触发的原因是服务器的问题，那么应该让服务器知道，所以可以在这里
            //选择上传原始异常描述信息给服务器
        }else if (e instanceof HttpException){
            code = 105;
            error = "网络请求失败，"+((HttpException) e).getMessage();
        }else {
            error = "网络请求异常，类型："+e.getClass().getName();
        }
        if (null!= wAct && ActivityUtils.isActRunning(wAct.get())){
            ToastUtils.showToast(wAct.get(),"网络请求失败："+code);
        }
        dimissLoadingDialog();
        if (callback !=null ){
            callback.onCallBackEnd(0,"");
        }
    }

    protected void showLoadingDialog() {
        if (null!= wAct && ActivityUtils.isActRunning(wAct.get())){
            if (wAct.get() instanceof BaseActivity){
                ((BaseActivity)wAct.get()).showDialog();
            }
        }
    }

    protected void dimissLoadingDialog() {
        if (null!= wAct && ActivityUtils.isActRunning(wAct.get())){
            if (wAct.get() instanceof BaseActivity){
                ((BaseActivity)wAct.get()).dimissDialog();
            }
        }
    }

}
