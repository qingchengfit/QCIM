package com.tencent.qcloud.timchat.ui;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.tencent.TIMCallBack;
import com.tencent.TIMFriendshipManager;
import com.tencent.TIMGroupDetailInfo;
import com.tencent.TIMGroupManager;
import com.tencent.TIMGroupMemberInfo;
import com.tencent.TIMGroupMemberResult;
import com.tencent.TIMUserProfile;
import com.tencent.TIMValueCallBack;
import com.tencent.qcloud.timchat.R;
import com.tencent.qcloud.timchat.adapters.ProfileSummaryItem;
import com.tencent.qcloud.timchat.chatmodel.GroupInfo;
import com.tencent.qcloud.timchat.chatmodel.GroupMemberProfile;
import com.tencent.qcloud.timchat.chatmodel.ProfileSummary;
import com.tencent.qcloud.timchat.chatutils.FileUtil;
import com.tencent.qcloud.timchat.common.AppData;
import com.tencent.qcloud.timchat.common.Util;
import com.tencent.qcloud.timchat.presenter.GroupManagerPresenter;
import com.tencent.qcloud.timchat.ui.qcchat.DeleteMemberActivity;
import com.tencent.qcloud.timchat.ui.qcchat.UpYunClient;
import com.tencent.qcloud.timchat.widget.PhotoUtils;
import com.tencent.qcloud.timchat.widget.TemplateTitle;

import java.io.File;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import eu.davidea.flexibleadapter.FlexibleAdapter;

public class GroupMemberActivity extends Activity implements TIMValueCallBack<List<TIMGroupMemberInfo>>, FlexibleAdapter.OnItemClickListener {

    public static final int IMAGE_STORE = 101;
    public static final int IMAGE_PREVIEW = 102;
    private static final int ADD_MEMBER = 103;
    private static final int DELETE_MEMBER = 104;

    List<GroupMemberProfile> list = new ArrayList<>();
    RecyclerView listView;
    TemplateTitle title;
    String groupId,type;
    private TextView tvTitle;
    private TextView btnExit;
    private RelativeLayout rlGroupName;
    private ImageView imgGroupHead;
    private final int MEM_REQ = 100;
    private final int CHOOSE_MEM_CODE = 200;
    private int memIndex;
    private List<String> users = new ArrayList<>();
    private List<TIMGroupMemberInfo> infos = new ArrayList<>();
    private FlexibleAdapter flexibleAdapter;
    private List<ProfileSummaryItem> itemList = new ArrayList<>();
    private boolean isChangeName;

    @SuppressLint("WrongViewCast")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_group_detail);
        title = (TemplateTitle) findViewById(R.id.group_mem_title);
        groupId = getIntent().getStringExtra("identify");
        type = getIntent().getStringExtra("type");

        rlGroupName = (RelativeLayout) findViewById(R.id.set_group_name);
        tvTitle = (TextView) findViewById(R.id.tv_member_count);
        btnExit = (TextView) findViewById(R.id.btn_exit_group);
        imgGroupHead = (ImageView) findViewById(R.id.image_group_head);
        imgGroupHead.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent_album = new Intent("android.intent.action.GET_CONTENT");
                intent_album.setType("image/*");
                startActivityForResult(intent_album, IMAGE_STORE);
            }
        });
        btnExit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                AlertDialog.Builder builder = new AlertDialog.Builder(GroupMemberActivity.this);
                builder.setMessage("确定退出群聊？")
                        .setTitle("提示")
                        .setCancelable(true)
                        .setPositiveButton("确定", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                TIMGroupManager.getInstance().quitGroup(groupId, new TIMCallBack() {
                                    @Override
                                    public void onError(int i, String s) {
                                        Util.showToast(getApplicationContext(), s);
                                    }

                                    @Override
                                    public void onSuccess() {
                                        setResult(RESULT_OK);
                                        finish();
                                    }
                                });
                            }
                        })
                        .setNegativeButton("取消", null);

                AlertDialog dialog = builder.create();
                dialog.show();
                dialog.getButton(DialogInterface.BUTTON_POSITIVE).setTextColor(ContextCompat.getColor(getApplicationContext(), R.color.qc_text_grey));
                dialog.getButton(DialogInterface.BUTTON_NEGATIVE).setTextColor(ContextCompat.getColor(getApplicationContext(), R.color.qc_text_grey));

            }
        });

        listView = (RecyclerView) findViewById(R.id.gridView_group_member);
        listView.setLayoutManager(new GridLayoutManager(getApplicationContext(), 5));
        listView.setItemAnimator(new DefaultItemAnimator());
        flexibleAdapter = new FlexibleAdapter(itemList, this);
        listView.setAdapter(flexibleAdapter);

        TIMGroupManager.getInstance().getGroupMembers(groupId, this);
        List<String> list = new ArrayList<>();
        list.add(groupId);
        TIMGroupManager.getInstance().getGroupDetailInfo(list, new TIMValueCallBack<List<TIMGroupDetailInfo>>() {
            @Override
            public void onError(int i, String s) {

            }

            @Override
            public void onSuccess(List<TIMGroupDetailInfo> timGroupDetailInfos) {
                for (TIMGroupDetailInfo groupDetailInfo : timGroupDetailInfos){
                    Glide.with(getApplicationContext())
                            .load(PhotoUtils.getMiddle(groupDetailInfo.getFaceUrl()))
                            .asBitmap()
                            .into(imgGroupHead);
                }
            }
        });

        rlGroupName.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(GroupMemberActivity.this, SetGroupNameActivity.class);
                intent.putExtra("id", groupId);
                startActivity(intent);
            }
        });

    }

    @Override
    public void onError(int i, String s) {

    }

    @Override
    public void onSuccess(List<TIMGroupMemberInfo> timGroupMemberInfos) {
        list.clear();
        itemList.clear();
        if (timGroupMemberInfos == null) return;
        for (TIMGroupMemberInfo item : timGroupMemberInfos){
            users.add(item.getUser());
            infos.add(item);
        }
        TIMFriendshipManager.getInstance().getUsersProfile(users, new TIMValueCallBack<List<TIMUserProfile>>() {
            @Override
            public void onError(int i, String s) {
            }

            @Override
            public void onSuccess(List<TIMUserProfile> timUserProfiles) {

                list.clear();
                itemList.clear();
                int index = 0;
                for (TIMUserProfile profile : timUserProfiles) {
                    list.add(new GroupMemberProfile(profile));
                    itemList.add(new ProfileSummaryItem(getApplicationContext(), new GroupMemberProfile(profile)));
                    index++;
                }
                resetView();
            }
        });

    }


    //加载最后的添加与删除成员
    private void resetView(){
        itemList.add(new ProfileSummaryItem(getApplicationContext(), new GroupMemberProfile(GroupMemberProfile.ADD)));
        itemList.add(new ProfileSummaryItem(getApplicationContext(), new GroupMemberProfile(GroupMemberProfile.REMOVE)));
        flexibleAdapter.notifyDataSetChanged();
        tvTitle.setText(getApplicationContext().getString(R.string.title_group_member_count, list.size()));
        if (isChangeName) {
            resetGroupName(list.size());
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == IMAGE_STORE){                    //打开系统相册
            if (resultCode == RESULT_OK && data != null) {
                showImagePreview(FileUtil.getFilePath(this, data.getData()), true);
            }
        }else if(requestCode == IMAGE_PREVIEW){            //选择头像
            if (resultCode == RESULT_OK && data != null) {
                String path = data.getStringExtra("path");
                UpYunClient.startLoadImg("/chat/", new File(path), new UpYunClient.OnLoadImgListener() {
                    @Override
                    public void onLoadSuccessed(final String avatarUrl) {
                        TIMGroupManager.getInstance().modifyGroupFaceUrl(groupId, avatarUrl, new TIMCallBack() {
                            @Override
                            public void onError(int i, String s) {
                                Toast.makeText(getApplicationContext(), s, Toast.LENGTH_SHORT).show();
                            }

                            @Override
                            public void onSuccess() {
                                Toast.makeText(getApplicationContext(), "修改群头像成功", Toast.LENGTH_SHORT).show();
                                Glide.with(getApplicationContext())
                                        .load(PhotoUtils.getMiddle(avatarUrl))
                                        .asBitmap()
                                        .into(imgGroupHead);
                            }
                        });

                    }
                });

            }
        } else if (requestCode == ADD_MEMBER){              //添加成员
            if (resultCode == RESULT_OK){
                List<String> idList = data.getStringArrayListExtra("ids");
                TIMGroupManager.getInstance().inviteGroupMember(groupId, idList, new TIMValueCallBack<List<TIMGroupMemberResult>>() {
                    @Override
                    public void onError(int i, String  s) {
                        Toast.makeText(getApplicationContext(), "添加失败", Toast.LENGTH_SHORT).show();
                    }

                    @Override
                    public void onSuccess(List<TIMGroupMemberResult> timGroupMemberResults) {
                        isChangeName = true;
                        Toast.makeText(getApplicationContext(), "添加成功", Toast.LENGTH_SHORT).show();
                        TIMGroupManager.getInstance().getGroupMembers(groupId, GroupMemberActivity.this);
                    }
                });
            }

        }else if (requestCode == DELETE_MEMBER){            //删除群成员
            isChangeName = true;
            list.clear();
            if (resultCode == RESULT_OK && data.getBundleExtra("data") != null) {
                list = data.getBundleExtra("data").getParcelableArrayList("delete");
                itemList.clear();
                for (GroupMemberProfile profile : list){
                    itemList.add(new ProfileSummaryItem(getApplicationContext(), profile));
                }
                resetView();
            }
        }
    }

    private void resetGroupName(int size){
        String name = GroupInfo.getInstance().getGroupName(groupId);
        String p = "\\(\\d\\)$";
        name = name.replaceAll(p, "(" + size + ")");

        TIMGroupManager.getInstance().modifyGroupName(groupId, name, new TIMCallBack() {
            @Override
            public void onError(int i, String s) {
            }

            @Override
            public void onSuccess() {
            }
        });
    }

    private void showImagePreview(String filePath, boolean isSetHead){
        if (filePath == null) return;
        Intent intent = new Intent(this, ImagePreviewActivity.class);
        intent.putExtra("path", filePath);
        intent.putExtra("head", isSetHead);
        startActivityForResult(intent, IMAGE_PREVIEW);
    }


    @Override
    public boolean onItemClick(int position) {
        GroupMemberProfile groupMemberProfile = itemList.get(position).getData();
        if (groupMemberProfile.getType() == GroupMemberProfile.REMOVE){
            Intent intent = new Intent(GroupMemberActivity.this, DeleteMemberActivity.class);
            Bundle b = new Bundle();
            b.putSerializable("member", (Serializable) list);
            intent.putExtra("datas", b);
            intent.putExtra("group", groupId);
            startActivityForResult(intent, DELETE_MEMBER);
        }else{
            try {
                Intent intent = new Intent();
                intent.setAction(getPackageName());
                if (getPackageName().contains("staff")) {
                    intent.setData(Uri.parse("qcstaff://choose/chat_friend"));
                } else if (getPackageName().contains("coach")) {
                    intent.setData(Uri.parse("qccoach://choose/chat_friend"));
                }
                startActivityForResult(intent, ADD_MEMBER);
            }catch (Exception e){
                e.printStackTrace();
            }
        }
        return false;
    }
}
