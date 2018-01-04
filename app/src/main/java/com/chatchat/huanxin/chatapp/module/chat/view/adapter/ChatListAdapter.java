package com.chatchat.huanxin.chatapp.module.chat.view.adapter;

import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.chatchat.huanxin.chatapp.R;
import com.chatchat.huanxin.chatapp.module.chat.view.ui.activity.ChatDetailAvtivity;
import com.hyphenate.chat.EMConversation;
import com.hyphenate.chat.EMTextMessageBody;

import java.text.DateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Map;

/**
 * 聊天列表
 * Created by dengzm on 2017/12/14.
 */

public class ChatListAdapter extends RecyclerView.Adapter<ChatListAdapter.ChatListViewHolder> {
    private static final String TAG = "ChatListAdapter";

    private Context mContext;
    private ArrayList<String> idList = new ArrayList<>();
    private ArrayList<EMConversation> conversations = new ArrayList<>();

    public ChatListAdapter(Context context) {
        mContext = context;
    }

    public void setData(Map<String, EMConversation> map) {
        if (map.size() != 0) {
            idList.clear();
            conversations.clear();
            for (Map.Entry<String, EMConversation> entry : map.entrySet()) {
                idList.add(entry.getKey());
                conversations.add(entry.getValue());
            }
        }
    }

    public void addData(Map<String, EMConversation> map) {
        if (map.size() != 0) {
            for (Map.Entry<String, EMConversation> entry : map.entrySet()) {
                idList.add(entry.getKey());
                conversations.add(entry.getValue());
            }
        }
        notifyDataSetChanged();
    }


    @Override
    public ChatListViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(mContext).inflate(R.layout.layout_item_chatlist, parent, false);
        return new ChatListViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ChatListViewHolder holder, int position) {
        String id = idList.get(position);
        final EMConversation conversation = conversations.get(position);
        holder.mTvName.setText(id);
        holder.mTvMsg.setText(((EMTextMessageBody)conversation.getLastMessage().getBody()).getMessage());
        Date date = new Date(conversation.getLastMessage().getMsgTime());
        DateFormat mediumFormat = DateFormat.getDateTimeInstance(DateFormat.MEDIUM, DateFormat.MEDIUM);
        holder.mTvTime.setText(mediumFormat.format(date));
        holder.mLlWhole.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent();
                intent.setClass(mContext, ChatDetailAvtivity.class);
                intent.putExtra("id", conversation.conversationId());
                mContext.startActivity(intent);
            }
        });
    }

    @Override
    public int getItemCount() {
        return idList.size();
    }

    class ChatListViewHolder extends RecyclerView.ViewHolder {
        TextView mTvName;
        TextView mTvTime;
        TextView mTvMsg;
        LinearLayout mLlWhole;

        ChatListViewHolder(View itemView) {
            super(itemView);
            mTvName = itemView.findViewById(R.id.item_chat_username);
            mTvTime = itemView.findViewById(R.id.item_chat_time);
            mTvMsg = itemView.findViewById(R.id.item_chat_last_msg);
            mLlWhole = itemView.findViewById(R.id.ll_chat_item);
        }
    }
}
