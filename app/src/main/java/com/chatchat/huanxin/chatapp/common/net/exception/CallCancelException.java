package com.chatchat.huanxin.chatapp.common.net.exception;

/**
 * Class 取消网络请求
 * Created by dengzm on 2018/1/9.
 */

public class CallCancelException extends Exception{

    public CallCancelException() {
        super("Call is canceled by user");
    }
}
