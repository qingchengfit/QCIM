package com.tencent.qcloud.timchat.ui.qcchat;

import android.content.Context;

import com.tencent.TIMCallBack;
import com.tencent.TIMGroupManager;
import com.tencent.TIMGroupMemberResult;
import com.tencent.TIMValueCallBack;
import com.tencent.qcloud.timchat.presenter.GroupManagerPresenter;

import java.util.List;

/**
 * Created by fb on 2017/3/14.
 */

//移除群成员
public class RemoveMemberProcessor {

    private Context context;
    private OnRemoveMemverListener removeMemverListener;

    public RemoveMemberProcessor(Context context) {
        this.context = context;
    }

    public void setRemoveMemverListener(OnRemoveMemverListener removeMemverListener) {
        this.removeMemverListener = removeMemverListener;
    }

    //移除群成员
    public void removeMember(String groupIdentify, List<String> userIdentify){

        TIMGroupManager.getInstance().deleteGroupMember(groupIdentify, userIdentify, new TIMValueCallBack<List<TIMGroupMemberResult>>() {
            @Override
            public void onError(int i, String s) {
                removeMemverListener.removeFailed(i);
            }

            @Override
            public void onSuccess(List<TIMGroupMemberResult> timGroupMemberResults) {
                removeMemverListener.removeSuccess();
            }
        });

    }

    //退出群聊
    public void quitGroup(String identify) {
        GroupManagerPresenter.quitGroup(identify, new TIMCallBack() {
            @Override
            public void onError(int i, String s) {
                removeMemverListener.removeFailed(i);
            }

            @Override
            public void onSuccess() {
                removeMemverListener.removeSuccess();
            }
        });
    }

    public interface OnRemoveMemverListener{
        void removeSuccess();
        void removeFailed(int errorId);
    }

}

