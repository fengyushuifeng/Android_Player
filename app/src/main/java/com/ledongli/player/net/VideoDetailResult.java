package com.ledongli.player.net;

import android.text.TextUtils;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

/**
 * 功能描述：
 * Created by zpp_zoe on 2018/2/13.
 */

public class VideoDetailResult {

    public int id;
    public String uuid;
    public String coverimage;//图片
    public String title;//电影名字
    public int duration;//单位：秒,2017-0401-单位修改为分钟
    public long onshowtime;//时间？1402156800，单位秒
    public long createtime;
    public long updatetime;
    public String playurl;//播放地址
    public ArrayList<MovieActorBean> actor;
    public ArrayList<MovieTagBean> movietag;//假设为“aaa;bbb;ccc;”tag数据以分号隔开

    public String getCoverimageUrl(){
        int index = coverimage.indexOf(";");
        if (index == -1) return coverimage;
        return coverimage.substring(0,index);
    }

    public String getOnShowTimeStr(){
        String result=""+onshowtime;
        if (onshowtime>0){
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy.MM.dd");
            result = sdf.format(new Date(onshowtime * 1000));
        }
        return "发行日期："+result;
    }
    public String getDurationStr(){
        return  "片长："+duration +"分钟";
    }

    public String getActorStrs(){
        String result = "";
        if (null != actor){
            for (int i=0;i<actor.size();i++){
                result += actor.get(i).name+" ";
            }
        }
        return "演员："+result;
    }

}
