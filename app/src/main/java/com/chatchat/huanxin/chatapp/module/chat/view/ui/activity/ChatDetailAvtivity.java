package com.chatchat.huanxin.chatapp.module.chat.view.ui.activity;

import android.content.Context;
import android.content.Intent;
import android.graphics.Rect;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.chatchat.huanxin.chatapp.R;
import com.chatchat.huanxin.chatapp.common.view.activity.BaseActivity;
import com.chatchat.huanxin.chatapp.module.chat.model.ChatDetailBean;
import com.chatchat.huanxin.chatapp.module.chat.view.adapter.ChatDetailAdapter;
import com.chatchat.huanxin.chatapp.utils.SpHelper;
import com.hyphenate.chat.EMClient;
import com.hyphenate.chat.EMConversation;
import com.hyphenate.chat.EMMessage;

import java.util.ArrayList;

/**
 * 聊天界面
 * Created by dengzm on 2017/12/27.
 */

public class ChatDetailAvtivity extends BaseActivity implements View.OnClickListener, View.OnLayoutChangeListener {
    private static final String TAG = "ChatDetailAvtivity";

    private RecyclerView mRvChatDetail;
    private ChatDetailAdapter mDetailAdapter;
    private EditText mEtTypein;
    private ImageView mIvEmoji;
    private ImageView mIvAdd;
    private TextView mTvSend;
    private int screenHeight;
    private boolean isCreated = false;

//    private EMConversation conversation;
//    private String id;

    private ArrayList<ChatDetailBean> data;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.layout_activity_chat_detail);
        initIntent();
        initData();
        initViews();
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (!isCreated) {
            //获取当前屏幕内容的高度
            screenHeight = getWindow().getDecorView().getHeight();
            getWindow().getDecorView().addOnLayoutChangeListener(this);

            isCreated  = !isCreated;
        }
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
            data.add(bean);
        }

    }

    private void initIntent() {
//        Intent intent = getIntent();
//        id = intent.getStringExtra("id");
//        conversation = EMClient.getInstance().chatManager().getAllConversations().get(id);
    }

    private void initViews() {
        TextView mTvTitle = findViewById(R.id.tv_chat_detail_name);
        mTvTitle.setText("会话");
        mRvChatDetail = findViewById(R.id.rv_chat_detail);
        findViewById(R.id.iv_back).setOnClickListener(this);

        mRvChatDetail.setLayoutManager(new LinearLayoutManager(ChatDetailAvtivity.this));
        mRvChatDetail.addItemDecoration(new DividerItemDecoration(ChatDetailAvtivity.this, DividerItemDecoration.VERTICAL));
        mDetailAdapter = new ChatDetailAdapter(ChatDetailAvtivity.this);
        //mDetailAdapter.setData((ArrayList<EMMessage>) conversation.getAllMessages());
        mDetailAdapter.setData(data);
        mRvChatDetail.setAdapter(mDetailAdapter);
        mRvChatDetail.scrollTo(0, mRvChatDetail.computeVerticalScrollRange());

        mEtTypein = findViewById(R.id.et_chatbar_typein);
        mIvEmoji = findViewById(R.id.iv_chatbar_emoji);
        mIvAdd = findViewById(R.id.iv_chatbar_add);
        mTvSend = findViewById(R.id.tv_chatbar_send);
        mIvEmoji.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.iv_back:
                //左上角返回键
                finish();
                break;
            case R.id.iv_chatbar_emoji:
                //emoji笑脸
                break;
            case R.id.iv_chatbar_add:
                //加号
                break;
            case R.id.tv_chatbar_send:
                //发送
                sendMessage();
                break;
            default:
                break;
        }
    }

    private void sendMessage() {
        String msg = mEtTypein.getText().toString();
        if (TextUtils.isEmpty(msg)) {
            Toast.makeText(this, "发送内容不能为空！", Toast.LENGTH_SHORT).show();
        }else {
            mEtTypein.setText("");
            ChatDetailBean bean = new ChatDetailBean();
            bean.setUserNameFrom("猫1");
            bean.setUserNameTo("猫2");
            bean.setUserIdFrom("1");
            bean.setUserIdTo("2");
            bean.setTextMessage(msg);
            mDetailAdapter.addData(bean);
            mRvChatDetail.scrollTo(0, mRvChatDetail.computeVerticalScrollRange());
        }
    }

    /**
     * 软键盘弹出时 显示发送按钮
     * 软键盘隐藏时 显示添加按钮
     * @param isPopup
     */
    private void changeSend(boolean isPopup) {
        if (isPopup) {
            mIvAdd.setVisibility(View.GONE);
            mTvSend.setVisibility(View.VISIBLE);
            mTvSend.setOnClickListener(this);
        }else {
            mIvAdd.setVisibility(View.VISIBLE);
            mTvSend.setVisibility(View.GONE);
            mIvAdd.setOnClickListener(this);
        }
    }

    /**
     * 监听软键盘弹出：超过三分之一，认为弹出了键盘
     */
    @Override
    public void onLayoutChange(View v, int left, int top, int right, int bottom, int oldLeft, int oldTop, int oldRight, int oldBottom) {
        if (bottom != 0 && oldBottom != 0) {
            Rect rect = new Rect();
            getWindow().getDecorView().getWindowVisibleDisplayFrame(rect);
            if (screenBottom == 0) {
                screenBottom = bottom;
            }
            if (bottom - rect.bottom > screenHeight / 3) {
                if (keyboardHeight == 0) {
                    keyboardHeight = screenBottom - rect.bottom;
                    SpHelper.getInstance().writeToPreferences("keyboard_height", keyboardHeight);
                }
                mRvChatDetail.scrollTo(0, mRvChatDetail.computeVerticalScrollRange());
                changeSend(true);
            }else {
                changeSend(false);
            }
        }
    }
    private int screenBottom = 0;
    private int keyboardHeight = 0;
}
