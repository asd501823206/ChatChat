package com.chatchat.huanxin.chatapp.common.database;

import android.content.Context;
import android.database.DatabaseErrorHandler;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

/**
 * 数据库操作辅助类-消息
 * Created by dengzm on 2018/1/5.
 */

public class DBHelper extends SQLiteOpenHelper {
    private static final String TAG = "DBHelper-Message";
    private static final String name = "message";
    private static final int version = 1;

    public static final String CREATE_MESSAGE = "create table message (" +
            "id integer primary key autoincrement," +
            "fromName text," +
            "fromId text," +
            "toName text," +
            "toId text," +
            "message text," +
            "time integer," +
            "conversionId text)";

    public DBHelper(Context context) {
        super(context, name, null, version);
    }

    public DBHelper(Context context, String name, SQLiteDatabase.CursorFactory factory, int version) {
        super(context, name, factory, version);
    }

    public DBHelper(Context context, String name, SQLiteDatabase.CursorFactory factory, int version, DatabaseErrorHandler errorHandler) {
        super(context, name, factory, version, errorHandler);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(CREATE_MESSAGE);
        Log.d(TAG, "onCreate: database created!");
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }
}
