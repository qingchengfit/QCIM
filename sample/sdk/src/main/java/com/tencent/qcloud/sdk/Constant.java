package com.tencent.qcloud.sdk;

/**
 * 常量
 */
public class Constant {

    //TODO 自由账号的type 与 AppID

    public static int ACCOUNT_TYPE;
    //sdk appid 由腾讯分配
    public static int SDK_APPID;

    public static void setAccountType(int accountType) {
        ACCOUNT_TYPE = accountType;
    }

    public static void setSdkAppid(int sdkAppid) {
        SDK_APPID = sdkAppid;
    }

    //    public static final int ACCOUNT_TYPE = 792;
//    //sdk appid 由腾讯分配
//    public static final int SDK_APPID = 1400001533;

}
