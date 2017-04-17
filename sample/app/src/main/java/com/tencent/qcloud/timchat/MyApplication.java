package com.tencent.qcloud.timchat;

import android.app.Application;
import android.content.Context;

import com.tencent.TIMGroupReceiveMessageOpt;
import com.tencent.TIMManager;
import com.tencent.TIMOfflinePushListener;
import com.tencent.TIMOfflinePushNotification;
import com.tencent.qalsdk.sdk.MsfSdkUtils;
import com.tencent.qcloud.timchat.chatutils.Foreground;


/**
 * 全局Application
 */
public class MyApplication {

    private static Context context;
    Application application;

    public MyApplication(Application application) {
        this.application = application;
        if (application != null){
            context = application.getApplicationContext();
        }
        init();
    }

    private void init(){
        Foreground.init(application);
        if(MsfSdkUtils.isMainProcess(context)) {
            TIMManager.getInstance().setOfflinePushListener(new TIMOfflinePushListener() {
                @Override
                public void handleNotification(TIMOfflinePushNotification notification) {
                    if (notification.getGroupReceiveMsgOpt() == TIMGroupReceiveMessageOpt.ReceiveAndNotify){
                        //消息被设置为需要提醒
                        notification.doNotify(context, R.mipmap.ic_launcher);
                    }
                }
            });
        }
    }

    public static Context getContext() {
        return context;
    }

}
