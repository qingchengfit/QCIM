package com.tencent.qcloud.timchat.ui.qcchat;

/**
 * Created by fb on 2017/3/15.
 */

public interface ChatProcessListener {
    void onProcessSuccessed();
    void onProcessFailed(int errorCode);
}
