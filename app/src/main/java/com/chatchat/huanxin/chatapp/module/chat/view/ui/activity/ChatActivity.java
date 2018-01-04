package com.chatchat.huanxin.chatapp.module.chat.view.ui.activity;

import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentTransaction;
import android.widget.TextView;
import android.widget.Toast;

import com.chatchat.huanxin.chatapp.R;
import com.chatchat.huanxin.chatapp.module.chat.model.ChatDetailBean;
import com.chatchat.huanxin.chatapp.module.chat.view.ChatFragmentListener;
import com.chatchat.huanxin.chatapp.module.chat.view.ui.fragment.ChatGroupFragment;
import com.chatchat.huanxin.chatapp.module.chat.view.ui.fragment.ChatListFragment;
import com.chatchat.huanxin.chatapp.utils.PermissionUtil;
import com.hyphenate.EMConnectionListener;
import com.hyphenate.EMError;
import com.hyphenate.chat.EMClient;
import com.hyphenate.util.NetUtils;

import java.util.ArrayList;

/**
 * 聊天界面
 * Created by dengzm on 2017/12/13.
 */

public class ChatActivity extends FragmentActivity {
    private static final String TAG = "ChatActivity";

    private final int REQ_READ_STATE = 1;
    private final int REQ_READ_WRITE = 2;

    private TextView mTvTitle;

    private ChatFragmentListener.ChatListListener chatListListener = null;
    private ChatFragmentListener.ChatGroupListener chatGroupListener = null;

    private Fragment[] tabFragments;
    private int pre = -1;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.layout_activity_chat);
        initPermissions();
        initEM();
        initViews();
    }

    private void initPermissions() {
        PermissionUtil.getPermission(ChatActivity.this, PermissionUtil.READ_PHONE_STATE,
                REQ_READ_STATE, "读取手机状态");
        PermissionUtil.getPermission(ChatActivity.this, PermissionUtil.GET_READ_WRITE,
                REQ_READ_WRITE);
    }

    private void initViews() {
        mTvTitle = findViewById(R.id.tv_toolbar_title);
        mTvTitle.setText("会话");

        tabFragments = new Fragment[2];

        initFragment(0);
    }

    private void initFragment(int position) {
        FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
        if (position == 0) {
            if (!(tabFragments[0] instanceof ChatListFragment)) {
                ChatListFragment fragment = new ChatListFragment();
                tabFragments[0] = fragment;
                ft.add(R.id.fl_chat_content, tabFragments[0]);
            }
        }else {
            if (!(tabFragments[1] instanceof ChatGroupFragment)) {
                ChatGroupFragment fragment = new ChatGroupFragment();
                tabFragments[1] = fragment;
                ft.add(R.id.fl_chat_content, tabFragments[1]);
            }
        }

        if (pre != -1) {
            ft.hide(tabFragments[pre]);
        }

        ft.show(tabFragments[position]).commitAllowingStateLoss();
        pre = position;
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if (requestCode == REQ_READ_STATE) {
            if (grantResults[0] != PackageManager.PERMISSION_GRANTED) {
                PermissionUtil.startDialog(ChatActivity.this, "读取手机状态");
            }
        }else if (requestCode == REQ_READ_WRITE) {
            if (grantResults[0] != PackageManager.PERMISSION_GRANTED) {
                PermissionUtil.startDialog(ChatActivity.this, "手机读写");
            }
        }
    }

    private void initEM() {
        EMClient.getInstance().addConnectionListener(new EMConnectionListener() {
            @Override
            public void onConnected() {

            }

            @Override
            public void onDisconnected(final int error) {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        if(error == EMError.USER_REMOVED){
                            // 显示帐号已经被移除
                            Toast.makeText(ChatActivity.this, "显示帐号已经被移除", Toast.LENGTH_SHORT).show();
                        }else if (error == EMError.USER_LOGIN_ANOTHER_DEVICE) {
                            // 显示帐号在其他设备登录
                            Toast.makeText(ChatActivity.this, "显示帐号在其他设备登录", Toast.LENGTH_SHORT).show();
                        } else {
                            if (NetUtils.hasNetwork(ChatActivity.this)){
                                //连接不到聊天服务器
                                Toast.makeText(ChatActivity.this, "连接不到聊天服务器", Toast.LENGTH_SHORT).show();
                            }
                            else{
                                //当前网络不可用，请检查网络设置
                                Toast.makeText(ChatActivity.this, "当前网络不可用，请检查网络设置", Toast.LENGTH_SHORT).show();
                            }
                        }
                    }
                });
            }
        });
    }



}
