package com.chatchat.huanxin.chatapp.module.chat.view.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.chatchat.huanxin.chatapp.R;

import java.util.ArrayList;

/**
 * 好友列表adapter
 * Created by dengzm on 2018/1/2.
 */

public class ChatGroupAdapter extends RecyclerView.Adapter<ChatGroupAdapter.GroupViewHolder> implements View.OnClickListener{
    private static final String TAG = "ChatGroupAdapter";

    private Context mContext;
    private ArrayList<String> data = new ArrayList<>();

    public ChatGroupAdapter(Context context) {
        mContext = context;
    }

    public void setData(ArrayList<String> data) {
        this.data = data;
    }

    @Override
    public GroupViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(mContext).inflate(R.layout.layout_item_chat_group, parent, false);
        return new GroupViewHolder(view);
    }

    @Override
    public void onBindViewHolder(GroupViewHolder holder, int position) {
        String name = data.get(position);
        holder.mTvName.setText(name);
        holder.mLlItem.setOnClickListener(this);
    }

    @Override
    public int getItemCount() {
        return data.size();
    }

    @Override
    public void onClick(View v) {

    }

    class GroupViewHolder extends RecyclerView.ViewHolder {
        LinearLayout mLlItem;
        ImageView mIvIcon;
        TextView mTvName;

        GroupViewHolder(View itemView) {
            super(itemView);
            mLlItem = itemView.findViewById(R.id.ll_chat_item);
            mIvIcon = itemView.findViewById(R.id.iv_group_icon);
            mTvName = itemView.findViewById(R.id.tv_group_name);
        }
    }
}
