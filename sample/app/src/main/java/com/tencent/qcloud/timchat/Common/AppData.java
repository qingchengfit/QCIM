package com.tencent.qcloud.timchat.common;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Build;

/**
 * Created by fb on 2017/4/15.
 */

public class AppData {


    private static SharedPreferences sharedPreferences;

    public AppData(Context context) {
        sharedPreferences =context.getSharedPreferences(com.tencent.qcloud.timchat.common.Configs.PREFRENCE_USERSIG, 0);
    }

    public static String getUSerSig(Context context){
        sharedPreferences =context.getSharedPreferences(com.tencent.qcloud.timchat.common.Configs.PREFRENCE_USERSIG, 0);
        return sharedPreferences.getString(com.tencent.qcloud.timchat.common.Configs.VALUE_USERSIG, "");
    }

    public static void putUserSig(Context context, String userSig) {
        sharedPreferences =context.getSharedPreferences(com.tencent.qcloud.timchat.common.Configs.PREFRENCE_USERSIG, 0);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString(com.tencent.qcloud.timchat.common.Configs.VALUE_USERSIG, userSig);
        applyCompat(editor);
    }

    public static void putUserAvatar(Context context, String avatar){
        sharedPreferences =context.getSharedPreferences(com.tencent.qcloud.timchat.common.Configs.PREFRENCE_USERSIG, 0);

        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString(com.tencent.qcloud.timchat.common.Configs.PREFRENCE_AVATAR, avatar);
        applyCompat(editor);
    }

    //TODO 返回默认头像
    public static String getAvatar(Context context){
        sharedPreferences =context.getSharedPreferences(com.tencent.qcloud.timchat.common.Configs.PREFRENCE_USERSIG, 0);
        return sharedPreferences.getString(com.tencent.qcloud.timchat.common.Configs.VALUE_AVATAR, "");
    }

    private static void applyCompat(SharedPreferences.Editor editor) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.GINGERBREAD) {
            editor.apply();
        } else {
            editor.commit();
        }
    }

}
