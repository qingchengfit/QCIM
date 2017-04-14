package com.tencent.qcloud.timchat.ui.qcchat;

import android.content.Context;
import android.content.Intent;
import android.widget.Toast;

import com.tencent.TIMCallBack;
import com.tencent.TIMConversationType;
import com.tencent.TIMFriendshipManager;
import com.tencent.TIMGroupManager;
import com.tencent.TIMManager;
import com.tencent.TIMUserProfile;
import com.tencent.TIMValueCallBack;
import com.tencent.qcloud.timchat.Common.Configs;
import com.tencent.qcloud.timchat.chatmodel.FriendProfile;
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
    public void createGroupWithArg(List<String> datas, final String avatorUrl){
        if (datas.size() == 1){
            ChatActivity.navToChat(context, datas.get(0), TIMConversationType.C2C);
        }else {
            GroupManagerPresenter.createGroup(getDefaultGroupName(datas),
                    GroupInfo.privateGroup,
                    datas,
                    new TIMValueCallBack<String>() {
                        @Override
                        public void onError(int i, String s) {

                        }

                        @Override
                        public void onSuccess(String s) {
                            Intent intent = new Intent(context, ChatActivity.class);
                            intent.putExtra(Configs.IDENTIFY, s);
                            intent.putExtra(Configs.CONVERSATION_TYPE, TIMConversationType.Group);
                            context.startActivity(intent);
                            TIMGroupManager.getInstance().modifyGroupFaceUrl(s, avatorUrl, new TIMCallBack() {
                                @Override
                                public void onError(int i, String s) {
                                }

                                @Override
                                public void onSuccess() {
                                    Util.showToast(context, "创建群成功");
                                }
                            });
                        }
                    }
            );
        }
    }

    private String getDefaultGroupName(List<String> datas){

        final StringBuilder s = new StringBuilder();

        TIMFriendshipManager.getInstance().getFriendsProfile(datas, new TIMValueCallBack<List<TIMUserProfile>>() {
            @Override
            public void onError(int i, String s) {
                onCreateConversation.onCreateFailed(i);
            }

            @Override
            public void onSuccess(List<TIMUserProfile> timUserProfiles) {
                for (TIMUserProfile profile : timUserProfiles){
                    s.append(profile.getIdentifier());
                }
            }
        });

        return s.toString();
    }

    public interface OnCreateConversation{
        void onCreateSuccess(String id);
        void onCreateFailed(int errorCode);
    }

}
