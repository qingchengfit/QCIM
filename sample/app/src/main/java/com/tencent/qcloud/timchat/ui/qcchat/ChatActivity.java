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
import android.view.ViewTreeObserver;
import android.view.WindowManager;
import android.widget.AbsListView;
import android.widget.RelativeLayout;
import android.widget.Toast;
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
import com.tencent.qcloud.timchat.adapters.ChatItem;
import com.tencent.qcloud.timchat.chatmodel.CustomMessage;
import com.tencent.qcloud.timchat.chatmodel.FileMessage;
import com.tencent.qcloud.timchat.chatmodel.GroupInfo;
import com.tencent.qcloud.timchat.chatmodel.ImageMessage;
import com.tencent.qcloud.timchat.chatmodel.Message;
import com.tencent.qcloud.timchat.chatmodel.MessageFactory;
import com.tencent.qcloud.timchat.chatmodel.TextMessage;
import com.tencent.qcloud.timchat.chatmodel.VideoMessage;
import com.tencent.qcloud.timchat.chatmodel.VoiceMessage;
import com.tencent.qcloud.timchat.chatutils.FileUtil;
import com.tencent.qcloud.timchat.chatutils.MediaUtil;
import com.tencent.qcloud.timchat.chatutils.RecorderUtil;
import com.tencent.qcloud.timchat.common.Configs;
import com.tencent.qcloud.timchat.common.Util;
import com.tencent.qcloud.timchat.presenter.ChatPresenter;
import com.tencent.qcloud.timchat.ui.GroupMemberActivity;
import com.tencent.qcloud.timchat.ui.ImagePreviewActivity;
import com.tencent.qcloud.timchat.viewfeatures.ChatView;
import com.tencent.qcloud.timchat.widget.ChatInput;
import com.tencent.qcloud.timchat.widget.ScrollLinearLayoutManager;
import com.tencent.qcloud.timchat.widget.TemplateTitle;
import com.tencent.qcloud.timchat.widget.VoiceSendingView;
import eu.davidea.flexibleadapter.FlexibleAdapter;
import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class ChatActivity extends AppCompatActivity implements ChatView, ChatItem.OnDeleteMessageItem {

    static {
        AppCompatDelegate.setCompatVectorFromResourcesEnabled(true);
    }

    private static final String TAG = "ChatActivity";

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

    public static void navToChat(Context context, String identify, TIMConversationType type) {
        Intent intent = new Intent(context, ChatActivity.class);
        intent.putExtra(Configs.IDENTIFY, identify);
        intent.putExtra(Configs.CONVERSATION_TYPE, type);
        context.startActivity(intent);
    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        setIntent(intent);
    }

    @Override
    public void onSaveInstanceState(Bundle outState, PersistableBundle outPersistentState) {
    }

    @Override
    protected void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);
        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN);
        title = (TemplateTitle) findViewById(R.id.chat_title);
        root = (RelativeLayout) findViewById(R.id.root);

        identify = getIntent().getStringExtra(Configs.IDENTIFY);
        if(getIntent().getStringExtra("groupName") != null) {
            titleStr = getIntent().getStringExtra("groupName");
            title.setTitleText(titleStr);
        }
        type = (TIMConversationType) getIntent().getSerializableExtra(Configs.CONVERSATION_TYPE);
        presenter = new ChatPresenter(this, identify, type);
        input = (ChatInput) findViewById(R.id.input_panel);
        input.setChatView(this);
        flexibleAdapter = new FlexibleAdapter(itemList);
        flexibleAdapter.addListener(this);
        listView = (RecyclerView) findViewById(R.id.list);
        listView.setLayoutManager(new ScrollLinearLayoutManager(getApplicationContext()));
        listView.setNestedScrollingEnabled(false);
        listView.setAdapter(flexibleAdapter);

        listView.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                switch (event.getAction()) {
                    case MotionEvent.ACTION_DOWN:
                        input.setInputMode(ChatInput.InputMode.NONE);
                        break;
                }
                return false;
            }
        });

        listView.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
            @Override
            public void onGlobalLayout() {
                listView.getViewTreeObserver().removeOnGlobalLayoutListener(this);
                new Handler(getMainLooper()).postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        RecyclerView.State state = new RecyclerView.State();
                        ((ScrollLinearLayoutManager)listView.getLayoutManager()).setSpeedSlow();
                        if (flexibleAdapter.getItemCount() > 1) {
                            listView.getLayoutManager().smoothScrollToPosition(listView, state, flexibleAdapter.getItemCount() - 1);
                        }
                    }
                }, 1000);
            }
        });

        listView.setOnScrollListener(new RecyclerView.OnScrollListener() {

            private int firstItem;

            @Override
            public void onScrollStateChanged(RecyclerView view, int scrollState) {
                if (scrollState == AbsListView.OnScrollListener.SCROLL_STATE_IDLE && firstItem == 0) {
                    //如果拉到顶端读取更多消息
                    presenter.getMessage(itemList.size() > 0 ? itemList.get(0).getData().getMessage() : null);
                }
            }

            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                if (recyclerView.getLayoutManager() instanceof LinearLayoutManager) {
                    firstItem = ((LinearLayoutManager)recyclerView.getLayoutManager()).findFirstVisibleItemPosition();
                }
            }
        });

        switch (type) {
            case C2C:
                isC2C = true;
                List<String> list = new ArrayList<>();
                list.add(identify);
                TIMFriendshipManager.getInstance().getUsersProfile(list, new TIMValueCallBack<List<TIMUserProfile>>() {
                    @Override
                    public void onError(int i, String s) {
                        Util.showToast(getApplicationContext(), s);
                    }

                    @Override
                    public void onSuccess(List<TIMUserProfile> timUserProfiles) {
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
                    @Override
                    public void onClick(View v) {
                        Intent intent = new Intent(ChatActivity.this, GroupMemberActivity.class);
                        intent.putExtra("identify", identify);
                        startActivityForResult(intent, MEMBER_OPERA);
                    }
                });

                if (!TextUtils.isEmpty(titleStr)) {
                    title.setTitleText(titleStr);
                } else {
                    TIMGroupManager.getInstance().getGroupMembers(identify, new TIMValueCallBack<List<TIMGroupMemberInfo>>() {
                        @Override
                        public void onError(int i, String s) {
                        }
                        @Override
                        public void onSuccess(List<TIMGroupMemberInfo> timGroupMemberInfos) {
                            title.setTitleText(GroupInfo.getInstance().getGroupName(identify) + "(" + timGroupMemberInfos.size() + ")");
                        }
                    });
                }
                break;

        }
        voiceSendingView = (VoiceSendingView) findViewById(R.id.voice_sending);
        presenter.start();
    }

    private boolean isC2C(){
        return isC2C;
    }


    @Override
    protected void onPause() {
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

    @Override
    protected void onDestroy() {
        super.onDestroy();
        presenter.stop();
    }

    /**
     * 显示消息
     *
     * @param message
     */
    @Override
    public void showMessage(final TIMMessage message) {
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
                    new Handler(getMainLooper()).postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            itemList.add(new ChatItem(getApplicationContext(), mMessage,
                                    isC2C() || mMessage.isSelf() ? avatar : message.getSenderProfile().getFaceUrl(), ChatActivity.this));

                            flexibleAdapter.notifyDataSetChanged();
                        }
                    }, 500);
                    new Handler(getMainLooper()).postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            listView.getLayoutManager().scrollToPosition(flexibleAdapter.getItemCount() - 1);
                        }
                    }, 500);
                }

            }
        }

    }

    /**
     * 显示消息
     *
     * @param messages
     */
    @Override
    public void showMessage(List<TIMMessage> messages) {
        int newMsgNum = 0;
        for (int i = 0; i < messages.size(); ++i) {
            final Message mMessage = MessageFactory.getMessage(messages.get(i));
            if (mMessage == null || messages.get(i).status() == TIMMessageStatus.HasDeleted)
                continue;
            if (mMessage instanceof CustomMessage && (((CustomMessage) mMessage).getType() == CustomMessage.Type.TYPING ||
                    ((CustomMessage) mMessage).getType() == CustomMessage.Type.INVALID)) continue;
            ++newMsgNum;
            if (i != messages.size() - 1) {
                mMessage.setHasTime(messages.get(i + 1));
                new Handler(getMainLooper()).postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        itemList.add(0, new ChatItem(getApplicationContext(), mMessage,
                                isC2C() || mMessage.isSelf() ? avatar : mMessage.getMessage().getSenderProfile().getFaceUrl(), ChatActivity.this));
                    }
                }, 500);
            } else {
                mMessage.setHasTime(null);
                List<String> list = new ArrayList<>();
                list.add(mMessage.getSender());
                new Handler(getMainLooper()).postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        itemList.add(0, new ChatItem(getApplicationContext(), mMessage,
                                isC2C() || mMessage.isSelf() ? avatar : mMessage.getMessage().getSenderProfile().getFaceUrl(), ChatActivity.this));
                    }
                }, 500);
            }
        }
        flexibleAdapter.notifyDataSetChanged();
        final int finalNewMsgNum = newMsgNum;
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                listView.getLayoutManager().scrollToPosition(finalNewMsgNum);
            }
        }, 500);

    }

    /**
     * 清除所有消息，等待刷新
     */
    @Override
    public void clearAllMessage() {
        itemList.clear();
    }

    /**
     * 发送消息成功
     *
     * @param message 返回的消息
     */
    @Override
    public void onSendMessageSuccess(TIMMessage message) {
        showMessage(message);
    }

    /**
     * 发送消息失败
     *
     * @param code 返回码
     * @param desc 返回描述
     */
    @Override
    public void onSendMessageFail(int code, String desc, TIMMessage message) {
        long id = message.getMsgUniqueId();
        for (int i = 0; i< itemList.size(); i++) {
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
    @Override
    public void sendImage() {
        Intent intent_album = new Intent("android.intent.action.GET_CONTENT");
        intent_album.setType("image/*");
        startActivityForResult(intent_album, IMAGE_STORE);
    }

    /**
     * 发送照片消息
     */
    @Override
    public void sendPhoto() {
        Intent intent_photo = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        if (intent_photo.resolveActivity(getPackageManager()) != null) {
            File tempFile = FileUtil.getTempFile(FileUtil.FileType.IMG);
            Uri toCamera = null;
            if (tempFile != null) {
                fileUri = Uri.fromFile(tempFile);

                if (Build.VERSION.SDK_INT >= 24){
                    toCamera = FileProvider.getUriForFile(this, this.getApplicationContext().getPackageName() + ".provider",tempFile);
                    List<ResolveInfo> resInfoList = getPackageManager().queryIntentActivities(intent_photo, PackageManager.MATCH_DEFAULT_ONLY);
                    for (ResolveInfo resolveInfo : resInfoList) {
                        String packageName = resolveInfo.activityInfo.packageName;
                        grantUriPermission(packageName, toCamera, Intent.FLAG_GRANT_WRITE_URI_PERMISSION | Intent.FLAG_GRANT_READ_URI_PERMISSION);
                    }
                }else
                    toCamera = Uri.fromFile(tempFile);
            }
            intent_photo.putExtra(MediaStore.EXTRA_OUTPUT, toCamera);
            startActivityForResult(intent_photo, CAPTURE_IMAGE_ACTIVITY_REQUEST_CODE);
        }
    }

    /**
     * 发送文本消息
     */
    @Override
    public void sendText() {
        Message message = new TextMessage(input.getText());
        presenter.sendMessage(message.getMessage());
        input.setText("");
    }

    /**
     * 发送文件
     */
    @Override
    public void sendFile() {
        Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
        intent.setType("*/*");
        startActivityForResult(intent, FILE_CODE);
    }


    /**
     * 开始发送语音消息
     */
    @Override
    public void startSendVoice() {
        voiceSendingView.setVisibility(View.VISIBLE);
        voiceSendingView.showRecording();
        recorder.startRecording();

    }

    /**
     * 结束发送语音消息
     */
    @Override
    public void endSendVoice(boolean isCancel) {
        voiceSendingView.release();
        voiceSendingView.setVisibility(View.GONE);
        recorder.stopRecording();
        if (recorder.getTimeInterval() < 1) {
            Toast.makeText(this, getResources().getString(R.string.chat_audio_too_short), Toast.LENGTH_SHORT).show();
        } else if (isCancel) {
            Toast.makeText(this, "已取消", Toast.LENGTH_SHORT).show();
        }else{
            Message message = new VoiceMessage(recorder.getTimeInterval(), recorder.getFilePath());
            presenter.sendMessage(message.getMessage());
        }
    }

    /**
     * 发送小视频消息
     *
     * @param fileName 文件名
     */
    @Override
    public void sendVideo(String fileName) {
        Message message = new VideoMessage(fileName);
        presenter.sendMessage(message.getMessage());
    }


    /**
     * 结束发送语音消息
     */
    @Override
    public void cancelSendVoice() {

    }

    /**
     * 正在发送
     */
    @Override
    public void sending() {
        if (type == TIMConversationType.C2C) {
            Message message = new CustomMessage(CustomMessage.Type.TYPING);
            presenter.sendOnlineMessage(message.getMessage());
        }
    }

    /**
     * 显示草稿
     */
    @Override
    public void showDraft(TIMMessageDraft draft) {
        input.getText().append(TextMessage.getString(draft.getElems(), this));
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


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
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
                        Toast.makeText(this, getString(R.string.chat_file_too_large), Toast.LENGTH_SHORT).show();
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
    public static String getRealFilePath( final Context context, final Uri uri ) {
        if ( null == uri ) return null;
        final String scheme = uri.getScheme();
        String data = null;
        if ( scheme == null )
            data = uri.getPath();
        else if ( ContentResolver.SCHEME_FILE.equals( scheme ) ) {
            data = uri.getPath();
        } else if ( ContentResolver.SCHEME_CONTENT.equals( scheme ) ) {
            Cursor cursor = context.getContentResolver().query( uri, new String[] { MediaStore.Images.ImageColumns.DATA }, null, null, null );
            if ( null != cursor ) {
                if ( cursor.moveToFirst() ) {
                    int index = cursor.getColumnIndex( MediaStore.Images.ImageColumns.DATA );
                    if ( index > -1 ) {
                        data = cursor.getString( index );
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
        @Override
        public void run() {
            title.setTitleText(titleStr);
        }
    };

    @Override
    public void onDelete(final int position) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage("删除会话？")
                .setNegativeButton("取消", null)
                .setPositiveButton("确定", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        itemList.get(position).getData().getMessage().remove();
                        flexibleAdapter.removeItem(position);
                        flexibleAdapter.notifyDataSetChanged();
                    }
                });

        AlertDialog dialog = builder.create();
        dialog.show();
        dialog.getButton(DialogInterface.BUTTON_POSITIVE).setTextColor(ContextCompat.getColor(getApplicationContext(), R.color.qc_green));
        dialog.getButton(DialogInterface.BUTTON_NEGATIVE).setTextColor(ContextCompat.getColor(getApplicationContext(), R.color.qc_text_grey));
    }

}
