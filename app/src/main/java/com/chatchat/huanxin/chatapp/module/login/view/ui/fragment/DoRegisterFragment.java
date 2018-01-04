package com.chatchat.huanxin.chatapp.module.login.view.ui.fragment;

import android.app.Activity;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;

import com.chatchat.huanxin.chatapp.R;
import com.chatchat.huanxin.chatapp.common.view.fragment.BaseFragment;
import com.chatchat.huanxin.chatapp.module.login.view.ui.LoginListener;

/**
 * 注册界面
 * Created by dengzm on 2017/12/12.
 */

public class DoRegisterFragment extends BaseFragment implements LoginListener.DoRegisterListener, View.OnClickListener {
    private static final String TAG = "DoRegisterFragment";

    private EditText mEtUsername;
    private EditText mEtPassword;
    private Button mBtnRegister;

    private String strName, strPwd;

    private IRegisterListener listener;

    public interface IRegisterListener {
        void onRegisterPressed(String username, String password);
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        listener = (IRegisterListener) activity;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.layout_fragment_register, container, false);
        initViews(view);
        return view;
    }

    private void initViews(View view) {
        mEtUsername = view.findViewById(R.id.et_username);
        mEtPassword = view.findViewById(R.id.et_password);
        mBtnRegister = view.findViewById(R.id.btn_register);
        mBtnRegister.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        checkUsernameAndPwd();
    }

    private void checkUsernameAndPwd() {
        strName = mEtUsername.getText().toString();
        strPwd = mEtPassword.getText().toString();
        if (!TextUtils.isEmpty(strName) && !TextUtils.isEmpty(strPwd)) {
            listener.onRegisterPressed(strName, strPwd);
        }
    }
}
