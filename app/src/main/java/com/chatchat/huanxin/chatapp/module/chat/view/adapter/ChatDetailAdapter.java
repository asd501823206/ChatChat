package com.chatchat.huanxin.chatapp.module.chat.view.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.chatchat.huanxin.chatapp.R;
import com.chatchat.huanxin.chatapp.module.chat.model.ChatDetailBean;
import com.chatchat.huanxin.chatapp.utils.SpHelper;
import com.hyphenate.chat.EMMessage;
import com.hyphenate.chat.EMTextMessageBody;

import java.util.ArrayList;

/**
 * Created by dengzm on 2017/12/29.
 */

public class ChatDetailAdapter extends RecyclerView.Adapter<ChatDetailAdapter.DetailViewHolder> {
    private static final String TAG = "ChatDetailAdapter";

    private Context mContext;
    //private ArrayList<EMMessage> data = new ArrayList<>();
    private ArrayList<ChatDetailBean> data = new ArrayList<>();

    public ChatDetailAdapter(Context context) {
        mContext = context;
    }

//    public void setData(ArrayList<EMMessage> data) {
//        this.data = data;
//    }

    public void setData(ArrayList<ChatDetailBean> bean) {
        data = bean;
    }

    public void addData(ArrayList<ChatDetailBean> bean) {
        data.addAll(bean);
        notifyDataSetChanged();
    }

    public void addData(ChatDetailBean bean) {
        data.add(bean);
        notifyDataSetChanged();
    }


    @Override
    public DetailViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(mContext).inflate(R.layout.layout_item_chat_detail, parent, false);
        return new DetailViewHolder(view);
    }

    @Override
    public void onBindViewHolder(DetailViewHolder holder, int position) {
        //EMMessage emMessage = data.get(position);
        ChatDetailBean bean = data.get(position);
        String from = bean.getUserNameFrom();
        String msg = bean.getTextMessage();
        //TextUtils.equals(from, SpHelper.getInstance().readStringPreference("username"))
        if (TextUtils.equals("猫1", from)) {
            holder.mIvLeft.setVisibility(View.GONE);
            holder.mTvLeft.setVisibility(View.GONE);
            holder.mIvRight.setVisibility(View.VISIBLE);
            holder.mTvRight.setVisibility(View.VISIBLE);
            holder.mTvRight.setText(msg);
        }else {
            holder.mIvLeft.setVisibility(View.VISIBLE);
            holder.mTvLeft.setVisibility(View.VISIBLE);
            holder.mIvRight.setVisibility(View.GONE);
            holder.mTvRight.setVisibility(View.GONE);
            holder.mTvLeft.setText(msg);
        }
    }

    @Override
    public int getItemCount() {
        return data.size();
    }

    class DetailViewHolder extends RecyclerView.ViewHolder {
        ImageView mIvLeft;
        ImageView mIvRight;
        TextView mTvLeft;
        TextView mTvRight;

        DetailViewHolder(View itemView) {
            super(itemView);

            mIvLeft = itemView.findViewById(R.id.user_icon_left);
            mIvRight = itemView.findViewById(R.id.user_icon_right);
            mTvLeft = itemView.findViewById(R.id.user_text_left);
            mTvRight = itemView.findViewById(R.id.user_text_right);
        }
    }
}
