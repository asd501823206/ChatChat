package com.chatchat.huanxin.chatapp.common.net.exception;

import java.io.IOException;

/**
 * Class For
 * Package Name com.chatchat.huanxin.chatapp.common.net.exception
 * Created by dengzm on 2018/1/9.
 */

public class EmptyBodyException extends IOException {
    public EmptyBodyException() {
        super("response body is null");
    }
}
