package com.tencent.qcloud.timchat.ui.qcchat;

import android.Manifest;
import android.annotation.TargetApi;
import android.app.Activity;
import android.app.ActivityManager;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.os.Build;
import android.text.TextUtils;
import android.widget.Toast;
import com.huawei.android.pushagent.PushManager;
import com.tencent.TIMCallBack;
import com.tencent.TIMFriendshipManager;
import com.tencent.TIMLogLevel;
import com.tencent.qcloud.timchat.business.InitBusiness;
import com.tencent.qcloud.timchat.business.LoginBusiness;
import com.tencent.qcloud.timchat.chatmodel.UserInfo;
import com.tencent.qcloud.timchat.chatutils.NetUtil;
import com.tencent.qcloud.timchat.chatutils.PushUtil;
import com.tencent.qcloud.timchat.common.AppData;
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

/**
 * Created by fb on 2017/3/15.
 */

public class LoginProcessor implements TIMCallBack {

    private Context context;
    private TLSService tlsService;
    private OnLoginListener onLoginListener;
    private String username;
    private String password;
    private String host;

    public LoginProcessor(Context context, String username, String password, String host) throws Exception {
        this.context = context;
        this.username = username;
        this.password = password;
        this.host = host;
        AppData appData = new AppData(context);
        tlsService = TLSService.getInstance();
        tlsService.initTlsSdk(context);
        //sientInstall();
    }

    public void setOnLoginListener(OnLoginListener onLoginListener) {
        this.onLoginListener = onLoginListener;
    }

    public void sientInstall(){

        // 验证用户名和密码的有效性
        if (username.length() == 0) {
            Util.showToast(context, "用户名密码不能为空");
            return;
        }
        init();
        if (TextUtils.isEmpty(AppData.getUSerSig(context)) && !AppData.getIdentify(context).equals(username)) {
            AppData.putIdentify(context, username);
            navToHome();
        }else {
            LoginBusiness.loginIm(username, AppData.getUSerSig(context), LoginProcessor.this);
        }
    }

    //清除登录信息
    public void logOut(Context context){
        AppData.clear(context);
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
        if(!TextUtils.isEmpty(avatarUrl)){
            AppData.putUserAvatar(context, avatarUrl);
        }
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
//        String id =  TLSService.getInstance().getLastUserIdentifier();
        UserInfo.getInstance().setId(username);
    }

    private void navToHome(){
        //登录之前要初始化群和好友关系链缓存
        FriendshipEvent.getInstance().init();
        GroupEvent.getInstance().init();
//        LoginBusiness.loginIm(UserInfo.getInstance().getId(), UserInfo.getInstance().getUserSig(), this);;
        NetUtil netUtil = new NetUtil(username,  host);
        netUtil.setOnUserSigListener(new NetUtil.OnUserSigListener() {
            @Override
            public void onSuccessed(String userSig) {
                LoginBusiness.loginIm(username, userSig, LoginProcessor.this);
                AppData.putUserSig(context, userSig);
                UserInfo.getInstance().setUserSig(userSig);
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

    public interface OnLoginListener{
        void onLoginSuccess();
        void onLoginFailed(TLSErrInfo errInfo);
    }

}
