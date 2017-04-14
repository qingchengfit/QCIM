package com.tencent.qcloud.timchat.adapters;

import android.content.Context;
import android.support.v4.content.ContextCompat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.tencent.qcloud.timchat.R;
import com.tencent.qcloud.timchat.chatmodel.ProfileSummary;
import com.tencent.qcloud.timchat.widget.CircleImageView;
import com.tencent.qcloud.tlslibrary.helper.Util;

import java.util.List;

import eu.davidea.flexibleadapter.FlexibleAdapter;

/**
 * 好友或群等资料摘要列表的adapter
 */
public class ProfileSummaryAdapter extends ArrayAdapter<ProfileSummary> implements View.OnClickListener {


    private int resourceId;
    private View view;
    private ViewHolder viewHolder;
    private List<ProfileSummary> list;
    private boolean isDelete;
    private OnDeleteMemberListener onDeleteMemberListener;

    /**
     * Constructor
     *
     * @param context  The current context.
     * @param resource The resource ID for a layout file containing a TextView to use when
     *                 instantiating views.
     * @param objects  The objects to represent in the ListView.
     */
    public ProfileSummaryAdapter(Context context, int resource, List<ProfileSummary> objects) {
        super(context, resource, objects);
        resourceId = resource;
        this.list = objects;
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
    public int getCount() {
        if (isDelete) {
            return list.size();
        }else{
            return list.size() + 2;
        }
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        if (convertView != null) {
            view = convertView;
            viewHolder = (ViewHolder) view.getTag();
        } else {
            view = LayoutInflater.from(getContext()).inflate(resourceId, null, false);
            viewHolder = new ViewHolder();
            viewHolder.avatar = (CircleImageView) view.findViewById(R.id.avatar);
            viewHolder.name = (TextView) view.findViewById(R.id.name);
            viewHolder.imgDelete = (ImageView) view.findViewById(R.id.image_delete_member);
            viewHolder.rlMemberItem = (RelativeLayout) view.findViewById(R.id.rl_member_item);
            viewHolder.imgDelete.setOnClickListener(this);
            view.setTag(viewHolder);
        }

        if (isDelete){
            viewHolder.imgDelete.setImageResource(R.drawable.ic_delete_member);
        }else{
            viewHolder.imgDelete.setVisibility(View.GONE);
        }

        if (position == list.size() + 1 && !isDelete){
            viewHolder.avatar.setImageResource(R.drawable.btn_minus);
        }
        if (position == list.size() && !isDelete){
            viewHolder.avatar.setImageResource(R.drawable.btn_add);
        }
        if (position < list.size()) {
            ProfileSummary data = getItem(position);
            viewHolder.avatar.setImageResource(data.getAvatarRes());
            viewHolder.name.setText(data.getName());
        }
        viewHolder.imgDelete.setTag(position);
        return view;
    }

    @Override
    public void onClick(View view) {
        if (onDeleteMemberListener != null){
            onDeleteMemberListener.onDelete(String.valueOf(view.getTag()));
        }
    }

    public class ViewHolder{
        public ImageView avatar;
        public TextView name;
        public ImageView imgDelete;
        public RelativeLayout rlMemberItem;
    }

    public interface OnDeleteMemberListener{
        void onDelete(String position);
    }
}
