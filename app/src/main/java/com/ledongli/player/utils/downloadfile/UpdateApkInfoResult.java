package com.ledongli.player.utils.downloadfile;

/**
 * 功能描述：
 * Created by zpp_zoe on 2017/12/18.
 */

public class UpdateApkInfoResult {
//    public UpdateApkInfoBean app_info;

//    public String version_code;
//    public int version_id;//版本号
//    public int type;//是否强制更新 1否 2是
//    public String apk_url;
//    public String description;//带换行
//
//    public boolean isForceUpdate(){
//        return type == 2;
//    }

    public String version_name;//版本名称，用于展示
    public int version_num;//版本号，用于比较
    public String apk_url;//最新版本链接
    public String code;//废弃
    public String download;//废弃
}
