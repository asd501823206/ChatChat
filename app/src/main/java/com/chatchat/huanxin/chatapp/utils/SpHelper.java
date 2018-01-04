package com.chatchat.huanxin.chatapp.utils;

import android.content.Context;
import android.content.SharedPreferences;
import android.text.TextUtils;
import android.util.Log;

/**
 * Created by dengzm on 2017/12/13.
 */

public class SpHelper {
    private static final String TAG = "SpHelper";

    private SharedPreferences sp;

    private static SpHelper instance;
    public static SpHelper getInstance() {
        if (instance == null) {
            instance = new SpHelper();
        }
        return instance;
    }

    public void init(Context context) {
        sp = context.getSharedPreferences("chatchat_config", Context.MODE_PRIVATE);
    }

    public void writeToPreferences(String key, String value) {
        Log.d(TAG, "writeToPreferences() called with: key = [" + key + "], value = [" + value + "]");
        if (TextUtils.isEmpty(value)) {
            return;
        }
        SharedPreferences.Editor editor = sp.edit();
        editor.putString(key, value);
        editor.apply();
    }

    public void removePreferences(String key) {
        Log.d(TAG, "removePreferences() called with: key = [" + key + "]");
        SharedPreferences.Editor editor = sp.edit();
        editor.remove(key);
        editor.apply();
    }

    public void writeToPreferences(String key, boolean value) {
        SharedPreferences.Editor editor = sp.edit();
        editor.putBoolean(key, value);
        editor.apply();
    }

    public String readStringPreference(String key) {
        return sp.getString(key, "");
    }

    public String readStringPreference(String key, String defaultValue) {
        return sp.getString(key, defaultValue);
    }

    public boolean readBooleanPreference(String key) {
        return sp.getBoolean(key, false);
    }

    public boolean readBooleanPreference(String key, boolean defaultValue) {
        return sp.getBoolean(key, defaultValue);
    }

    public void writeToPreferences(String key, int value) {
        SharedPreferences.Editor editor = sp.edit();
        editor.putInt(key, value);
        editor.apply();
    }

    public int readIntPreference(String key) {
        return sp.getInt(key, -1);
    }

    public int readIntPreference(String key, int defaultValue) {
        return sp.getInt(key, defaultValue);
    }

    public void writeToPreferences(String s, long timeValue) {
        SharedPreferences.Editor editor = sp.edit();
        editor.putLong(s, timeValue);
        editor.apply();
    }

    public void writeToPreferences(String key, float value) {
        SharedPreferences.Editor editor = sp.edit();
        editor.putFloat(key, value);
        editor.apply();
    }
}
