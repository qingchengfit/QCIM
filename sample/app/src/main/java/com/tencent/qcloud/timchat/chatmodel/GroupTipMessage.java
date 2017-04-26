package com.tencent.qcloud.timchat.chatmodel;

import android.content.Context;
import android.view.View;

import com.tencent.TIMGroupMemberInfo;
import com.tencent.TIMGroupTipsElem;
import com.tencent.TIMMessage;
import com.tencent.TIMUserProfile;
import com.tencent.qcloud.timchat.MyApplication;
import com.tencent.qcloud.timchat.R;
import com.tencent.qcloud.timchat.adapters.ChatItem;

import java.util.Iterator;
import java.util.Map;

/**
 * 群tips消息
 */
public class GroupTipMessage extends Message {


    public GroupTipMessage(TIMMessage message){
        this.message = message;
    }


    /**
     * 显示消息
     *
     * @param viewHolder 界面样式
     * @param context    显示消息的上下文
     */
    @Override
    public void showMessage(ChatItem.ViewHolder viewHolder, Context context) {
        viewHolder.leftPanel.setVisibility(View.GONE);
        viewHolder.rightPanel.setVisibility(View.GONE);
        viewHolder.systemMessage.setVisibility(View.VISIBLE);
        viewHolder.systemMessage.setText(getSummary());
    }

    /**
     * 获取消息摘要
     */
    @Override
    public String getSummary() {
        TIMGroupTipsElem e = null;
        if (message.getElement(0) != null && message.getElement(0) instanceof  TIMGroupTipsElem){
             e = (TIMGroupTipsElem) message.getElement(0);
        }else{
            return "";
        }
        StringBuilder stringBuilder = new StringBuilder();
        Iterator<Map.Entry<String,TIMUserProfile>> iterator = e.getChangedUserInfo().entrySet().iterator();
        switch (e.getTipsType()){
            case CancelAdmin:
            case SetAdmin:
                return MyApplication.getContext().getString(R.string.summary_group_admin_change);
            case Join:
                while(iterator.hasNext()){
                    Map.Entry<String,TIMUserProfile> item = iterator.next();
                    stringBuilder.append(item.getValue().getNickName());
                }
                return stringBuilder +
                        MyApplication.getContext().getString(R.string.summary_group_mem_add);
            case Kick:
                return e.getChangedUserInfo().get(e.getUserList().get(0)).getNickName() +
                        MyApplication.getContext().getString(R.string.summary_group_mem_kick);
            case ModifyMemberInfo:
                while(iterator.hasNext()){
                    Map.Entry<String,TIMUserProfile> item = iterator.next();
                    stringBuilder.append(item.getValue().getNickName());
                    stringBuilder.append(" ");
                }
                return stringBuilder +
                        MyApplication.getContext().getString(R.string.summary_group_mem_modify);
            case Quit:
                return e.getOpUser() +
                        MyApplication.getContext().getString(R.string.summary_group_mem_quit);
            case ModifyGroupInfo:
                return MyApplication.getContext().getString(R.string.summary_group_info_change);
        }
        return "";
    }

    /**
     * 保存消息或消息文件
     */
    @Override
    public void save() {

    }

    private String getName(TIMGroupMemberInfo info){
        if (info.getNameCard().equals("")){
            return info.getUser();
        }
        return info.getNameCard();
    }
}
