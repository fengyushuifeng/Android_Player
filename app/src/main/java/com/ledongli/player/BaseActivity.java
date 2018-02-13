package com.ledongli.player;

import android.Manifest;
import android.annotation.TargetApi;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.provider.Settings;
import android.support.annotation.DrawableRes;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.KeyEvent;

import com.ledongli.player.utils.ToastUtils;
import com.ledongli.player.view.ILoadingDialogListener;
import com.ledongli.player.view.LoadingDialog;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;

/**
 * 功能描述：
 * Created by zpp_zoe on 2017/11/13.
 */

public class BaseActivity extends AppCompatActivity implements ILoadingDialogListener {

    ////////////////////弹框//////////////////
    private LoadingDialog mLoadingDialog;

    @Override
    public void setTips(String tips) {
        if (mLoadingDialog == null ){
            mLoadingDialog = new LoadingDialog(this);
        }
        mLoadingDialog.setTvTips(tips);
    }

    @Override
    public void setIcon(@DrawableRes int icon) {
        if (mLoadingDialog == null ){
            mLoadingDialog = new LoadingDialog(this);
        }
        mLoadingDialog.setIcon(icon);
    }

    public void showDialog(String tag){//加入请求tag,在该tag未清除完时不取消dialog的展示

    }

    @Override
    public void showDialog() {
        if (mLoadingDialog == null ){
            mLoadingDialog = new LoadingDialog(this);
        }
        mLoadingDialog.show();
    }

    @Override
    public void dimissDialog() {
        if (mLoadingDialog == null ){
            Log.e("BaseActivity","未初始化加载框");
            return;
        }
        if (mLoadingDialog.isShowing()){
            mLoadingDialog.dismiss();
        }
    }


    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if ((keyCode == KeyEvent.KEYCODE_BACK)) {//监听返回键
            return onBackKeyPress();
        }else {
            return super.onKeyDown(keyCode, event);
        }
    }

    //监听返回键
    protected boolean onBackKeyPress(){
        finish();
        return true;
    }

    /////////

    public void setNeedPermissions(String[] needPermissions) {
        this.needPermissions = needPermissions;
    }

    /**
     * 需要进行检测的权限数组
     */
    protected String[] needPermissions = {
    };
    protected String[] permissionPosition = {
            Manifest.permission.ACCESS_COARSE_LOCATION,
            Manifest.permission.ACCESS_FINE_LOCATION,
            Manifest.permission.WRITE_EXTERNAL_STORAGE,
            Manifest.permission.READ_EXTERNAL_STORAGE,
            Manifest.permission.READ_PHONE_STATE
    };
    private String[] permissionTakePhoto = {
            Manifest.permission.CAMERA,
            Manifest.permission.WRITE_EXTERNAL_STORAGE,
            Manifest.permission.READ_EXTERNAL_STORAGE
    };
    private String[] permissionCallPhone = {
            Manifest.permission.CALL_PHONE
    };

    private static final int PERMISSON_REQUESTCODE = 0;

    public void setNeedCheck(boolean needCheck) {
        isNeedCheck = needCheck;
    }

    public boolean isNeedCheck() {
        return isNeedCheck;
    }

    /**
     * 判断是否需要检测，防止不停的弹框
     */
    private boolean isNeedCheck = false;

    public boolean isNotSecondAct = false;//SecondActivity的在子类中进行处理

    @Override
    protected void onResume() {
        super.onResume();
        if (Build.VERSION.SDK_INT >= 23
                && getApplicationInfo().targetSdkVersion >= 23) {
            if (isNeedCheck) {
                checkPermissions(needPermissions);
            }
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
    }

    public boolean checkPermissionsOfCallPhone(){
        needPermissions = permissionCallPhone;
        return checkPermissionsOfList();
    }
    public boolean checkPermissionsOfTakePhoto(){
        needPermissions = permissionTakePhoto;
        return checkPermissionsOfList();
    }
    public boolean checkPermissionsOfPosition(){
        needPermissions = permissionPosition;
        return checkPermissionsOfList();
    }
    private boolean checkPermissionsOfList(){
        List<String> needRequestPermissonList = findDeniedPermissions(needPermissions);
        if (null == needRequestPermissonList || needRequestPermissonList.size()==0){
            return true;
        }
        isNeedCheck = true;
        checkPermissions(needPermissions);
        return false;
    }
    /**
     * 请求权限
     * @param permissions
     * @since 2.5.0
     *
     */
    private void checkPermissions(String... permissions) {
        if (null != permissions && permissions.length>0){
            try {
                if (Build.VERSION.SDK_INT >= 23
                        && getApplicationInfo().targetSdkVersion >= 23) {
                    List<String> needRequestPermissonList = findDeniedPermissions(permissions);
                    if (null != needRequestPermissonList
                            && needRequestPermissonList.size() > 0) {
                        String[] array = needRequestPermissonList.toArray(new String[needRequestPermissonList.size()]);
                        Method method = getClass().getMethod("requestPermissions",
                                new Class[]{String[].class, int.class});

                        method.invoke(this, array, PERMISSON_REQUESTCODE);
                    }
                }
            } catch (Throwable e) {
            }
        }
    }

    /**
     * 获取权限集中需要申请权限的列表
     *
     * @param permissions
     * @return
     * @since 2.5.0
     *
     */
    public List<String> findDeniedPermissions(String[] permissions) {
        List<String> needRequestPermissonList = new ArrayList<String>();
        if (Build.VERSION.SDK_INT >= 23
                && getApplicationInfo().targetSdkVersion >= 23){
            try {
                for (String perm : permissions) {
                    Method checkSelfMethod = getClass().getMethod("checkSelfPermission", String.class);
                    Method shouldShowRequestPermissionRationaleMethod = getClass().getMethod("shouldShowRequestPermissionRationale",
                            String.class);
                    if ((Integer)checkSelfMethod.invoke(this, perm)!= PackageManager.PERMISSION_GRANTED
                            || (boolean)shouldShowRequestPermissionRationaleMethod.invoke(this, perm)) {
                        needRequestPermissonList.add(perm);
                    }
                }
            } catch (Throwable e) {

            }
        }
        return needRequestPermissonList;
    }

    /**
     * 检测是否所有的权限都已经授权
     * @param grantResults
     * @return
     * @since 2.5.0
     *
     */
    public boolean verifyPermissions(int[] grantResults) {
        for (int result : grantResults) {
            if (result != PackageManager.PERMISSION_GRANTED) {
                return false;
            }
        }
        return true;
    }


    @TargetApi(23)
    public void onRequestPermissionsResult(int requestCode,
                                           String[] permissions, int[] paramArrayOfInt) {
        if (requestCode == PERMISSON_REQUESTCODE) {
            if (!verifyPermissions(paramArrayOfInt)) {
                showMissingPermissionDialog();
                isNeedCheck = false;
            }
        }
    }
    AlertDialog mAlertDialog;
    /**
     * 显示提示信息
     *
     * @since 2.5.0
     *
     */
    private void showMissingPermissionDialog() {
        if (null == mAlertDialog){
            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setTitle("请求权限");
            builder.setMessage("请您对应用须要的权限进行授权，以获取更好的服务~");
            // 拒绝, 退出应用
            builder.setNegativeButton("取消",
                    new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            ToastUtils.showToast(getApplicationContext(),"您取消了对应用的授权，将对您的使用造成一定影响~");
//                        finish();
                        }
                    });
            builder.setPositiveButton("设置",
                    new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            startAppSettings();
                        }
                    });
            builder.setCancelable(false);
            mAlertDialog = builder.create();
        }
        if (null != mAlertDialog && !mAlertDialog.isShowing()){
            mAlertDialog.show();
        }
    }

    /**
     *  启动应用的设置
     *
     * @since 2.5.0
     *
     */
    private void startAppSettings() {
        Intent intent = new Intent(
                Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
        intent.setData(Uri.parse("package:" + getPackageName()));
        startActivity(intent);
    }
}
