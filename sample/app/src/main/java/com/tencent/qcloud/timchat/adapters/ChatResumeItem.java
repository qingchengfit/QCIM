package com.tencent.qcloud.timchat.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import butterknife.BindView;
import butterknife.ButterKnife;
import com.bumptech.glide.Glide;
import com.tencent.qcloud.timchat.R;
import com.tencent.qcloud.timchat.R2;
import com.tencent.qcloud.timchat.chatmodel.CustomMessage;
import com.tencent.qcloud.timchat.chatmodel.Message;
import com.tencent.qcloud.timchat.chatmodel.ResumeModel;
import com.tencent.qcloud.timchat.widget.PhotoUtils;
import eu.davidea.flexibleadapter.FlexibleAdapter;
import java.util.List;

/**
 * Created by fb on 2017/6/14.
 */

public class ChatResumeItem extends ChatItem<ChatResumeItem.ResumeVH> {

  private Message message;
  private Context context;

  public ChatResumeItem(Context context, Message message, String avatar,
      OnDeleteMessageItem onDeleteMessageItem) {
    super(context, message, avatar, onDeleteMessageItem);
    this.message = message;
    this.setAvatar(avatar);
    this.context = context;
  }

  @Override public int getLayoutRes() {
    return R.layout.item_resume_message;
  }

  @Override public Message getData() {
    return message;
  }

  @Override public ResumeVH createViewHolder(FlexibleAdapter adapter, LayoutInflater inflater,
      ViewGroup parent) {
    ResumeVH holder = new ResumeVH(inflater.inflate(getLayoutRes(), parent, false), adapter);
    return holder;
  }

  @Override public void bindViewHolder(FlexibleAdapter adapter, ResumeVH holder, int position,
      List payloads) {
    super.bindViewHolder(adapter, holder, position, payloads);
    ResumeModel resumeModel = (ResumeModel) ((CustomMessage) message).getData();
    if (resumeModel == null) {
      return;
    }
    if (message.isSelf()) {
      Glide.with(context)
          .load(PhotoUtils.getSmall(resumeModel.avatar))
          .asBitmap()
          .into(holder.imgResume);
      holder.resumeName.setText(resumeModel.username);
      holder.resumeTextAge.setText(resumeModel.birthday);
      holder.resumeTextAgree.setText(resumeModel.max_education + "");
      holder.resumeTextWorkYear.setText(resumeModel.work_year + "");
      holder.textClickResume.setText(context.getString(R.string.text_click_resume_name, resumeModel.username));
      holder.resumeGender.setImageResource(
          resumeModel.gender == 1 ? R.drawable.ic_gender_signal_male
              : R.drawable.ic_gender_signal_female);
    } else {
      Glide.with(context)
          .load(PhotoUtils.getSmall(resumeModel.avatar))
          .asBitmap()
          .into(holder.imgLeftResume);
      holder.leftResumeName.setText(resumeModel.username);
      holder.leftResumeTextAge.setText(resumeModel.birthday);
      holder.leftResumeTextAgree.setText(resumeModel.max_education + "");
      holder.leftResumeTextWorkYear.setText(resumeModel.work_year + "");
      holder.leftTextClickResume.setText(context.getString(R.string.text_click_resume_name, resumeModel.username));
      holder.leftResumeGender.setImageResource(
          resumeModel.gender == 1 ? R.drawable.ic_gender_signal_male
              : R.drawable.ic_gender_signal_female);
    }
  }

  class ResumeVH extends ChatItem.ViewHolder {

    @BindView(R2.id.img_resume) ImageView imgResume;
    @BindView(R2.id.resume_name) TextView resumeName;
    @BindView(R2.id.resume_gender) ImageView resumeGender;
    @BindView(R2.id.resume_text_work_year) TextView resumeTextWorkYear;
    @BindView(R2.id.resume_agree) ImageView resumeAgree;
    @BindView(R2.id.resume_text_agree) TextView resumeTextAgree;
    @BindView(R2.id.resume_age) ImageView resumeAge;
    @BindView(R2.id.resume_text_age) TextView resumeTextAge;
    @BindView(R2.id.text_click_resume) TextView textClickResume;
    @BindView(R2.id.img_left_resume) ImageView imgLeftResume;
    @BindView(R2.id.left_resume_name) TextView leftResumeName;
    @BindView(R2.id.left_resume_gender) ImageView leftResumeGender;
    @BindView(R2.id.left_resume_text_work_year) TextView leftResumeTextWorkYear;
    @BindView(R2.id.left_resume_text_agree) TextView leftResumeTextAgree;
    @BindView(R2.id.left_resume_text_age) TextView leftResumeTextAge;
    @BindView(R2.id.left_text_click_resume) TextView leftTextClickResume;

    public ResumeVH(View view, FlexibleAdapter adapter) {
      super(view, adapter);
      ButterKnife.bind(this, view);
    }
  }
}

