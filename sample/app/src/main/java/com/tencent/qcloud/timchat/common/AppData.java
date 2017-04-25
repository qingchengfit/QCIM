package com.tencent.qcloud.timchat.common;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Build;
import android.support.v4.content.ContextCompat;

import com.tencent.qcloud.timchat.chatmodel.GroupInfo;
import com.tencent.qcloud.timchat.chatmodel.UserInfo;
import com.tencent.qcloud.timchat.event.MessageEvent;

/**
 * Created by fb on 2017/4/15.
 */

public class AppData {


    private static SharedPreferences sharedPreferences;
    public static final String defaultAvatar = "http://zoneke-img.b0.upaiyun.com/75656eb980b79e7748041f830332cc62.png!120x120";
    public static final String defaultGroupAvatar = "http://zoneke-img.b0.upaiyun.com/4ca8948c8e2cc5d0874d163001fa2267.png";

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
        editor.putString(Configs.VALUE_AVATAR, avatar);
        applyCompat(editor);
    }

    //TODO 返回默认头像
    public static String getAvatar(Context context){
        return getSharedPreferences(context).getString(Configs.VALUE_AVATAR, "");
    }

    public static void putIdentify(Context context, String identify){
        SharedPreferences.Editor editor = getSharedPreferences(context).edit();
        editor.putString(Configs.VALUE_IDENTIFY, identify);
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
        UserInfo.getInstance().setId(null);
        MessageEvent.getInstance().clear();
        GroupInfo.getInstance().clear();
        getSharedPreferences(context).edit().clear().apply();
    }

}
