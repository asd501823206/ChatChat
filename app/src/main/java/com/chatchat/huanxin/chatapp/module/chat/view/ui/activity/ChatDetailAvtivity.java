package com.chatchat.huanxin.chatapp.module.chat.view.ui.activity;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Rect;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.chatchat.huanxin.chatapp.R;
import com.chatchat.huanxin.chatapp.common.database.DBHelper;
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
    private float mDown;

    private DBHelper dbHelper;

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
        dbHelper = new DBHelper(this);
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        Cursor cursor = db.query("message", null, null, null, null, null, null);
        if (cursor.moveToFirst()) {
            data = new ArrayList<>();
            do {
                ChatDetailBean bean = new ChatDetailBean();
                bean.setUserNameFrom(cursor.getString(cursor.getColumnIndex("fromName")));
                bean.setUserNameTo(cursor.getString(cursor.getColumnIndex("toName")));
                bean.setUserIdFrom(cursor.getString(cursor.getColumnIndex("fromId")));
                bean.setUserIdTo(cursor.getString(cursor.getColumnIndex("toId")));
                bean.setTextMessage(cursor.getString(cursor.getColumnIndex("message")));
                bean.setTime(cursor.getLong(cursor.getColumnIndex("time")));
                data.add(bean);
            }while (cursor.moveToNext());
        }
        cursor.close();
        db.close();

        if (data == null) {
            insertFakeData();
        }
    }

    private void insertFakeData() {
        data = new ArrayList<>();
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        ContentValues values = new ContentValues();

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
            bean.setTime(System.currentTimeMillis());
            data.add(bean);
            values.clear();
            values.put("fromName", bean.getUserNameFrom());
            values.put("toName", bean.getUserNameTo());
            values.put("fromId", bean.getUserIdFrom());
            values.put("toId", bean.getUserIdTo());
            values.put("message", bean.getTextMessage());
            values.put("time", bean.getTime());
            db.insert("message", null, values);
        }
        db.close();
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

        mEtTypein = findViewById(R.id.et_chatbar_typein);
        mIvEmoji = findViewById(R.id.iv_chatbar_emoji);
        mIvAdd = findViewById(R.id.iv_chatbar_add);
        mTvSend = findViewById(R.id.tv_chatbar_send);
        mIvEmoji.setOnClickListener(this);
    }

    private void hideInputKeyboard() {
        //隐藏输入法
        InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
        if (imm != null) {
            imm.hideSoftInputFromWindow(getWindow().getDecorView().getWindowToken(),0);
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.iv_back:
                //左上角返回键
                finish();
                break;
            case R.id.iv_chatbar_emoji:
                //TODO:emoji笑脸
                break;
            case R.id.iv_chatbar_add:
                //TODO:加号
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
        mTvSend.setClickable(false);
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
            bean.setTime(System.currentTimeMillis());
            mDetailAdapter.addData(bean);

            save2Database(bean);
        }
        mTvSend.setClickable(true);
    }

    private void save2Database(ChatDetailBean bean) {
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put("fromName", bean.getUserNameFrom());
        values.put("toName", bean.getUserNameTo());
        values.put("fromId", bean.getUserIdFrom());
        values.put("toId", bean.getUserIdTo());
        values.put("message", bean.getTextMessage());
        values.put("time", bean.getTime());
        db.insert("message", null, values);
        values.clear();
        db.close();
    }

    /**
     * 软键盘弹出时 显示发送按钮
     * 软键盘隐藏时 显示添加按钮
     * @param isPopup 软键盘是是否弹出
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
