package com.tencent.qcloud.timchat.ui.qcchat;

import android.Manifest;
import android.app.Activity;
import android.app.ActivityManager;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.os.Build;

import com.huawei.android.pushagent.PushManager;
import com.tencent.TIMCallBack;
import com.tencent.TIMLogLevel;
import com.tencent.TIMManager;
import com.tencent.qcloud.timchat.business.InitBusiness;
import com.tencent.qcloud.timchat.business.LoginBusiness;
import com.tencent.qcloud.timchat.chatmodel.UserInfo;
import com.tencent.qcloud.timchat.chatutils.PushUtil;
import com.tencent.qcloud.timchat.event.FriendshipEvent;
import com.tencent.qcloud.timchat.event.GroupEvent;
import com.tencent.qcloud.timchat.event.MessageEvent;
import com.tencent.qcloud.timchat.event.RefreshEvent;
import com.tencent.qcloud.tlslibrary.helper.Util;
import com.tencent.qcloud.tlslibrary.service.TLSService;
import com.tencent.qcloud.tlslibrary.service.TlsBusiness;
import com.xiaomi.mipush.sdk.MiPushClient;

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.List;

import tencent.tls.platform.TLSErrInfo;
import tencent.tls.platform.TLSPwdLoginListener;
import tencent.tls.platform.TLSUserInfo;

/**
 * Created by fb on 2017/3/15.
 */

public class LoginProcessor implements TIMCallBack {

    private Context context;
    private TLSService tlsService;
    private PwdLoginListener pwdLoginListener;
    private OnLoginListener onLoginListener;

    public LoginProcessor(Context context) {
        this.context = context;
        tlsService = TLSService.getInstance();
        tlsService.initTlsSdk(context);
    }

    public void setOnLoginListener(OnLoginListener onLoginListener) {
        this.onLoginListener = onLoginListener;
    }

    public void sientInstall(String username, String password){
        pwdLoginListener = new LoginProcessor.PwdLoginListener();


        // 验证用户名和密码的有效性
        if (username.length() == 0) {
            Util.showToast(context, "用户名密码不能为空");
            return;
        }
        tlsService.TLSPwdLogin(username, password, pwdLoginListener);
    }

    public void checkPermission(WeakReference<Activity> weakReference){
        final List<String> permissionsList = new ArrayList<>();
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M){
            if ((context.checkSelfPermission(Manifest.permission.READ_PHONE_STATE)!= PackageManager.PERMISSION_GRANTED)) permissionsList.add(Manifest.permission.READ_PHONE_STATE);
            if ((context.checkSelfPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE)!= PackageManager.PERMISSION_GRANTED)) permissionsList.add(Manifest.permission.WRITE_EXTERNAL_STORAGE);
            if (permissionsList.size() == 0){
                init();
            }else{
                weakReference.get().requestPermissions(permissionsList.toArray(new String[permissionsList.size()]),0);
            }
        }else{
            init();
        }
    }

    private void init(){

        SharedPreferences pref = context.getSharedPreferences("data", context.MODE_PRIVATE);
        int loglvl = pref.getInt("loglvl", TIMLogLevel.DEBUG.ordinal());
        //初始化IMSDK
        InitBusiness.start(context,loglvl);
        //初始化TLS
        TlsBusiness.init(context);
        //设置刷新监听
        RefreshEvent.getInstance();
        String id =  TLSService.getInstance().getLastUserIdentifier();
        UserInfo.getInstance().setId(id);
        UserInfo.getInstance().setUserSig(TLSService.getInstance().getUserSig(id));
    }

    private void navToHome(){
        //登录之前要初始化群和好友关系链缓存
        FriendshipEvent.getInstance().init();
        GroupEvent.getInstance().init();
        LoginBusiness.loginIm(UserInfo.getInstance().getId(), UserInfo.getInstance().getUserSig(), this);
    }

    @Override
    public void onError(int i, String s) {
        onLoginListener.onLoginFailed(i);
    }

    @Override
    public void onSuccess() {
        //初始化程序后台后消息推送
        PushUtil.getInstance();
        //初始化消息监听
        MessageEvent.getInstance();
        String deviceMan = android.os.Build.MANUFACTURER;
        //注册小米和华为推送
        if (deviceMan.equals("Xiaomi") && shouldMiInit()) {
            MiPushClient.registerPush(context, "2882303761517480335", "5411748055335");
        } else if (deviceMan.equals("HUAWEI")) {
            PushManager.requestToken(context);
        }

        onLoginListener.onLoginSuccess();
    }

    /**
     * 判断小米推送是否已经初始化
     */
    private boolean shouldMiInit() {
        ActivityManager am = ((ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE));
        List<ActivityManager.RunningAppProcessInfo> processInfos = am.getRunningAppProcesses();
        String mainProcessName = context.getPackageName();
        int myPid = android.os.Process.myPid();
        for (ActivityManager.RunningAppProcessInfo info : processInfos) {
            if (info.pid == myPid && mainProcessName.equals(info.processName)) {
                return true;
            }
        }
        return false;
    }

    //登录TLService
    class PwdLoginListener implements TLSPwdLoginListener {
        @Override
        public void OnPwdLoginSuccess(TLSUserInfo userInfo) {
            TLSService.getInstance().setLastErrno(0);
            navToHome();
        }

        @Override
        public void OnPwdLoginReaskImgcodeSuccess(byte[] picData) {
        }

        @Override
        public void OnPwdLoginNeedImgcode(byte[] picData, TLSErrInfo errInfo) {
        }

        @Override
        public void OnPwdLoginFail(TLSErrInfo errInfo) {
            TLSService.getInstance().setLastErrno(-1);
            Util.notOK(context, errInfo);
        }

        @Override
        public void OnPwdLoginTimeout(TLSErrInfo errInfo) {
            TLSService.getInstance().setLastErrno(-1);
            Util.notOK(context, errInfo);
        }
    }

    public interface OnLoginListener{
        void onLoginSuccess();
        void onLoginFailed(int errorCode);
    }

}
