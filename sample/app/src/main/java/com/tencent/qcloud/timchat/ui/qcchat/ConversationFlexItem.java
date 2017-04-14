package com.tencent.qcloud.timchat.ui.qcchat;

import android.content.Context;
import android.graphics.BitmapFactory;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.tencent.qcloud.timchat.R;
import com.tencent.qcloud.timchat.chatmodel.Conversation;
import com.tencent.qcloud.timchat.chatutils.TimeUtil;
import com.tencent.qcloud.timchat.widget.CircleImageView;
import com.tencent.qcloud.timchat.widget.PhotoUtils;

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

    @Override
    public void bindViewHolder(FlexibleAdapter adapter, ConversationViewHolder holder, int position, List payloads) {
        holder.tvName.setText(conversation.getName());
        Glide.with(context)
                .load(PhotoUtils.getSmall(conversation.getAvatar()))
                .asBitmap()
                .into(holder.avator);
        holder.lastMessage.setText(conversation.getLastMessageSummary());
        holder.time.setText(TimeUtil.getTimeStr(conversation.getLastMessageTime()));
        long unRead = conversation.getUnreadNum();
        if (unRead <= 0){
            holder.imageDot.setVisibility(View.GONE);
        }else{
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
