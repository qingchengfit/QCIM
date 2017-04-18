package com.tencent.qcloud.timchat.chatmodel;

import android.content.Context;

import com.tencent.TIMGroupMemberInfo;
import com.tencent.TIMGroupMemberRoleType;
import com.tencent.TIMUserProfile;
import com.tencent.qcloud.timchat.R;

import java.io.Serializable;

/**
 * 群成员数据
 */
public class GroupMemberProfile implements ProfileSummary,Serializable {
    public static final int NORMAL = 0;
    public static final int ADD = 1;
    public static final int REMOVE = 2;

    private String name;
    private String id;
    private long quietTime;
    private TIMGroupMemberRoleType roleType;
    private String headUrl;
    private int type = NORMAL;

    public GroupMemberProfile(TIMUserProfile info){
        name = info.getNickName();
        id = info.getIdentifier();
        headUrl = info.getFaceUrl();
    }

    public GroupMemberProfile(int type) {
        this.type = type;
    }

    public void setType(int type) {
        this.type = type;
    }

    public int getType(){
        return type;
    }

    /**
     * 获取头像资源
     */
    @Override
    public int getAvatarRes() {
        if (type == ADD){
            return R.drawable.btn_add;
        }else if(type == REMOVE){
            return R.drawable.btn_minus;
        }
        return R.drawable.head;
    }

    /**
     * 获取头像地址
     */
    @Override
    public String getAvatarUrl() {
        return headUrl == null ? null : headUrl;
    }

    /**
     * 获取名字
     */
    @Override
    public String getName() {
        if (name != null && !name.equals("")){
            return name;
        }
        return id;
    }

    /**
     * 获取描述信息
     */
    @Override
    public String getDescription() {
        return null;
    }

    /**
     * 获取id
     */
    @Override
    public String getIdentify() {
        return id;
    }

    /**
     * 显示详情等点击事件
     *
     * @param context 上下文
     */
    @Override
    public void onClick(Context context) {

    }

    /**
     * 获取身份
     */
    public TIMGroupMemberRoleType getRole(){
        return roleType;
    }


    /**
     * 获取群名片
     */
    public String getNameCard(){
        if (name == null) return "";
        return name;
    }

    public long getQuietTime(){
        return quietTime;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setId(String id) {
        this.id = id;
    }

    public void setQuietTime(long quietTime) {
        this.quietTime = quietTime;
    }

    public void setRoleType(TIMGroupMemberRoleType roleType) {
        this.roleType = roleType;
    }
}
