package com.tencent.qcloud.timchat.ui.qcchat;

import android.content.ContentResolver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.database.Cursor;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.PersistableBundle;
import android.provider.MediaStore;
import android.support.v4.content.ContextCompat;
import android.support.v4.content.FileProvider;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.app.AppCompatDelegate;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;
import android.widget.AbsListView;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import com.bumptech.glide.Glide;
import com.tencent.TIMConversationType;
import com.tencent.TIMFriendshipManager;
import com.tencent.TIMGroupManager;
import com.tencent.TIMGroupMemberInfo;
import com.tencent.TIMMessage;
import com.tencent.TIMMessageDraft;
import com.tencent.TIMMessageStatus;
import com.tencent.TIMUserProfile;
import com.tencent.TIMValueCallBack;
import com.tencent.qcloud.timchat.R;
import com.tencent.qcloud.timchat.R2;
import com.tencent.qcloud.timchat.adapters.ChatImageItem;
import com.tencent.qcloud.timchat.adapters.ChatItem;
import com.tencent.qcloud.timchat.adapters.ChatRercuitItem;
import com.tencent.qcloud.timchat.adapters.ChatResumeItem;
import com.tencent.qcloud.timchat.adapters.ChatTextItem;
import com.tencent.qcloud.timchat.adapters.ChatVoiceItem;
import com.tencent.qcloud.timchat.chatmodel.CustomMessage;
import com.tencent.qcloud.timchat.chatmodel.FileMessage;
import com.tencent.qcloud.timchat.chatmodel.GroupInfo;
import com.tencent.qcloud.timchat.chatmodel.ImageMessage;
import com.tencent.qcloud.timchat.chatmodel.Message;
import com.tencent.qcloud.timchat.chatmodel.MessageFactory;
import com.tencent.qcloud.timchat.chatmodel.RecruitModel;
import com.tencent.qcloud.timchat.chatmodel.ResumeModel;
import com.tencent.qcloud.timchat.chatmodel.TextMessage;
import com.tencent.qcloud.timchat.chatmodel.VideoMessage;
import com.tencent.qcloud.timchat.chatmodel.VoiceMessage;
import com.tencent.qcloud.timchat.chatutils.FileUtil;
import com.tencent.qcloud.timchat.chatutils.MediaUtil;
import com.tencent.qcloud.timchat.chatutils.RecorderUtil;
import com.tencent.qcloud.timchat.chatutils.RecruitBusinessUtils;
import com.tencent.qcloud.timchat.common.Configs;
import com.tencent.qcloud.timchat.common.Util;
import com.tencent.qcloud.timchat.presenter.ChatPresenter;
import com.tencent.qcloud.timchat.ui.GroupMemberActivity;
import com.tencent.qcloud.timchat.ui.ImagePreviewActivity;
import com.tencent.qcloud.timchat.viewfeatures.ChatView;
import com.tencent.qcloud.timchat.widget.ChatInput;
import com.tencent.qcloud.timchat.widget.PhotoUtils;
import com.tencent.qcloud.timchat.widget.TemplateTitle;
import com.tencent.qcloud.timchat.widget.VoiceSendingView;
import eu.davidea.flexibleadapter.FlexibleAdapter;
import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class ChatActivity extends AppCompatActivity
    implements ChatView, ChatItem.OnDeleteMessageItem, FlexibleAdapter.OnItemClickListener {

  static {
    AppCompatDelegate.setCompatVectorFromResourcesEnabled(true);
  }

  private static final String TAG = "ChatActivity";
  @BindView(R2.id.img_gym) ImageView imgGym;
  @BindView(R2.id.tv_position_name) TextView tvPositionName;
  @BindView(R2.id.tv_salary) TextView tvSalary;
  @BindView(R2.id.tv_gym_info) TextView tvGymInfo;
  @BindView(R2.id.tv_work_year) TextView tvWorkYear;
  @BindView(R2.id.tv_gender) TextView tvGender;
  @BindView(R2.id.tv_age) TextView tvAge;
  @BindView(R2.id.tv_height) TextView tvHeight;
  @BindView(R2.id.img_head_resume) ImageView imgHeadResume;
  @BindView(R2.id.tv_resume_name) TextView tvResumeName;
  @BindView(R2.id.img_resume_gender) ImageView imgResumeGender;
  @BindView(R2.id.tv_resume_info) TextView tvResumeInfo;
  @BindView(R2.id.tv_resume_detail) TextView tvResumeDetail;
  @BindView(R2.id.frame_detail_layout) FrameLayout frameDetailLayout;
  @BindView(R2.id.layout_recruit) View layoutRecruit;
  @BindView(R2.id.layout_resume) View layoutResume;
  @BindView(R2.id.chat_send_resume)
  public LinearLayout chatSendResume;
  @BindView(R2.id.chat_send_button)
  public TextView tvSendResume;
  @BindView(R2.id.frag_bottom_position_select)
  public FrameLayout fragBottomPosition;

  private List<ChatItem> itemList = new ArrayList<>();
  private FlexibleAdapter flexibleAdapter;
  private RecyclerView listView;
  private ChatPresenter presenter;
  private ChatInput input;
  private static final int CAPTURE_IMAGE_ACTIVITY_REQUEST_CODE = 100;
  private static final int IMAGE_STORE = 200;
  private static final int FILE_CODE = 300;
  private static final int IMAGE_PREVIEW = 400;
  private static final int MEMBER_OPERA = 500;
  private Uri fileUri;
  private VoiceSendingView voiceSendingView;
  private String identify;
  private RecorderUtil recorder = new RecorderUtil();
  private TIMConversationType type;
  private String titleStr;
  private Handler handler = new Handler();
  private TemplateTitle title;
  private String avatar;
  private boolean isC2C;
  private RelativeLayout root;
  private List<ChatItem> tempItemList = new ArrayList<>();
  private String resumeJson;
  private boolean isLatestMessage;

  public static void navToChat(Context context, String identify, TIMConversationType type) {
    Intent intent = new Intent(context, ChatActivity.class);
    intent.putExtra(Configs.IDENTIFY, identify);
    intent.putExtra(Configs.CONVERSATION_TYPE, type);
    context.startActivity(intent);
  }

  @Override protected void onNewIntent(Intent intent) {
    super.onNewIntent(intent);
    setIntent(intent);
  }

  @Override public void onSaveInstanceState(Bundle outState, PersistableBundle outPersistentState) {
  }

  @Override protected void onCreate(final Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_chat);
    ButterKnife.bind(this);
    getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN);
    title = (TemplateTitle) findViewById(R.id.chat_title);
    root = (RelativeLayout) findViewById(R.id.root);
    frameDetailLayout.setVisibility(View.GONE);
    chatSendResume.setVisibility(View.GONE);

    identify = getIntent().getStringExtra(Configs.IDENTIFY);
    if (getIntent().getStringExtra("groupName") != null) {
      titleStr = getIntent().getStringExtra("groupName");
      title.setTitleText(titleStr);
    }
    if (getIntent().getSerializableExtra(Configs.CONVERSATION_TYPE) != null) {
      type = (TIMConversationType) getIntent().getSerializableExtra(Configs.CONVERSATION_TYPE);
    }
    if (getIntent().getIntExtra(Configs.TEMP_CONVERSATION_TYPE, 0) != 0) {
      if (getIntent().getIntExtra(Configs.TEMP_CONVERSATION_TYPE, 0) == Configs.C2C) {
        type = TIMConversationType.C2C;
      } else {
        type = TIMConversationType.Group;
      }
    }
    presenter = new ChatPresenter(this, identify, type);
    input = (ChatInput) findViewById(R.id.input_panel);
    input.setChatView(this);
    flexibleAdapter = new FlexibleAdapter(itemList, this);
    listView = (RecyclerView) findViewById(R.id.list);
    //linearLayoutManager.setStackFromEnd(false);
    listView.setLayoutManager(
        new LinearLayoutManager(getBaseContext(), LinearLayoutManager.VERTICAL, true));
    LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getBaseContext());
    listView.setNestedScrollingEnabled(false);
    listView.setAdapter(flexibleAdapter);
    listView.setOnTouchListener(new View.OnTouchListener() {
      @Override public boolean onTouch(View v, MotionEvent event) {
        switch (event.getAction()) {
          case MotionEvent.ACTION_DOWN:
            input.setInputMode(ChatInput.InputMode.NONE);
            break;
        }
        return false;
      }
    });
    listView.setOnScrollListener(new RecyclerView.OnScrollListener() {

      private int firstItem;

      @Override public void onScrollStateChanged(RecyclerView view, int scrollState) {
        if (scrollState == AbsListView.OnScrollListener.SCROLL_STATE_IDLE
            && firstItem == itemList.size() - 1) {
          //如果拉到顶端读取更多消息
          presenter.getMessage(
              itemList.size() >= 1 ? itemList.get(itemList.size() - 1).getData().getMessage()
                  : null);
        }
      }

      @Override public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
        if (recyclerView.getLayoutManager() instanceof LinearLayoutManager) {
          firstItem =
              ((LinearLayoutManager) recyclerView.getLayoutManager()).findLastVisibleItemPosition();
        }
      }
    });

    switch (type) {
      case C2C:
        isC2C = true;
        List<String> list = new ArrayList<>();
        list.add(identify);
        TIMFriendshipManager.getInstance()
            .getUsersProfile(list, new TIMValueCallBack<List<TIMUserProfile>>() {
              @Override public void onError(int i, String s) {
                Util.showToast(ChatActivity.this, s);
              }

              @Override public void onSuccess(List<TIMUserProfile> timUserProfiles) {
                for (TIMUserProfile profile : timUserProfiles) {
                  titleStr = profile.getNickName();
                  avatar = profile.getFaceUrl();
                }

                title.setTitleText(titleStr);
              }
            });
        break;
      case Group:
        title.setMoreImg(R.drawable.ic_form_white_group);
        title.setMoreImgAction(new View.OnClickListener() {
          @Override public void onClick(View v) {
            Intent intent = new Intent(ChatActivity.this, GroupMemberActivity.class);
            intent.putExtra("identify", identify);
            startActivityForResult(intent, MEMBER_OPERA);
          }
        });

        if (!TextUtils.isEmpty(titleStr)) {
          title.setTitleText(titleStr);
        } else {
          TIMGroupManager.getInstance()
              .getGroupMembers(identify, new TIMValueCallBack<List<TIMGroupMemberInfo>>() {
                @Override public void onError(int i, String s) {
                }

                @Override public void onSuccess(List<TIMGroupMemberInfo> timGroupMemberInfos) {
                  title.setTitleText(GroupInfo.getInstance().getGroupName(identify)
                      + "("
                      + timGroupMemberInfos.size()
                      + ")");
                }
              });
        }
        break;
    }
    voiceSendingView = (VoiceSendingView) findViewById(R.id.voice_sending);
    presenter.start();

    if (getIntent().getStringExtra(Configs.CHAT_RECRUIT) != null && !TextUtils.isEmpty(
        getIntent().getStringExtra(Configs.CHAT_RECRUIT))) {
      sendRercuitMessage(getIntent().getStringExtra(Configs.CHAT_RECRUIT));
    }

    if (getIntent().getStringExtra(Configs.CHAT_JOB_RESUME) != null && !TextUtils.isEmpty(
        getIntent().getStringExtra(Configs.CHAT_JOB_RESUME))) {
      resumeJson = getIntent().getStringExtra(Configs.CHAT_JOB_RESUME);
    }

    if (getIntent().getBooleanExtra(Configs.SEND_RESUME, false)) {
      sendResumeMessage(resumeJson);
    }
  }

  public int getFragId(){
    return R.id.frag_bottom_position_select;
  }

  private boolean isC2C() {
    return isC2C;
  }

  @Override protected void onPause() {
    super.onPause();
    //退出聊天界面时输入框有内容，保存草稿
    if (input.getText().length() > 0) {
      TextMessage message = new TextMessage(input.getText());
      presenter.saveDraft(message.getMessage());
    } else {
      presenter.saveDraft(null);
    }
    //        RefreshEvent.getInstance().onRefresh();
    presenter.readMessages();
    MediaUtil.getInstance().stop();
  }

  @Override protected void onDestroy() {
    super.onDestroy();
    presenter.stop();
  }

  /**
   * 显示消息
   */
  @Override public void showMessage(final TIMMessage message) {
    if (message == null) {
      flexibleAdapter.notifyDataSetChanged();
    } else {
      final Message mMessage = MessageFactory.getMessage(message);
      if (mMessage != null) {
        if (mMessage instanceof CustomMessage) {
          CustomMessage.Type messageType = ((CustomMessage) mMessage).getType();
          switch (messageType) {
            case TYPING:
              title = (TemplateTitle) findViewById(R.id.chat_title);
              title.setTitleText(getString(R.string.chat_typing));
              handler.removeCallbacks(resetTitle);
              handler.postDelayed(resetTitle, 3000);
              break;
            case RECRUIT:
              if (!isLatestMessage) {
                showTopRercuit(mMessage);
                isLatestMessage = true;
              }
              break;
            case RESUME:
              itemList.add(0, new ChatResumeItem(this, mMessage,
                  isC2C() || mMessage.isSelf() ? avatar
                      : mMessage.getMessage().getSenderProfile().getFaceUrl(), ChatActivity.this));
              flexibleAdapter.notifyDataSetChanged();
              break;
            case SEND_RECRUIT:
              itemList.add(0, new ChatRercuitItem(getBaseContext(),
                  (RecruitModel) (((CustomMessage) mMessage).getData()), mMessage));
              flexibleAdapter.notifyDataSetChanged();
              break;
            case TOP_RESUME:
              if (!isLatestMessage) {
                showTopResume(mMessage);
                isLatestMessage = true;
              }
              break;
            default:
              break;
          }
        } else {
          if (itemList.size() == 0) {
            mMessage.setHasTime(null);
          } else {
            mMessage.setHasTime(itemList.get(itemList.size() - 1).getData().getMessage());
          }
          List<String> list = new ArrayList<>();
          list.add(message.getSender());
          dispatchMessage(mMessage, true);
          flexibleAdapter.notifyDataSetChanged();
        }
      }
    }
  }

  @OnClick(R2.id.chat_send_button)
  public void onSendResume(){
    sendResume();
  }

  public void sendResume(){
    sendResumeMessage(resumeJson);
  }

  private void showTopResume(final Message mMessage) {
    frameDetailLayout.setVisibility(View.VISIBLE);
    layoutRecruit.setVisibility(View.GONE);
    layoutResume.setVisibility(View.VISIBLE);
    layoutResume.setOnClickListener(new View.OnClickListener() {
      @Override public void onClick(View view) {
        outLink(getCurAppSchema(getBaseContext()) + "://resume/?id=" + ((ResumeModel)((CustomMessage)mMessage).getData()).id);
      }
    });
    ResumeModel resumeModel = (ResumeModel) ((CustomMessage) mMessage).getData();

    Glide.with(getBaseContext())
        .load(PhotoUtils.getSmall(resumeModel.avatar))
        .asBitmap()
        .into(imgHeadResume);

    tvResumeName.setText(resumeModel.username);
    imgResumeGender.setImageResource(resumeModel.gender == 1 ? R.drawable.ic_gender_signal_male
        : R.drawable.ic_gender_signal_female);
    tvResumeInfo.setText(resumeModel.work_year
        + "年经验 | "
        + RecruitBusinessUtils.getAge(resumeModel.birthday)
        + " | "
        + resumeModel.height
        + "cm,"
        + resumeModel.weight
        + "kg | "
        + RecruitBusinessUtils.getDegree(getBaseContext(), resumeModel.max_education));
    tvResumeDetail.setText(
        RecruitBusinessUtils.dealData(resumeModel.exp_job) + " · " + RecruitBusinessUtils.dealData(
            resumeModel.city) + " · " + RecruitBusinessUtils.getSalary(
            resumeModel.min_salary, resumeModel.max_salary));
  }

  private void showTopRercuit(final Message mMessage) {
    frameDetailLayout.setVisibility(View.VISIBLE);
    layoutRecruit.setVisibility(View.VISIBLE);
    layoutResume.setVisibility(View.GONE);
    layoutRecruit.setOnClickListener(new View.OnClickListener() {
      @Override public void onClick(View view) {
        outLink(getCurAppSchema(getBaseContext()) + "://job/" + ((RecruitModel)((CustomMessage)mMessage).getData()).id + "/");
      }
    });
    RecruitModel recruitModel = (RecruitModel) (((CustomMessage) mMessage).getData());

    Glide.with(getBaseContext())
        .load(PhotoUtils.getSmall(recruitModel.photo))
        .asBitmap()
        .into(imgGym);
    tvPositionName.setText(recruitModel.name);
    tvSalary.setText(
        RecruitBusinessUtils.getSalary(recruitModel.min_salary, recruitModel.max_salary, "面议"));
    tvGymInfo.setText(recruitModel.address + (TextUtils.isEmpty(recruitModel.gym_name) ? ""
        : "·" + recruitModel.gym_name));
    tvWorkYear.setText(
        RecruitBusinessUtils.getWorkYear(recruitModel.min_work_year, recruitModel.max_work_year,
            "经验"));
    tvGender.setText(RecruitBusinessUtils.getGender(recruitModel.gender, "性别"));
    tvAge.setText(RecruitBusinessUtils.getAge(recruitModel.min_age, recruitModel.max_age, "年龄"));
    tvHeight.setText(
        RecruitBusinessUtils.getHeight(recruitModel.min_height, recruitModel.max_height, "身高"));
  }

  private void dispatchMessage(Message message, boolean isSingle) {

    if (message instanceof TextMessage) {
      if (isSingle) {
        itemList.add(0, new ChatTextItem(this, message, isC2C() || message.isSelf() ? avatar
            : message.getMessage().getSenderProfile().getFaceUrl(), ChatActivity.this));
      } else {
        itemList.add(new ChatTextItem(this, message, isC2C() || message.isSelf() ? avatar
            : message.getMessage().getSenderProfile().getFaceUrl(), ChatActivity.this));
      }
    }
    if (message instanceof ImageMessage) {
      if (isSingle) {
        itemList.add(0, new ChatImageItem(this, message, isC2C() || message.isSelf() ? avatar
            : message.getMessage().getSenderProfile().getFaceUrl(), ChatActivity.this));
      } else {
        itemList.add(new ChatImageItem(this, message, isC2C() || message.isSelf() ? avatar
            : message.getMessage().getSenderProfile().getFaceUrl(), ChatActivity.this));
      }
    }

    if (message instanceof VoiceMessage) {
      if (isSingle) {
        itemList.add(0, new ChatVoiceItem(this, message, isC2C() || message.isSelf() ? avatar
            : message.getMessage().getSenderProfile().getFaceUrl(), ChatActivity.this));
      } else {
        itemList.add(new ChatVoiceItem(this, message, isC2C() || message.isSelf() ? avatar
            : message.getMessage().getSenderProfile().getFaceUrl(), ChatActivity.this));
      }
    }
    if (message instanceof CustomMessage) {
      switch (((CustomMessage) message).getType()) {
        case SEND_RECRUIT:
          itemList.add(
              new ChatRercuitItem(this, (RecruitModel) (((CustomMessage) message).getData()),
                  message));
          break;
        case RESUME:
          itemList.add(new ChatResumeItem(this, message, isC2C() || message.isSelf() ? avatar
              : message.getMessage().getSenderProfile().getFaceUrl(), ChatActivity.this));
          break;
        case TOP_RESUME:
          if (!isLatestMessage) {
            showTopResume(message);
            isLatestMessage = true;
          }
          break;
        case RECRUIT:
          if (!isLatestMessage) {
            showTopRercuit(message);
            isLatestMessage = true;
          }
          break;
      }
    }
  }

  ////对聊天记录进行重新排序
  private void reSortList() {
    if (tempItemList.size() == 0) return;
    List<ChatItem> newMessages = new ArrayList<>();
    for (int i = tempItemList.size() - 1; i >= 0; i--) {
      newMessages.add(tempItemList.get(i));
    }
    tempItemList.clear();
    tempItemList.addAll(newMessages);
  }

  /**
   * 显示消息
   */
  @Override public void showMessage(List<TIMMessage> messages) {
    int newMsgNum = 0;
    //reSortList(messages);
    for (int i = 0; i < messages.size(); ++i) {
      final Message mMessage = MessageFactory.getMessage(messages.get(i));
      if (mMessage == null || messages.get(i).status() == TIMMessageStatus.HasDeleted) continue;
      if (mMessage instanceof CustomMessage && (((CustomMessage) mMessage).getType()
          == CustomMessage.Type.TYPING
          || ((CustomMessage) mMessage).getType() == CustomMessage.Type.INVALID)) {
        continue;
      }
      ++newMsgNum;
      if (i != messages.size() - 1) {
        mMessage.setHasTime(messages.get(i + 1));
        dispatchMessage(mMessage, false);
      } else {
        mMessage.setHasTime(null);
        List<String> list = new ArrayList<>();
        list.add(mMessage.getSender());
        dispatchMessage(mMessage, false);
      }
    }
    flexibleAdapter.notifyDataSetChanged();
    final int finalNewMsgNum = newMsgNum;
    //listView.getLayoutManager()
    //            .scrollToPosition(finalNewMsgNum);

  }

  /**
   * 清除所有消息，等待刷新
   */
  @Override public void clearAllMessage() {
    itemList.clear();
  }

  /**
   * 发送消息成功
   *
   * @param message 返回的消息
   */
  @Override public void onSendMessageSuccess(TIMMessage message) {
    showMessage(message);
  }

  /**
   * 发送消息失败
   *
   * @param code 返回码
   * @param desc 返回描述
   */
  @Override public void onSendMessageFail(int code, String desc, TIMMessage message) {
    if (message == null) {
      return;
    }
    long id = message.getMsgUniqueId();
    for (int i = 0; i < itemList.size(); i++) {
      Message msg = itemList.get(i).getData();
      if (msg.getMessage().getMsgUniqueId() == id) {
        switch (code) {
          case 80001:
            //发送内容包含敏感词
            msg.setDesc(getString(R.string.chat_content_bad));
            flexibleAdapter.notifyDataSetChanged();
            break;
        }
      }
    }
  }

  /**
   * 发送图片消息
   */
  @Override public void sendImage() {
    Intent intent_album = new Intent("android.intent.action.GET_CONTENT");
    intent_album.setType("image/*");
    startActivityForResult(intent_album, IMAGE_STORE);
  }

  /**
   * 发送照片消息
   */
  @Override public void sendPhoto() {
    Intent intent_photo = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
    if (intent_photo.resolveActivity(getPackageManager()) != null) {
      File tempFile = FileUtil.getTempFile(FileUtil.FileType.IMG);
      Uri toCamera = null;
      if (tempFile != null) {
        fileUri = Uri.fromFile(tempFile);

        if (Build.VERSION.SDK_INT >= 24) {
          toCamera = FileProvider.getUriForFile(this,
              this.getApplicationContext().getPackageName() + ".provider", tempFile);
          List<ResolveInfo> resInfoList = getPackageManager().queryIntentActivities(intent_photo,
              PackageManager.MATCH_DEFAULT_ONLY);
          for (ResolveInfo resolveInfo : resInfoList) {
            String packageName = resolveInfo.activityInfo.packageName;
            grantUriPermission(packageName, toCamera,
                Intent.FLAG_GRANT_WRITE_URI_PERMISSION | Intent.FLAG_GRANT_READ_URI_PERMISSION);
          }
        } else {
          toCamera = Uri.fromFile(tempFile);
        }
      }
      intent_photo.putExtra(MediaStore.EXTRA_OUTPUT, toCamera);
      startActivityForResult(intent_photo, CAPTURE_IMAGE_ACTIVITY_REQUEST_CODE);
    }
  }

  /**
   * 发送文本消息
   */
  @Override public void sendText() {
    Message message = new TextMessage(input.getText());
    presenter.sendMessage(message.getMessage());
    input.setText("");
  }

  /**
   * 发送文件
   */
  @Override public void sendFile() {
    Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
    intent.setType("*/*");
    startActivityForResult(intent, FILE_CODE);
  }

  /**
   * 开始发送语音消息
   */
  @Override public void startSendVoice() {
    voiceSendingView.setVisibility(View.VISIBLE);
    voiceSendingView.showRecording();
    recorder.startRecording();
  }

  /**
   * 结束发送语音消息
   */
  @Override public void endSendVoice(boolean isCancel) {
    voiceSendingView.release();
    voiceSendingView.setVisibility(View.GONE);
    recorder.stopRecording();
    if (recorder.getTimeInterval() < 1) {
      Toast.makeText(this, getResources().getString(R.string.chat_audio_too_short),
          Toast.LENGTH_SHORT).show();
    } else if (isCancel) {
      Toast.makeText(this, "已取消", Toast.LENGTH_SHORT).show();
    } else {
      Message message = new VoiceMessage(recorder.getTimeInterval(), recorder.getFilePath());
      presenter.sendMessage(message.getMessage());
    }
  }

  /**
   * 发送小视频消息
   *
   * @param fileName 文件名
   */
  @Override public void sendVideo(String fileName) {
    Message message = new VideoMessage(fileName);
    presenter.sendMessage(message.getMessage());
  }

  /**
   * 结束发送语音消息
   */
  @Override public void cancelSendVoice() {

  }

  /**
   * 正在发送
   */
  @Override public void sending() {
    if (type == TIMConversationType.C2C) {
      Message message = new CustomMessage(CustomMessage.Type.TYPING);
      presenter.sendOnlineMessage(message.getMessage());
    }
  }

  /**
   * 显示草稿
   */
  @Override public void showDraft(TIMMessageDraft draft) {
    input.getText().append(TextMessage.getString(draft.getElems(), this));
  }

  public void sendRercuitMessage(String recruitJson) {
    Message message = new CustomMessage(CustomMessage.Type.RECRUIT, recruitJson);
    presenter.sendMessage(message.getMessage());
  }

  public void sendResumeMessage(String resumeJson) {
    Message message = new CustomMessage(CustomMessage.Type.RESUME, resumeJson);
    presenter.sendMessage(message.getMessage());
  }

  //    @Override
  //    public void onCreateContextMenu(ContextMenu menu, View v,
  //                                    ContextMenu.ContextMenuInfo menuInfo) {
  //        AdapterView.AdapterContextMenuInfo info = (AdapterView.AdapterContextMenuInfo) menuInfo;
  //        Message message = itemList.get(info.position).getData();
  //        menu.add(0, 1, Menu.NONE, getString(R.string.chat_del));
  //        if (message.isSendFail()) {
  //            menu.add(0, 2, Menu.NONE, getString(R.string.chat_resend));
  //        }
  //        if (message instanceof ImageMessage || message instanceof FileMessage) {
  //            menu.add(0, 3, Menu.NONE, getString(R.string.chat_save));
  //        }
  //    }
  //
  //
  //    @Override
  //    public boolean onContextItemSelected(MenuItem item) {
  //        AdapterView.AdapterContextMenuInfo info = (AdapterView.AdapterContextMenuInfo) item.getMenuInfo();
  //        Message message = itemList.get(info.position).getData();
  //        switch (item.getItemId()) {
  //            case 1:
  //                message.remove();
  //                flexibleAdapter.removeItem(info.position);
  //                flexibleAdapter.notifyDataSetChanged();
  //                break;
  //            case 2:
  ////                messageList.remove(message);
  ////                presenter.sendMessage(message.getMessage());
  //                break;
  //            case 3:
  //                message.save();
  //                break;
  //            default:
  //                break;
  //        }
  //        return super.onContextItemSelected(item);
  //    }

  @Override protected void onActivityResult(int requestCode, int resultCode, Intent data) {
    if (requestCode == CAPTURE_IMAGE_ACTIVITY_REQUEST_CODE) {
      if (resultCode == RESULT_OK && fileUri != null) {

        showImagePreview(FileUtil.getFilePath(this, fileUri));
      }
    } else if (requestCode == IMAGE_STORE) {
      if (resultCode == RESULT_OK && data != null) {
        showImagePreview(FileUtil.getFilePath(this, data.getData()));
      }
    } else if (requestCode == FILE_CODE) {
      if (resultCode == RESULT_OK) {
        sendFile(FileUtil.getFilePath(this, data.getData()));
      }
    } else if (requestCode == IMAGE_PREVIEW) {
      if (resultCode == RESULT_OK) {
        boolean isOri = data.getBooleanExtra("isOri", false);
        String path = data.getStringExtra("path");
        File file = new File(path);
        if (file.exists() && file.length() > 0) {
          if (file.length() > 1024 * 1024 * 10) {
            Toast.makeText(this, getString(R.string.chat_file_too_large), Toast.LENGTH_SHORT)
                .show();
          } else {
            Message message = new ImageMessage(path, isOri);
            presenter.sendMessage(message.getMessage());
          }
        } else {
          Toast.makeText(this, getString(R.string.chat_file_not_exist), Toast.LENGTH_SHORT).show();
        }
      }
    } else if (requestCode == MEMBER_OPERA) {
      if (resultCode == RESULT_OK) {
        finish();
      }
    }
  }

  public static String getRealFilePath(final Context context, final Uri uri) {
    if (null == uri) return null;
    final String scheme = uri.getScheme();
    String data = null;
    if (scheme == null) {
      data = uri.getPath();
    } else if (ContentResolver.SCHEME_FILE.equals(scheme)) {
      data = uri.getPath();
    } else if (ContentResolver.SCHEME_CONTENT.equals(scheme)) {
      Cursor cursor = context.getContentResolver()
          .query(uri, new String[] { MediaStore.Images.ImageColumns.DATA }, null, null, null);
      if (null != cursor) {
        if (cursor.moveToFirst()) {
          int index = cursor.getColumnIndex(MediaStore.Images.ImageColumns.DATA);
          if (index > -1) {
            data = cursor.getString(index);
          }
        }
        cursor.close();
      }
    }
    return data;
  }

  private void showImagePreview(String path) {
    if (path == null) return;
    Intent intent = new Intent(this, ImagePreviewActivity.class);
    intent.putExtra("path", path);
    startActivityForResult(intent, IMAGE_PREVIEW);
  }

  private void sendFile(String path) {
    if (path == null) return;
    File file = new File(path);
    if (file.exists()) {
      if (file.length() > 1024 * 1024 * 10) {
        Toast.makeText(this, getString(R.string.chat_file_too_large), Toast.LENGTH_SHORT).show();
      } else {
        Message message = new FileMessage(path);
        presenter.sendMessage(message.getMessage());
      }
    } else {
      Toast.makeText(this, getString(R.string.chat_file_not_exist), Toast.LENGTH_SHORT).show();
    }
  }

  /**
   * 将标题设置为对象名称
   */
  private Runnable resetTitle = new Runnable() {
    @Override public void run() {
      title.setTitleText(titleStr);
    }
  };

  @Override public void onDelete(final int position) {
    AlertDialog.Builder builder = new AlertDialog.Builder(this);
    builder.setMessage("删除会话？")
        .setNegativeButton("取消", null)
        .setPositiveButton("确定", new DialogInterface.OnClickListener() {
          @Override public void onClick(DialogInterface dialogInterface, int i) {
            itemList.get(position).getData().getMessage().remove();
            flexibleAdapter.removeItem(position);
            flexibleAdapter.notifyDataSetChanged();
          }
        });

    AlertDialog dialog = builder.create();
    dialog.show();
    dialog.getButton(DialogInterface.BUTTON_POSITIVE)
        .setTextColor(ContextCompat.getColor(this, R.color.qc_green));
    dialog.getButton(DialogInterface.BUTTON_NEGATIVE)
        .setTextColor(ContextCompat.getColor(this, R.color.qc_text_grey));
  }

  @Override public boolean onItemClick(int position) {
    if (flexibleAdapter.getItem(position) instanceof ChatTextItem) {
      return false;
    }
    if (flexibleAdapter.getItem(position) instanceof ChatImageItem) {
      return false;
    }
    if (flexibleAdapter.getItem(position) instanceof ChatVoiceItem) {
      return false;
    }
    if (flexibleAdapter.getItem(position) instanceof ChatRercuitItem) {
      outLink(getCurAppSchema(this) + "://job/" + ((ChatRercuitItem) flexibleAdapter.getItem(
          position)).getJobId() + "/");
      return false;
    }
    if (flexibleAdapter.getItem(position) instanceof ChatResumeItem) {
      outLink(getCurAppSchema(this) + "://resume/?id=" + ((ChatResumeItem) flexibleAdapter.getItem(
          position)).getResumeId());
      return false;
    }
    return false;
  }

  public static String getCurAppSchema(Context context) {
    String packagename = context.getPackageName();
    if (packagename.contains("coach")) {
      return "qccoach";
    } else {
      return "qcstaff";
    }
  }

  public void outLink(String uri) {
    try {
      Intent toOut = new Intent();
      toOut.setPackage(getPackageName());
      toOut.setData(Uri.parse(uri));
      startActivity(toOut);
    } catch (Exception e) {

    }
  }
}
