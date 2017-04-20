package com.tencent.qcloud.timchat.adapters;

import android.content.Context;
import android.graphics.drawable.BitmapDrawable;
import android.provider.ContactsContract;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.tencent.qcloud.timchat.R;
import com.tencent.qcloud.timchat.chatmodel.GroupMemberProfile;
import com.tencent.qcloud.timchat.chatmodel.ProfileSummary;
import com.tencent.qcloud.timchat.common.AppData;
import com.tencent.qcloud.timchat.widget.CircleImgWrapper;
import com.tencent.qcloud.timchat.widget.PhotoUtils;

import java.util.List;

import eu.davidea.flexibleadapter.FlexibleAdapter;
import eu.davidea.flexibleadapter.items.AbstractFlexibleItem;
import eu.davidea.viewholders.FlexibleViewHolder;

/**
 * 好友或群等资料摘要列表的adapter
 */
public class ProfileSummaryItem extends AbstractFlexibleItem<ProfileSummaryItem.ProfileVh> implements View.OnClickListener {


    private View view;
    private GroupMemberProfile profileSummary;
    private boolean isDelete;
    private OnDeleteMemberListener onDeleteMemberListener;

    /**
     * Constructor
     *
     * @param context  The current context.
     * @param objects  The objects to represent in the ListView.
     */
    public ProfileSummaryItem(Context context, GroupMemberProfile objects) {
        this.profileSummary = objects;
    }

    public boolean isDelete() {
        return isDelete;
    }

    public void setDelete(boolean delete) {
        isDelete = delete;
    }

    public void setOnDeleteMemberListener(OnDeleteMemberListener onDeleteMemberListener) {
        this.onDeleteMemberListener = onDeleteMemberListener;
    }

    public GroupMemberProfile getData(){
        return profileSummary;
    }

    @Override
    public boolean equals(Object o) {
        return false;
    }

    @Override
    public ProfileVh createViewHolder(FlexibleAdapter adapter, LayoutInflater inflater, ViewGroup parent) {
        ProfileVh vh = new ProfileVh(inflater.inflate(R.layout.item_group_detail_grid, parent, false), adapter);
        if (isDelete){
            vh.imgDelete.setOnClickListener(this);
        }
        return vh;
    }

    @Override
    public void bindViewHolder(FlexibleAdapter adapter, ProfileVh holder, int position, List payloads) {
        if (isDelete){
            holder.imgDelete.setImageResource(R.drawable.ic_delete_member);
        }else{
            holder.imgDelete.setVisibility(View.GONE);
        }

        if (profileSummary.getType() == GroupMemberProfile.ADD && !isDelete){
            holder.avatar.setImageResource(profileSummary.getAvatarRes());
        }
        if (profileSummary.getType() == GroupMemberProfile.REMOVE && !isDelete){
            holder.avatar.setImageResource(profileSummary.getAvatarRes());
        }
        if (profileSummary.getType() == GroupMemberProfile.NORMAL) {
            String url = profileSummary.getAvatarUrl();
            Glide.with(holder.itemView.getContext())
                    .load(PhotoUtils.getSmall(TextUtils.isEmpty(url) ? AppData.defaultAvatar : url))
                    .asBitmap()
                    .into(new CircleImgWrapper(holder.avatar,
                            holder.itemView.getContext()));
            holder.name.setText(profileSummary.getName());
        }
        holder.imgDelete.setTag(position);
    }

    @Override
    public void onClick(View view) {
        if (onDeleteMemberListener != null){
            onDeleteMemberListener.onDelete((int)view.getTag());
        }
    }

    public class ProfileVh extends FlexibleViewHolder{
        public ImageView avatar;
        public TextView name;
        public ImageView imgDelete;

        public ProfileVh(View view, FlexibleAdapter adapter) {
            super(view, adapter);
            avatar = (ImageView) view.findViewById(R.id.group_member_avatar);
            name = (TextView) view.findViewById(R.id.group_member_name);
            imgDelete = (ImageView) view.findViewById(R.id.image_delete_member);
        }
    }

    public interface OnDeleteMemberListener{
        void onDelete(int position);
    }
}
