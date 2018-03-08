package com.ledongli.player.utils;

import android.content.Context;
import android.content.SharedPreferences;

import com.google.gson.Gson;
import com.ledongli.player.net.bean.MovieItemBean;

import java.util.ArrayList;

/**
 * 功能描述：
 * Created by zpp_zoe on 2018/2/13.
 */

public class SPUtils {

    ////////////////////////

    private static final String SPName_VideoCollectList = "VideoCollectList";
    private static final String SPKey_VideoCollectList_data = "VideoCollectList_data";
    private static final String SPKey_VideoCollectList_time = "VideoCollectList_time";


    ///////////////////////////////////////////

    public static String getCollectVideoListStr(Context ctx){
        return getStrFromSP(ctx,SPName_VideoCollectList,SPKey_VideoCollectList_data);
    }
    public static ArrayList<MovieItemBean> getCollectVideoList(Context ctx){
        String listStr = getStrFromSP(ctx,SPName_VideoCollectList,SPKey_VideoCollectList_data);
        return GsonUtil.getMovieListFormJsonStr(listStr);
    }

    public static void addCollectVideo(Context ctx,MovieItemBean movie){
        ArrayList<MovieItemBean> list = getCollectVideoList(ctx);
        if (null != list && list.size()>0){
            for (int i=0;i<list.size();i++){
                if (movie.id == list.get(i).id){
                    return;
                }
            }
        }else{
            list = new ArrayList<>();
        }
        list.add(movie);
        setCollectVideoListToSP(ctx,list);
    }

    public static boolean isVideoCollected(Context ctx,int movieId){
        String listJsonStr = getCollectVideoListStr(ctx);
        return listJsonStr.contains("\"id\":"+movieId+",");
    }
    public static void removeCollectVideo(Context ctx,int movieId){
        ArrayList<MovieItemBean> list = getCollectVideoList(ctx);
        if (null != list && list.size()>0){
            for (int i=0;i<list.size();i++){
                if (movieId == list.get(i).id){
                    list.remove(i);
                }
            }
        }
        setCollectVideoListToSP(ctx,list);
    }

    private static void setCollectVideoListToSP(Context ctx,ArrayList<MovieItemBean> list) {
        String listJsonStr = new Gson().toJson(list);
        setStrToSP(ctx,SPName_VideoCollectList,SPKey_VideoCollectList_data,listJsonStr);
    }


    ////////////////////////////////////////////
    private static void clearSPInfo(Context ctx, String spName){
        SharedPreferences sp = ctx.getSharedPreferences(spName,Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sp.edit();
        editor.clear();
        editor.commit();
    }
    private static void setStrToSP(Context ctx,String spName,String key, String value){
        SharedPreferences sp = ctx.getSharedPreferences(spName,Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sp.edit();
        editor.putString(key,value);
        editor.commit();
    }
    private static String getStrFromSP(Context ctx, String spName,String key){
        SharedPreferences sp = ctx.getSharedPreferences(spName,Context.MODE_PRIVATE);
        return sp.getString(key,"");
    }

}
