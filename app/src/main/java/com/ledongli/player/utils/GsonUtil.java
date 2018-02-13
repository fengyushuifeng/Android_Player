package com.ledongli.player.utils;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.ledongli.player.net.bean.MovieItemBean;

import java.util.ArrayList;

/**
 * 功能描述：
 * Created by zpp_zoe on 2017/12/1.
 */

public class GsonUtil {

    public static ArrayList<MovieItemBean> getMovieListFormJsonStr(String jsonStr){
        ArrayList<MovieItemBean> result ;
        try {
            Gson gson = new Gson();
            result = gson.fromJson(jsonStr,new TypeToken<ArrayList<MovieItemBean>>(){}.getType());
        }catch (Exception e){
            e.printStackTrace();
            return new ArrayList<>();
        }
        return result;
    }

}
