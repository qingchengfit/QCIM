package com.tencent.qcloud.timchat.ui.qcchat;

import android.content.Context;

import com.tencent.TIMConversationType;
import com.tencent.TIMGroupManager;
import com.tencent.TIMManager;
import com.tencent.TIMValueCallBack;
import com.tencent.qcloud.timchat.chatmodel.FriendProfile;
import com.tencent.qcloud.timchat.chatmodel.GroupInfo;
import com.tencent.qcloud.timchat.presenter.FriendshipManagerPresenter;
import com.tencent.qcloud.timchat.presenter.GroupManagerPresenter;

import java.util.List;

/**
 * Created by fb on 2017/3/14.
 */

//添加好友形成会话列表（单聊／群聊）
public class AddConversationProcessor {

    private Context context;
    private FriendshipManagerPresenter presenter;
    private OnCreateConversation onCreateConversation;

    public AddConversationProcessor(Context context) {
        this.context = context;
    }

    public void setOnCreateConversation(OnCreateConversation onCreateConversation) {
        this.onCreateConversation = onCreateConversation;
    }

    public void createGroupWithArg(List<String> datas, List<FriendProfile> friendProfileList){
        if (friendProfileList.size() == 1){
            ChatActivity.navToChat(context, datas.get(0), TIMConversationType.C2C);
        }else {
            GroupManagerPresenter.createGroup(getDefaultGroupName(friendProfileList),
                    GroupInfo.privateGroup,
                    datas,
                    new TIMValueCallBack<String>() {
                        @Override
                        public void onError(int i, String s) {
                            onCreateConversation.onCreateFailed(i);
                        }

                        @Override
                        public void onSuccess(String s) {
                            onCreateConversation.onCreateSuccess(s);
                        }
                    }
            );
        }
    }

    private String getDefaultGroupName(List<FriendProfile> datas){
        StringBuilder name = new StringBuilder();
        int i = 0;
        for (FriendProfile profile : datas){
            if (i >= 5){
                break;
            }
            name.append(profile.getIdentify() + ", ");     //将id拼接在一起组成Group的名称
            i++;
        }
        return name.toString();
    }

    public interface OnCreateConversation{
        void onCreateSuccess(String id);
        void onCreateFailed(int errorCode);
    }

}
