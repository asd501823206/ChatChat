package com.chatchat.huanxin.chatapp.utils;

import android.app.Activity;
import android.content.Context;
import android.graphics.Rect;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;

import java.util.Stack;

/**
 * Class For
 * Package Name com.chatchat.huanxin.chatapp.utils
 * Created by dengzm on 2018/1/18.
 */

public class Touch2CloseSoftKeyboardUtil {
    private static final String TAG = "Touch2CloseSoftKeyboard";

    private Stack<View> filterViews = new Stack<>();

    /**
     * 点击非edittext控件和filterviews外控件 收回软键盘(有问题 待修改）
     */
    public boolean autoClose(Activity activity, MotionEvent ev) {
        if (ev.getAction() != MotionEvent.ACTION_DOWN)
            return false;
        View focusView = activity.getWindow().getCurrentFocus();
        if (focusView == null)
            return false;
        View decorView = activity.getWindow().getDecorView();
        if (!judgeIsTouchInView(decorView, ev))
            return false;
        if (decorView instanceof ViewGroup) {
            View view = getChild((ViewGroup) decorView, ev);
            if (!(view instanceof EditText) && !filterViews.contains(view)) {
                focusView.clearFocus();
                closeKeyBoadr(view, activity);
                return true;
            }
        }
        return false;
    }

    public boolean autoCloseSoft(Activity activity, MotionEvent ev) {
        if (ev.getAction() != MotionEvent.ACTION_DOWN)
            return false;
        View focusView = activity.getWindow().getCurrentFocus();
        if (focusView == null)
            return false;
        focusView.clearFocus();
        closeKeyBoadr(focusView, activity);
        return true;
    }

    /**
     * 判断点击点是否在view上
     * @param view focus view
     * @param ev motion event
     * @return is focus on the view
     */
    private boolean judgeIsTouchInView(View view, MotionEvent ev) {
        int[] outLocation = new int[2];
        view.getLocationInWindow(outLocation);
        Rect rect = new Rect();
        rect.left = outLocation[0];
        rect.top = outLocation[1];
        rect.right = rect.left + view.getMeasuredWidth();
        rect.bottom = rect.top + view.getMeasuredHeight();
        Log.d(TAG, "judgeIsTouchInView: rect:left=" + rect.left + ",top=" + rect.top + ",right=" + rect.right + ",bottom=" + rect.bottom);
        Log.d(TAG, "judgeIsTouchInView: ev  :left=" + ev.getX() + ",top=" + ev.getY());
        return rect.contains((int)ev.getX(), (int)ev.getY());
    }

    /**
     * 获取被点击view
     */
    private View getChild(ViewGroup viewGroup, MotionEvent ev) {
        if (viewGroup.getChildCount() == 0)
            return viewGroup;
        for (int i = 0; i < viewGroup.getChildCount(); i ++) {
            View view = viewGroup.getChildAt(i);
            if (judgeIsTouchInView(view, ev)) {
                if (view instanceof ViewGroup) {
                    View touchView = getChild((ViewGroup) view, ev);
                    if (touchView != null && touchView.getTouchables().size() > 0)
                        return touchView;
                }else {
                    if (view.getTouchables().size() > 0)
                        return view;
                }
            }
        }
        return null;
    }

    /**
     * 关闭软键盘
     * @param focusView 获得焦点的view
     * @param context context
     */
    private void closeKeyBoadr(View focusView, Context context) {
        InputMethodManager inputMethodManager = (InputMethodManager) context.getSystemService(Context.INPUT_METHOD_SERVICE);
        if (focusView != null) {
            assert inputMethodManager != null;
            inputMethodManager.hideSoftInputFromWindow(focusView.getWindowToken(), 0);
            Log.d(TAG, "closeKeyBoadr: hide excuted");
        }
    }
}
