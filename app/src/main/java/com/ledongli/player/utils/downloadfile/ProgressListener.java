package com.ledongli.player.utils.downloadfile;

/**
 * 功能描述：
 * Created by zpp_zoe on 2017/12/18.
 */
public interface ProgressListener {
    void onProgress(long currentBytes, long contentLength, boolean done);
}
