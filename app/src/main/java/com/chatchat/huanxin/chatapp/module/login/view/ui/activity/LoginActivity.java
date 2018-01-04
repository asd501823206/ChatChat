package com.chatchat.huanxin.chatapp.module.login.view.ui.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.util.Log;
import android.widget.Toast;

import com.chatchat.huanxin.chatapp.R;
import com.chatchat.huanxin.chatapp.module.chat.view.ui.activity.ChatActivity;
import com.chatchat.huanxin.chatapp.module.login.view.ui.LoginListener;
import com.chatchat.huanxin.chatapp.module.login.view.ui.fragment.DoLoginFragment;
import com.chatchat.huanxin.chatapp.module.login.view.ui.fragment.DoRegisterFragment;
import com.chatchat.huanxin.chatapp.utils.SpHelper;
import com.hyphenate.EMCallBack;
import com.hyphenate.chat.EMClient;
import com.hyphenate.exceptions.HyphenateException;

import java.util.Stack;

/**
 * 登录界面
 * Created by dengzm on 2017/12/12.
 */

public class LoginActivity extends FragmentActivity implements DoLoginFragment.ILoginListener, DoRegisterFragment.IRegisterListener{
    private static final String TAG = "LoginActivity";


    private Stack<Fragment> mFragmentStack = new Stack<>();

    private LoginListener.DoLoginListener doLoginListener;
    private LoginListener.DoRegisterListener doRegisterListener;


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.layout_activity_login);

        initDoLoginFragment();
    }

    private void initDoLoginFragment() {
        DoLoginFragment doLoginFragment = new DoLoginFragment();
        doLoginListener = doLoginFragment;
        showNewFragment(doLoginFragment, "doLoginFragment");
    }

    private void initDoRegisterFragment() {
        DoRegisterFragment doRegisterFragment = new DoRegisterFragment();
        doRegisterListener = doRegisterFragment;
        showNewFragment(doRegisterFragment, "doRegisterFragment");
    }

    private void showNewFragment(Fragment fragment,String tag) {
        Fragment temp =getPriviousFragment();
        if (temp != null) {
            getSupportFragmentManager().beginTransaction().hide(temp).add(R.id.fl_login_content, fragment, tag).commit();
        }else {
            getSupportFragmentManager().beginTransaction().add(R.id.fl_login_content, fragment, tag).commit();
        }

        addFragmentToStack(fragment);
    }

    private Fragment getPriviousFragment() {
        return mFragmentStack.empty() ? null : mFragmentStack.peek();
    }

    private void addFragmentToStack(Fragment fragment) {
        mFragmentStack.push(fragment);
    }

    @Override
    public void onBackPressed() {
        if (mFragmentStack.size() > 1) {
            getSupportFragmentManager().beginTransaction().remove(mFragmentStack.pop()).show(mFragmentStack.peek()).commit();
        } else {
            super.onBackPressed();
        }
    }

    /**
     * 登录界面
     * 注册和登录btn的点击回调
     */
    @Override
    public void onRegisterPressed() {
        initDoRegisterFragment();
    }

    @Override
    public void onLoginPressed(final String userName, String password) {
        EMClient.getInstance().login(userName, password, new EMCallBack() {
            @Override
            public void onSuccess() {
                SpHelper.getInstance().writeToPreferences("isLogin", true);
                SpHelper.getInstance().writeToPreferences("username", userName);
                EMClient.getInstance().groupManager().loadAllGroups();
                EMClient.getInstance().chatManager().loadAllConversations();
                Log.d(TAG, "onSuccess: login success!");
                jump2Chat();
            }

            @Override
            public void onError(int code, final String message) {
                Log.d(TAG, "onError:code= "+code+",message="+message);
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Toast.makeText(LoginActivity.this,"login failed,"+message, Toast.LENGTH_SHORT).show();
                    }
                });
            }

            @Override
            public void onProgress(int i, String s) {

            }
        });
    }

    /**
     * 跳转到聊天界面
     */
    private void jump2Chat() {
        Intent intent = new Intent(this, ChatActivity.class);
        startActivity(intent);
    }

    @Override
    public void onRegisterPressed(String username, String password) {
        try {
            EMClient.getInstance().createAccount(username, password);
        } catch (HyphenateException e) {
            e.printStackTrace();
            Toast.makeText(this, "账号有误，请重新输入", Toast.LENGTH_SHORT).show();
        }
    }
}
