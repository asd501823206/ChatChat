package com.chatchat.huanxin.chatapp.utils;

import android.Manifest;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.support.v4.app.ActivityCompat;

import java.util.ArrayList;

/**
 * 动态权限获取
 * Created by dengzm on 2017/12/30.
 */

public class PermissionUtil {
    public static final String READ_PHONE_STATE = android.Manifest.permission.READ_PHONE_STATE;
    public static final String[] GET_PHONE_LOCATION = {
            Manifest.permission.ACCESS_COARSE_LOCATION,
            Manifest.permission.ACCESS_FINE_LOCATION,
            Manifest.permission.ACCESS_LOCATION_EXTRA_COMMANDS};
    public static final String[] GET_READ_WRITE = {
            Manifest.permission.READ_EXTERNAL_STORAGE,
            Manifest.permission.WRITE_EXTERNAL_STORAGE};

    public static boolean getPermission(Activity context, String permission, int requestCode, String permissionName) {
        int hasPermission = ActivityCompat.checkSelfPermission(context, permission);
        if (hasPermission != PackageManager.PERMISSION_GRANTED) {
            if (ActivityCompat.shouldShowRequestPermissionRationale(context, permission)) {
                //表明用户没有彻底禁止请求权限
                ActivityCompat.requestPermissions(context, new String[]{permission}, requestCode);
            }else {
                //表明用户已彻底禁止请求权限
                ActivityCompat.requestPermissions(context, new String[]{permission}, requestCode);
                startDialog(context, permissionName);
            }
            return false;
        }
        return true;
    }

    public static boolean getPermission(Activity context, String[] permissions, int requestCode) {
        ArrayList<String> result = new ArrayList<>();
        for (String permission : permissions) {
            int hasPermission = ActivityCompat.checkSelfPermission(context, permission);
            if (hasPermission != PackageManager.PERMISSION_GRANTED) {
                result.add(permission);
            }
        }

        if (result.size() > 0) {
            ActivityCompat.requestPermissions(context, result.toArray(new String[result.size()]), requestCode);
            return false;
        }
        return true;
    }

    public static void startDialog(final Activity context, String permissionName) {
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setTitle("友情提示");
        builder.setMessage("您没有授权" + permissionName + "权限，请在设置中打开权限");
        builder.setPositiveButton("确定", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                Intent intent = new Intent();
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                //SDK > 9
                intent.setAction("android.settings.APPLICATION_DETAILS_SETTINGS");
                intent.setData(Uri.fromParts("package", context.getPackageName(), null));
                context.startActivity(intent);
            }
        });
        builder.setCancelable(true);
        builder.show();
    }

}
