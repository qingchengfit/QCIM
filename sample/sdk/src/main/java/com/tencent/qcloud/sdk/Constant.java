package com.tencent.qcloud.sdk;

/**
 * 常量
 */
public class Constant {

    //TODO 自由账号的type 与 AppID

    public static int ACCOUNT_TYPE;
    //sdk appid 由腾讯分配
    public static int SDK_APPID;

    //小米推送AppId
    public static String XIAOMI_PUSH_APPID;
    //小米推送AppKey
    public static String XIAOMI_PUSH_APPKEY;
    //证书ID
    public static int BUSS_ID;

    public static void setAccountType(int accountType) {
        ACCOUNT_TYPE = accountType;
    }

    public static void setSdkAppid(int sdkAppid) {
        SDK_APPID = sdkAppid;
    }

    public static void setXiaomiPushAppid(String xiaomiPushAppid) {
        XIAOMI_PUSH_APPID = xiaomiPushAppid;
    }

    public static void setXiaomiPushAppkey(String xiaomiPushAppkey) {
        XIAOMI_PUSH_APPKEY = xiaomiPushAppkey;
    }

    public static void setBussId(int bussId) {
        BUSS_ID = bussId;
    }

    //    public static final int ACCOUNT_TYPE = 792;
//    //sdk appid 由腾讯分配
//    public static final int SDK_APPID = 1400001533;

}
