package com.tencent.qcloud.timchat.adapters;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import butterknife.BindView;
import butterknife.ButterKnife;
import com.bumptech.glide.Glide;
import com.tencent.qcloud.timchat.R;
import com.tencent.qcloud.timchat.R2;
import com.tencent.qcloud.timchat.chatmodel.CustomMessage;
import com.tencent.qcloud.timchat.chatmodel.Message;
import com.tencent.qcloud.timchat.chatmodel.ResumeModel;
import com.tencent.qcloud.timchat.chatutils.RecruitBusinessUtils;
import com.tencent.qcloud.timchat.widget.PhotoUtils;
import eu.davidea.flexibleadapter.FlexibleAdapter;
import eu.davidea.flexibleadapter.items.IFlexible;
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

  public ResumeModel getResume() {
    return (ResumeModel) ((CustomMessage) message).getData();
  }

  public String getResumeId() {
    ResumeModel r = (ResumeModel) ((CustomMessage) message).getData();
    if (r != null) {
      return r.id;
    } else {
      return "";
    }
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
      holder.resumeTextAge.setText(RecruitBusinessUtils.getAge(resumeModel.birthday));
      holder.resumeTextAgree.setText(
          RecruitBusinessUtils.getDegree(context, resumeModel.max_education));
      holder.resumeTextWorkYear.setText(RecruitBusinessUtils.getResumeWorkYear(resumeModel.work_year));
      holder.textClickResume.setText(
          context.getString(R.string.text_click_resume_name, resumeModel.username));
      holder.resumeGender.setImageResource(
          resumeModel.gender == 0 ? R.drawable.ic_gender_signal_male
              : R.drawable.ic_gender_signal_female);
    } else {
      Glide.with(context)
          .load(PhotoUtils.getSmall(resumeModel.avatar))
          .asBitmap()
          .into(holder.imgLeftResume);
      holder.leftResumeName.setText(resumeModel.username);
      holder.leftResumeTextAge.setText(RecruitBusinessUtils.getAge(resumeModel.birthday));
      holder.leftResumeTextAgree.setText(
          RecruitBusinessUtils.getDegree(context, resumeModel.max_education));
      holder.leftResumeTextWorkYear.setText(
          RecruitBusinessUtils.getResumeWorkYear(resumeModel.work_year));
      holder.leftTextClickResume.setText(
          context.getString(R.string.text_click_resume_name, resumeModel.username));
      holder.leftResumeGender.setImageResource(
          resumeModel.gender == 0 ? R.drawable.ic_gender_signal_male
              : R.drawable.ic_gender_signal_female);
    }
    message.showStatus(holder);
  }

  public class ResumeVH extends ChatItem.ViewHolder {
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
    @BindView(R2.id.leftMessage) RelativeLayout leftMessage;
    @BindView(R2.id.rightMessage) RelativeLayout rightMessage;
    public ResumeVH(View view, final FlexibleAdapter adapter) {
      super(view, adapter);
      ButterKnife.bind(this, view);
      leftMessage.setOnClickListener(new View.OnClickListener() {
        @Override public void onClick(View v) {
          jumpToResumeId(adapter);
        }
      });
      rightMessage.setOnClickListener(new View.OnClickListener() {
        @Override public void onClick(View v) {
          jumpToResumeId(adapter);
        }
      });

    }

    // TODO: 2017/6/24 硬编码 要改
    public void jumpToResumeId(FlexibleAdapter adapter){
      IFlexible item = adapter.getItem(getAdapterPosition());
      if (item instanceof ChatResumeItem){
        String id = ((ChatResumeItem) item).getResumeId();
        String packagename = context.getPackageName();
        String uri = "";
        if (packagename.contains("coach")) {
          uri = uri+"qccoach://resume/?id="+id;
        }else uri = uri+"qcstaff://resume/?id="+id;
        try{
          Intent toOut = new Intent();
          toOut.setPackage(context.getPackageName());
          toOut.setData(Uri.parse(uri));
          context.startActivity(toOut);
        }catch (Exception e){
          e.printStackTrace();
        }
      }
    }
  }
}

