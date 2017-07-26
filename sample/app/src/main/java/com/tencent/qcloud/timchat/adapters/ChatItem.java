package com.tencent.qcloud.timchat.adapters;

import android.content.Context;
import android.support.v7.app.AppCompatDelegate;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.tencent.TIMConversationType;
import com.tencent.TIMFriendshipManager;
import com.tencent.TIMUserProfile;
import com.tencent.TIMValueCallBack;
import com.tencent.qcloud.timchat.R;
import com.tencent.qcloud.timchat.chatmodel.Message;
import com.tencent.qcloud.timchat.chatutils.TimeUtil;
import com.tencent.qcloud.timchat.common.AppData;
import com.tencent.qcloud.timchat.common.Util;
import com.tencent.qcloud.timchat.widget.CircleImageView;
import com.tencent.qcloud.timchat.widget.PhotoUtils;

import java.util.ArrayList;
import java.util.List;

import eu.davidea.flexibleadapter.FlexibleAdapter;
import eu.davidea.flexibleadapter.items.AbstractFlexibleItem;
import eu.davidea.viewholders.FlexibleViewHolder;

/**
 * 聊天界面adapter
 */
public class ChatItem<T extends ChatItem.ViewHolder> extends AbstractFlexibleItem<T> {

    private final String TAG = "ChatItem";

    private String avatar;
    public Message message;
    private Context context;
    private OnDeleteMessageItem onDeleteMessageItem;

    /**
     * Constructor
     *
     * @param context  The current context.
     */
    public ChatItem(Context context, Message message, String avatar, OnDeleteMessageItem onDeleteMessageItem) {
        this.context = context;
        this.message = message;
        this.avatar = avatar;
        this.onDeleteMessageItem = onDeleteMessageItem;
    }

    public ChatItem(Context context){
        this.context = context;
    }

    public void setAvatar(String avatar) {
        this.avatar = avatar;

    }

    public Message getData(){
        return message;
    }

    @Override
    public T createViewHolder(final FlexibleAdapter adapter, LayoutInflater inflater, ViewGroup parent) {
        T vh = (T) new ViewHolder(inflater.inflate(getLayoutRes(), parent, false), adapter);
        //leftMessage.setOnLongClickListener(new View.OnLongClickListener() {
        //    @Override
        //    public boolean onLongClick(View view) {
        //        int pos = adapter.;
        //        if (onDeleteMessageItem != null){
        //            onDeleteMessageItem.onDelete(pos);
        //        }
        //        return false;
        //    }
        //});
        //rightMessage.setOnLongClickListener(new View.OnLongClickListener() {
        //    @Override
        //    public boolean onLongClick(View view) {
        //        int pos = getAdapterPosition();
        //        if (onDeleteMessageItem != null){
        //            onDeleteMessageItem.onDelete(pos);
        //        }
        //        return false;
        //    }
        //});
        return vh;
    }

    @Override
    public int getLayoutRes() {
        return R.layout.item_message;
    }

    @Override
    public void bindViewHolder(FlexibleAdapter adapter, T holder, int position, List payloads) {
        if (!message.isSelf()) {
            Glide.with(context)
                .load(PhotoUtils.getSmall(TextUtils.isEmpty(avatar) ? AppData.defaultAvatar : avatar))
                .asBitmap()
                .into(holder.leftAvatar);
        }else {
            Glide.with(context)
                .load(PhotoUtils.getSmall(AppData.getAvatar(context)))
                .asBitmap()
                .into(holder.rightAvatar);
        }

      message.getBubbleView(holder);
    }

    @Override
    public boolean equals(Object o) {
        return false;
    }

    public static class ViewHolder extends FlexibleViewHolder{
        public RelativeLayout leftPanel;
        public RelativeLayout rightPanel;
        public RelativeLayout senderLayout;
        public ProgressBar sending;
        public ImageView error;
        public TextView sender;
        public TextView systemMessage;
        public TextView rightDesc;
        public TextView leftVoice;
        public TextView rightVoice;
        public CircleImageView leftAvatar;
        public CircleImageView rightAvatar;

        public ViewHolder(View view, FlexibleAdapter adapter) {
            super(view, adapter);
            leftPanel = (RelativeLayout) view.findViewById(R.id.leftPanel);
            rightPanel = (RelativeLayout) view.findViewById(R.id.rightPanel);
            sending = (ProgressBar) view.findViewById(R.id.sending);
            error = (ImageView) view.findViewById(R.id.sendError);
            sender = (TextView) view.findViewById(R.id.sender);
            rightDesc = (TextView) view.findViewById(R.id.rightDesc);
            systemMessage = (TextView) view.findViewById(R.id.systemMessage);
            senderLayout = (RelativeLayout) view.findViewById(R.id.sendStatus);
            leftAvatar = (CircleImageView) view.findViewById(R.id.leftAvatar);
            rightAvatar = (CircleImageView) view.findViewById(R.id.rightAvatar);
        }
    }

    public interface OnDeleteMessageItem{
        void onDelete(int position);
    }
}
