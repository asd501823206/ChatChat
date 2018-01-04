package com.chatchat.huanxin.chatapp.module.splash.view.ui.activity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.os.PersistableBundle;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import com.chatchat.huanxin.chatapp.R;
import com.chatchat.huanxin.chatapp.common.view.activity.BaseActivity;
import com.chatchat.huanxin.chatapp.module.chat.view.ui.activity.ChatActivity;
import com.chatchat.huanxin.chatapp.module.login.view.ui.activity.LoginActivity;
import com.chatchat.huanxin.chatapp.utils.SpHelper;

/**
 * 启动页
 * Created by dengzm on 2017/12/11.
 */

public class SplashActivity extends Activity {
    private static final String TAG = "SplashActivity";

    private TextView mTvPass;
    private Handler handler;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.layout_activity_splash);
        Log.d(TAG, "onCreate: -----splashactivity-----");
        handler = new Handler(callback);
        mTvPass = findViewById(R.id.tv_pass);
        mTvPass.setText("跳转 3");
        mTvPass.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                gotoNextActivity();
            }
        });
        Message msg = handler.obtainMessage();
        msg.what = 2;
        handler.sendMessageDelayed(msg, 1000);
    }

    private Handler.Callback callback = new Handler.Callback() {
        @Override
        public boolean handleMessage(Message msg) {
            Log.d(TAG, "handleMessage: callback="+msg.what);
            int what = msg.what;
            if (what == 0) {
                gotoNextActivity();
            }else {
                mTvPass.setText("跳转 " + what);
                what --;
                if (handler != null){
                    Message message = handler.obtainMessage();
                    message.what = what;
                    handler.sendMessageDelayed(message, 1000);
                }

            }
            return true;
        }
    };

    private void gotoNextActivity() {
        handler = null;
        //boolean isLogin = SpHelper.getInstance().readBooleanPreference("isLogin", false);
        boolean isLogin = false;
        Intent intent = new Intent();
        if (isLogin) {
            intent.setClass(this, ChatActivity.class);
        }else {
            intent.setClass(this, LoginActivity.class);
        }
        startActivity(intent);
        this.finish();
    }
}
