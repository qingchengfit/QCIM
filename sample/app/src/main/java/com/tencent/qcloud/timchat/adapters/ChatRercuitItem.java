package com.tencent.qcloud.timchat.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import butterknife.BindView;
import butterknife.ButterKnife;
import com.bumptech.glide.Glide;
import com.tencent.qcloud.timchat.R;
import com.tencent.qcloud.timchat.R2;
import com.tencent.qcloud.timchat.chatmodel.Message;
import com.tencent.qcloud.timchat.chatmodel.RecruitModel;
import com.tencent.qcloud.timchat.chatutils.RecruitBusinessUtils;
import com.tencent.qcloud.timchat.widget.PhotoUtils;
import eu.davidea.flexibleadapter.FlexibleAdapter;
import java.util.List;

/**
 * Created by fb on 2017/6/14.
 */

public class ChatRercuitItem extends ChatItem<ChatRercuitItem.RecruitVH> {

  private RecruitModel recruitModel;
  private Context context;
  private Message message;

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

  @Override public Message getData() {
    return message;
  }

  @Override public void bindViewHolder(FlexibleAdapter adapter, RecruitVH holder, int position,
      List payloads) {

    if (recruitModel == null){
      return;
    }

    Glide.with(context)
        .load(PhotoUtils.getSmall(recruitModel.photo))
        .asBitmap()
        .into(holder.imgGym);

    holder.tvPositionName.setText(recruitModel.name);
    holder.tvSalary.setText(RecruitBusinessUtils.getSalary(recruitModel.min_salary, recruitModel.max_salary));
    holder.tvGymInfo.setText(recruitModel.address + "·" + recruitModel.gym_name);
    holder.tvWorkYear.setText(RecruitBusinessUtils.getWorkYear(recruitModel.min_work_year, recruitModel.max_work_year));
    holder.tvGender.setText(recruitModel.gender == 1 ? "男性" : "女性");
    holder.tvAge.setText(RecruitBusinessUtils.getAge(recruitModel.min_age, recruitModel.max_age));
    holder.tvHeight.setText(RecruitBusinessUtils.getHeight(recruitModel.min_height, recruitModel.max_height));
  }

  @Override public int getLayoutRes() {
    return R.layout.item_recruit;
  }

  class RecruitVH extends ChatItem.ViewHolder {

    @BindView(R2.id.img_gym) ImageView imgGym;
    @BindView(R2.id.tv_position_name) TextView tvPositionName;
    @BindView(R2.id.tv_salary) TextView tvSalary;
    @BindView(R2.id.tv_gym_info) TextView tvGymInfo;
    @BindView(R2.id.tv_work_year) TextView tvWorkYear;
    @BindView(R2.id.tv_gender) TextView tvGender;
    @BindView(R2.id.tv_age) TextView tvAge;
    @BindView(R2.id.tv_height) TextView tvHeight;
    @BindView(R2.id.layout_limit) LinearLayout layoutLimit;

    public RecruitVH(View view, FlexibleAdapter adapter) {
      super(view, adapter);
      ButterKnife.bind(this, view);
    }
  }
}
