package com.tencent.qcloud.timchat.ui.qcchat;

import android.content.Context;
import android.content.Intent;

import com.tencent.TIMCallBack;
import com.tencent.TIMConversationType;
import com.tencent.TIMFriendshipManager;
import com.tencent.TIMGroupManager;
import com.tencent.TIMUserProfile;
import com.tencent.TIMValueCallBack;
import com.tencent.qcloud.timchat.common.AppData;
import com.tencent.qcloud.timchat.common.Configs;
import com.tencent.qcloud.timchat.chatmodel.GroupInfo;
import com.tencent.qcloud.timchat.presenter.FriendshipManagerPresenter;
import com.tencent.qcloud.timchat.presenter.GroupManagerPresenter;
import com.tencent.qcloud.tlslibrary.helper.Util;

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

    /**
     * @param datas    id列表
     */
    public void createGroupWithArg(final List<String> datas, final String name) {

        TIMGroupManager.getInstance().createGroup(GroupInfo.privateGroup,
                datas,
                name,
                new TIMValueCallBack<String>() {
                    @Override
                    public void onError(int i, String s) {
                        onCreateConversation.onCreateFailed(i, s);
                    }

                    @Override
                    public void onSuccess(final String s) {
                        setGroupAvatar(s, AppData.defaultGroupAvatar);
                    }
                }
        );
    }

    public void setGroupAvatar(final String groupId, String avatarUrl){
        TIMGroupManager.getInstance().modifyGroupFaceUrl(groupId, avatarUrl, new TIMCallBack() {
            @Override
            public void onError(int i, String s) {
                onCreateConversation.onCreateFailed(i, s);
            }

            @Override
            public void onSuccess() {
                Util.showToast(context, "创建群成功");
                Intent intent = new Intent(context, ChatActivity.class);
                intent.putExtra(Configs.IDENTIFY, groupId);
                intent.putExtra(Configs.CONVERSATION_TYPE, TIMConversationType.Group);
                context.startActivity(intent);
            }
        });
    }

    //
    public void creaetGroupWithName(final List<String> datas){

        if (datas.size() == 1) {
            ChatActivity.navToChat(context, datas.get(0), TIMConversationType.C2C);
        } else {
            final StringBuilder s = new StringBuilder();
            TIMFriendshipManager.getInstance().getUsersProfile(datas, new TIMValueCallBack<List<TIMUserProfile>>() {
                @Override
                public void onError(int i, String s) {
                    onCreateConversation.onCreateFailed(i, s);
                }

                @Override
                public void onSuccess(List<TIMUserProfile> timUserProfiles) {
                    int index = 0;
                    for (TIMUserProfile profile : timUserProfiles) {
                        if (index > 2) {
                            break;
                        }
                        s.append(profile.getNickName()).append("(").append(timUserProfiles.size()).append(")人");
                        index++;
                    }
                    createGroupWithArg(datas, s.toString());
                }
            });
        }

    }

    public interface OnCreateConversation{
        void onCreateSuccess(String id);
        void onCreateFailed(int errorCode, String s);
    }

}
