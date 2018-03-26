package com.ledongli.player.utils.downloadfile;

/**
 * 功能描述：
 * Created by zpp_zoe on 2017/12/18.
 */

public class UpdateApkInfoBean {
    public String version_code;
    public int version_id;
    public int type;//是否强制更新 1否 2是
    public String apk_url;
    public String description;//带换行

    public boolean isForceUpdate(){
        return type == 2;
    }
}
