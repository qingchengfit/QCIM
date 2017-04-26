package com.tencent.qcloud.timchat.adapters;

import android.content.Context;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.tencent.TIMFriendshipManager;
import com.tencent.TIMUserProfile;
import com.tencent.TIMValueCallBack;
import com.tencent.qcloud.timchat.R;
import com.tencent.qcloud.timchat.chatmodel.Message;
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
public class ChatItem extends AbstractFlexibleItem<ChatItem.ViewHolder> {

    private final String TAG = "ChatItem";

    private String avatar;
    private Message message;
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

    public void setAvatar(String avatar) {
        this.avatar = avatar;

    }

    public Message getData(){
        return message;
    }

    @Override
    public ViewHolder createViewHolder(FlexibleAdapter adapter, LayoutInflater inflater, ViewGroup parent) {
        ViewHolder vh = new ViewHolder(inflater.inflate(getLayoutRes(), parent, false), adapter);
        return vh;
    }

    @Override
    public int getLayoutRes() {
        return R.layout.item_message;
    }

    @Override
    public void bindViewHolder(FlexibleAdapter adapter, ViewHolder holder, int position, List payloads) {

        Glide.with(holder.leftAvatar.getContext())
                .load(PhotoUtils.getSmall(TextUtils.isEmpty(avatar) ? AppData.defaultAvatar : avatar))
                .asBitmap()
                .into(holder.leftAvatar);
        Glide.with(holder.rightAvatar.getContext())
                .load(PhotoUtils.getSmall(AppData.getAvatar(holder.rightAvatar.getContext())))
                .asBitmap()
                .into(holder.rightAvatar);


        message.showMessage(holder, context);
    }

    private void getAvatar(final ViewHolder holder){
        List<String> list = new ArrayList<>();
        list.add(message.getSender());
        TIMFriendshipManager.getInstance().getUsersProfile(list, new TIMValueCallBack<List<TIMUserProfile>>() {
            @Override
            public void onError(int i, String s) {
                Util.showToast(context, s);
            }

            @Override
            public void onSuccess(List<TIMUserProfile> timUserProfiles) {
                for (TIMUserProfile profile : timUserProfiles) {

                }
            }
        });
    }


    @Override
    public boolean equals(Object o) {
        return false;
    }

    public class ViewHolder extends FlexibleViewHolder{
        public RelativeLayout leftMessage;
        public RelativeLayout rightMessage;
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
            leftMessage = (RelativeLayout) view.findViewById(R.id.leftMessage);
            leftMessage.setMinimumWidth(Util.dpToPx(64f, view.getContext().getResources()));
            rightMessage = (RelativeLayout) view.findViewById(R.id.rightMessage);
            rightMessage.setMinimumWidth(Util.dpToPx(64f, view.getContext().getResources()));
            leftPanel = (RelativeLayout) view.findViewById(R.id.leftPanel);
            leftVoice = (TextView) view.findViewById(R.id.text_voice_time_left);
            leftVoice.setVisibility(View.GONE);
            rightPanel = (RelativeLayout) view.findViewById(R.id.rightPanel);
            sending = (ProgressBar) view.findViewById(R.id.sending);
            error = (ImageView) view.findViewById(R.id.sendError);
            sender = (TextView) view.findViewById(R.id.sender);
            rightDesc = (TextView) view.findViewById(R.id.rightDesc);
            rightVoice = (TextView) view.findViewById(R.id.text_voice_time_right);
            leftVoice.setVisibility(View.GONE);
            systemMessage = (TextView) view.findViewById(R.id.systemMessage);
            senderLayout = (RelativeLayout) view.findViewById(R.id.sendStatus);
            leftAvatar = (CircleImageView) view.findViewById(R.id.leftAvatar);
            rightAvatar = (CircleImageView) view.findViewById(R.id.rightAvatar);
            leftMessage.setOnLongClickListener(new View.OnLongClickListener() {
                @Override
                public boolean onLongClick(View view) {
                    int pos = getAdapterPosition();
                    if (onDeleteMessageItem != null){
                        onDeleteMessageItem.onDelete(pos);
                    }
                    return false;
                }
            });
            rightMessage.setOnLongClickListener(new View.OnLongClickListener() {
                @Override
                public boolean onLongClick(View view) {
                    int pos = getAdapterPosition();
                    if (onDeleteMessageItem != null){
                        onDeleteMessageItem.onDelete(pos);
                    }
                    return false;
                }
            });
        }
    }

    public interface OnDeleteMessageItem{
        void onDelete(int position);
    }
}
