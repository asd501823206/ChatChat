package com.chatchat.huanxin.chatapp.common.view.activity;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.os.Bundle;
import android.os.PersistableBundle;
import android.support.annotation.Nullable;
import android.view.MotionEvent;
import android.view.inputmethod.InputMethodManager;

/**
 * 基类
 * Created by dengzm on 2017/12/11.
 */

@SuppressLint("Registered")
public class BaseActivity extends Activity{

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState, @Nullable PersistableBundle persistentState) {
        super.onCreate(savedInstanceState, persistentState);
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        if (null != this.getCurrentFocus()) {
            //点击空白位置 隐藏软键盘
            InputMethodManager manager = (InputMethodManager) getSystemService(INPUT_METHOD_SERVICE);
            if (manager != null) {
                return manager.hideSoftInputFromWindow(this.getCurrentFocus().getWindowToken(), 0);
            }
        }

        return super.onTouchEvent(event);
    }
}
