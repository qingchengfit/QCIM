package com.tencent.qcloud.timchat.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.tencent.qcloud.timchat.R;
import com.tencent.qcloud.timchat.chatmodel.ProfileSummary;

import java.util.List;

import eu.davidea.flexibleadapter.FlexibleAdapter;
import eu.davidea.flexibleadapter.items.AbstractFlexibleItem;
import eu.davidea.viewholders.FlexibleViewHolder;

/**
 * 好友或群等资料摘要列表的adapter
 */
public class ProfileSummaryItem extends AbstractFlexibleItem<ProfileSummaryItem.ProfileVh> implements View.OnClickListener {


    private View view;
    private ProfileSummary profileSummary;
    private boolean isDelete;
    private OnDeleteMemberListener onDeleteMemberListener;

    /**
     * Constructor
     *
     * @param context  The current context.
     * @param objects  The objects to represent in the ListView.
     */
    public ProfileSummaryItem(Context context, ProfileSummary objects) {
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

        if (position == adapter.getItemCount() + 1 && !isDelete){
            holder.avatar.setImageResource(R.drawable.btn_minus);
        }
        if (position == adapter.getItemCount() && !isDelete){
            holder.avatar.setImageResource(R.drawable.btn_add);
        }
        if (position < adapter.getItemCount()) {
            holder.avatar.setImageResource(profileSummary.getAvatarRes());
            holder.name.setText(profileSummary.getName());
        }
        holder.imgDelete.setTag(position);
    }

    @Override
    public void onClick(View view) {
        if (onDeleteMemberListener != null){
            onDeleteMemberListener.onDelete(String.valueOf(view.getTag()));
        }
    }

    public class ProfileVh extends FlexibleViewHolder{
        public ImageView avatar;
        public TextView name;
        public ImageView imgDelete;
        public RelativeLayout rlMemberItem;

        public ProfileVh(View view, FlexibleAdapter adapter) {
            super(view, adapter);
        }
    }

    public interface OnDeleteMemberListener{
        void onDelete(String position);
    }
}
