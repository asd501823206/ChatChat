package com.chatchat.huanxin.chatapp.common.net.exception;

import java.io.IOException;

/**
 * Class For wrong response code ( code != 600 )
 * Package Name com.chatchat.huanxin.chatapp.common.net.exception
 * Created by dengzm on 2018/1/9.
 */

public class ErrorCodeException extends IOException {
    public ErrorCodeException(int code) {
        super("response failed . response code is "+code);
    }
}
