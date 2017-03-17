package com.tencent.qcloud.timchat.ui.qcchat;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.tencent.qcloud.timchat.R;
import com.tencent.qcloud.timchat.chatmodel.Conversation;
import com.tencent.qcloud.timchat.chatutils.TimeUtil;
import com.tencent.qcloud.timchat.widget.CircleImageView;

import java.util.List;

import eu.davidea.flexibleadapter.FlexibleAdapter;
import eu.davidea.flexibleadapter.items.AbstractFlexibleItem;
import eu.davidea.viewholders.FlexibleViewHolder;

/**
 * Created by fb on 2017/3/17.
 */

public class ConversationFlexItem extends AbstractFlexibleItem<ConversationFlexItem.ConversationViewHolder> {

    private Conversation conversation;
    private Context context;

    public ConversationFlexItem(Context context, Conversation conversation) {
        this.context = context;
        this.conversation = conversation;
    }

    public Conversation getConversation() {
        return conversation;
    }

    @Override
    public void bindViewHolder(FlexibleAdapter adapter, ConversationViewHolder holder, int position, List payloads) {
        holder.tvName.setText(conversation.getName());
        holder.avator.setImageResource(conversation.getAvatar());
        holder.lastMessage.setText(conversation.getLastMessageSummary());
        holder.time.setText(TimeUtil.getTimeStr(conversation.getLastMessageTime()));
        long unRead = conversation.getUnreadNum();
        if (unRead <= 0){
            holder.unread.setVisibility(View.INVISIBLE);
        }else{
            holder.unread.setVisibility(View.VISIBLE);
            String unReadStr = String.valueOf(unRead);
            if (unRead < 10){
                holder.unread.setBackground(context.getResources().getDrawable(R.drawable.point1));
            }else{
                holder.unread.setBackground(context.getResources().getDrawable(R.drawable.point2));
                if (unRead > 99){
                    unReadStr = context.getResources().getString(R.string.time_more);
                }
            }
            holder.unread.setText(unReadStr);
        }
    }

    @Override
    public ConversationViewHolder createViewHolder(FlexibleAdapter adapter, LayoutInflater inflater, ViewGroup parent) {
        return super.createViewHolder(adapter, inflater, parent);
    }

    @Override
    public boolean equals(Object o) {
        return false;
    }

    class ConversationViewHolder extends FlexibleViewHolder{

        private TextView tvName;
        private CircleImageView avator;
        private TextView lastMessage;
        private TextView time;
        private TextView unread;

        public ConversationViewHolder(View view, FlexibleAdapter adapter) {
            super(view, adapter);
            tvName = (TextView) view.findViewById(R.id.name);
            avator = (CircleImageView) view.findViewById(R.id.avatar);
            lastMessage = (TextView) view.findViewById(R.id.last_message);
            time = (TextView) view.findViewById(R.id.message_time);
            unread = (TextView) view.findViewById(R.id.unread_num);
        }



    }

}
