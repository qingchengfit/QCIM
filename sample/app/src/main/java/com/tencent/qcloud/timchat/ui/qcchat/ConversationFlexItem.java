package com.tencent.qcloud.timchat.ui.qcchat;

import android.content.Context;
import android.graphics.BitmapFactory;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.tencent.TIMConversationType;
import com.tencent.TIMFriendshipManager;
import com.tencent.TIMGroupDetailInfo;
import com.tencent.TIMGroupManager;
import com.tencent.TIMUserProfile;
import com.tencent.TIMValueCallBack;
import com.tencent.qcloud.timchat.R;
import com.tencent.qcloud.timchat.chatmodel.Conversation;
import com.tencent.qcloud.timchat.chatmodel.NomalConversation;
import com.tencent.qcloud.timchat.chatutils.TimeUtil;
import com.tencent.qcloud.timchat.common.AppData;
import com.tencent.qcloud.timchat.widget.CircleImageView;
import com.tencent.qcloud.timchat.widget.PhotoUtils;

import java.util.ArrayList;
import java.util.List;

import eu.davidea.flexibleadapter.FlexibleAdapter;
import eu.davidea.flexibleadapter.items.AbstractFlexibleItem;
import eu.davidea.viewholders.FlexibleViewHolder;

/**
 * Created by fb on 2017/3/17.
 */

public class ConversationFlexItem extends AbstractFlexibleItem<ConversationFlexItem.ConversationViewHolder> implements Comparable {

    private Conversation conversation;
    private Context context;

    public ConversationFlexItem(Context context, Conversation conversation) {
        this.context = context;
        this.conversation = conversation;
    }

    public Conversation getConversation() {
        return conversation;
    }

    public long getLastMessageTime(){
        if (conversation != null){
            return conversation.getLastMessageTime();
        }
        return 0;
    }

    private void getPersonalAvatar(final List<String> list, final ConversationViewHolder holder){
        TIMFriendshipManager.getInstance().getUsersProfile(list, new TIMValueCallBack<List<TIMUserProfile>>() {
            @Override
            public void onError(int i, String s) {

            }

            @Override
            public void onSuccess(List<TIMUserProfile> timUserProfiles) {
                for (TIMUserProfile profile : timUserProfiles) {
                    Glide.with(context)
                            .load(PhotoUtils.getSmall(TextUtils.isEmpty(profile.getFaceUrl()) ?
                                    AppData.defaultAvatar : profile.getFaceUrl()))
                            .asBitmap()
                            .into(holder.avator);
                    holder.tvName.setText(profile.getNickName());

                }
            }
        });
    }

    private void getGroupAvator(List<String> list, final ConversationViewHolder holder){
        TIMGroupManager.getInstance().getGroupDetailInfo(list, new TIMValueCallBack<List<TIMGroupDetailInfo>>() {
            @Override
            public void onError(int i, String s) {

            }

            @Override
            public void onSuccess(List<TIMGroupDetailInfo> timGroupDetailInfos) {
                for (TIMGroupDetailInfo profile : timGroupDetailInfos) {
                    Glide.with(context)
                            .load(PhotoUtils.getSmall(TextUtils.isEmpty(profile.getFaceUrl()) ?
                                    AppData.defaultGroupAvatar : profile.getFaceUrl()))
                            .asBitmap()
                            .into(holder.avator);
                    holder.tvName.setText(profile.getGroupName());

                }
            }
        });
    }

    @Override
    public void bindViewHolder(FlexibleAdapter adapter, final ConversationViewHolder holder, int position, final List payloads) {
        List<String> list = new ArrayList<>();
        list.add(conversation.getIdentify());
        if (((NomalConversation)conversation).getType() == TIMConversationType.Group){
            getGroupAvator(list, holder);
        }else{
            getPersonalAvatar(list, holder);
        }

        holder.lastMessage.setText(conversation.getLastMessageSummary());
        holder.time.setText(TimeUtil.getTimeStr(conversation.getLastMessageTime()));
        long unRead = conversation.getUnreadNum();
        if (unRead <= 0) {
            holder.imageDot.setVisibility(View.GONE);
        } else {
            holder.imageDot.setVisibility(View.VISIBLE);
            String unReadStr = String.valueOf(unRead);
//            if (unRead < 10){
//                holder.unread.setBackground(context.getResources().getDrawable(R.drawable.point1));
//            }else{
//                holder.unread.setBackground(context.getResources().getDrawable(R.drawable.point2));
//                if (unRead > 99){
//                    unReadStr = context.getResources().getString(R.string.time_more);
//                }
//            }
//            holder.unread.setText(unReadStr);
        }
    }

    @Override
    public ConversationViewHolder createViewHolder(FlexibleAdapter adapter, LayoutInflater inflater, ViewGroup parent) {
        ConversationViewHolder vh = new ConversationViewHolder(inflater.inflate(R.layout.item_conversation, parent, false), adapter);
        return vh;
    }

    @Override
    public boolean equals(Object o) {
        return false;
    }

    @Override
    public int compareTo(@NonNull Object o) {
        if (o instanceof ConversationFlexItem){
            ConversationFlexItem item = (ConversationFlexItem) o;
            long timeGap = item.getConversation().getLastMessageTime() - getLastMessageTime();
            if (timeGap > 0) return  1;
            else if (timeGap < 0) return -1;
            return 0;
        }else{
            throw new ClassCastException();
        }
    }

    class ConversationViewHolder extends FlexibleViewHolder{

        private TextView tvName;
        private CircleImageView avator;
        private TextView lastMessage;
        private TextView time;
        private ImageView imageDot;

        public ConversationViewHolder(View view, FlexibleAdapter adapter) {
            super(view, adapter);
            tvName = (TextView) view.findViewById(R.id.name);
            avator = (CircleImageView) view.findViewById(R.id.avatar);
            lastMessage = (TextView) view.findViewById(R.id.last_message);
            time = (TextView) view.findViewById(R.id.message_time);
            imageDot = (ImageView) view.findViewById(R.id.new_red_dot);
        }



    }

}
