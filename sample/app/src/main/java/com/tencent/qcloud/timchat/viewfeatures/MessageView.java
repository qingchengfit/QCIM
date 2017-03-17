package com.tencent.qcloud.timchat.viewfeatures;

/**
 * 消息回调接口
 */
public interface MessageView {


    void onStatusChange(Status newStatus);


    enum Status{
        NORMAL,
        SENDING,
        ERROR,
    }
}
