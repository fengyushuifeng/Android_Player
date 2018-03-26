package com.ledongli.player.utils;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Handler;
import android.os.Message;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.ledongli.player.R;
import com.ledongli.player.net.ApiManager;
import com.ledongli.player.utils.downloadfile.ProgressListener;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * Created by 嘟伊 on 2016/12/14.
 */
public class UpdateManager {
    private Context mContext; //上下文

    private String apkUrl ; //apk下载地址
    private static final String savePath = MyConstant.BASE_DIR+"/updateAPK/"; //apk保存到SD卡的路径
    private static final String saveFileName = savePath + "ldl_update.apk"; //完整路径名

    private ProgressBar mProgress; //下载进度条控件
    private TextView tvProgress; //下载进度条控件
    private static final int DOWNLOADING = 1; //表示正在下载
    private static final int DOWNLOADED = 2; //下载完毕
    private static final int DOWNLOAD_FAILED = 3; //下载失败
    private static final int DOWNLOAD_SAVE_FAILED = 4; //下载完成，存储失败
    private int progress; //下载进度
    private long speed;//下载速度
    private boolean cancelFlag = false; //取消下载标志位

    private int serverVersion = 0; //从服务器获取的版本号
    private int clientVersion = 0; //客户端当前的版本号
    private String new_version_name = ""; //新版本名
//    private String updateDescription = ""; //更新内容描述信息
    private boolean forceUpdate = false; //是否强制更新

    private AlertDialog alertDialog1, alertDialog2; //表示提示对话框、进度条对话框

    /**
     * @param context activity
     */
    public UpdateManager(Context context, int clientVersion,
                         String apkDownloadUrl, int serverVersion, String new_version_name, boolean forceUpdate) {
        this.mContext = context;
        this.apkUrl = apkDownloadUrl;
        this.forceUpdate = forceUpdate;
        this.new_version_name = new_version_name;
        this.serverVersion = serverVersion;
        this.clientVersion = clientVersion;
    }

    /** 显示更新对话框 */
    public void showNoticeDialog() {
        //如果版本最新，则不需要更新
        if (serverVersion <= clientVersion)
            return;
        if (null == alertDialog1){
            AlertDialog.Builder dialog = new AlertDialog.Builder(mContext);
            dialog.setTitle("版本更新");
            dialog.setMessage("检测到APP有新的版本:"+new_version_name+"\n"+( forceUpdate?"本版本为强制更新，更新后方可使用APP,敬请谅解！":"为保证程序的正常运行，请下载安装新版本。" ));
            dialog.setPositiveButton("现在更新", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface arg0, int arg1) {
                    arg0.dismiss();
                    showDownloadDialog();
                }
            });
            //是否强制更新
            if (forceUpdate == false) {
                dialog.setNegativeButton("稍后更新", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface arg0, int arg1) {
                        arg0.dismiss();
                    }
                });
            }
            alertDialog1  = dialog.create();
        }
        if (!alertDialog1.isShowing()){
            alertDialog1.setCancelable(false);
            alertDialog1.show();
        }
    }

    /** 显示进度条对话框 */
    private void showDownloadDialog() {
        AlertDialog.Builder dialog = new AlertDialog.Builder(mContext);
        dialog.setTitle("正在更新");
        final LayoutInflater inflater = LayoutInflater.from(mContext);
        View v = inflater.inflate(R.layout.update_progress, null);
        mProgress = (ProgressBar) v.findViewById(R.id.update_progress);
        tvProgress = (TextView)v.findViewById(R.id.update_progresstext);
        tvProgress.setText("当前进度：0%，下载速度：0kb/s");
        dialog.setView(v);
        //如果是强制更新，则不显示取消按钮
        if (forceUpdate == false) {
            dialog.setNegativeButton("取消", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface arg0, int arg1) {
                    arg0.dismiss();
                    cancelFlag = false;
                }
            });
        }
        alertDialog2  = dialog.create();
        alertDialog2.setCancelable(false);
        alertDialog2.show();

        //下载apk
        downloadAPK();
    }

    long lastSecondSystemMillTime = 0;//上一秒的起始毫秒数
    long lastSecondCurrentBytes = 0;//上一秒的大小
    /** 下载apk的线程 */
    private void downloadAPK() {
        ApiManager.downloadFileProgress(apkUrl,new ProgressListener() {
            @Override
            public void onProgress(long currentBytes, long contentLength, boolean done) {
                progress = (int)(currentBytes * 100  / contentLength );
                if (lastSecondSystemMillTime ==0 ){
                    lastSecondSystemMillTime = System.currentTimeMillis();
                    lastSecondCurrentBytes = currentBytes;
                }else{
                    long mills = System.currentTimeMillis() - lastSecondSystemMillTime;
                    if ( mills>1000){
                        speed = ( currentBytes -lastSecondCurrentBytes );
                        lastSecondSystemMillTime = System.currentTimeMillis();
                        lastSecondCurrentBytes = currentBytes;
                    }
                }
                //更新进度
                mHandler.sendEmptyMessage(DOWNLOADING);
            }
        }, new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                boolean isWhiteSuccess = writeResponseBodyToDisk(response.body());
                if (isWhiteSuccess){
                    //下载完成通知安装
                    mHandler.sendEmptyMessage(DOWNLOADED);
                }else{
                    //下载完成通知安装
                    mHandler.sendEmptyMessage(DOWNLOAD_SAVE_FAILED);
                }
            }
            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                //下载完成通知安装
                mHandler.sendEmptyMessage(DOWNLOAD_FAILED);
            }
        });
    }

    /** 更新UI的handler */
    private Handler mHandler = new Handler(new Handler.Callback() {
        @Override
        public boolean handleMessage(Message msg) {
            switch (msg.what) {
                case DOWNLOADING:
                    mProgress.setProgress(progress);
                    tvProgress.setText("当前进度："+progress+"%，下载速度："+speed/1024+"kb/s");
                    break;
                case DOWNLOADED:
                    if (alertDialog2 != null)
                        alertDialog2.dismiss();
                    installAPK();
                    break;
                case DOWNLOAD_FAILED:
                    Toast.makeText(mContext, "网络断开，请稍候再试", Toast.LENGTH_LONG).show();
                    break;
                case DOWNLOAD_SAVE_FAILED:
                    Toast.makeText(mContext, "保存到本地失败，请确认您已授予本应用存储权限~", Toast.LENGTH_LONG).show();
                    break;
                default:
                    break;
            }
            return false;
        }
    });

    /** 下载完成后自动安装apk */
    private void installAPK() {
        File apkFile = new File(saveFileName);
        if (!apkFile.exists()) {
            return;
        }
        Intent intent = new Intent(Intent.ACTION_VIEW);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        intent.setDataAndType(Uri.parse("file://" + apkFile.toString()), "application/vnd.android.package-archive");
        mContext.startActivity(intent);
    }

    private boolean writeResponseBodyToDisk(ResponseBody body) {
        try {
            File file = new File(savePath);
            if(!file.exists()){
                boolean  isOk = file.mkdirs();
                if (!isOk){
                    ToastUtils.showToast(mContext,"创建目录失败~");
                }
            }
//            String apkFileName = saveFileName;
            File apkFile = new File(saveFileName);
            InputStream inputStream = null;
            OutputStream outputStream = null;
            try {
                byte[] fileReader = new byte[4096];

                long fileSize = body.contentLength();
                long fileSizeDownloaded = 0;

                inputStream = body.byteStream();
                outputStream = new FileOutputStream(apkFile);

                while (true) {
                    int read = inputStream.read(fileReader);

                    if (read == -1) {
                        break;
                    }

                    outputStream.write(fileReader, 0, read);

                    fileSizeDownloaded += read;

//                    Log.d(TAG, "file download: " + fileSizeDownloaded + " of " + fileSize);
                }

                outputStream.flush();

                return true;
            } catch (IOException e) {
                return false;
            } finally {
                if (inputStream != null) {
                    inputStream.close();
                }

                if (outputStream != null) {
                    outputStream.close();
                }
            }
        } catch (IOException e) {
            return false;
        }
    }

}