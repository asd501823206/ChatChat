package com.chatchat.huanxin.chatapp.module.chat.view.ui.fragment;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.chatchat.huanxin.chatapp.R;
import com.chatchat.huanxin.chatapp.common.view.fragment.BaseFragment;
import com.chatchat.huanxin.chatapp.module.chat.view.ChatFragmentListener;
import com.chatchat.huanxin.chatapp.module.chat.view.adapter.ChatListAdapter;
import com.hyphenate.chat.EMClient;
import com.hyphenate.chat.EMConversation;

import java.util.Map;

/**
 * 会话列表
 * Created by dengzm on 2017/12/13.
 */

public class ChatListFragment extends BaseFragment implements ChatFragmentListener.ChatListListener{
    private static final String TAG = "ChatListFragment";

    private RecyclerView mRlChatList;
    private ChatListAdapter adapter = null;
    private Map<String, EMConversation> conversations;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.layout_fragment_chat_list, container, false);
        initViews(view);
        return view;
    }

    private void initViews(View view) {
        mRlChatList = view.findViewById(R.id.rl_chat_list);
        mRlChatList.setLayoutManager(new LinearLayoutManager(getActivity()));
        mRlChatList.addItemDecoration(new DividerItemDecoration(getActivity(), DividerItemDecoration.VERTICAL));
        addData();
        view.findViewById(R.id.refresh).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                addData();
            }
        });
    }

    private void addData() {
        conversations = EMClient.getInstance().chatManager().getAllConversations();
        if (adapter == null) {
            adapter = new ChatListAdapter(getActivity());
            mRlChatList.setAdapter(adapter);
        }
        adapter.setData(conversations);
        adapter.notifyDataSetChanged();
    }
}
