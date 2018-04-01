package com.ledongli.player.net.bean;

import android.text.TextUtils;

import java.io.Serializable;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

/**
 * 功能描述：
 * Created by zpp_zoe on 2018/2/10.
 */

public class MovieItemBean implements Serializable{

    public int id;
    public String uuid;
    public String coverimage;//图片
    public String title;//电影名字
    public int duration;//单位：秒  20170401-修改为单位分钟
    public long onshowtime;//时间？1402156800，单位秒
    public long createtime;
    public long updatetime;
    public String playurl;//播放地址
    public String actor;
    public String movietag;//假设为“aaa;bbb;ccc;”tag数据以分号隔开

    public String getCoverimageUrl(){

        if (coverimage.indexOf(";") < 0) {

            return  coverimage;
        }

        return coverimage.substring(0,coverimage.indexOf(";"));
    }

    public String getOnShowTimeStr(){
        String result=""+onshowtime;
        if (onshowtime>0){
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy.MM.dd");
            result = sdf.format(new Date(onshowtime * 1000));
        }
        return result;
    }
    public String getDurationStr(){
        return  duration +"分钟";
    }

    public String[] getTags(){
        String[] result = new String[]{};
        if (!TextUtils.isEmpty(movietag)){
            result = movietag.split(";");
        }
        return result;
    }

}
