package com.tencent.qcloud.timchat.adapters;

import android.content.Context;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import butterknife.BindView;
import butterknife.ButterKnife;
import com.bumptech.glide.Glide;
import com.tencent.qcloud.timchat.R;
import com.tencent.qcloud.timchat.R2;
import com.tencent.qcloud.timchat.chatmodel.Message;
import com.tencent.qcloud.timchat.chatmodel.RecruitModel;
import com.tencent.qcloud.timchat.chatutils.RecruitBusinessUtils;
import com.tencent.qcloud.timchat.widget.CircleImageView;
import com.tencent.qcloud.timchat.widget.PhotoUtils;
import eu.davidea.flexibleadapter.FlexibleAdapter;
import java.util.List;

/**
 * Created by fb on 2017/6/14.
 */

public class ChatRercuitItem extends ChatItem<ChatRercuitItem.RecruitVH> {


  private RecruitModel recruitModel;
  private Context context;

  /**
   * Constructor
   *
   * @param context The current context.
   */
  public ChatRercuitItem(Context context, Message message, String avatar,
      OnDeleteMessageItem onDeleteMessageItem) {
    super(context, message, avatar, onDeleteMessageItem);
    this.message = message;
  }

  public ChatRercuitItem(Context context, RecruitModel recruitModel, Message message) {
    super(context);
    this.context = context;
    this.recruitModel = recruitModel;
    this.message = message;
  }

  @Override public RecruitVH createViewHolder(FlexibleAdapter adapter, LayoutInflater inflater,
      ViewGroup parent) {
    RecruitVH holder = new RecruitVH(inflater.inflate(getLayoutRes(), parent, false), adapter);
    return holder;
  }

  public String getJobId() {
    if (recruitModel != null) {
      return recruitModel.id;
    } else {
      return "";
    }
  }

  @Override public Message getData() {
    return message;
  }

  @Override public void bindViewHolder(FlexibleAdapter adapter, RecruitVH holder, int position,
      List payloads) {
    super.bindViewHolder(adapter, holder, position, payloads);
    if (recruitModel == null) {
      return;
    }
    if (message.isSelf()) {
      Glide.with(context).load(PhotoUtils.getSmall(recruitModel.photo)).asBitmap().into(holder.imgLeftInviteRecruit);

      holder.tvLeftInviteName.setText(recruitModel.name);
      holder.leftInviteRecruitSalary.setText(
          RecruitBusinessUtils.getSalary(recruitModel.min_salary, recruitModel.max_salary, "面议"));
      holder.leftInviteAddress.setText(recruitModel.address + (TextUtils.isEmpty(recruitModel.gym_name) ? ""
          : "·" + recruitModel.gym_name));
    }else{
      Glide.with(context).load(PhotoUtils.getSmall(recruitModel.photo)).asBitmap().into(holder.imgRightInviteRecruit);

      holder.tvRightInviteName.setText(recruitModel.name);
      holder.rightInviteRecruitSalary.setText(
          RecruitBusinessUtils.getSalary(recruitModel.min_salary, recruitModel.max_salary, "面议"));
      holder.rightInviteAddress.setText(recruitModel.address + (TextUtils.isEmpty(recruitModel.gym_name) ? ""
          : "·" + recruitModel.gym_name));
    }
    message.showStatus(holder);
  }

  @Override public int getLayoutRes() {
    return R.layout.item_send_invite;
  }

  class RecruitVH extends ChatItem.ViewHolder {

    @BindView(R2.id.img_left_invite_recruit) ImageView imgLeftInviteRecruit;
    @BindView(R2.id.tv_left_invite_name) TextView tvLeftInviteName;
    @BindView(R2.id.left_invite_recruit_salary) TextView leftInviteRecruitSalary;
    @BindView(R2.id.left_invite_address) TextView leftInviteAddress;
    @BindView(R2.id.leftMessage) RelativeLayout leftMessage;
    @BindView(R2.id.img_right_invite_recruit) ImageView imgRightInviteRecruit;
    @BindView(R2.id.tv_right_invite_name) TextView tvRightInviteName;
    @BindView(R2.id.right_invite_recruit_salary) TextView rightInviteRecruitSalary;
    @BindView(R2.id.right_invite_address) TextView rightInviteAddress;
    @BindView(R2.id.rightMessage) RelativeLayout rightMessage;

    public RecruitVH(View view, FlexibleAdapter adapter) {
      super(view, adapter);
      ButterKnife.bind(this, view);
    }
  }
}
