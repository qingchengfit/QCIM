package com.tencent.qcloud.timchat.common;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Build;
import android.support.v4.content.ContextCompat;

/**
 * Created by fb on 2017/4/15.
 */

public class AppData {


    private static SharedPreferences sharedPreferences;

    public static SharedPreferences getSharedPreferences(Context context) {
        return context.getSharedPreferences(Configs.PREFRENCE_USERSIG, 0);
    }

    public static String getUSerSig(Context context){
        return getSharedPreferences(context).getString(Configs.VALUE_USERSIG, "");
    }

    public static void putUserSig(Context context , String userSig) {
        SharedPreferences.Editor editor = getSharedPreferences(context).edit();
        editor.putString(Configs.VALUE_USERSIG, userSig);
        applyCompat(editor);
    }

    public static void putUserAvatar(Context context, String avatar){
        SharedPreferences.Editor editor = getSharedPreferences(context).edit();
        editor.putString(Configs.PREFRENCE_AVATAR, avatar);
        applyCompat(editor);
    }

    //TODO 返回默认头像
    public static String getAvatar(Context context){
        return getSharedPreferences(context).getString(Configs.VALUE_AVATAR, "");
    }

    public static void putIdentify(Context context, String identify){
        SharedPreferences.Editor editor = getSharedPreferences(context).edit();
        editor.putString(Configs.PREFRENCE_AVATAR, identify);
        applyCompat(editor);
    }

    public static String getIdentify(Context context){
        return getSharedPreferences(context).getString(Configs.VALUE_IDENTIFY, "");
    }

    private static void applyCompat(SharedPreferences.Editor editor) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.GINGERBREAD) {
            editor.apply();
        } else {
            editor.commit();
        }
    }

    public static void clear(Context context){
        getSharedPreferences(context).edit().clear();
    }

}
