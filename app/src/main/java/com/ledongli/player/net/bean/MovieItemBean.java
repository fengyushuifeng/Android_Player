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
    public int duration;//单位：秒
    public long onshowtime;//时间？1402156800，单位秒
    public long createtime;
    public long updatetime;
    public String playurl;//播放地址
    public String actor;
    public String movietag;//假设为“aaa;bbb;ccc;”tag数据以分号隔开

    public String getCoverimageUrl(){
        int index = coverimage.indexOf(";");
        if (index != -1){
            //数据异常的处理，应返回一张图片，实际上返回了两张图片且以;隔开的时候，截取第一张
            return coverimage.substring(0,index);
        }
        return coverimage;
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
        String result=""+duration;
        if (duration>0){
            int min = duration / 60;
            return  min +"分钟";
//            return min / 60 +"小时" + min % 60 +"分钟";
        }
        return result;
    }

    public String[] getTags(){
        String[] result = new String[]{};
        if (!TextUtils.isEmpty(movietag)){
            result = movietag.split(";");
        }
        return result;
    }

}
