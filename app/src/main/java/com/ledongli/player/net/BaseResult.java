package com.ledongli.player.net;

/**
 * 功能描述：
 * Created by zpp_zoe on 2018/2/13.
 */

public class BaseResult<T> {

    public int errorcode;
    public String errormesaage;
    public T ret;

    public int getErrorcode() {
        return errorcode;
    }

    public void setErrorcode(int errorcode) {
        this.errorcode = errorcode;
    }

    public String getErrormesaage() {
        return errormesaage;
    }

    public void setErrormesaage(String errormesaage) {
        this.errormesaage = errormesaage;
    }

    public T getRet() {
        return ret;
    }

    public void setRet(T ret) {
        this.ret = ret;
    }
}
