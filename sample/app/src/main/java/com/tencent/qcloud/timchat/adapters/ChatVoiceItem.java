package com.tencent.qcloud.timchat.adapters;

import android.content.Context;
import android.graphics.drawable.AnimationDrawable;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import butterknife.BindView;
import butterknife.ButterKnife;
import com.tencent.TIMMessage;
import com.tencent.TIMSoundElem;
import com.tencent.qcloud.timchat.R;
import com.tencent.qcloud.timchat.R2;
import com.tencent.qcloud.timchat.chatmodel.Message;
import com.tencent.qcloud.timchat.chatmodel.VoiceMessage;
import com.tencent.qcloud.timchat.common.Util;
import eu.davidea.flexibleadapter.FlexibleAdapter;
import java.util.List;

/**
 * Created by fb on 2017/5/22.
 */

public class ChatVoiceItem extends ChatItem<ChatVoiceItem.ChatVoiceVH>{

  private VoiceMessage voiceMessage;
  private Context context;
  private AnimationDrawable drawable;

  /**
   * Constructor
   *
   * @param context The current context.
   */
  public ChatVoiceItem(Context context, Message message, String avatar,
      OnDeleteMessageItem onDeleteMessageItem) {
    super(context, message, avatar, onDeleteMessageItem);
    this.voiceMessage = (VoiceMessage) message;
    this.context = context;
  }

  @Override public ChatVoiceVH createViewHolder(final FlexibleAdapter adapter, LayoutInflater inflater,
      ViewGroup parent) {
    ChatVoiceVH holder = new ChatVoiceVH(inflater.inflate(getLayoutRes(), parent, false), adapter);
    if (voiceMessage.isSelf()) {
      holder.layoutLeftMessage.setOnClickListener(new View.OnClickListener() {
        @Override public void onClick(View view) {
          if (drawable != null) {
          VoiceMessage message =
              (VoiceMessage) ((ChatVoiceItem)(adapter.getItem((int)(view.getTag())))).getData();
            message.playAudio(message.getMessage(),
                ((ChatVoiceItem)(adapter.getItem((int)(view.getTag())))).getDrawable());
          }
        }
      });
      holder.layoutRightMessage.setOnClickListener(new View.OnClickListener() {
        @Override public void onClick(View view) {
          if (drawable != null) {
            VoiceMessage message =
                (VoiceMessage) ((ChatVoiceItem)(adapter.getItem((int)(view.getTag())))).getData();
            message.playAudio(message.getMessage(),
                ((ChatVoiceItem)(adapter.getItem((int)(view.getTag())))).getDrawable());
          }
        }
      });
    }
    return holder;
  }

  @Override public Message getData() {
    return super.getData();
  }

  public AnimationDrawable getDrawable(){
    return drawable;
  }

  @Override public void bindViewHolder(FlexibleAdapter adapter, ChatVoiceVH holder, int position,
      List payloads) {
    super.bindViewHolder(adapter, holder, position, payloads);

    TIMMessage message = voiceMessage.getMessage();
    int mWidth;
    int duration = (int)((TIMSoundElem) message.getElement(0)).getDuration();
    if (duration > 3) {
      mWidth = 64 + (duration - 3) * 5;
      if (mWidth > 212){
        mWidth = 212;
      }
    }else{
      mWidth = 64;
    }

    if(voiceMessage.isSelf()){
      drawable = (AnimationDrawable) holder.voiceRightMessage.getDrawable();
      holder.textVoiceTimeLeft.setVisibility(View.GONE);
      holder.textVoiceTimeRight.setVisibility(View.VISIBLE);
      holder.textVoiceTimeRight.setText(duration + "'");
      holder.layoutRightMessage.getLayoutParams().width = Util.dpToPx(mWidth, context.getResources());
      holder.layoutRightMessage.setGravity(Gravity.RIGHT);
    }else{
      drawable = (AnimationDrawable) holder.voiceLeftMessage.getDrawable();
      holder.textVoiceTimeLeft.setVisibility(View.VISIBLE);
      holder.layoutLeftMessage.getLayoutParams().width = Util.dpToPx(mWidth, context.getResources());
      holder.textVoiceTimeRight.setVisibility(View.GONE);
      holder.layoutLeftMessage.setGravity(Gravity.LEFT);
      holder.textVoiceTimeLeft.setText(duration + "'");
    }
    holder.layoutLeftMessage.setTag(position);
    holder.layoutRightMessage.setTag(position);
    voiceMessage.showStatus(holder);
  }

  @Override public int getLayoutRes() {
    return R.layout.item_voice_message;
  }

  class ChatVoiceVH extends ChatItem.ViewHolder {
    @BindView(R2.id.voice_left_message) ImageView voiceLeftMessage;
    @BindView(R2.id.voice_right_message) ImageView voiceRightMessage;
    @BindView(R2.id.text_voice_time_left) TextView textVoiceTimeLeft;
    @BindView(R2.id.text_voice_time_right) TextView textVoiceTimeRight;
    @BindView(R2.id.rightMessage) RelativeLayout layoutRightMessage;
    @BindView(R2.id.leftMessage) RelativeLayout layoutLeftMessage;
    @BindView(R2.id.linearLeftMessage) LinearLayout linearLeftMessage;
    @BindView(R2.id.linearRightMessage) LinearLayout linearRightMessage;

    public ChatVoiceVH(View view, FlexibleAdapter adapter) {
      super(view, adapter);
      ButterKnife.bind(this, view);
    }
  }
}
