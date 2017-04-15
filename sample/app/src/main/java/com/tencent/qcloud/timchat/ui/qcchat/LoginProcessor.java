package com.tencent.qcloud.timchat.ui.qcchat;

import android.Manifest;
import android.annotation.TargetApi;
import android.app.Activity;
import android.app.ActivityManager;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.os.Build;

import android.support.annotation.NonNull;
import android.support.v4.content.ContextCompat;
import android.widget.Toast;

import com.huawei.android.pushagent.PushManager;
import com.tencent.TIMCallBack;
import com.tencent.TIMFriendshipManager;
import com.tencent.TIMLogLevel;
import com.tencent.TIMManager;
import com.tencent.qcloud.timchat.business.InitBusiness;
import com.tencent.qcloud.timchat.business.LoginBusiness;
import com.tencent.qcloud.timchat.chatmodel.UserInfo;
import com.tencent.qcloud.timchat.chatutils.NetUtil;
import com.tencent.qcloud.timchat.chatutils.PushUtil;
import com.tencent.qcloud.timchat.event.FriendshipEvent;
import com.tencent.qcloud.timchat.event.GroupEvent;
import com.tencent.qcloud.timchat.event.MessageEvent;
import com.tencent.qcloud.timchat.event.RefreshEvent;
import com.tencent.qcloud.tlslibrary.helper.Util;
import com.tencent.qcloud.tlslibrary.service.AccountRegisterService;
import com.tencent.qcloud.tlslibrary.service.TLSService;
import com.tencent.qcloud.tlslibrary.service.TlsBusiness;
import com.xiaomi.mipush.sdk.MiPushClient;

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.List;

import tencent.tls.platform.TLSErrInfo;
import tencent.tls.platform.TLSPwdLoginListener;
import tencent.tls.platform.TLSStrAccRegListener;
import tencent.tls.platform.TLSUserInfo;

/**
 * Created by fb on 2017/3/15.
 */

public class LoginProcessor implements TIMCallBack, TLSStrAccRegListener {

    private Context context;
    private TLSService tlsService;
    private PwdLoginListener pwdLoginListener;
    private OnLoginListener onLoginListener;
    private String username;
    private String password;
    private String host;

    public LoginProcessor(Context context, String username, String password, String host) throws Exception {
        this.context = context;
        this.username = username;
        this.password = password;
        this.host = host;

        tlsService = TLSService.getInstance();
        tlsService.initTlsSdk(context);
        sientInstall();
    }

    public void setOnLoginListener(OnLoginListener onLoginListener) {
        this.onLoginListener = onLoginListener;
    }

    public void initAccount(){
        int result = tlsService.TLSStrAccReg(username, password, this);
        if (result == TLSErrInfo.INPUT_INVALID) {
            Util.showToast(context, "IM账号注册失败");
        }
    }

    public void sientInstall(){
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

    /**
     * 设置用户昵称以及头像
     * @param username       用户名
     * @param avatarUrl     头像
     */
    public void setUserInfo(String username, String avatarUrl){
        TIMFriendshipManager.getInstance().setNickName(username, new TIMCallBack() {
            @Override
            public void onError(int i, String s) {
                Toast.makeText(context, "设置用户昵称：" + s, Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onSuccess() {
                Toast.makeText(context, "设置用户昵称：成功", Toast.LENGTH_SHORT).show();
            }
        });

        TIMFriendshipManager.getInstance().setFaceUrl(avatarUrl, new TIMCallBack() {
            @Override
            public void onError(int i, String s) {
                Toast.makeText(context, "设置用户头像：" + s, Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onSuccess() {
                Toast.makeText(context, "设置用户头像：成功", Toast.LENGTH_SHORT).show();
            }
        });

    }

    private void init(){

        SharedPreferences pref = context.getSharedPreferences("data", Context.MODE_PRIVATE);
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
//        LoginBusiness.loginIm(UserInfo.getInstance().getId(), UserInfo.getInstance().getUserSig(), this);
        final String id = UserInfo.getInstance().getId();
        NetUtil netUtil = new NetUtil(id,  host);
        netUtil.setOnUserSigListener(new NetUtil.OnUserSigListener() {
            @Override
            public void onSuccessed(String userSig) {
                LoginBusiness.loginIm(id, userSig, LoginProcessor.this);
            }
        });
    }

    @Override
    public void onError(int i, String s) {
        TLSErrInfo tlsErrInfo = new TLSErrInfo();
        tlsErrInfo.ErrCode = i;
        tlsErrInfo.Msg = s;
        onLoginListener.onLoginFailed(tlsErrInfo);
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
    @TargetApi(Build.VERSION_CODES.CUPCAKE) private boolean shouldMiInit() {
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

    @Override public void OnStrAccRegSuccess(TLSUserInfo tlsUserInfo) {
        Util.showToast(context, "成功注册 " + tlsUserInfo.identifier);
        TLSService.getInstance().setLastErrno(0);
        sientInstall();
    }

    @Override public void OnStrAccRegFail(TLSErrInfo tlsErrInfo) {
        Util.showToast(context, "注册聊天失败，请重试");
    }

    @Override public void OnStrAccRegTimeout(TLSErrInfo tlsErrInfo) {
        Util.showToast(context, "注册聊天超时，请重试");
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
            onLoginListener.onLoginFailed(errInfo);
            //Util.notOK(context, errInfo);
        }

        @Override
        public void OnPwdLoginTimeout(TLSErrInfo errInfo) {
            TLSService.getInstance().setLastErrno(-1);
            Util.notOK(context, errInfo);
        }
    }

    public interface OnLoginListener{
        void onLoginSuccess();
        void onLoginFailed(TLSErrInfo errInfo);
    }

}
