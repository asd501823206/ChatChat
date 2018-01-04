package com.chatchat.huanxin.chatapp.module.chat.view.ui.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.TextView;

import com.chatchat.huanxin.chatapp.R;
import com.chatchat.huanxin.chatapp.common.view.activity.BaseActivity;
import com.chatchat.huanxin.chatapp.module.chat.model.ChatDetailBean;
import com.chatchat.huanxin.chatapp.module.chat.view.adapter.ChatDetailAdapter;
import com.hyphenate.chat.EMClient;
import com.hyphenate.chat.EMConversation;
import com.hyphenate.chat.EMMessage;

import java.util.ArrayList;

/**
 * 聊天界面
 * Created by dengzm on 2017/12/27.
 */

public class ChatDetailAvtivity extends BaseActivity implements View.OnClickListener {
    private static final String TAG = "ChatDetailAvtivity";

    private TextView mTvTitle;
    private RecyclerView mRvChatDetail;
    private ChatDetailAdapter mDetailAdapter;

    private EMConversation conversation;
    private String id;

    private ArrayList<ChatDetailBean> data;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.layout_activity_chat_detail);
        //initIntent();
        initData();
        initViews();
    }

    private void initData() {
        data = new ArrayList<>();

        for (int i = 0;i < 10; i ++) {
            ChatDetailBean bean = new ChatDetailBean();
            if (i % 2 == 0) {
                bean.setUserNameFrom("猫1");
                bean.setUserNameTo("猫2");
                bean.setUserIdFrom("1");
                bean.setUserIdTo("2");
                bean.setTextMessage("啊啊啊啊啊啊"+i);
            }else {
                bean.setUserNameFrom("猫2");
                bean.setUserNameTo("猫1");
                bean.setUserIdFrom("2");
                bean.setUserIdTo("1");
                bean.setTextMessage("喵喵喵喵喵喵"+i);
            }
        }

    }

    private void initIntent() {
        Intent intent = getIntent();
        id = intent.getStringExtra("id");
        conversation = EMClient.getInstance().chatManager().getAllConversations().get(id);
    }

    private void initViews() {
        mTvTitle = findViewById(R.id.tv_chat_detail_name);
        mTvTitle.setText(id);
        mRvChatDetail = findViewById(R.id.rv_chat_detail);
        findViewById(R.id.iv_back).setOnClickListener(this);

        mRvChatDetail.setLayoutManager(new LinearLayoutManager(ChatDetailAvtivity.this));
        mRvChatDetail.addItemDecoration(new DividerItemDecoration(ChatDetailAvtivity.this, DividerItemDecoration.VERTICAL));
        mDetailAdapter = new ChatDetailAdapter(ChatDetailAvtivity.this);
        //mDetailAdapter.setData((ArrayList<EMMessage>) conversation.getAllMessages());
        mDetailAdapter.setData(data);
        mRvChatDetail.setAdapter(mDetailAdapter);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.iv_back:
                finish();
                break;
        }
    }
}
