package com.tencent.qcloud.timchat.ui.qcchat;

import com.tencent.qcloud.timchat.chatmodel.FriendProfile;

import java.util.List;

/**
 * Created by fb on 2017/3/16.
 */

public class ConversationBean {

    private List<String> identifyList;
    private List<FriendProfile> memberList;

    public ConversationBean() {
    }

    public void setIdentifyList(List<String> identifyList) {
        this.identifyList = identifyList;
    }

    public List<String> getIdentifyList() {
        return identifyList;
    }

    public void setMemberList(List<FriendProfile> memberList) {
        this.memberList = memberList;
    }

    public List<FriendProfile> getMemberList() {
        return memberList;
    }
}
