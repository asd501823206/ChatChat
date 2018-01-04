package com.chatchat.huanxin.chatapp.module.chat.view.ui.fragment;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.chatchat.huanxin.chatapp.R;
import com.chatchat.huanxin.chatapp.common.view.fragment.BaseFragment;
import com.chatchat.huanxin.chatapp.module.chat.view.adapter.ChatGroupAdapter;
import com.hyphenate.chat.EMClient;
import com.hyphenate.exceptions.HyphenateException;

import java.util.ArrayList;

/**
 * 聊天好友/群组界面
 * Created by dengzm on 2017/12/14.
 */

public class ChatGroupFragment extends BaseFragment {
    private static final String TAG = "ChatGroupFragment";

    private RecyclerView mRvGroup;
    private ChatGroupAdapter adapter;
    private ArrayList<String> usernames = new ArrayList<>();

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.layout_fragment_chat_group, container, false);
        initViews(view);
        return view;
    }

    private void initViews(View view) {
        mRvGroup = view.findViewById(R.id.rv_chat_group);

    }

    private void addData() {
        if (adapter == null) {
            adapter = new ChatGroupAdapter(getActivity());
            mRvGroup.setAdapter(adapter);
        }
        try {
            usernames = (ArrayList<String>) EMClient.getInstance().contactManager().getAllContactsFromServer();
        } catch (HyphenateException e) {
            e.printStackTrace();
        }
        adapter.setData(usernames);
    }
}
